package org.hsqldb.util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Vector;

public class KdTree {
	public Vector<Point> points;
	public int [] point_index;
	public int KdTreeDim;
	public KdTreeNode root;

	private class KdTreeNode{
		public Point value;
		public KdTreeNode left;
		public KdTreeNode right;

		public void setValue(Point other){
			value=other;
		}
		public KdTreeNode(){
			value=null;
			left=null;
			right=null;
		}
	}
	boolean smallerDimVal(Point a, Point b, int dim){
		if(a.myDim[dim]<b.myDim[dim])
			return true;
		if(a.myDim[dim]==b.myDim[dim]){
			isSmaller(a,b);
		}
		return false;
	}
	boolean isSmaller(Point a ,Point b){
		boolean less = false;
		for(int i = 0; i < a.Dim; ++i){
			less = a.myDim[i] < b.myDim[i];
			if(a.myDim[i] != b.myDim[i])
				break;
		}
		return less;
	}
	boolean shouldReplace(Point target, Point currentBest,  Point potential)
	{   
		if(getDistance(target,potential)<getDistance(target,currentBest))
			return true;
		else if(getDistance(target,potential)==getDistance(target,currentBest))
			return isSmaller(potential,currentBest); // break tie
		else return false;
	}

	double getDistance(Point curr, Point target) 
	{
		double sum=0;
		for(int i=0;i<KdTreeDim;i++)
		{
			sum=sum+Math.pow(curr.myDim[i]-target.myDim[i],2);
		}
		return sum;
	}
	double getE (Point curr, Point target){
		double sum=0;
		for(int i=0;i<KdTreeDim;i++)
		{
			sum=sum+Math.pow(curr.myDim[i]-target.myDim[i],2);
		}
		return Math.sqrt(sum);
	}

	public KdTree ( Vector<Point> newPoints)
	{
		KdTreeDim = newPoints.get(0).Dim;
		points = newPoints;
		point_index= new int[points.size()];
		for(int i=0;i<newPoints.size();i++)//set up point index
		{
			point_index[i]=i;

		}
		buildTree(0,newPoints.size()-1,0);
	}
	public KdTree(Vector<Point> newPoints, int [] index){
		KdTreeDim = newPoints.get(0).Dim;
		points = newPoints;
		point_index= new int[index.length];
		for(int i =0; i< index.length ; i++)
			point_index[i] = index[i];
	}


	//	KdTreeNode buildTree(int low,int high,int dim)
	//	{
	//		KdTreeNode node = new KdTreeNode();
	//		if(low>high)
	//			return null;
	//		if(low==high){
	//			node.setValue(points.get(point_index[low]));
	//			return node;
	//		}
	//	   
	//	    int mid=(low+high)/2;
	//        int index =select(mid,low,high,dim);
	//        node.setValue(points.get(index));
	//	    node.left=buildTree(low,mid-1,(dim+1)%KdTreeDim);
	//	    node.right =buildTree(mid+1,high,(dim+1)%KdTreeDim);
	//	  
	//	    return node;
	//	}
	void buildTree(int low,int high,int dim)
	{
		if(low<high){
			int mid=(low+high)/2;
			int index =select(mid,low,high,dim);

			buildTree(low,mid-1,(dim+1)%KdTreeDim);
			buildTree(mid+1,high,(dim+1)%KdTreeDim);
		}
	}

	void swap(int pivot,int high)
	{                
		int tmp = point_index[pivot];
		point_index[pivot]=point_index[high];
		point_index[high] = tmp;
	}
	int partition( int pivot, int high, int low, int dim)
	{
		int newpivot=high;

		swap(pivot, high); //put pivot to the end of the vector
		high--;
		while (low <high) 
		{   
			if(smallerDimVal(points.get(point_index[newpivot]), points.get(point_index[low]), dim))
			{    
				swap(low, high);
				high--;
			}
			else
				low++;
		}

		if(smallerDimVal(points.get(point_index[low]), points.get(point_index[newpivot]), dim))   //when low=high
		{
			low++;
			swap(newpivot,low);
		}
		else
			swap(newpivot,low);
		return low;

	}

