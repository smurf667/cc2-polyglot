package de.engehausen.cc2.reporters;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Charsets;

import de.engehausen.cc2.api.Reporter;
import de.engehausen.cc2.data.Inputs;
import de.engehausen.cc2.data.Results;
import de.engehausen.cc2.data.Results.Record;

/**
 * Reports challenge results to {@code System.out}.
 */
public class Console implements Reporter {

	/** system property for limiting the response when outputting, defaults to 63 */
	public  static final String RESPONSELIMIT = "responselimit";

	private static final String NAME = "console";
	private static final String NEWLINE = System.lineSeparator();
	private static final String SEPARATOR = String.join("", Collections.nCopies(78, "-")) + NEWLINE;
	private static final String SEPARATOR_MAIN = String.join("", Collections.nCopies(78, "=")) + NEWLINE;
	private static final String ANONYMOUS_TEST = "(anonymous)";
	private static final String DOTS = "...";
	private static final String INDENT = "  ";
	private static final String RESPONSE = "response: ";
	private static final String TIME = "time    : ";
	private static final String MILLIS = "ms";
	private static final String FAIL = "fail: ";
	private static final String PASS = "pass";
	
	private final int outLimit;

	/**
	 * Creates the reporter. The system property {@link #RESPONSELIMIT} defines how much of the response is output.
	 */
	public Console() {
		outLimit = Integer.parseInt(System.getProperty(RESPONSELIMIT, "63"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generate(final Inputs results, final OutputStream output) {
		final OutputStreamWriter writer = new OutputStreamWriter(output, Charsets.UTF_8);
		try {
			report("Java: Minimal Edit Distance", results.editDistances, writer);
			report("Java: Busy Bee", results.beeGraphs, writer);
			report("JavaScript: Pancake Flipper", results.pancakes, writer);
			report("JavaScript: Happy Seven", results.happySeven, writer);
			report("Lisp: Reverse Polish Notation", results.notationExpressions, writer);
			report("Lisp: Huffman Coding", results.huffmanStrings, writer);
			report("XSLT: Simple element transformation", results.xmlEasy, writer);
			report("XSLT: Multi-rule based document transformation", results.xmlHard, writer);
			writer.flush();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputStream createOutputStream(final String folder) {
		return System.out;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name() {
		return NAME;
	}

	protected void report(final String title, final List<? extends Results> results, final OutputStreamWriter writer) throws IOException {
		if (results != null) {
			writer
				.append(title)
				.append(NEWLINE)
				.append(SEPARATOR_MAIN);
			for (final Results testCase : results) {
				writer
					.append(testCase.label != null ? testCase.label : ANONYMOUS_TEST)
					.append(NEWLINE)
					.append(SEPARATOR);
				if (testCase.results != null) {
					testCase
						.results
						.keySet()
						.stream()
						.sorted()
						.forEach( participant -> {
							try {
								writer
								.append(participant)
								.append(NEWLINE);
								writeRecord(writer, testCase.results.get(participant));
							} catch (IOException e) {
								throw new IllegalStateException(e);
							}
						});
				}
			}
			writer.append(NEWLINE);
		}
	}

	protected void writeRecord(final OutputStreamWriter writer, final Record record) throws IOException {
		writer
			.append(INDENT)
			.append(record.ok ? PASS : FAIL + record.reason)
			.append(NEWLINE);
		if (record.response != null) {
			writer
				.append(INDENT)
				.append(RESPONSE)
				.append(shorten(record.response, outLimit))
				.append(NEWLINE);
		}
		if (record.millis > 0) {
			writer
				.append(INDENT)
				.append(TIME)
				.append(Double.toString(record.millis))
				.append(MILLIS)
				.append(NEWLINE);
		}
	}

	protected static String shorten(final String in, final int limit) {
		if (in != null) {
			if (in.contains(NEWLINE)) {
				return shorten(in.replaceAll("[\n\r]", ""), limit);
			}
			return in.length() < limit ? in : in.substring(0, limit) + DOTS;
		}
		return "";
	}

}
