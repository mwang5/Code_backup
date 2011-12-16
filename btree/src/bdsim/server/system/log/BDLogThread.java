package bdsim.server.system.log;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import bdsim.server.system.BDSystemThread;
import bdsim.server.system.BDTuple;

/**
 * A thread used by the logging system to generate testing calls into the
 * logging subsystem.
 * 
 * @author wpijewsk
 */
final class BDLogThread extends BDSystemThread {

	private BDLogTester m_tester;
	private BDLogTransaction m_transaction;
	private BDLogManager m_logManager;
	private volatile boolean m_haveCrashed;
	private CyclicBarrier m_barrier;

	/**
	 * @return The transaction executing in this thread
	 */
	BDLogTransaction getTransaction() {
		return m_transaction;
	}

	public BDLogThread(int tid, BDLogTester tester,
			BDLogTransaction transaction, BDLogManager manager) {
		super(tid);
		m_tester = tester;
		m_transaction = transaction;
		m_haveCrashed = false;
		m_logManager = manager;
	}

	public void run() {

		/* Start the transaction. */
		m_logManager.startTransaction();
		System.out.println("[TID " + m_id + "] Started transaction");

		/* Lock all the tuples in order */
		SortedMap<Integer, BDTuple> tuplesToLock = new TreeMap<Integer, BDTuple>();
		for (BDLogOperation operation : m_transaction.getOperations()) {
			int id = (Integer) operation.getTuple().getField(
					BDLogTester.keyFieldName);
			if (!tuplesToLock.containsKey(id)) {
				tuplesToLock.put(id, operation.getTuple());
			}
		}
		for (BDTuple tuple : tuplesToLock.values()) {
			System.out.println("[TID " + m_id + "] About to lock tuple "
					+ tuple.getField(BDLogTester.keyFieldName));
			tuple.lock();
			System.out.println("[TID " + m_id + "] Locked tuple "
					+ tuple.getField(BDLogTester.keyFieldName));
		}
		
		/* Process each operation sequentially. */
		for (BDLogOperation operation : m_transaction.getOperations()) {

			if (!m_haveCrashed) {

				BDTuple tuple = operation.getTuple();

				/*
				 * Note that we need to synchronize on the tuple so the value
				 * doesn't change underneath us
				 */
				//synchronized (tuple) {
					int curVal = (Integer) tuple
							.getObject(BDLogTester.valueFieldName);
					int newVal = -1;

					switch (operation.getOpType()) {
					case ADD:
						newVal = curVal + operation.getAmount();
						break;
					case SUBTRACT:
						newVal = curVal - operation.getAmount();
						break;
					case EQUAL:
						newVal = operation.getAmount();
						break;
					default:
						System.err.println("Unrecognized BDOperationType: "
								+ operation.getOpType());
						System.exit(-1);
						break;
					}

					/* Call into log manager. */
					m_logManager.dataItemWritten(tuple,
							BDLogTester.valueFieldName, curVal, newVal);

					/* Update the tuple. */
					tuple.setObject(BDLogTester.valueFieldName, newVal);
					System.out.println("[TID " + m_id + "] Applying operation "
							+ operation + "; ("
							+ tuple.getObject(BDLogTester.keyFieldName) + ", "
							+ curVal + ") -> ("
							+ tuple.getObject(BDLogTester.keyFieldName) + ", "
							+ newVal + ")");
				//}
			}

			/* If I've crashed, stop immediately by returning */
			if (m_haveCrashed) {
				System.out.println("[TID " + m_id + "] CRASHED");
				
				/* Unlock all tuples */
				for(BDTuple lockedTuple : tuplesToLock.values()) {
					lockedTuple.unlock();
				}					
				
				try {
					System.out.println("About to wait on the barrier!");
					m_barrier.await();
					System.out.println("Release from the barrier!");
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					System.err.println("[TID " + m_id + "] returned from barrier");
				}
				m_tester.threadCrashing(m_transaction);				
				return;
			} else {
				/* Otherwise, yield so I can let another transaction run */
				try {
					Thread.sleep((long)(150 * Math.random()));
				} catch (InterruptedException e) {}
			}
		}

		synchronized (this) {
			/* Commit the transaction and clean up */
			m_logManager.commitTransaction();
			System.out.println("[TID " + m_id + "] Committed transaction");
			m_tester.threadEnding(m_transaction);
			
			/* Unlock all tuples */
			for(BDTuple tuple : tuplesToLock.values()) {
				tuple.unlock();
			}			
		}
	}

	public boolean equals(Object o) {
		if (!(o instanceof BDLogThread)) {
			return false;
		} else {
			BDLogThread other = (BDLogThread) o;
			return other.m_id == this.m_id;
		}
	}
	
	synchronized void crashThread() {
		m_haveCrashed = true;
	}

	void setBarrier(CyclicBarrier barrier) {
		m_barrier = barrier;
	}
}