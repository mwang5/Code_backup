package humanmouseorthology;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Human_Mouse_Orthology {

	private int[][] _dpmatrix;
	public Human_Mouse_Orthology() {}
	protected void freematrix() {
		this._dpmatrix = null;
	}
	/**
	 * Fitting alignment code
	 * */
	public ArrayList<String> HumanMouse(String str1, String str2) {
		int i, j, k, diagonal, above, left, max, w;
		int column = 0;
        String Alignmentf1 = ""; 
        String Alignmentf2 = "";
        ArrayList<String> list = new ArrayList<String>();
        /**
         * Filling the dp matrix
         * */
		if (str1.length() > str2.length()) {
			this._dpmatrix = new int[str2.length() + 1][str1.length() + 1]; 
			
			for (i = 0; i <= str2.length(); i++) {
				this._dpmatrix[i][0] = i * -1;	
			}	
			for (j = 0; j <= str1.length(); j++) {
				this._dpmatrix[0][j] = j * -1;
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
					above = this._dpmatrix[c-1][r] - 1;
					left = this._dpmatrix[c][r-1] - 1;
					if (str2.charAt(c-1) == str1.charAt(r-1)) {
						diagonal = this._dpmatrix[c-1][r-1] + 1;
					} else {
						diagonal = this._dpmatrix[c-1][r-1] - 1;
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
		                w = 1;
		            } else {
		                w = -1;
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
					if (Score == ScoreLeft - 1) {
		                Alignmentf1 = str2.charAt(i-1) + Alignmentf1;
		                Alignmentf2 = "_" + Alignmentf2;
		                i = i - 1;
		                continue;
					}
				}
				if (j > 0) {
					if (Score == ScoreUp - 1) {
		            	Alignmentf1 = "_" + Alignmentf1;
		                Alignmentf2 = str1.charAt(j-1) + Alignmentf2;
		                j = j - 1;  
		                continue;
					}
				}
				if (i == 1 && j >= 1) {
					
				}
			}

		} else {
			/**
			 * Duplicated code while the length of str1 is less than str2
			 * */
			this._dpmatrix = new int[str1.length() + 1][str2.length() + 1]; 
			
			for (i = 0; i <= str1.length(); i++) {
				this._dpmatrix[i][0] = i * -1;	
			}	
			for (j = 0; j <= str2.length(); j++) {
				this._dpmatrix[0][j] = j * -1;
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
					above = this._dpmatrix[c-1][r] - 1;
					left = this._dpmatrix[c][r-1] - 1;
					if (str1.charAt(c-1) == str2.charAt(r-1)) {
						diagonal = this._dpmatrix[c-1][r-1] + 1;
					} else {
						diagonal = this._dpmatrix[c-1][r-1] - 1;
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
		                w = 1;
		            } else {
		                w = -1;
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
					if (Score == ScoreLeft - 1) {
		                Alignmentf1 = str1.charAt(i-1) + Alignmentf1;
		                Alignmentf2 = "_" + Alignmentf2;
		                i = i - 1;
		                continue;
					}
				}
				if (j > 0) {
					if (Score == ScoreUp - 1) {
		            	Alignmentf1 = "_" + Alignmentf1;
		                Alignmentf2 = str2.charAt(j-1) + Alignmentf2;
		                j = j - 1;  
		                continue;
					}
				}
			}
		}
		list.add(Alignmentf1);
		list.add(Alignmentf2);
		return list;
	}
	/**
	 * Parse the two files, first if the line contains ">" then jump through this line end with a newline,
	 * then put the info below it together. so e.g in mouse.fa we have a array with 5 strings. string[0]
	 * only contains a "\n".
	 * */
    public String[] readFile(String path) {
    	File file = new File(path);
    	StringBuffer sb = new StringBuffer();
    	BufferedReader br = null;
    	String[] temp = null;
    	try {
    		br = new BufferedReader(new FileReader(file));
    		String str = "";
    		
    		while ((str = br.readLine()) != null) {
    			if (str.contains(">")) {
    				str = br.readLine();
    				sb.append(System.getProperty("line.separator"));
    				//sb.append('/');
    			}
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
        temp = sb.toString().split("\n");
        return temp;
	}
    /**
     * Main class calculate the sequence identity directly in main. 
     * If two fitting sequences have same character, then put it into a new String. 
     * At last compute the ratio.
     * */
    public static void main(String[] args) {
    	Human_Mouse_Orthology h = new Human_Mouse_Orthology();
    	String[] seq1 = h.readFile(args[0]);
    	String[] seq2 = h.readFile(args[1]);
    	ArrayList<String> list = new ArrayList<String>();
    	int q;
    	double ratio = 0.0;
    	double max = 0.0;
    	//for every fitting aligned mouse gene compare with all fitting aligned human gene
    	for (int i = 1; i < seq1.length; i++) {
    		for (int j = 1; j < seq2.length; j++) {
    			String ortholog = ""; 
    			list = h.HumanMouse(seq1[i], seq2[j]);
    			String s1 = list.get(0);
    			String s2 = list.get(1);
    			for (q = 0; q < s1.length(); q++) {
    				if (s1.charAt(q) == s2.charAt(q)) {
    					ortholog = ortholog + s1.charAt(q);
    				}
    			}
    			ratio = (double)ortholog.length()/(double)s2.length();		
    			
    			if (ratio > 0.85) {
    				max = ratio;
    				System.out.println("Match! ! ! At mouse sequence: " + i + " Human sequence: " + j + " " +
    						"with sequence identity: "+ max);
    				System.out.println(s1);
    				System.out.println(s2);
    				System.out.println("Ortholog sequence: " + ortholog);
    			}
    			//System.out.println (ratio);
    		}
    	}
    }
}
