package de.engehausen.cc2.data;

/**
 * Input data for the huffman coding challenge.
 */
public class HuffmanData extends Results {

	/** the text to encode */
	public String text;
	/** the expected number of bits for the encoded data */
	public int bitcount;

	/**
	 * Creates the data.
	 * @param text the text to encode 
	 * @param bitcount the expected number of bits for the encoded data
	 */
	public HuffmanData(final String text, final int bitcount) {
		this.text = text;
		this.bitcount = bitcount;
	}
	
	public HuffmanData() {
		this(null, 0);
	}

}
