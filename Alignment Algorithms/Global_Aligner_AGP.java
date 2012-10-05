package globalalignerwithagp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Global_Aligner_AGP {
	private int[][] _V;
	private int[][] _A;
	private int[][] _E;
	private int[][] _F;

	public Global_Aligner_AGP() {}
	protected void freematrix() {
		this._V = null;
		this._A = null;
		this._E = null;
		this._F = null;
	}
	
	public void AGPaligner(String str1, String str2, int match, int mis, int open, int extend) {
		int i, j, max, c, r, w;
		String Alignmentf1 = "";
		String Alignmentf2 = "";
		this._V = new int[str1.length() + 1][str2.length() + 1];
		this._A = new int[str1.length() + 1][str2.length() + 1];
		this._E = new int[str1.length() + 1][str2.length() + 1];
		this._F = new int[str1.length() + 1][str2.length() + 1];
		this._V[0][0] = 0;
		for (i = 1; i <= str1.length(); i++) {
			this._V[i][0] = this._E[i][0] = open + i * extend;
		}
		for (j = 1; j <= str2.length(); j++) {
			this._V[0][j] = this._F[0][j] = open + j * extend;
		}
		for (i = 1; i <= str1.length(); i++) {
			for (j = 1; j <= str2.length(); j++) {

				int maximum_1 = this._E[i][j-1] + extend;
				if (this._V[i][j-1] + open + extend > maximum_1) {
					maximum_1 = this._V[i][j-1] + open + extend;
				}
				int maximum_2 = this._F[i-1][j] + extend;
				if (this._V[i-1][j] + open + extend > maximum_2) {
					maximum_2 = this._V[i-1][j] + open + extend;
				}
				
				if (str1.charAt(i-1) == str2.charAt(j-1)) {
					this._A[i][j] = this._V[i-1][j-1] + match;
				} else {
					this._A[i][j]  = this._V[i-1][j-1] + mis;
				}
				max = Math.max(_A[i][j], _E[i][j]) > Math.max(_E[i][j], _F[i][j]) ? Math.max(_A[i][j], _E[i][j]) : Math.max(_E[i][j], _F[i][j]);
				if (max == _A[i][j]) {
					_V[i][j] = _A[i][j];
				} else if (max == _E[i][j]) {
					_V[i][j] = _E[i][j];
				} else {
					_V[i][j] = _F[i][j];
				}
			}
		}
		
        System.out.println("The matrix Matrix[][]:");
        for(c=0;c <= str1.length();c++){
            for(r=0;r <= str2.length();r++){
                System.out.print(this._V[c][r]+"  ");  
            }
            System.out.println();
        }
        
		c = str1.length();
		r = str2.length();
		while (c > 0 || r > 0) {				
			int Score = this._V[c][r];
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
				int ScoreDiag = this._V[c-1][r-1];
				int ScoreLeft = this._V[c-1][r];
				int ScoreUp = this._V[c][r-1];
				if (c > 0 && r >0) {
		            if (str1.charAt(c - 1) == str2.charAt(r - 1)) {
		                w = match;
		            } else {
		                w = open;
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
					if (Score == ScoreLeft + open) {
		                Alignmentf1 = str1.charAt(c-1) + Alignmentf1;
		                Alignmentf2 = "_" + Alignmentf2;
		                c = c - 1;
		                continue;
					}
				}
				if (r > 0) {
					if (Score == ScoreUp + open) {
		            	Alignmentf1 = "_" + Alignmentf1;
		                Alignmentf2 = str2.charAt(r-1) + Alignmentf2;
		                r = r - 1; 
		                continue;
					}
				}
			}
		}
        /**
         * Same with global alignment. Print the score and aligned sequence here
         * */
        System.out.println("Optimal Alignment score = " + this._V[str1.length()][str2.length()]);
        System.out.println("Sequence1 = "+Alignmentf1);
        System.out.println("Sequence2 = "+Alignmentf2);
        
	}
	
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
    
    public static void main(String[] args) {
    	Global_Aligner_AGP a = new Global_Aligner_AGP();
    	String seq1 = a.readFile(args[0]);
    	String seq2 = a.readFile(args[1]);
    	a.AGPaligner(seq1, seq2, Integer.parseInt(args[2]), Integer.parseInt(args[3]), 
    			Integer.parseInt(args[4]), Integer.parseInt(args[5]));
    	a.freematrix();
    }
}
