package it.polito.dp2.BIB.sol2;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import it.polito.dp2.BIB.BibReader;
import it.polito.dp2.BIB.BibReaderException;
import it.polito.dp2.BIB.BibReaderFactory;
import it.polito.dp2.BIB.BookReader;
import it.polito.dp2.BIB.ItemReader;
import it.polito.dp2.BIB.JournalReader;
import it.polito.dp2.BIB.ass2.CitationFinder;
import it.polito.dp2.BIB.ass2.CitationFinderException;
import it.polito.dp2.BIB.ass2.ServiceException;
import it.polito.dp2.BIB.ass2.UnknownItemException;
import it.polito.dp2.rest.neo4j.client.jaxb.Node;
import it.polito.dp2.rest.neo4j.client.jaxb.Relationship;
import it.polito.dp2.rest.neo4j.client.jaxb.Traversal;

public class CitationFinderImpl implements CitationFinder {
	private BibReader monitor;
	private Map<ItemReader,Node> readerToNode;
	private Map<URL,ItemReader> urlToReader;
	private Neo4jClient client;

	
	public static void main(String[] args) throws CitationFinderException {
		System.setProperty("it.polito.dp2.BIB.BibReaderFactory", "it.polito.dp2.BIB.Random.BibReaderFactoryImpl");
		CitationFinderImpl cfi = new CitationFinderImpl();
	}
	
	public CitationFinderImpl() throws CitationFinderException {
		try {
			// Retrieve data from BibRandom
			BibReaderFactory factory = BibReaderFactory.newInstance();
			monitor = factory.newBibReader();
			readerToNode = new HashMap<ItemReader, Node>();
			urlToReader = new HashMap<URL, ItemReader>();
			// Create a new client that connects to a custom uri and a custom port (given)
			client = new Neo4jClient();
			// Create nodes
			Set<ItemReader> items = monitor.getItems(null, 0, 3000);
			for (ItemReader item : items) {
				Node node = client.createNode(item.getTitle());
				readerToNode.put(item, node);
				URL url = new URL(node.getSelf());
				urlToReader.put(url, item);
			}
			// Create relationships between citing and cited nodes
			for (ItemReader item : items) {
				String uri_from = readerToNode.get(item).getSelf();
				for (ItemReader cit : item.getCitingItems()){
					String uri_to = readerToNode.get(cit).getSelf();
					Relationship rel = client.createRelationship(uri_from, uri_to);
					URL url = new URL(rel.getSelf());
				}
			}
		} catch (Neo4jClientException | BibReaderException | MalformedURLException e) {
			throw new CitationFinderException(e);
		}
	}

	@Override
	public Set<ItemReader> findAllCitingItems(ItemReader item, int maxDepth) throws UnknownItemException, ServiceException {
		Set<ItemReader> items = new HashSet<ItemReader>();
		/*Search in the db for the citing items given a depth and from the list of nodes
		 * return a list of items
		 */
		try{
				if (maxDepth <= 0) maxDepth = 1; 
				String uri_from = readerToNode.get(item).getSelf();
				List<Node> nodes = client.createTraversal(maxDepth, uri_from);
				for(Node node : nodes){
					URL url = new URL(node.getSelf());
					ItemReader it = urlToReader.get(url);
					items.add(it);
				}
				return items;
				
		} catch (Neo4jClientException | MalformedURLException e) {
			throw new ServiceException(e);
		} catch (NullPointerException e){
			throw new UnknownItemException(e);
		}
	}

	@Override
	public BookReader getBook(String arg0) {
		return monitor.getBook(arg0);
	}

	@Override
	public Set<ItemReader> getItems(String arg0, int arg1, int arg2) {
		return monitor.getItems(arg0, arg1, arg2);
	}

	@Override
	public JournalReader getJournal(String arg0) {
		return monitor.getJournal(arg0);
	}

	@Override
	public Set<JournalReader> getJournals(String arg0) {
		return monitor.getJournals(arg0);
	}

}
