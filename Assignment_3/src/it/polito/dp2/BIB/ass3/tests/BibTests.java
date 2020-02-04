package it.polito.dp2.BIB.ass3.tests;

import org.junit.Before;
import org.junit.BeforeClass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;


import it.polito.dp2.BIB.*;
import it.polito.dp2.BIB.ass3.Bookshelf;
import it.polito.dp2.BIB.ass3.Client;
import it.polito.dp2.BIB.ass3.ClientFactory;
import it.polito.dp2.BIB.ass3.DestroyedBookshelfException;
import it.polito.dp2.BIB.ass3.ItemReader;
import it.polito.dp2.BIB.ass3.ServiceException;
import it.polito.dp2.BIB.ass3.TooManyItemsException;
import it.polito.dp2.BIB.ass3.UnknownItemException;
import it.polito.dp2.BIB.ass3.admClient.AdminClient;
import it.polito.dp2.BIB.ass3.admClient.BookType;
import it.polito.dp2.BIB.ass3.admClient.Items.Item;

public class BibTests {
	private static BibReader referenceBibReader;		// reference data generator
	private static Client testClient;	                // Client under test
	private static AdminClient adminClient;	            // AdminClient for populating items
	
	// prepare the environment
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Create reference data generator
		System.setProperty("it.polito.dp2.BIB.BibReaderFactory", "it.polito.dp2.BIB.Random.BibReaderFactoryImpl");
		referenceBibReader = BibReaderFactory.newInstance().newBibReader();
		
