package com.retrievaleffectiveness.myapp;

public class OutputTable 
{
	private String documentId;
	private double documentScore;
	private Integer relevanceLevel;
	private double precision;
	private double recall;
	private double dg;
	private double dcg;
	private double ndcg;
	
	public String getDocumentId() 
	{
		return documentId;
	}
	
	public void setDocumentId(String documentId) 
	{
		this.documentId = documentId;
	}
	
	public double getDocumentScore() 
	{
		return documentScore;
	}
	
	public void setDocumentScore(double documentScore) 
	{
		this.documentScore = documentScore;
	}
	
	public int getRelevanceLevel() 
	{
		return relevanceLevel;
	}
	
	public void setRelevanceLevel(int relevanceLevel) 
	{
		this.relevanceLevel = relevanceLevel;
	}
	
	public double getPrecision() 
	{
		return precision;
	}
	
	public void setPrecision(double precision) 
	{
		this.precision = precision;
	}
	
	public double getRecall() 
	{
		return recall;
	}
	
	public void setRecall(double recall) 
	{
		this.recall = recall;
	}
	
	public double getDg() 
	{
		return dg;
	}

	public void setDg(double dg) 
	{
		this.dg = dg;
	}

	public double getDcg() 
	{
		return dcg;
	}

	public void setDcg(double dcg) 
	{
		this.dcg = dcg;
	}
	
	public double getNdcg() 
	{
		return ndcg;
	}
	
	public void setNdcg(double ndcg) 
	{
		this.ndcg = ndcg;
	}
	
	public int compareTo(OutputTable ot) 
	{
		return relevanceLevel.compareTo(ot.relevanceLevel);
	}

	@Override
	public String toString()
	{
		return documentId + " " + documentScore + " " + relevanceLevel + " " + precision + " " + recall + " " + dg + " " + dcg + " " + ndcg;
	}
}
