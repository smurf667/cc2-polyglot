package de.engehausen.cc2.verifiers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import com.google.common.base.Charsets;

import de.engehausen.cc2.api.Verifier;
import de.engehausen.cc2.data.XmlData;

/**
 * Verifies XML transformation challenge responses.
 */
public class XmlVerifier implements Verifier<Document, XmlData> {

	/**
	 * Verifies a XML transformation challenge response.
	 * The list of XPath expressions of the data must all evaluate to
	 * {@code true}. Please be aware that namespace handling is a bit
	 * finicky. The input document must declare expected namespaces.
	 * @param document the transformed document
	 * @param data the input data
	 * @return {@code null} if verification was successful, a message indicating the failure otherwise.
	 */
	@Override
	public String verify(final Document document, final XmlData data) {
		String lastExpression = null;
		try {
			final XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(new NamespaceResolver(document));
			for (final String expression : data.xpathAsserts) {
				lastExpression = expression;
				final Boolean success = (Boolean) xpath.evaluate(expression, document, XPathConstants.BOOLEAN);
				if (Boolean.FALSE.equals(success)) {
					return "the expectation is not met: " + expression;
				}
			}
		} catch (XPathExpressionException e) {
			return (lastExpression != null ? lastExpression : "" + " an error occurred: " + e.getMessage()).trim();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(final Document input) {
		if (input != null) {
			final ByteArrayOutputStream stream = new ByteArrayOutputStream(4096);
			try {
				printDocument(input, stream);
			} catch (TransformerException | IOException e) {
				return null;
			}
			return new String(stream.toByteArray(), Charsets.UTF_8);
		}
		return null;
	}

	public static void printDocument(final Document doc, final OutputStream out) throws IOException, TransformerException {
		final TransformerFactory tf = TransformerFactory.newInstance();
		final Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(out, "UTF-8")));
	}

	private static class NamespaceResolver implements NamespaceContext {

		private final Document document;

		public NamespaceResolver(final Document document) {
			this.document = document;
		}

		/**
		 * Returns the namespace URIs by delegating to the underlying
		 * document.
		 * @param prefix the prefix to look up
		 */
		public String getNamespaceURI(final String prefix) {
			if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
				return document.lookupNamespaceURI(null);
			} else {
				return document.lookupNamespaceURI(prefix);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public String getPrefix(final String namespaceURI) {
			return document.lookupPrefix(namespaceURI);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("rawtypes")
		public Iterator getPrefixes(final String namespaceURI) {
			return Collections.emptyIterator();
		}
	}

}
