package DoodleJump;

import java.awt.*;
import java.util.Vector;
import javax.swing.*;

/**
 * 
 * @author mwang5
 */

public class DrawPanel extends JPanel {
	
	private Action _leftaction, _rightaction, _pauseaction;
	private ActionMap _action;
	private Timer _timer;
	private MainPanel _mp;
	private Doodle _doodle;
	protected Platforms _firstplatform;
	private Vector<Platforms> _platformcollection;
	private boolean _isPause;
	private double _velocity;
	
	public DrawPanel(MainPanel mp) {
		//set the attributes of DrawPanel
		super();
		Dimension size = new Dimension(400, 700);
		this.setPreferredSize(size);
		this.setSize(size);
		this.setBackground(Color.WHITE);
		
		_mp = mp;
		_platformcollection = new Vector<Platforms>();
		_firstplatform = new Platforms(this);
		_firstplatform.setLocation(this.getWidth()/2 - Constants.PLATFORM_WIDTH/2, 
				 this.getHeight() - Constants.PLATFORM_HEIGHT - Math.random() * Constants.PLATFORM_WIDTH);
		_platformcollection.addElement(_firstplatform);
		this.placePlatform(_firstplatform);
		_doodle = new Doodle(this);
		_isPause = true;
		_velocity = Constants.REBOUND_VELOCITY;
		_timer = new Timer(this);
		_timer.start();
		
		//Keyboard actions
		InputMap input;
		
		input = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		input.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
		_action = this.getActionMap();
		_leftaction = new Action("moveLeft", this);
		_action.put("moveLeft", _leftaction);
		
		input.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
		_rightaction = new Action("moveRight", this);
		_action.put("moveRight", _rightaction);
		
		input.put(KeyStroke.getKeyStroke("P"), "Pause");
		_pauseaction = new Action("Pause", this);
		_action.put("Pause", _pauseaction);
	}
	
	public void placePlatform(Platforms current) {
		while (current.getY() > 0) {
			Platforms newplatform = new Platforms(this);
			_platformcollection.addElement(newplatform);
			newplatform.setLocation(this.randomgenerator(20, 300), 
					(this.randomgenerator((int)(current.getY() -  200), (int)current.getY())));
			current = newplatform;
		}
	}
	
	public boolean isEnd() {
		//Use -5 becaue sometimes the doodle will fall off the drawpanel a little bit 
		if ((this._doodle.getY() + Constants.DOODLE_HEIGHT)  >= this.getHeight()) {
			return true;
		} else {
			return false;
		}
	}
 	
	public void doodlejump() {
		/* put reset re-bounce code in doodlejump method at condition that
			 doodle is going down.*/
		int arraySize = _platformcollection.size();
		int count[] = new int[arraySize];
		if (this._velocity > 0) {
			for (int i = 0; i < _platformcollection.size(); i++) {
		        if (_doodle.intersects(_platformcollection.elementAt(i).getX(),
		        		_platformcollection.elementAt(i).getY(),
		                               Constants.PLATFORM_WIDTH,
		                               Constants.PLATFORM_HEIGHT) && !_doodle.isUp(this._velocity)) {
		        	_velocity = Constants.REBOUND_VELOCITY;
		        	count[i]++;
		        	if (count[i] == 1) {
		        		_mp.setScore();
		        	} 
		        	this.generatePlatforms();
		        }
		    }
			this._velocity += Constants.GRAVITY * (double)Constants.TIMESTEP/1000;
			this.moveDoodle(0, (double)Constants.TIMESTEP/1000 * this._velocity);
		} else {
			/* put scrollPlatform code in doodlejump method at condition that
				 doodle is going up.*/
			if (_doodle.getY() < this.getHeight()/2) {
				double dy = this.getHeight()/2 - _doodle.getY();
				_doodle.setLocation(_doodle.getX(), this.getHeight()/2);
				for (int i = 0; i < _platformcollection.size(); i++) {
					_platformcollection.elementAt(i).setLocation(
							_platformcollection.elementAt(i).getX(), 
							_platformcollection.elementAt(i).getY() + dy);
				}
			}
			this._velocity += Constants.GRAVITY * (double)Constants.TIMESTEP/1000;
			this.moveDoodle(0, (double)Constants.TIMESTEP/1000 * this._velocity);
		}
	}
	
	public void generatePlatforms() {
		Platforms platform = new Platforms(this);
		_platformcollection.addElement(platform);
		platform.setLocation((double)this.randomgenerator(20, 300), 
				(double)this.randomgenerator(10, 80));
		
	}
	
	public void paintPlatforms(Graphics2D brush) {
		for(int i = 0; i < _platformcollection.size(); i++) {
			_platformcollection.elementAt(i).paint(brush);
		}
	}
	
	public int randomgenerator(int min, int max) {
		return min + (int)(Math.random() * ((max - min) + 1 ));
	}
	
	public void moveDoodle(double x, double y) {
		_doodle.move(x, y);
		this.repaint();
	}
	
	public void moveLeft() {  
		if (_doodle.getX() > 0) {
			this.moveDoodle(-Constants.DOODLE_WIDTH, 0);
		}
	}
	
	public void moveRight() {
		if (_doodle.getX() < this.getWidth() - Constants.DOODLE_WIDTH - 1) {
			this.moveDoodle(Constants.DOODLE_WIDTH, 0);
		}
	}
	
	public void Pause() {
		if (_isPause == false) {
			_timer.start();
			_action.put("moveLeft", _leftaction);
			_action.put("moveRight", _rightaction);
			_mp.ResumeText();
			_isPause = true;
		}
		else if (_isPause = true) {
			_timer.stop();
			_action.clear();
			_action.put("Pause", _pauseaction);
			_mp.PauseText();
			_isPause = false;
		}
	}	
	
	public void stop() {
		_timer.stop();
		_mp.GameOverText();
		_action.clear();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D brush = (Graphics2D) g;
		_doodle.paint(brush);
		this.paintPlatforms(brush);
	}
}
