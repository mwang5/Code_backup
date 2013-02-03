package gfx;
import java.awt.*;
import javax.swing.*;

/**
This class allows you to paint nice looking fonts to specific locations on your JPanel.  
*/

public class ColorText {
	private String _text;
	private Font _font;
	private int _posX, _posY;
	private Color _color;
	private Dimension _size;
	
	/**
		Creates a new instance of ColorText
		Inputs:  
			p -- reference to the Panel where you want to draw the text
			text -- the text you wish to appear (for example, 'CS15 is awesome!')
			color -- the java.awt.Color that you wish your text to have
			posX -- the x position of where the upper left corner of the text should be placed
			posY -- the y position of where the upper left corner of the text should be placed
	*/

	public ColorText (JPanel p, String text, Color color, int posX, int posY) {
		_text = text;
		_font = new Font("SansSerif", Font.PLAIN, 20);
		_color = color;
		_posX = posX;
		_posY = posY;
		FontMetrics fm = p.getFontMetrics(_font);
		_size = new Dimension(fm.stringWidth(_text), fm.getHeight());
	}

	/**
		Make sure you call this method on an instance of your ColorText in the paintComponent of your DrawingPanel to make your text appear!
	*/
	public void paint(Graphics2D brush) {
		brush.setColor(_color);
		brush.setFont(_font);
		brush.drawString(_text, _posX, _posY);
		// rotation can be done same as shape
	}
	
	public int getCenterX() {
		return (_size.width/2) + _posX;
	}
	
	public int getCenterY() {
		return (_size.height/2) + _posY;
	}
}
