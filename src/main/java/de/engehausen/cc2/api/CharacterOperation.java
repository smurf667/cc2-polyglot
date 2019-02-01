package de.engehausen.cc2.api;

/**
 * Represents a string manipulation operation.
 */
public final class CharacterOperation {

	/**
	 * Returns an insertion operation.
	 * @param position the position at which to insert
	 * @param c the character to insert
	 * @return the operation
	 */
	public static CharacterOperation insert(final int position, final char c) {
		return new CharacterOperation(CharacterOperation.Type.INSERT, position, c);
	}

	/**
	 * Returns a replacement operation.
	 * @param position the position at which to replace the character
	 * @param c the character to replace with
	 * @return the operation
	 */
	public static CharacterOperation replace(final int position, final char c) {
		return new CharacterOperation(CharacterOperation.Type.REPLACE, position, c);
	}

	/**
	 * Returns a removal operation.
	 * @param position the position at which to remove the character
	 * @return the operation
	 */
	public static CharacterOperation remove(final int position) {
		return new CharacterOperation(CharacterOperation.Type.REMOVE, position, ' ');
	}

	/**
	 * Operation type.
	 */
	public enum Type {
		/** inserts a given character */
		INSERT("+%c%d"),
		/** removes a character */
		REMOVE("-%d"),
		/** replaces a character */
		REPLACE("=%c%d");
		
		private final String pattern;
		
		Type(final String pattern) {
			this.pattern = pattern;
		}

		/** returns a pattern for representing the operation as a string */
		String pattern() {
			return pattern;
		}
	}

	private final CharacterOperation.Type type;
	private final int position;
	private final char c;

	/**
	 * Creates the operation
	 * @param type the type of operation
	 * @param position the position inside the string to manipulat
	 * @param c the character of the operation
	 */
	CharacterOperation(final CharacterOperation.Type type, final int position, final char c) {
		this.type = type;
		this.position = position;
		this.c = c;
	}

	/**
	 * Returns the type of operation.
	 * @return the type of operation
	 */
	public Type type() {
		return type;
	}

	/**
	 * Returns the position of the operation in the string to manipulate.
	 * @return the position of the operation in the string to manipulate.
	 */
	public int position() {
		return position;
	}

	/**
	 * Returns the character of the operation.
	 * @return the character of the operation.
	 */
	public char character() {
		return c;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return Type.REMOVE.equals(type) ?
			String.format(type.pattern(), Integer.valueOf(position)) :
			String.format(type.pattern(), Character.valueOf(c), Integer.valueOf(position));
	}

}