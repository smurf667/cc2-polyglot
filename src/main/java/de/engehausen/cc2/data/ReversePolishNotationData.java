package de.engehausen.cc2.data;

import java.math.BigDecimal;

/**
 * Input data for the reverse polish notation challenge.
 */
public class ReversePolishNotationData extends Results {

	/** the infix expression to transform */
	public String expression;
	/** the result of the expression when evaluated */
	public BigDecimal expected;

	/**
	 * Creates the data.
	 * @param expression the infix expression to transform
	 * @param expected the result of the expression when evaluated
	 */
	public ReversePolishNotationData(final String expression, final BigDecimal expected) {
		this.expression = expression;
		this.expected = expected;
	}

	public ReversePolishNotationData() {
		this(null, null);
	}

}
