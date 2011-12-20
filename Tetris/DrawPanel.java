package Tetris;

import javax.swing.*;
import java.awt.*;

/**
 * 
 *
 * @author mwang5
 * 
 */

@SuppressWarnings("serial")
public class DrawPanel extends JPanel {

	private InputMap _input;
	private Proxy _proxy;
	private Action _leftaction, _rightaction, _rotateaction, _dropaction, _accaction, _pauseaction;
	private ActionMap _action;
	private Timer _timer;
	private TetrisFactory _factory;
	private MainPanel _mp;
	private Square[][] _grid;
	private int[] _pos;
	private boolean _isPause = true;
	
	public DrawPanel(MainPanel mp) {
		
		super();
		Dimension size = new Dimension(400, 700);
		this.setPreferredSize(size);
		this.setSize(size);

		_factory = new TetrisFactory(this);
		_proxy = new Proxy(_factory);
		_timer = new Timer(this);
		_timer.start();
		_mp = mp;
		
		_grid = new Square[Constants.COL][Constants.ROW];
		
		for (int i = 0; i < Constants.COL; i++) {
			for (int j = 0; j < Constants.ROW; j++) {
				_grid[i][j] = new Square(this);
				_grid[i][j].setLocation(i * Constants.SIZE, j * Constants.SIZE);
				_grid[i][j].setBorderColor(java.awt.Color.black);
				_grid[i][j].setFillColor(Color.GRAY);
				_grid[i][j].setVisible(true);
			}

		}
		
		for (int i = 2; i < 18; i++) {
			for (int j = 2; j < 33; j++) {
				_grid[i][j].setColor(Color.BLACK);
			}
		}
		_pos = _proxy.getLocation();
		
		
		_input = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		_input.put(KeyStroke.getKeyStroke("J"), "moveLeft");
		_action = this.getActionMap();
		_leftaction = new Action("moveLeft", this);
		_action.put("moveLeft", _leftaction);
		
		_input.put(KeyStroke.getKeyStroke("L"), "moveRight");
		_rightaction = new Action("moveRight", this);
		_action.put("moveRight", _rightaction);
		
		_input.put(KeyStroke.getKeyStroke("I"), "Rotate");
		_rotateaction = new Action("Rotate", this);
		_action.put("Rotate", _rotateaction);
		
		_input.put(KeyStroke.getKeyStroke("K"), "Accelerate");
		_accaction = new Action("Accelerate", this);
		_action.put("Accelerate", _accaction);
		
		_input.put(KeyStroke.getKeyStroke("SPACE"), "Drop");
		_dropaction = new Action("Drop", this);
		_action.put("Drop", _dropaction);
		
		_input.put(KeyStroke.getKeyStroke("P"), "Pause");
		_pauseaction = new Action("Pause", this);
		_action.put("Pause", _pauseaction);
		
	}
	/*If the the gridcolor that block stay on is not BLACK then return false*/
	public boolean IsValid(double dx, double dy) {
		_pos = _proxy.checkLoc(dx, dy);
		if (_grid[_pos[0]][_pos[1]].getFillColor() == Color.BLACK
			&& _grid[_pos[2]][_pos[3]].getFillColor() == Color.BLACK
			&& _grid[_pos[4]][_pos[5]].getFillColor() == Color.BLACK
			&& _grid[_pos[6]][_pos[7]].getFillColor() == Color.BLACK)
			return true;
		else
			return false;
	}
	/*If the the gridcolor that block stay on is not BLACK then retrun false*/
	public boolean IsRotateValid() {
		_pos = _proxy.checkRotateLoc();
		if (_grid[_pos[0]][_pos[1]].getFillColor() == Color.BLACK
			&& _grid[_pos[2]][_pos[3]].getFillColor() == Color.BLACK
			&& _grid[_pos[4]][_pos[5]].getFillColor() == Color.BLACK
			&& _grid[_pos[6]][_pos[7]].getFillColor() == Color.BLACK)
			return true;
		else
			return false;
	}

