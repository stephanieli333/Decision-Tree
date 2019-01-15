# Decision-Tree
Decision Tree was an in-class assignment, which can be found here:
https://drive.google.com/file/d/18GKjloYRF4YHaR6-VMWBWSBPUQMK6roK/view?usp=sharing
The program analyses spatial data by reading a list of ordered pairs with a binary attribute and producing a decision tree 
based on the entropy achieved at different splits (i.e. along the y coordinate or x coordinate of each point). 
Lower entropy indicates a better split. The best split is chosen, and that value of that split is assigned to a new node of the
decision tree.
Other data sets can then be passed through the decision tree to be sorted into one of the binary attributes. The attribute is assigned 
based on a binary decision made at each node and its final attribute assigment occurs at a leaf node. 
