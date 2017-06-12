package org.stoevesand.findow.hint;

import org.stoevesand.findow.model.FinTransaction;

public interface HintAnalyzer {

	public Hint search(FinTransaction transaction);
	public String getName();
	
}
