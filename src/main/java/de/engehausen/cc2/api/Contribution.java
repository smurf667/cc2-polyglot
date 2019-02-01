package de.engehausen.cc2.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation specifies the solutions for the coding
 * challenges.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PACKAGE)
public @interface Contribution {

	/**
	 * One or more authors of the contribution.
	 * @return an array with the names of the authors of the contribution.
	 */
	String[] authors();

	/**
	 * Returns the absolute resource name to the Lisp
	 * script for the "Reverse Polish Notation" challenge.
	 * The resource must be on the classpath.
	 * @return the resource name, e.g. {@code /codemonkey/rpn.lisp}.
	 */
	String reversePolishNotation() default "";

	/**
	 * Returns the absolute resource name to the Lisp
	 * script for the "Huffman Coding" challenge.
	 * The resource must be on the classpath.
	 * @return the resource name, e.g. {@code /codemonkey/huffman.lisp}.
	 */
	String huffmanCoding() default "";

	/**
	 * Returns the absolute resource name to the XSL transformation
	 * for the "XSLT Easy" challenge.
	 * The resource must be on the classpath.
	 * @return the resource name, e.g. {@code /codemonkey/easy.xslt}.
	 */
	String xsltEasy() default "";

	/**
	 * Returns the absolute resource name to the XSL transformation
	 * for the "XSLT Hard" challenge.
	 * The resource must be on the classpath.
	 * @return the resource name, e.g. {@code /codemonkey/hard.xslt}.
	 */
	String xsltHard() default "";

	/**
	 * Returns the absolute resource name to the JavaScript file
	 * for the "Pancake Flipper" challenge.
	 * The resource must be on the classpath.
	 * @return the resource name, e.g. {@code /codemonkey/flipper.js}.
	 */
	String pancakeFlipper() default "";

	/**
	 * Returns the absolute resource name to the JavaScript file
	 * for the "Happy Seven" challenge.
	 * The resource must be on the classpath.
	 * @return the resource name, e.g. {@code /codemonkey/happy7.js}.
	 */
	String happySeven() default "";

	/**
	 * Returns the class implementing the "Minimal Edit Distance" challenge.
	 * @return the class implementing the "Minimal Edit Distance" challenge.
	 */
	Class<? extends EditDistanceFunction> minimalEditDistance() default EditDistanceFunction.class;

	/**
	 * Returns the class implementing the "Busy Bee" challenge.
	 * @return the class implementing the "Busy Bee" challenge.
	 */
	Class<? extends BusyBeeFunction> busyBee() default BusyBeeFunction.class;

}
