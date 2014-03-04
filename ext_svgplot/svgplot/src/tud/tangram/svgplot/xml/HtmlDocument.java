package tud.tangram.svgplot.xml;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class HtmlDocument extends Document {
	
	//TODO: make it more general in a mathematical way
	

	final protected Element head;
	final protected Element body;
	/**
	 * 
	 * @author Gregor Harlan
	 * Idea and supervising by Jens Bornschein jens.bornschein@tu-dresden.de
	 * Copyright by Technische Universität Dresden / MCI 2014
	 *
	 */
	public HtmlDocument(String title) throws ParserConfigurationException {
		super("html");

		head = createElement("head");
		appendChild(head);

		Element charset = createElement("meta");
		charset.setAttribute("charset", "utf-8");
		head.appendChild(charset);
		head.appendChild(createTextElement("title", title));

		body = createElement("body");
		appendChild(body);
	}

	public Node appendBodyChild(Element child) {
		return body.appendChild(child);
	}

	public Element createDiv(String id) {
		return createElement("div", id);
	}

	public Element createP() {
		return createElement("p");
	}

	public Element createP(String content) {
		return createTextElement("p", content);
	}

	@Override
	protected void setTransformerProperties(Transformer transformer) {
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	}
}
