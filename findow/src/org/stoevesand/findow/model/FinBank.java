package org.stoevesand.findow.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonGetter;

import me.figo.models.Service;

@Entity(name = "Bank")
@Table(name = "BANKS")
public class FinBank {

	int id = 0;
	String name = "";
	String type = "";

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "BANK_ID")
	@JsonGetter("id")
	public int getId() {
		return id;
	}

	@JsonGetter("name")
	public String getName() {
		return name;
	}

	@JsonGetter("blz")
	public String getBlz() {
		return blz;
	}

	@JsonGetter("bic")
	public String getBic() {
		return bic;
	}

	private String blz;
	private String bic;
	private String icon;
	private String advice;

	public FinBank(JSONObject json_bank) {
		try {
			name = json_bank.getString("bank_name");
			blz = json_bank.getString("bank_code");
			bic = json_bank.getString("bic");
			advice = json_bank.getString("advice");
			JSONArray icons = json_bank.getJSONArray("icon");
			icon = icons.getString(0);
			type = "BANK";
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public FinBank(Service service) {
		name = service.getName();
		blz = service.getBankCode();
		bic = null;
		advice = null;
		icon = service.getIcon();
		type = "SERVICE";
	}

	public FinBank() {
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setBlz(String blz) {
		this.blz = blz;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}

	public String toString() {
		return String.format("%s (%d)", name, id);
	}

}
