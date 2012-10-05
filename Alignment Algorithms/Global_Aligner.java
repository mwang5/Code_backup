package globalaligner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Global_Aligner {
	
	private int[][] _dpmatrix;
	public Global_Aligner() {}
	
	protected void freematrix() {
		this._dpmatrix = null;
	}
	
	/**
	 * This is the main global aligner with two parts: filling the dp matrix 
	 * and tracebacking the sequences
	 * */
	public void globalaligner(String str1, String str2, int match, int mis, int gap) {
		int r, c, diagonal, above, left, max, w;
		String Alignmentf1 = "";
		String Alignmentf2 = "";
		this._dpmatrix = new int[str1.length() + 1][str2.length() + 1];
		/**
		 * This part is filling the matrix
		 * */
		for (c = 0; c <= str1.length(); c++) { 
			this._dpmatrix[c][0] = c * gap;
		}
		for (r = 0; r <= str2.length(); r++) {
			this._dpmatrix[0][r] = r * gap;
		}
		for (c = 1; c <= str1.length(); c++) {
			for (r = 1; r <= str2.length(); r++) {
				diagonal = 0;
				above = this._dpmatrix[c-1][r] + gap;
				left = this._dpmatrix[c][r-1] + gap;
				if (str1.charAt(c-1) == str2.charAt(r-1)) {
					diagonal = this._dpmatrix[c-1][r-1] + match;
				} else {
					diagonal = this._dpmatrix[c-1][r-1] + mis;
				}
				max = Math.max(left, above) > Math.max(above, diagonal) ? Math.max(left, above) : Math.max(above, diagonal);
				if (max == left) {
					this._dpmatrix[c][r] = left;
				} else if (max == above) {
					this._dpmatrix[c][r] = above;
				} else {
					this._dpmatrix[c][r] = diagonal;
				}
			}
		}
		/**
		 * Print out the filled matrix
		 * */
        System.out.println("The matrix Matrix[][]:");
        for(c=0;c <= str1.length();c++){
            for(r=0;r <= str2.length();r++){
                System.out.print(this._dpmatrix[c][r]+"  ");  
            }
            System.out.println();
        }
        /**
         * Traceback and Print the score and sequence
         * */
		c = str1.length();
		r = str2.length();
		while (c > 0 || r > 0) {				
			int Score = this._dpmatrix[c][r];
			if ((c == 0 && r != 0) || (c != 0 && r == 0)) { 
				while ((c == 0 && r != 0) || (c != 0 && r == 0)) {
					if (c == 0 && r != 0) {
		                Alignmentf1 = "_" + Alignmentf1;
		                Alignmentf2 = str2.charAt(r-1) + Alignmentf2;
		                r = r - 1;
		                continue;
					} 
					if (c != 0 && r == 0) {
		                Alignmentf1 = str1.charAt(c-1) + Alignmentf1;
		                Alignmentf2 = "_" + Alignmentf2;
		                c = c - 1;
		                continue;
					}
				} 
			} else { 
				int ScoreDiag = this._dpmatrix[c-1][r-1];
				int ScoreLeft = this._dpmatrix[c-1][r];
				int ScoreUp = this._dpmatrix[c][r-1];
				if (c > 0 && r >0) {
		            if (str1.charAt(c - 1) == str2.charAt(r - 1)) {
		                w = match;
		            } else {
		                w = gap;
		            }
		            if (Score == ScoreDiag + w) {
		                Alignmentf1 = str1.charAt(c-1) + Alignmentf1;
		                Alignmentf2 = str2.charAt(r-1) + Alignmentf2;
		                c = c - 1;
		                r = r - 1;
		                continue;
		            }
				}
				if ( c > 0) {
					if (Score == ScoreLeft + gap) {
		                Alignmentf1 = str1.charAt(c-1) + Alignmentf1;
		                Alignmentf2 = "_" + Alignmentf2;
		                c = c - 1;
		                continue;
					}
				}
				if (r > 0) {
					if (Score == ScoreUp + gap) {
		            	Alignmentf1 = "_" + Alignmentf1;
		                Alignmentf2 = str2.charAt(r-1) + Alignmentf2;
		                r = r - 1; 
		                continue;
					}
				}
			}
		}
        /**
         * Print the score and aligned sequence here
         * */
        System.out.println("Optimal Global Alignment score = " + this._dpmatrix[str1.length()][str2.length()]);
        System.out.println("Sequence1 = "+Alignmentf1);
        System.out.println("Sequence2 = "+Alignmentf2);
	}
	/**
	 * This is the FASTA file processor for removing the head and aligning all lines of 
	 * sequence into one line
	 * */
    public String readFile(String path) {
    	File file = new File(path);
    	StringBuffer sb = new StringBuffer();
    	BufferedReader br = null;
    	try {
    		br = new BufferedReader(new FileReader(file));
    		String str = null;
    		br.readLine();
    		while ((str = br.readLine()) != null) {
    			sb.append(str);
    		}
    	} catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	try {
        		if (br != null)
        			br.close();
        	} catch(IOException e) {
        		e.printStackTrace();
        	}
        }
        return sb.toString();
	}
    /**
     * This is the main class
     * */
    public static void main(String[] args) {
    	Global_Aligner g = new Global_Aligner();
    	String seq1 = g.readFile(args[0]);
    	String seq2 = g.readFile(args[1]);
    	g.globalaligner(seq1, seq2, Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
    	g.freematrix();
    }
}
