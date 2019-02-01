package de.engehausen.cc2.verifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.script.Bindings;

import com.google.common.primitives.Ints;

import de.engehausen.cc2.api.Verifier;
import de.engehausen.cc2.data.HappySevenData;

/**
 * Verifies edit distance challenge responses.
 */
public class HappySevenVerifier implements Verifier<Object, HappySevenData> {

	public enum Operation {

		LEFT_CW("L+", arr -> rotate(arr, 0, 3, 1)), // left clockwise
		LEFT_CCW("L-", arr -> rotate(arr, 0, 3, -1)), // left counter-clockwise
		RIGHT_CW("R+", arr -> rotate(arr, 3, 6, 1)), // right clockwise
		RIGHT_CCW("R-", arr -> rotate(arr, 3, 6, -1)), // right counter-clockwise
		ALL_CW("A+", arr -> rotate(arr, 0, 6, 1)), // all clockwise
		ALL_CCW("A-", arr -> rotate(arr, 0, 6, -1)); // all counter-clockwise

		private final String code;
		private final Function<int[], Void> op;
		
		Operation(final String code, final Function<int[], Void> worker) {
			this.code = code;
			this.op = worker;
		}
		
		public String toString() {
			return code;
		}
		
		void rotate(final int arr[]) {
			op.apply(arr);
		}
		
		private static Void rotate(final int arr[], final int start, final int end, final int direction) {
			for (int i = end - start, pos = start; --i >= 0; ) {
				int next = pos - direction;
				if (next < start) {
					next = end;
				} else if (next > end) {
					next = start;
				}
				final int temp = arr[next];
				arr[next] = arr[pos];
				arr[pos] = temp;
				pos = next;
			}
			return null;
		}
	}
	
	private static Map<String, Operation> OPS;
	
	static {
		OPS = new HashMap<>();
		for (final Operation op : Operation.values()) {
			OPS.put(op.code, op);
		}
	}

	/**
	 * Verifies a happy seven challenge response.
	 * The response must
	 * <ul>
	 *   <li>be a list consisting of the well-defined operations
	 *   ({@code L+}, {@code L-}, {@code R+}, {@code R-}, {@code A+}, {@code A-})</li>
	 *   <li>produce the happy seven sequence when applied to the input</li>
	 * </ul>
	 * @param result the challenge response
	 * @param data the input data
	 * @return {@code null} if verification was successful, a message indicating the failure otherwise.
	 */
	@Override
	public String verify(final Object result, final HappySevenData data) {
		if (!(result instanceof Bindings)) {
			return "unexpected: " + (result != null ? result.getClass().getName() : "null");
		}
		if (data.configuration.size() != 7) {
			return "invalid configuration " + data.configuration;
		}
		final List<Integer> expected = new ArrayList<>(data.configuration);
		expected.sort((a, b) -> a.intValue() - b.intValue());
		final List<Integer> actual = applyOperations(data.configuration, toOperations(result));
		return expected.equals(actual) ? null : "operations don't return the happy sequence, instead: " + actual;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(final Object input) {
		if (input instanceof Bindings) {
			return toOperations(input).toString();
		}
		return null;
	}

	protected List<Operation> toOperations(final Object input) {
		final Bindings bindings = (Bindings) input;
		return bindings
			.values()
			.stream()
			.map(e -> OPS.getOrDefault(e, Operation.ALL_CW))
			.collect(Collectors.toList());
	}

	protected List<Integer> applyOperations(final List<Integer> input, List<Operation> operations) {
		final int state[] = Ints.toArray(input); 
		for (final Operation op : operations) {
			op.rotate(state);
		}
		return Ints.asList(state);
	}

}