	int select(int keyPosition,int low,int high,int dim)
	{
		int index=(low+high)/2;
		int pivot=partition(index,high,low,dim); //get position of an arbitrary pivot
		if(pivot==keyPosition)
		{
			return point_index[keyPosition];
		}   
		else if(pivot<keyPosition)
			return select(keyPosition,pivot+1,high,dim);
		else 
			return select(keyPosition,low,pivot-1,dim);
	}
	void handleArray (ArrayList<Point> result, Point target, Point pivot, double distance){
		if(result.contains(pivot))
			return;
		if(getE(target,pivot)<=distance)
			result.add(pivot);
	}
	ArrayList<Point> rangeQuery(Point target, double distance) throws IOException{
		ArrayList<Point> result = new ArrayList<Point>();
		rangeQuery(result, target, 0,0,point_index.length-1,distance);
		return result;
		
	}
	void rangeQuery (ArrayList<Point> result, Point target,int dim, int low, int high, double distance) throws IOException{
		int root=(low+high)/2;
		Point pivot=points.get(point_index[root]);
		handleArray(result, target, pivot, distance);
		if(low>high)
			return;
		if(smallerDimVal(pivot, target, dim))
			rangeQuery (result, target,(dim+1)%KdTreeDim, root+1, high, distance);
		else
			rangeQuery(result, target, (dim+1)%KdTreeDim, low, root-1, distance);

		Point temp= new Point(KdTreeDim, -1);
		for(int i=0;i<KdTreeDim;i++)
		{
			if(i!=dim%KdTreeDim)
				temp.set(i,target.myDim[i]);
			else 
				temp.set(i,pivot.myDim[i]);  
		}
		if(getE(target, temp)<distance)
		{
			if(smallerDimVal(pivot, target, dim))
				rangeQuery(result, target, (dim+1)%KdTreeDim, low, root-1, distance);
			else
				rangeQuery (result, target,(dim+1)%KdTreeDim, root+1, high, distance);

		}
	}
	PriorityQueue<Point>  findKNN(Point target, int topK) throws IOException{
		Comparator<Point> comparator = new PointDistanceComparator(target);
		PriorityQueue<Point> queue = new PriorityQueue<Point>(topK, comparator);
		Vector<Point> ref = new Vector<Point>();
		findKNN(target, 0, 0,point_index.length-1, queue, topK, ref  );
		return queue;
	}
	void handleQueue(Point target, PriorityQueue<Point> queue, int topK, Point curr, Vector<Point> ref ){
		
		if(ref.contains(curr))
			return;
		if(queue.size()<topK)
		{	
			queue.add(curr);
			ref.add(curr);
		}
		else if(queue.size()==topK){
			Point large= queue.peek();
			if(getDistance(large,target)>getDistance(curr,target))
			{
				queue.poll();
				ref.remove(large);
				queue.add(curr);
				ref.add(curr);
			}
		}
	}
	Point findKNN(Point target,int dim, int low,int high, PriorityQueue<Point> queue, int topK, Vector<Point> ref) throws IOException
	{
		int root=(low+high)/2;
		Point pivot=points.get(point_index[root]);
	
		handleQueue(target, queue, topK, pivot, ref);

		Point currbest;
		Point tempbest;
		Point big= new Point(KdTreeDim, -1);
		big.setMax();
		if(low>high)
		{
			//handleQueue(target, queue, topK, big);
			return big;
		}
		if(smallerDimVal(pivot, target, dim))
			currbest =findKNN(target,(dim+1)%KdTreeDim,root+1,high, queue, topK, ref);
		else
			currbest=findKNN(target,(dim+1)%KdTreeDim,low,root-1, queue, topK, ref);

		Point temp= new Point(KdTreeDim, -1);
		for(int i=0;i<KdTreeDim;i++)
		{
			if(i!=dim%KdTreeDim)
				temp.set(i,target.myDim[i]);
			else 
				temp.set(i,pivot.myDim[i]);  
		}
//		if(shouldReplace(target,currbest,pivot)){
//			handleQueue(target, queue, topK, pivot, ref);
//			currbest=pivot;
//		}
		if(shouldReplace(target,currbest,temp))
		{
			if(smallerDimVal(pivot, target, dim))
				tempbest=findKNN(target,(dim+1)%KdTreeDim,low,root-1, queue, topK, ref);
			else
				tempbest=findKNN(target,(dim+1)%KdTreeDim,root+1,high, queue, topK,ref);

			if(shouldReplace(target,currbest,tempbest))
			{
				handleQueue(target, queue, topK, tempbest, ref);
				currbest=tempbest;
			}

		}
		return currbest;
	}


