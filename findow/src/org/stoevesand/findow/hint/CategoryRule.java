package org.stoevesand.findow.hint;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "CategoryRule")
@Table(name = "CATEGORY_RULES")
public class CategoryRule {

	public static final int TYPE_CONTAINS = 1;
	public static final int TYPE_REGEX = 2;

	int type;
	String content;
	private Long categoryId;
	private Long id;
	private int prio;

	public CategoryRule() {
		this.type = TYPE_CONTAINS;
		this.content = "";
		this.prio = 0;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getPrio() {
		return prio;
	}

	public void setPrio(int prio) {
		this.prio = prio;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
