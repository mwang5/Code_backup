package Tetris;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 * This class peformed action interact with keyboard
 *
 * @author mwang5
 *
 */

@SuppressWarnings("serial")
public class Action extends AbstractAction{

	private DrawPanel _dp;
	
	public Action(String str, DrawPanel dp) {
		super(str);
		_dp = dp;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if (getValue(Action.NAME).equals("moveLeft"))
			_dp.moveLeft();
		else if (getValue(Action.NAME).equals("moveRight"))
			_dp.moveRight();
		else if (getValue(Action.NAME).equals("Rotate"))
			_dp.Rotate();
		else if (getValue(Action.NAME).equals("Accelerate"))
			_dp.Accelerate();
		else if (getValue(Action.NAME).equals("Drop"))
			_dp.Drop();
		else if (getValue(Action.NAME).equals("Pause"))
			_dp.Pause();
	}
}
