package org.stoevesand.findow.hint;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.stoevesand.findow.model.Transaction;

@Entity
@Table(name = "REGEXHINTS")
public class RegexHint implements HintAnalyzer {

	String name;
	String type;
	String content;
	private Long id;

	public RegexHint() {
		this.name = "";
		this.type = "";
		this.content = "";
	}

	@Override
	public Hint search(Transaction transaction) {
		Hint hint = null;
		if (transaction.getPurpose().toUpperCase().matches(content)) {
			hint = new Hint(name, transaction);
		}
		;
		return hint;
	}

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "REGEX_HINT_ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
