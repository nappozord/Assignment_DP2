package it.polito.dp2.BIB.sol1;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import it.polito.dp2.BIB.BibReader;
import it.polito.dp2.BIB.BibReaderException;
import it.polito.dp2.BIB.sol1.jaxb.Biblio;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;

public class BibReaderFactory extends it.polito.dp2.BIB.BibReaderFactory {
	
	@Override
	public BibReader newBibReader() throws BibReaderException{
		try {
			/*If the unmarshaller cannot be created (Biblio class not well implemented...)
			 * an exception is generated
			 */
			JAXBContext jc = JAXBContext.newInstance(Biblio.class);
			Unmarshaller u = jc.createUnmarshaller();
			SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
			try{
				/*Validation of the xml file against the xsd schema.
				 * If the file is the one generated by the Serializer,
				 * it should obviously be valid.
				 * If not, an exception is generated
				 */
				Schema schema = sf.newSchema(new File("xsd/biblio_e.xsd"));
				u.setSchema(schema);
				u.setEventHandler(
						new ValidationEventHandler(){
							public boolean handleEvent(ValidationEvent ve){
									 if (ve.getSeverity() != ValidationEvent.WARNING) {
			                                ValidationEventLocator vel = ve.getLocator();
			                                System.out.println("Line:Col[" + vel.getLineNumber() +
			                                    ":" + vel.getColumnNumber() +
			                                    "]:" + ve.getMessage());
			                           }
			                           return true;
							}
						});
			} catch (org.xml.sax.SAXException se) {
                System.out.println("Unable to validate due to following error.");
                se.printStackTrace();
            }
			
			/*The input filename should be given by that property by design,
			 * so there are no checks that the property isn't actually a null...
			 */
			String filename = System.getProperty("it.polito.dp2.BIB.sol1.BibInfo.file");
			Biblio bib = (Biblio) u.unmarshal(new File(filename));
			BibReader bibimp = new BibReaderImpl(bib);
			
			// If all goes right, a BibReader instance is generated
			return bibimp;
			
		} catch(UnmarshalException ue) {
            /*The JAXB specification does not mandate how the JAXB provider
             * must behave when attempting to unmarshal invalid XML data.  In
             * those cases, the JAXB provider is allowed to terminate the 
             * call to unmarshal with an UnmarshalException.
             */
            System.out.println( "Caught UnmarshalException" );
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*If for any reason (xml file not valid...) it cannot return a
		 * BibReader, an exception is thrown
		 */
		throw new BibReaderException();
	}
	
}