package de.engehausen.cc2.verifiers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.script.Bindings;

import de.engehausen.cc2.api.Verifier;
import de.engehausen.cc2.data.PancakeFlipperData;

/**
 * Verifies pancake flipper challenge responses.
 */
public class PancakeVerifier implements Verifier<Object, PancakeFlipperData> {

	/**
	 * Verifies a pancake flipper challenge response.
	 * The response must
	 * <ul>
	 *   <li>be a list of legal flip positions</li>
	 *   <li>produce a sorted pancake stack when applied to the input</li>
	 * </ul>
	 * @param result the challenge response
	 * @param data the input data
	 * @return {@code null} if verification was successful, a message indicating the failure otherwise.
	 */
	@Override
	public String verify(final Object result, final PancakeFlipperData data) {
		if (!(result instanceof Bindings)) {
			return "unexpected: " + (result != null ? result.getClass().getName() : "null");
		}
		final List<Integer> expected = new ArrayList<>(data.stack);
		expected.sort((a, b) -> a.intValue() - b.intValue());
		final List<Integer> actual = applyFlips(new ArrayList<>(data.stack), getFlips(result));
		return expected.equals(actual) ? null : "operations do not order: " + actual;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(final Object input) {
		if (input instanceof Bindings) {
			return getFlips(input).toString();
		}
		return null;
	}

	protected List<Integer> getFlips(final Object input) {
		final Bindings bindings = (Bindings) input;
		return bindings
			.values()
			.stream()
			.map(e -> e instanceof Number ? Integer.valueOf(((Number) e).intValue()) : Integer.valueOf(0))
			.collect(Collectors.toList());
	}

	protected List<Integer> applyFlips(final List<Integer> stack, final List<Integer> flips) {
		for (final Integer flipPosition : flips) {
			final int pos = 1 + flipPosition.intValue();
			if (pos < 2 || pos > stack.size()) {
				// not good
				break;
			}
			Collections.reverse(stack.subList(0, pos));
		}
		return stack;
	}

}
