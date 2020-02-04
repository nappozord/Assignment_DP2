package it.polito.dp2.BIB.sol3.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.core.UriInfo;

import it.polito.dp2.BIB.ass3.Bookshelf;
import it.polito.dp2.BIB.sol3.client.BookshelfImpl;
import it.polito.dp2.BIB.sol3.db.BadRequestInOperationException;
import it.polito.dp2.BIB.sol3.db.ConflictInOperationException;
import it.polito.dp2.BIB.sol3.db.DB;
import it.polito.dp2.BIB.sol3.db.ItemPage;
import it.polito.dp2.BIB.sol3.db.Neo4jDB;
import it.polito.dp2.BIB.sol3.service.jaxb.BookshelfType;
import it.polito.dp2.BIB.sol3.service.jaxb.Bookshelves;
import it.polito.dp2.BIB.sol3.service.jaxb.Citation;
import it.polito.dp2.BIB.sol3.service.jaxb.Item;
import it.polito.dp2.BIB.sol3.service.jaxb.Items;
import it.polito.dp2.BIB.sol3.service.jaxb.NRead;
import it.polito.dp2.BIB.sol3.service.util.ResourseUtils;

public class BiblioService {
	private DB n4jDb = Neo4jDB.getNeo4jDB();
	ResourseUtils rutil;
	UriInfo uriInfo;

	private static class SingletonClass {
	    private static SingletonClass SINGLE_INSTANCE = null;
	    public Bookshelves bs;
	    private SingletonClass() {
	    	bs = new Bookshelves();
	    }
	    public static SingletonClass getInstance() {
	        if (SINGLE_INSTANCE == null) {
	            synchronized (SingletonClass.class) {
	                if (SINGLE_INSTANCE == null) {
	                    SINGLE_INSTANCE = new SingletonClass();
	                }
	            }
	        }
	        return SINGLE_INSTANCE;
	    }
	}

	public BiblioService(UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		rutil = new ResourseUtils((uriInfo.getBaseUriBuilder()));
	}
	
	public Items getItems(SearchScope scope, String keyword, int beforeInclusive, int afterInclusive, BigInteger page) throws Exception {
		ItemPage itemPage = n4jDb.getItems(scope,keyword,beforeInclusive,afterInclusive,page);

		Items items = new Items();
		List<Item> list = items.getItem();
		
		Set<Entry<BigInteger,Item>> set = itemPage.getMap().entrySet();
		for(Entry<BigInteger,Item> entry:set) {
			Item item = entry.getValue();
			rutil.completeItem(item, entry.getKey());
			list.add(item);
		}
		items.setTotalPages(itemPage.getTotalPages());
		items.setPage(page);
		return items;
	}

	public Item getItem(BigInteger id) throws Exception {
			Item item = n4jDb.getItem(id);
			if (item!=null)
				rutil.completeItem(item, id);
			return item;
	}

	public Item updateItem(BigInteger id, Item item) throws Exception {
		Item ret = n4jDb.updateItem(id, item);
		if (ret!=null) {
			rutil.completeItem(item, id);
			return item;
		} else
			return null;
	}

	public Item createItem(Item item) throws Exception {
		BigInteger id = n4jDb.createItem(item);
		if (id==null)
			throw new Exception("Null id");
		rutil.completeItem(item, id);
		return item;
	}

	public BigInteger deleteItem(BigInteger id) throws ConflictServiceException, Exception {
		try {
			
			SingletonClass sc = SingletonClass.getInstance();
			for(BookshelfType b : sc.bs.getBookshelfType()){
				this.deleteItemBookshelf(b.getName(), id.toString());
			}
			
			return n4jDb.deleteItem(id);
		} catch (ConflictInOperationException e) {
			throw new ConflictServiceException();
		}
	}

	public Citation createItemCitation(BigInteger id, BigInteger tid, Citation citation) throws Exception {
		try {
			return n4jDb.createItemCitation(id, tid, citation);
		} catch (BadRequestInOperationException e) {
			throw new BadRequestServiceException();
		}
	}

	public Citation getItemCitation(BigInteger id, BigInteger tid) throws Exception {
		Citation citation = n4jDb.getItemCitation(id,tid);
		if (citation!=null)
			rutil.completeCitation(citation, id, tid);
		return citation;
	}

