package org.stoevesand.findow.hint;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.stoevesand.findow.model.FinTransaction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "HINTS")
public class Hint {

	private String name;
	private FinTransaction transaction;
	private Long id;
	private String link;

	public Hint() {
		this.name = "";
	}

	public Hint(String name, String link, FinTransaction transaction) {
		this.name = name;
		this.link = link;
		this.transaction=transaction;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "HINT_ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "TX_ID", nullable = false)
	@JsonIgnore
	public FinTransaction getTransaction() {
		return transaction;
	}

	public void setTransaction(FinTransaction transaction) {
		this.transaction = transaction;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	
}
