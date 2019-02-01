package de.engehausen.cc2.impl.codemonkey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.engehausen.cc2.api.CharacterOperation;
import de.engehausen.cc2.api.EditDistanceFunction;

/**
 * Example implementation for the edit distance function.
 * This uses a naive (recursive) solution. According to Wikipedia
 * this has exponential runtime. For larger inputs running this...
 * is not advisable! As an improvement, previous calls are "memoized"
 * to prevent unnecessary computation.
 */
public class MinimalEditDistance implements EditDistanceFunction {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CharacterOperation> apply(final String from, final String to) {
		final List<CharacterOperation> result = new ArrayList<CharacterOperation>();
		collectCharacterOperations(from, to, from.length(), to.length(), result, createMemoMap());
		return result;
	}

	/**
	 * Naive, recursive implementation. This recurses by calling into
	 * a smaller sub-problem. This basically tries all operations and
	 * then chooses one with smallest cost.
	 *
	 * @param from the string to turn into the...
	 * @param to ...target string
	 * @param fromLength the number of characters to consider from the source string
	 * @param toLength the number of characters to consider from the target string
	 * @param ops the character operations required for converting the string
	 * @param memo a map remembering previous steps
	 */
	private void collectCharacterOperations(final String from, final String to,
			final int fromLength, final int toLength, final List<CharacterOperation> ops,
			final Map<String, List<CharacterOperation>> memo) {
		// lazy memo key
		final String key = fromLength + "/" + toLength;
		final List<CharacterOperation> known = memo.get(key);
		if (known != null) {
			ops.addAll(known);
			return;
		}
		if (fromLength == 0) {
			// insert chars of target
			for (int i = toLength; --i >= 0; ) {
				ops.add(CharacterOperation.insert(0, to.charAt(i)));
			}
			memo.put(key, new ArrayList<>(ops));
			return;
		}
		if (toLength == 0) {
			// remove all characters of source
			for (int i = 0; i < fromLength; i++) {
				ops.add(CharacterOperation.remove(0));
			}
			memo.put(key, new ArrayList<>(ops));
			return;
		}
		if (from.charAt(fromLength - 1) == to.charAt(toLength - 1)) {
			// same suffix, do nothing and consider the smaller sub-problem
			collectCharacterOperations(from, to, fromLength - 1, toLength - 1, ops, memo);
			return;
		}
		// a change is required - perform it and look at the smaller sub-problem
		final List<CharacterOperation> insert = new ArrayList<>();
		final List<CharacterOperation> remove = new ArrayList<>();
		final List<CharacterOperation> replace = new ArrayList<>();
		collectCharacterOperations(from, to, fromLength, toLength - 1, insert, memo);
		collectCharacterOperations(from, to, fromLength - 1, toLength, remove, memo);
		collectCharacterOperations(from, to, fromLength - 1, toLength - 1, replace, memo);
		// find the best action
		final List<CharacterOperation> best = min(insert, remove, replace);
		if (best == remove) {
			// remove character
			ops.addAll(remove);
			ops.add(CharacterOperation.remove(toLength));
		} else if (best == insert) {
			// insert character
			ops.addAll(insert);
			ops.add(CharacterOperation.insert(toLength - 1, to.charAt(toLength - 1)));
		} else {
			// replace character
			ops.addAll(replace);
			ops.add(CharacterOperation.replace(toLength - 1, to.charAt(toLength - 1)));
		}
		memo.put(key, new ArrayList<>(ops));
	}

	/**
	 * Returns the smallest list.
	 *
	 * @param a the first list
	 * @param b the second list
	 * @param c the third list
	 * @return the list with the smallest size amongst the given input
	 */
	public List<CharacterOperation> min(final List<CharacterOperation> a, final List<CharacterOperation> b, final List<CharacterOperation> c) {
		List<CharacterOperation> result = a;
		if (b.size() < result.size()) {
			result = b;
		}
		if (c.size() < result.size()) {
			result = c;
		}
		return result;
	}

	/**
	 * Returns a writable map for memoization.
	 * If the system property {@code MAKE_SLOW} is set, the
	 * map does not store anything. You can observe how this
	 * recursive solution degrades massively in performance
	 * @return a writable map, never {@code null}.
	 */
	private Map<String, List<CharacterOperation>> createMemoMap() {
		if (System.getProperty("MAKE_SLOW") != null) {
			return new HashMap<String, List<CharacterOperation>>() {
				private static final long serialVersionUID = 1L;
				@Override
				public List<CharacterOperation> put(String key, List<CharacterOperation> value) {
					// I'm the http://disney.wikia.com/wiki/Dory of hash maps
					// uh, this is going to be slow
					return null;
				}
			};
		}
		return new HashMap<>();
	}

}
