package com.lucene.myapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class LuceneApp 
{
	public static void main (String[] args)
	{
		try
		{
			System.out.println("Enter the FULL path where the index will be created: (e.g. /Usr/index or c:\\temp\\index)");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String s = br.readLine();
			String indexLocation = s;
			Indexer indexer = new Indexer(indexLocation);
			
			while (!s.equalsIgnoreCase("q")) 
			{
			    try 
			    {
			    	System.out.println("Enter the FULL path to add into the index (q=quit): (e.g. /home/mydir/docs or c:\\Users\\mydir\\docs)");
			    	System.out.println("[Acceptable file types: .xml, .html, .html, .txt]");
			    	s = br.readLine();
			    	
			    	if (s.equalsIgnoreCase("q")) 
			    	{
			    		break;
			    	}

			    	indexer.indexFileOrDirectory(s);
			    }
			    catch (Exception e) 
			    {
			    	System.out.println("Error indexing " + s + ": " + e.getMessage());
			    }
			}
			
			indexer.closeIndex();
			
			SearchEngine searchEngine = new SearchEngine(indexLocation);
			
			s = "";
			while (!s.equalsIgnoreCase("q")) 
			{
			    try 
			    {
			    	System.out.println("Enter the search query (q=quit):");
			    	s = br.readLine();
			    	
			    	if (s.equalsIgnoreCase("q")) 
			    	{
			    		break;
			    	}
			    	
			    	TopDocs topDocs = searchEngine.performSearch(s, 100);
			    	ScoreDoc[] hits = topDocs.scoreDocs;

			    	System.out.println("Found " + hits.length + " hits.");
			    	for (int i = 0; i < hits.length; ++i)
			    	{
			    		int docId = hits[i].doc;
			    		Document d = searchEngine.getDocument(docId);
			    		System.out.println((i + 1) + ". " + d.get("path") + " score=" + hits[i].score);
			    	}
			    	
			    	Term termInstance = new Term("contents", s);
					long termFreq = searchEngine.getTotalTermFreq(termInstance);
					long docCount = searchEngine.getDocFreq(termInstance);
					System.out.println(s + " Term Frequency " + termFreq + " - Document Frequency " + docCount);
			    }
			    catch (Exception e)
			    {
			    	System.out.println("Error searching " + s + ": " + e.getMessage());
			    }
			}
			
			Map<String, Long> allTerms = searchEngine.getTermFrequencyForAllTerms();
			searchEngine.WriteToFile(allTerms);
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.getMessage());
		}
	}
}