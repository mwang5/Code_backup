package gfx;
import java.awt.*;
import javax.swing.*;
/**
This class is a wrapper around a java.awt.Image.  It allows you to read in an image and paint it to a JPanel.  Note:  You may have to resize your image or drawing panel to make sure that the image appears, in it's entirety, on screen!
*/
public class Image {

	private java.awt.Image _awtImg;
	private int _posX, _posY;
	private JPanel _jPanel;
	
	/**
		Creates a new instance of Image
		Inputs:  
			p -- reference to the Panel where you want to draw the image
			filename -- name of the image file you wish to draw (jpg or png)
			posX -- the x position of where the upper left corner of the image should be placed
			posY -- the y position of where the upper left corner of the image should be placed
	*/
	public Image(JPanel p, String filename, int posX, int posY) {
		_jPanel = p;
		_posX = posX;
		_posY = posY;
		_awtImg = p.getToolkit().createImage(filename);
		MediaTracker mediaTracker = new MediaTracker(p);
		mediaTracker.addImage(_awtImg, 0);
		try { // try loading the image
			mediaTracker.waitForAll();
			}
		catch (Exception e) { // something happened
			if (mediaTracker.isErrorAny()) {
				System.out.println("Image did not load");
				return; // bail out
				}
			}
		}
	
	/**
		Call this method on your instance of Image (in the paintComponent method of your drawing panel) to make 
		the image appear!
	*/
	public void paint (Graphics2D brush) {
		// rotation same as always
		brush.drawImage(_awtImg, _posX , _posY , _jPanel);
	}
}
