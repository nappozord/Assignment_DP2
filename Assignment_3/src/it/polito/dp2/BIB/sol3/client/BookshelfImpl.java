package it.polito.dp2.BIB.sol3.client;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.xml.sax.SAXParseException;

import com.sun.jersey.api.client.ClientResponse.Status;

import it.polito.dp2.BIB.ass3.Bookshelf;
import it.polito.dp2.BIB.ass3.DestroyedBookshelfException;
import it.polito.dp2.BIB.ass3.ItemReader;
import it.polito.dp2.BIB.ass3.ServiceException;
import it.polito.dp2.BIB.ass3.UnknownItemException;
import it.polito.dp2.BIB.sol3.service.jaxb.BookshelfType;
import it.polito.dp2.BIB.sol3.service.jaxb.Bookshelves;
import it.polito.dp2.BIB.sol3.service.jaxb.NRead;

public class BookshelfImpl implements Bookshelf {
	private BookshelfType b;
	private ClientFactoryImpl client;

	public BookshelfImpl(BookshelfType b, ClientFactoryImpl client) {
		super();
		this.b = b;
		this.client = client;
	}
	
	public BookshelfImpl(BookshelfType b) {
		super();
		this.b = b;
	}

	@Override
	public String getName() throws DestroyedBookshelfException {
		return b.getName();
	}

	@Override
	public void addItem(ItemReader item) throws DestroyedBookshelfException, UnknownItemException, ServiceException {
		// TODO Auto-generated method stub
		Items items = client.target.path("/items")
				.queryParam("keyword", item.getTitle())
				.queryParam("beforeInclusive", 10000)
				.queryParam("afterInclusive", 0)
			 	  .request(MediaType.APPLICATION_JSON_TYPE)
			 	  .get(Items.class);
		
		for(Items.Item i : items.getItem()) {
			if(i.getAuthor().size() == item.getAuthors().length &&  item.getTitle().equals(i.getTitle())){
				if(i.getArticle() == null){
					BookType book = new BookType();
					book.setISBN("0000000000000");
					GregorianCalendar c = new GregorianCalendar();
			        Date dob = null;
			        DateFormat df = new SimpleDateFormat("yyyy");
			        try {
						dob = df.parse("2000");
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        c.setTimeInMillis(dob.getTime());
			        try {
						XMLGregorianCalendar xmldate = DatatypeFactory.newInstance().newXMLGregorianCalendar(2000, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED,  DatatypeConstants.FIELD_UNDEFINED,  DatatypeConstants.FIELD_UNDEFINED,  DatatypeConstants.FIELD_UNDEFINED,  DatatypeConstants.FIELD_UNDEFINED);
						book.setYear(xmldate);
					} catch (DatatypeConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					book.setPublisher("N");         
					i.setBook(book);
				}
				
				Response bsdf = client.target.path("/bookshelves/" + b.getName())
					.request(MediaType.APPLICATION_JSON_TYPE)
					.post(Entity.json(i));
				if (bsdf.getStatus() == 404){
					throw new DestroyedBookshelfException();
				}	
				return;
			}
		}
	}

	@Override
	public void removeItem(ItemReader item) throws DestroyedBookshelfException, UnknownItemException, ServiceException {
		// TODO Auto-generated method stub
		Items items = client.target.path("/items")
				.queryParam("keyword", item.getTitle())
				.queryParam("beforeInclusive", 10000)
				.queryParam("afterInclusive", 0)
			 	  .request(MediaType.APPLICATION_JSON_TYPE)
			 	  .get(Items.class);
		
		for(Items.Item i : items.getItem()) {
			if(i.getAuthor().size() == item.getAuthors().length &&  item.getTitle().equals(i.getTitle())){
				String trim = i.getSelf().replace(client.target.path("/items/").getUri().toString(), "");
				client.target.path("/bookshelves/" + b.getName() + "/" + trim)
					.request(MediaType.APPLICATION_JSON_TYPE)
					.delete();
			}
			return;
		}
	}

	@Override
	public Set<ItemReader> getItems() throws DestroyedBookshelfException, ServiceException {
		// TODO Auto-generated method stub
		Set<ItemReader> items = new HashSet<ItemReader>();

		BookshelfType bs = client.target.path("/bookshelves/" + b.getName())
			 	  .request(MediaType.APPLICATION_JSON_TYPE)
			 	  .get(BookshelfType.class);
		
		b = bs;
		
		for(String itemUri : b.getItems()){
			String x = client.target.path("/items/").getUri().toString();
			String trim = itemUri.replace(client.target.path("/items/").getUri().toString(), "");
			it.polito.dp2.BIB.sol3.client.Items.Item item = client.target.path("/items/" + trim)
					.request(MediaType.APPLICATION_JSON_TYPE)
					.get(it.polito.dp2.BIB.sol3.client.Items.Item.class);
			ItemReader i = new ItemReaderImpl(item);
			items.add(i);
		}
		
		return items;
	}

	@Override
	public void destroyBookshelf() throws DestroyedBookshelfException, ServiceException {
		// TODO Auto-generated method stub
		client.target.path("/bookshelves/" + b.getName())
		.request(MediaType.APPLICATION_JSON_TYPE)
		.delete();
	}

	@Override
	public int getNumberOfReads() throws DestroyedBookshelfException {
		// TODO Auto-generated method stub
		NRead i = client.target.path("/bookshelves/" + b.getName() + "/n_read")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.get(NRead.class);
		return i.getNRead().intValue();
	}

}
