package it.polito.dp2.BIB.sol1;

import java.util.HashSet;
import java.util.Set;

import it.polito.dp2.BIB.ArticleReader;
import it.polito.dp2.BIB.BibReader;
import it.polito.dp2.BIB.BookReader;
import it.polito.dp2.BIB.ItemReader;
import it.polito.dp2.BIB.JournalReader;
import it.polito.dp2.BIB.sol1.jaxb.*;
import it.polito.dp2.BIB.sol1.jaxb.JournalType.Issue;

public class BibReaderImpl implements BibReader {
	Biblio bib;

	public BibReaderImpl(Biblio bib) {
		super();
		this.bib = bib;
	}

	// Returns the book which ISBN correspond exactly to the one in input
	@Override
	public BookReader getBook(String arg0) {
		// TODO Auto-generated method stub
		for(BookType book: bib.getBook()){
			if(book.getISBN().equals(arg0)){
				BookReader br = new BookReaderImpl(bib, book);
				return br;
			}
		}
		return null;
	}

	/*Returns a set of ItemReader. An ItemReader can be a book or an article 
	 * because their classes extend the item one.
	 */
	@Override
	public Set<ItemReader> getItems(String arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
		Set<ItemReader> iSet = new HashSet<ItemReader>(); 
        for (BookType b : bib.getBook()){
        	// Check if the book year of publication is within the limits
        	if(b.getYear().getYear() >= arg1 && b.getYear().getYear() <= arg2){
        		/*If the first argument is a valid string (not null),
        		 * check if the book contains that keyword
        		 */
        		if(arg0 == null){
        			BookReader i = new BookReaderImpl(bib, b);
        			iSet.add(i);
        		} else if(b.getTitle().contains(arg0)){
        			BookReader i = new BookReaderImpl(bib, b);
        			iSet.add(i);
        		}
        	}
        }
        for (ArticleType a : bib.getArticle()){
        	// If the verify returns true, then it's ok to go on
        	if(verifyIssueYear(arg1, arg2, a)){
        		// Checks the first argument same as the book
        		if(arg0 == null){
        			// Get the journal AND the issue for the article
        			journalAndIssue ji = searchJournal(a);
        			ArticleReader i = new ArticleReaderImpl(bib, a, ji.getI(), ji.getJ(), bib.getArticle());
        			iSet.add(i);
        		} else if(a.getTitle().contains(arg0)){
        			journalAndIssue ji = searchJournal(a);
        			ArticleReader i = new ArticleReaderImpl(bib, a, ji.getI(), ji.getJ(), bib.getArticle());
        			iSet.add(i);
        		}
        	}
        }
		return iSet;
	}

	/*Returns a class journalAndIssue that contains an instance
	 * of a journal and an issue, both related to the input article
	 */
	public journalAndIssue searchJournal(ArticleType a) {
		// TODO Auto-generated method stub
		journalAndIssue ji = new journalAndIssue();
		for(JournalType j: bib.getJournal()){
			for(Issue i: j.getIssue()){
				if(a.getIssue().intValue() == i.getId().intValue()){
					ji.setI(i);
					ji.setJ(j);
					return ji;
				}
			}
		}
		return null;
	}

	/*Check if the issue the article is in respects the year constraints:
	 * with a double for, because ArticleType doesn't have per se the reference
	 * to it's own issue...
	 */
	private boolean verifyIssueYear(int arg1, int arg2, ArticleType a) {
		// TODO Auto-generated method stub
		for(JournalType j: bib.getJournal()){
			for(Issue i: j.getIssue()){
				if(i.getYear().getYear() >= arg1 && i.getYear().getYear() <= arg2){
					if(i.getId().intValue() == a.getIssue().intValue())
						return true;
				}
			}
		}
		return false;
	}

	// Returns the journal which ISSN correspond exactly to the one in input
	@Override
	public JournalReader getJournal(String arg0) {
		// TODO Auto-generated method stub
		for(JournalType journal: bib.getJournal()){
			if(journal.getISSN().equals(arg0)){
				JournalReader jr = new JournalReaderImpl(bib, journal, bib.getArticle());
				return jr;
			}
		}
		return null;
	}

	// Returns a set of journals given a keyword or all journals if the keyword is null
	@Override
	public Set<JournalReader> getJournals(String arg0) {
		// TODO Auto-generated method stub
		Set<JournalReader> jSet = new HashSet<JournalReader>(); 
        for (JournalType x : bib.getJournal()){
        	if(arg0 == null){
        		JournalReader j = new JournalReaderImpl(bib, x, bib.getArticle());
            	jSet.add(j);
        	}else if(x.getTitle().contains(arg0)){
        		JournalReader j = new JournalReaderImpl(bib, x, bib.getArticle());
            	jSet.add(j);
        	}
        }
		return jSet;
	}
	
	public class journalAndIssue{
		public Issue i;
		public JournalType j;
		
		public Issue getI() {
			return i;
		}
		public void setI(Issue i) {
			this.i = i;
		}
		public JournalType getJ() {
			return j;
		}
		public void setJ(JournalType j) {
			this.j = j;
		}
	}

}
