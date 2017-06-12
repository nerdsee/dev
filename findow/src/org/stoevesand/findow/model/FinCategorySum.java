package org.stoevesand.findow.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.stoevesand.findow.persistence.PersistanceManager;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class FinCategorySum {

	private double sum = 0;
	private long categoryId = 0;
	private int count;

	@JsonGetter
	public double getSum() {
		return sum;
	}

	@Transient
	public FinCategory getCategory() {
		return category;
	}

	private FinCategory category;

	public FinCategorySum() {
		this.category = null;
		sum = 0;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public void setCategory(FinCategory category) {
		this.category = category;
	}

	@Id
	@Column(name = "CATEGORY_ID")
	@JsonIgnore
	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
		if (categoryId>0) {
			category = PersistanceManager.getInstance().getCategory(categoryId);
		}
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
