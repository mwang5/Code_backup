package Tetris;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * MainPanel has two components DrawPanel and eastPanel, three Panels in 
 * eastPanel include tipanel to show next block(doesnt implement)
 * infopanel for level and score(doesnt implement) and buttonpanel
 * 
 * @author mwang5
 * 
 */

@SuppressWarnings("serial")
public class MainPanel extends JPanel{

	private DrawPanel _dp;
	private JLabel _label;
	
	public MainPanel() {
		
		super();
		_dp = new DrawPanel(this);
		this.setLayout(new GridLayout(1,0));
		JPanel eastPanel = new JPanel(new GridLayout(3, 0));
		Border border = new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(148, 145, 140));
		_label = new JLabel("");
		
		JTextField score = new JTextField("0");
		JTextField level = new JTextField("" + Constants.DEFAULT_LEVEL);
		score.setEditable(false);
		level.setEditable(false);
		
		JButton levelup = new JButton("Turn Harder");
		JButton leveldown = new JButton("Turn Easier");
		
		JButton start = new JButton("Start NewGame");
		JButton pause = new JButton("Pause");
		JButton stop = new JButton("Stop");
		JButton quit = new JButton("Quit");
		
		
		/*eastpanel's sub-panel*/
		JPanel tipanel = new JPanel(new BorderLayout());
		JPanel infopanel = new JPanel(new GridLayout(4, 1));
		JPanel subpanel = new JPanel(new GridLayout(3, 1));
		
		tipanel.add(new JLabel("Next Block"), BorderLayout.NORTH);
		/*need add something*/
		tipanel.setBorder(border);
		
		infopanel.add(new JLabel("level"));
		infopanel.add(level);
		infopanel.add(new JLabel("Score"));
		infopanel.add(score);
		infopanel.setBorder(border);
		
		subpanel.add(levelup);
	    subpanel.add(leveldown);
	    subpanel.add(start);
	    subpanel.add(pause);
	    subpanel.add(stop);
	    subpanel.add(quit);
	    subpanel.setBorder(border);
	    
	    eastPanel.add(tipanel);
	    eastPanel.add(infopanel);
	    eastPanel.add(subpanel);
	    
		quit.addActionListener(new ButtonListener(_dp)); 
		start.addActionListener(new ButtonListener(_dp));
		pause.addActionListener(new ButtonListener(_dp));
		stop.addActionListener(new ButtonListener(_dp));
		
	    _dp.add(_label);
		this.add(_dp);
		this.add(eastPanel);
	}
	
	public void GameOverText() {
		_label.setText("Game Over!");
	}
	
	public void PauseText() {
		_label.setText("Paused!");
	}
	
	public void ResumeText() {
		_label.setText("");
	}
	
}