package org.hsqldb.util;
import java.util.Comparator;

public class PointDistanceComparator implements Comparator<Point>{
	Point target;
	public PointDistanceComparator(Point target){
		this.target=target;
	}
	double getDistance(Point curr, Point target) 
	{
	    double sum=0;
	    int PointDim = this.target.Dim; 
	    for(int i=0;i<PointDim;i++)
	    {
//	    	System.out.println("i");
//	    	System.out.println(i);
//	    	System.out.println("MYdim:");
//	    	System.out.println("a");
//	    	System.out.println(curr.myDim.length);
//	    	System.out.println("b");
//	    	System.out.println(target.myDim.length);
//	    	System.out.println("c");
//	    	System.out.println(curr.myDim[i]-target.myDim[i]);
	        sum=sum+Math.pow(curr.myDim[i]-target.myDim[i],2);
	        // System.out.println(sum);
	    }
	    return sum;
	}
	@Override
	public int compare(Point a, Point b){
		double distA = getDistance(target, a);
		double distB = getDistance(target, b);
		if(distA > distB){
			return -1;
		}
		else if(distA < distB){
			return 1;
		}
		else
			return 0;
	}
}
