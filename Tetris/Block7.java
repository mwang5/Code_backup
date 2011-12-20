package Tetris;

import java.awt.Color;

/**
 * Block "Z"
 *
 * @author mwang5
 *
 */

public class Block7 extends Block{

	public Block7(DrawPanel dp) {
		super(dp, new BlockShape(9, 2, -1, 0, 0, 1, 1, 1), Color.CYAN);
	}
}
