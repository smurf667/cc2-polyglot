package de.engehausen.cc2;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Hamilton {
/*
	private static class Jan {
		public List<Connection> c;
	}
	public int cost;
	public static void main(String[] args) throws Throwable {
//		final String json = "{\"c\":[{\"a\":\"n17\",\"b\":\"n18\",\"time\":67},{\"a\":\"n0\",\"b\":\"n1\",\"time\":47},{\"a\":\"n6\",\"b\":\"n15\",\"time\":81},{\"a\":\"n15\",\"b\":\"n17\",\"time\":125},{\"a\":\"n11\",\"b\":\"n13\",\"time\":117},{\"a\":\"n10\",\"b\":\"n15\",\"time\":89},{\"a\":\"n11\",\"b\":\"n19\",\"time\":58},{\"a\":\"n6\",\"b\":\"n9\",\"time\":105},{\"a\":\"n9\",\"b\":\"n14\",\"time\":96},{\"a\":\"n5\",\"b\":\"n29\",\"time\":141},{\"a\":\"n12\",\"b\":\"n19\",\"time\":86},{\"a\":\"n22\",\"b\":\"n29\",\"time\":71},{\"a\":\"n12\",\"b\":\"n26\",\"time\":123},{\"a\":\"n16\",\"b\":\"n25\",\"time\":129},{\"a\":\"n8\",\"b\":\"n23\",\"time\":151},{\"a\":\"n3\",\"b\":\"n24\",\"time\":131},{\"a\":\"n0\",\"b\":\"n4\",\"time\":65},{\"a\":\"n0\",\"b\":\"n12\",\"time\":52},{\"a\":\"n6\",\"b\":\"n26\",\"time\":84},{\"a\":\"n6\",\"b\":\"n13\",\"time\":61},{\"a\":\"n7\",\"b\":\"n15\",\"time\":88},{\"a\":\"n10\",\"b\":\"n18\",\"time\":156},{\"a\":\"n2\",\"b\":\"n10\",\"time\":130},{\"a\":\"n1\",\"b\":\"n15\",\"time\":129},{\"a\":\"n0\",\"b\":\"n10\",\"time\":133},{\"a\":\"n10\",\"b\":\"n22\",\"time\":163},{\"a\":\"n1\",\"b\":\"n14\",\"time\":85},{\"a\":\"n25\",\"b\":\"n30\",\"time\":69},{\"a\":\"n3\",\"b\":\"n16\",\"time\":111},{\"a\":\"n15\",\"b\":\"n28\",\"time\":158},{\"a\":\"n3\",\"b\":\"n22\",\"time\":98},{\"a\":\"n12\",\"b\":\"n25\",\"time\":113},{\"a\":\"n20\",\"b\":\"n22\",\"time\":115},{\"a\":\"n0\",\"b\":\"n11\",\"time\":129},{\"a\":\"n1\",\"b\":\"n25\",\"time\":118},{\"a\":\"n12\",\"b\":\"n24\",\"time\":121},{\"a\":\"n3\",\"b\":\"n27\",\"time\":100},{\"a\":\"n9\",\"b\":\"n24\",\"time\":151},{\"a\":\"n0\",\"b\":\"n23\",\"time\":104},{\"a\":\"n6\",\"b\":\"n17\",\"time\":45},{\"a\":\"n4\",\"b\":\"n10\",\"time\":136},{\"a\":\"n0\",\"b\":\"n5\",\"time\":154},{\"a\":\"n4\",\"b\":\"n30\",\"time\":109},{\"a\":\"n20\",\"b\":\"n21\",\"time\":80},{\"a\":\"n2\",\"b\":\"n7\",\"time\":87},{\"a\":\"n13\",\"b\":\"n18\",\"time\":106},{\"a\":\"n10\",\"b\":\"n14\",\"time\":72},{\"a\":\"n7\",\"b\":\"n12\",\"time\":134},{\"a\":\"n14\",\"b\":\"n19\",\"time\":77},{\"a\":\"n16\",\"b\":\"n28\",\"time\":123},{\"a\":\"n1\",\"b\":\"n23\",\"time\":75},{\"a\":\"n1\",\"b\":\"n26\",\"time\":95},{\"a\":\"n8\",\"b\":\"n17\",\"time\":134},{\"a\":\"n2\",\"b\":\"n21\",\"time\":117},{\"a\":\"n13\",\"b\":\"n26\",\"time\":73}]}";
		final String json = "{\"c\":[{\"a\":\"n4\",\"b\":\"n19\",\"time\":64},{\"a\":\"n9\",\"b\":\"n12\",\"time\":91},{\"a\":\"n17\",\"b\":\"n21\",\"time\":55},{\"a\":\"n7\",\"b\":\"n14\",\"time\":137},{\"a\":\"n12\",\"b\":\"n20\",\"time\":86},{\"a\":\"n8\",\"b\":\"n16\",\"time\":118},{\"a\":\"n2\",\"b\":\"n12\",\"time\":42},{\"a\":\"n13\",\"b\":\"n16\",\"time\":102},{\"a\":\"n3\",\"b\":\"n14\",\"time\":87},{\"a\":\"n5\",\"b\":\"n17\",\"time\":107},{\"a\":\"n15\",\"b\":\"n21\",\"time\":124},{\"a\":\"n4\",\"b\":\"n13\",\"time\":52},{\"a\":\"n1\",\"b\":\"n10\",\"time\":138},{\"a\":\"n0\",\"b\":\"n13\",\"time\":67},{\"a\":\"n11\",\"b\":\"n18\",\"time\":120},{\"a\":\"n3\",\"b\":\"n4\",\"time\":99},{\"a\":\"n6\",\"b\":\"n9\",\"time\":113},{\"a\":\"n1\",\"b\":\"n9\",\"time\":34},{\"a\":\"n6\",\"b\":\"n10\",\"time\":64},{\"a\":\"n16\",\"b\":\"n18\",\"time\":102},{\"a\":\"n12\",\"b\":\"n18\",\"time\":34},{\"a\":\"n3\",\"b\":\"n6\",\"time\":62},{\"a\":\"n1\",\"b\":\"n18\",\"time\":65},{\"a\":\"n10\",\"b\":\"n11\",\"time\":54},{\"a\":\"n15\",\"b\":\"n18\",\"time\":64},{\"a\":\"n3\",\"b\":\"n16\",\"time\":82},{\"a\":\"n0\",\"b\":\"n4\",\"time\":71},{\"a\":\"n2\",\"b\":\"n14\",\"time\":131},{\"a\":\"n1\",\"b\":\"n16\",\"time\":41},{\"a\":\"n10\",\"b\":\"n17\",\"time\":96},{\"a\":\"n5\",\"b\":\"n12\",\"time\":143},{\"a\":\"n9\",\"b\":\"n15\",\"time\":124},{\"a\":\"n5\",\"b\":\"n11\",\"time\":66},{\"a\":\"n8\",\"b\":\"n10\",\"time\":36},{\"a\":\"n0\",\"b\":\"n11\",\"time\":156},{\"a\":\"n3\",\"b\":\"n10\",\"time\":87},{\"a\":\"n15\",\"b\":\"n20\",\"time\":75},{\"a\":\"n9\",\"b\":\"n16\",\"time\":52},{\"a\":\"n10\",\"b\":\"n14\",\"time\":91},{\"a\":\"n3\",\"b\":\"n9\",\"time\":59},{\"a\":\"n4\",\"b\":\"n12\",\"time\":129},{\"a\":\"n2\",\"b\":\"n22\",\"time\":101},{\"a\":\"n1\",\"b\":\"n19\",\"time\":92},{\"a\":\"n16\",\"b\":\"n19\",\"time\":66},{\"a\":\"n2\",\"b\":\"n19\",\"time\":64},{\"a\":\"n8\",\"b\":\"n21\",\"time\":124},{\"a\":\"n8\",\"b\":\"n11\",\"time\":47},{\"a\":\"n2\",\"b\":\"n18\",\"time\":76},{\"a\":\"n3\",\"b\":\"n8\",\"time\":53},{\"a\":\"n18\",\"b\":\"n20\",\"time\":71},{\"a\":\"n2\",\"b\":\"n16\",\"time\":84}]}";
		final Jan jan = new ObjectMapper().readValue(json, Jan.class);
		final FastBusyBee fbb = new FastBusyBee();
		List<String> list;
//		list = fbb.apply(jan.c, 1600);
//		System.out.printf("%s%n", list);
//		System.out.println(fbb.cost);
//		if (true) return;
		int max = 1621; //Integer.MAX_VALUE - 1;
		do {
			list = fbb.apply(jan.c, max);	
			System.out.printf("%d -> %s%n", fbb.cost, list);
			max = fbb.cost - 1;
		} while (!list.isEmpty());
		
	}
 */
	public static final String HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg xmlns:svg=\"http://www.w3.org/2000/svg\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.0\" width=\"480\" height=\"480\">";
	
	public static void add(final Connection c, Map<String, Connection> connections) {
		final String one = c.a+"/"+c.b;
		final String two = c.b+"/"+c.a;
		if (connections.containsKey(one) || connections.containsKey(two)) {
			return;
		}
		if (one.compareTo(two) < 0) {
			connections.put(one, c);
		} else {
			connections.put(two, c);
		}
	}

	public static void main(String[] args) throws Throwable {
		final byte[] b = Files.readAllBytes(Paths.get("C:/Users/engehau/workspace/cc2-polyglot/resources/hamiltonian.txt"));
		final String[] lines = new String(b).split("\n");
		boolean cons = false;
		final Map<String, Node> nodes = new HashMap<>();
		final Map<String, Connection> connections = new HashMap<>();
		for (String l : lines) {
			if (cons) {
				final String[] info = l.split(" ");
				for (String n : info[1].split(",")) {
					Connection c = new Connection();
					c.a = info[0].trim();
					c.b = n.trim();
					add(c, connections);
				}
			} else {
				if (l.trim().isEmpty()) {
					cons = true;
				} else {
					final String[] info = l.split(" ");
					final Node n = new Node();
					n.name = info[0].trim();
					n.x = Integer.parseInt(info[1].trim());
					n.y = Integer.parseInt(info[2].trim());
					nodes.put(n.name, n);
				}
			}
		}
		final List<Connection> list = new ArrayList<>(times(nodes, connections.values()));
		
		System.out.println();
		System.out.println(HEAD);
		System.out.println("<!-- ");
		System.out.println(new ObjectMapper().writeValueAsString(list));
		System.out.println("-->");
		for (Connection c : list) {
			final Node na = nodes.get(c.a);
			final Node nb = nodes.get(c.b);
			System.out.printf("<line id=\"c-%s-%s\" x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" style=\"stroke:#999;stroke-width:3\"/>%n",
				c.a, c.b,
				na.x, na.y,
				nb.x, nb.y
			);
		}
		for (Node n : nodes.values()) {
			System.out.printf("<circle id=\"n-%s\" cx=\"%d\" cy=\"%d\" r=\"5\" fill=\"#fc0\" />%n",
				n.name,
				n.x, n.y
			);
		}
		System.out.println("</svg>");
	}

	private static Collection<Connection> times(final Map<String, Node> nodes, final Collection<Connection> values) {
		for (Connection c : values) {
			final Node a = nodes.get(c.a);
			final Node b = nodes.get(c.b);
			double x = Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
			c.time = (int) x;
		}
		return values;
	}

	public static class Node {
		public String name;
		public int x;
		public int y;
	}
	public static class Connection {
		public String a;
		public String b;
		public int time;
		
		public String toString() {
			return a + "-" + b;
		}
	}
}
