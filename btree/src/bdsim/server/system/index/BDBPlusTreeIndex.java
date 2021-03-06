/**
 * @author wpijewsk, ebuzek
 * BDBPlusTreeIndex
 * 
 * CS127: Implement the missing methods in BDBPlusTreeIndex.BDBPlusTree
 */
package bdsim.server.system.index;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import bdsim.server.system.BDSystem;
import bdsim.server.system.BDSystemResultSet;
import bdsim.server.system.BDSystemThread;
import bdsim.server.system.BDTable;
import bdsim.server.system.BDTuple;
import bdsim.server.system.concurrency.RollbackException;

import static java.lang.System.out;

@SuppressWarnings("unchecked")
public class BDBPlusTreeIndex implements BDIndex {
	//five incomplete methods in this inner class
	private class BDBPlusTree {
		
		/** 
		 * D-value of the tree. A leaf node can hold 2*d
		 * keys; an internal node can hold 2*d splitters. 
		 */
		private int m_d;
		
		/** 
		 * Is this a primary index? 
		 * cs127 hint: all of our indices are primary. Don't worry about duplicates.
		 */
		private boolean m_isPrimary;
		
		/** Root of the tree */
		private BDBPlusTreeNode m_root;
		
		/** Number of nodes in the whole tree */
		private int m_numNodes;
		
		/** 
		 * Number of completed disk operations
		 * Use these fields for the Analysis & Tuning part
		 * You will need to increment and output these values
		 * when needed. 
		 */
		protected int m_readNumDiskOps = 0; 
		protected int m_writeNumDiskOps = 0;
		
		/** 
		 * Saves the path of most recent call to find(), which prevents
		 * the need for parent pointers.
		 */
		private Vector<BDBPlusTreeNode> m_searchPath;
		
		/** 
		 * All leaves of the tree (for fast sequential access)
		 * cs127 hint: unimportant.
		 */
		private Vector<BDBPlusTreeNode> m_leaves;
		
		/**
		 * Constructs a B+-tree on the BDBPlusTreeIndex.m_keyName field of each
		 * tuple.
		 * 
		 * @param d The d-value of the tree. See m_d
		 * @param isPrimary Is this a primary index? 
		 */
		public BDBPlusTree(int d, boolean isPrimary) {
			m_d = d;
			m_isPrimary = isPrimary;
			m_root = new BDBPlusTreeNode(m_d, m_numNodes, true);
			m_leaves = new Vector<BDBPlusTreeNode>();
			m_searchPath = new Vector<BDBPlusTreeNode>();
		}

		public Vector<BDBPlusTreeNode> getLeaves() {
			return this.getLeavesHelper(m_root);
		}

		private Vector<BDBPlusTreeNode> getLeavesHelper(BDBPlusTreeNode  node) {
			Vector<BDBPlusTreeNode> leaves = new Vector<BDBPlusTreeNode>();
			if(node.isLeaf()) {
				leaves.add(node);
			} else {
				for(BDBPlusTreeNode child : node.getChildren()) {
					leaves.addAll(getLeavesHelper(child));
				}
			}
			
			return leaves;
		}

		public int getNumNodes() {
			return m_numNodes;
		}

		public BDBPlusTreeNode getRoot() {
			return m_root;
		}
		
		public Vector<BDBPlusTreeNode> getSearchPath() {
			return m_searchPath;
		}
		
		/**
		 * Delete the tuple t from the tree. This may involve merging or 
		 * redistributing nodes. The call does nothing if the tuple is not in 
		 * the tree. 
		 * 
		 * @param t Tuple to delete
		 * @throws InterruptedException 
		 */
		public void delete(BDTuple t) throws InterruptedException {

			// TODO Not yet implemented
			// throw new NotYetImplementedException();
			Comparable K = (Comparable)t.getObject(m_keyName);
			m_readNumDiskOps++;
			find (K);
			BDBPlusTreeNode L = m_searchPath.lastElement();
			delete_entry(L, K, t);
		}

