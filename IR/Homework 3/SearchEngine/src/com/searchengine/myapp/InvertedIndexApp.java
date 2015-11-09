package com.searchengine.myapp;

public final class InvertedIndexApp {
	private final static int TWO = 2;

	private InvertedIndexApp() {
	}

	public static void main(final String[] args) {
		if (args.length == TWO) {
			try {
				final InvertedIndex invertedIndex = new InvertedIndex();
				invertedIndex.setInputFileName(args[0]);
				invertedIndex.setOutputFileName(args[1]);
				invertedIndex.create();
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		} else {
			System.out.println("Illegal number of arguments !");
		}
	}
}
