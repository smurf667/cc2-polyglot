package de.engehausen.cc2;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.engehausen.cc2.data.Inputs;
import de.engehausen.cc2.data.Results;
import de.engehausen.cc2.reporters.Console;

public class ExecutorTest {
	
	private static Inputs inputs;

	@BeforeClass
	public static void setup() throws IOException {
		inputs = new ObjectMapper().readValue(ExecutorTest.class.getResourceAsStream("/testInputs.json"), Inputs.class);
	}

	@Test
	public void testAll() throws IOException {
		try (final Executor executor = new Executor(inputs)) {
			executor.run(info -> info.getName().contains(".examples.") && !info.getName().startsWith("target"));
			new Console().generate(inputs, System.out);
			verifyResults(inputs.beeGraphs);
			verifyResults(inputs.editDistances);
			verifyResults(inputs.happySeven);
			verifyResults(inputs.huffmanStrings);
			verifyResults(inputs.notationExpressions);
			verifyResults(inputs.pancakes);
			verifyResults(inputs.xmlEasy);
			verifyResults(inputs.xmlHard);
		}
	}
	
	protected void verifyResults(final List<? extends Results> list) {
		list
			.stream()
			.flatMap( results -> results.results.values().stream() )
			.forEach(record -> Assert.assertTrue(record.reason, record.ok) );
	}

}
