package Cartoon;

import java.awt.*;

/**
 * This class is the create face class
 *
 * @author mwang5
 *
 */
 
public class NiceAlien {

	private gfx.Ellipse _face, _lEye, _rEye;
	
	public NiceAlien(DrawingPanel dp) {
	
		_face = new gfx.Ellipse(dp);
		_lEye = new gfx.Ellipse(dp);
		_rEye = new gfx.Ellipse(dp);
		
		/*set Color position*/
		_face.setSize(80,80);
		_face.setLocation(0, 0);
		_face.setBorderColor(java.awt.Color.black);
		_face.setBorderWidth(3);
		_face.setFillColor(java.awt.Color.GREEN);
		_face.setVisible(true);
		
		_lEye.setSize(20,20);
		_lEye.setLocation(16, 20);
		_lEye.setBorderColor(java.awt.Color.WHITE);
		_lEye.setBorderWidth(3);
		_lEye.setFillColor(java.awt.Color.BLACK);
		_lEye.setVisible(true);
				
		_rEye.setSize(20,20);
		_rEye.setLocation(45, 20);
		_rEye.setBorderColor(java.awt.Color.WHITE);
		_rEye.setBorderWidth(3);
		_rEye.setFillColor(java.awt.Color.BLACK);
		_rEye.setVisible(true);

	}
	
	public int getX() {
		return (int)_face.getX();
	}
	
	public int getY() {
		return (int)_face.getY();
	}
	
	public void setLocation(double x, double y) {
		_face.setLocation(x, y);
		_lEye.setLocation(x + 16, y + 20);
		_rEye.setLocation(x + 45, y + 20);

	}
	
	public void paint(Graphics2D brush) {
		_face.paint(brush);
		_lEye.paint(brush);
		_rEye.paint(brush);
	}
	
}