		/**
		 * Delete tuple P associated with key K from leaf node N
		 * 
		 * @param N leaf node
		 * @param K comparable associated with tuple P
		 * @param P tuple to delete
		 * @throws InterruptedException 
		 */
		private void delete_entry(BDBPlusTreeNode N, Comparable K, BDTuple P) throws InterruptedException {

			// TODO Not yet implemented
			// throw new NotYetImplementedException();			
			if (N == getRoot()) {
				N.deleteTuple(K, P);
				m_writeNumDiskOps++;
				return;
			}
			
			int i;
			Comparable K_1 = null;
			boolean Nispredecessor = false;
			BDBPlusTreeNode N_1 = new BDBPlusTreeNode(m_d, m_numNodes, true);
			
			N.deleteTuple(K, P);
			m_writeNumDiskOps++;
			if (N.keyCount() < N.d()) {
				BDBPlusTreeNode parentN = m_searchPath.get(m_searchPath.indexOf(N) - 1);
				
				if (parentN == getRoot() && parentN.keyCount() == 1) {
					if (parentN.getKey(0).compareTo(K) > 0) {
						Nispredecessor = true;
						K_1 = parentN.getKey(0);
						N_1 = parentN.getChild(1);
					}
					else {
						K_1 = parentN.getKey(0);
						N_1 = parentN.getChild(0);
					}
					m_readNumDiskOps++;
				}
				else
				{
					for (i = 0; i < parentN.keyCount(); i++) {
						if (parentN.getKey(i).compareTo(K) > 0)
							break;
					}
					if (i == 0) {
						Nispredecessor = true;
						K_1 = parentN.getKey(i);
						N_1 = parentN.getChild(i + 1);
					}
					else {
						K_1 = parentN.getKey(i - 1);
						N_1 = parentN.getChild(i - 1); 
					}
					m_readNumDiskOps++;
				}

				/*entries in N and N_1 can fit in a single node*/
				if (N.keyCount() + N_1.keyCount() <= 2 * N.d()) {
					if (Nispredecessor == true) {
						BDBPlusTreeNode temp;
						temp = N;
						N = N_1;
						N_1 = temp;
					}
					for (int j = 0; j < N.keyCount(); j++) {
						N_1.insertTuple(N.getKey(j), N.getTuple(j));
					}
					m_writeNumDiskOps++;
					delete_entry(parentN, K_1, N);
				}
				else {
					if (Nispredecessor == false) {
						int m = N_1.keyCount() - 1;
						N.insertTuple(N_1.getKey(m), N_1.getTuple(m));
						m_writeNumDiskOps++;
						parentN.replace(K_1, N_1.getKey(m));
						m_writeNumDiskOps++;
						N_1.deleteTuple(N_1.getKey(m), N_1.getTuple(m));
						m_writeNumDiskOps++;
					}
					else {
						int m = 0;
						N_1.deleteTuple(N_1.getKey(m), N_1.getTuple(m));
						m_writeNumDiskOps++;
						N.insertTuple(N_1.getKey(m), N_1.getTuple(m));
						m_writeNumDiskOps++;
						parentN.replace(K_1, N_1.getKey(m));
						m_writeNumDiskOps++;
					}
				}
			}
		}

