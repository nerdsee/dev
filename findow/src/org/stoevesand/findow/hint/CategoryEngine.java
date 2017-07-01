package org.stoevesand.findow.hint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.model.FinCategory;
import org.stoevesand.findow.model.FinTransaction;
import org.stoevesand.findow.persistence.PersistanceManager;

public class CategoryEngine {

	private Logger log = LoggerFactory.getLogger(CategoryEngine.class);

	static private CategoryEngine _instance = null;
	List<CategoryRule> categoryRules;

	private CategoryEngine() {
		refresh();
	}

	public void refresh() {
		log.info("Load CategoryRules");

		// einmal alle laden
		categoryRules = PersistanceManager.getInstance().getCategoryRules();
	};

	public static CategoryEngine getInstance() {
		if (_instance == null) {
			_instance = new CategoryEngine();
		}
		return _instance;
	}

	public FinCategory searchCategory(FinTransaction transaction) {

		String purpose = transaction.getPurpose();
		for (CategoryRule rule : categoryRules) {
			String content = rule.getContent();

			if ((purpose != null) && (content != null) && (purpose.toUpperCase().contains(content.toUpperCase()))) {
				FinCategory category = PersistanceManager.getInstance().getCategory(rule.getCategoryId());
				log.info("Found category: " + "[" + category.getName() + "] " + transaction.getPurpose());
				return category;
			}
		}

		return null;
	}

}
