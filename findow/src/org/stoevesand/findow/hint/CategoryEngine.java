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
	Map<Long, List<CategoryRule>> categoryRulesMap = null;

	private CategoryEngine() {
		refresh();
	}

	public void refresh() {
		log.info("Load CategoryRules");
		categoryRulesMap = new HashMap<Long, List<CategoryRule>>();

		// einmal alle laden
		List<CategoryRule> categoryRules = PersistanceManager.getInstance().getCategoryRules();

		// und dann in der Map zu den Categories sortieren
		for (CategoryRule rule : categoryRules) {
			Long categoryId = rule.getCategoryId();
			List<CategoryRule> ruleset = categoryRulesMap.get(categoryId);

			if (ruleset == null) {
				ruleset = new Vector<CategoryRule>();
			}

			ruleset.add(rule);
			categoryRulesMap.put(categoryId, ruleset);

		}

	};

	public static CategoryEngine getInstance() {
		if (_instance == null) {
			_instance = new CategoryEngine();
		}
		return _instance;
	}

	public FinCategory searchCategory(FinTransaction transaction) {

		List<FinCategory> categories = PersistanceManager.getInstance().getCategories();

		for (FinCategory category : categories) {

			Long categoryId = category.getId();
			List<CategoryRule> ruleset = categoryRulesMap.get(categoryId);

			if (ruleset != null) {
				for (CategoryRule rule : ruleset) {
					String purpose = transaction.getPurpose();
					String content = rule.getContent();

					if ((purpose != null) && (content != null) && (purpose.toUpperCase().contains(content.toUpperCase()))) {
						log.info("Found category: " + "[" + category.getName() + "] " + transaction.getPurpose());
						return category;
					}
				}
			}

		}

		return null;
	}

}
