package com.searchengine.myapp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

public class SearchEngineApp 
{
	private static String inputFileName = "src/tccorpus/tccorpus.txt";
	//private static String inputFileName = "src/tccorpus/sample.txt";
	private static String outputFileName = "src/tccorpus/index.out";
	private static String hashPattern = "#.*";
	private static String digitsPattern = "[0-9]+";
	private static String spaceDelimeter = " ";
	private static Map<String, Map<Integer, Long>> invertedIndex = new TreeMap<String, Map<Integer, Long>>();
	private static Map<Integer, Long> termFrequency;
	private static Map<Integer, Long> numberOfTokens = new TreeMap<Integer, Long>();
	
	public static void main (String[] args)
	{
		try 
		{
			FileReader fr = new FileReader(inputFileName);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			int docId = 0;
			long counter = 0;
			
			while ((line = br.readLine()) != null)
			{
				if (line.matches(hashPattern))
				{
					docId = Integer.parseInt(line.split(spaceDelimeter)[1]);
					counter = 0;
				}
				else
				{
					String[] words = line.split(spaceDelimeter);
					
					for (String word: words)
					{
						if (word.matches(digitsPattern))
						{
							continue;
						}
						else
						{
							counter++;
							
							if (invertedIndex.containsKey(word))
							{
								termFrequency = invertedIndex.get(word);
								
								if (termFrequency.containsKey(docId))
								{
									termFrequency.put(docId, termFrequency.get(docId) + 1);
								}
								else
								{
									termFrequency.put(docId, (long) 1);
								}
							}
							else
							{
								termFrequency = new TreeMap<Integer, Long>();
								termFrequency.put(docId, (long) 1);
								invertedIndex.put(word, termFrequency);
							}
						}
						
						numberOfTokens.put(docId, counter);
					}
				}
			}
			
			PrintWriter writer = new PrintWriter(outputFileName, "UTF-8");
			
			writer.println("# Inverted Index: ");
			for (Map.Entry<String, Map<Integer, Long>> entry: invertedIndex.entrySet())
			{
				writer.print(entry.getKey());
				writer.print(":");
				
				for (Map.Entry<Integer, Long> tf: invertedIndex.get(entry.getKey()).entrySet())
				{
					writer.print("(" + tf.getKey() + "," + tf.getValue() + ") ");
				}
				
				writer.println();
			}
			
			writer.println();
			
			writer.println("# Number of Tokens: ");
			for (Map.Entry<Integer, Long> entry: numberOfTokens.entrySet())
			{
				writer.println("(" + entry.getKey() + "," + entry.getValue() + ")");
			}
			
			writer.close();
		}
		catch (Exception e) 
		{
			System.out.println("Error: " + e.getMessage());
		}
	}
}
