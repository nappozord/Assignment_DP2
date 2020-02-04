package it.polito.dp2.BIB.sol1;

import it.polito.dp2.BIB.ArticleReader;
import it.polito.dp2.BIB.IssueReader;
import it.polito.dp2.BIB.JournalReader;
import it.polito.dp2.BIB.sol1.jaxb.ArticleType;
import it.polito.dp2.BIB.sol1.jaxb.Biblio;
import it.polito.dp2.BIB.sol1.jaxb.JournalType;
import it.polito.dp2.BIB.sol1.jaxb.JournalType.Issue;
import java.util.List;

public class ArticleReaderImpl extends ItemReaderImpl implements ArticleReader {

    public ArticleReaderImpl(Biblio bib, ArticleType article, Issue issue, JournalType journal, List<ArticleType> articles) {
        super(bib, article, issue, journal, articles);
    }
    
    @Override
    public IssueReader getIssue() {
        IssueReader i = new IssueReaderImpl(bib, issue, journal, articles);
        return i;
    }

    @Override
    public JournalReader getJournal() {
        JournalReader j = new JournalReaderImpl(bib, journal, articles);
        return j;
    }
}
