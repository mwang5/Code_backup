package DoodleJump;

/**
 * This is Doodle class which extends gfx.Rectangle
 * set the attributes in constructor
 * 
 * @author mwang5
 */

public class Doodle extends gfx.Rectangle {
	private DrawPanel _dp; 
	
	public Doodle(DrawPanel container) {
		super(container);
		_dp = container;
		this.setSize(Constants.DOODLE_WIDTH, Constants.DOODLE_HEIGHT);
		this.setLocation(_dp._firstplatform.getX() + Constants.PLATFORM_WIDTH/2 - Constants.DOODLE_WIDTH/2,
				_dp._firstplatform.getY() - Constants.DOODLE_HEIGHT);
		this.setVisible(true);
		this.setWrapping(false);
		this.setColor(java.awt.Color.BLACK);
	}
	
	//This move method will move based on current location of the doodle
	public void move(double dx, double dy) {
		this.setLocation(this.getX() + dx, this.getY() + dy);
	}
	
	//Check if the doodle is going up
	public boolean isUp(double speed) {
		if (speed > 0) {
			return false;
		} else {
			return true;
		}
	}
}
