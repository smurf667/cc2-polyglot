package de.engehausen.cc2.impl.examples;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import de.engehausen.cc2.api.CharacterOperation;
import de.engehausen.cc2.api.EditDistanceFunction;

public class MinimalEditDistanceDemo implements EditDistanceFunction {

	@Override
	public List<CharacterOperation> apply(final String from, final String to) {
		assertEquals("hello world!", from);
		assertEquals("hello, world", to);
		final List<CharacterOperation> edits = 
			Arrays.asList(
				CharacterOperation.insert(5, ','),
				CharacterOperation.remove(12)
			);
		return edits;
	}

}
