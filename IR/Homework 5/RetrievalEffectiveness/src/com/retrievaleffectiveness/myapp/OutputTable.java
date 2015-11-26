package com.retrievaleffectiveness.myapp;

public class OutputTable 
{
	private int rank;
	private String documentId;
	private double documentScore;
	private int relevanceLevel;
	private double precision;
	private double recall;
	private double ndcg;
	
	public int getRank() 
	{
		return rank;
	}
	
	public void setRank(int rank) 
	{
		this.rank = rank;
	}
	
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
	
	public double getNdcg() 
	{
		return ndcg;
	}
	
	public void setNdcg(double ndcg) 
	{
		this.ndcg = ndcg;
	}
}