	Point findNearestNeighbor(Point a) throws IOException 
	{

		return findNearestNeighbor( a,0, 0,point_index.length-1);
	}

	Point findNearestNeighbor(Point target,int dim, int low,int high) throws IOException
	{

		int root=(low+high)/2;
		Point pivot=points.get(point_index[root]);
		Point currbest;
		Point tempbest;
		Point big= new Point(KdTreeDim, -1);
		big.setMax();
		if(smallerDimVal(pivot, target, dim))
		{
			if(low>high)
				return big;
			currbest=findNearestNeighbor(target,(dim+1)%KdTreeDim,root+1,high);
		}
		else
		{
			if(low>high)  //what is the proper base case
			return big;
			currbest=findNearestNeighbor(target,(dim+1)%KdTreeDim,low,root-1);

		}

		Point temp= new Point(KdTreeDim, -1);
		for(int i=0;i<KdTreeDim;i++)
		{
			if(i!=dim%KdTreeDim)
				temp.set(i,target.myDim[i]);
			else 
				temp.set(i,pivot.myDim[i]);  
		}
		if(shouldReplace(target,currbest,pivot))
			currbest=pivot;

		if(shouldReplace(target,currbest,temp))
		{
			if(smallerDimVal(pivot, target, dim))
				tempbest=findNearestNeighbor(target,(dim+1)%KdTreeDim,low,root-1);
			else
				tempbest=findNearestNeighbor(target,(dim+1)%KdTreeDim,root+1,high);

			if(shouldReplace(target,currbest,tempbest))
				currbest=tempbest;
		}
		return currbest;
	}

	public static void printTree(KdTreeNode root){
		if(root == null)
			return;
		System.out.format("( %.0f, %.0f) : ", root.value.myDim[0], root.value.myDim[1]  );
		if(root.left!=null)
			System.out.format("( %.0f, %.0f) ",root.left.value.myDim[0], root.left.value.myDim[1] );
		else
			System.out.print("#, ");
		if(root.right!=null)
			System.out.format("( %.0f, %.0f )%n ",root.right.value.myDim[0], root.right.value.myDim[1] );
		else
			System.out.println("#");
		printTree(root.left);
		printTree(root.right);

	}
	public String printIndex(){
		StringBuilder result = new StringBuilder();
		int i =0;
		for (; i<point_index.length-1;i++){
			result.append(point_index[i]).append(" ");
		}
		result.append(point_index[i]);
		return result.toString();
	}

