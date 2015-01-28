package org.hsqldb.util;

import java.io.IOException;

public class Point {
	public long nodeId;
	public double [] myDim;	
	public int Dim;
	private String tag=null;
	public Point (int Dim, int nodeId){
		this.nodeId = nodeId;
		this.Dim = Dim;
		myDim = new double[Dim];
	}
	public Point(double [] other, int nodeId){
		this.Dim = other.length;
		myDim= new double[this.Dim];
		this.nodeId = nodeId;
		for(int i = 0; i<other.length; i++){
			myDim[i]=other[i];
		}
	}
	public void setTag(String tag){
		this.tag = tag;
	}
	public String getTag(){
		return tag;
	}
	public void setMax(){
		for(int i=0; i< Dim; i++)
			myDim[i]=Double.MAX_VALUE;
	}
	void set(int index, double val) throws IOException
	{
		if (index >= Dim)
		{
			throw new IOException("Point index out of range");
		}
		myDim[index] = val;
	}
}
