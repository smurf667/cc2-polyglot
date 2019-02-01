package de.engehausen.cc2.impl.codemonkey;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import de.engehausen.cc2.api.BusyBeeFunction;
import de.engehausen.cc2.api.Connection;

/**
 * Example implementation of the busy bee function.
 * "Unfortunately" this problem does not allow for much optimization.
 * In technical terms, the problem is to find a Hamiltonian path
 * through the graph (visit all nodes exactly once). This is a NP-complete
 * problem.
 * <p>This solution goes through all nodes sequentially and performs
 * a recursive depth-first search for a path covering all nodes within the
 * given time limit.</p>
 */
public class BusyBee implements BusyBeeFunction {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> apply(final List<Connection> connections, final Integer maxTime) {
		return naive(new Nodes(connections), maxTime.intValue());
	}

	/**
	 * Busy bee search for a path through all flowers within the given
	 * time limit.
	 * @param nodes the node information of the graph (including distance matrix)
	 * @param maxTime the maximally allowed time for the path.
	 * @return the result list, never {@code null}.
	 */
	protected List<String> naive(final Nodes nodes, final int maxTime) {
		final Deque<Integer> path = new ArrayDeque<>();
		for (int i = 0; i < nodes.distance.length; i++) {
			final Integer start = Integer.valueOf(i);
			path.offer(start);
			if (depthFirstSearch(path, nodes, 0, maxTime)) {
				// build result
				return path
					.stream()
					.map(idx -> nodes.names.get(idx.intValue()))
					.collect(Collectors.toList());
			}
			path.removeLast();
		}
		return Collections.emptyList();
	}

	protected boolean depthFirstSearch(final Deque<Integer> path, final Nodes nodes, final int time, final int maxTime) {
		if (path.size() == nodes.distance.length) {
			// all nodes are on the path
			return true;
		}
		final int i = path.getLast().intValue();
		// look at all neighbors of the last node in the path
		for (int j = 0; j < nodes.distance.length; j++) {
			if (nodes.distance[i][j] > 0) {
				final Integer candidate = Integer.valueOf(j);
				// visit neighbors which lie inside the time constraint
				final int newTime = nodes.distance[i][j] + time;
				if (newTime <= maxTime && !path.contains(candidate)) {
					path.offer(candidate);
					if (depthFirstSearch(path, nodes, newTime, maxTime)) {
						return true;
					}
					path.removeLast();
				}
			}
		}
		return false;
	}

}
