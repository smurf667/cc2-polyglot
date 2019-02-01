package de.engehausen.cc2.verifiers;

import java.util.List;

import de.engehausen.cc2.api.CharacterOperation;
import de.engehausen.cc2.api.Verifier;
import de.engehausen.cc2.data.EditDistanceData;

/**
 * Verifies edit distance challenge responses.
 */
public class EditDistanceVerifier implements Verifier<List<CharacterOperation>, EditDistanceData> {

	/**
	 * Verifies an edit distance challenge response.
	 * The list of character operations must
	 * <ul>
	 *   <li>not be {@code null}</li>
	 *   <li>must produce the target string when applied to the source string</li>
	 *   <li>must have the expected number of operations</li>
	 * </ul>
	 * @param edits the list of character operations
	 * @param data the input data
	 * @return {@code null} if verification was successful, a message indicating the failure otherwise.
	 */
	@Override
	public String verify(final List<CharacterOperation> edits, final EditDistanceData data) {
		if (edits == null) {
			return "null response";
		}
		if (edits.size() != data.distance) {
			return "unexpected number of operations";
		}
		final StringBuilder builder = new StringBuilder(128);
		builder.append(data.from);
		applyOperations(edits, builder);
		if (builder.toString().equals(data.to)) {
			// the operations transform input into the target
			return null;
		}
		return "operations produce unexpected string: " + builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(final List<CharacterOperation> input) {
		if (input != null) {
			final StringBuilder sb = new StringBuilder(128);
			for (CharacterOperation op : input) {
				sb.append(op.toString());
			}
			return sb.toString();
		}
		return null;
	}

	protected void applyOperations(final List<CharacterOperation> operations, final StringBuilder current) {
		try {
			for (final CharacterOperation op : operations) {
				switch (op.type()) {
				case REMOVE:
					current.delete(op.position(), 1 + op.position());
					break;
				case REPLACE:
					current.replace(op.position(), 1 + op.position(), Character.toString(op.character()));
					break;
				case INSERT:
					current.insert(op.position(), op.character());
					break;
				}
			}
		} catch (IndexOutOfBoundsException e) {
			// return the not completely edited string builder...
			return;
		}
	}

}
