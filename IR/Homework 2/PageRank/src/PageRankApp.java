import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class PageRankApp 
{
	static File inlinks_file = null;
	static String outputFile = "output.txt";
	static String charSet = "UTF-8";

	static HashMap<String, Double> all_nodes = new HashMap<String, Double>();
	static HashMap<String, Double> all_nodes_copy = new HashMap<String, Double>();
	static HashSet<String> sink_nodes = new HashSet<String>();

	static HashMap<String, HashSet<String>> mapping = new HashMap<String, HashSet<String>>();
	static HashMap<String, Integer> outlinks = new HashMap<String, Integer>();

	static String whitespace = " ";

	static double[] prev_perplexity = new double[] { 0.0, 0.0, 0.0, 0.0 };
	static double cur_perplexity = 0.0;
	static double damping_factor = 0.85;

	public static void main(String[] args) 
	{
		try
		{
			if (args.length == 0)
			{
				throw new Exception();
			}
			else if (args.length == 1)
			{
				inlinks_file = new File(args[0]);
			}
		}
		catch(Exception e)
		{
			System.out.println("Error: Illegal arguments");
			System.exit(0);
		}
		
		try 
		{
			loadFileInMemory();

			for (Map.Entry<String, Double> entry : all_nodes.entrySet()) 
			{
				String node = entry.getKey();
				if (!outlinks.containsKey(node)) 
				{
					sink_nodes.add(node);
				}
			}

			initializeValues();

			do 
			{
				double sinkPR = totalSinkPR();

				for (Map.Entry<String, Double> entry : all_nodes.entrySet()) 
				{
					double newPR = (1.0 - damping_factor) / all_nodes.size();
					newPR += damping_factor * sinkPR / all_nodes.size();

					HashSet<String> temp = mapping.get(entry.getKey());

					if (temp != null) 
					{
						Iterator<String> iterator = temp.iterator();

						while (iterator.hasNext()) 
						{
							String node = iterator.next();
							newPR += damping_factor * all_nodes.get(node) / outlinks.get(node);
						}
					}

					all_nodes_copy.put(entry.getKey(), newPR);
				}

				all_nodes = all_nodes_copy;
			} 
			while (hasConverged());
			
			Map<String, Double> map = new TreeMap<String, Double>();
			for (Map.Entry<String, Double> entry: all_nodes.entrySet())
			{
				map.put(entry.getKey(), entry.getValue());
			}
			
			WriteToFile(map);
		} 
		catch (Exception e) 
		{
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	public static void WriteToFile(Map<String, Double> map) throws FileNotFoundException, UnsupportedEncodingException
	{
		PrintWriter writer = new PrintWriter(outputFile, charSet);		
		for (Map.Entry<String, Double> entry: map.entrySet())
		{
			writer.println(entry.getKey() + ": " + entry.getValue());
		}
		writer.close();
	}

	public static void loadFileInMemory() throws IOException 
	{
		BufferedReader br = new BufferedReader(new FileReader(inlinks_file));
		String str;

		while ((str = br.readLine()) != null) 
		{
			String[] nodes = str.split(whitespace);
			String newKey = nodes[0];
			HashSet<String> temp;

			if (!mapping.containsKey(newKey)) 
			{
				temp = new HashSet<String>();
			} 
			else 
			{
				temp = mapping.get(newKey);
			}

			all_nodes.put(newKey, null);

			for (int i = 1; i < nodes.length; i++) 
			{
				if ((!temp.contains(nodes[i])) && (!(nodes[i].equalsIgnoreCase(newKey)))) 
				{
					temp.add(nodes[i]);

					if (!outlinks.containsKey(nodes[i])) 
					{
						outlinks.put(nodes[i], 1);
					} 
					else 
					{
						outlinks.put(nodes[i], outlinks.get(nodes[i]) + 1);
					}

					all_nodes.put(nodes[i], null);
				}
			}

			if (temp.size() != 0) 
			{
				mapping.put(newKey, temp);
			}
		}
	}

	public static void initializeValues() 
	{
		double val = 1.0 / all_nodes.size();

		for (Map.Entry<String, Double> entry : all_nodes.entrySet()) 
		{
			entry.setValue(val);
		}
	}

	public static double totalSinkPR() 
	{
		double total = 0.0;

		for (String node : sink_nodes) 
		{
			double val = all_nodes.get(node);
			total += val;
		}

		return total;
	}

	public static double perplexity() 
	{
		double total = 0.0;

		for (Map.Entry<String, Double> entry : all_nodes.entrySet()) 
		{
			double val = entry.getValue();
			double log = Math.log(val) / Math.log(2);
			double intermediateVal = val * log;
			total += intermediateVal;
		}

		total *= -1;

		return Math.pow(2, total);
	}

	public static boolean hasConverged() 
	{
		for (int i = prev_perplexity.length - 1; i > 0; i--) 
		{
			prev_perplexity[i] = prev_perplexity[i - 1];
		}

		prev_perplexity[0] = cur_perplexity;
		cur_perplexity = perplexity();

		boolean result = false;

		for (int i = 0; i < prev_perplexity.length; i++) 
		{
			if (Math.abs(prev_perplexity[i] - cur_perplexity) < 1.0) 
			{
				result = result || false;
			}
			else 
			{
				result = result || true;
			}
		}

		return result;
	}
}
