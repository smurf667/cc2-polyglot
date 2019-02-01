package de.engehausen.cc2.verifiers;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.armedbear.lisp.Cons;
import org.armedbear.lisp.Lisp;
import org.armedbear.lisp.LispObject;

import de.engehausen.cc2.api.Verifier;
import de.engehausen.cc2.data.ReversePolishNotationData;

/**
 * Verifies reverse polish notation challenge responses.
 */
public class ReversePolishNotationVerifier implements Verifier<LispObject, ReversePolishNotationData> {

	private static final Map<String, BiFunction<BigDecimal, BigDecimal, String>> OPERATORS;
	
	static {
		OPERATORS = new HashMap<>();
		OPERATORS.put("+", (a, b) -> a.add(b).toPlainString());
		OPERATORS.put("-", (a, b) -> a.subtract(b).toPlainString());
		OPERATORS.put("*", (a, b) -> a.multiply(b).toPlainString());
		OPERATORS.put("/", (a, b) -> a.divide(b).toPlainString());
	}

	/**
	 * Verifies a reverse polish notation challenge response.
	 * The response must
	 * <ul>
	 *   <li>be in the expected, documented format</li>
	 *   <li>produce the expected result value when the expression is evaluated</li>
	 * </ul>
	 * @param result the challenge response
	 * @param data the input data
	 * @return {@code null} if verification was successful, a message indicating the failure otherwise.
	 */
	@Override
	public String verify(final LispObject result, final ReversePolishNotationData data) {
		if (result instanceof Cons) {
			// verification;
			final Deque<String> stack = new ArrayDeque<>();
			LispObject head = result;
			while (head != Lisp.NIL) {
				final String token = head.car().getStringValue();
				if (OPERATORS.containsKey(token)) {
					final BigDecimal second = new BigDecimal(stack.pop());
					final BigDecimal first = new BigDecimal(stack.pop());
					stack.push(OPERATORS.get(token).apply(first, second));
				} else {
					stack.push(token);
				}
				head = head.cdr();
			}
			final BigDecimal actual = new BigDecimal(stack.pop());
			return actual.equals(data.expected) ? null : actual + " != " + data.expected;
		}
		return "unexpected: " + (result != null ? result.printObject() : "null");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(final LispObject input) {
		return input == null ? null : input.printObject();
	}

}
