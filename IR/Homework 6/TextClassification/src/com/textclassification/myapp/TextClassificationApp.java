package com.textclassification.myapp;

import weka.classifiers.bayes.NaiveBayes;

public class TextClassificationApp 
{
	public static void main (String[] args) throws Exception
	{
		GenerateARFF generateARFF = new GenerateARFF();
		generateARFF.addInstance("Parth      is a good boy \n", "positive");
		generateARFF.addInstance("Parth      is an intelligent boy \n", "positive");
		generateARFF.addInstance("Parth      is a bad boy \n", "negative");
		generateARFF.addInstance("      is a Parth Parth worse boy \n", "negative");
		generateARFF.addInstance("Parth is a smart boy", "positive");
		
		NaiveBayes cModel = new NaiveBayes();
		cModel.buildClassifier(generateARFF.getFilteredInstances());
		System.out.println(cModel);
	}
}
