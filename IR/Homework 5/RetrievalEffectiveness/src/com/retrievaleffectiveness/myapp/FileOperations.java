package com.retrievaleffectiveness.myapp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileOperations 
{
	private String space = " ";
	private String slash = "/";
	private String dot = "[.]";
	private String equal = "=";
	private double sumAvgPrecision = 0.0;
	
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
			String documentId = cols[1].split(slash)[1].split(dot)[0];
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
		double sumPrecision = 0.0;
		
		for (Map.Entry<Integer, OutputTable> entry: documents.entrySet())
		{
			int flag = 0;
			OutputTable ot = entry.getValue();
			String documentId = ot.getDocumentId();
			
			if (queryRelDocs.contains(documentId))
			{
				++relCount;
				flag = 1;
				ot.setRelevanceLevel(1);
			}
			else
			{
				ot.setRelevanceLevel(0);
			}
			
			double precision = relCount / (double) entry.getKey();
			ot.setPrecision(precision);
			
			double recall = relCount / (double) queryRelDocs.size(); 
			ot.setRecall(recall);
			
			if (flag == 1)
			{
				sumPrecision += precision;
			}
		}
		
		sumAvgPrecision += (sumPrecision / queryRelDocs.size());
	}
	
	public void calculateDiscountedGain(Map<Integer, OutputTable> documents)
	{
		int counter = 1;
		
		for (Map.Entry<Integer, OutputTable> entry: documents.entrySet())
		{
			OutputTable ot = entry.getValue();
			double discountedGain = ot.getRelevanceLevel();
			
			if (counter >= 2)
			{
				discountedGain /= log2(counter);
			}
			
			ot.setDg(discountedGain);
			++counter;
		}
	}
	
	public void calculateDiscountedCumulativeGain(Map<Integer, OutputTable> documents)
	{
		int counter = 1;
		double previousDCG = 0.0;
		
		for (Map.Entry<Integer, OutputTable> entry: documents.entrySet())
		{
			OutputTable ot = entry.getValue();
			double discountedCumulativeGain = ot.getDg();
			
			if (counter >= 2)
			{
				discountedCumulativeGain += previousDCG;
			}
			
			ot.setDcg(discountedCumulativeGain);
			previousDCG = discountedCumulativeGain;
			++counter;
		}
	}
	
	public Map<Integer, OutputTable> sortDocuments(Map<Integer, OutputTable> documents)
	{
		List<Map.Entry<Integer, OutputTable>> sortedEntries = new ArrayList<Map.Entry<Integer, OutputTable>>(documents.entrySet());
		Collections.sort(sortedEntries, new Comparator<Map.Entry<Integer, OutputTable>>() {
			@Override
			public int compare(Map.Entry<Integer, OutputTable> e1, Map.Entry<Integer, OutputTable> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});

		Map<Integer, OutputTable> result = new LinkedHashMap<Integer, OutputTable>();
		for (Map.Entry<Integer, OutputTable> entry : sortedEntries) {
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}
	
	public void calculateNormalizedDCG(Map<Integer, OutputTable> documents, Map<Integer, OutputTable> documentsCopy)
	{
		int counter = 0;
		List<OutputTable> docCopy = new LinkedList<OutputTable>();
		
		for (Map.Entry<Integer, OutputTable> entry: documentsCopy.entrySet())
		{
			docCopy.add(entry.getValue());
		}
		
		for (Map.Entry<Integer, OutputTable> entry: documents.entrySet())
		{
			OutputTable ot = entry.getValue();
			OutputTable otCopy = docCopy.get(counter);
			double ndcg = ot.getDcg() / otCopy.getDcg();
			ot.setNdcg(ndcg);
			++counter;
		}
	}
	
	public void serializeObject(String objectFileName, Map<Integer, OutputTable> documents) throws IOException 
	{
		FileOutputStream fos = new FileOutputStream(objectFileName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(documents);
		oos.close();
	}
	
	public Map<Integer, OutputTable> deserializeObject(String objectFileName) throws IOException, ClassNotFoundException 
	{
		FileInputStream fis = new FileInputStream(objectFileName);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Map<Integer, OutputTable> documents = (Map<Integer, OutputTable>) ois.readObject();
		ois.close();
		return documents;
	}
	
	public void display(Map<Integer, OutputTable> documents)
	{
		for (Map.Entry<Integer, OutputTable> entry: documents.entrySet())
		{
			System.out.println(entry.getKey() + ". " + entry.getValue());
		}
	}
	
	public double getMeanAvgPrecision(int size)
	{
		return (sumAvgPrecision / size);
	}
	
	public double logb(double a, double b)
	{
		return Math.log(a) / Math.log(b);
	}

	public double log2(double a)
	{
		return logb(a, 2);
	}
}