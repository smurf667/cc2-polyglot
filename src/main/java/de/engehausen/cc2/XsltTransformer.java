package de.engehausen.cc2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.engehausen.cc2.verifiers.XmlVerifier;

/**
 * Helper transforming a document with a stylesheet transformation;
 * to be run on the command line.
 * It produces a transformer for both standalone (command-line)
 * invocation and for use in the {@link Executor}.
 * An <em>namespace-aware</em> document builder factory is used in
 * the standalone case.
 */
public class XsltTransformer {

	public final DocumentBuilder docBuilder;
	private final TransformerFactory transformerFactory;

	public XsltTransformer() {
		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			documentBuilderFactory.setNamespaceAware(true);
			docBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		}
		transformerFactory = TransformerFactory.newInstance();
	}
	
	public Transformer newTransformer(final InputStream stream) throws TransformerConfigurationException {
		return transformerFactory.newTransformer(
			new StreamSource(stream)
		);
	}

	private Document transform(final String xmlResource, final String xsltResource, final String flags) throws IOException, SAXException, TransformerException {
		try (final InputStream xsltStream = new FileInputStream(xsltResource)) {
			final Transformer xsltTransformer = newTransformer(xsltStream);
			if (flags != null) {
				xsltTransformer.setParameter("flags", flags);
			}
			try (final InputStream xmlStream = new FileInputStream(xmlResource)) {
				final Document input = docBuilder.parse(
					new InputSource(xmlStream)
				);
				final Document output = docBuilder.newDocument();
				xsltTransformer.transform(new DOMSource(input), new DOMResult(output));
				return output;
			}
		}
	}

	/**
	 * Performs a transformation of an input file given a stylesheet
	 * transformation file.
	 * @param args the arguments, must be exactly one argument, containing
	 * a whitespace-separated string with at least two parts: First the XML file,
	 * second the XSLT, and the third (optional) one the flags to pass
	 * to the transformation.
	 * <p>This is normally run through Maven using {@code mvn -Pxslt ...}</p>
	 * @throws Throwable in case of error
	 */
	public static void main(final String... args) throws Throwable {
		if (args.length != 1) {
			System.err.println("*** please specify one argument containing 'in-xml xslt [flags]'");
			return;
		}
		final String[] params = args[0].split(" ");
		final String xmlFile = getOrNull(params, 0);
		final String xsltFile = getOrNull(params, 1);
		final String flags = getOrNull(params, 2);
		if (xmlFile == null)  {
			throw new IllegalStateException("-Din is required");
		}
		if (xsltFile == null)  {
			throw new IllegalStateException("-Dxslt is required");
		}
		final Document output = new XsltTransformer().transform(xmlFile, xsltFile, flags);
		XmlVerifier.printDocument(output, System.out);
		try (final FileOutputStream fos = new FileOutputStream("target/out.xml")) {
			XmlVerifier.printDocument(output, fos);
		}
	}
	
	private static String getOrNull(final String[] params, final int index) {
		if (index < params.length) {
			final String result = params[index];
			if (!result.startsWith("$")) {
				return result;
			}
		}
		return null;
	}

}
