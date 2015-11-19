package com.lucene.myapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;

public class Indexer 
{
	private IndexWriter writer;
	private ArrayList<File> queue = new ArrayList<File>();
	private Analyzer simpleAnalyzer = new SimpleAnalyzer(Version.LUCENE_47);
	
	public Indexer(String indexLocation) throws IOException
	{
		FSDirectory dir = FSDirectory.open(new File(indexLocation));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, simpleAnalyzer);
		writer = new IndexWriter(dir, config);
	}
	
	public void indexFileOrDirectory(String fileName) throws IOException 
	{
		addFiles(new File(fileName));

		int originalNumDocs = writer.numDocs();
		
		for (File f : queue) 
		{
		    FileReader fr = null;
		    
		    try 
		    {
		    	Document doc = new Document();
		    	fr = new FileReader(f);
		    	BufferedReader br = new BufferedReader(fr);
		    	String line = null;
		    	StringBuilder sb = new StringBuilder();
		    	
		    	while ((line = br.readLine()) != null) 
		    	{
		    		org.jsoup.nodes.Document jDoc = Jsoup.parse(line);
		    		sb.append(jDoc.text());
		    		sb.append("\n");
		    	}
		    	
		    	doc.add(new TextField("contents", new StringReader(sb.toString())));
		    	doc.add(new StringField("path", f.getPath(), Field.Store.YES));
		    	doc.add(new StringField("filename", f.getName(), Field.Store.YES));
		    	writer.addDocument(doc);
		    	
		    	System.out.println("Added: " + f);
		    } 
		    catch (Exception e) 
		    {
		    	System.out.println("Could not add: " + f);
		    } 
		    finally 
		    {
		    	fr.close();
		    }
		}

		int newNumDocs = writer.numDocs();
		System.out.println("");
		System.out.println("************************");
		System.out.println((newNumDocs - originalNumDocs) + " documents added.");
		System.out.println("************************");

		queue.clear();
	}
	
	private void addFiles(File file) 
	{
		if (!file.exists()) 
		{
		    System.out.println(file + " does not exist.");
		}
		
		if (file.isDirectory()) 
		{
		    for (File f : file.listFiles()) 
		    {
		    	addFiles(f);
		    }
		} 
		else 
		{
		    String filename = file.getName().toLowerCase();
		    
		    if (filename.endsWith(".htm") || filename.endsWith(".html") || filename.endsWith(".xml") || filename.endsWith(".txt")) 
		    {
		    	queue.add(file);
		    } 
		    else 
		    {
		    	System.out.println("Skipped " + filename);
		    }
		}
	}
	
	public void closeIndex() throws IOException 
	{
		writer.close();
	}
}
