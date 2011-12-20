package Cartoon;

import javax.swing.*;

/**
 * Here's Cartoon! Your first Swing assignment!
 * Before you start coding your Cartoon, you shauld make sure\
 * that you have a (semi) functional gfx package. Take a look at 
 * the book chapters and lecture slides for all the information 
 * you'll need (and more!).
 *
 * @author mwang5
 * Did you discuss your design with another student?
 * If so, list their login here:
 *
 */

@SuppressWarnings("serial")
public class App extends JFrame {	// You'll need to subclass something

	
	public App() {
		super("Nice Alien");
		
		this.setSize(600,450);
                this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);// Be sure to call super and pass it any arguments
		// Be sure to call super and pass it any arguments
		// you want (look at the docs)
		// Create top-level class and initialize App
		MainPanel mainPanel=new MainPanel(); 
                this.add(mainPanel);
                this.pack();
		this.setVisible(true);
	}

	/*
	 * Here is the mainline!  
	 */
	public static void main(String [] argv) {
		new App();
	}

}
