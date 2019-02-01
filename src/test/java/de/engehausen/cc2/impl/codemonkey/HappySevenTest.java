package de.engehausen.cc2.impl.codemonkey;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.engehausen.cc2.Executor;
import de.engehausen.cc2.data.HappySevenData;
import de.engehausen.cc2.verifiers.HappySevenVerifier;

/**
 * This tests the JavaScript solution for the Happy Seven challenge.
 * It builds all 5040 possible permutations of the numbers and calls
 * the solver.
 * <p>By the way, 5040 (= 7!) is an interesting <a href="https://www.youtube.com/watch?v=2JM2oImb9Qg">number</a>.</p>
 */
public class HappySevenTest {

	private static ScriptEngine JS;
	private static HappySevenVerifier VERIFIER;
	private static final String REAL_JS = "toRealJavaScript";

	private Info min = new Info(Integer.MAX_VALUE);
	private Info max = new Info(0);

	private final Function<List<Integer>, String> runner = list -> {
		final HappySevenData data = new HappySevenData(list);
		final Invocable js = (Invocable) JS;
		try {
			final Object result = js.invokeFunction("process",
				js.invokeFunction(REAL_JS, data.configuration)
			);
			record(data.configuration, (Bindings) result);
			return VERIFIER.verify(result, data);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	};
	
	@BeforeClass
	public static void init() throws ScriptException, IOException, URISyntaxException {
		if (System.getProperty("nashorn.args") == null) {
			System.setProperty("nashorn.args", "--language=es6");
		}
		JS = new ScriptEngineManager().getEngineByName("nashorn");
		JS.eval("function " + REAL_JS + "(i) { return Java.from(i); }");
		JS.eval(new String(
			Files.readAllBytes(
				Paths.get(
					Executor.class.getResource("/codemonkey/happySeven.js").toURI()
				)
			),
			StandardCharsets.UTF_8
		));
		VERIFIER = new HappySevenVerifier();
	}

	/**
	 * There are only 5040 permutations - try all inputs.
	 * @throws Exception in case of error
	 */
	@Test
	public void testAllPermutations() throws Exception {
		permutate(new int[] { 1, 2, 3, 4, 5, 6, 7 }, 7, runner);
		System.out.printf("smallest: %s -> %s%n", min.in, min.ops);
		System.out.printf("largest : %s -> %s%n", max.in, max.ops);
		Assert.assertTrue("smallest ops list is not empty", min.ops.isEmpty());
	}

	@Test
	public void testSequence1() throws Exception {
		testSequence(Arrays.asList(
			Integer.valueOf(7),
			Integer.valueOf(6),
			Integer.valueOf(5),
			Integer.valueOf(4),
			Integer.valueOf(3),
			Integer.valueOf(2),
			Integer.valueOf(1)
		));
	}

	@Test
	public void testSequence2() throws Exception {
		testSequence(Arrays.asList(
			Integer.valueOf(1),
			Integer.valueOf(2),
			Integer.valueOf(4),
			Integer.valueOf(6),
			Integer.valueOf(7),
			Integer.valueOf(3),
			Integer.valueOf(5)
		));
	}

	private void testSequence(final List<Integer> sequence) throws Exception {
		min = new Info(Integer.MAX_VALUE);
		final String ok = runner.apply(sequence);
		System.out.printf("%s -> %3d %s%n", sequence, Integer.valueOf(min.size), min.ops);
		Assert.assertNull(sequence.toString(), ok);
	}

	private void record(final List<Integer> configuration, final Bindings result) {
		final List<String> ops = result
			.values()
			.stream()
			.map(e -> e.toString())
			.collect(Collectors.toList());
		if (min.size > ops.size()) {
			min.size = ops.size();
			min.ops = ops;
			min.in = configuration;
		} else if (max.size < ops.size()) {
			max.size = ops.size();
			max.ops = ops;
			max.in = configuration;
		}
	}

	// using Heap's algorithm
	private void permutate(final int[] nums, final int len, Function<List<Integer>, String> happySeven) {
		if (len == 0) {
			final List<Integer> sequence = new ArrayList<>(nums.length);
			for (final int i : nums) {
				sequence.add(Integer.valueOf(i));
			}
			Assert.assertNull(Arrays.toString(nums), happySeven.apply(sequence));
			return;
		}
		for (int i = 0; i < len; i++) {
			permutate(nums, len - 1, happySeven);
			swap(nums, len - 1, (len % 2) * i);
		}
	}
	
	private void swap(final int[] a, final int i, final int j) {
		final int t = a[i];
		a[i] = a[j];
		a[j] = t;
	}

	private static class Info {
		public int size;
		public List<Integer> in;
		public List<String> ops;
		public Info(final int initial) {
			size = initial;
		}
	}
}