		/**
		 * Delete node P associated with key K from inner node N
		 * 
		 * @param P node to delete
		 * @param K comparable associated with node P
		 * @param N inner node
		 * @throws InterruptedException 
		 */
		private void delete_entry(BDBPlusTreeNode N, Comparable K, BDBPlusTreeNode P) throws InterruptedException {
			
			// TODO Not yet implemented
			// throw new NotYetImplementedException();
			int i;
			Comparable K_1 = null;
			boolean Nispredecessor = false;
			
			N.deleteChild(K, P);
			m_writeNumDiskOps++;
			
			BDBPlusTreeNode N_1 = new BDBPlusTreeNode(m_d, m_numNodes, true);

			if (N == getRoot() && N.childCount() == 1) {
				m_root = N.getChild(0);
				m_writeNumDiskOps++;
				N.clear();
			}
			else if (N == getRoot()) {
				return;
			}
			else if (N.keyCount() < N.d()) {
				BDBPlusTreeNode parentN = m_searchPath.get(m_searchPath.indexOf(N) - 1);
				
				if (parentN == getRoot() && parentN.keyCount() == 1) {
					if (parentN.getKey(0).compareTo(K) > 0) {
						Nispredecessor = true;
						K_1 = parentN.getKey(0);
						N_1 = parentN.getChild(1);
					}
					else {
						K_1 = parentN.getKey(0);
						N_1 = parentN.getChild(0);
					}
					m_readNumDiskOps++;
				}
				else {
					for (i = 0; i < parentN.keyCount(); i++) {
						if (parentN.getKey(i).compareTo(K) > 0)
							break;
					}
	
					if (i == 0) {
						Nispredecessor = true;
						K_1 = parentN.getKey(i);
						N_1 = parentN.getChild(i + 1);
					}
					else {
						K_1 = parentN.getKey(i - 1);
						N_1 = parentN.getChild(i - 1); 
					}
					m_readNumDiskOps++;
				}
				
				if (N.keyCount() + N_1.keyCount() + 1 <= 2 * N.d()) {
					if (Nispredecessor == true) {
						BDBPlusTreeNode temp;
						temp = N;
						N = N_1;
						N_1 = temp;
					}
					N_1.insertKey(K_1);
					for (int j = 0; j < N.keyCount(); j++) {
						N_1.insertKey(N.getKey(j));
						N_1.insertChild(N.getKey(j), N.getChild(j));
					}
					N_1.insertChild(N.getKey(N_1.keyCount() - 1), N.getChild(N.keyCount()));
//					N_1.insertChild(N.getKey(N.keyCount() - 1), N.getChild(N.keyCount()));
					m_writeNumDiskOps++;
					delete_entry(parentN, K_1, N);
					N.clear();
				}
				/*not fits one node*/
				else {
					if (Nispredecessor == false) {
						int m = N_1.keyCount();	
						parentN.replace(K_1, N_1.getKey(m - 1));
						m_writeNumDiskOps++;
						N.insertChild(K_1, N_1.getChild(m));
						m_writeNumDiskOps++;
						N_1.deleteChild(N_1.getKey(m - 1), N_1.getChild(m));
						m_writeNumDiskOps++;
					}
					else {
						int m = 0;
						parentN.replace(K_1, N_1.getKey(m));
						m_writeNumDiskOps++;	
						N.insertChild(K_1, N_1.getChild(m));
						m_writeNumDiskOps++;
						N_1.deleteChild(N_1.getKey(m), N_1.getChild(m));
						m_writeNumDiskOps++;
					}
				}
			}
		}

		/**
		 * Add the tuple t to the tree. This may involve splitting nodes. 
		 * Duplicates are not allowed on a primary index.
		 * 
		 * @param t Tuple to insert
		 * @throws InterruptedException
		 */
		public void insert(BDTuple t) throws InterruptedException {
			
			// TODO Not yet implemented
			// throw new NotYetImplementedException();
			Comparable K = (Comparable)t.getObject(m_keyName);
			m_readNumDiskOps++;
			if (find(K) == true)
				return;
			
			BDBPlusTreeNode L = m_searchPath.lastElement(); //Get the leaf node since searchPath stores every node
			
			if (L.keyCount() < 2 * m_d) {
				L.insertTuple(K, t);
				m_writeNumDiskOps++;
			}
			
			else {
				BDBPlusTreeNode L_1 = new BDBPlusTreeNode(m_d, m_numNodes, true);
				BDBPlusTreeNode B = new BDBPlusTreeNode(m_d + 1, m_numNodes, false);
				/*copy*/
				for (int i = 0; i < L.keyCount(); i++){
					B.insertTuple(L.getKey(i), L.getTuple(i));
				}	
				B.insertTuple(K, t);
				m_writeNumDiskOps++;
				L.clear();
				/*L and L_1*/
				for (int i = 0; i < B.keyCount() / 2; i++) {
					L.insertTuple(B.getKey(i), B.getTuple(i));
				}
				m_writeNumDiskOps++;
				for (int j = B.keyCount() / 2; j < B.keyCount(); j++) {
					L_1.insertTuple(B.getKey(j), B.getTuple(j));
				}
				m_writeNumDiskOps++;
				
				Comparable K_1 = L_1.getKey(0);
				m_readNumDiskOps++;
				insert_in_parent(L, K_1, L_1);
			}
		}

