package Tetris;

/**
 * Using the random() method to return a random generated block
 *
 * @author mwang5
 *
 */

public class TetrisFactory {

	private DrawPanel _dp;
	
	public TetrisFactory(DrawPanel dp) {
		_dp = dp;
	}
	
	public Block getBlock() {
		
		int rand = (int)(Math.random() * 7);
		switch (rand) {
			case 0:
				return new Block1(_dp);
			case 1:
				return new Block2(_dp);
			case 2:
				return new Block3(_dp);
			case 3:
				return new Block4(_dp);
			case 4:
				return new Block5(_dp);
			case 5:
				return new Block6(_dp);
			case 6:
				return new Block7(_dp);
			default:
				return null;
		}
		
	}

}