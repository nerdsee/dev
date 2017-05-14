package org.stoevesand.findow.hint;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.stoevesand.findow.model.Transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "HINTS")
public class Hint {

	private String name;
	private Transaction transaction;
	private Long id;

	public Hint() {
		this.name = "";
	}

	public Hint(String name, Transaction transaction) {
		this.name = name;
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
	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

}
