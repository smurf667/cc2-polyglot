package de.engehausen.cc2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.engehausen.cc2.api.Connection;
import de.engehausen.cc2.data.BusyBeeData;
import de.engehausen.cc2.data.EditDistanceData;
import de.engehausen.cc2.data.HuffmanData;
import de.engehausen.cc2.data.Inputs;
import de.engehausen.cc2.data.HappySevenData;
import de.engehausen.cc2.data.PancakeFlipperData;
import de.engehausen.cc2.data.Results.Record;
import de.engehausen.cc2.data.ReversePolishNotationData;
import de.engehausen.cc2.data.XmlData;

public class InputsTest {

	private Inputs inputs;
	
	@Before
	public void setup() {
		inputs = new Inputs();
		inputs.editDistances = Collections.singletonList(new EditDistanceData("hello", "hallo", 1));
		inputs.beeGraphs = Collections.singletonList(
			new BusyBeeData(
				Collections.singletonList(new Connection("A", "B", 1)),
				3,
				true)
		);
		inputs.notationExpressions = Collections.singletonList(new ReversePolishNotationData("1 + 2", BigDecimal.valueOf(3)));
		inputs.huffmanStrings = Collections.singletonList(new HuffmanData("hello world", 33));
		inputs.xmlEasy = Collections.singletonList(new XmlData("easy.xml", null, Collections.emptyList()));
		inputs.xmlHard = Collections.singletonList(new XmlData("hard.xml", "a,b,c", Collections.emptyList()));
		inputs.pancakes = Collections.singletonList(
			new PancakeFlipperData(
				Arrays.asList(Integer.valueOf(2), Integer.valueOf(1))
			)
		);
		inputs.happySeven = Collections.singletonList(
			new HappySevenData(
				IntStream
				.range(1, 8)
				.mapToObj( i -> Integer.valueOf(i) )
				.collect(Collectors.toList())
			)
		);
	}

	@Test
	public void testSerialize() throws IOException {
		final String str = inputsToString();
		assertTrue(str.contains("editDistances"));
		assertTrue(str.contains("beeGraphs"));
		assertTrue(str.contains("notationExpressions"));
		assertTrue(str.contains("huffmanStrings"));
		assertTrue(str.contains("xmlEasy"));
		assertTrue(str.contains("xmlHard"));
		assertTrue(str.contains("pancakes"));
		assertTrue(str.contains("happySeven"));
		System.out.println(str);
	}

	@Test
	public void testDeserialize() throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		final String test = "test";
		inputs.editDistances.get(0).results = Collections.singletonMap(test, new Record(null, "=b0+a0", 11));
		final Inputs result = mapper.readValue(inputsToString(), Inputs.class); 
		assertNotNull(result);
		assertNotNull(result.editDistances);
		assertFalse(result.editDistances.isEmpty());
		assertNotNull(result.editDistances.get(0).results);
		assertTrue(result.editDistances.get(0).results.get(test).ok);
		assertNotNull(result.editDistances.get(0).results.get(test).response);
	}

	protected String inputsToString() throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		return mapper.writeValueAsString(inputs); 
	}

}
