package it.polito.dp2.BIB.sol1;

import it.polito.dp2.BIB.BookReader;
import it.polito.dp2.BIB.sol1.jaxb.Biblio;
import it.polito.dp2.BIB.sol1.jaxb.BookType;

public class BookReaderImpl extends ItemReaderImpl implements BookReader {
	
	public BookReaderImpl(Biblio bib, BookType book) {
		super(bib, book);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getISBN() {
		// TODO Auto-generated method stub
		return book.getISBN();
	}

	@Override
	public String getPublisher() {
		// TODO Auto-generated method stub
		return book.getPublisher();
	}

	@Override
	public int getYear() {
		// TODO Auto-generated method stub
		return book.getYear().getYear();
	}

}
