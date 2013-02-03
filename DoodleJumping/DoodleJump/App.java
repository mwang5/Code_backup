package DoodleJump;

import javax.swing.JFrame;

/**
 * This is the  main class where your DoodleJump game will start.
 * The main method of this application calls the App constructor. You 
 * will need to fill in the constructor to instantiate your game.
 *
 * Work done beyond basic requirement:
 * 1. Start new game, pause the game
 *
 * @author mwang5
 * Did you discuss your design with another student?
 * If so, list their login here:
 *
 */

public class App extends JFrame {

	public App() {
		//Constructor code goes here
		super("Dooooo0odle Jump!");
		this.setSize(400, 700);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MainPanel panel = new MainPanel();
		this.add(panel);
		this.pack();
		this.setVisible(true);
	}

	/*Here's the mainline!*/
	public static void main(String[] argv) {
		new App();
	}

}
