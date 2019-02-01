package de.engehausen.cc2.reporters;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.engehausen.cc2.api.Reporter;
import de.engehausen.cc2.data.Inputs;
import de.engehausen.cc2.data.Results;
import de.engehausen.cc2.data.Results.Record;

/**
 * Generates a fancy <a href="https://revealjs.com/">RevealJS</a>-based
 * presentation with the results.
 */
public class Slides implements Reporter {

	/** name of non-competing reference solution */
	private static final String REFERENCE_NAME = "codemonkey";
	private static final String NAME = "slides";

	@Override
	public OutputStream createOutputStream(final String folder) {
		final Path root = Paths.get(folder);
		try {
			unzipReveal(root);
			final File file = root.resolve("slides/index.html").toFile();
			System.out.printf("%nWriting %s%n%n", file.getAbsolutePath());
			return new FileOutputStream(file);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}


	@Override
	public String name() {
		return NAME;
	}

	@Override
	public void generate(final Inputs results, final OutputStream output) {
		try {
			writeFragment("/fragments/head.htmlf", output);
			writeFragment("/fragments/title.htmlf", output);
			writeFragment("/fragments/intro.htmlf", output);

			final Participants participants = allParticipants(results);
			final PrintStream stream = new PrintStream(output);

			report("/fragments/01-minimal.htmlf", stream, participants, results.editDistances);
			report("/fragments/02-busy.htmlf", stream, participants, results.beeGraphs);
			report("/fragments/03-pancake.htmlf", stream, participants, results.pancakes);
			report("/fragments/04-happy.htmlf", stream, participants, results.happySeven);
			report("/fragments/05-rpn.htmlf", stream, participants, results.notationExpressions);
			report("/fragments/06-huffman.htmlf", stream, participants, results.huffmanStrings);
			report("/fragments/07-xmleasy.htmlf", stream, participants, results.xmlEasy);
			report("/fragments/08-xmlhard.htmlf", stream, participants, results.xmlHard);
			
			ranking(allChallenges(results), participants, stream);

			writeFragment("/fragments/beer.htmlf", output);

			writeFragment("/fragments/tail.htmlf", output);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	protected void report(final String fragment, final PrintStream stream, final Participants participants, final List<? extends Results> results) throws IOException {
		writeFragment(fragment, stream, stats(participants, results));
		details(participants, results, stream);
		stream.println("</section>");
	}

	protected void ranking(final int allChallenges, final Participants participants, final PrintStream stream) throws IOException {
		writeFragment("/fragments/ranking.htmlf", stream);
		final Participant reference = participants.participants.get(REFERENCE_NAME);
		final int original;
		if (reference != null) {
			original = reference.ok;
			// make the reference solutions last in list
			reference.ok = -1;
		} else {
			original = 0;
		}
		participants
			.names
			.sort( (a, b) -> {
				final Participant first = participants.participants.get(a);
				final Participant second = participants.participants.get(b);
				if (first.ok == second.ok) {
					return (int) (first.time - second.time);
				} else {
					return second.ok - first.ok;
				}
			});
		if (reference != null) {
			reference.ok = original;
		}
		final int last = participants.names.size() - 1;
		partition(participants.names, 5)
			.forEach( sublist -> {
				final DateFormat timeFormatter = new SimpleDateFormat("mm:ss.SSS");
				stream.printf("<section><h3>Ranking%s</h3><table><tbody>%n", participants.names.indexOf(sublist.get(0)) == 0 ? "" : " (continued)");
				sublist
					.stream()
					.forEach( name -> {
						final Participant participant = participants.participants.get(name);
						final int position = participants.names.indexOf(name);
						stream.printf("<tr%s><td><span class=\"pos%s\">%s</span></td><td>%.2f&#37;</td><td>%s</tr>%n",
							position < 5 ?  " class=\"fragment\" data-fragment-index=\"" + Integer.toString(sublist.size() - position) + "\"" : "",
							position < 3 ? Integer.toString(position) : "n",
							position != last ? name : "<em>" + name + " (reference)</em>",
							Double.valueOf( 100d * participant.ok / allChallenges),
							timeFormatter.format(new Date((long) participant.time))
						);
					});
				stream.println("</tbody></table></section>");
			});
		stream.println("</section>");
	}

	protected void writeFragment(final String name, final OutputStream out, final Map<String, String> tokens) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
		writeFragment(name, baos);
		String result = new String(baos.toByteArray(), StandardCharsets.UTF_8);
		for (Map.Entry<String, String> entry : tokens.entrySet()) {
			if (result.contains(entry.getKey())) {
				result = result.replaceAll(entry.getKey(), entry.getValue());
			}
		}
		out.write(result.getBytes());
	}

	protected void writeFragment(final String name, final OutputStream out) throws IOException {
		try (final InputStream in = Slides.class.getResourceAsStream(name)) {
			final byte[] b = new byte[8192];
			for (int r; (r = in.read(b)) != -1;) {
				out.write(b, 0, r);
			}
		}
	}

	protected void unzipReveal(final Path rootFolder) throws IOException {
		final InputStream stream = Slides.class.getResourceAsStream("/revealjs-3.7.0.zip");
		if (stream == null) {
			throw new IllegalStateException("cannot find revealjs");
		}
		try (final ZipInputStream zipStream = new ZipInputStream(stream)) {
			ZipEntry entry;
			while ( (entry = zipStream.getNextEntry()) != null ) {
				if (entry.isDirectory()) {
					Files.createDirectories(rootFolder.resolve(entry.getName()));
				} else {
					Files.copy(zipStream, rootFolder.resolve(entry.getName()), StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
	}

	protected void details(final Participants participants, final List<? extends Results> results, final PrintStream out) throws IOException {
		results
			.stream()
			.forEach(data -> {
				partition(participants.names, 7)
					.stream()
					.forEach( sublist -> {
						out.println("<section>");
						out.printf("<h3>Scenario: %s%s</h3>%n<table><tbody>%n", data.label != null ? data.label : "anonymous - provide label!", participants.names.indexOf(sublist.get(0)) > 0 ? "(cont.)" : "");
						sublist
							.stream()
							.forEach( name -> {
								final Participant participant = participants.participants.get(name);
								final Record record = data.results.get(name);
								if (record != null) {
									participant.time += record.millis;
									if (record.ok) {
										participant.ok++;
									}
									out.printf("<tr><td>%s</td><td><span class=\"%s\">%s</span></td><td>%.3fms</td></tr>%n",
										name,
										record.ok ? "ok" : "nok",
										record.ok ? "ok" : Console.shorten(record.reason, 32),
										Double.valueOf(record.millis)
									);
								}
							});
						out.println("</tbody></table></section>");
					});
			});
	}

	protected <T> Collection<List<T>> partition(final List<T> list, final int chunkSize) {
		final AtomicInteger counter = new AtomicInteger(0);
		return list
			.stream()
			.collect(Collectors.groupingBy(it -> Integer.valueOf(counter.getAndIncrement() / chunkSize)))
			.values();
	}

	protected Map<String, String> stats(final Participants participants, final List<? extends Results> results) {
		final float total = participants.names.size() * results.size();
		final float[] states = new float[3];
		for (final String p : participants.names) {
			for (final Results res : results) {
				final Record r = res.results.get(p);
				if (r != null) {
					if (r.ok) {
						states[0]++;
					} else {
						states[2]++;
					}
				}
			}
		}
		states[1] = total - states[0] - states[2];
		final StringBuilder sb = new StringBuilder(128);
		sb.append("<div class=\"resbar\">");
		for (int i = 0; i < states.length; i++) {
			if (states[i] > 0) {
				final String val = Math.round(100 * states[i] / total) + "%";
				sb
					.append("<div class=\"res")
					.append(i)
					.append("\" style=\"width: ")
					.append(val)
					.append("\">")
					.append(val)
					.append("</div>");
			}
		}
		sb.append("</div>");
		return Collections.singletonMap("@STATS@", sb.toString());
	}

	protected Participants allParticipants(final Inputs results) {
		final Set<String> participants = new HashSet<>();

		collectParticipants(results.editDistances, participants);
		collectParticipants(results.beeGraphs, participants);
		collectParticipants(results.pancakes, participants);
		collectParticipants(results.happySeven, participants);
		collectParticipants(results.notationExpressions, participants);
		collectParticipants(results.huffmanStrings, participants);
		collectParticipants(results.xmlEasy, participants);
		collectParticipants(results.xmlHard, participants);
		
		return new Participants(participants
			.stream()
			.sorted()
			.collect(Collectors.toList()));
	}

	protected int allChallenges(final Inputs results) {
		return results
			.all()
			.stream()
			.mapToInt( l -> l.size() )
			.sum();
	}

	protected void collectParticipants(final List<? extends Results> list, final Set<String> collector) {
		if (list != null) {
			list
				.stream()
				.forEach( res -> collector.addAll(res.results != null ? res.results.keySet() : Collections.emptySet() ) );
		}
	}

	public static void main(String[] args) throws Throwable {
		final Inputs inputs = new ObjectMapper().readValue(Slides.class.getResourceAsStream("/report.json"), Inputs.class);
		final Slides s = new Slides();
		s.generate(inputs, s.createOutputStream("C:\\Users\\engehau\\workspace\\cc2-polyglot\\target"));
	}

	private static class Participants {
		
		public final List<String> names;
		public final Map<String, Participant> participants;
		
		protected Participants(final List<String> names) {
			this.names = names;
			participants = new HashMap<>();
			names
				.stream()
				.forEach( name -> participants.put(name, new Participant()));
		}
	}

	private static class Participant {

		public double time;
		public int ok;

	}

}
