package org.stoevesand.findow.hint;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.model.Transaction;
import org.stoevesand.findow.persistence.PersistanceManager;

public class HintEngine {

	private Logger log = LoggerFactory.getLogger(HintEngine.class);

	static private HintEngine _instance = null;
	List<RegexHint> hintAnalyzers;

	private HintEngine() {
		refresh();
	}

	public void refresh() {
		log.info("Load HintAnalyzers");
		hintAnalyzers = PersistanceManager.getInstance().getRegexHints();
		for (HintAnalyzer ha : hintAnalyzers) {
			log.info("Analyzer: " + ha.getName());
		}
	};

	public static HintEngine getInstance() {
		if (_instance == null) {
			_instance = new HintEngine();
		}
		return _instance;
	}

	public List<Hint> search(Transaction transaction) {
		List<Hint> hints = new Vector<Hint>();

		for (HintAnalyzer hintAnalyzer : hintAnalyzers) {
			Hint hint = hintAnalyzer.search(transaction);
			if (hint != null) {
				log.info("Fount hint: " + "[" + hint.getName() + "] " + transaction.getPurpose());
				hints.add(hint);
			}
		}

		return hints;
	}

}
