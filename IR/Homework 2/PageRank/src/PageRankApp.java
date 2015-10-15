import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PageRankApp {
	static String inlinks_file = Paths.get(".").toAbsolutePath().normalize().toString() + "/src/wt2g_inlinks.txt";

	static HashMap<String, Double> all_nodes = new HashMap<String, Double>();
	static HashMap<String, Double> all_nodes_copy = new HashMap<String, Double>();
	static HashSet<String> sink_nodes = new HashSet<String>();

	static HashMap<String, HashSet<String>> mapping = new HashMap<String, HashSet<String>>();
	static HashMap<String, Integer> outlinks = new HashMap<String, Integer>();

	static String whitespace = " ";
	
	static double[] prev_perplexity = new double[] {0.0, 0.0, 0.0, 0.0};
	static double cur_perplexity = 0.0;
	static double damping_factor = 0.85;

	public static void main(String[] args) {
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
			
			int counter = 0;
			
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
					//entry.setValue(newPR);
				}
				
				all_nodes = (HashMap<String, Double>) all_nodes_copy.clone();
				
				//System.out.print("Iteration " + ++counter + ": ");
				
				//counter++;
				
				//for (Map.Entry<String, Double> entry : all_nodes.entrySet())
				//{
				//	System.out.println(entry.getKey() + ": " + entry.getValue() + " ");
				//}
				
				//System.out.println();
				//System.out.println("---------------------------------------------------------------------------");
			}
			while (hasConverged());
			
			/*List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(all_nodes.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, Double>>() 
			{
				public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2)
				{
					return (o1.getValue()).compareTo( o2.getValue() );
				}
			});
			
			Map<String, Double> result = new LinkedHashMap<String, Double>();
	        for (Map.Entry<String, Double> entry : list)
	        {
	        	result.put(entry.getKey(), entry.getValue());
	            //System.out.println(entry.getKey() + ": "+ entry.getValue());
	        }
	        
	        ArrayList<String> keys = new ArrayList<String>(result.keySet());
	        for (int i = keys.size() - 1; i >= keys.size() - 50; i--)
	        {
	        	System.out.println(keys.get(i) + ": " + result.get(keys.get(i)));
	        }*/
			
			/*Map<String, Integer> temp = new HashMap<String, Integer>();
			for (Map.Entry<String, HashSet<String>> entry: mapping.entrySet())
			{
				int num = entry.getValue().size();
				temp.put(entry.getKey(), num);
			}
			
			List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(temp.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() 
			{
				public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2)
				{
					return (o1.getValue()).compareTo(o2.getValue());
				}
			});
			
			Map<String, Integer> result = new LinkedHashMap<String, Integer>();
	        for (Map.Entry<String, Integer> entry : list)
	        {
	        	if (entry.getValue() == 0)
	        	{
	        		result.put(entry.getKey(), entry.getValue());
	        	}
	            //System.out.println(entry.getKey() + ": "+ entry.getValue());
	        }*/
	        			
	        //ArrayList<String> keys = new ArrayList<String>(result.keySet());
	        //for (int i = keys.size() - 1; i >= keys.size() - 50; i--)
	        //{
	        //	System.out.println(keys.get(i) + ": " + result.get(keys.get(i)));
	        //}
	        
	        /*String a = Long.toString(all_nodes.size());
        	System.out.println("All pages: " + all_nodes.size());
        	
        	for (Map.Entry<String, Integer> entry: outlinks.entrySet())
        	{
        		all_nodes.remove(entry.getKey());
        	}
        	
        	System.out.println("Pages with no out-links (sinks): " + all_nodes.size());
        	System.out.println("Proportion: " + (all_nodes.size()/Double.parseDouble(a)));*/
	        
	        /*double t = 1.0/all_nodes.size();
	        int c = 0;
	        for (Map.Entry<String, Double> entry: all_nodes.entrySet())
	        {
	        	if (entry.getValue() < t)
	        	{
	        		c++;
	        	}
	        }
	        
	        System.out.println("All pages: " + all_nodes.size());
	        System.out.println("Pages whose PageRank is less than their initial, uniform values: " + c);
	        System.out.println("Proportion: " + ((double) c/all_nodes.size()));*/
	        
			/*PrintWriter writer = new PrintWriter("output.txt", "UTF-8");

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
			//Iterator<String> iterator = all_nodes.iterator();
			for (Map.Entry<String, Double> entry : all_nodes.entrySet()) {
				String node = entry.getKey();
				System.out.println(node + " " + entry.getValue());
				writer.println(node);
			}
			
			//iterator = all_nodes.iterator();
			for (Map.Entry<String, Double> entry : all_nodes.entrySet())
			{
				String node = entry.getKey();
				if (!outlinks.containsKey(node))
				{
					sink_nodes.add(node);
				}
			}
			
			System.out.println();
			writer.println();
			
			System.out.println("Sink Nodes");
			writer.println("Sink Nodes");
			Iterator<String> iterator = sink_nodes.iterator();
			while (iterator.hasNext())
			{
				String node = iterator.next();
				System.out.println(node);
				writer.println(node);
			}
			
			writer.close();*/
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	public static void loadFileInMemory() throws IOException
	{
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

			all_nodes.put(newKey, null);

			for (int i = 1; i < nodes.length; i++) {
				if ((!temp.contains(nodes[i])) && (!(nodes[i].equalsIgnoreCase(newKey)))) {
					temp.add(nodes[i]);

					if (!outlinks.containsKey(nodes[i])) {
						outlinks.put(nodes[i], 1);
					} else {
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
		double val = 1.0/all_nodes.size();

		for (Map.Entry<String, Double> entry : all_nodes.entrySet())
		{
			entry.setValue(val);
		}
	}
	
	public static double totalSinkPR()
	{
		double total = 0.0;
		
		for (String node: sink_nodes)
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
			prev_perplexity[i] = prev_perplexity[i-1];
		}
		
		prev_perplexity[0] = cur_perplexity;
		cur_perplexity = perplexity();
		//System.out.println(cur_perplexity);
		
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
