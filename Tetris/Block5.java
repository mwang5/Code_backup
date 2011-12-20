package Tetris;

import java.awt.Color;

/**
 * Block up side down "T"
 *
 * @author mwang5
 *
 */

public class Block5 extends Block{

	public Block5(DrawPanel dp) {
		super(dp, new BlockShape(9, 2, -1, 1, 0, 1, 1, 1), Color.ORANGE);
	}
}
