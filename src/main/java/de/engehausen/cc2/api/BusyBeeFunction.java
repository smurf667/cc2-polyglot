package de.engehausen.cc2.api;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Function to compute a flight path of a bee through a flower field,
 * visiting each flower exactly once within a given time limit.
 */
public interface BusyBeeFunction extends BiFunction<List<Connection>, Integer, List<String>>{

	/**
	 * Computes a path visiting all flowers within the given time limit, if possible.
	 * 
	 * @param connections a list of connections, never {@code null}
	 * @param maxTime the maximal time the result path can take, never {@code null}
	 * @return a list with the flower names of a valid path, or an empty list if no path exists - never {@code null}
	 */
	List<String> apply(List<Connection> connections, Integer maxTime);

}
