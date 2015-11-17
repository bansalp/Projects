package com.lucene.myapp;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
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
}
