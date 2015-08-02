// itaysofer , 201507357 , איתי סופר
// omersofer , 201507340 , עומר סופר


/**
 *
 * WAVLTree
 *
 * An implementation of a WAVL Tree with distinct integer keys and info
 *
 */


public class WAVLTree {
	private WAVLNode root;
	private String min;
	private String max;
	private int size;
	
	
	/**
	 * public WAVLTree(int key, String info)
	 *
	 * returns a tree with root, with key and info
	 *
	 */
	public WAVLTree(int key, String info){
		this.root = new WAVLNode(key, info, null);
		this.min = info;
		this.max = info;
		this.size = 1;
	}
	
	/**
	 * public WAVLTree(){
	 *
	 * returns a tree with an external leaf as a root
	 *
	 */
	public WAVLTree(){
		this.root = new WAVLNode(null);
		this.min = null;
		this.max = null;
		this.size = 0;
	}
	
	
	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 *
	 */
	public boolean empty() {
		return this.size == 0;
	}

	/**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree
	 * otherwise, returns null
	 */
	public String search(int k) {
		WAVLNode ans = this.searchNode(k);
		if (ans == null)
			return null;
		else 
			return ans.info;
	}
	
	/**
	 * public WAVLNode searchNode(int k)
	 *
	 * returns the WAVLNode pointer with key k if it exists in the tree
	 * otherwise, returns null
	 */
	public WAVLNode searchNode(int k) {
		WAVLNode temp = this.root;
		while (temp.isExternalLeaf() == false){
			if (temp.key == k)
				return temp;
			else if (k < temp.key)
				temp = temp.leftChild;
			else if (k > temp.key)
				temp = temp.rightChild;
		}
		return null;
	}

	/**
	 * public int insert(int k, String i)
	 *
	 * inserts an item with key k and info i to the WAVL tree. the tree must
	 * remain valid (keep its invariants). returns the number of rebalancing
	 * operations, or 0 if no rebalancing operations were necessary. returns -1
	 * if an item with key k already exists in the tree.
	 */
	public int insert(int k, String i) {
		
		//finding place to insert the new node
		WAVLNode freeSpace = this.root.findPlace(k);
		if (freeSpace == null) //the key is already in the tree
			return -1;
		
		//inserting the node and rebalancing
		WAVLNode newNode = new WAVLNode (k,i,freeSpace.parent);
		int numberOfRebalancing;
		if (!freeSpace.isRoot()){
			if (freeSpace.isRightChild())
				freeSpace.parent.rightChild = newNode;
			else
				freeSpace.parent.leftChild = newNode;
			freeSpace.parent = null;
			numberOfRebalancing = newNode.parent.totalRebalanceAfterInsert();
			if (!this.root.isRoot())
				this.root = this.root.parent;
		}
		else{
			this.root = newNode;
			freeSpace.parent = null;
			numberOfRebalancing = newNode.totalRebalanceAfterInsert();
		}
		
		//update tree characteristics
		this.size++;
		this.min = this.root.findMin().info;
		this.max = this.root.findMax().info;
		
		return numberOfRebalancing; 
	}

