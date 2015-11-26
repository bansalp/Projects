package com.retrievaleffectiveness.myapp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MyFileReader 
{
	private String space = " ";
	
	public Map<Integer, Set<String>> cacmRelRead(String cacmRelFileLocation) throws IOException
	{
		Map<Integer, Set<String>> relDocs = new HashMap<Integer, Set<String>>();
		String line = null;
		FileReader fr = new FileReader(cacmRelFileLocation);
		BufferedReader br = new BufferedReader(fr);
		
		while ((line = br.readLine()) != null)
		{
			String[] cols = line.split(space);
			int queryId = Integer.parseInt(cols[0]);
			String documentId = cols[2];
			
			if (relDocs.containsKey(queryId))
			{
				Set<String> docIds = relDocs.get(queryId);
				docIds.add(documentId);
			}
			else
			{
				Set<String> docId = new HashSet<String>();
				docId.add(documentId);
				relDocs.put(queryId, docId);
			}
		}
		
		return relDocs;
	}
}