package de.engehausen.cc2.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Input data for both XML transformation challenges.
 */
public class XmlData extends Results {

	/** the resource name of the XML file to transform (must be on classpath) */
	public String xmlIn;
	/** parameters (flags) for the transformation */
	@JsonInclude(Include.NON_NULL)
	public String parameter;
	/** a list of XPath assertions to be applied to the response, expecting {@code true} as evaluation result */
	public List<String> xpathAsserts;

	/**
	 * Creates the data.
	 * @param xmlIn the resource name of the XML file to transform (must be on classpath)
	 * @param parameter parameters (flags) for the transformation
	 * @param xpathAsserts a list of XPath assertions to be applied to the response, expecting {@code true} as evaluation result
	 */
	public XmlData(final String xmlIn, final String parameter, final List<String> xpathAsserts) {
		this.xmlIn = xmlIn;
		this.parameter = parameter;
		this.xpathAsserts = xpathAsserts;
	}
	
	public XmlData() {
		this(null, null, null);
	}

}
