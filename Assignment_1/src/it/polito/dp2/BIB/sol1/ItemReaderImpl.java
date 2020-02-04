package it.polito.dp2.BIB.sol1;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.dp2.BIB.ItemReader;
import it.polito.dp2.BIB.sol1.BibReaderImpl.journalAndIssue;
import it.polito.dp2.BIB.sol1.jaxb.ArticleType;
import it.polito.dp2.BIB.sol1.jaxb.Biblio;
import it.polito.dp2.BIB.sol1.jaxb.BookType;
import it.polito.dp2.BIB.sol1.jaxb.JournalType;
import it.polito.dp2.BIB.sol1.jaxb.JournalType.Issue;

public class ItemReaderImpl implements ItemReader {
	Biblio bib;
	BookType book = new BookType();
	ArticleType article = new ArticleType();
	JournalType journal = new JournalType();
	Issue issue = new Issue();
	List<ArticleType> articles = new ArrayList<ArticleType>();

	// Constructor for a book
	public ItemReaderImpl(Biblio bib, BookType book) {
		super();
		this.bib = bib;
		this.book = book;
		this.journal = null;
		this.articles = null;
		this.issue = null;
		this.article = null;
	}
	
	// Constructor for an article
	public ItemReaderImpl(Biblio bib, ArticleType article, Issue issue, JournalType journal, List<ArticleType> articles) {
		super();
		this.bib = bib;
		this.article = article;
		this.issue = issue;
        this.journal = journal;
        this.articles = articles;
		this.book = null;
	}

	@Override
	public String[] getAuthors() {
		// TODO Auto-generated method stub
		if(book != null){
			return book.getAuthor().toArray(new String[0]);
		} else {
			return article.getAuthor().toArray(new String[0]);
		}
	}

	/*An item can be a book or an article, and a citing item can
	 * also be a book or an article. So first there's the need to
	 * distinguish the two cases, then to check if the ID of the
	 * citing item is a book or an article itself.
	 */
	@Override
	public Set<ItemReader> getCitingItems() {
		// TODO Auto-generated method stub
		Set<ItemReader> sItem = new HashSet<ItemReader>();
		if(book != null){
			for(BigInteger i: book.getCitedBy()){
				for(BookType b: bib.getBook()){
					if(b.getId().intValue() == i.intValue()){
						sItem.add(new BookReaderImpl(bib, b));
						break;
					}
				}
				for(ArticleType a: bib.getArticle()){
					if(a.getId().intValue() == i.intValue()){
						BibReaderImpl bimp = new BibReaderImpl(bib);
						journalAndIssue ji = bimp.searchJournal(a);
						sItem.add(new ArticleReaderImpl(bib, a, ji.getI(), ji.getJ(), articles));
						break;
					}
				}
			}
			return sItem;
		} else {
			for(BigInteger i: article.getCitedBy()){
				for(BookType b: bib.getBook()){
					if(b.getId().intValue() == i.intValue()){
						sItem.add(new BookReaderImpl(bib, b));
						break;
					}
				}
				for(ArticleType a: bib.getArticle()){
					if(a.getId().intValue() == i.intValue()){
						BibReaderImpl bimp = new BibReaderImpl(bib);
						journalAndIssue ji = bimp.searchJournal(a);
						sItem.add(new ArticleReaderImpl(bib, a, ji.getI(), ji.getJ(), articles));
						break;
					}
				}
			}
			return sItem;
		}
	}

	@Override
	public String getSubtitle() {
		// TODO Auto-generated method stub
		if(book != null){
			return book.getSubtitle();
		} else {
			return article.getSubtitle();
		}
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		if(book != null){
			return book.getTitle();
		} else {
			return article.getTitle();
		}
	}

}
