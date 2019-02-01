package de.engehausen.cc2.reporters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.engehausen.cc2.api.Reporter;
import de.engehausen.cc2.data.Inputs;

/**
 * Writes the challenge results to a JSON file name {@code report.json}.
 * This is actually a parseable {@link Inputs} instance.
 */
public class Json implements Reporter {

	private static final String NAME = "json";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generate(final Inputs results, final OutputStream output) {
		try {
			new ObjectMapper()
				.writer()
				.withDefaultPrettyPrinter()
				.writeValue(output, results);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputStream createOutputStream(final String folder) {
		try {
			return new FileOutputStream(folder + File.separatorChar + "report.json");
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name() {
		return NAME;
	}

}