		/**
		 * Helper function to add the tuple if the node requires splitting
		 * 
		 * @param n Primary node to insert into
		 * @param kprime Key-value comparable associated with the splitting node
		 * @param nprime Splitter node to insert into
		 * @throws InterruptedException
		 */
		private void insert_in_parent(BDBPlusTreeNode N, Comparable Kprime, BDBPlusTreeNode Nprime) {

			// TODO Not yet implemented
			// throw new NotYetImplementedException();
			if (N == getRoot()) {
				BDBPlusTreeNode R = new BDBPlusTreeNode(m_d, m_numNodes, false);
				R.makeRoot(N, Nprime, Kprime);
				m_root = R;
				m_writeNumDiskOps++;
				return;
			}
			BDBPlusTreeNode P = m_searchPath.get(m_searchPath.indexOf(N) - 1); 
 			
			/*if P pointer size less than 2 * m_d + 1*/
			if (P.getChildren().size() < 2 * m_d + 1) {
				P.insertChild(Kprime, Nprime);
				m_writeNumDiskOps++;
			}
			
			else {
				BDBPlusTreeNode T = new BDBPlusTreeNode(m_d + 1, m_numNodes, false);
				for (int i = 0; i < P.keyCount(); i++) {
					T.insertChild(P.getKey(i), P.getChild(i));
				}
				T.insertChild(P.getKey(P.keyCount() - 1), P.getChild(P.keyCount()));
				T.insertChild(Kprime, Nprime);
				m_writeNumDiskOps++;
				P.clear();
				
				for (int i = 0; i < T.keyCount() / 2; i++)
					P.insertChild(T.getKey(i), T.getChild(i));
				P.insertChild(T.getKey(T.keyCount() / 2 - 1), T.getChild(T.keyCount() / 2));
				m_writeNumDiskOps++;
				
				BDBPlusTreeNode P_1 = new BDBPlusTreeNode(m_d, m_numNodes, false);
				Comparable K_1 = T.getKey(T.keyCount() / 2);
				m_readNumDiskOps++;
				
				for (int i = T.keyCount() /2 + 1; i < T.keyCount(); i++) 
					P_1.insertChild(T.getKey(i), T.getChild(i));
				P_1.insertChild(T.getKey(T.keyCount() - 1), T.getChild(T.keyCount()));
				m_writeNumDiskOps++;
				insert_in_parent(P, K_1, P_1);
			}
		}

		/**
		 * Searches the tree. Saves the search path in m_searchPath.
		 * 
		 * @param value to find
		 * @return Whether or not the value is in the tree
		 */
		public boolean find(Comparable value) {
			
			// TODO Not yet implemented
			// throw new NotYetImplementedException();
			// 3 readOps;
			m_searchPath.clear();
			BDBPlusTreeNode C = getRoot();

			int flag = 0, i, m, n;
			m_searchPath.add(C);
			while (!C.isLeaf()) {
				for(i = 0; i < C.keyCount(); i++) {
					if (C.getKey(i).compareTo(value) > 0) {
						/*if find key value greater than value then flag++. if flag = 0 means not found*/
						flag++;
						break;
					}
				}
				if (flag == 0) {
					m = C.getChildren().size() - 1;
					C = C.getChild(m);
				}
				else {
					C = C.getChild(i);
				}
				m_readNumDiskOps++;
				m_searchPath.add(C);
			}
			for (n = 0; n < C.keyCount(); n++) {
				if (C.getKey(n).compareTo(value) == 0) {
					m_readNumDiskOps++;
					return true;	
				}
			}
			m_readNumDiskOps++;
			return false;
		}	
		
		public int NumRead() {
			return m_readNumDiskOps;
		}
		
		public int NumWrite() {
			return m_writeNumDiskOps;
		}
	}
		
	/*
	 * CS127 NOTE: You don't have to worry about the code below this line.
	 * ------------------------------------------------------------------- 
	 */
	
	public Map<Integer, List<BDTuple>> m_delete_shadows;
	
	public Map<Integer, List<BDTuple>> m_insert_shadows;

	private Logger m_logger;
	
	/** Name of the key on which this is an index */
	private String m_keyName;
	
	/** The workhorse of the index */
	private BDBPlusTree m_tree;

	/** Tree visualizer (may be null) */
	private BDBPlusTreeVisualizer m_visualizer;

	public BDBPlusTreeIndex(BDTable table, int d, String keyName,
			boolean isPrimary) {
		m_logger = Logger.getLogger(BDBPlusTree.class);
		m_insert_shadows = new HashMap<Integer, List<BDTuple>>();
		m_delete_shadows = new HashMap<Integer, List<BDTuple>>();
		m_tree = new BDBPlusTree(d, isPrimary);
		m_keyName = keyName;
	}
	
