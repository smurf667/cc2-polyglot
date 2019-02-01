package de.engehausen.cc2.data;

import java.util.List;

import de.engehausen.cc2.api.Connection;

/**
 * Input data for the busy bee challenge.
 */
public class BusyBeeData extends Results {

	/** a list of the connections between the flowers */
	public List<Connection> connections;
	/** the maximally allowed time to visit all flowers */
	public int maxTime;
	/** a flag indicating whether or not an empty list is
	 * expected as the response or not */
	public boolean empty;

	/**
	 * Creates the data.
	 * @param connections a list of the connections between the flowers
	 * @param maxTime the maximally allowed time to visit all flowers
	 * @param empty flag indicating that the response is expected to be empty
	 */
	public BusyBeeData(final List<Connection> connections, final int maxTime, final boolean empty) {
		this.connections = connections;
		this.maxTime = maxTime;
		this.empty = empty;
	}

	public BusyBeeData() {
		this(null, 0, false); // for Jackson
	}

}
