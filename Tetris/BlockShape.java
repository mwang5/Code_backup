package Tetris;

/**
 * loc1 and loc2 are the main location and the rest are the location that relatve to loc1 and loc2
 *
 * @author mwang5
 *
 */

public class BlockShape {
	
	int _loc1,_loc2,_loc3,_loc4,_loc5,_loc6,_loc7,_loc8;
	int[] squareloc;
	
	/*This defines the relative postion of a Block which has seven combinations in Tetris*/
	public BlockShape(int loc1, int loc2, int loc3, int loc4, int loc5, int loc6 ,int loc7, int loc8){
		_loc1 = loc1;
		_loc2 = loc2;
		_loc3 = loc3;
		_loc4 = loc4;
		_loc5 = loc5;
		_loc6 = loc6;
		_loc7 = loc7;
		_loc8 = loc8;
		squareloc = new int[8];
	}
	
	public int[] getLoc(){
		squareloc[0] = _loc1;
		squareloc[1] = _loc2;
		squareloc[2] = _loc3;
		squareloc[3] = _loc4;
		squareloc[4] = _loc5;
		squareloc[5] = _loc6;
		squareloc[6] = _loc7;
		squareloc[7] = _loc8;
		return squareloc;	
	}

}