	public void printOps() {
		System.out.println("Num of Read: " + m_tree.NumRead() + " and Num of Write: " + m_tree.NumWrite());
	}
	
	
	/**
	 * Actually DOES operations on the tree which are queued in m_*_shadows.
	 * @throws RollbackException 
	 */
	public void commit(int TID) throws InterruptedException, RollbackException {
		if (m_delete_shadows.get(TID) != null) {
			for (BDTuple t : m_delete_shadows.get(TID)) {
				BDSystem.concurrencyController.writeDataItem(t);
			}
		}
		if (m_insert_shadows.get(TID) != null) {
			for (BDTuple t : m_insert_shadows.get(TID)) {
				BDSystem.concurrencyController.writeDataItem(t);
			}
		}
		
		
		if (m_delete_shadows.get(TID) != null) {
			for (BDTuple t : m_delete_shadows.get(TID)) {
				deleteNow(t);
			}
		}
		if (m_insert_shadows.get(TID) != null) {
			for (BDTuple t : m_insert_shadows.get(TID)) {
				insertNow(t);
			}
		}

		m_delete_shadows.put(TID, null);
		m_insert_shadows.put(TID, null);
	}

	/**
	 * Deletes enqueued operations.
	 */
	public void rollback(int TID) {
		m_insert_shadows.put(TID, null);
		m_delete_shadows.put(TID, null);
	}

	/**
	 * Enqueues the deletion of tuple t from the index.
	 */
	public void delete(BDTuple t) {
		BDSystemThread thread = (BDSystemThread)(Thread.currentThread());
		int TID = thread.getTransactionId();
		if(m_delete_shadows.get(TID) == null) {
			m_delete_shadows.put(TID, new LinkedList<BDTuple>());
		}
		m_delete_shadows.get(TID).add(t);
	}
	
	/**
	 * Actually removes the tuple from the index, ignoring concurrency.
	 */
	public void deleteNow(BDTuple t) throws InterruptedException {
		m_tree.delete(t);
		pokeVisualizer();
	}
	
	/**
	 * Enqueues the insertion of tuple t from the index.
	 */
	public void insert(BDTuple t) {
		BDSystemThread thread = (BDSystemThread)(Thread.currentThread());
		int TID = thread.getTransactionId();
		if(m_insert_shadows.get(TID) == null) {
			m_insert_shadows.put(TID, new LinkedList<BDTuple>());
		}
		m_insert_shadows.get(TID).add(t);
	}

	/**
	 * Actually inserts the tuple into the index, ignoring concurrency.
	 */
	public void insertNow(BDTuple t) throws InterruptedException {
		m_tree.insert(t);
		pokeVisualizer();
	}
	
	public IndexType getIndexType() {
		return IndexType.B_PLUS_TREE;
	}

	public String getKeyName() {
		return m_keyName;
	}
	
	public BDBPlusTreeNode getRoot() {
		return m_tree.getRoot();
	}

	/**
	 * Note: this method makes a copy of the tuples, leaving the caller free to
	 * mess with these tuples.
	 * 
	 * @return All tuples in the index
	 * @throws RollbackException 
	 */
	public BDSystemResultSet getAllTuples() throws InterruptedException, RollbackException {
		
		BDSystemThread thread = (BDSystemThread)(Thread.currentThread());
		int TID = thread.getTransactionId();
		
		BDSystemResultSet result = new BDSystemResultSet();

		for (BDBPlusTreeNode node : m_tree.getLeaves()) {
			for (int i = 0; i < node.keyCount(); i++) {

				m_logger.debug("Values: " + node.keyCount());
				m_logger.debug("Getting a tuple...");
				m_logger.debug(node.getTuple(i));
				
				if(m_delete_shadows.get(TID) == null ||
				 !(m_delete_shadows.get(TID).contains(node.getTuple(i)))) {
					result.addRow(node.getTuple(i));
				}
			}
		}
		if(m_insert_shadows.get(TID) != null) {
			for(BDTuple tx : m_insert_shadows.get(TID)) {
				result.addRow(tx);
			}
		}
		return result;
	}
	
