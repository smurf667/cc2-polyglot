package de.engehausen.cc2.data;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Records challenge results.
 */
public abstract class Results {

	/**
	 * Mapping of participant name to result record.
	 */
	@JsonInclude(Include.NON_NULL)
	public Map<String, Record> results;
	/**
	 * Name of the challenge scenario.
	 */
	@JsonInclude(Include.NON_NULL)
	public String label;

	/**
	 * A result record.
	 */
	public static class Record {

		/** success flag */
		public boolean ok;
		/** execution time in milliseconds */
		public double millis;
		/** failure reason */
		@JsonInclude(Include.NON_NULL)
		public String reason;
		/** challenge solution response in string form */
		@JsonInclude(Include.NON_NULL)
		public String response;

		/**
		 * Creates the result record.
		 * @param reason the failure reason ({@code null} if no failure)
		 * @param response the response to the challenge in string form
		 * @param nanos execution time in nanoseconds
		 */
		public Record(final String reason, final String response, final long nanos) {
			this.ok = reason == null;
			this.response = response;
			this.reason = reason;
			this.millis = nanos / 1000000d;
		}

		public Record() {
			this(null, null, 0); // for Jackson
		}

	}

}
