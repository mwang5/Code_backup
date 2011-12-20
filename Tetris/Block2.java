package Tetris;

import java.awt.Color;

/**
 * Block "L"
 *
 * @author mwang5
 *
 */

public class Block2 extends Block{

	public Block2(DrawPanel dp) {
		super(dp, new BlockShape(9, 2, 0, 1, 0, 2, 1, 2), Color.MAGENTA);
	}
}
