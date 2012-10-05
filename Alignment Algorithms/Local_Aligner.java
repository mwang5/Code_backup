package localaligner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Local_Aligner {
	
	private int[][] _dpmatrix;
	public Local_Aligner() {}
	protected void freematrix() {
		this._dpmatrix = null;
	}
	/**
	 * This is the main local aligner with two parts: filling the dp matrix 
	 * and tracebacking the sequences
	 * 
	 * */
	public void localaligner(String str1, String str2, int match, int mis, int gap) {
		int i, j, diagonal, above, left, w;
		this._dpmatrix = new int[str1.length() + 1][str2.length() + 1]; 
		int max = -10000;
		int line = 0, colum = 0;
		/**
		 * This part is filling the matrix
		 * */
		for (j = 0; j <= str2.length(); j++)
			this._dpmatrix[0][j] = 0;
		for (i = 0; i <= str1.length(); i++) 
			this._dpmatrix[i][0] = 0;
		for (i = 1; i <= str1.length(); i++) {
			for (j = 1; j <= str2.length(); j++) {
				diagonal = 0;
				above = this._dpmatrix[i-1][j] + gap;
				left = this._dpmatrix[i][j-1] + gap;
				if (str1.charAt(i-1) == str2.charAt(j-1)) {
					diagonal = this._dpmatrix[i-1][j-1] + match;
				} else {
					diagonal = this._dpmatrix[i-1][j-1] + mis;
				}

				if (diagonal > above && diagonal > 0 && diagonal > left) {
					this._dpmatrix[i][j] = diagonal;
				} else if (above > 0 && above > left) {
					this._dpmatrix[i][j] = above;
				} else if (left > 0) {
					this._dpmatrix[i][j] = left;
				} else {
					this._dpmatrix[i][j] = 0;
				}
			}
		}
		/**
		 * Print out the filled matrix
		 * */
        System.out.println("The matrix Matrix[][]:");
        for(i=0;i<=str1.length();i++){
            for(j=0;j<=str2.length();j++){
                System.out.print(this._dpmatrix[i][j]+"  ");  
            }
            System.out.println();
        }
        /**
         * Traceback and Print the score and sequence
         * */
        String Alignmentf1 = ""; 
        String Alignmentf2 = "";
        for(i=0;i<=str1.length();i++){
            for(j=0;j<=str2.length();j++){
                if(this._dpmatrix[i][j]>max){
                    max = this._dpmatrix[i][j];
                    line = i;
                    colum = j;
                }
            }
        }
        i = line;
        j = colum;
        while ((i > 0) && (j > 0) && (this._dpmatrix[i][j] > 0)) {
            int Score = this._dpmatrix[i][j];
            int ScoreDiag = this._dpmatrix[i - 1][j - 1];   
            int ScoreLeft = this._dpmatrix[i - 1][j];
            if (str1.charAt(i-1) == str2.charAt(j-1)) {
                w = match;
            } else {
                w = gap;
            }
            if (Score == (ScoreDiag + w)) {
                Alignmentf1 = str1.charAt(i-1) + Alignmentf1;
                Alignmentf2 = str2.charAt(j-1) + Alignmentf2;
                i = i - 1;
                j = j - 1;
            } else if (Score == ScoreLeft + w) {
                Alignmentf1 = str1.charAt(i-1) + Alignmentf1;
                Alignmentf2 = "-" + Alignmentf2;
                i = i - 1;
            } else {                             
                Alignmentf1 = "-" + Alignmentf1;
                Alignmentf2 = str2.charAt(j-1) + Alignmentf2;
                j = j - 1;
            }
        }
        /**
         * Print the score and aligned sequence here
         * */
        System.out.println("Optimal Local Alignment score = " + this._dpmatrix[line][colum]);
        System.out.print("The value is in row " +line+", ");
        System.out.println(" colum " +colum);
        System.out.println("Sequence1= "+Alignmentf1);
        System.out.println("Sequence2= "+Alignmentf2);
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
	 * Main
	 * */
	public static void main(String[] args) {
		Local_Aligner l = new Local_Aligner();
		String seq1 = l.readFile(args[0]);
		String seq2 = l.readFile(args[1]);
		l.localaligner(seq1, seq2, Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
		l.freematrix();
	}
}
