package org.stoevesand.findow.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.annotations.GenericGenerator;
import org.stoevesand.findow.hint.Hint;
import org.stoevesand.findow.hint.HintEngine;
import org.stoevesand.findow.persistence.PersistanceManager;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "Transaction")
@Table(name = "TRANSACTIONS")
public class FinTransaction {

	JSONObject jo = null;

	// internal id used for persistance
	private Long id;

	// id coming from a source system
	private String sourceId;
	private String sourceSystem = "FIGO";

	private transient int parentId;
	private String accountId;
	private long amount;
	private Date valueDate;
	private Date bookingDate;
	private String purpose;
	private String counterpartName;

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public void setCounterpartName(String counterpartName) {
		this.counterpartName = counterpartName;
	}

	private FinCategory category;

	private String type;

	private List<Hint> hints;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "transaction", fetch = FetchType.EAGER, orphanRemoval = true)
	public List<Hint> getHints() {
		return hints;
	}

	public void setHints(List<Hint> hints) {
		this.hints = hints;
	}

	public FinTransaction() {
		purpose = "-";
		counterpartName = "-";
	}

	@Column(name = "AMOUNT_CENT")
	public long getAmountCent() {
		return amount;
	}

	@Transient
	public double getAmount() {
		return (double) amount / 100;
	}

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "TX_ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Transient
	public int getParentId() {
		return parentId;
	}

	@Column(name = "ACCOUNT_ID")
	@JsonIgnore
	public String getAccountId() {
		return accountId;
	}

	@Column(name = "TYPE")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date date) {
		valueDate = date;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "BOOKING_DATE")
	public Date getBookingDate() {
		return bookingDate;
	}

	@Column(name = "PURPOSE", columnDefinition = "text")
	public String getPurpose() {
		String p = purpose;
		if (purpose != null) {
			p = purpose.replaceAll(" +", " ").trim();
		}
		return p;
	}

	@Column(name = "COUNTERPART_NAME")
	public String getCounterpartName() {
		return counterpartName;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "CATEGORY_ID", nullable = true)
	public FinCategory getCategory() {
		return category;
	}

	public void setCategory(FinCategory category) {
		this.category = category;
	}

	public FinTransaction(JSONObject jo) {
		this.jo = jo;
		try {
			sourceId = jo.getString("id");
			// parentId = jo.getInt("parentId");
			accountId = jo.getString("accountId");
			amount = (long) (jo.getDouble("amount") * 100);

			String valueDateText = jo.getString("valueDate");
			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				valueDate = df.parse(valueDateText);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			String bookingDateText = jo.getString("finapiBookingDate");
			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				bookingDate = df.parse(bookingDateText);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			purpose = jo.getString("purpose");

			purpose = purpose.replaceAll("[\\t\\n\\r]", "");

			counterpartName = jo.getString("counterpartName");
			type = jo.getString("type");

			JSONObject jocat = jo.getJSONObject("category");
			if (jocat != null) {
				category = PersistanceManager.getInstance().getCategory(new FinCategory(jocat));
			}

		} catch (JSONException e) {
		}
	}

	public FinTransaction(me.figo.models.Transaction tx) {
		accountId = tx.getAccountId();
		amount = (long) (tx.getAmount().doubleValue() * 100);
		bookingDate = tx.getBookingDate();
		valueDate = tx.getValueDate();
		type = tx.getBookingText();
		purpose = tx.getPurposeText();
		counterpartName = tx.getName();
		sourceId = tx.getTransactionId();
	}

	public void setAmountCent(long amount) {
		this.amount = amount;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	@JsonIgnore
	public String toString() {
		return String.format("** %d # %s # %f # %s # %s", id, purpose, amount, counterpartName, category);
	}

	@Column(name = "SOURCE_ID")
	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceid) {
		this.sourceId = sourceid;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public void lookForHints() {
		List<Hint> hints = HintEngine.getInstance().search(this);
		if (hints.size() > 0) {
			this.hints = hints;
		}
	}

}
