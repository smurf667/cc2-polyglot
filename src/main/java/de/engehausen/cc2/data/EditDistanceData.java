package de.engehausen.cc2.data;

/**
 * Input data for the minimal edit distance challenge.
 */
public class EditDistanceData extends Results {

	/** the string to transform */
	public String from;
	/** the target string */
	public String to;
	/** the expected edit distance */
	public int distance;

	/**
	 * Creates the data.
	 * @param from the string to transform
	 * @param to the target string
	 * @param distance the expected edit distance
	 */
	public EditDistanceData(final String from, final String to, final int distance) {
		this.from = from;
		this.to = to;
		this.distance = distance;
	}
	
	public EditDistanceData() {
		this(null, null, 0); // for Jackson
	}

}
