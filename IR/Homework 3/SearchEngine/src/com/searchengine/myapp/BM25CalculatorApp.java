package com.searchengine.myapp;

public class BM25CalculatorApp {
	private static int THREE = 3;

	public static void main(String[] args) {
		if (args.length == THREE) {
			try {
				BM25Calculator calculator = new BM25Calculator();
				calculator.setInvertedIndexFileName(args[0]);
				calculator.setQueriesFileName(args[1]);
				calculator.setMaxDocumentResults(Long.parseLong(args[2]));
				calculator.calculate();
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		} else {
			System.out.println("Illegal number of arguments !");
		}
	}
}