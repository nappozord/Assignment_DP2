package it.polito.dp2.BIB.sol2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import it.polito.dp2.rest.neo4j.client.jaxb.Data;
import it.polito.dp2.rest.neo4j.client.jaxb.DataRel;
import it.polito.dp2.rest.neo4j.client.jaxb.Node;
import it.polito.dp2.rest.neo4j.client.jaxb.ObjectFactory;
import it.polito.dp2.rest.neo4j.client.jaxb.Relationship;
import it.polito.dp2.rest.neo4j.client.jaxb.Relationships;
import it.polito.dp2.rest.neo4j.client.jaxb.Traversal;

public class Neo4jClient {
	Client client;
	WebTarget target;
	String uri = "http://localhost:7474/db";
	String urlProperty = "it.polito.dp2.BIB.ass2.URL";
	String portProperty = "it.polito.dp2.BIB.ass2.PORT";
	private ObjectFactory of = new ObjectFactory();

	public Neo4jClient() {
		client = ClientBuilder.newClient();
		
		String customUri = System.getProperty(urlProperty);
		String customPort = System.getProperty(portProperty);
		if (customUri != null)
			uri = customUri;
		
		target = client.target(uri).path("data");
	}
	
	public void close() {
		client.close();
	}

	// Use Neo4j REST APIs to create nodes for every item
	public Node createNode(String title) throws Neo4jClientException {
		Data data = of.createData();
		data.setTitle(title);
		try {
			Node node = target.path("node")
				  .request(MediaType.APPLICATION_JSON_TYPE)
				  .post(Entity.json(data), Node.class);
			return node;
		} catch (WebApplicationException|ProcessingException e) {
			throw new Neo4jClientException(e);
		}
	}
	
	// Use Neo4j REST APIs to create relationships between cited and citing items
	public Relationship createRelationship(String uri_from, String uri_to) throws Neo4jClientException {
		DataRel data = of.createDataRel();
		data.setType("CitedBy");
		data.setTo(uri_to);
		try{
			Relationship rel = client.target(uri_from).path("relationships")
					.request(MediaType.APPLICATION_JSON_TYPE)
					.post(Entity.json(data), Relationship.class);
			return rel;
		} catch (WebApplicationException|ProcessingException e) {
			throw new Neo4jClientException(e);
		}
	}
	
	/*Use Noe4j REST APIs to get every item citing (directly or indirectly) the given item, given a depth
	 * return a list of nodes
	 */
	public List<Node> createTraversal(int maxDepth, String uri_from) throws Neo4jClientException {
		Traversal data = of.createTraversal();
		BigInteger b = BigInteger.valueOf(maxDepth);
		data.setMaxDepth(b);
		data.setOrder("breadth_first");
		data.setUniqueness("node_global");
		Relationships relationships = new Relationships();
		relationships.setDirection("out");
		relationships.setType("CitedBy");
		data.setRelationships(relationships);
		//System.out.println(client.target(uri_from).path("traverse/node"));
		try{
			Invocation.Builder builder = client.target(uri_from).path("traverse/node")
					.request(MediaType.APPLICATION_JSON_TYPE);
			Response response = builder.post(Entity.json(data));
			List<Node> nodes = response.readEntity(new GenericType<List<Node>>(){});
			return nodes;
		} catch (WebApplicationException|ProcessingException e) {
			throw new Neo4jClientException(e);
		}
	}

}
