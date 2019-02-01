package de.engehausen.cc2.data;

import java.util.List;

/**
 * Input data for the pancake flipper challenge.
 */
public class PancakeFlipperData extends Results {

	/** the input stack of pancake sizes */
	public List<Integer> stack;

	/**
	 * Creates the data.
	 * @param stack the input stack of pancake sizes
	 */
	public PancakeFlipperData(final List<Integer> stack) {
		this.stack = stack;
	}

	public PancakeFlipperData() {
		this(null);
	}

}
