import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class PageRankApp {
	static String inlinks_file = Paths.get(".").toAbsolutePath().normalize().toString() + "/src/inlinks_file.txt";

	static HashSet<String> all_nodes = new HashSet<String>();
	static HashSet<String> sink_nodes = new HashSet<String>();

	static HashMap<String, HashSet<String>> mapping = new HashMap<String, HashSet<String>>();
	static HashMap<String, Integer> outlinks = new HashMap<String, Integer>();

	static String whitespace = " ";

	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(inlinks_file));
			String str;

			while ((str = br.readLine()) != null) {
				String[] nodes = str.split(whitespace);
				String newKey = nodes[0];

				if (!mapping.containsKey(newKey)) {
					mapping.put(newKey, new HashSet<String>());
				}

				if (!all_nodes.contains(newKey)) {
					all_nodes.add(newKey);
				}

				HashSet<String> temp = mapping.get(newKey);

				for (int i = 1; i < nodes.length; i++) {
					if ((!temp.contains(nodes[i])) && (!(nodes[i].equalsIgnoreCase(newKey)))) {
						temp.add(nodes[i]);

						if (!outlinks.containsKey(nodes[i])) {
							outlinks.put(nodes[i], 1);
						} else {
							outlinks.put(nodes[i], outlinks.get(nodes[i]) + 1);
						}

						if (!all_nodes.contains(newKey)) {
							all_nodes.add(newKey);
						}
					}
				}

				mapping.put(newKey, temp);
			}

			System.out.println("Set of pages that link to page p");
			for (Map.Entry<String, HashSet<String>> entry : mapping.entrySet()) {
				System.out.println(entry.getKey() + ": " + entry.getValue());
			}

			System.out.println();

			System.out.println("Number of out-links from page q");
			for (Map.Entry<String, Integer> entry : outlinks.entrySet()) {
				System.out.println(entry.getKey() + ": " + entry.getValue());
			}

			System.out.println();

			System.out.println("All Nodes");
			Iterator<String> iterator = all_nodes.iterator();
			while (iterator.hasNext()) {
				System.out.println(iterator.next());
			}
			
			iterator = all_nodes.iterator();
			while (iterator.hasNext())
			{
				String node = iterator.next();
				if (!outlinks.containsKey(node))
				{
					sink_nodes.add(node);
				}
			}
			
			System.out.println();
			
			System.out.println("Sink Nodes");
			iterator = sink_nodes.iterator();
			while (iterator.hasNext())
			{
				System.out.println(iterator.next());
			}
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
}