	/**
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there; the tree
	 * must remain valid (keep its invariants). returns the number of
	 * rebalancing operations, or 0 if no rebalancing operations were needed.
	 * returns -1 if an item with key k was not found in the tree.
	 */
	public int delete(int k) {
		int numberOfRebalanceOperations;
		WAVLNode node = this.searchNode(k);
		if (node == null)
			return(-1);
		
		//replacing binary node with its successor, then node is an unary node/a leaf - and we would be able to delete it.
		else if (node.isBinaryNode()){ 
			WAVLNode successorNode = node.successor();
			WAVLNode tempParent = node.parent;
			WAVLNode tempLeftChild = node.leftChild;
			WAVLNode tempRightChild = node.rightChild;
			
			if (node.rightChild != successorNode){
				if (node.isRoot()){
					this.root = successorNode;
					if (successorNode.isRightChild())
						successorNode.parent.rightChild = node;
					else
						successorNode.parent.leftChild = node;
				}
				else{
					if (successorNode.isRightChild()){
						successorNode.parent.rightChild = node;
						if (node.isRightChild())
							node.parent.rightChild = successorNode;
						else
							node.parent.leftChild = successorNode;
					}
					else{
						successorNode.parent.leftChild = node;
						if (node.isRightChild())
							node.parent.rightChild = successorNode;
						else
							node.parent.leftChild = successorNode;
					}
				}
				node.rightChild.parent = successorNode;
				successorNode.rightChild.parent = node;
				node.leftChild.parent = successorNode;
				successorNode.leftChild.parent = node;
				node.parent = successorNode.parent;
				successorNode.parent = tempParent;
				node.leftChild = successorNode.leftChild;
				successorNode.leftChild = tempLeftChild;
				node.rightChild = successorNode.rightChild;
				successorNode.rightChild = tempRightChild;
			}
			else{ // node.rightChild == successorNode
				if (node.isRoot())
					this.root = successorNode;
				else{
					if (node.isRightChild())
						node.parent.rightChild = successorNode;
					else
						node.parent.leftChild = successorNode;
				}
				successorNode.rightChild.parent = node;
				node.leftChild.parent = successorNode;
				successorNode.leftChild.parent = node;
				node.parent = successorNode;
				successorNode.parent = tempParent;
				node.leftChild = successorNode.leftChild;
				successorNode.leftChild = tempLeftChild;
				node.rightChild = successorNode.rightChild;
				successorNode.rightChild = node;
			}
			int tempRank = node.rank;
			node.rank = successorNode.rank;
			successorNode.rank = tempRank;
		}		
		// end of successor replacement for binary node
		
		// deleteing node
		if (node.isRoot()){
			if (node.hasOnlyRightChild()){
				this.root=node.rightChild;
				node.rightChild=null;
				this.root.parent = null;
			}
			else{
				this.root=node.leftChild;
				node.leftChild=null;
				this.root.parent = null;
			}
			numberOfRebalanceOperations=0;
		}
		else if (node.isLeaf()){	
			WAVLNode rebalacingNode = node.parent;
			if (node.isRightChild()){ 
				node.parent.rightChild = new WAVLNode(node.parent);
				node.parent = null;
			}
			else{
				node.parent.leftChild = new WAVLNode(node.parent);
				node.parent = null;
			}
			numberOfRebalanceOperations = rebalacingNode.totalRebalanceAfterDelete();
			if (!this.root.isRoot())
				this.root = this.root.parent;
		}
		else{ //node is an unary node
			WAVLNode rebalacingNode = node.parent;
			if (node.isRightChild()){
				if (node.hasOnlyRightChild()){
					node.parent.rightChild = node.rightChild;
					node.rightChild.parent = node.parent;
					node.parent = null;
					node.rightChild = null;
				}
				else{
					node.parent.rightChild = node.leftChild;
					node.leftChild.parent = node.parent;
					node.parent = null;
					node.leftChild = null;
				}
			}
			else{ //node is a left child
				if (node.hasOnlyRightChild()){
					node.parent.leftChild = node.rightChild;
					node.rightChild.parent = node.parent;
					node.parent = null;
					node.rightChild = null;
				}
				else{
					node.parent.leftChild = node.leftChild;
					node.leftChild.parent = node.parent;
					node.parent = null;
					node.leftChild = null;
				}
			}
			numberOfRebalanceOperations = rebalacingNode.totalRebalanceAfterDelete();
			if (!this.root.isRoot())
				this.root = this.root.parent;
		}
		
		//update tree characteristics
		this.size--;
		this.min = this.root.findMin().info;
		this.max = this.root.findMax().info;
		return (numberOfRebalanceOperations);
	}

