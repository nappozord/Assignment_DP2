package it.polito.dp2.BIB.ass1.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.polito.dp2.BIB.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
//import java.util.TimeZone;
import java.util.TreeSet;

public class BibTests {
	protected static BibReader referenceBibReader; // reference input data
	protected static BibReader testBibReader; // implementation under test

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		// Create reference data generator
		System.setProperty("it.polito.dp2.BIB.BibReaderFactory", "it.polito.dp2.BIB.Random.BibReaderFactoryImpl");
		referenceBibReader = BibReaderFactory.newInstance().newBibReader();

		// Create implementation under test
		System.setProperty("it.polito.dp2.BIB.BibReaderFactory", "it.polito.dp2.BIB.sol1.BibReaderFactory");
		testBibReader = BibReaderFactory.newInstance().newBibReader();

	}

	@Before
	public void setUp() throws Exception {
		assertNotNull("Internal tester error during test setup: null reference", referenceBibReader);
		assertNotNull("Could not run tests: the implementation under test generated a null BibReader", testBibReader);
	}

	
	@Test
	// Check that getItems(null,0,9999) returns the expected data
	public final void testGetItems() {
		int since = 0;
		int to = 9999;
		// call getItems on the two implementations
		Set<ItemReader> ris = referenceBibReader.getItems(null, since, to);
		Set<ItemReader> tis = testBibReader.getItems(null, since, to);
		

		// compare the returned sets
		List<Iterator<ItemReader>> list = startComparison(ris, tis, "Items");
		if (list != null) {
			Iterator<ItemReader> ri = list.get(0);
			Iterator<ItemReader> ti = list.get(1);

			while (ri.hasNext() && ti.hasNext()) {
				ItemReader rir = ri.next();
				ItemReader tir = ti.next();
				compareItemReader(rir, tir);
			}
		}
	}

	// private method for comparing two ItemReader objects
	protected void compareItemReader(ItemReader rir, ItemReader tir) {
		// check the ItemReaders are not null and return the same data
		compareTitleSubtitleAuhor(rir, tir, " item ");

		List<Iterator<ItemReader>> list = startComparison(rir.getCitingItems(), tir.getCitingItems(), "citing items");
		if (list != null) {
			System.out.println("  citing items:");
			Iterator<ItemReader> ri = list.get(0);
			Iterator<ItemReader> ti = list.get(1);
			while (ri.hasNext() && ti.hasNext()) {
				ItemReader rItem = ri.next();
				ItemReader tItem = ti.next();
				compareTitleSubtitleAuhor(rItem, tItem, " citing item ");
				compareArticleOrBook(rItem, tItem);
			}
		}
	}

	// compares the article or book specific fields of two item readers
	private void compareArticleOrBook(ItemReader rItem, ItemReader tItem) {	
		if (rItem instanceof BookReader) {
			System.out.println("     book "+ rItem.getTitle());
			assertTrue("wrong type of item reader (should be BookReader)", tItem instanceof BookReader);
			BookReader rBookReader = (BookReader) rItem;
			BookReader tBookReader = (BookReader) tItem;
			compareString(rBookReader.getISBN(), tBookReader.getISBN(), " book reader ISBN ");
			compareString(rBookReader.getPublisher(), tBookReader.getPublisher(), " book reader Publisher ");
			assertEquals("wrong publication year ", rBookReader.getYear(), tBookReader.getYear());
		} else if (rItem instanceof ArticleReader) {
			System.out.println("     article "+ rItem.getTitle());
			assertTrue("wrong type of item reader (should be ArticleReader)", tItem instanceof ArticleReader);
			ArticleReader rArticleReader = (ArticleReader) rItem;
			ArticleReader tArticleReader = (ArticleReader) tItem;
			compareString(rArticleReader.getJournal().getISSN(), tArticleReader.getJournal().getISSN(), " article reader ISSN");
			assertEquals("wrong article reader issue number", rArticleReader.getIssue().getNumber(), tArticleReader.getIssue().getNumber());
		}
	}

	// checks two item readers are not null and compares their title, subtitle, and authors
	protected void compareTitleSubtitleAuhor(ItemReader rItem, ItemReader tItem, String meaning) {
		// check the ItemReaders are not null
		assertNotNull("internal tester error: null item reader", rItem);
		assertNotNull("unexpected null item reader", tItem);
		
		System.out.println("Comparing "+ meaning + " " + rItem.getTitle());

		compareString(rItem.getTitle(), tItem.getTitle(), meaning + " title");
		assertTrue("lists of authors do not match ", compareStringArray(rItem.getAuthors(), tItem.getAuthors()," authors"));
		if(rItem.getSubtitle()!=null) compareString(rItem.getSubtitle(), tItem.getSubtitle(), meaning + " subtitle");
	}

	@Test
	// Check that testGetJournals(null) returns the expected data
	public final void testGetJournals() {

		Set<JournalReader> rjs = referenceBibReader.getJournals(null);
		Set<JournalReader> tjs = testBibReader.getJournals(null);

		assertNotNull("internal tester error: null journal reader", rjs);
		assertNotNull("unexpected null journal reader", tjs);

		assertEquals("wrong Number of Journals", rjs.size(), tjs.size());

		// create TreeSets of elements, using the comparator for sorting, one for
		// reference and one for implementation under test
		TreeSet<JournalReader> rts = new TreeSet<JournalReader>(new JournalReaderComparator());
		TreeSet<JournalReader> tts = new TreeSet<JournalReader>(new JournalReaderComparator());

		rts.addAll(rjs);
		tts.addAll(tjs);

		Iterator<JournalReader> listRef = rts.iterator();
		Iterator<JournalReader> listTest = tts.iterator();
		while (listRef.hasNext() && listTest.hasNext()) {
			JournalReader refJR = listRef.next();
			JournalReader testJR = listTest.next();
			compareJournals(refJR, testJR);
		}

	}

	// private method for comparing two JournalReader objects
	private void compareJournals(JournalReader refJR, JournalReader testJR) {
		int since = 0;
		int to = 9999;
		// check the JournalReaders are not null
		assertNotNull("internal tester error: null journal reader", refJR);
		assertNotNull("unexpected null journal reader", testJR);

		System.out.println("Comparing journal " + refJR.getTitle());
		
		// compare main fields
		compareJournalStrings(refJR, testJR, " journal ");
		
		// compare issues
		Set<IssueReader> ris = refJR.getIssues(since, to);
		Set<IssueReader> tis = testJR.getIssues(since, to);
		
		assertNotNull("internal tester error: null issue set", ris);
		assertNotNull("unexpected null issue set", tis);

		assertEquals("wrong number of journal issues ", ris.size(), tis.size());

		// create TreeSets of elements, using the comparator for sorting, one for
		// reference and one for implementation under test
		TreeSet<IssueReader> rts = new TreeSet<IssueReader>(new IssueReaderComparator());
		TreeSet<IssueReader> tts = new TreeSet<IssueReader>(new IssueReaderComparator());

		rts.addAll(refJR.getIssues(since, to));
		tts.addAll(testJR.getIssues(since, to));

		Iterator<IssueReader> listRef = rts.iterator();
		Iterator<IssueReader> listTest = tts.iterator();

		// compare issues one by one
		while (listRef.hasNext() && listTest.hasNext()) {
			IssueReader refIssue = listRef.next();
			IssueReader testIssue = listTest.next();

			
			compareIssueReader(refIssue, testIssue);
		}

	}

	// private method for comparing two IssueReader objects
	private void compareIssueReader(IssueReader refIssue, IssueReader testIssue) {
		// check the IssueReaders are not null
		assertNotNull("internal tester error: null issue reader", refIssue);	
		assertNotNull("unexpected null issue reader", testIssue);

		// compare issue number and year
		assertEquals("wrong issue number ", refIssue.getNumber(), testIssue.getNumber());
		assertEquals("wrong issue year ", refIssue.getYear(), testIssue.getYear());

		// check the issue readers return the same articles
		Set<ArticleReader> rps = refIssue.getArticles();
		Set<ArticleReader> tps = testIssue.getArticles();
		
		List<Iterator<ArticleReader>> list = startComparison(rps, tps, " Articles ");
		if (list != null) {
			Iterator<ArticleReader> ri = list.get(0);
			Iterator<ArticleReader> ti = list.get(1);
			while (ri.hasNext() && ti.hasNext()) {
				ArticleReader rar = ri.next();
				ArticleReader tar = ti.next();
				compareTitleSubtitleAuhor(rar, tar, " articles ");
			}
		}

	}

	private void compareJournalStrings(JournalReader refJR, JournalReader testJR, String meaning) {
		compareString(refJR.getISSN(), testJR.getISSN(), meaning + " ISSN ");
		compareString(refJR.getPublisher(), testJR.getPublisher(), meaning + " publisher ");
		compareString(refJR.getTitle(), testJR.getTitle(), meaning + " title ");
	}
	
	
	class ItemReaderComparator implements Comparator<ItemReader> {
		public int compare(ItemReader f0, ItemReader f1) {
			if (f0 == f1)
				return 0;
			if (f0 == null)
				return -1;
			if (f1 == null)
				return 1;
			String title0 = f0.getTitle();
			String title1 = f1.getTitle();
			if (title0 == title1)
				return 0;
			if (title0 == null)
				return -1;
			if (title1 == null)
				return 1;
			return title0.compareTo(title1);
		}
	}

	class JournalReaderComparator implements Comparator<JournalReader> {
		public int compare(JournalReader f0, JournalReader f1) {
			if (f0 == f1)
				return 0;
			if (f0 == null)
				return -1;
			if (f1 == null)
				return 1;
			String issn0 = f0.getISSN();
			String issn1 = f1.getISSN();
			if (issn0 == issn1)
				return 0;
			if (issn0 == null)
				return -1;
			if (issn1 == null)
				return 1;
			return issn0.compareTo(issn1);
		}
	}

	class IssueReaderComparator implements Comparator<IssueReader> {
		public int compare(IssueReader f0, IssueReader f1) {
			if (f0 == f1)
				return 0;
			if (f0 == null)
				return -1;
			if (f1 == null)
				return 1;
			int numberDiff = f0.getNumber() - f1.getNumber();
			if (numberDiff != 0)
				return numberDiff;
			return f0.getYear() - f1.getYear();
		}
	}

	
	// method for comparing two strings arrays
	public static boolean compareStringArray(String[] rs, String[] ts, String meaning) {
		if (rs == ts)
			return true;

		if (rs == null || ts == null)
			return false;

		int n = rs.length;
		if (n != ts.length)
			return false;

		for (int i = 0; i < n; i++) {
			String r=rs[i];
			String t=ts[i];
			assertNotNull("internal tester error: null string in array of "+meaning, r);
			assertNotNull("unexpected null string in array of "+meaning, t);
			if (!r.equals(t))
				return false;
		}

		return true;
	}

	// method for comparing two strings that should be non-null
	public void compareString(String rs, String ts, String meaning) {
		assertNotNull("Unexpected null reference string "+meaning, rs);
		assertNotNull("null " + meaning, ts);
		assertEquals("wrong " + meaning, rs, ts);
	}

	/**
	 * Starts the comparison of two sets of elements that extend ItemReader. This
	 * method already makes some comparisons that are independent of the type (e.g.
	 * the sizes of the sets must match). Then the method arranges the set elements
	 * into ordered sets (TreeSet) and returns a pair of iterators that can be used
	 * later on for one-to-one matching of the set elements
	 * 
	 * @param rs   the first set to be compared
	 * @param ts   the second set to be compared
	 * @param type a string that specified the type of data in the set (will appear
	 *             in test messages)
	 * @return a list made of two iterators to be used for one-to-one comparisons of
	 *         the set elements
	 */
	public <T extends ItemReader> List<Iterator<T>> startComparison(Set<T> rs, Set<T> ts, String type) {
		// if one of the two sets is null while the other isn't null, the test fails
		if ((rs == null) && (ts != null) || (rs != null) && (ts == null)) {
			fail("returned set of " + type + " was null when it should be non-null or vice versa");
			return null;
		}

		// if both sets are null, there are no data to compare, and the test passes
		if ((rs == null) && (ts == null)) {
			assertTrue("there are no " + type + "!", true);
			return null;
		}

		// check that the number of elements matches
		assertEquals("wrong Number of " + type, rs.size(), ts.size());

		// create TreeSets of elements, using the comparator for sorting, one for
		// reference and one for implementation under test
		TreeSet<T> rts = new TreeSet<T>(new ItemReaderComparator());
		TreeSet<T> tts = new TreeSet<T>(new ItemReaderComparator());

		rts.addAll(rs);
		tts.addAll(ts);

		// get iterators and store them in a list
		List<Iterator<T>> list = new ArrayList<Iterator<T>>();
		list.add(rts.iterator());
		list.add(tts.iterator());

		// return the list
		return list;

	}

}
