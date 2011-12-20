package Tetris;

/**
 * This class is the proxy class which stands for the current Block.
 *
 * @author mwang5
 *
 */

public class Proxy {
	
	private Block _b;
	private TetrisFactory _factory;
	
	public Proxy(TetrisFactory f){
		_factory = f;
		_b = _factory.getBlock();
	}
	
	public void setBlock(){
		_b = _factory.getBlock();
	}
	
	public void moveBlock(double dx,double dy){
		_b.moveBlock(dx, dy);
	}
	
	public void rotate() {
		_b.rotate();
	}
	
	public int[] getLocation() {
		return _b.getLocation();
	}
	
	public java.awt.Color getColor(){
		return _b.getColor();
	}
	

	public void paint(java.awt.Graphics2D brush) {
		_b.paint(brush);
	}


	public int[] checkLoc(double dx, double dy) {
		return _b.NextLoc(dx, dy);
	}
	
	public int[] checkIniLoc(){
		return _b.IniLoc();
	}

	public int[] checkRotateLoc(){
		return _b.NextRotateLoc();
	}
}
