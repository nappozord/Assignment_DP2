package it.polito.dp2.BIB.sol2;

import javax.ws.rs.client.Client;

import it.polito.dp2.rest.gbooks.client.Factory;
import it.polito.dp2.rest.gbooks.client.MyErrorHandler;
import it.polito.dp2.rest.gbooks.client.jaxb.*;
import it.polito.dp2.xml.biblio.PrintableItem;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;


public class BookClient_e {
	
	JAXBContext jc;
	javax.xml.validation.Validator validator;

	public static void main(String[] args) {
		
		if (args.length == 0) {
	          System.err.println("Usage: java BookClient keyword1 keyword2 ...");
	          System.exit(1);
	    }
		try{
			BookClient_e bclient = new BookClient_e();
			// First search with Google APIs...
			bclient.PerformSearchOnGoogleAPI(args);
			// ...then search with Crossref APIs
			bclient.PerformSearchOnCrossrefAPI(args);
		}catch(Exception ex ){
			System.err.println("Error during execution of operation");
			ex.printStackTrace(System.out);
		}
	}

	public BookClient_e() throws Exception {        
    	// create validator that uses the DataTypes schema
    	SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
    	Schema schema = sf.newSchema(new File("xsd/gbooks/DataTypes.xsd"));
    	validator = schema.newValidator();
    	validator.setErrorHandler(new MyErrorHandler());
    	
		// Create JAXB context related to the classed generated from the DataTypes schema
        jc = JAXBContext.newInstance("it.polito.dp2.rest.gbooks.client.jaxb");
	}
	
	private void PerformSearchOnCrossrefAPI(String[] kw) {
		int tot = Integer.parseInt(kw[1]);
		List<PrintableItem> pitems = new ArrayList<PrintableItem>();
		
		// Create client for Crossref
		Client client = ClientBuilder.newClient();
		
		WebTarget target = client.target(getBaseURICrossrefAPI()).path("works");
		
		/*Used to keep on searching and "scroll" through the list of items
		 * the Crossref apis give each time until there are enough valid items
		 */
		String indexer = "*";
		
		StringBuffer queryString = new StringBuffer(/*kw[0]*/);
		for (int i=2; i<kw.length; i++) {
			queryString.append(' ');
			queryString.append(kw[i]);
		}
		
		System.out.println("Searching " + queryString + " on Crossref:");
		
		// Go on until there are enough valid items
		while(pitems.size() < tot){
			Response response = target
								   .queryParam("query", queryString)
								   .queryParam("cursor", indexer)
								   .request()
								   .accept(MediaType.APPLICATION_JSON)
								   .get();
			if (response.getStatus()!=200) {
				System.out.println("Error in remote operation: "+response.getStatus()+" "+response.getStatusInfo());
				return;
			}
			response.bufferEntity();
			//System.out.println("Response as string: "+response.readEntity(String.class));
			SearchResultForCrossref result = response.readEntity(SearchResultForCrossref.class);
			
			//System.out.println("OK Response received. Items:"+result.getMessage().getTotalResults());
			//System.out.println("Validating items and converting validated items to xml.");
			
			// Validate items against the schema and create printable items
			pitems.addAll(validateItems(tot - pitems.size(), createItem(result.getMessage().getItems()), pitems.size()));
			indexer = result.getMessage().getNextCursor();
		}
		
		System.out.println("Validated Bibliography items by CrossrefAPIs: " + pitems.size());
	    for (PrintableItem item:pitems)
	    	item.print();
	    System.out.println("End of Validated Bibliography items by CrossrefAPIs");
		
	}
	
