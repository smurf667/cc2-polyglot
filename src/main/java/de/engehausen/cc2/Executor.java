package de.engehausen.cc2;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.armedbear.lisp.Cons;
import org.armedbear.lisp.Interpreter;
import org.armedbear.lisp.LispObject;
import org.armedbear.lisp.Packages;
import org.armedbear.lisp.SimpleString;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import de.engehausen.cc2.api.BusyBeeFunction;
import de.engehausen.cc2.api.CharacterOperation;
import de.engehausen.cc2.api.Contribution;
import de.engehausen.cc2.api.EditDistanceFunction;
import de.engehausen.cc2.api.Verifier;
import de.engehausen.cc2.data.BusyBeeData;
import de.engehausen.cc2.data.EditDistanceData;
import de.engehausen.cc2.data.HappySevenData;
import de.engehausen.cc2.data.HuffmanData;
import de.engehausen.cc2.data.Inputs;
import de.engehausen.cc2.data.PancakeFlipperData;
import de.engehausen.cc2.data.Results;
import de.engehausen.cc2.data.Results.Record;
import de.engehausen.cc2.data.ReversePolishNotationData;
import de.engehausen.cc2.data.XmlData;
import de.engehausen.cc2.verifiers.BusyBeeVerifier;
import de.engehausen.cc2.verifiers.EditDistanceVerifier;
import de.engehausen.cc2.verifiers.HappySevenVerifier;
import de.engehausen.cc2.verifiers.HuffmanVerifier;
import de.engehausen.cc2.verifiers.PancakeVerifier;
import de.engehausen.cc2.verifiers.ReversePolishNotationVerifier;
import de.engehausen.cc2.verifiers.XmlVerifier;

/**
 * Runs challenge solution contributions and records the results.
 */
public class Executor implements Closeable {

	protected static final String PKG_PREFIX = Executor.class.getPackage().getName() + ".impl.";

	private static final String LISP_FUNCTION = "PROCESS";
	private static final String LISP_USER_SPACE = "CL-USER";
	private static final String REAL_JS = "toRealJavaScript";
	private static final String FLAGS = "flags";
	private static final Cons EMPTY_CONS = new Cons(new LispObject());

	private final Interpreter lispInterpreter;
	private final ScriptEngine javaScript;

	private final Inputs inputs;
	
	private final Verifier<List<String>, BusyBeeData> busyBeeVerifier;
	private final Verifier<List<CharacterOperation>, EditDistanceData> editDistanceVerifier;
	private final Verifier<LispObject, HuffmanData> huffmanVerifier;
	private final Verifier<LispObject, ReversePolishNotationData> polishNotationVerifier;
	private final Verifier<Document, XmlData> xmlVerifier;
	private final Verifier<Object, PancakeFlipperData> pancakeVerifier;
	private final Verifier<Object, HappySevenData> happySevenVerifier;
	
	private final Path tempFile;
	private final XsltTransformer xsltTransformer;

	/**
	 * Creates the executor for the given inputs.
	 * @param inputs the inputs to run the challenges with, must not be {@code null}.
	 */
	public Executor(final Inputs inputs) {
		this(
			inputs,
			Interpreter.createInstance(),
			new XsltTransformer(),
			new BusyBeeVerifier(),
			new EditDistanceVerifier(),
			new HuffmanVerifier(),
			new ReversePolishNotationVerifier(),
			new XmlVerifier(),
			new PancakeVerifier(),
			new HappySevenVerifier()
		);
	}

	/**
	 * Stops the executor and frees resources it held.
	 */
	@Override
	public void close() {
		if (lispInterpreter != null) {
			lispInterpreter.dispose();
		}
	}

	/**
	 * Runs all contributions in the {@code de.engehausen.cc2.impl} sub-packages.
	 * @throws IOException in case of error
	 */
	public void runAll() throws IOException {
		run(info -> info.getName().startsWith(PKG_PREFIX));
	}

