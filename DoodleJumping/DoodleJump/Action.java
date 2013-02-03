package DoodleJump;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 * This is Action class which detect Action name defined 
 * in DrawPanel
 * 
 * @author mwang5
 */

public class Action extends AbstractAction{

	private DrawPanel _dp;
	
	public Action(String str, DrawPanel dp) {
		super(str);
		_dp = dp;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (getValue(Action.NAME).equals("moveLeft")) {
			_dp.moveLeft();
		} else if (getValue(Action.NAME).equals("moveRight")) {
			_dp.moveRight();
		} else if (getValue(Action.NAME).equals("Pause")) {
			_dp.Pause();
		}
	}
}
