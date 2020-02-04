package it.polito.dp2.BIB.ass3;

/**
 * An interface for reading information about a book.
 * In addition to the attributes inherited from {@link ItemReader},
 * a book is also characterized by ISBN, publisher and year.
 */
public interface BookReader extends ItemReader {
	
	/**
	 * Reads the ISBN of the book (i.e. a unique identifier of the book)
	 * @return the ISBN of the book.
	 */
	public String getISBN();
	
	/**
	 * Reads the publisher of the book (more precisely, the publisher's name)
	 * @return the publisher's name (a string)
	 */
	public String getPublisher();
	
	/**
	 * Reads the publication year of the book
	 * @return the publication year of the book (an integer)
	 */
	public int getYear();

}