	/**
	 * public String min()
	 *
	 * Returns the ifo of the item with the smallest key in the tree, or null if
	 * the tree is empty
	 */
	public String min (){
		return (this.min);
	}
	
	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree, or null if
	 * the tree is empty
	 */	
	public String max (){
		return (this.max);
	}
	
	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree, or an empty
	 * array if the tree is empty.
	 */
	public int[] keysToArray() {
		WAVLNode[] nodes = new WAVLNode[this.size];
		this.root.inOrderWalk(nodes, 0);
		int[] keys = new int[this.size];
		for (int i = 0; i < nodes.length; i++){
			keys[i] = nodes[i].key;
		}
		return keys;
	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree, sorted by their
	 * respective keys, or an empty array if the tree is empty.
	 */
	public String[] infoToArray() {
		WAVLNode[] nodes = new WAVLNode[this.size];
		this.root.inOrderWalk(nodes, 0);
		String[] infos = new String[this.size];
		for (int i = 0; i < nodes.length; i++){
			infos[i] = nodes[i].info;
		}
		return infos;
	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 * precondition: none postcondition: none
	 */
	public int size() {
		return (this.size);
	}

	/**
	 * public class WAVLNode
	 *
	 * If you wish to implement classes other than WAVLTree (for example
	 * WAVLNode), do it in this file, not in another file. This is an example
	 * which can be deleted if no such classes are necessary.
	 */
	
	public class WAVLNode {
		private int key;
		private String info;
		private WAVLNode parent;
		private WAVLNode leftChild;
		private WAVLNode rightChild;
		private int rank;
		
		/**
		 *public WAVLNode(int key, String info, WAVLNode parent)
		 *
		 * returns a new WAVLNode with key, info and parent
		 */
		public WAVLNode(int key, String info, WAVLNode parent) {
			this.key = key;
			this.info = info;
			this.parent = parent;
			this.leftChild = new WAVLNode(this);
			this.rightChild = new WAVLNode(this);
			this.rank = 0;
		}

		/**
		 *public WAVLNode(WAVLNode parent)
		 *
		 * returns a new WAVLNode representing an external leaf with parent
		 */
		public WAVLNode(WAVLNode parent) { // Building an external leaf.
			this.key = 0;
			this.info = null;
			this.parent = parent;
			this.leftChild = null;
			this.rightChild = null;
			this.rank = -1;
		}
		
		/**
		 *public WAVLNode findMin()
		 *
		 * a recursive method which returns the pointer to the WAVLNode with the minimum key
		 */
		public WAVLNode findMin() {
			if (this.isExternalLeaf())
				return this;
			else if (this.leftChild.isExternalLeaf())
				return this;
			else return this.leftChild.findMin();
		}

		/**
		 *public WAVLNode findMax()
		 *
		 * a recursive method which returns the pointer to the WAVLNode with the maximum key
		 */
		public WAVLNode findMax() {
			if (this.isExternalLeaf())
				return this;
			else if (this.rightChild.isExternalLeaf())
				return this;
			else return this.rightChild.findMax();
		}

		/**
		 *public void rotateRight ()
		 *
		 * rotates right from the given node (this node will go up)
		 */
		public void rotateRight (){
			
			WAVLNode prevParent = this.parent;
			WAVLNode prevRightChild = this.rightChild;
			
			if (! prevParent.isRoot()){
				if (prevParent.isRightChild())
					prevParent.parent.rightChild = this;
				else
					prevParent.parent.leftChild = this;
			}
			this.parent = prevParent.parent;
			
			this.rightChild = prevParent;
			prevParent.parent = this;

			prevParent.leftChild = prevRightChild;
			prevRightChild.parent = prevParent;
		}
		
		/**
		 *public void rotateLeft ()
		 *
		 * rotates left from the given node (this node will go up)
		 */
		public void rotateLeft (){
			WAVLNode prevParent = this.parent;
			WAVLNode prevLeftChild = this.leftChild;
			
			if (! prevParent.isRoot()){
				if (prevParent.isRightChild())
					prevParent.parent.rightChild = this;
				else
					prevParent.parent.leftChild = this;
			}
			this.parent = prevParent.parent;
			
			this.leftChild = prevParent;
			prevParent.parent = this;
			
			prevParent.rightChild = prevLeftChild;
			prevLeftChild.parent = prevParent;
		}
		
		/**
		 *public void doubleRotateRight ()
		 *
		 * double rotates right from the given node (this node will go up)
		 */
		public void doubleRotateRight (){
			this.rotateLeft();
			this.rotateRight();
		}
		
		/**
		 *public void doubleRotateLeft ()
		 *
		 * double rotates left from the given node (this node will go up)
		 */
		public void doubleRotateLeft (){
			this.rotateRight();
			this.rotateLeft();
		}		
		
		/**
		 *public void promote ()
		 *
		 * increases the rank of the given node by 1
		 */
		public void promote (){
			this.rank = this.rank+1;
		}
		
		/**
		 *public void demote ()
		 *
		 * decreases the rank of the given node by 1
		 */
		public void demote (){
			this.rank = this.rank-1;
		}		
			
		/**
		 *public WAVLNode successor()
		 *
		 * returns a pointer to the successor of the given node
		 * 
		 */
		public WAVLNode successor(){
			if (this.rightChild.isExternalLeaf()){
				WAVLNode temp = this;
				while (temp.isRoot() == false && temp.isRightChild()){
					temp = temp.parent;
				}
				return temp.parent;
			}
			else
				return this.rightChild.findMin();
		}		
		
		/**
		 * public boolean isXYNode(int x, int y)
		 *
		 * returns true if and only if the node has rank differences of: x from his LeftChild and y from his rightChild
		 *
		 */		
		public boolean isXYNode(int x, int y){
			return (this.rank - this.leftChild.rank == x) && (this.rank - this.rightChild.rank == y);
		}
		
		/**
		 * public boolean isRoot ()
		 *
		 * returns true if and only if the node is the Root
		 *
		 */	
		public boolean isRoot (){
			return (this.parent == null);
		}
		
		/**
		 * public boolean isLeaf ()
		 *
		 * returns true if and only if the node is a Leaf
		 *
		 */	
		public boolean isLeaf (){
			return (this.leftChild.isExternalLeaf() && this.rightChild.isExternalLeaf());
		}
		
		/**
		 * public boolean isExternalLeaf ()
		 *
		 * returns true if and only if the node is an ExternalLeaf
		 *
		 */	
		public boolean isExternalLeaf (){
			if (this.rank == -1)
				return (true);
			return (false);
		}
		
		/**
		 * public boolean isBinaryNode()
		 *
		 * returns true if and only if the node is a BinaryNode
		 *
		 */	
		public boolean isBinaryNode(){
			return this.leftChild.isExternalLeaf() == false && this.rightChild.isExternalLeaf() == false;
		}
		
		/**
		 * public boolean isRightChild ()
		 *
		 * returns true if and only if the node is a RightChild
		 *
		 */
		public boolean isRightChild (){
			return (this == this.parent.rightChild);
		}
		
		/**
		 * public boolean hasOnlyRightChild()
		 *
		 * returns true if and only if the node hasOnlyRightChild
		 *
		 */
		public boolean hasOnlyRightChild(){
			return ((!this.rightChild.isExternalLeaf())&&(this.leftChild.isExternalLeaf()));
		}
		
		/**
		 * public boolean isLegalNode()
		 *
		 * returns true if and only if the node is a LegalNode
		 *
		 */		
		public boolean isLegalNode(){
			return this.isXYNode(1,1) || this.isXYNode(1,2) || this.isXYNode(2,1) || (this.isXYNode(2,2) && !this.isLeaf()) ;
		}

		/**
		 *public WAVLNode findPlace (int k)
		 *
		 * returns a pointer to available place in the tree for insertion (external leaf).
		 * if the key already exists, returns null.
		 * 
		 */
		public WAVLNode findPlace (int k){
			if (this.isExternalLeaf())
				return this;
			else if (k == this.key)
				return null;
			else if (k < this.key)
				return this.leftChild.findPlace(k);
			else
				return this.rightChild.findPlace(k);
		}	
		
		/**
		 * public boolean isInsertCase1 ()
		 *
		 * returns true if and only if the node is in InsertCase1
		 *
		 */	
		public boolean isInsertCase1(){
			return this.isXYNode(0,1) || this.isXYNode(1,0);
		}
		
		/**
		 * public boolean isInsertCase2R ()
		 *
		 * returns true if and only if the node is in InsertCase2R
		 *
		 */	
		public boolean isInsertCase2R(){
			return this.isXYNode(0,2) && this.leftChild.isXYNode(1,2);
		}
		
		/**
		 * public boolean isInsertCase2L ()
		 *
		 * returns true if and only if the node is in InsertCase2L
		 *
		 */	
		public boolean isInsertCase2L(){
			return this.isXYNode(2,0) && this.rightChild.isXYNode(2,1);
		}
		
		/**
		 * public boolean isInsertCase3R ()
		 *
		 * returns true if and only if the node is in InsertCase3R
		 *
		 */	
		public boolean isInsertCase3R(){
			return this.isXYNode(0,2) && this.leftChild.isXYNode(2,1);
		}
		
		/**
		 * public boolean isInsertCase3L ()
		 *
		 * returns true if and only if the node is in InsertCase3L
		 *
		 */	
		public boolean isInsertCase3L(){
			return this.isXYNode(2,0) && this.rightChild.isXYNode(1,2);
		}
		
		/**
		 * public int localRebalanceAfterInsert()
		 *
		 * does all the rebalancing operations for the given node. problem may go up.
		 * returns the number of balancing operations done.
		 *
		 */	
		public int localRebalanceAfterInsert(){
			if (this.isLegalNode())
				return 0;
			else if (this.isInsertCase1()){
				this.promote();
				return 1;
			}
			else if (this.isInsertCase2R()){
				this.leftChild.rotateRight();
				this.demote();
				return 1;//demote is counted as part of rotate rebalncing operation
			}
			else if (this.isInsertCase2L()){
				this.rightChild.rotateLeft();
				this.demote();
				return 1; //demote is counted as part of rotate rebalncing operation
			}
			else if (this.isInsertCase3R()){
				this.leftChild.rightChild.doubleRotateRight();
				this.demote();
				this.parent.leftChild.demote();
				this.parent.promote();
				return 2;//demotes and promote are counted as part of double rotate rebalncing operation
			}
			else if (this.isInsertCase3L()){
				this.rightChild.leftChild.doubleRotateLeft();
				this.demote();
				this.parent.rightChild.demote();
				this.parent.promote();
				return 2; //demotes and promote are counted as part of double rotate rebalncing operation
			}
			else
				return 0;
		}
		
		/**
		 * public int totalRebalanceAfterInsert()
		 *
		 * does all the rebalancing operations required in the tree from a given node up.
		 * returns the number of balancing operations done.
		 *
		 */	
		public int totalRebalanceAfterInsert(){
			if (this.isLegalNode())
				return 0;
			else{
				if (this.isRoot())
					return this.localRebalanceAfterInsert();
				else{
					WAVLNode parent = this.parent;
					int localRebalance = this.localRebalanceAfterInsert();
					int totalRebalance = parent.totalRebalanceAfterInsert(); // check if the problem went up and rebalance if needed
					return localRebalance + totalRebalance;
				}
			}
		}
		
		/**
		 * public boolean isDeleteCase1 ()
		 *
		 * returns true if and only if the node is in DeleteCase1
		 *
		 */	
		public boolean isDeleteCase1 (){
			return ((this.isXYNode(3,2)) || (this.isXYNode(2,3)));
		}
		
		/**
		 * public boolean isDeleteCase2R ()
		 *
		 * returns true if and only if the node is in DeleteCase2R
		 *
		 */
		public boolean isDeleteCase2R (){
			return ((this.isXYNode(3,1)) &&  (this.rightChild.isXYNode(2,2)));
		}
		
		/**
		 * public boolean isDeleteCase2L ()
		 *
		 * returns true if and only if the node is in DeleteCase2L
		 *
		 */
		public boolean isDeleteCase2L (){
			return ((this.isXYNode(1,3)) &&  (this.leftChild.isXYNode(2,2)));
		}
		
		/**
		 * public boolean isDeleteCase3R ()
		 *
		 * returns true if and only if the node is in DeleteCase3R
		 *
		 */
		public boolean isDeleteCase3R (){
			return ((this.isXYNode(3,1)) &&  ((this.rightChild.isXYNode(1,1))||(this.rightChild.isXYNode(2,1))));
		}
		
		/**
		 * public boolean isDeleteCase3L ()
		 *
		 * returns true if and only if the node is in DeleteCase3L
		 *
		 */
		public boolean isDeleteCase3L (){
			return ((this.isXYNode(1,3)) &&  ((this.leftChild.isXYNode(1,1))||(this.leftChild.isXYNode(1,2))));
		}
		
		/**
		 * public boolean isDeleteCase4R ()
		 *
		 * returns true if and only if the node is in DeleteCase4R
		 *
		 */
		public boolean isDeleteCase4R (){
			return ((this.isXYNode(3,1)) &&  (this.rightChild.isXYNode(1,2)));
		}
		
		/**
		 * public boolean isDeleteCase4L ()
		 *
		 * returns true if and only if the node is in DeleteCase4L
		 *
		 */
		public boolean isDeleteCase4L (){
			return ((this.isXYNode(1,3)) &&  (this.leftChild.isXYNode(2,1)));
		}
		
		/**
		 * public int localRebalanceAfterDelete()
		 *
		 * does all the rebalancing operations for the given node. problem may go up.
		 * returns the number of balancing operations done.
		 *
		 */	
		public int localRebalanceAfterDelete(){
			if (this.isLegalNode())
				return(0);
			else if (this.isLeaf()&&this.isXYNode(2,2)){
				this.demote();
				return(1);
			}
			else if (isDeleteCase1()){
				this.demote();
				return(1);
			}
			else if (isDeleteCase2R()){
				this.demote();
				this.rightChild.demote();
				return(2);
			}
			else if (isDeleteCase2L()){
				this.demote();
				this.leftChild.demote();
				return(2);
			}
			else if (isDeleteCase3R()){
				this.rightChild.rotateLeft();
				this.parent.promote();
				this.demote();
				if (this.isLeaf()&&this.isXYNode(2,2)){
					this.demote();
					return(2); //demote and promote of non-leaf are counted as part of rotate rebalncing operation
				}
				return(1);//demote and promote of non-leaf are counted as part of rotate rebalncing operation
			}
			else if (isDeleteCase3L()){
				this.leftChild.rotateRight();
				this.parent.promote();
				this.demote();
				if (this.isLeaf()&&this.isXYNode(2,2)){
					this.demote();
					return(2);//demote and promote of non-leaf are counted as part of rotate rebalncing operation
				}
				return(1);//demote and promote of non-leaf are counted as part of rotate rebalncing operation
			}
			else if (isDeleteCase4R()){
				this.rightChild.leftChild.doubleRotateLeft();
				this.demote();
				this.demote();
				this.parent.promote();
				this.parent.promote();
				this.parent.rightChild.demote();
				return (2); //demotes and promotes are counted as part of rotate rebalncing operation
			}
			else if (isDeleteCase4L()){
				this.leftChild.rightChild.doubleRotateRight();
				this.demote();
				this.demote();
				this.parent.promote();
				this.parent.promote();
				this.parent.leftChild.demote();
				return (2); //demotes and promotes are counted as part of rotate rebalncing operation
			}
			else
				return (0);
		}
		
		/**
		 * public int totalRebalanceAfterDelete()
		 *
		 * does all the rebalancing operations required in the tree from a given node up.
		 * returns the number of balancing operations done.
		 *
		 */	
		public int totalRebalanceAfterDelete(){
			if (this.isLegalNode())
				return 0;
			else{
				if (this.isRoot())
					return this.localRebalanceAfterDelete();
				else{
					WAVLNode parent = this.parent;
					int localRebalance = this.localRebalanceAfterDelete();
					int totalRebalance = parent.totalRebalanceAfterDelete(); // check if the problem went up and rebalance if needed
					return localRebalance + totalRebalance;
				}
			}
		}
		
		/**
		 * public int inOrderWalk(WAVLNode[] nodes, int i)
		 *
		 * Inserts the nodes to the given array.
		 * Returns an index in the array for the next recursive insertion. 
		 */
		public int inOrderWalk(WAVLNode[] nodes, int i) {		
			if (this.isExternalLeaf())
				return i;
			if (this.isLeaf()){
				nodes[i] = this;
				return i+1;
			}
			else{
				int j = this.leftChild.inOrderWalk(nodes, i);
				nodes[j] = this;
				return this.rightChild.inOrderWalk(nodes, j+1);
			}
		}
	}
}
	
