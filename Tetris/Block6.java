package Tetris;

import java.awt.Color;

/**
 * Block "s"
 *
 * @author mwang5
 *
 */

public class Block6 extends Block{

	public Block6(DrawPanel dp) {
		super(dp, new BlockShape(9, 2, 1, 0, 0, 1, -1, 1), Color.BLUE);
	}
}
