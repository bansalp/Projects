package com.textclassification.myapp;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instances;

public class GenerateARFF 
{
	private FastVector atts;
	private Instances data;
	private double[] vals;

	public GenerateARFF() 
	{
		// 1. set up attributes
		atts = new FastVector();

		// - string
		atts.addElement(new Attribute("text", (FastVector) null));
		atts.addElement(new Attribute("review", (FastVector) null));

		// 2. create Instances object
		data = new Instances("MovieReviews", atts, 0);
	}

	public void addInstance(String text, String review) 
	{
		// 3. fill with data
		vals = new double[data.numAttributes()];
		
		// - string
		vals[0] = data.attribute(0).addStringValue(text);
		vals[1] = data.attribute(1).addStringValue(review);

		// add
		data.add(new DenseInstance(1.0, vals));
	}
	
	public Instances getInstances()
	{
		return data;
	}
}