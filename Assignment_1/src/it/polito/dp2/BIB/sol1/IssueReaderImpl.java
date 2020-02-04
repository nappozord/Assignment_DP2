package it.polito.dp2.BIB.sol1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.dp2.BIB.ArticleReader;
import it.polito.dp2.BIB.IssueReader;
import it.polito.dp2.BIB.JournalReader;
import it.polito.dp2.BIB.sol1.jaxb.ArticleType;
import it.polito.dp2.BIB.sol1.jaxb.Biblio;
import it.polito.dp2.BIB.sol1.jaxb.JournalType;
import it.polito.dp2.BIB.sol1.jaxb.JournalType.Issue;

public class IssueReaderImpl implements IssueReader {
	Issue issue;
	JournalType journal;
	List<ArticleType> articles;
	Biblio bib;

	public IssueReaderImpl(Biblio bib, Issue issue, JournalType journal, List<ArticleType> list) {
		super();
		this.issue = issue;
		this.journal = journal;
		this.articles = list;
		this.bib = bib;
	}

	// Return the set of articles that the issue got in the biblio
	@Override
	public Set<ArticleReader> getArticles() {
		// TODO Auto-generated method stub
		List<ArticleType> arts = searchArticles(issue);
		Set<ArticleReader> aSet = new HashSet<ArticleReader>();
		for(ArticleType art: arts){
			ArticleReader i = new ArticleReaderImpl(bib, art, issue, journal, articles);
			aSet.add(i);
		}
		return aSet;
	}

	@Override
	public JournalReader getJournal() {
		// TODO Auto-generated method stub
		JournalReaderImpl j = new JournalReaderImpl(bib, journal, articles);
		return j;
	}

	@Override
	public int getNumber() {
		// TODO Auto-generated method stub
		return issue.getNumber().intValue();
	}

	@Override
	public int getYear() {
		// TODO Auto-generated method stub
		return issue.getYear().getYear();
	}
	
	// Search if any article has a correspondence with this issue
	private List<ArticleType> searchArticles(Issue issue) {
		// TODO Auto-generated method stub
		List<ArticleType> arts = new ArrayList<ArticleType>();
		for(ArticleType art: articles){
			if(art.getIssue().intValue() == issue.getId().intValue()){
				arts.add(art);
			}
		}
		return arts;
	}

}
