package gfx;

/**
 * This class represents an Rectangle shape.
 */
public class Rectangle extends Shape {

	public Rectangle(javax.swing.JPanel container) {
		// Call the superclass's constructor with the appropriate parameters
		super(container, new java.awt.geom.Rectangle2D.Double());
	}
}
