package de.engehausen.cc2.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Data object holding all input data which is used to
 * run the challenge solutions with.
 * This is normally created by reading a JSON file;
 * an example file can be found in {@code src/test/resources/testInputs.json}.
 * There is also a markdown file there with more information.
 */
public class Inputs {

	/** list of edit distance data */
	public List<EditDistanceData> editDistances;
	/** list of busy bee data */
	public List<BusyBeeData> beeGraphs;

	/** list of pancake flipper data */
	public List<PancakeFlipperData> pancakes;
	/** list of happy seven data */
	public List<HappySevenData> happySeven;

	/** list of reverse polish notation data */
	public List<ReversePolishNotationData> notationExpressions;
	/** list of huffman coding data */
	public List<HuffmanData> huffmanStrings;

	/** list of "easy" xml transformation data */
	public List<XmlData> xmlEasy;
	/** list of "hard" xml transformation data */
	public List<XmlData> xmlHard;

	/**
	 * Returns a list of all results
	 * @return a list of all results
	 */
	public List<List<? extends Results>> all() {
		final List<List<? extends Results>> result = new ArrayList<>(8);
		add(editDistances, result);
		add(beeGraphs, result);
		add(pancakes, result);
		add(happySeven, result);
		add(notationExpressions, result);
		add(huffmanStrings, result);
		add(xmlEasy, result);
		add(xmlHard, result);
		return result;
	}

	private void add(final List<? extends Results> list, final List<List<? extends Results>> collector) {
		if (list != null && !list.isEmpty()) {
			collector.add(list);
		}
	}

}