	public static void main(String [] args) throws IOException{
		double[] coord = new double[2];
//		coord[0]=84;coord[1]=44;
//		Point a = new Point(coord,0);
//		coord[0]=74;coord[1]=0;
//		Point b = new Point(coord,0);
//		coord[0]=54;coord[1]=62;
//		Point c = new Point(coord,0);
//		coord[0]=34;coord[1]=15;
//		Point d = new Point(coord,0);
//		coord[0]=42;coord[1]=63;
//		Point e = new Point(coord,0);
//		coord[0]=96;coord[1]=56;
//		Point f = new Point(coord,0);
//
//		coord[0]=44;coord[1]=79;
//		Point g = new Point(coord,0);
//		coord[0]=44;coord[1]=43;
//		Point h = new Point(coord,0);
//		coord[0]=28;coord[1]=10;
//		Point i = new Point(coord,0);
//		coord[0]=60;coord[1]=30;
//		Point j = new Point(coord,0);
//
//		coord[0]=88;coord[1]=72;
//		Point k = new Point(coord,0);
//		coord[0]=75;coord[1]=68;
//		Point l = new Point(coord,0);
//		coord[0]=43;coord[1]=65;
//		Point m = new Point(coord,0);
//		coord[0]=48;coord[1]=0;
//		Point n = new Point(coord,0);
//
//		coord[0]=14;coord[1]=15;
//		Point o = new Point(coord,0);
//		coord[0]=49;coord[1]=83;
//		Point p = new Point(coord,0);
//		coord[0]=51;coord[1]=35;
//		Point q = new Point(coord,0);
//		coord[0]=95;coord[1]=50;
//		Point r = new Point(coord,0);
//		coord[0]=82;coord[1]=20;
//		Point s = new Point(coord,0);
//
//
//
//		Vector<Point> newPoints = new Vector<Point>();
//		newPoints.add(a); newPoints.add(b); newPoints.add(c);newPoints.add(d); newPoints.add(e); newPoints.add(f);
//		newPoints.add(g); newPoints.add(h); newPoints.add(i);newPoints.add(j);
//		newPoints.add(k); newPoints.add(l); newPoints.add(m);newPoints.add(n); newPoints.add(o); newPoints.add(p);
//		newPoints.add(q); newPoints.add(r); newPoints.add(s);
//		KdTree test= new KdTree ( newPoints);
//		//System.out.println(test.printIndex());
//
//		coord[0] =45; coord[1]=59;
//		Point target = new Point(coord,0);
//		PriorityQueue<Point> goal= test.findKNN(target,10); 
//		while(!goal.isEmpty()){
//			Point curr= goal.poll();
//			System.out.format("%.0f,  %.0f%n", curr.myDim[0], curr.myDim[1]);
//		}


				coord[0] = 1; coord[1]=1;
				Point a = new Point(coord, 0);
				coord[0] = 2; coord[1] = 2;
				Point b= new Point(coord, 0);
				coord[0] =3; coord[1] =3;
				Point c = new Point(coord, 0);
				coord[0] =4; coord[1]=4;
				Point e = new Point(coord, 0);
				coord[0] =5; coord[1]=5;
				Point f = new Point(coord, 0);
				coord[0] =0; coord[1]=0;
				Point d = new Point(coord, 0);
				Vector<Point> myList = new Vector<Point>();
				myList.add(a);myList.add(b); myList.add(c);myList.add(d);myList.add(e);myList.add(f);
				KdTree test= new KdTree (myList);
				coord[0] = 4.5; coord[1]=4.5;
				Point target = new Point(coord, 0);
				ArrayList<Point> goal= test.rangeQuery(target,4); 
				for(int i=0; i< goal.size(); i++){
					Point curr = goal.get(i);
					System.out.format("%.0f,  %.0f%n", curr.myDim[0], curr.myDim[1]);
				}
				
//				Comparator<Point> comparator = new PointDistanceComparator(d);
//				PriorityQueue<Point> queue = new PriorityQueue<Point>(2, comparator);
//				queue.add(a);
//				queue.add(e);
//				queue.add(b);
//				queue.add(f);
//				queue.add(c);
//				while(!queue.isEmpty()){
//					Point goal= queue.poll();
//					System.out.format("%.0f,  %.0f%n", goal.myDim[0], goal.myDim[1]);
//				}
//				queue.add(a);
//				queue.add(e);
//				queue.add(b);
//				queue.add(f);
//				queue.add(c);
//				coord[0] = 2; coord[1] = 2;
//				Point l= new Point(coord, 0);
//				queue.remove(l);
//				while(!queue.isEmpty()){
//					Point goal= queue.poll();
//					System.out.format("%.0f,  %.0f%n", goal.myDim[0], goal.myDim[1]);
//				}
	}

}