	public boolean deleteItemCitation(BigInteger id, BigInteger tid) throws Exception {
		return n4jDb.deleteItemCitation(id, tid);
	}

	public Items getItemCitations(BigInteger id) throws Exception {
		ItemPage itemPage = n4jDb.getItemCitations(id, BigInteger.ONE);
		if (itemPage==null)
			return null;

		Items items = new Items();
		List<Item> list = items.getItem();
		
		Set<Entry<BigInteger,Item>> set = itemPage.getMap().entrySet();
		for(Entry<BigInteger,Item> entry:set) {
			Item item = entry.getValue();
			rutil.completeItem(item, entry.getKey());
			list.add(item);
		}
		items.setTotalPages(itemPage.getTotalPages());
		items.setPage(BigInteger.ONE);
		return items;
	}

	public Items getItemCitedBy(BigInteger id) throws Exception {
		ItemPage itemPage = n4jDb.getItemCitedBy(id, BigInteger.ONE);
		if (itemPage==null)
			return null;

		Items items = new Items();
		List<Item> list = items.getItem();
		
		Set<Entry<BigInteger,Item>> set = itemPage.getMap().entrySet();
		for(Entry<BigInteger,Item> entry:set) {
			Item item = entry.getValue();
			rutil.completeItem(item, entry.getKey());
			list.add(item);
		}
		items.setTotalPages(itemPage.getTotalPages());
		items.setPage(BigInteger.ONE);
		return items;
	}

	public synchronized BookshelfType createBookshelf(BookshelfType bookshelf) throws ConflictServiceException {
		// TODO Auto-generated method stub
		SingletonClass sc = SingletonClass.getInstance();
		for(BookshelfType b : sc.bs.getBookshelfType()){
			if(b.getName().equals(bookshelf.getName()))
				throw new ConflictServiceException();
		}
		sc.bs.getBookshelfType().add(bookshelf);
		return bookshelf;
	}

	public synchronized Bookshelves getBookshelves(String keyword) {
		// TODO Auto-generated method stub
		SingletonClass sc = SingletonClass.getInstance();
		for(BookshelfType b : sc.bs.getBookshelfType()){
			if(b.getName().contains(keyword)){
				NRead n = b.getNRead();
				n.setNRead(n.getNRead().add(new BigInteger("1")));
				b.setNRead(n);
			}
		}
		return sc.bs;
	}

	public synchronized int deleteBookshelf(String name) {
		// TODO Auto-generated method stub
		SingletonClass sc = SingletonClass.getInstance();
		for(BookshelfType b : sc.bs.getBookshelfType()){
			if(b.getName().equals(name)){
				sc.bs.getBookshelfType().remove(b);
				return 0;
			}
		}
		return 1;
	}

	public synchronized BookshelfType insertBook(String name, Item book) {
		// TODO Auto-generated method stub
		SingletonClass sc = SingletonClass.getInstance();
		for(BookshelfType b : sc.bs.getBookshelfType()){
			if(b.getName().equals(name) && !b.getItems().contains(book.getSelf())){
				b.getItems().add(book.getSelf());
				return b;
			}
		}
		return null;
	}

	public synchronized int deleteItemBookshelf(String name, String id) {
		// TODO Auto-generated method stub
		SingletonClass sc = SingletonClass.getInstance();
		for(BookshelfType b : sc.bs.getBookshelfType()){
			if(b.getName().equals(name)){
				for(String self : b.getItems()){
					String trim = self.replace(uriInfo.getBaseUriBuilder().path("biblio/items/").toString(), "");
					if(trim.equals(id)){
						b.getItems().remove(self);
						return 0;
					}
				}
			}
		}
		return 1;
	}

	public synchronized BookshelfType getBookshelf(String name) {
		// TODO Auto-generated method stub
		SingletonClass sc = SingletonClass.getInstance();
		for(BookshelfType b : sc.bs.getBookshelfType()){
			if(b.getName().equals(name)){
				NRead n = b.getNRead();
				n.setNRead(n.getNRead().add(new BigInteger("1")));
				b.setNRead(n);
				return b;
			}
		}
		return null;
	}

	public synchronized NRead getNRead(String name) {
		// TODO Auto-generated method stub
		SingletonClass sc = SingletonClass.getInstance();
		for(BookshelfType b : sc.bs.getBookshelfType()){
			if(b.getName().equals(name)){
				return b.getNRead();
			}
		}
		return null;
	}

}
