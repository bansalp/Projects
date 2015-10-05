import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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
			PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
			
			BufferedReader br = new BufferedReader(new FileReader(inlinks_file));
			String str;

			while ((str = br.readLine()) != null) {
				String[] nodes = str.split(whitespace);
				String newKey = nodes[0];
				HashSet<String> temp;

				if (!mapping.containsKey(newKey)) {
					temp = new HashSet<String>();
				}
				else
				{
					temp = mapping.get(newKey);
				}

				all_nodes.add(newKey);

				for (int i = 1; i < nodes.length; i++) {
					if ((!temp.contains(nodes[i])) && (!(nodes[i].equalsIgnoreCase(newKey)))) {
						temp.add(nodes[i]);

						if (!outlinks.containsKey(nodes[i])) {
							outlinks.put(nodes[i], 1);
						} else {
							outlinks.put(nodes[i], outlinks.get(nodes[i]) + 1);
						}

						all_nodes.add(nodes[i]);
					}
				}

				if (temp.size() != 0)
				{
					mapping.put(newKey, temp);
				}
			}

			System.out.println("Set of pages that link to page p");
			writer.println("Set of pages that link to page p");
			for (Map.Entry<String, HashSet<String>> entry : mapping.entrySet()) {
				System.out.println(entry.getKey() + ": " + entry.getValue());
				writer.println(entry.getKey() + ": " + entry.getValue());
			}

			System.out.println();
			writer.println();

			System.out.println("Number of out-links from page q");
			writer.println("Number of out-links from page q");
			for (Map.Entry<String, Integer> entry : outlinks.entrySet()) {
				System.out.println(entry.getKey() + ": " + entry.getValue());
				writer.println(entry.getKey() + ": " + entry.getValue());
			}

			System.out.println();
			writer.println();

			System.out.println("All Nodes");
			writer.println("All Nodes");
			Iterator<String> iterator = all_nodes.iterator();
			while (iterator.hasNext()) {
				String node = iterator.next();
				System.out.println(node);
				writer.println(node);
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
			writer.println();
			
			System.out.println("Sink Nodes");
			writer.println("Sink Nodes");
			iterator = sink_nodes.iterator();
			while (iterator.hasNext())
			{
				String node = iterator.next();
				System.out.println(node);
				writer.println(node);
			}
			
			writer.close();
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
}
