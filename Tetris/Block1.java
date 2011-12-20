package Tetris;

import java.awt.Color;

/**
 * Block "l"
 *
 * @author mwang5
 *
 */

public class Block1 extends Block{

	public Block1(DrawPanel dp) {
		super(dp, new BlockShape(9, 2, 0, 1, 0, 2, 0, 3), Color.GREEN);
	}
}
