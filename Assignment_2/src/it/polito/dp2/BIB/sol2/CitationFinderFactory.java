package it.polito.dp2.BIB.sol2;

import it.polito.dp2.BIB.ass2.CitationFinder;
import it.polito.dp2.BIB.ass2.CitationFinderException;

public class CitationFinderFactory extends it.polito.dp2.BIB.ass2.CitationFinderFactory {

	@Override
	public CitationFinder newCitationFinder() throws CitationFinderException {
		// TODO Auto-generated method stub
		CitationFinder cf = new CitationFinderImpl();
		return cf;
	}

}
