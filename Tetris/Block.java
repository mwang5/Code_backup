package Tetris;

import java.awt.*;

/**
 * This class contains the main movement. and could return nextLocation for check if next move is valid
 *
 * @author mwang5
 *
 */

public abstract class Block {
	
	protected Square[] s = new Square[4];
	
	private Color _color;
	private int[] Loc;
	private int[] checkLoc;
	private boolean first = true;
	
	private double _loc1, _loc2, _loc3, _loc4,_loc5, _loc6, _loc7, _loc8, // variables for get relative postion
					_x1, _x2, _x3, _x4, _y1, _y2, _y3, _y4,
					_nx1, _ny1, _nx2, _ny2, _nx3, _ny3, _nx4, _ny4, //real position on DrawPanel 
					_cnx1, _cnx2, _cnx3, _cnx4, _cny1, _cny2, _cny3, _cny4,//real position on DrawPanel
					_cenX, _cenY, _rnx1, _rnx2, _rnx4, _rny1, _rny2, _rny4;//real position on DrawPanel
	
	public Block(DrawPanel dp, BlockShape arr, Color color){
		
		_color = color;
		Loc = new int[8];
		checkLoc = new int[8];
		s[0] = new Square(dp);
		s[1] = new Square(dp);
		s[2] = new Square(dp);
		s[3] = new Square(dp);
		
		/*set different colors on each block with four same color square*/
		s[0].setColor(color);
		s[1].setColor(color);
		s[2].setColor(color);
		s[3].setColor(color);
		
		/*Pass the relative positon and set at the postion of drawpanel of paticular one block*/
		int[] ralpos = arr.getLoc();
		_loc1 = ralpos[0];
		_loc2 = ralpos[1];
		_loc3 = ralpos[2];
		_loc4 = ralpos[3];
		_loc5 = ralpos[4];
		_loc6 = ralpos[5];
		_loc7 = ralpos[6];
		_loc8 = ralpos[7];
		_x1 = _loc1 * Constants.SIZE;
		_y1 = _loc2 * Constants.SIZE;
		_x2 = _loc3 * Constants.SIZE;
		_y2 = _loc4 * Constants.SIZE;
		_x3 = _loc5 * Constants.SIZE;
		_y3 = _loc6 * Constants.SIZE;
		_x4 = _loc7 * Constants.SIZE;
		_y4 = _loc8 * Constants.SIZE;
		s[0].setLocation(_x1, _y1);
		s[1].setLocation(_x1 + _x2, _y1 + _y2);
		s[2].setLocation(_x1 + _x3, _y1 + _y3);
		s[3].setLocation(_x1 + _x4, _y1 + _y4);
		
		/*Location on DrawPanel*/
		_nx1 = _x1;
		_ny1 = _y1;
		_nx2 = _x1 + _x2;
		_ny2 = _y1 + _y2;
		_nx3 = _x1 + _x3;
		_ny3 = _y1 + _y3;
		_nx4 = _x1 + _x4;
		_ny4 = _y1 + _y4;		
	}
	
	public void setLocation(double x1, double y1) {

		s[0].setLocation(x1, y1);
		s[1].setLocation(x1 + _x2, y1 + _y2);
		s[2].setLocation(x1 + _x3, y1 + _y3);
		s[3].setLocation(x1 + _x4, y1 + _y4);
		_nx1 = x1;
		_ny1 = y1;
		_nx2 = x1 + _x2;
		_ny2 = y1 + _y2;
		_nx3 = x1 + _x3;
		_ny3 = y1 + _y3;
		_nx4 = x1 + _x4;
		_ny4 = y1 + _y4;
		Loc[0] = (int) _nx1 / Constants.SIZE;
		Loc[1] = (int) _ny1 / Constants.SIZE;
		Loc[2] = (int) _nx2 / Constants.SIZE;
		Loc[3] = (int) _ny2 / Constants.SIZE;
		Loc[4] = (int) _nx3 / Constants.SIZE;
		Loc[5] = (int) _ny3 / Constants.SIZE;
		Loc[6] = (int) _nx4 / Constants.SIZE;
		Loc[7] = (int) _ny4 / Constants.SIZE;
	}

	/*next location of the Block*/
	public int[] NextLoc(double dx, double dy) {

		_cnx1 = _nx1 + dx;
		_cny1 = _ny1 + dy;
		_cnx2 = _nx1 + _x2 + dx;
		_cny2 = _ny1 + _y2 + dy;
		_cnx3 = _nx1 + _x3 + dx;
		_cny3 = _ny1 + _y3 + dy;
		_cnx4 = _nx1 + _x4 + dx;
		_cny4 = _ny1 + _y4 + dy;
		checkLoc[0] = (int) _cnx1 / Constants.SIZE;
		checkLoc[1] = (int) _cny1 / Constants.SIZE;
		checkLoc[2] = (int) _cnx2 / Constants.SIZE;
		checkLoc[3] = (int) _cny2 / Constants.SIZE;
		checkLoc[4] = (int) _cnx3 / Constants.SIZE;
		checkLoc[5] = (int) _cny3 / Constants.SIZE;
		checkLoc[6] = (int) _cnx4 / Constants.SIZE;
		checkLoc[7] = (int) _cny4 / Constants.SIZE;
		
		return checkLoc;
	}

