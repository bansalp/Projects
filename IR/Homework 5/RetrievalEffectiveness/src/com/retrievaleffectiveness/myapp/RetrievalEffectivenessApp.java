package com.retrievaleffectiveness.myapp;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class RetrievalEffectivenessApp 
{
	private static String cacmRelFileLocation = "files/cacm.rel";
	private String qa12FileLocation = "files/Qa_12.txt";
	private String qb13FileLocation = "files/Qb_13.txt";
	private String qc19FileLocation = "files/Qc_19.txt";
	private static Map<Integer, String> files = new LinkedHashMap<Integer, String>();
	private static String objectFileName = "files/object.txt";
	
	public RetrievalEffectivenessApp()
	{
		files.put(12, qa12FileLocation);
		files.put(13, qb13FileLocation);
		files.put(19, qc19FileLocation);
	}
	
	public static void main (String[] args)
	{
		try
		{
			RetrievalEffectivenessApp rea = new RetrievalEffectivenessApp();
			FileOperations fileOperations = new FileOperations();
			Map<Integer, Set<Integer>> relDocs = fileOperations.cacmRelRead(cacmRelFileLocation);
			
			for (Map.Entry<Integer, String> entry: files.entrySet())
			{
				Map<Integer, OutputTable> documents = fileOperations.queryRead(entry.getValue());
				fileOperations.generateTable(relDocs, documents, entry.getKey());
				fileOperations.calculateDiscountedGain(documents);
				fileOperations.calculateDiscountedCumulativeGain(documents);
				
				fileOperations.serializeObject(objectFileName, documents);
				
				Map<Integer, OutputTable> documentsCopy = fileOperations.sortDocuments(documents);
				fileOperations.calculateDiscountedGain(documentsCopy);
				fileOperations.calculateDiscountedCumulativeGain(documentsCopy);
				
				documents = null;
				documents = fileOperations.deserializeObject(objectFileName);
				
				fileOperations.calculateNormalizedDCG(documents, documentsCopy);
				
				System.out.println("Q_ID: " + entry.getKey());
				System.out.println();
				fileOperations.display(documents);
				System.out.println();
				System.out.println("P@20: " + documents.get(20).getPrecision());
				System.out.println();
				System.out.println();
			}
			
			System.out.println("Mean Average Precision: " + fileOperations.getMeanAvgPrecision(files.size()));
		}
		catch (Exception e)
		{
			System.out.println(e.getStackTrace());
		}
	}
}
