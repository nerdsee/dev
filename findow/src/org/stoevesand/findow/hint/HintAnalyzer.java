package org.stoevesand.findow.hint;

import org.stoevesand.findow.model.Transaction;

public interface HintAnalyzer {

	public Hint search(Transaction transaction);
	public String getName();
	
}
