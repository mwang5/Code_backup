package Tetris;

import java.awt.Color;

/**
 * Block in mirror "L"
 *
 * @author mwang5
 *
 */

public class Block3 extends Block{

	public Block3(DrawPanel dp) {
		super(dp, new BlockShape(9, 2, 0, 2, 0, 1, -1, 2), Color.RED);
	}
}
