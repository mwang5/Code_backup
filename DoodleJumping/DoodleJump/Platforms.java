package DoodleJump;

/**
 * This is Platform class which extends gfx.Rectangle
 * set the attributes in constructor
 * 
 * @author mwang5
 */

public class Platforms extends gfx.Rectangle{

	public Platforms(DrawPanel container) {
		super(container);
		this.setSize(Constants.PLATFORM_WIDTH, Constants.PLATFORM_HEIGHT);
		this.setVisible(true);
		this.setWrapping(false);
		this.setColor(java.awt.Color.GRAY);
	}	
}
