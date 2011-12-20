package Tetris;

/**
 * This class extends the gfx.Rectangle2D and draw the basic square
 *
 * @author mwang5
 *
 */

public class Square extends gfx.Rectangle2D{
	
	public Square(DrawPanel dp){
		super(dp);
		this.setVisible(true);
		this.setSize(Constants.SIZE, Constants.SIZE);
		this.setBorderColor(java.awt.Color.WHITE);
		this.setBorderWidth(1);
	}
}