		try {
			testClient = ClientFactory.newInstance().newClient();
			adminClient = new AdminClient();
		} catch (FactoryConfigurationError fce) {
			fce.printStackTrace();
		}
		assertNotNull("Internal tester error during test setup: null reference", referenceBibReader);
		assertNotNull("Internal tester error during test setup: null reference", adminClient);
		assertNotNull("Could not run test: the implementation under test generated a null Client", testClient);
	}

	@Before
	public void setUp() throws Exception {
		assertNotNull("Internal tester error during test setup: null reference", referenceBibReader);
	}

	
	
	@Test
	// Check that bookshelves are created and destroyed correctly and that getBookshelfs returns consistent sets
	public final void testCreateBookshelf() throws ServiceException, DestroyedBookshelfException  {
		System.out.println("DEBUG: starting testCreateBookshelf");
		Bookshelf first = testClient.createBookshelf("Non-Fiction Science");
		Bookshelf second = testClient.createBookshelf("Non-Fiction History");
		Bookshelf toRemove = testClient.createBookshelf("Non-Fiction Math");
		
		//get bookshelves with the specified string
		Set<? extends Bookshelf> bookshelves = testClient.getBookshelfs("Non-Fiction");
		assertNotNull("The client under test returned a null set", bookshelves);
		assertEquals("Wrong number of bookshelves ",3,bookshelves.size());
		
		toRemove.destroyBookshelf();
		bookshelves = testClient.getBookshelfs("Non-Fiction");
		assertNotNull("The client under test returned a null set", bookshelves);
		assertEquals("Wrong number of bookshelves ",2,bookshelves.size());
		
		first.destroyBookshelf();
		second.destroyBookshelf();
		
		bookshelves = testClient.getBookshelfs("Non-Fiction");
		assertNotNull("The client under test returned a null set", bookshelves);
		assertEquals("Wrong number of bookshelves ",0,bookshelves.size());
	}
	
	@Test
	// Check that removeItem() correctly manages the removal of an item from the bookshelf
	public final void testGetItems() throws ServiceException, DestroyedBookshelfException, UnknownItemException  {
		System.out.println("DEBUG: starting testGetItems");
		Set<ItemReader> items = testClient.getItems("", 0, 3000);
		assertNotNull("The getItems of the implementation under test generated a null set of ItemReader", items);
		
		Set<it.polito.dp2.BIB.ItemReader> ref_items = referenceBibReader.getItems("", 0, 3000);
		
		assertEquals("getItems returned a wrong number of items ",ref_items.size(),items.size());
	}
	
	@Test
	// Check that removeItem() correctly manages the removal of an item from the bookshelf
	public final void testBookShelfOperations() throws ServiceException, DestroyedBookshelfException, UnknownItemException, TooManyItemsException  {
		System.out.println("DEBUG: starting testBookShelfOperations");
		String testBookShelf="Biography";
		
		Bookshelf bookshelf = testClient.createBookshelf(testBookShelf);
		assertNotNull("The createBookshelf of the client under test generated a null Bookshelf", bookshelf);
			
		int numberOfBooksInBookShelf = addItemsBookshelf(testBookShelf, bookshelf);
		System.out.println("DEBUG: testBookShelfOperations() "+numberOfBooksInBookShelf+" number of new items added to the "+testBookShelf);
		
		if (numberOfBooksInBookShelf>0) {
			// remove first item
			Set<ItemReader> items = bookshelf.getItems();
			assertNotNull("The getItems of the implementation under test generated a null set of ItemReader", items);
			ItemReader firstItem = bookshelf.getItems().iterator().next();
			assertNotNull("The getItems of the implementation under test returned a null ItemReader", firstItem);
			bookshelf.removeItem(firstItem);
			numberOfBooksInBookShelf--;
			Set<ItemReader> intemsAfterRemove = bookshelf.getItems();
			assertNotNull("The getItems of the implementation under test generated a null set of ItemReader", intemsAfterRemove);
			int actual = intemsAfterRemove.size();
			
			// check that the number of items in the bookshelf matches after removing an item
			assertEquals("Wrong number of books in the bookshelf "+testBookShelf +" after removing an item ",numberOfBooksInBookShelf,actual);		
		}
		
		bookshelf.destroyBookshelf();
	}

	// add all book items to the current bookshelf up to limit and check number of items
	private int addItemsBookshelf(String testBookShelf, Bookshelf bookshelf)
			throws ServiceException, DestroyedBookshelfException, UnknownItemException, TooManyItemsException {
		Set<ItemReader> items = testClient.getItems("", 0, 3000);
		
		
		TreeSet<ItemReader> sortedItems = new TreeSet<ItemReader>(new ItemReaderComparator());
		sortedItems.addAll(items);
		
		// limit the number of items to be added to the bookshelf
		int limit = 20;
		assertNotNull("The getItems of the implementation under test generated a null set of ItemReader", items);
				
		int numberOfBooksInBookShelf=0;
		for (ItemReader item: sortedItems) {
			if(numberOfBooksInBookShelf>=limit) break;
				bookshelf.addItem(item);
				numberOfBooksInBookShelf++;
		}
		
		Set<ItemReader> itemsAfterAdd = bookshelf.getItems();
		assertNotNull("The getItems of the implementation under test generated a null set of ItemReader", itemsAfterAdd);
		int actual = itemsAfterAdd.size();
		
		// check the number of items in bookshelf has been increased as expected
		assertEquals("Wrong number of books in the bookshelf "+testBookShelf +" after adding "+ numberOfBooksInBookShelf +" items ",numberOfBooksInBookShelf,actual);
		return numberOfBooksInBookShelf;
	}
	
	@Test 
	// Check that the removal of an item from the database correctly removes it from all bookshelves
	public final void testRemoveItemFromBibliography() throws ServiceException, DestroyedBookshelfException, UnknownItemException, DatatypeConfigurationException, TooManyItemsException {
		System.out.println("DEBUG: starting testRemoveItemFromBibliography");
		// create three different bookshelves
		Bookshelf first = testClient.createBookshelf("Non-Fiction Science");
		Bookshelf second = testClient.createBookshelf("Non-Fiction History");
		Bookshelf third = testClient.createBookshelf("Non-Fiction Math");
		
		// upload a new book item to the server and update the item with returned data
		Item item = buildItem();
		item = adminClient.createItem(item);
		
		Set<ItemReader> addedItemSet = testClient.getItems("The Way of Z", 1990, 1999);
		assertNotNull("The getItems of the implementation under test generated a null set of added ItemReader", addedItemSet);
		assertTrue("The getItems of the implementation under test generated an empty set of ItemReader", addedItemSet.iterator().hasNext());
		
		
		// retrieve the only (expected) item from the set   
		ItemReader addedItem = addedItemSet.iterator().next();
		
		// add the new book item to all bookshelves
		first.addItem(addedItem);
		second.addItem(addedItem);
		third.addItem(addedItem);
		
		Set<ItemReader> firstItems = first.getItems();
		Set<ItemReader> secondItems = second.getItems();
		Set<ItemReader> thirdItems = third.getItems();
		
		assertNotNull("The getItems of the implementation under test generated a null set of ItemReader", firstItems);
		assertNotNull("The getItems of the implementation under test generated a null set of ItemReader", secondItems);
		assertNotNull("The getItems of the implementation under test generated a null set of ItemReader", thirdItems);
		
		// check that the number of items in the bookshelves equal to one after adding an item
		assertEquals("Wrong number of books in the bookshelf Non-Fiction Science",1,first.getItems().size());
		assertEquals("Wrong number of books in the bookshelf Non-Fiction History",1,second.getItems().size());
		assertEquals("Wrong number of books in the bookshelf Non-Fiction Math",1,third.getItems().size());
		
		// delete the new added book item from the server
		adminClient.removeItem(item);

		assertNotNull("The getItems of the implementation under test generated a null set of ItemReader", firstItems);
		assertNotNull("The getItems of the implementation under test generated a null set of ItemReader", secondItems);
		assertNotNull("The getItems of the implementation under test generated a null set of ItemReader", thirdItems);

		// check that the number of items in the bookshelves equal to 0 after removing the added item
		assertEquals("Wrong number of books in the bookshelf Non-Fiction Science",0,first.getItems().size());
		assertEquals("Wrong number of books in the bookshelf Non-Fiction History",0,second.getItems().size());
		assertEquals("Wrong number of books in the bookshelf Non-Fiction Math",0,third.getItems().size());
		
		first.destroyBookshelf();
		second.destroyBookshelf();
		third.destroyBookshelf();
		
	}
	
	// generate a new book item
	private Item buildItem() {
		Item itemToSend = new Item();
		itemToSend.setTitle("The Way of Z");
		itemToSend.setSubtitle("Practica1 Programming with Formal Methods");
		itemToSend.getAuthor().add("J. Yusupov");
		BookType book = new BookType();
		book.setISBN("0131411553");
		book.setPublisher("Addison Wesley");
		itemToSend.setBook(book);
		try {
			XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
			calendar.setYear(1996);
			book.setYear(calendar);
		} catch (DatatypeConfigurationException e) {
			assertTrue("Unexpected internal error",false);
		}
		return itemToSend;
	}

	@Test
	// Check that the getItems() method of the bookshelf correctly updates the number of reads
	public final void testNumberOfReads() throws ServiceException, DestroyedBookshelfException, UnknownItemException, TooManyItemsException{
		System.out.println("DEBUG: starting testNumberOfReads");
		String testBookShelf="Phantasy";
		
		Bookshelf bookshelf = testClient.createBookshelf(testBookShelf);
		assertNotNull("The createBookshelf of the implementation under testNumberOfReads generated a null Bookshelf", bookshelf);
		
		addItemsBookshelf(testBookShelf, bookshelf);
		int beforeNumberOfReads = bookshelf.getNumberOfReads();
		
		// reading contents of the bookshelf must increment the number of reads
		bookshelf.getItems();
		int afterNumberOfReads = bookshelf.getNumberOfReads();
		
		assertTrue("Wrong number of reads in the bookshelf "+testBookShelf ,beforeNumberOfReads<afterNumberOfReads);
		
		bookshelf.destroyBookshelf();
	}
		
	
	@Test(expected=DestroyedBookshelfException.class)
	//Check if adding an item to the destroyed bookshelf correctly managed
	public final void testNonExistingBookshelf() throws ServiceException, DestroyedBookshelfException, UnknownItemException, TooManyItemsException{
		System.out.println("DEBUG: starting testNonExistingBookshelf");
		// create a new bookshelf and destroy it
		String testBookShelf = "Memoir";
		Bookshelf bookshelf = testClient.createBookshelf(testBookShelf);
		assertNotNull("The createBookshelf of the implementation under testNumberOfReads generated a null Bookshelf", bookshelf);

		bookshelf.destroyBookshelf();
		
		// retrieve items from the server in order to add to the bookshelf
		Set<ItemReader> items = testClient.getItems("", 0, 3000);
		assertTrue("Server did not return any item to add " , items!=null && !items.isEmpty());
		
		// check that addItem() method of the destroyed bookshelf throws the expected exception
		for (ItemReader itemReader : items) {
			bookshelf.addItem(itemReader);
		}
		
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

	

}