	/**
	 * Creates the executor with the given components.
	 * @param inputs the inputs to run the challenges with, must not be {@code null}.
	 * @param interpreter the interpreter to use
	 * @param xsltTransformer the XSLT transformer to use
	 * @param busyBeeVerifier the busy bee verifier
	 * @param editDistanceVerifier the minimal edit distance verifier
	 * @param huffmanVerifier the Huffman coding verifier
	 * @param polishNotationVerifier the reverse polish notation verifier
	 * @param xmlVerifier the XML verifier
	 * @param pancakeVerifier the verifier for the pancake flipper
	 * @param happySevenVerifier the verifier for the happy seven challenge
	 */
	protected Executor(
		final Inputs inputs,
		final Interpreter interpreter,
		final XsltTransformer xsltTransformer,
		final Verifier<List<String>, BusyBeeData> busyBeeVerifier,
		final Verifier<List<CharacterOperation>, EditDistanceData> editDistanceVerifier,
		final Verifier<LispObject, HuffmanData> huffmanVerifier,
		final Verifier<LispObject, ReversePolishNotationData> polishNotationVerifier,
		final Verifier<Document, XmlData> xmlVerifier,
		final Verifier<Object, PancakeFlipperData> pancakeVerifier,
		final Verifier<Object, HappySevenData> happySevenVerifier) {
		this.inputs = inputs;
		this.lispInterpreter = interpreter;
		this.xsltTransformer = xsltTransformer;
		this.busyBeeVerifier = busyBeeVerifier;
		this.editDistanceVerifier = editDistanceVerifier;
		this.huffmanVerifier = huffmanVerifier;
		this.polishNotationVerifier = polishNotationVerifier;
		this.xmlVerifier = xmlVerifier;
		this.pancakeVerifier = pancakeVerifier;
		this.happySevenVerifier = happySevenVerifier;
		try {
			if (System.getProperty("nashorn.args") == null) {
				System.setProperty("nashorn.args", "--language=es6");
			}
			javaScript = new ScriptEngineManager().getEngineByName("nashorn");
			javaScript.eval("function " + REAL_JS + "(i) { return Java.from(i); }");
			tempFile = ensureLispScript();
			runLisp("/abcl.lisp");
		} catch (ScriptException|IOException e) {
			throw new IllegalStateException(e);
		}
	}

	
	private static Path ensureLispScript() {
		try {
			final Path userDir = FileSystems
				.getDefault()
				.getPath(".")
				.normalize()
				.toAbsolutePath();
			final Path target = userDir.resolve("target");
			if (Files.notExists(target)) {
				Files.createDirectory(target);
			}
			final Path tempFile = target.resolve("script.lisp");
			Runtime.getRuntime().addShutdownHook(new Thread( () -> {
				try {
					Files.delete(tempFile);
				} catch (IOException e) {
					// just give up...
				}
			}));
			return tempFile;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Runs the contributions matching the given class filter.
	 * @param classFilter the filter to use for class information.
	 * @throws IOException in case of error
	 */
	protected void run(final Predicate<? super ClassInfo> classFilter) throws IOException {
		ClassPath
			.from(Thread.currentThread().getContextClassLoader())
			.getTopLevelClasses()
			.stream()
			.filter(classFilter)
			.forEach(this::run);
	}

	protected LispObject runLisp(final String scriptName) throws IOException {
		try (final InputStream stream = Executor.class.getResourceAsStream(scriptName)) {
			Files.copy(
				stream, 
				tempFile, 
				StandardCopyOption.REPLACE_EXISTING);
			final String instr = "(load \"target/script.lisp\")";
			return lispInterpreter.eval(instr); 
		}
	}
	/**
	 * Reads the given resource from the classpath and returns
	 * the contents as a String, using UTF-8 encoding.
	 * @param name the resource name, e.g. {@code /examples/rpn.lisp}.
	 * @return the contents of the resource
	 * @throws IOException in case of error
	 * @throws URISyntaxException in case of error
	 */
	protected String readFromClasspath(final String name) throws IOException, URISyntaxException {
		return new String(
			Files.readAllBytes(
				Paths.get(
					Executor.class.getResource(name).toURI()
				)
			),
			StandardCharsets.UTF_8
		);
	}

	/**
	 * Executes the contributions defined for the given class, if any.
	 * @param info the class information, must not be {@code null}.
	 */
	protected void run(final ClassInfo info) {
		final Class<?> clz = info.load();
		final Optional<Contribution> candidate = Optional.ofNullable(clz.getAnnotation(Contribution.class));
		if (candidate.isPresent()) {
			final Contribution contribution = candidate.get();
			final String name = getName(clz);
			// Java
			newInstance(contribution.minimalEditDistance(), EditDistanceFunction.class)
				.ifPresent(impl -> processEditDistance(name, impl));
			newInstance(contribution.busyBee(), BusyBeeFunction.class)
				.ifPresent(impl -> processBusyBee(name, impl));
			// JavaScript
			fileInfo(contribution.pancakeFlipper())
				.ifPresent(scriptFile -> processPancakes(name, scriptFile));
			fileInfo(contribution.happySeven())
				.ifPresent(scriptFile -> processHappySeven(name, scriptFile));
			// Lisp
			fileInfo(contribution.reversePolishNotation())
				.ifPresent(lispFile -> processPolishNotation(name, lispFile));
			fileInfo(contribution.huffmanCoding())
				.ifPresent(lispFile ->processHuffmanCoding(name, lispFile));
			// XSLT
			fileInfo(contribution.xsltEasy())
				.ifPresent(xslt -> processXml(name, xslt, i -> i.xmlEasy));
			fileInfo(contribution.xsltHard())
				.ifPresent(xslt -> processXml(name, xslt, i -> i.xmlHard));
		}
	}

	protected String getName(final Class<?> clz) {
		return clz.getPackage().getName().replace(PKG_PREFIX, "");
	}

	protected void processEditDistance(final String name, final EditDistanceFunction function) {
		for (final EditDistanceData data : inputs.editDistances) {
			timedExecution(name, data, () -> {
				final List<CharacterOperation> edits = function.apply(data.from, data.to);
				return putRecord(name, data, new Record(editDistanceVerifier.verify(edits, data), editDistanceVerifier.toString(edits), 0));
			});
		}
	}

	protected void processBusyBee(final String name, final BusyBeeFunction function) {
		for (final BusyBeeData data : inputs.beeGraphs) {
			timedExecution(name, data, () -> {
				final List<String> nodes = function.apply(data.connections, Integer.valueOf(data.maxTime));
				return putRecord(name, data, new Record(busyBeeVerifier.verify(nodes, data), busyBeeVerifier.toString(nodes), 0));
			});
		}
	}

	protected void processPolishNotation(final String name, final String resource) {
		try {
			runLisp(resource);
			final LispObject function = Packages
				.findPackage(LISP_USER_SPACE)
				.findAccessibleSymbol(LISP_FUNCTION)
				.getSymbolFunction();
			for (final ReversePolishNotationData data : inputs.notationExpressions) {
				final Cons expressionList = Stream
					.of(data.expression.split(" "))
					.filter(s -> s.length() > 0)
					.map( s ->  new Cons(new SimpleString(s)) )
					.reduce(EMPTY_CONS, (a, b) -> {
						if (a != EMPTY_CONS) {
							Cons last = a;
							while (last.cdr() instanceof Cons) {
								last = (Cons) last.cdr;
							}
							last.setCdr(b);
							return a;
						}
						return b;
					});
				timedExecution(name, data, () -> {
					final LispObject result = function.execute(expressionList);
					return putRecord(name, data, new Record(polishNotationVerifier.verify(result, data), polishNotationVerifier.toString(result), 0));
				});
			}
			
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	protected void processHuffmanCoding(final String name, final String resource) {
		try {
			runLisp(resource);
			final LispObject function = Packages
				.findPackage(LISP_USER_SPACE)
				.findAccessibleSymbol(LISP_FUNCTION)
				.getSymbolFunction();
			for (final HuffmanData data : inputs.huffmanStrings) {
				timedExecution(name, data, () -> {
					final LispObject result = function.execute(new SimpleString(data.text));
					return putRecord(name, data, new Record(huffmanVerifier.verify(result, data), huffmanVerifier.toString(result), 0));
				});
			}
			
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	protected void processXml(final String name, final String xsltResource, final Function<Inputs, List<XmlData>> supplier) {
		try {
			final Transformer transformer = xsltTransformer.newTransformer(Executor.class.getResourceAsStream(xsltResource));
			for (final XmlData data : supplier.apply(inputs)) {
				final InputStream xmlIn = Executor.class.getResourceAsStream(data.xmlIn);
				if (xmlIn != null) {
					try {
						final Document input = xsltTransformer.docBuilder.parse(
							new InputSource(xmlIn)
						);
						if (data.parameter != null) {
							transformer.setParameter(FLAGS, data.parameter);
						}
						timedExecution(name, data, () -> {
							final Document output = xsltTransformer.docBuilder.newDocument();
							transformer.transform(new DOMSource(input), new DOMResult(output));
							return putRecord(name, data, new Record(xmlVerifier.verify(output, data), xmlVerifier.toString(output), 0));
						});
					} catch (SAXParseException e) {
						throw new IOException(data.xmlIn, e);
					}
				}
			}
		} catch (TransformerException | SAXException | IOException e) {
			throw new IllegalStateException(e);
		}
	}

	protected void processPancakes(final String name, final String jsFile) {
		try {
			final String script = readFromClasspath(jsFile);
			javaScript.eval(script); // how many scripts can it take?
			 
			for (final PancakeFlipperData data : inputs.pancakes) {
				timedExecution(name, data, () -> {
					final Object result = ((Invocable) javaScript).invokeFunction("process",
						((Invocable) javaScript).invokeFunction(REAL_JS, data.stack)
					);
					return putRecord(name, data, new Record(pancakeVerifier.verify(result, data), pancakeVerifier.toString(result), 0));
				});
			}
			
		} catch (IOException | URISyntaxException | ScriptException e) {
			throw new IllegalStateException(e);
		}
	}

	protected void processHappySeven(final String name, final String jsFile) {
		try {
			final String script = readFromClasspath(jsFile);
			javaScript.eval(script); // how many scripts can it take?

			for (final HappySevenData data : inputs.happySeven) {
				timedExecution(name, data, () -> {
					final Object result = ((Invocable) javaScript).invokeFunction("process",
						((Invocable) javaScript).invokeFunction(REAL_JS, data.configuration)
					);
					return putRecord(name, data, new Record(happySevenVerifier.verify(result, data), happySevenVerifier.toString(result), 0));
				});
			}

		} catch (IOException | URISyntaxException | ScriptException e) {
			throw new IllegalStateException(e);
		}
	}

	protected <T> Optional<T> newInstance(final Class<T> clz, final Class<?> root) {
		if (clz != null && clz != root) {
			try {
				return Optional.of(clz.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}
		return Optional.empty();
	}
	
	
	protected Optional<String> fileInfo(final String name) {
		if ("".equals(name)) {
			return Optional.empty();
		}
		return Optional.of(name);
	}

	protected void timedExecution(final String name, final Results results, final Callable<Record> func) {
		// best of ten...
		Record result = null;
		long min = Long.MAX_VALUE;
		for (int i = 0; i < 10; i++) {
			final long then = System.nanoTime();
			try {
				result = func.call();
				final long now = System.nanoTime() - then;
				if (now < min) {
					min = now;
				}
			} catch (Throwable t) {
				final String message = t.getMessage() == null ? t.getClass().getName() : t.getMessage();
				final Record failure = new Record(message, null, System.nanoTime() - then);
				failure.ok = false;
				putRecord(name, results, failure);
				return;
			}
		}
		if (result != null) {
			result.millis = min / 1000000d;
		}
	}

	protected Record putRecord(final String name, final Results results, final Record record) {
		if (results.results == null) {
			results.results = new HashMap<>();
		}
		results.results.put(name, record);
		return record;
	}

}