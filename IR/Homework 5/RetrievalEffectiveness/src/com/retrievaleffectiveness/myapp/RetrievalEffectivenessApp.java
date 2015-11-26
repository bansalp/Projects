package com.retrievaleffectiveness.myapp;

import java.util.Map;
import java.util.Set;

public class RetrievalEffectivenessApp 
{
	private static String cacmRelFileLocation = "files/cacm.rel";
	
	public static void main (String[] args)
	{
		try
		{
			MyFileReader myFileReader = new MyFileReader();
			Map<Integer, Set<String>> relDocs = myFileReader.cacmRelRead(cacmRelFileLocation);
			System.out.println(relDocs);
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.getMessage());
		}
	}
}