	/*return first appear loc*/
	public int[] IniLoc() {
		if (first == true) {
			_cnx1 = _x1;
			_cny1 = _y1;
			_cnx2 = _x1 + _x2;
			_cny2 = _y1 + _y2;
			_cnx3 = _x1 + _x3;
			_cny3 = _y1 + _y3;
			_cnx4 = _x1 + _x4;
			_cny4 = _y1 + _y4;
			first = false;
			checkLoc[0] = (int) _cnx1 / Constants.SIZE;
			checkLoc[1] = (int) _cny1 / Constants.SIZE;
			checkLoc[2] = (int) _cnx2 / Constants.SIZE;
			checkLoc[3] = (int) _cny2 / Constants.SIZE;
			checkLoc[4] = (int) _cnx3 / Constants.SIZE;
			checkLoc[5] = (int) _cny3 / Constants.SIZE;
			checkLoc[6] = (int) _cnx4 / Constants.SIZE;
			checkLoc[7] = (int) _cny4 / Constants.SIZE;
			
			return checkLoc;
		} else
			return null;
	}
	
	public void moveBlock(double dx, double dy) {
		this.setLocation(_nx1 + dx, _ny1 + dy);
	}

	public void rotate() {
		_cenX = _nx3 + Constants.SIZE * 0.5;
		_cenY = _ny3 + Constants.SIZE * 0.5;
		_rnx1 = _cenX - _cenY + _ny1 + Constants.SIZE * 0.5;
		_rnx2 = _cenX - _cenY + _ny2 + Constants.SIZE * 0.5;
		_rnx4 = _cenX - _cenY + _ny4 + Constants.SIZE * 0.5;
		_rny1 = _cenY + _cenX - _nx1 - Constants.SIZE * 0.5;
		_rny2 = _cenY + _cenX - _nx2 - Constants.SIZE * 0.5;
		_rny4 = _cenY + _cenX - _nx4 - Constants.SIZE * 0.5;
		s[0].setLocation(_rnx1 - Constants.SIZE * 0.5, _rny1 - Constants.SIZE * 0.5);
		s[1].setLocation(_rnx2 - Constants.SIZE * 0.5, _rny2 - Constants.SIZE * 0.5);
		s[3].setLocation(_rnx4 - Constants.SIZE * 0.5, _rny4 - Constants.SIZE * 0.5);
		_nx1 = _rnx1 - Constants.SIZE * 0.5;
		_ny1 = _rny1 - Constants.SIZE * 0.5;
		_nx2 = _rnx2 - Constants.SIZE * 0.5;
		_ny2 = _rny2 - Constants.SIZE * 0.5;

		_nx4 = _rnx4 - Constants.SIZE * 0.5;
		_ny4 = _rny4 - Constants.SIZE * 0.5;
		_x2 = _nx2 - _nx1;
		_y2 = _ny2 - _ny1;
		_x3 = _nx3 - _nx1;
		_y3 = _ny3 - _ny1;
		_x4 = _nx4 - _nx1;
		_y4 = _ny4 - _ny1;

		s[0].setLocation(_nx1, _ny1);
		s[1].setLocation(_nx2, _ny2);
		s[2].setLocation(_nx3, _ny3);
		s[3].setLocation(_nx4, _ny4);

		Loc[0] = (int) _nx1 / Constants.SIZE;
		Loc[1] = (int) _ny1 / Constants.SIZE;
		Loc[2] = (int) _nx2 / Constants.SIZE;
		Loc[3] = (int) _ny2 / Constants.SIZE;
		Loc[4] = (int) _nx3 / Constants.SIZE;
		Loc[5] = (int) _ny3 / Constants.SIZE;
		Loc[6] = (int) _nx4 / Constants.SIZE;
		Loc[7] = (int) _ny4 / Constants.SIZE;
		
	}
	
	public int[] NextRotateLoc() {

		_cenX = _nx3 + Constants.SIZE * 0.5;
		_cenY = _ny3 + Constants.SIZE * 0.5;
		_rnx1 = _cenX - _cenY + _ny1 + Constants.SIZE * 0.5;
		_rnx2 = _cenX - _cenY + _ny2 + Constants.SIZE * 0.5;
		_rnx4 = _cenX - _cenY + _ny4 + Constants.SIZE * 0.5;
		_rny1 = _cenY + _cenX - _nx1 - Constants.SIZE * 0.5;
		_rny2 = _cenY + _cenX - _nx2 - Constants.SIZE * 0.5;
		_rny4 = _cenY + _cenX - _nx4 - Constants.SIZE * 0.5;

		_cnx1 = _rnx1 - Constants.SIZE * 0.5;
		_cny1 = _rny1 - Constants.SIZE * 0.5;
		_cnx2 = _rnx2 - Constants.SIZE * 0.5;
		_cny2 = _rny2 - Constants.SIZE * 0.5;
		_cnx3 = _nx3;
		_cny3 = _ny3;
		_cnx4 = _rnx4 - Constants.SIZE * 0.5;
		_cny4 = _rny4 - Constants.SIZE * 0.5;

		checkLoc[0] = (int) _cnx1 / Constants.SIZE;
		checkLoc[1] = (int) _cny1 / Constants.SIZE;
		checkLoc[2] = (int) _cnx2 / Constants.SIZE;
		checkLoc[3] = (int) _cny2 / Constants.SIZE;
		checkLoc[4] = (int) _cnx3 / Constants.SIZE;
		checkLoc[5] = (int) _cny3 / Constants.SIZE;
		checkLoc[6] = (int) _cnx4 / Constants.SIZE;
		checkLoc[7] = (int) _cny4 / Constants.SIZE;

		return checkLoc;
	}

	public int[] getLocation() {
		return Loc;
	}
	
	public Color getColor() {
		return _color;
	}

	public void paint(Graphics2D brush) {	
		s[0].paint(brush);
		s[1].paint(brush);
		s[2].paint(brush);
		s[3].paint(brush);
	}
}
	