	public boolean IsEnd() {
		_pos = _proxy.checkIniLoc();
		if (_pos == null || (_grid[_pos[0]][_pos[1]].getFillColor() == Color.BLACK
							&& _grid[_pos[2]][_pos[3]].getFillColor() == Color.BLACK
							&& _grid[_pos[4]][_pos[5]].getFillColor() == Color.BLACK 
							&& _grid[_pos[6]][_pos[7]].getFillColor() == Color.BLACK))
			return false;
		else
			return true;

	}
	
	public void updategird() {
		_pos = _proxy.getLocation();
		for (int i = 2; i < 18; i++) {
			for (int j = 4; j < 33; j++) {
				_grid[_pos[0]][_pos[1]].setFillColor(_proxy.getColor());
				_grid[_pos[2]][_pos[3]].setFillColor(_proxy.getColor());
				_grid[_pos[4]][_pos[5]].setFillColor(_proxy.getColor());
				_grid[_pos[6]][_pos[7]].setFillColor(_proxy.getColor());
			}
		}
	}
	
	public void fullLine() {
		boolean full = true;
		int tp;
		_pos = _proxy.getLocation();
		for (int i = 1; i < 8; i = i + 2) {
			int j = i;
			while (j > 1 && _pos[j - 2] > _pos[j]) {
				tp = _pos[j];
				_pos[j] = _pos[j - 2];
				_pos[j - 2] = tp;
				j--;
			}
		}
		
		int[] _position = new int[4];
		_position[0] = _pos[1];
		_position[1] = _pos[3];
		_position[2] = _pos[5];
		_position[3] = _pos[7];
		for (int i = 0; i < 4; i++) {
			full = true;
			for (int j = 2; j < 18; j++) {
				if (_grid[j][_position[i]].getFillColor() == Color.BLACK) {
					full = false;
					break;
				}
			}

			if (full == false) {
				continue;
			}
			
			if (full == true) {
				for (int a = 2; a < 18; a++)
					for (int j = _position[i]; j > 2; j--)
						_grid[a][j].setFillColor(_grid[a][j - 1].getFillColor());
			}
		}
		this.repaint();
		
		/*get new current block*/
		_proxy.setBlock();
	}
	
	public void paint(Graphics2D brush) {
		for (int i = 0; i < Constants.COL; i++) {
			for (int j = 0; j < Constants.ROW; j++) {
				_grid[i][j].paint(brush);
			}
		}
	}
	
	public void moveLeft() {
		
		if (this.IsValid(-Constants.SIZE, 0))
			this.moveBlock(-Constants.SIZE, 0);
	}
	
	public void moveRight() {

		if (this.IsValid(Constants.SIZE, 0))
			this.moveBlock(Constants.SIZE, 0);
	}
	
	public void Rotate() {

		if (this.IsRotateValid())
			this.rotate();
	}
	
	public void Accelerate() {

		if (this.IsValid(0, Constants.SIZE)) {
			this.moveBlock(0, Constants.SIZE);
		}
	}
	
	public void Drop() {
		for (int j = 2; j < 33; j++) {
			if (this.IsValid(0, Constants.SIZE))
				this.moveBlock(0, Constants.SIZE);
			else {
				this.updategird();
				this.fullLine();
				break;
			}
		}
	}
	
	public void Pause() {
		if (_isPause == false) {
			_timer.start();
			_action.put("moveLeft", _leftaction);
			_action.put("moveRight", _rightaction);
			_action.put("Rotate", _rotateaction);
			_action.put("Accelerate", _accaction);
			_action.put("Drop", _dropaction);
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
		this.paint(brush);
		_proxy.paint(brush);
	}
	
	public void moveBlock(double x, double y) {
		_proxy.moveBlock(x, y);
		this.repaint();
	}

	public void rotate() {
		_proxy.rotate();
		this.repaint();
	}
}