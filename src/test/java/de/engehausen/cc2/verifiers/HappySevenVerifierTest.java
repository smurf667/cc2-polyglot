package de.engehausen.cc2.verifiers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.engehausen.cc2.verifiers.HappySevenVerifier.Operation;

public class HappySevenVerifierTest {

	private HappySevenVerifier verifier;
	private List<Integer> sequence;

	@Before
	public void init() {
		verifier = new HappySevenVerifier();
		sequence = IntStream.range(1, 8).mapToObj(Integer::valueOf).collect(Collectors.toList());
	}

	@Test
	public void testAPlus() {
		Assert.assertEquals(Arrays.asList(
			Integer.valueOf(7),
			Integer.valueOf(1),
			Integer.valueOf(2),
			Integer.valueOf(3),
			Integer.valueOf(4),
			Integer.valueOf(5),
			Integer.valueOf(6)
		),
			verifier.applyOperations(sequence, Collections.singletonList(Operation.ALL_CW))
		);
	}

	@Test
	public void testAMinus() {
		Assert.assertEquals(Arrays.asList(
			Integer.valueOf(2),
			Integer.valueOf(3),
			Integer.valueOf(4),
			Integer.valueOf(5),
			Integer.valueOf(6),
			Integer.valueOf(7),
			Integer.valueOf(1)
		),
			verifier.applyOperations(sequence, Collections.singletonList(Operation.ALL_CCW))
		);
	}

	@Test
	public void testLPlus() {
		Assert.assertEquals(Arrays.asList(
			Integer.valueOf(4),
			Integer.valueOf(1),
			Integer.valueOf(2),
			Integer.valueOf(3),
			Integer.valueOf(5),
			Integer.valueOf(6),
			Integer.valueOf(7)
		),
			verifier.applyOperations(sequence, Collections.singletonList(Operation.LEFT_CW))
		);
	}

	@Test
	public void testLMinus() {
		Assert.assertEquals(Arrays.asList(
			Integer.valueOf(2),
			Integer.valueOf(3),
			Integer.valueOf(4),
			Integer.valueOf(1),
			Integer.valueOf(5),
			Integer.valueOf(6),
			Integer.valueOf(7)
		),
			verifier.applyOperations(sequence, Collections.singletonList(Operation.LEFT_CCW))
		);
	}

	@Test
	public void testRPlus() {
		Assert.assertEquals(Arrays.asList(
			Integer.valueOf(1),
			Integer.valueOf(2),
			Integer.valueOf(3),
			Integer.valueOf(7),
			Integer.valueOf(4),
			Integer.valueOf(5),
			Integer.valueOf(6)
		),
			verifier.applyOperations(sequence, Collections.singletonList(Operation.RIGHT_CW))
		);
	}

	@Test
	public void testRMinus() {
		Assert.assertEquals(Arrays.asList(
			Integer.valueOf(1),
			Integer.valueOf(2),
			Integer.valueOf(3),
			Integer.valueOf(5),
			Integer.valueOf(6),
			Integer.valueOf(7),
			Integer.valueOf(4)
		),
			verifier.applyOperations(sequence, Collections.singletonList(Operation.RIGHT_CCW))
		);
	}

}
