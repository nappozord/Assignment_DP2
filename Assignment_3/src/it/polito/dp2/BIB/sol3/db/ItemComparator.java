package it.polito.dp2.BIB.sol3.db;

import java.util.Calendar;
import java.util.Comparator;

import it.polito.dp2.BIB.sol3.service.jaxb.ArticleType;
import it.polito.dp2.BIB.sol3.service.jaxb.BookType;
import it.polito.dp2.BIB.sol3.service.jaxb.Item;

public class ItemComparator implements Comparator<Item> {

	@Override
	public int compare(Item i1, Item i2) {
		int yeardiff = getYear(i1)-getYear(i2);
		if (yeardiff!=0)
			return yeardiff;
		else
			return i1.getSelf().compareTo(i2.getSelf());
	}

	private int  getYear(Item i1) {
		ArticleType a1 = i1.getArticle();
		if (a1!=null)
			return getYearFromArticle(a1);
		else
			return getYearFromBook(i1.getBook());
	}

	private int getYearFromBook(BookType book) {
		return book.getYear().toGregorianCalendar().get(Calendar.YEAR);
	}

	private int getYearFromArticle(ArticleType a) {
		return a.getVolume().toGregorianCalendar().get(Calendar.YEAR);
	}

}
