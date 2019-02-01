package de.engehausen.cc2.api;

/**
 * Represents a connection between two flowers of a field.
 * Curiously, the flowers have names...
 * They are uniquely identified by their name.
 */
public class Connection {

	/** the name of the first node of the connection */
	public String a;
	/** the name of the second node of the connection */
	public String b;
	/** travel time between the nodes */
	public int time;

	/**
	 * Creates a connection.
	 * @param a the name of the first flower
	 * @param b the name of the second flower
	 * @param time the bee travel time between the nodes
	 */
	public Connection(final String a, final String b, final int time) {
		this.a = a;
		this.b = b;
		this.time = time;
	}

	public Connection() {
		this(null, null, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 101;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		result = prime * result + time;
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		final Connection other = (Connection) obj;
		if (a == null) {
			if (other.a != null) {
				return false;
			}
		} else if (!a.equals(other.a)) {
			return false;
		}
		if (b == null) { 
			if (other.b != null) {
				return false;
			}
		} else if (!b.equals(other.b)) {
			return false;
		}
		return time == other.time;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("%s -> %s (%d)", a, b, Integer.valueOf(time));
	}

}
