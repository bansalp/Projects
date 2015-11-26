package com.retrievaleffectiveness.myapp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class FileOperations 
{
	private String space = " ";
	private String slash = "/";
	private String dotHtml = ".html";
	private String equal = "=";
	
	public Map<Integer, Set<String>> cacmRelRead(String cacmRelFileLocation) throws IOException
	{
		Map<Integer, Set<String>> relDocs = new LinkedHashMap<Integer, Set<String>>();
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
	
	public Map<Integer, OutputTable> queryRead(String queryFileLocation) throws IOException
	{
		Map<Integer, OutputTable> documents = new LinkedHashMap<Integer, OutputTable>();
		String line = null;
		FileReader fr = new FileReader(queryFileLocation);
		BufferedReader br = new BufferedReader(fr);
		
		while ((line = br.readLine()) != null)
		{
			String[] cols = line.split(space);
			int rank = Integer.parseInt(cols[0].substring(0, cols[0].length() - 1));
			String documentName = cols[1].split(slash)[1];
			String documentId = documentName.split(dotHtml)[0];
			double documentScore = Double.parseDouble(cols[2].split(equal)[1]);
			OutputTable ot = new OutputTable();
			ot.setDocumentId(documentId);
			ot.setDocumentScore(documentScore);
			documents.put(rank, ot);
		}
		
		return documents;
	}
	
	public void generateTable(Map<Integer, Set<String>> relDocs, Map<Integer, OutputTable> documents, int queryId)
	{
		Set<String> queryRelDocs = relDocs.get(queryId);
		int relCount = 0;
		
		for (Map.Entry<Integer, OutputTable> entry: documents.entrySet())
		{
			OutputTable ot = entry.getValue();
			String documentId = ot.getDocumentId();
			
			if (queryRelDocs.contains(documentId))
			{
				++relCount;				
				ot.setRelevanceLevel(1);
			}
			else
			{
				ot.setRelevanceLevel(0);
			}
			
			ot.setPrecision(relCount / (double) entry.getKey());
		}
	}
	
	public void display(Map<Integer, OutputTable> documents)
	{
		for (Map.Entry<Integer, OutputTable> entry: documents.entrySet())
		{
			System.out.println(entry.getKey() + ". " + entry.getValue());
		}
	}
}