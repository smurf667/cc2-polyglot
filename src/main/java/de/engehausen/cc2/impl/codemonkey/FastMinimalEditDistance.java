package de.engehausen.cc2.impl.codemonkey;

import java.util.ArrayList;
import java.util.List;

import de.engehausen.cc2.api.CharacterOperation;
import de.engehausen.cc2.api.EditDistanceFunction;

/**
 * Example implementation for the edit distance function.
 * <p>This implementation uses "dynamic programming" and
 * relies on pre-computed results of smaller sub-problems.
 * If you compare with the naive implementation in
 * {@link MinimalEditDistance} you will recognize the "logical
 * skeleton" of the solution.</p>
 */
public class FastMinimalEditDistance implements EditDistanceFunction {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CharacterOperation> apply(final String from, final String to) {
		return computeCharacterOperations(from, to, from.length(), to.length());
	}

	/**
	 * This uses dynamic programming by storing the results of smaller sub-problems
	 * first, then looking these up to combine results for a bigger problem, which
	 * is combined out of the sub-problem(s).
	 * <p>If you look closely, you will see the recursive approach reflecting in this
	 * solution (the core is to find the least-cost operation for a given problem).</p>
	 *
	 * @param from the string to turn into the...
	 * @param to ...target string
	 * @param fromLength the number of characters to consider from the source string
	 * @param toLength the number of characters to consider from the target string
	 * @return a list of the required character operations to turn the source string
	 * into the target string.
	 */
	private List<CharacterOperation> computeCharacterOperations(final String from, final String to, final int fromLength, final int toLength) {
		@SuppressWarnings("unchecked")
		final List<CharacterOperation>[][] memory = new List[fromLength + 1][toLength + 1];
		// fill table with pre-computed results
		for (int i = 0; i <= fromLength; i++) {
			for (int j = 0; j <= toLength; j++) {
				if (i == 0) {
					// need to fill all chars of target
					final List<CharacterOperation> ops = new ArrayList<>(j);
					for (int idx = j; --idx >= 0; ) {
						ops.add(CharacterOperation.insert(0, to.charAt(idx)));
					}
					memory[i][j] = ops;
				} else if (j == 0) {
					// need to remove all chars of source
					final List<CharacterOperation> ops = new ArrayList<>(i);
					for (int idx = 0; idx < i; idx++) {
						ops.add(CharacterOperation.remove(0));
					}
					memory[i][j] = ops;
				} else if (from.charAt(i - 1) == to.charAt(j - 1)) {
					// no change required, look at sub-problem
					memory[i][j] = memory[i - 1][j - 1];
				} else {
					// a change is required, find the minimum
					final List<CharacterOperation> insert = memory[i][j - 1];
					final List<CharacterOperation> remove = memory[i - 1][j];
					final List<CharacterOperation> replace = memory[i - 1][j - 1];
					final List<CharacterOperation> best = min(insert, remove, replace);
					final List<CharacterOperation> ops = new ArrayList<>(1 + best.size());
					if (best == remove) {
						// remove character
						ops.addAll(remove);
						ops.add(CharacterOperation.remove(j));
					} else if (best == insert) {
						// insert character
						ops.addAll(insert);
						ops.add(CharacterOperation.insert(j - 1, to.charAt(j - 1)));
					} else {
						// replace character
						ops.addAll(replace);
						ops.add(CharacterOperation.replace(j - 1, to.charAt(j - 1)));
					}
					memory[i][j] = ops;
				}
			}
		}
		return memory[fromLength][toLength];
	}

	/**
	 * Returns the smallest list.
	 *
	 * @param a the first list
	 * @param b the second list
	 * @param c the third list
	 * @return the list with the smallest size amongst the given input
	 */
	private List<CharacterOperation> min(final List<CharacterOperation> a, final List<CharacterOperation> b, final List<CharacterOperation> c) {
		List<CharacterOperation> result = a;
		if (b.size() < result.size()) {
			result = b;
		}
		if (c.size() < result.size()) {
			result = c;
		}
		return result;
	}

}