	/**
	 * @return All tuples in the index right now, ignoring shadows 
	 * @throws RollbackException 
	 */
	public BDSystemResultSet getAllTuplesUnchecked() {

		BDSystemResultSet result = new BDSystemResultSet();

		for (BDBPlusTreeNode node : m_tree.getLeaves()) {
			for (int i = 0; i < node.keyCount(); i++) {
				try {
					result.addRow(node.getTuple(i));
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (RollbackException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	

	/**
	 * Selects all tuples from the index that fall within a given range in some
	 * field.
	 * 
	 * @param rtype The type of range (less-than, greater-than, etc)
	 * @param field The field on which to filter 
	 * @param value The value to be compared against (i.e. the range's bound)
	 * @return All tuples in the range
	 * @throws RollbackException 
	 */
	public BDSystemResultSet getTuplesByRange(RangeType rtype, String field,
			Comparable value) throws InterruptedException, RollbackException {
		Collections.sort(m_tree.getLeaves());
		BDSystemThread thread = (BDSystemThread)(Thread.currentThread());
		int TID = thread.getTransactionId();
		BDSystemResultSet result = new BDSystemResultSet();
		BDBPlusTreeNode currentNode;
		boolean moreNodes = false;
		int lastIndex = 0;

		switch (rtype) {
		case GT:
			m_tree.find(value);
			currentNode = m_tree.getSearchPath().lastElement();
			//curr is now the leaf node that should have value
			for (int i = 0; i < currentNode.keyCount(); i++) {
				if (currentNode.getKey(i) == null) continue;
				if (currentNode.getKey(i).compareTo(value) > 0) {
					result.addRow(currentNode.getTuple(i));
					if (i == (currentNode.keyCount() - 1)) {
						moreNodes = true;
					}
				}
			}
			while (moreNodes == true
					&& (m_tree.getLeaves().indexOf(currentNode) < (m_tree.getLeaves().size() - 1))) {
				currentNode = m_tree.getLeaves()
						.elementAt(m_tree.getLeaves().indexOf(currentNode) + 1);
				moreNodes = false;
				for (int i = 0; i < currentNode.keyCount(); i++) {
					if (currentNode.getKey(i) == null) continue;
					if (currentNode.getKey(i).compareTo(value) > 0) {
						result.addRow(currentNode.getTuple(i));
						if (i == (currentNode.keyCount() - 1)) {
							moreNodes = true;
						}
					}
				}
			}
			
			if(m_insert_shadows.get(TID) != null) {
				for(BDTuple tx : m_insert_shadows.get(TID)) {			
					if(tx.getField(field) != null) {
						if (((Comparable)(tx.getField(field))).compareTo(value) > 0) {
							result.addRow(tx);
						}
					}
				}
			}
			break;

		case GTEQ:
			m_tree.find(value);
			currentNode = m_tree.getSearchPath().lastElement();
			//curr is now the leaf node that should have value
			for (int i = 0; i < currentNode.keyCount(); i++) {
				if (currentNode.getKey(i) == null) continue;
				if (currentNode.getKey(i).compareTo(value) >= 0) {
					result.addRow(currentNode.getTuple(i));
					if (i == (currentNode.keyCount() - 1)) {
						moreNodes = true;
					}
				}
			}
			while (moreNodes == true
					&& (m_tree.getLeaves().indexOf(currentNode) < (m_tree.getLeaves().size() - 1))) {
				currentNode = m_tree.getLeaves()
						.elementAt(m_tree.getLeaves().indexOf(currentNode) + 1);
				moreNodes = false;
				for (int i = 0; i < currentNode.keyCount(); i++) {
					if (currentNode.getKey(i) == null) continue;
					if (currentNode.getKey(i).compareTo(value) >= 0) {
						result.addRow(currentNode.getTuple(i));
						if (i == (currentNode.keyCount() - 1)) {
							moreNodes = true;
						}
					}
				}
			}
			if(m_insert_shadows.get(TID) != null) {
				for(BDTuple tx : m_insert_shadows.get(TID)) {
					if(tx.getField(field) != null) {
						if (((Comparable)(tx.getField(field))).compareTo(value) >= 0) {
							result.addRow(tx);
						}
					}
				}
			}
			break;

		case LT:
			System.out.println("LT on primary key: ");
			m_tree.find(value);
			currentNode = m_tree.getSearchPath().lastElement();
			//curr is now the leaf node that should have value
			for (int i = 0; i < currentNode.keyCount(); i++) {
				if (currentNode.getKey(i) == null) continue;
				if (currentNode.getKey(i).compareTo(value) <= 0) {
					//if(currentNode.value(i).compareTo(value) < 0)
						//result.addRow(currentNode.getTuple(i));
					if (i == 0) {
						moreNodes = true;						
						System.out.println("More nodes...");
					}
				}
			}
			lastIndex = m_tree.getLeaves().indexOf(currentNode);
			currentNode = m_tree.getLeaves().firstElement();
			while (moreNodes == true
					&& (m_tree.getLeaves().indexOf(currentNode) <= lastIndex)) {
				System.out.println("Inspecting next node...");
				//currentNode = m_tree.getLeaves().elementAt(m_tree.getLeaves().indexOf(currentNode) + 1);
				moreNodes = false;
				for (int i = 0; i < currentNode.keyCount(); i++) {
					System.out.println("Comparing: " + currentNode.getKey(i) + " to " + value);
					if (currentNode.getKey(i) == null) continue;
					if (currentNode.getKey(i).compareTo(value) < 0) {						
						result.addRow(currentNode.getTuple(i));
						if (i == (currentNode.keyCount() - 1)) {
							moreNodes = true;
							System.out.println("Even more nodes...");
						}
					}
				}
				if(moreNodes) {
					currentNode = m_tree.getLeaves().elementAt(m_tree.getLeaves().indexOf(currentNode) + 1);
				}
			}
			if(m_insert_shadows.get(TID) != null) {
				for(BDTuple tx : m_insert_shadows.get(TID)) {
					if(tx.getField(field) != null) {
						if (((Comparable)(tx.getField(field))).compareTo(value) < 0) {
							result.addRow(tx);
						}
					}
				}
			}
			break;

		case LTEQ:
			System.out.println("LT on primary key: ");
			m_tree.find(value);
			currentNode = m_tree.getSearchPath().lastElement();
			//curr is now the leaf node that should have value
			for (int i = 0; i < currentNode.keyCount(); i++) {
				if (currentNode.getKey(i) == null) continue;
				if (currentNode.getKey(i).compareTo(value) <= 0) {
					//if(currentNode.value(i).compareTo(value) < 0)
						//result.addRow(currentNode.getTuple(i));
					if (i == 0) {
						moreNodes = true;						
						System.out.println("More nodes...");
					}
				}
			}
			lastIndex = m_tree.getLeaves().indexOf(currentNode);
			currentNode = m_tree.getLeaves().firstElement();
			while (moreNodes == true
					&& (m_tree.getLeaves().indexOf(currentNode) <= lastIndex)) {
				System.out.println("Inspecting next node...");
				//currentNode = m_tree.getLeaves().elementAt(m_tree.getLeaves().indexOf(currentNode) + 1);
				moreNodes = false;
				for (int i = 0; i < currentNode.keyCount(); i++) {
					System.out.println("Comparing: " + currentNode.getKey(i) + " to " + value);
					if (currentNode.getKey(i) == null) continue;
					if (currentNode.getKey(i).compareTo(value) <= 0) {
						result.addRow(currentNode.getTuple(i));
						if (i == currentNode.keyCount()) {
							moreNodes = true;
							System.out.println("Even more nodes...");
						}
					}
				}
				if(moreNodes) {
					currentNode = m_tree.getLeaves().elementAt(m_tree.getLeaves().indexOf(currentNode) + 1);
				}
			}
			if(m_insert_shadows.get(TID) != null) {
				for(BDTuple tx : m_insert_shadows.get(TID)) {
					if(tx.getField(field) != null) {
						if (((Comparable)(tx.getField(field))).compareTo(value) <= 0) {
							result.addRow(tx);
						}
					}
				}
			}
			break;

		case NEQ:
			for (BDBPlusTreeNode node : m_tree.getLeaves()) {
				for (int i = 0; i < node.keyCount(); i++) {
					if (node.getKey(i) == null) continue;
					if (value.compareTo(node.getKey(i)) != 0)
						result.addRow(node.getTuple(i));
				}
			}
			if(m_insert_shadows.get(TID) != null) {
				for(BDTuple tx : m_insert_shadows.get(TID)) {
					if(tx.getField(field) != null) {
						if (((Comparable)(tx.getField(field))).compareTo(value) != 0) {
							result.addRow(tx);
						}
					}
				}
			}
			break;
		}

		if(m_delete_shadows.get(TID) != null) {
			for(BDTuple tx : m_delete_shadows.get(TID)) {
				if(result.hasTuple(tx))
					result.remove(tx);
			}
		}
		
		return result;
	}	

	/**
	 * @return All tuples whose [m_keyName] field is equal to [value]
	 * @throws RollbackException 
	 */
	public BDSystemResultSet getTuplesByValue(Comparable value) throws InterruptedException, RollbackException {
		
		BDSystemThread thread = (BDSystemThread)(Thread.currentThread());
		int TID = thread.getTransactionId();
		
		BDSystemResultSet returnSet = new BDSystemResultSet();

		m_tree.getSearchPath().clear();
		BDBPlusTreeNode curr = m_tree.getRoot();
		m_tree.getSearchPath().add(curr);
		int i;
		while (!curr.isLeaf()) {
			assert (curr.keyCount() == (curr.childCount() - 1));
			for (i = 0; i < curr.keyCount(); i++) {
				//if (curr.getValue(i) == null) continue;
				//System.out.println("i: " + i + " out of: " + curr.valueCount());
			
				if (curr.getKey(i).compareTo(value) > 0) break;
			}
			if (i == curr.keyCount()) //Reached the end, nothing greater here
				curr = curr.getChild(i);
			else
				//broke on value(i) > value
				curr = curr.getChild(i);
			m_tree.getSearchPath().add(curr);
		}
		
		//curr is now the leaf node that should have value
		for (i = 0; i < curr.keyCount(); i++) {
			//if (curr.getValue(i) == null) continue;
			if (curr.getKey(i).compareTo(value) == 0) {
				returnSet.addRow(curr.getTuple(i));
			}
		}
		
		
		if(m_insert_shadows.get(TID) != null) {
			for(BDTuple tx : m_insert_shadows.get(TID)) {
				if(tx.getField(m_keyName) != null) {
					if(((Comparable)(tx.getField(m_keyName))).compareTo(value) == 0) {
						returnSet.addRow(tx);
					}
				}
			}				
		}
		if(m_delete_shadows.get(TID) != null) {
			for(BDTuple tx : m_delete_shadows.get(TID)) {
				if(returnSet.hasTuple(tx))
					returnSet.remove(tx);
			}
		}
		
		return returnSet;
	}
	
	/**
	 * @return All tuples whose [field] field is equal to [value]
	 * @throws RollbackException 
	 */
	public BDSystemResultSet getTuplesByValue(String field, Comparable value) throws InterruptedException, RollbackException {

		BDSystemThread thread = (BDSystemThread)(Thread.currentThread());
		int TID = thread.getTransactionId();
		
		if (field.equals(m_keyName))
			return this.getTuplesByValue(value);
		
		//System.out.println("It's not a primary key.");
		
		BDTuple t;
		BDSystemResultSet result = new BDSystemResultSet();
		for (BDBPlusTreeNode n : m_tree.getLeaves()) {
			for (int i = 0; i < n.keyCount(); i++) {
				t = n.getTuple(i);
				if (t.getObject(field) == null) continue;
				if (((Comparable) t.getObject(field)).compareTo(value) == 0) {
					result.addRow(t);
				}
			}
		}
		if(m_insert_shadows.get(TID) != null) {
			for(BDTuple tx : m_insert_shadows.get(TID)) {
				if(tx.getField(m_keyName) != null) {
					if(((Comparable)(tx.getField(field))).compareTo(value) == 0) {
						result.addRow(tx);
					}
				}
			}				
		}
		if(m_delete_shadows.get(TID) != null) {
			for(BDTuple tx : m_delete_shadows.get(TID)) {
				if(result.hasTuple(tx))
					result.remove(tx);
			}
		}
		return result;
	}
	
	/**
	 * Delete oldTuple, insert newTuple
	 */
	public void replace(BDTuple oldTuple, BDTuple newTuple) {
		this.delete(oldTuple);
		this.insert(newTuple);
	}

	public void setVisualizer(BDBPlusTreeVisualizer visualizer) {
		m_visualizer = visualizer;
	}
	
	private void pokeVisualizer() {
		if (m_visualizer != null) {
			m_visualizer.treeUpdatedHandler();
		}
	}

	/**
	 * Removes a tuple from either the insert shadows or the delete shadows
	 * 
	 * @param tuple
	 *            The tuple to remove
	 */
	public void abandon(BDTuple tuple) {
		m_insert_shadows.remove(tuple);
		m_delete_shadows.remove(tuple);	
	}
	
	
	/**
	 * cs127, ebuzek, 2011
	 * NotYetImplementedException
	 * 
	 * only to mark TODOs in the stencil code
	 */
	private class NotYetImplementedException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		/**
		 * NotYetImplementedException()
		 * 
		 * creates exception, the second last method name of the 
		 * current StackTrace will be in the message
		 */
		public NotYetImplementedException() {
			super(String.format("cs127: Please implement %s.", Thread.currentThread().getStackTrace()[1].getMethodName()));
		}
	}
}


