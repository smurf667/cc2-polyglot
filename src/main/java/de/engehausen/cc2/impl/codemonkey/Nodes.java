package de.engehausen.cc2.impl.codemonkey;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.engehausen.cc2.api.Connection;

/**
 * This class provides distance information between
 * all nodes of the graph and can supply all node names.
 * <p>The input for the challenge is (on purpose) a bit
 * sub-optimal (a connection list). This class builds a
 * convenient data structure out of the connection list.</p>
 */
public class Nodes {

	/**
	 * Distance matrix for the node of the graph.
	 * The index in matrix corresponds to the index of
	 * the node name in the list of node names.
	 */
	public final int[][] distance;

	/**
	 * The number of connections per node.
	 */
	public final int[] count;

	/** a list with the names of the nodes of the graph */
	public final List<String> names;

	/**
	 * Builds the node information based on the list of
	 * connections
	 * @param connections the connections list
	 */
	public Nodes(final List<Connection> connections) {
		names = buildNames(connections);
		distance = buildMatrix(names, connections);
		count = new int[distance.length];
		for (int i = 0; i < count.length; i++) {
			for (int j = 0; j < count.length; j++) {
				count[i] += distance[i][j] > 0 ? 1 : 0;
			}
		}
	}

	/**
	 * Outputs the distance matrix in human-readable form.
	 * @return a string representing the distance matrix
	 */
	public String toString() {
		final StringBuilder sb = new StringBuilder(128);
		sb.append(names.toString()).append(System.lineSeparator());
		final int max = names.size();
		for (int i = 0; i < max; i++) {
			for (int j = 0; j < max; j++) {
				sb.append(String.format("%5d ", Integer.valueOf(distance[i][j])));
			}
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}

	/**
	 * Builds the list of node names from a connections list.
	 * @param connections the connections list
	 * @return a sorted list of all node names that occured in the connections list
	 */
	private static List<String> buildNames(final List<Connection> connections) {
		final Set<String> names = new HashSet<>();
		for (final Connection connection : connections) {
			names.add(connection.a);
			names.add(connection.b);
		}
		return names
			.stream()
			.sorted()
			.collect(Collectors.toList());
	}

	/**
	 * Builds the distance matrix out of a given connections list.
	 * @param names the node names
	 * @param connections the connections of the graph
	 * @return the distance matrix
	 */
	private static int[][] buildMatrix(final List<String> names, final List<Connection> connections) {
		final int max = names.size();
		final int[][] result = new int[max][max];
		for (final Connection connection : connections) {
			final int a = names.indexOf(connection.a);
			final int b = names.indexOf(connection.b);
			result[a][b] = connection.time;
			result[b][a] = connection.time;
		}
		return result;
	}
}