package com.searchengine.myapp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BM25Calculator 
{
	private String invertedIndexFileName;
	private String queriesFileName;
	private long maxDocumentResults;
	
	private Map<String, Map<Integer, Long>> invertedIndex;
	// private static Map<Integer, Long> termFrequency;
	private Map<Integer, Long> numberOfTokens;
	private List<String> queries;
	
	private double k1;
	private double k2;
	private double b;
	
	private double avdl;
	private double N;
	
	private String space;
	private String digitsPattern;
	
	private String systemName;
	
	public BM25Calculator()
	{
		queries = new ArrayList<String>();
		
		k1 = 1.2;
		k2 = 100;
		b = 0.75;
		
		space = " ";
		digitsPattern = "[0-9]+";
		
		systemName = "BANSAL-P";
	}
	
	public String getInvertedIndexFileName() 
	{
		return invertedIndexFileName;
	}
	
	public void setInvertedIndexFileName(String invertedIndexFileName) 
	{
		this.invertedIndexFileName = invertedIndexFileName;
	}
	
	public String getQueriesFileName() 
	{
		return queriesFileName;
	}
	
	public void setQueriesFileName(String queriesFileName) 
	{
		this.queriesFileName = queriesFileName;
	}
	
	public long getMaxDocumentResults() 
	{
		return maxDocumentResults;
	}

	public void setMaxDocumentResults(long maxDocumentResults) 
	{
		this.maxDocumentResults = maxDocumentResults;
	}
	
	public void calculate() throws ClassNotFoundException, IOException
	{
		// This is used to read the contents from the index.out file and 
		// load the inverted index and number of tokens data structures in memory
		readInvertedIndexFromFile();
		// This is used to read the contents from the queries.txt file and 
		// load the queries in the memory
		readQueriesFromFile();
		// This is used to calculate avdl for the corpus
		calculateAvdl();
		// This is used to calculate N for the corpus
		calculateN();
		// This is used to calculate the scores for each and every query in the 
		// queries.txt file
		calculateScores();
		
		//System.out.println(invertedIndex);
		//System.out.println(numberOfTokens);
		//System.out.println(avdl);
		//System.out.println(N);
		//System.out.println(queries);
	}
	
	private void readInvertedIndexFromFile() throws IOException, ClassNotFoundException 
	{
		FileInputStream fis = new FileInputStream(invertedIndexFileName);
		ObjectInputStream ois = new ObjectInputStream(fis);
		invertedIndex = (Map<String, Map<Integer, Long>>) ois.readObject();
		numberOfTokens = (Map<Integer, Long>) ois.readObject();
		ois.close();
	}
	
	private void readQueriesFromFile() throws IOException
	{
		FileReader fr = new FileReader(queriesFileName);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		
		while ((line = br.readLine()) != null) 
		{
			queries.add(line);
		}
	}
	
	private void calculateAvdl()
	{
		long totalDocumentLength = 0;
		
		for (Map.Entry<Integer, Long> entry: numberOfTokens.entrySet())
		{
			totalDocumentLength += entry.getValue();
		}
		
		avdl = (double) totalDocumentLength / numberOfTokens.size();
	}
	
	private void calculateN()
	{
		N = (double) numberOfTokens.size();
	}
	
	private void calculateScores()
	{		
		int queryId = 1;
		
		for (String query: queries)
		{	
			int rank = 1;
			
			Map<Integer, Double> score = new HashMap<Integer, Double>();
			
			//System.out.println(query);
			
			Map<String, Integer> queryFrequency = getQueryFrequency(query);
			
			for (Map.Entry<String, Integer> queryTerm: queryFrequency.entrySet())
			{
				//System.out.println(queryTerm.getKey() + " - " + queryTerm.getValue());
				
				Map<Integer, Long> invertedIndexQueryTerm = retrieveInvertedIndexForQueryTerm(queryTerm.getKey());
				calculateScoreForDocument(queryTerm.getValue(), invertedIndexQueryTerm, score);
				
				//System.out.println(invertedIndexQueryTerm);
			}

			score = sortScoreDescending(score);
			
			for (Map.Entry<Integer, Double> entry: score.entrySet())
			{
				if (rank <= maxDocumentResults)
				{
					System.out.println(queryId + " Q0 " + entry.getKey() + " " + rank + " " + entry.getValue() + " " + systemName);
					rank++;
				}
				else
				{
					System.out.println();
					break;
				}
			}
			
			queryId++;
		}
	}
	
	private Map<String, Integer> getQueryFrequency(String query)
	{
		Map<String, Integer> queryFrequency = new HashMap<String, Integer>();
		
		for (String term: query.split(space))
		{
			if (term.matches(digitsPattern))
			{
				continue;
			}
			else
			{
				if (queryFrequency.containsKey(term))
				{
					queryFrequency.put(term, queryFrequency.get(term) + 1);
				}
				else
				{
					queryFrequency.put(term, 1);
				}
			}
		}
		
		return queryFrequency;
	}
	
	private Map<Integer, Long> retrieveInvertedIndexForQueryTerm(String queryTerm)
	{
		return invertedIndex.get(queryTerm);
	}
	
	private void calculateScoreForDocument(int qf, Map<Integer, Long> invertedIndexQueryTerm, Map<Integer, Double> score)
	{
		long n = invertedIndexQueryTerm.size();
		
		for (Map.Entry<Integer, Long> document: invertedIndexQueryTerm.entrySet())
		{
			double scr = bm25Score(qf, n, document.getKey(), document.getValue());
			
			if (score.containsKey(document.getKey()))
			{
				score.put(document.getKey(), score.get(document.getKey()) + scr);
			}
			else
			{
				score.put(document.getKey(), scr);
			}
		}
	}
	
	private double bm25Score(int qf, long n, int docId, long f)
	{
		double logNumerator = N - (double) n + 0.5;
		double logDenominator = (double) n + 0.5;
		double firstTerm = Math.log(logNumerator / logDenominator);
		double secondNumerator = (k1 + 1.0) * (double) f;
		double secondDenominator = calculateK(docId) + (double) f;
		double secondTerm = secondNumerator / secondDenominator;
		double thirdNumerator = (k2 + 1.0) * (double) qf;
		double thirdDenominator = k2 + (double) qf;
		double thirdTerm = thirdNumerator / thirdDenominator;
		return firstTerm * secondTerm * thirdTerm;
	}
	
	private double calculateK(int docId)
	{
		double term1 = 1.0 - b;
		double term2 = b * (double) numberOfTokens.get(docId) / avdl;
		double term12 = term1 + term2;
		return k1 * term12;
	}
	
	private Map<Integer, Double> sortScoreDescending(Map<Integer, Double> score)
	{
		List<Map.Entry<Integer, Double>> sortedEntries = new ArrayList<Entry<Integer,Double>>(score.entrySet());
		Collections.sort(sortedEntries, new Comparator<Entry<Integer, Double>>() 
		{
			@Override
			public int compare(Entry<Integer,Double> e1, Entry<Integer,Double> e2)
			{
				 return e2.getValue().compareTo(e1.getValue());
			}
		});
		
		Map<Integer, Double> result = new LinkedHashMap<Integer, Double>();
		for (Entry<Integer, Double> entry: sortedEntries) 
		{
		   result.put(entry.getKey(), entry.getValue());
		}
		
		return result;
	}
}