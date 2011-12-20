package Cartoon;

import java.awt.*;
import javax.swing.*;


/**
 * This class is the DrawPanel class
 *
 * @author mwang5
 *
 */

@SuppressWarnings("serial")
public class DrawingPanel extends JPanel {

    private NiceAlien _alien;
    
    public DrawingPanel() {
	    super();
	    this.setBackground(java.awt.Color.WHITE);
	    _alien = new NiceAlien(this);
    }
	
    public void paintComponent(Graphics g){
	    super.paintComponent(g);
	    Graphics2D brush = (Graphics2D) g;
	    _alien.paint(brush);
    }
    
    public void moveAlien(int x, int y) {
	    _alien.setLocation(x, y);
	    this.repaint();
    }
}