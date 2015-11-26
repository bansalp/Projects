package com.retrievaleffectiveness.myapp;

import java.util.Map;
import java.util.Set;

public class RetrievalEffectivenessApp 
{
	private static String cacmRelFileLocation = "files/cacm.rel";
	private static String qa12FileLocation = "files/Qa_12.txt";
	//private static String qb13FileLocation = "files/Qb_13.txt";
	//private static String qc19FileLocation = "files/Qc_19.txt";
	
	public static void main (String[] args)
	{
		try
		{
			FileOperations fileOperations = new FileOperations();
			Map<Integer, Set<String>> relDocs = fileOperations.cacmRelRead(cacmRelFileLocation);
			Map<Integer, OutputTable> qa12Documents = fileOperations.queryRead(qa12FileLocation);
			fileOperations.generateTable(relDocs, qa12Documents, 12);
			fileOperations.display(qa12Documents);
		}
		catch (Exception e)
		{
			System.out.println(e.getStackTrace());
		}
	}
}
