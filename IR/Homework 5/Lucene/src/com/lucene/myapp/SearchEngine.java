package com.lucene.myapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

public class SearchEngine 
{
	private IndexReader reader;
	private IndexSearcher searcher;
    private QueryParser parser;
    private Analyzer simpleAnalyzer = new SimpleAnalyzer(Version.LUCENE_47);
    
    public SearchEngine(String indexLocation) throws IOException
    {
    	reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
    	searcher = new IndexSearcher(reader);
        parser = new QueryParser(Version.LUCENE_47, "contents", simpleAnalyzer);
    }
    
    public TopDocs performSearch(String queryString, int n) throws IOException, ParseException 
    {
    	Query query = parser.parse(queryString);        
    	return searcher.search(query, n);
    }
    
    public Document getDocument(int docId) throws IOException 
    {
    	return searcher.doc(docId);
    }
    
    public long getTotalTermFreq(Term termInstance) throws IOException
    {
    	return reader.totalTermFreq(termInstance);
    }
    
    public long getDocFreq(Term termInstance) throws IOException
    {
    	return reader.docFreq(termInstance);
    }
    
    public Map<String, Long> getTermFrequencyForAllTerms() throws IOException
	{
    	Map<String, Long> allTerms = new HashMap<String, Long>();
    	Fields fields = MultiFields.getFields(reader);
        Terms terms = fields.terms("contents");
        TermsEnum iterator = terms.iterator(null);
        BytesRef byteRef = null;
        
        while((byteRef = iterator.next()) != null) 
        {
        	String t = byteRef.utf8ToString();
            Term term = new Term ("contents", t);
            long termFreq = getTotalTermFreq(term);
            allTerms.put(t, termFreq);
        }
        
        return sortDescending(allTerms);
	}
    
    private Map<String, Long> sortDescending(Map<String, Long> allTerms)
    {
		List<Map.Entry<String, Long>> sortedEntries = new ArrayList<Entry<String, Long>>(allTerms.entrySet());
		Collections.sort(sortedEntries, new Comparator<Entry<String, Long>>() {
			@Override
			public int compare(Entry<String, Long> e1, Entry<String, Long> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});

		Map<String, Long> result = new LinkedHashMap<String, Long>();
		for (Entry<String, Long> entry : sortedEntries) {
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}
    
    public void WriteToFile(Map<String, Long> allTerms) throws FileNotFoundException, UnsupportedEncodingException
	{
		PrintWriter writer = new PrintWriter("term_frequency.txt", "UTF-8");		
		
		for (Map.Entry<String, Long> entry: allTerms.entrySet())
		{
			writer.println(entry.getKey() + ": " + entry.getValue());
		}
		
		writer.close();
	}
}