package com.textclassification.myapp;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class GenerateARFF 
{
	private FastVector atts;
	private FastVector mr;
	private Instances data;

	public GenerateARFF() 
	{
		// 1. set up attributes
		atts = new FastVector();
		
		mr = new FastVector(2);
		mr.addElement("positive");
		mr.addElement("negative");
		Attribute mrAttr = new Attribute("mr", mr);

		// - string
		atts.addElement(new Attribute("text", (FastVector) null));
		// - class 
		atts.addElement(new Attribute("review", mr));

		// 2. create Instances object
		data = new Instances("MovieReviews", atts, 0);
		data.setClassIndex(data.numAttributes() - 1);
	}

	public void addInstance(String text, String review) 
	{
		// 3. fill with data
		Instance inst = new DenseInstance(2);
		inst.setValue((Attribute) atts.elementAt(0), text);
		inst.setValue((Attribute) atts.elementAt(1), review);

		// add
		data.add(inst);
	}
	
	public Instances getFilteredInstances() throws Exception
	{
		// Set the tokenizer 
		NGramTokenizer tokenizer = new NGramTokenizer(); 
		tokenizer.setNGramMinSize(1); 
		tokenizer.setNGramMaxSize(1); 
		tokenizer.setDelimiters("\\s+\\n");
		
		// Set the filter 
		StringToWordVector filter = new StringToWordVector(); 
		filter.setInputFormat(data); 
		filter.setTokenizer(tokenizer); 
		filter.setDoNotOperateOnPerClassBasis(true);
		filter.setMinTermFreq(5);
		
		return Filter.useFilter(data, filter);
	}
	
	public Instances getInstances() throws Exception
	{
		return data;
	}
}