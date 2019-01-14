import java.io.Serializable;
import java.util.ArrayList;
import java.text.*;
import java.lang.Math;

public class DecisionTree implements Serializable {

	DTNode rootDTNode;
	int minSizeDatalist; //minimum number of datapoints that should be present in the dataset so as to initiate a split
	//Mention the serialVersionUID explicitly in order to avoid getting errors while deserializing.
	public static final long serialVersionUID = 343L;
	public DecisionTree(ArrayList<Datum> datalist , int min) {
		minSizeDatalist = min;
		rootDTNode = (new DTNode()).fillDTNode(datalist);
	}

	class DTNode implements Serializable{
		//Mention the serialVersionUID explicitly in order to avoid getting errors while deserializing.
		public static final long serialVersionUID = 438L;
		boolean leaf;
		int label = -1;      // only defined if node is a leaf
		int attribute; // only defined if node is not a leaf
		double threshold;  // only defined if node is not a leaf



		DTNode left, right; //the left and right child of a particular node. (null if leaf)

		DTNode() {
			leaf = true;
			threshold = Double.MAX_VALUE;
		}



		// this method takes in a datalist (ArrayList of type datum) and a minSizeInClassification (int) and returns
		// the calling DTNode object as the root of a decision tree trained using the datapoints present in the
		// datalist variable
		// Also, KEEP IN MIND that the left and right child of the node correspond to "less than" and "greater than or equal to" threshold
		DTNode fillDTNode(ArrayList<Datum> datalist) {
			//YOUR CODE HERE
			double entropy = calcEntropy(datalist);
			double avgEntropy;
			ArrayList<Datum> list1a = new ArrayList<Datum>();
			ArrayList<Datum> list2a = new ArrayList<Datum>();
			boolean same = true;
			// if there are no items in the list, then no node should be created
			if(datalist.isEmpty()) {
				return null;
			}
			// if all data have same label, then create a node and place the label on it
			for(Datum currentTerm:datalist) {
				if(currentTerm.y != datalist.get(0).y) {
					same = false;
					break;
				}
			}
			if(same) {
				this.label = datalist.get(0).y;
				this.leaf = true;
				return this;
			}
			// if labels are not all the same, and the datalist size is greater than or equal to the minimum, then enter loop
			if(datalist.size()>= minSizeDatalist) {
				// loop through the entire dataset looking at all attributes' values
				for(int i = 0; i<2; i++) {
					for(Datum currentTerm: datalist) {
						// loop to compare each datum's attribute value to the outer loop datum's attribute value
						double numXa = 0;
						double numXb = 0;
						ArrayList<Datum> list1b = new ArrayList<Datum>();
						ArrayList<Datum> list2b = new ArrayList<Datum>();
						for(Datum compare:datalist) {
							// put all datum with xcoord attribute smaller than x in list 1
							if (compare.x[i]<currentTerm.x[i]) {
								list1b.add(compare);
								numXa++;
							}
							// put all datum with xcoord attribute greater than or equal to x in list 2
							else {
								list2b.add(compare);
								numXb++;
							}
						}
						// calculate average entropy between list1 + list2
						avgEntropy = ((numXa)*calcEntropy(list1b)
								+(numXb)*calcEntropy(list2b))/(numXa + numXb);
						if(avgEntropy<entropy) {
							list1a = list1b;
							list2a = list2b;
							this.threshold = currentTerm.x[i];
							this.attribute = i;
							entropy = avgEntropy;
						}
					}
				}
				this.leaf = false;
				this.left = (new DTNode()).fillDTNode(list1a);
				this.right = (new DTNode()).fillDTNode(list2a);
			}
			else {
				this.label = findMajority(datalist);
				this.leaf = true;
			}
			return this;
		}

