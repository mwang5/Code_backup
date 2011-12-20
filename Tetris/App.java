package Tetris;

import javax.swing.*;

/**
 * It's time for Tetris! This is the  main class to get things started.
 * The main method of this application calls the App constructor. You 
 * will need to fill in the constructor to instantiate your Tetris game.
 *
 * This main class calls mainpanel.
 *
 * @author mwang5
 * Did you discuss your design with another student?
 * If so, list their login here:
 *
 */

@SuppressWarnings("serial")
public class App extends JFrame{

	public App() {
		// Constructor code goes here.
		super("Tetris");
		this.setSize(700, 700);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MainPanel mainpanel = new MainPanel();
		this.add(mainpanel);
		this.pack();
		this.setVisible(true);
	}

	/*Here's the mainline!*/
	public static void main(String[] argv) {
		new App();
	}

}
