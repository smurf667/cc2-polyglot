package de.engehausen.cc2.verifiers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.engehausen.cc2.api.Connection;
import de.engehausen.cc2.api.Verifier;
import de.engehausen.cc2.data.BusyBeeData;

/**
 * Verifies busy bee challenge responses.
 */
public class BusyBeeVerifier implements Verifier<List<String>, BusyBeeData> {

	/**
	 * Verifies a busy bee challenge response.
	 * The list of nodes must
	 * <ul>
	 *   <li>not be {@code null}</li>
	 *   <li>only be empty if the empty flag in the data indicates so</li>
	 *   <li>cover all flowers</li>
	 *   <li>not visit any flower twice</li>
	 *   <li>not contain an invalid connection transition</li>
	 *   <li>not exceed the maximal time limit</li>
	 * </ul>
	 * @param nodes the list in which to visit the flowers
	 * @param data the input data
	 * @return {@code null} if verification was successful, a message indicating the failure otherwise.
	 */
	@Override
	public String verify(final List<String> nodes, final BusyBeeData data) {
		if (nodes == null) {
			return "null response";
		}
		if (data.empty) {
			// expecting empty list
			return nodes.isEmpty() ? null : "the list must be empty";
		}
		final Set<String> allNodes = new HashSet<>();
		final Map<String, Integer> distances = new HashMap<>();
		for (final Connection connection : data.connections) {
			allNodes.add(connection.a);
			allNodes.add(connection.b);
			distances.put(connection.a + "." + connection.b, Integer.valueOf(connection.time));
			distances.put(connection.b + "." + connection.a, Integer.valueOf(connection.time));
		}
		if (nodes.size() != allNodes.size()) {
			return "all nodes must be visited";
		}
		allNodes.removeAll(nodes);
		if (!allNodes.isEmpty()) {
			return "the path contained duplicates";
		}
		final Iterator<String> path = nodes.iterator();
		String last = path.next();
		int total = 0;
		while (path.hasNext()) {
			final String to = path.next();
			final Integer step = distances.get(last + "." + to);
			if (step == null) {
				return "illegal connection " + last + " to " + to;
			}
			total += step.intValue();
			last = to;
		}
		return total <= data.maxTime ? null : "not within time limit";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(final List<String> input) {
		return input == null ? null : input.toString();
	}

}
