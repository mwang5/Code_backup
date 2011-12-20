package Tetris;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Timer class
 *
 * @author mwang5
 *
 */

@SuppressWarnings("serial")
public class Timer extends javax.swing.Timer {

	private DrawPanel _dp;
	
	public Timer(DrawPanel dp) {
		super(400, null);
		_dp = dp;
		this.addActionListener(new TimerListenr());
	}
	private class TimerListenr implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			if (_dp.IsEnd()) {
				_dp.moveBlock(0, 0);
				_dp.stop();
				return;
			}
			if (_dp.IsValid(0, Constants.SIZE))
				_dp.moveBlock(0, Constants.SIZE); 
			else {
				_dp.updategird();
				_dp.fullLine();

			}
		}
	}
}