	/*Adjust the Crossref output items so it can be used as a parameter
	 * in the CreatePrintableItem method
	 */
	private List<Items> createItem(List<VolumeInfo> items) {
		List<Items> itemz = new ArrayList<Items>();
		
		for (VolumeInfo item: items){
			Items i = new Items();
			if(item.getAuthor().size() != 0){
				for(AuthorType a: item.getAuthor())
					item.getAuthors().add(a.getFamily() + " " + a.getGiven());
				item.getAuthor().clear();
			}
			if(item.getIssued().getDateParts().get(0) != null){
				DateFormat format = new SimpleDateFormat("yyyy");
				String dateStr = item.getIssued().getDateParts().get(0).toString().substring(0, 4);
				Date date;
				try {
					date = format.parse(dateStr);
					GregorianCalendar greg = new GregorianCalendar();
					greg.setTime(date);
					XMLGregorianCalendar c;
					try {
						c = DatatypeFactory.newInstance()
						        .newXMLGregorianCalendar(
						                greg);
						item.setPublishedDate(c);
						item.setIssued(null);;
					} catch (DatatypeConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(item.getISBN() != null){
				for(String isbn : item.getISBN()){
					IndustryIdentifier e = new IndustryIdentifier();
					e.setIdentifier(isbn);
					if (isbn.length() == 13)
						e.setType("ISBN_13");
					else e.setType("ISBN_9");
					item.getIndustryIdentifiers().add(e);
				}
				item.getISBN().clear();
			}
			if(item.getURL() != null){
				i.setSelfLink(item.getURL());
				item.setURL(null);
			}
			i.setVolumeInfo(item);
			itemz.add(i);
		}
		
		return itemz;
	}

	public void PerformSearchOnGoogleAPI(String[] kw){
		int tot = Integer.parseInt(kw[1]);
		List<PrintableItem> pitems = new ArrayList<PrintableItem>();
		
		// build the JAX-RS client object 
		Client client = ClientBuilder.newClient();
		
		// build the web target
		WebTarget target = client.target(getBaseURIGoogleAPI()).path("volumes");
		
		// perform a get request using mediaType=APPLICATION_JSON
		// and convert the response into a SearchResult object
		StringBuffer queryString = new StringBuffer(/*kw[0]*/);
		for (int i=2; i<kw.length; i++) {
			queryString.append(' ');
			queryString.append(kw[i]);
		}
		
		System.out.println("Searching " + queryString + " on Google Books:");
		
		int indexer = 0;
		
		while(pitems.size() < tot){
			
			Response response = target
								   .queryParam("q", queryString)
								   .queryParam("start-index",  + indexer)
								   .queryParam("printType", "books")
								   .request()
								   .accept(MediaType.APPLICATION_JSON)
								   .get();
			if (response.getStatus()!=200) {
				System.out.println("Error in remote operation: "+response.getStatus()+" "+response.getStatusInfo());
				return;
			}
			response.bufferEntity();
			//System.out.println("Response as string: "+response.readEntity(String.class));
			SearchResult result = response.readEntity(SearchResult.class);
			
			//System.out.println("OK Response received. Items:"+result.getTotalItems());
			//System.out.println("Validating items and converting validated items to xml.");
			
			//Validate items
			pitems.addAll(validateItems(tot - pitems.size(), result.getItems(), pitems.size()));
			indexer += tot;
		}
		
	    System.out.println("Validated Bibliography items by GoogleAPIs: " + pitems.size());
	    for (PrintableItem item:pitems)
	    	item.print();
	    System.out.println("End of Validated Bibliography items by GoogleAPIs");
	}
	
	private List<PrintableItem> validateItems(int tot, List<Items> items, int add) {

		// TODO Auto-generated method stub
		// create empty list
		List<PrintableItem> pitems = new ArrayList<PrintableItem>();
		
		int i=0;
		for (Items item:items) {
			if (i >= tot) break;
			try {
				// validate item
		    	JAXBSource source = new JAXBSource(jc, item);
		    	//System.out.println("Validating...");
		    	validator.validate(source);
		    	//System.out.println("Validation OK");
		    	// add item to list
				//System.out.println("Adding item to list");
		    	i++;
				pitems.add(Factory.createPrintableItem(BigInteger.valueOf(add+i),item.getVolumeInfo()));
			} catch (org.xml.sax.SAXException se) {
			      //System.out.println("Validation Failed");
			      // print error messages
			      Throwable t = se;
			      while (t!=null) {
				      String message = t.getMessage();
				      //if (message!= null)
				    	  //System.out.println(message);
				      t = t.getCause();
			      }
			} catch (IOException e) {
				System.out.println("Unexpected I/O Exception");
			} catch (JAXBException e) {
				System.out.println("Unexpected JAXB Exception");
			}
		}
		return pitems;
	}

	private static URI getBaseURIGoogleAPI() {
	    return UriBuilder.fromUri("https://www.googleapis.com/books/v1").build();
	}
	
	private static URI getBaseURICrossrefAPI(){
		return UriBuilder.fromUri("https://api.crossref.org").build();
	}

}
