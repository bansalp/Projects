package com.searchengine.myapp;

public class BM25CalculatorApp 
{
	private static String closingAngularBracket = ">";
	private static int FIVE = 5;
	
	public static void main(String[] args) 
	{
		if (args.length == FIVE)
		{
			try 
			{
				BM25Calculator calculator = new BM25Calculator();
				calculator.setInvertedIndexFileName(args[0]);
				calculator.setQueriesFileName(args[1]);
				calculator.setMaxDocumentResults(Long.parseLong(args[2]));
				
				if (args[3].equals(closingAngularBracket))
				{
					calculator.setOutputFileName(args[4]);
				}
				else
				{
					throw new Exception("Please have '>' as your fourth argument !");
				}
				
				calculator.calculate();
			} 
			catch (Exception e) 
			{
				System.out.println("Error: " + e.getMessage());
			}
		}
		else 
		{
			System.out.println("Illegal number of arguments !");
		}
	}
}