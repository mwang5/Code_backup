package Tetris;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This is the buttonlistener class
 *
 * @author mwang5
 *
 */

public class ButtonListener implements ActionListener{

	private DrawPanel _dp;
	public ButtonListener(DrawPanel dp) {
		_dp = dp;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if (e.getActionCommand().equals("Quit"))
			System.exit(0);
		if (e.getActionCommand().equals("Start NewGame")) {
			new App();
		}
		if (e.getActionCommand().equals("Stop")) {
			_dp.stop();
		}
		if (e.getActionCommand().equals("Pause")) {
			_dp.Pause();
		}
	}
}
