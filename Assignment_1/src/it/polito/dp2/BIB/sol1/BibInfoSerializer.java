package it.polito.dp2.BIB.sol1;

import it.polito.dp2.BIB.ArticleReader;
import it.polito.dp2.BIB.BibReader;
import it.polito.dp2.BIB.BibReaderException;
import it.polito.dp2.BIB.BibReaderFactory;
import it.polito.dp2.BIB.BookReader;
import it.polito.dp2.BIB.IssueReader;
import it.polito.dp2.BIB.ItemReader;
import it.polito.dp2.BIB.JournalReader;
import it.polito.dp2.BIB.sol1.jaxb.ArticleType;
import it.polito.dp2.BIB.sol1.jaxb.Biblio;
import it.polito.dp2.BIB.sol1.jaxb.BookType;
import it.polito.dp2.BIB.sol1.jaxb.JournalType;
import it.polito.dp2.BIB.sol1.jaxb.JournalType.Issue;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class BibInfoSerializer {
    private BibReader monitor;
    private int counter_issue;
    private HashMap<ItemReader, Integer> items = new HashMap<ItemReader, Integer>();
    private HashMap<IssueReader, Integer> issues = new HashMap<IssueReader, Integer>();

    public BibInfoSerializer() throws BibReaderException {
    	System.setProperty("it.polito.dp2.BIB.BibReaderFactory", "it.polito.dp2.BIB.Random.BibReaderFactoryImpl");
    	BibReaderFactory factory = BibReaderFactory.newInstance();
        this.monitor = factory.newBibReader();
    }

    public BibInfoSerializer(BibReader monitor) {
        this.monitor = monitor;
    }

    public static void main(String[] args) throws DatatypeConfigurationException, JAXBException, ParseException {
        try {
            BibInfoSerializer wf = new BibInfoSerializer();
            new Biblio();
            Biblio biblio = wf.setBiblio();
            String filename = new String();
            System.out.println(args.length);
            if(args.length == 0){
            	/*In case there are no args (meaning no output filename given, which should never occur, but...)
            	 * use the default output filename (biblio_e_2.xml)
            	 */
            	filename = "xsd/biblio_e_2.xml";
            } else {
            	filename = args[0];
            }
            wf.Marshaller(biblio, filename);
        } catch (BibReaderException var3) {
            System.err.println("Could not instantiate data generator.");
            var3.printStackTrace();
        }

    }
    
    /* Given the class Biblio, the marshaller generates an xml
     * named after the argument passed and populated with the 
     * informations from the biblio instance
     */
    private void Marshaller(Biblio biblio, String filename) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(new Class[]{Biblio.class});
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
        try {
        	System.out.println("file is " + filename);
            OutputStream os = new FileOutputStream(filename);
            jaxbMarshaller.marshal(biblio, os);
        } catch (FileNotFoundException var5) {
            var5.printStackTrace();
        }

    }

    /*Reads the informations taken from the monitor
     * and stores them in the biblio_e.xsd-auto-generated java classes
     */
    private Biblio setBiblio() throws DatatypeConfigurationException, ParseException {
        Biblio biblio = new Biblio();
        int counter_art_bo = 0;
        this.counter_issue = 0;
        Set<JournalReader> setJ = this.monitor.getJournals((String)null);
        Iterator<JournalReader> var5 = setJ.iterator();

        // Populates the journals fields
        while(var5.hasNext()) {
            JournalReader journal = (JournalReader)var5.next();
            JournalType jo = this.setJournal(journal);
            biblio.getJournal().add(jo);
        }

        Set<ItemReader> setI = this.monitor.getItems((String)null, 0, 3000);

        ItemReader item;
        Iterator<ItemReader> var10;
        // Every item is mapped with an exclusive ID (which could be different from the biblio.xml file!)
        for(var10 = setI.iterator(); var10.hasNext(); ++counter_art_bo) {
            item = (ItemReader)var10.next();
            this.items.put(item, counter_art_bo);
        }

        var10 = setI.iterator();
        
        // Populates the items fields
        while(var10.hasNext()) {
            item = (ItemReader)var10.next();
            if (item instanceof ArticleReader) {
                ArticleType art = this.setArticle(item);
                biblio.getArticle().add(art);
            } else if (item instanceof BookReader) {
                BookType bo = this.setBook(item);
                biblio.getBook().add(bo);
            }
        }

        return biblio;
    }

    // Populates the journals
    private JournalType setJournal(JournalReader journal) throws DatatypeConfigurationException, ParseException {
        JournalType jo = new JournalType();
        jo.setTitle(journal.getTitle());
        jo.setPublisher(journal.getPublisher());
        jo.setISSN(journal.getISSN());

        for(Iterator<?> var4 = journal.getIssues(0, 3000).iterator(); var4.hasNext(); ++this.counter_issue) {
            IssueReader issue = (IssueReader)var4.next();
            Issue iss = new Issue();
            iss.setId(BigInteger.valueOf((long)this.counter_issue));
            iss.setNumber(BigInteger.valueOf((long)issue.getNumber()));
            iss.setYear(this.getXMLYear(issue.getYear()));
            
            // Every issue is mapped with an exclusive id (which could be different from the biblio.xml file!)
            jo.getIssue().add(iss);
            this.issues.put(issue, this.counter_issue);
        }

        return jo;
    }

    // This function "simply" generates an xml-type date given in integer
    private XMLGregorianCalendar getXMLYear(int year) throws DatatypeConfigurationException, ParseException {
        GregorianCalendar c = new GregorianCalendar();
        Date dob = null;
        DateFormat df = new SimpleDateFormat("yyyy");
        String x = Integer.toString(year);
        dob = df.parse(x);
        c.setTimeInMillis(dob.getTime());
        XMLGregorianCalendar xmldate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        return xmldate;
    }

    // Populates the books
    private BookType setBook(ItemReader item) throws DatatypeConfigurationException, ParseException {
        BookReader book = (BookReader)item;
        BookType bo = new BookType();
        bo.setISBN(book.getISBN());
        bo.setPublisher(book.getPublisher());
        bo.setYear(this.getXMLYear(book.getYear()));
        bo.setId(BigInteger.valueOf((long)(Integer)this.items.get(item)));
        bo.setTitle(item.getTitle());
        if (item.getSubtitle() != null) {
            bo.setSubtitle(item.getSubtitle());
        }

        String[] var7;
        int var6 = (var7 = item.getAuthors()).length;

        for(int var5 = 0; var5 < var6; ++var5) {
            String author = var7[var5];
            bo.getAuthor().add(author);
        }

        Iterator<?> var9 = item.getCitingItems().iterator();

        while(var9.hasNext()) {
            ItemReader citer = (ItemReader)var9.next();
            if (this.items.containsKey(citer)) {
                bo.getCitedBy().add(BigInteger.valueOf((long)(Integer)this.items.get(citer)));
            }
        }

        return bo;
    }

    // Populates the articles
    private ArticleType setArticle(ItemReader item) {
        ArticleReader article = (ArticleReader)item;
        ArticleType art = new ArticleType();
        art.setISSN(article.getJournal().getISSN());
        art.setIssue(BigInteger.valueOf((long)(Integer)this.issues.get(article.getIssue())));
        art.setId(BigInteger.valueOf((long)(Integer)this.items.get(item)));
        art.setTitle(item.getTitle());
        if (item.getSubtitle() != null) {
            art.setSubtitle(item.getSubtitle());
        }

        String[] var7;
        int var6 = (var7 = item.getAuthors()).length;

        for(int var5 = 0; var5 < var6; ++var5) {
            String author = var7[var5];
            art.getAuthor().add(author);
        }

        Iterator<?> var9 = item.getCitingItems().iterator();

        // If the item has been cited, get the citer ID from the map and add it to the CitedBy field
        while(var9.hasNext()) {
            ItemReader citer = (ItemReader)var9.next();
            if (this.items.containsKey(citer)) {
                art.getCitedBy().add(BigInteger.valueOf((long)(Integer)this.items.get(citer)));
            }
        }

        return art;
    }
}
