package de.engehausen.cc2.data;

import java.util.List;

/**
 * Input data for the happy seven challenge.
 */
public class HappySevenData extends Results {

	/** a list with the input configuration - a permutation of the numbers one to seven */
	public List<Integer> configuration;

	/**
	 * Creates the data.
	 * @param configuration a list with the input configuration
	 */
	public HappySevenData(final List<Integer> configuration) {
		this.configuration = configuration;
	}

	public HappySevenData() {
		this(null);
	}

}
