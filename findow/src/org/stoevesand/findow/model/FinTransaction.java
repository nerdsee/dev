package org.stoevesand.findow.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Vector;

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

import me.figo.models.Category;

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
    private Long accountId;
    private long amount;
    private Date valueDate;
    private Date bookingDate;
    private String purpose;
    private String counterpartName;

    private static String[] delims = { "IBAN+", "BIC+", "EREF+", "KREF+", "MREF+", "CRED+", "DEBT+", "COAM+", "OAMT+", "SVWZ+", "ABWA+", "ABWE+", "BREF+",
        "RREF+" };

    public void setPurposeMT940(String purpose) {

        if (purpose != null) {
            int pos = -1;
            int lastpos = -1;
            boolean foundSepa = false;
            List<String> entries = new Vector<String>();
            for (String delim : delims) {
                pos = purpose.toUpperCase().indexOf(delim);
                if (pos >= 0) {
                    foundSepa = true;
                    if (lastpos >= 0) {
                        String entry = purpose.substring(lastpos, pos);
                        entries.add(entry);
                    }
                    lastpos = pos;
                }
            }
            if (lastpos >= 0) {
                String entry = purpose.substring(lastpos);
                entries.add(entry);
            }

            if (!foundSepa) {
                setPurpose(purpose);
            } else {
                setSepaFields(entries);
            }
        }
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setCounterpartName(String counterpartName) {
        this.counterpartName = counterpartName;
    }

    private FinCategory category;

    private String type;

    private List<Hint> hints;

    private Long userId;

    private String bankCode;

    private String bankName;

    private String bookingText;

    private List<Category> categories;

    private String txCode;

    private String iban;

    private String bic;

    private String eref;

    private String kref;

    private String mref;

    private String cred;

    private String debt;

    private String coam;

    private String oamt;

    private String abwa;

    private String abwe;

    private String bref;

    private String rref;

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
        return (double)amount / 100;
    }

    @Transient
    public FinAccount getAccount() {
        FinAccount account = PersistanceManager.getInstance().getAccount(accountId);

        return account;
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
    public Long getAccountId() {
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
            accountId = jo.getLong("accountId");
            amount = (long)(jo.getDouble("amount") * 100);

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

    public FinTransaction(FinUser user, FinAccount account, me.figo.models.Transaction tx) {
        accountId = account.getId();
        amount = (long)(tx.getAmount().doubleValue() * 100);
        bookingDate = tx.getBookingDate();
        valueDate = tx.getValueDate();

        setPurposeMT940(tx.getPurposeText());

        counterpartName = tx.getName();
        sourceId = tx.getTransactionId();
        userId = user.getId();
        type = tx.getType();

        bankCode = tx.getBankCode();
        bankName = tx.getBankName();
        bookingText = tx.getBookingText();
        categories = tx.getCategories();
        txCode = tx.getTransactionCode();

    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBookingText() {
        return bookingText;
    }

    public void setBookingText(String bookingText) {
        this.bookingText = bookingText;
    }

    public String getTxCode() {
        return txCode;
    }

    public void setTxCode(String txCode) {
        this.txCode = txCode;
    }

    public void setAmountCent(long amount) {
        this.amount = amount;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    @Column(name = "USER_ID")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getEref() {
        return eref;
    }

    public void setEref(String eref) {
        this.eref = eref;
    }

    public String getKref() {
        return kref;
    }

    public void setKref(String kref) {
        this.kref = kref;
    }

    public String getMref() {
        return mref;
    }

    public void setMref(String mref) {
        this.mref = mref;
    }

    public String getCred() {
        return cred;
    }

    public void setCred(String cred) {
        this.cred = cred;
    }

    public String getDebt() {
        return debt;
    }

    public void setDebt(String debt) {
        this.debt = debt;
    }

    public String getCoam() {
        return coam;
    }

    public void setCoam(String coam) {
        this.coam = coam;
    }

    public String getOamt() {
        return oamt;
    }

    public void setOamt(String oamt) {
        this.oamt = oamt;
    }

    public String getAbwa() {
        return abwa;
    }

    public void setAbwa(String abwa) {
        this.abwa = abwa;
    }

    public String getAbwe() {
        return abwe;
    }

    public void setAbwe(String abwe) {
        this.abwe = abwe;
    }

    public String getBref() {
        return bref;
    }

    public void setBref(String bref) {
        this.bref = bref;
    }

    public String getRref() {
        return rref;
    }

    public void setRref(String rref) {
        this.rref = rref;
    }

    public void lookForHints() {
        List<Hint> hints = HintEngine.getInstance().search(this);
        if (hints.size() > 0) {
            this.hints = hints;
        }
    }

    // delims = { "IBAN+", "BIC+", "EREF+", "KREF+", "MREF+", "CRED+", "DEBT+",
    // "COAM+", "OAMT+", "SVWZ+", "ABWA+", "ABWE+", "BREF+", "RREF+" };

    private void setSepaFields(List<String> entries) {
        for (String entry : entries) {
            if (entry.toUpperCase().startsWith("SVWZ+")) {
                purpose = entry.substring(5);
            } else if (entry.toUpperCase().startsWith("IBAN+")) {
                iban = entry.substring(5);
            } else if (entry.toUpperCase().startsWith("BIC+")) {
                bic = entry.substring(5);
            } else if (entry.toUpperCase().startsWith("EREF+")) {
                eref = entry.substring(5);
            } else if (entry.toUpperCase().startsWith("KREF+")) {
                kref = entry.substring(5);
            } else if (entry.toUpperCase().startsWith("MREF+")) {
                mref = entry.substring(5);
            } else if (entry.toUpperCase().startsWith("CRED+")) {
                cred = entry.substring(5);
            } else if (entry.toUpperCase().startsWith("DEBT+")) {
                debt = entry.substring(5);
            } else if (entry.toUpperCase().startsWith("COAM+")) {
                coam = entry.substring(5);
            } else if (entry.toUpperCase().startsWith("OAMT+")) {
                oamt = entry.substring(5);
            } else if (entry.toUpperCase().startsWith("ABWA+")) {
                abwa = entry.substring(5);
            } else if (entry.toUpperCase().startsWith("ABWE+")) {
                abwe = entry.substring(5);
            } else if (entry.toUpperCase().startsWith("BREF+")) {
                bref = entry.substring(5);
            } else if (entry.toUpperCase().startsWith("RREF+")) {
                rref = entry.substring(5);
            }

        }

    }

    public static Comparator<FinTransaction> BookingDateComparator = new Comparator<FinTransaction>() {

        public int compare(FinTransaction tx1, FinTransaction tx2) {
            return tx1.bookingDate.compareTo(tx2.bookingDate);
        }

    };

}
