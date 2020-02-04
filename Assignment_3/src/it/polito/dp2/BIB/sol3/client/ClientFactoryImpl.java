package it.polito.dp2.BIB.sol3.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Before;
import org.junit.BeforeClass;

import it.polito.dp2.BIB.BibReader;
import it.polito.dp2.BIB.BibReaderFactory;
import it.polito.dp2.BIB.FactoryConfigurationError;
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
import it.polito.dp2.BIB.ass3.tests.BibTests;
//import it.polito.dp2.BIB.ass3.tests.BibTests.ItemReaderComparator;
import it.polito.dp2.BIB.sol3.service.jaxb.Bookshelves;
import it.polito.dp2.BIB.sol3.service.jaxb.NRead;
import it.polito.dp2.BIB.sol3.service.jaxb.BookshelfType;

public class ClientFactoryImpl implements Client {
	javax.ws.rs.client.Client client;
	WebTarget target;
	static String uri = "http://localhost:8080/BiblioSystem/rest";
	static String urlProperty = "it.polito.dp2.BIB.ass3.URL";
	static String portProperty = "it.polito.dp2.BIB.ass3.PORT";
	//Set<Bookshelf> bookshelves;
	
	public ClientFactoryImpl(URI uri) {
		this.uri = uri.toString();
		
		client = ClientBuilder.newClient();
		target = client.target(uri).path("biblio");
	}
	
	

	@Override
	public Bookshelf createBookshelf(String name) throws ServiceException {
		// TODO Auto-generated method stub
		BookshelfType b = new BookshelfType();
		b.setName(name);
		NRead n = new NRead();
		n.setNRead(new BigInteger("0"));
		b.setNRead(n);
		b.setSelf(target.getUri().toString() + "/bookshelves/" + name);
		BookshelfType bt = target.path("/bookshelves")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.json(b), BookshelfType.class);
		Bookshelf bs = new BookshelfImpl(bt, this);
		return bs;
	}

	@Override
	public Set<Bookshelf> getBookshelfs(String name) throws ServiceException {
		// TODO Auto-generated method stub
		Bookshelves bs = target.path("/bookshelves")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.get(Bookshelves.class);
		
		Set<Bookshelf> set = new HashSet<Bookshelf>();
		
		for(BookshelfType bt : bs.getBookshelfType()){
			BookshelfImpl bimpl = new BookshelfImpl(bt);
			set.add(bimpl);
		}
		
		return set;
	}

	@Override
	public Set<ItemReader> getItems(String keyword, int since, int to) throws ServiceException {
		Set<ItemReader> itemSet=new HashSet<>();
		Items items = target.path("/items")
				.queryParam("keyword", keyword)
				.queryParam("beforeInclusive", to)
				.queryParam("afterInclusive", since)
			 	  .request(MediaType.APPLICATION_JSON_TYPE)
			 	  .get( Items.class);
		
		for (it.polito.dp2.BIB.sol3.client.Items.Item i : items.getItem()) {
			itemSet.add(new ItemReaderImpl(i));
		}
		
		return itemSet;
	}
	
	private static void printItems() throws ServiceException {
		Set<ItemReader> set = mainClient.getItems("", 0, 3000);
		System.out.println("Items returned: "+set.size());
		
		// For each Item print related data
		for (ItemReader item: set) {
			System.out.println("Title: "+item.getTitle());
			if (item.getSubtitle()!=null)
				System.out.println("Subtitle: "+item.getSubtitle());
			System.out.print("Authors: ");
			String[] authors = item.getAuthors();
			System.out.print(authors[0]);
			for (int i=1; i<authors.length; i++)
				System.out.print(", "+authors[i]);
			System.out.println(";");
			
			Set<ItemReader> citingItems = item.getCitingItems();
			System.out.println("Cited by "+citingItems.size()+" items:");
			for (ItemReader citing: citingItems) {
				System.out.println("- "+citing.getTitle());
			}	
			printLine('-');

		}
		printBlankLine();
	}

	private static void printBlankLine() {
		System.out.println(" ");
	}

	
	private static void printLine(char c) {
		System.out.println(makeLine(c));
	}
	
	private static StringBuffer makeLine(char c) {
		StringBuffer line = new StringBuffer(132);
		
		for (int i = 0; i < 132; ++i) {
			line.append(c);
		}
		return line;
	}
	
	static ClientFactoryImpl mainClient;
	public static void main(String[] args) {
		
		String customUri = "http://localhost:8080/BiblioSystem/rest";
		if (customUri != null)
			uri = customUri;
		
		try {
			mainClient = new ClientFactoryImpl(new URI(uri));
			printItems();
		} catch (URISyntaxException | ServiceException e) {
			e.printStackTrace();
		}
	}
		
}
