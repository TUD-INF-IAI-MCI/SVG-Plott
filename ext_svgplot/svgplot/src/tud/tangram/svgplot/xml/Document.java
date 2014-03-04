package tud.tangram.svgplot.xml;

import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
/**
 * 
 * @author Gregor Harlan
 * Idea and supervising by Jens Bornschein jens.bornschein@tu-dresden.de
 * Copyright by Technische Universität Dresden / MCI 2014
 *
 */
public class Document {

	protected final org.w3c.dom.Document doc;
	protected final Element root;

	public Document(String root) throws ParserConfigurationException {
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		doc.setXmlStandalone(true);

		this.root = doc.createElement(root);
		doc.appendChild(this.root);
	}

	public Node appendChild(Node child) {
		return root.appendChild(child);
	}

	public void writeTo(OutputStream outputStream) throws TransformerException {
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		setTransformerProperties(transformer);
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(outputStream);

		transformer.transform(source, result);
	}

	protected void setTransformerProperties(Transformer transformer) {
	}

	public Element createElement(String element) {
		return doc.createElement(element);
	}

	public Element createElement(String element, String id) {
		Element el = createElement(element);
		el.setAttribute("id", id);
		return el;
	}

	public Text createTextNode(String data) {
		return doc.createTextNode(data);
	}

	public Element createTextElement(String element, String content) {
		Element el = createElement(element);
		el.setTextContent(content);
		return el;
	}

}