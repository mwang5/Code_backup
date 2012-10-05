package fittingaligner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Fitting_Aligner {
	
	private int[][] _dpmatrix;
	public Fitting_Aligner() {}
	protected void freematrix() {
		this._dpmatrix = null;
	}
	/**
	 * This is the main fitting aligner with two parts: filling the dp matrix 
	 * and tracebacking the sequences.
	 * 
	 * I choose to always put the longer sequence in column. so there 
	 * are duplicate code in filling dp matrix part
	 * 
	 * */
	public void fittingaligner(String str1, String str2, int match, int mis, int gap) {
		int i, j, k, diagonal, above, left, max, w;
		int column = 0;
        String Alignmentf1 = ""; 
        String Alignmentf2 = "";
        /**
         * Filling the dp matrix
         * */
		if (str1.length() > str2.length()) {
			this._dpmatrix = new int[str2.length() + 1][str1.length() + 1]; 
			
			for (i = 0; i <= str2.length(); i++) {
				this._dpmatrix[i][0] = i * gap;	
			}	
			for (j = 0; j <= str1.length(); j++) {
				this._dpmatrix[0][j] = j * gap;
			}
			for (k = 1; k <= str1.length(); k++) {
				if (str2.charAt(0) == str1.charAt(k - 1)) {
					this._dpmatrix[1][k] = 1;
				} else {
					this._dpmatrix[1][k] = 0;
				}
			}
			for (int c = 2; c <= str2.length(); c++) {
				for (int r = 1; r <= str1.length(); r++) {
					diagonal = 0;
					above = this._dpmatrix[c-1][r] + gap;
					left = this._dpmatrix[c][r-1] + gap;
					if (str2.charAt(c-1) == str1.charAt(r-1)) {
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
			 * Print matrix
			 * */
	        System.out.println("The matrix Matrix[][]:");
	        for(i=0;i<=str2.length();i++){
	            for(j=0;j<=str1.length();j++){
	                System.out.print(this._dpmatrix[i][j]+"  ");  
	            }
	            System.out.println();
	        }
	        int maximum = this._dpmatrix[str2.length()][1];
	        for (j = 2; j <= str1.length(); j++) {
	        	if (this._dpmatrix[str2.length()][j] > maximum) {
	        		maximum = this._dpmatrix[str2.length()][j];
	        		column = j;
	        	}
	        }
	        i = str2.length();
	        j = column;
			while (i > 0) {				
				int Score = this._dpmatrix[i][j];
				int ScoreDiag = this._dpmatrix[i-1][j-1];
				int ScoreLeft = this._dpmatrix[i-1][j];
				int ScoreUp = this._dpmatrix[i][j-1];
				if (i > 0 && j >0) {
		            if (str2.charAt(i - 1) == str1.charAt(j - 1)) {
		                w = match;
		            } else {
		                w = gap;
		            }
		            if (Score == ScoreDiag + w) {
		                Alignmentf1 = str2.charAt(i-1) + Alignmentf1;
		                Alignmentf2 = str1.charAt(j-1) + Alignmentf2;
		                i = i - 1;
		                j = j - 1;
		                continue;
		            } else if (i == 1 && Score == 1) {
		                Alignmentf1 = str2.charAt(i-1) + Alignmentf1;
		                Alignmentf2 = str1.charAt(j-1) + Alignmentf2;
		                i = i - 1;
		                j = j - 1;
		                continue;
		            } else if (i == 1 && Score == 0 ) {
		            	Alignmentf1 = str2.charAt(i - 1) + Alignmentf1;
		            	Alignmentf2 = "_" + Alignmentf2;
		            	i = i - 1;
		            	continue;
		            }
				}
				if ( i > 0) {
					if (Score == ScoreLeft + gap) {
		                Alignmentf1 = str2.charAt(i-1) + Alignmentf1;
		                Alignmentf2 = "_" + Alignmentf2;
		                i = i - 1;
		                continue;
					}
				}
				if (j > 0) {
					if (Score == ScoreUp + gap) {
		            	Alignmentf1 = "_" + Alignmentf1;
		                Alignmentf2 = str1.charAt(j-1) + Alignmentf2;
		                j = j - 1;  
		                continue;
					}
				}
			}
			/**
			 * Print the score and aligned sequences
			 * */
	        System.out.println("Optimal Fitting Alignment score = " + this._dpmatrix[str2.length()][column]);
	        System.out.println("Sequence1 = "+Alignmentf2);
	        System.out.println("Sequence2 = "+Alignmentf1);

		} else {
			/**
			 * Duplicated code while the length of str1 is less than str2
			 * */
			this._dpmatrix = new int[str1.length() + 1][str2.length() + 1]; 
			
			for (i = 0; i <= str1.length(); i++) {
				this._dpmatrix[i][0] = i * gap;	
			}	
			for (j = 0; j <= str2.length(); j++) {
				this._dpmatrix[0][j] = j * gap;
			}		
			for (k = 1; k <= str2.length(); k++) {
				if (str1.charAt(0) == str2.charAt(k - 1)) {
					this._dpmatrix[1][k] = 1;
				} else {
					this._dpmatrix[1][k] = 0;
				}
			}
			for (int c = 2; c <= str1.length(); c++) {
				for (int r = 1; r <= str2.length(); r++) {
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
			
	        System.out.println("The matrix Matrix[][]:");
	        for(i=0;i<=str1.length();i++){
	            for(j=0;j<=str2.length();j++){
	                System.out.print(this._dpmatrix[i][j]+"  ");  
	            }
	            System.out.println();
	        }
	        
	        int maximum = this._dpmatrix[str1.length()][1];
	        for (j = 2; j <= str2.length(); j++) {
	        	if (this._dpmatrix[str1.length()][j] > maximum) {
	        		maximum = this._dpmatrix[str1.length()][j];
	        		column = j;
	        	}
	        }
	        i = str1.length();
	        j = column;
			while (i > 0) {				
				int Score = this._dpmatrix[i][j];
				int ScoreDiag = this._dpmatrix[i-1][j-1];
				int ScoreLeft = this._dpmatrix[i-1][j];
				int ScoreUp = this._dpmatrix[i][j-1];
				if (i > 0 && j >0) {
		            if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
		                w = match;
		            } else {
		                w = gap;
		            }
		            if (Score == ScoreDiag + w) {
		                Alignmentf1 = str1.charAt(i-1) + Alignmentf1;
		                Alignmentf2 = str2.charAt(j-1) + Alignmentf2;
		                i = i - 1;
		                j = j - 1;
		                continue;
		            } else if (i == 1 && Score == 1) {
		                Alignmentf1 = str1.charAt(i-1) + Alignmentf1;
		                Alignmentf2 = str2.charAt(j-1) + Alignmentf2;
		                i = i - 1;
		                j = j - 1;
		                continue;
		            } else if (i == 1 && Score == 0 ) {
		            	Alignmentf1 = str1.charAt(i - 1) + Alignmentf1;
		            	Alignmentf2 = "_" + Alignmentf2;
		            	i = i - 1;
		            	continue;
		            }
				}
				if ( i > 0) {
					if (Score == ScoreLeft + gap) {
		                Alignmentf1 = str1.charAt(i-1) + Alignmentf1;
		                Alignmentf2 = "_" + Alignmentf2;
		                i = i - 1;
		                continue;
					}
				}
				if (j > 0) {
					if (Score == ScoreUp + gap) {
		            	Alignmentf1 = "_" + Alignmentf1;
		                Alignmentf2 = str2.charAt(j-1) + Alignmentf2;
		                j = j - 1;  
		                continue;
					}
				}
			}
	        System.out.println("Optimal Fitting Alignment score = " + this._dpmatrix[str1.length()][column]);
	        System.out.println("Sequence1 = "+Alignmentf2);
	        System.out.println("Sequence2 = "+Alignmentf1);
		}
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
    	Fitting_Aligner f = new Fitting_Aligner();
    	String seq1 = f.readFile(args[0]);
    	String seq2 = f.readFile(args[1]);
    	f.fittingaligner(seq1, seq2, Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
    	f.freematrix();
    }
}
