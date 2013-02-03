package DoodleJump;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This is the Timer class. For each time tick,
 * check if the game is over(isEnd()) function in DrawPanel
 * class, if yes, move no distance of doodle and stop the timer. 
 * else keep jumping the doodle.
 * 
 * @author mwang5
 */

public class Timer extends javax.swing.Timer{
	
	private DrawPanel _dp;
	
	public Timer(DrawPanel dp) {
		super(Constants.TIMESTEP, null);
		_dp = dp;
		this.addActionListener(new TimerListenr());
	}
	private class TimerListenr implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (_dp.isEnd()) {
				_dp.moveDoodle(0, 0);
				_dp.stop();
				return;
			} else {
				_dp.doodlejump();
			}
		}
	}
}
