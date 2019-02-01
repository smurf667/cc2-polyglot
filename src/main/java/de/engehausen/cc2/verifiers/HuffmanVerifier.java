package de.engehausen.cc2.verifiers;

import java.util.HashMap;
import java.util.Map;

import org.armedbear.lisp.Cons;
import org.armedbear.lisp.LispObject;

import de.engehausen.cc2.api.Verifier;
import de.engehausen.cc2.data.HuffmanData;

/**
 * Verifies huffman coding challenge responses.
 */
public class HuffmanVerifier implements Verifier<LispObject, HuffmanData> {

	/**
	 * Verifies a happy seven challenge response.
	 * The response must
	 * <ul>
	 *   <li>be in the expected, documented format</li>
	 *   <li>have an encoded binary string not larger than the expected size</li>
	 *   <li>result in the input string when decoded</li>
	 * </ul>
	 * @param result the challenge response
	 * @param data the input data
	 * @return {@code null} if verification was successful, a message indicating the failure otherwise.
	 */
	@Override
	public String verify(final LispObject result, final HuffmanData data) {
		if (result instanceof Cons) {
			final Map<String, String> codes = new HashMap<>();
			LispObject current = ((Cons) result).car();
			while (current.car() instanceof Cons) {
				final LispObject pair = current.car();
				codes.put(pair.cdr().getStringValue(), pair.car().getStringValue());
				current = current.cdr();
			}
			final String coding = result.cdr().getStringValue();
			final int len = coding.length();
			if (len <= data.bitcount) {
				final StringBuilder prefix = new StringBuilder(128);
				final StringBuilder decoded = new StringBuilder(data.text.length());
				for (int i = 0; i < len; i++) {
					prefix.append(coding.charAt(i));
					final String character = codes.get(prefix.toString());
					if (character != null) {
						decoded.append(character);
						prefix.setLength(0);
					}
				}
				return decoded.toString().equals(data.text) ? null : "the decoded string is not the input: " + decoded.toString();
			} else {
				return "unexpected bit count: " + len;
			}
		}
		return "unexpected: " + (result != null ? result.printObject() : "null");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(final LispObject input) {
		return input == null ? null : input.printObject();
	}

}
