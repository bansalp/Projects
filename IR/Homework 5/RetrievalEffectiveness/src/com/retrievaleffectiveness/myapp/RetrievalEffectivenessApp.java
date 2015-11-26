package com.retrievaleffectiveness.myapp;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class RetrievalEffectivenessApp 
{
	private static String cacmRelFileLocation = "files/cacm.rel";
	private static String qa12FileLocation = "files/Qa_12.txt";
	private static String qb13FileLocation = "files/Qb_13.txt";
	private static String qc19FileLocation = "files/Qc_19.txt";
	private static Map<Integer, String> files = new LinkedHashMap<Integer, String>();
	
	public static void main (String[] args)
	{
		try
		{
			FileOperations fileOperations = new FileOperations();
			Map<Integer, Set<String>> relDocs = fileOperations.cacmRelRead(cacmRelFileLocation);
			
			loadQueries();
			
			for (Map.Entry<Integer, String> entry: files.entrySet())
			{
				Map<Integer, OutputTable> documents = fileOperations.queryRead(entry.getValue());
				fileOperations.generateTable(relDocs, documents, entry.getKey());
				fileOperations.display(documents);
				System.out.println();
			}
			
			System.out.println("Mean Average Precision: " + fileOperations.getMeanAvgPrecision());
		}
		catch (Exception e)
		{
			System.out.println(e.getStackTrace());
		}
	}
	
	private static void loadQueries()
	{
		files.put(12, qa12FileLocation);
		files.put(13, qb13FileLocation);
		files.put(19, qc19FileLocation);
	}
}
