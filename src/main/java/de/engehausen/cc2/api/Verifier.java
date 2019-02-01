package de.engehausen.cc2.api;

import de.engehausen.cc2.data.Results;

/**
 * Verifies the response to a challenge.
 *
 * @param <I> the input response to a challenge
 * @param <D> the result recording structure
 */
public interface Verifier<I, D extends Results> {

	/**
	 * Verifies the input result challenge solution using the
	 * given data.
	 * @param input the response for a challenge, must not be {@code null}
	 * @param data the input data of the challenge, must not be {@code null}
	 * @return {@code null} if every check has passed, or a string giving the reason for the failure.
	 */
	String verify(I input, D data);

	/**
	 * Returns a string representation of the challenge response.
	 * @param input the challenge response, must not be {@code null}
	 * @return a string representation of the response, or {@code null}
	 */
	String toString(I input);

}