		//This is a helper method. Given a datalist, this method returns the label that has the most
		// occurences. In case of a tie it returns the label with the smallest value (numerically) involved in the tie.
		int findMajority(ArrayList<Datum> datalist)
		{
			int l = datalist.get(0).x.length;
			int [] votes = new int[l];

			//loop through the data and count the occurrences of datapoints of each label
			for (Datum data : datalist)
			{
				votes[data.y]+=1;
			}
			int max = -1;
			int max_index = -1;
			//find the label with the max occurrences
			for (int i = 0 ; i < l ;i++)
			{
				if (max<votes[i])
				{
					max = votes[i];
					max_index = i;
				}
			}
			return max_index;
		}

		// This method takes in a datapoint (excluding the label) in the form of an array of type double (Datum.x) and
		// returns its corresponding label, as determined by the decision tree
		int classifyAtNode(double[] xQuery) {
			//YOUR CODE HERE
			if(this!=null && this.leaf) {
				return this.label;
			}
			if(this!=null && this.attribute == 0) {
				if(xQuery[0] < this.threshold) {
					return this.left.classifyAtNode(xQuery);
				}
				else {
					return this.right.classifyAtNode(xQuery);
				}
			}
			else {
				if(xQuery[1] < this.threshold) {
					return this.left.classifyAtNode(xQuery);
				}
				else {
					return this.right.classifyAtNode(xQuery);
				}
			}
		}


		//given another DTNode object, this method checks if the tree rooted at the calling DTNode is equal to the tree rooted
		//at DTNode object passed as the parameter
		public boolean equals(Object dt2)
		{
			//YOUR CODE HERE
			// implement a traversal and check to see if all nodes are equal
			// ie for internal nodes, attributes and thresholds should be the same
			// for leaf nodes, the labels should be the same
			boolean equals = false;
			DTNode DT2 = (DTNode)dt2;
			// if the DT2 is null, then the trees cannot be equal
			if(DT2 == null) {
				return false;
			}
			// if node is a leaf, then compare the labels 
			if(this.leaf && DT2.leaf) {	
				if (this.label == DT2.label) {
					return true;
				}
			}
			else {
				if(this.attribute==DT2.attribute && this.threshold==DT2.threshold) {
					if (this.right!=null&& DT2.right!=null) {
						equals= this.right.equals(DT2.right);
					}
					if (equals && this.left!=null && DT2.left!=null) {
						equals= this.left.equals(DT2.left);
					}
				}
			}
			return equals;
		}
	}



	//Given a dataset, this retuns the entropy of the dataset
	double calcEntropy(ArrayList<Datum> datalist)
	{
		double entropy = 0;
		double px = 0;
		float [] counter= new float[2];
		if (datalist.size()==0)
			return 0;
		double num0 = 0.00000001, num1 = 0.000000001;

		//calculates the number of points belonging to each of the labels
		for (Datum d : datalist)
		{
			counter[d.y]+=1;
		}
		//calculates the entropy using the formula specified in the document
		for (int i = 0 ; i< counter.length ; i++)
		{
			if (counter[i]>0)
			{
				px = counter[i]/datalist.size();
				entropy -= (px*Math.log(px)/Math.log(2));
			}
		}

		return entropy;
	}


	// given a datapoint (without the label) calls the DTNode.classifyAtNode() on the rootnode of the calling DecisionTree object
	int classify(double[] xQuery ) {
		DTNode node = this.rootDTNode;
		return node.classifyAtNode( xQuery );
	}

    // Checks the performance of a DecisionTree on a dataset
    //  This method is provided in case you would like to compare your
    //results with the reference values provided in the PDF in the Data
    //section of the PDF

    String checkPerformance( ArrayList<Datum> datalist)
	{
		DecimalFormat df = new DecimalFormat("0.000");
		float total = datalist.size();
		float count = 0;

		for (int s = 0 ; s < datalist.size() ; s++) {
			double[] x = datalist.get(s).x;
			int result = datalist.get(s).y;
			if (classify(x) != result) {
				count = count + 1;
			}
		}

		return df.format((count/total));
	}


	//Given two DecisionTree objects, this method checks if both the trees are equal by
	//calling onto the DTNode.equals() method
	public static boolean equals(DecisionTree dt1,  DecisionTree dt2)
	{
		boolean flag = true;
		flag = dt1.rootDTNode.equals(dt2.rootDTNode);
		return flag;
	}

}
