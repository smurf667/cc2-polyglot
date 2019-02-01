package de.engehausen.cc2.impl.examples;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

import de.engehausen.cc2.api.BusyBeeFunction;
import de.engehausen.cc2.api.Connection;

public class BusyBeeDemo implements BusyBeeFunction {

	@Override
	public List<String> apply(final List<Connection> connections, final Integer minRequired) {
		final List<Connection> expected =
			Arrays.asList(
				new Connection("A", "B", 1),
				new Connection("B", "C", 2)
			);
		assertEquals(expected, connections);
		assertEquals(Integer.valueOf(3), minRequired);
		// return one possible solution for this
		return Arrays.asList("A", "B", "C");
	}

}
