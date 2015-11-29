package com.textclassification.myapp;

public class TextClassificationApp 
{
	public static void main (String[] args)
	{
		GenerateARFF generateARFF = new GenerateARFF();
		generateARFF.addInstance("good", "positive");
		generateARFF.addInstance("bad", "negative");
		System.out.println(generateARFF.getInstances());
	}
}
