package org.stoevesand.findow.hint;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.stoevesand.findow.model.FinCategory;
import org.stoevesand.findow.model.FinTransaction;
import org.stoevesand.findow.model.FinUser;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "CategoryRule")
@Table(name = "CATEGORY_RULES")
public class CategoryRule {

	String name;
	String type;
	String content;
	private Long categoryId;
	private Long id;

	public CategoryRule() {
		this.name = "";
		this.type = "";
		this.content = "";
	}

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "CATEGORY_RULE_ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "CATEGORY_ID")
	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long id) {
		categoryId = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}