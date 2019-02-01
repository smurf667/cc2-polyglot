package de.engehausen.cc2.api;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Function to compute the character operations for turning a given
 * string into another string. The number of operations is also called
 * the "minimal edit distance" for the two strings if no unnecessary
 * operations are in the list and the operations transform the input
 * string into the target string.
 */
public interface EditDistanceFunction extends BiFunction<String, String, List<CharacterOperation>> {

	/**
	 * Computes the minimal number of character operations required
	 * for turning the input string into the desired string.
	 * 
	 * @param from the input string, never {@code null}
	 * @param to the string resulting of applying the character operations to the input string, never {@code null}
	 * @return a list of operations which turn the input string into the desired string, never {@code null}
	 */
	List<CharacterOperation> apply(String from, String to);

}
