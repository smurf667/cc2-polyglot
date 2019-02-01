package de.engehausen.cc2.impl.codemonkey;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.engehausen.cc2.api.BusyBeeFunction;
import de.engehausen.cc2.api.Connection;

/**
 * Example implementation of the busy bee function.
 * Why call it "fast", what is "fast"? Not much can be done
 * compared to the straight-forward implementation of {@link BusyBee}.
 * This implementation uses a specialized FIFO queue and does not
 * use recursion.
 * <p>In fact, for small inputs, the recursive implementation (see above)
 * seems faster. There are measurable improvements for larger graphs with
 * this implementation. However, time complexity is the same for both
 * implementations.</p>.
 */
public class FastBusyBee implements BusyBeeFunction {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> apply(final List<Connection> connections, final Integer maxTime) {
		return findPath(new Nodes(connections), maxTime.intValue());
	}

	/**
	 * Busy bee search for a path through all flowers within the given
	 * time limit.
	 * @param nodes the node information of the graph (including distance matrix)
	 * @param maxTime the maximally allowed time for the path.
	 * @return the result list, never {@code null}.
	 */
	protected List<String> findPath(final Nodes nodes, final int maxTime) {
		for (int i = 0; i < nodes.distance.length; i++) {
			final List<String> path = depthFirstSearch(i, nodes, maxTime);
			if (path.size() == nodes.distance.length) {
				return path;
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Performs a non-recursive depth-first search for a valid path.
	 * @param start the start node
	 * @param nodes all nodes
	 * @param maxTime the maximally allowed time for the path
	 * @return a list with node names, never {@code null} (may be empty)
	 */
	protected List<String> depthFirstSearch(final int start, final Nodes nodes, final int maxTime) {
		final Map<Integer, FastIntFIFO> candidates = new HashMap<>();
		final UniqueIntFIFO path = new UniqueIntFIFO(nodes.distance.length);
		int time = 0;
		// begin the path at the given node
		visit(Integer.valueOf(start), nodes, path, time, maxTime, candidates);
		while (path.size() < nodes.distance.length && !candidates.isEmpty()) {
			final Integer currentNode = Integer.valueOf(path.get());
			final FastIntFIFO currentCandidates = candidates.get(currentNode);
			if (currentCandidates.hasElements()) {
				time += visit(Integer.valueOf(currentCandidates.remove()), nodes, path, time, maxTime, candidates);
			} else {
				candidates.remove(currentNode);
				final int last = path.remove();
				if (path.hasElements()) {
					// undo the cost
					time -= nodes.distance[last][path.get()];
				}
			}
		}
		if (path.size() == nodes.distance.length) {
			// found a valid path
			return path
				.stream()
				.mapToObj( i -> nodes.names.get(i))
				.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	/**
	 * Visits a node. This updates the tracking data structures (the
	 * path, the candidate nodes).
	 *
	 * @param node the node (index) to visit
	 * @param nodes all nodes
	 * @param path the current path
	 * @param time the current time
	 * @param maxTime the maximally allowed time for the path
	 * @param candidates a map per node of all candidates to visit
	 * @return the transition time to the given node (zero for start node)
	 */
	protected int visit(final Integer node, final Nodes nodes, final UniqueIntFIFO path, final int time, final int maxTime, final Map<Integer, FastIntFIFO> candidates) {
		final int result;
		final int i = node.intValue();
		if (path.hasElements()) {
			// transition cost check - if visiting this node
			// exceeds the time limit, return
			result = nodes.distance[i][path.get()];
			if (result + time > maxTime) {
				return 0;
			}
		} else {
			result = 0;
		}
		// put the node on the path...
		path.offer(i);
		final FastIntFIFO neighbors = new FastIntFIFO(nodes.count[i]);
		candidates.put(node, neighbors);
		// ...make all neighbors candidates for the next node in the path
		for (int j = 0; j < nodes.distance.length; j++) {
			if (nodes.distance[i][j] > 0) {
				if (!path.contains(j)) {
					neighbors.offer(j);
				}
			}
		}
		return result;
	}

	/**
	 * Optimized {@code int} FIFO queue without sanity checks.
	 * The class is private and the invoker "knows what it is doing".
	 */
	private static class UniqueIntFIFO {

		private boolean[] members;
		private int[] content;
		private int position;

		/**
		 * Creates the FIFO, prepare for values {@code 0..max-1}.
		 * @param max value limit
		 */
		public UniqueIntFIFO(final int max) {
			members = new boolean[max];
			content = new int[max];
			position = -1;
		}

		/**
		 * Indicates whether the FIFO has elements or not.
		 * @return {@code true} if the FIFO contains elements, {@code false} otherwise.
		 */
		public boolean hasElements() {
			return position >= 0;
		}

		/**
		 * Returns the current element in the FIFO.
		 * @return the current element in the FIFO.
		 */
		public int get() {
			return content[position];
		}

		/**
		 * Inserts the given value as the current element into the FIFO.
		 * @param value the value to insert
		 */
		public void offer(final int value) {
			content[++position] = value;
			members[value] = true;
		}

		/**
		 * Removes the current element from the FIFO.
		 * @return the removed element
		 */
		public int remove() {
			final int result = content[position--];
			members[result] = false;
			return result;
		}

		/**
		 * Indicates if the give value is part of the queue.
		 * @param value the value to check
		 * @return {@code true} if the queue contains the value, {@code false} otherwise.
		 */
		public boolean contains(final int value) {
			return members[value];
		}

		/**
		 * Returns the size of the queue.
		 * @return the size of the queue.
		 */
		public int size() {
			return position + 1;
		}

		/**
		 * Returns a stream over the elements of the queue.
		 * @return a stream over the elements of the queue, never {@code null}.
		 */
		public IntStream stream() {
			return Arrays.stream(content, 0, position + 1);
		}
	}

	/**
	 * Optimized int FIFO without sanity checks.
	 */
	private static class FastIntFIFO {
		
		private final int[] values;
		private int position = -1;
		
		public FastIntFIFO(final int max) {
			values = new int[max];
		}
		public void offer(final int value) {
			values[++position] = value;
		}
		public int remove() {
			return values[position--];
		}
		public boolean hasElements() {
			return position >= 0;
		}
	}

}
