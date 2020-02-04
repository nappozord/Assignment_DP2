package it.polito.dp2.BIB.ass3;

/**
 * An interface for reading information about an article in the BIB system.
 * In addition to the attributes inherited from {@link ItemReader},
 * an article is also characterized by the journal, year and number of the
 * issue it has been published in.
 */
public interface ArticleReader extends ItemReader {
	
	/**
	 * Gets the ISSN of the journal where the article has been published.
	 * @return the ISSN as a string.
	 */
	public String getJournal();

	/**
	 * Gets the publication year of the article
	 * @return the publication year (an integer)
	 */
	public int getYear();
	
	/**
	 * Gets the issue number of the article
	 * @return the issue number (an integer)
	 */
	public int getNumber();
	
}
