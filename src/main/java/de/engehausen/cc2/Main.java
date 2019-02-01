/**
 * Coding Challenge II: Polyglot - a coding challenge.
 * Copyright (C) 2018  Jan Engehausen, smurf667@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.engehausen.cc2;

import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.engehausen.cc2.api.Reporter;
import de.engehausen.cc2.data.Inputs;

/**
 * Main challenge executor. Executes the challenge contributions found
 * and reports to the console by default.
 * The following command line arguments are evaluated:
 * <ul>
 *   <li>{@code -inputs=<filename>} - mandatory file specifying the inputs for testing</li>
 *   <li>{@code -reporter=<name>} - reporter to use, {@code console} by default</li>
 *   <li>{@code -filter=<regexp>} - filter challenge solutions by regular expression</li>
 *   <li>{@code -dry} - does not perform the tests, but outputs the inputs</li>
 * </ul>
 */
public class Main {

	/** {@code -inputs}: the file containing the {@link Inputs} in JSON format */
	public static final String ARG_INPUTS = "-inputs";
	/** {@code -reporter}: the reporter, {@link #REPORTER_CONSOLE} by default */
	public static final String ARG_REPORTER = "-reporter";
	/** {@code -filter}: a regular expression filter to include contributions for execution */
	public static final String ARG_FILTER = "-filter";
	/** {@code -dry}: a flag to not run the contributions but just to report */
	public static final String ARG_DRY = "-dry";
	/** {@code console}: {@code System.out} output */
	public static final String REPORTER_CONSOLE = "console";

	private static final String L_LN = "\n    ";
	private static final String LICENSE_INFO = 
		"\n" + L_LN + "Coding Challenge II: Polyglot  Copyright (C) 2018  Jan Engehausen\n" +
		L_LN + "This program comes with ABSOLUTELY NO WARRANTY." +
		L_LN + "This is free software, and you are welcome to redistribute it" +
		L_LN + "under certain conditions; see source code for details.\n\n";

	private static Map<String, Reporter> REPORTERS;

	static {
		REPORTERS = new HashMap<>();
		for (final Class<? extends Reporter> clz : Reporter.all()) {
			try  {
				final Reporter reporter = clz.newInstance();
				REPORTERS.put(reporter.name(), reporter);
			} catch (IllegalAccessException|InstantiationException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Executes the challenges by running them with a number of inputs.
	 * @param args command line arguments
	 * @throws Throwable in case of error
	 */
	public static void main(final String... args) throws Throwable {
		System.out.println(LICENSE_INFO);
		final Map<String, String> parameters =
			Stream.of(args.length != 1 ? args : args[0].split(" "))
			.collect(Collectors.toMap(Main::key, Main::value));
		if (!parameters.containsKey(ARG_INPUTS)) {
			System.out.println("please specify -inputs=...json");
			System.out.printf("known reporters (-reporter=...) are ", REPORTERS.keySet());
			return;
		}
		final Inputs inputs = new ObjectMapper().readValue(new File(parameters.get(ARG_INPUTS)), Inputs.class);
		try (final Executor executor = new Executor(inputs)) {
			final Reporter reporter = REPORTERS.get(parameters.get(ARG_REPORTER) == null ? REPORTER_CONSOLE : parameters.get(ARG_REPORTER));
			if (reporter == null) {
				System.out.printf("unknown reporter %s%n", parameters.get(ARG_REPORTER));
				return;
			}
			final OutputStream out = reporter.createOutputStream(System.getProperty("user.dir") + File.separatorChar + "target");
			if (!parameters.containsKey(ARG_DRY)) {
				if (!parameters.containsKey(ARG_FILTER)) {
					executor.runAll();
				} else {
					final Pattern pattern = Pattern.compile(parameters.get(ARG_FILTER));
					executor.run(info -> info.getName().startsWith(Executor.PKG_PREFIX) && pattern.matcher(info.getName()).find());
				}
			}
			reporter.generate(inputs, out);
		}
	}
	
	protected static String key(final String parameter) {
		final int idx = parameter.indexOf('=');
		return idx < 0 ? parameter : parameter.substring(0, idx);
	}

	protected static String value(final String parameter) {
		final int idx = parameter.indexOf('=');
		return idx < 0 ? parameter : parameter.substring(1 + idx);
	}

}