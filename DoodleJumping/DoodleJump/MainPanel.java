package DoodleJump;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * This is the MainPanel class. MainPanel contains 
 * three panels which are uppanel contains the score,
 * drawpanel contains the main canvas with activities 
 * and subpanel contains three button of "PLAY", "PAUSE"
 * and "QUIT". 
 * 
 * @author mwang5
 */


public class MainPanel extends JPanel {
	
	private DrawPanel _dp;
	//This JLabel is used for display game over or pause
	private JLabel _label;
	//Planned display score
	private JLabel _score;
	//Used for storing current score
	private String _tmp;
	//Score for pass each platform
	private int _num;
	
	public MainPanel() {
		super();
		//Use BorderLayout() in MainPanel
		this.setLayout(new BorderLayout());
		_dp = new DrawPanel(this);
		Border border = new EtchedBorder(EtchedBorder.RAISED, 
				Color.WHITE, new Color(148, 145, 140));
		//this JLabel is used for display game over or pause
		_label = new JLabel("");
		_label.setLocation(_dp.getX()/2, _dp.getY()/2);

		JLabel score = new JLabel("Score:");
		_tmp = "0";
		//_num;
		_score = new  JLabel(_tmp);
		_dp.add(_label);

		//PLAY will create a new instance of App()
		JButton start = new JButton("PLAY");
		start.addActionListener(new StartButtonListener());
		
		//PAUSE will stop the timer; press again to resume
		JButton pause = new JButton("PAUSE");
		pause.addActionListener(new PauseButtonListener());
		
		//QUIT is exit the game
		JButton quit = new JButton("QUIT");
		quit.addActionListener(new ExitButtonListener());
		
		//FlowLayout() for uppanel and subpanel
		JPanel uppanel = new JPanel(new FlowLayout());
		JPanel subpanel = new JPanel(new FlowLayout());

		uppanel.add(score);
		uppanel.add(_score);
		uppanel.setBorder(border);
		subpanel.add(start);
		subpanel.add(pause);
		subpanel.add(quit);
		subpanel.setBorder(border);
		
		this.add(uppanel,BorderLayout.PAGE_START);
		this.add(_dp, BorderLayout.CENTER);
		this.add(subpanel, BorderLayout.PAGE_END);
	}
	//Inner classes for button listener
	private class ExitButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}
	private class PauseButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(_dp.isEnd()) {
				return;
			} else {
				_dp.Pause();
			}
		}
	}
	private class StartButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			new App();
		}
	}
	
	public void GameOverText() {
		_label.setText("Game Over! Press Start to play again! ");
	}
	public void PauseText() {
		_label.setText("Paused!");
	}
	public void setScore() {
		_num = Integer.parseInt(_tmp); 
		_num = _num + 100;
		_tmp = new Integer(_num).toString();
		_score.setText(_tmp);
	}
	public void ResumeText() {
		_label.setText("");
	}
}
