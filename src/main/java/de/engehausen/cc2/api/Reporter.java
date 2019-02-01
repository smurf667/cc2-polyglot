package de.engehausen.cc2.api;

import java.io.OutputStream;

import de.engehausen.cc2.data.Inputs;
import de.engehausen.cc2.reporters.Console;
import de.engehausen.cc2.reporters.Json;
import de.engehausen.cc2.reporters.Slides;

/**
 * Reporter for challenge results.
 */
public interface Reporter {

	/**
	 * Creates an output stream
	 * @param folder the folder into which to output (where this has useful meaning)
	 * @return the output stream (the {@link Console} reports to {@code System.out} and does not create a new stream)
	 */
	OutputStream createOutputStream(String folder);

	/**
	 * Creates the report based on the given results.
	 * @param results the results to report
	 * @param output the stream to write to
	 */
	void generate(Inputs results, OutputStream output);

	/**
	 * The name of the reporter.
	 * @return the name of the reporter.
	 */
	String name();

	/**
	 * Returns all known reporters.
	 * @return all known reporters.
	 */
	@SuppressWarnings("unchecked")
	static Class<? extends Reporter>[] all() {
		return new Class[] {
			Console.class,
			Json.class,
			Slides.class
		};
	}
}
