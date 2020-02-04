package it.polito.dp2.BIB.sol3.model;

import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import it.polito.dp2.BIB.sol3.service.jaxb.Biblio;

@XmlType(name = "", propOrder = {
	    "items",
	    "journals",
	    "articles",
	    "books",
	    "self"
	})
@XmlRootElement(name = "biblio")
public class EBiblio extends Biblio {
	transient private UriBuilder root;
	transient private UriBuilder items;

	public EBiblio() {
		super();
	}
	
	public EBiblio(UriBuilder root) {
		this.root = root;
		items = root.clone().path("items");
	}

	@Override
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
	public String getItems() {
		return items.toTemplate();
	}

	@Override
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
	public String getJournals() {
		return root.clone().path("journals").toTemplate();
	}

	@Override
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
	public String getArticles() {
		return items.clone().path("articles").toTemplate();
	}

	@Override
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
	public String getBooks() {
		return items.clone().path("books").toTemplate();
	}

	@Override
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
	public String getSelf() {
		return root.toTemplate();
	}

}
