package it.polito.dp2.BIB.sol1;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.dp2.BIB.IssueReader;
import it.polito.dp2.BIB.JournalReader;
import it.polito.dp2.BIB.sol1.jaxb.ArticleType;
import it.polito.dp2.BIB.sol1.jaxb.Biblio;
import it.polito.dp2.BIB.sol1.jaxb.JournalType;
import it.polito.dp2.BIB.sol1.jaxb.JournalType.Issue;

public class JournalReaderImpl implements JournalReader {
	JournalType journal;
	List<ArticleType> articles;
	Biblio bib;
	
	public JournalReaderImpl(Biblio bib, JournalType journal, List<ArticleType> list) {
		super();
		this.journal = journal;
		this.articles = list;
		this.bib = bib;
	}

	@Override
	public String getISSN() {
		// TODO Auto-generated method stub
		return journal.getISSN();
	}

	// Return, if there is, a unique issue, distinct by number and year
	@Override
	public IssueReader getIssue(int arg0, int arg1) {
		// TODO Auto-generated method stub
		for(Issue issue: journal.getIssue()){
			if(issue.getYear().getYear() == arg0 && issue.getNumber().intValue() == arg1){
				IssueReader i = new IssueReaderImpl(bib, issue, journal, articles);
				return i;
			}
		}
		return null;
	}

	// Return all the issues that the journal has
	@Override
	public Set<IssueReader> getIssues(int arg0, int arg1) {
		// TODO Auto-generated method stub
		Set<IssueReader> issues = new HashSet<IssueReader>();
		for(Issue issue: journal.getIssue()){
			if(issue.getYear().getYear() >= arg0 && issue.getYear().getYear() <= arg1){
				IssueReader i = new IssueReaderImpl(bib, issue, journal, articles);
				issues.add(i);
			}
		}
		return issues;
	}

	@Override
	public String getPublisher() {
		// TODO Auto-generated method stub
		return journal.getPublisher();
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return journal.getTitle();
	}
	
}
