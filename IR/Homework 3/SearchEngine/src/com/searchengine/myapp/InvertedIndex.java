package com.searchengine.myapp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;

public class InvertedIndex {
	private String inputFileName;
	private String outputFileName;
	private String hashPattern;
	private String digitsPattern;
	private String spaceDelimeter;
	private Map<String, Map<Integer, Long>> invertedIndex;
	private Map<Integer, Long> termFrequency;
	private Map<Integer, Long> numberOfTokens;

	public InvertedIndex() {
		hashPattern = "#.*";
		digitsPattern = "[0-9]+";
		spaceDelimeter = " ";

		invertedIndex = new TreeMap<String, Map<Integer, Long>>();
		numberOfTokens = new TreeMap<Integer, Long>();
	}

	public String getInputFileName() {
		return inputFileName;
	}

	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public void create() throws NumberFormatException, IOException {
		FileReader fr = new FileReader(inputFileName);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		int docId = 0;
		long counter = 0;

		while ((line = br.readLine()) != null) {
			if (line.matches(hashPattern)) {
				docId = Integer.parseInt(line.split(spaceDelimeter)[1]);
				counter = 0;
			} else {
				String[] words = line.split(spaceDelimeter);

				for (String word : words) {
					if (word.matches(digitsPattern)) {
						continue;
					} else {
						counter++;

						if (invertedIndex.containsKey(word)) {
							termFrequency = invertedIndex.get(word);

							if (termFrequency.containsKey(docId)) {
								termFrequency.put(docId, termFrequency.get(docId) + 1);
							} else {
								termFrequency.put(docId, (long) 1);
							}
						} else {
							termFrequency = new TreeMap<Integer, Long>();
							termFrequency.put(docId, (long) 1);
							invertedIndex.put(word, termFrequency);
						}
					}

					numberOfTokens.put(docId, counter);
				}
			}
		}
		
		writeOutputToFile();
	}
	
	private void writeOutputToFile() throws FileNotFoundException, UnsupportedEncodingException
	{
		PrintWriter writer = new PrintWriter(outputFileName, "UTF-8");

		writer.println("# Inverted Index: ");
		for (Map.Entry<String, Map<Integer, Long>> entry : invertedIndex.entrySet()) {
			writer.print(entry.getKey());
			writer.print(":");

			for (Map.Entry<Integer, Long> tf : invertedIndex.get(entry.getKey()).entrySet()) {
				writer.print("(" + tf.getKey() + "," + tf.getValue() + ") ");
			}

			writer.println();
		}

		writer.println();

		writer.println("# Number of Tokens: ");
		for (Map.Entry<Integer, Long> entry : numberOfTokens.entrySet()) {
			writer.println("(" + entry.getKey() + "," + entry.getValue() + ")");
		}

		writer.close();
	}
}
