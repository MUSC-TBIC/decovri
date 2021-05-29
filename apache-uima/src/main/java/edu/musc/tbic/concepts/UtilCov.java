package edu.musc.tbic.concepts;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.util.UriUtils;

import edu.musc.covid.type.Dosage;
import edu.musc.covid.type.Drug;
import edu.musc.covid.type.Duration;
import edu.musc.covid.type.Frequency;
import edu.musc.covid.type.LabName;
import edu.musc.covid.type.LabValue;
import edu.musc.covid.type.Route;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;


public class UtilCov 
{
    public static String getDocumentID(JCas jcas)
    {

    	String fName = null;
		FSIterator<?> it = jcas.getAnnotationIndex(SourceDocumentInformation.type).iterator();
		if (it.hasNext()) {
			SourceDocumentInformation fileLoc = (SourceDocumentInformation) it.next();
			fName = fileLoc.getUri().toString();
		}
		return fName;
    }
    
	public static void splitTokens(String str, int tB, TreeMap<Integer, Integer> map) {
		
		char pCh = str.charAt(0);
		int s = 0;
		for (int i = 0; i < str.length(); i++)
	    {
			char ch = str.charAt(i);
			boolean ifC = false;
			
	        if (Character.isDigit(ch) && i > 0) {
	        	if (!Character.isDigit(pCh)) {
	        		ifC = true;
	        	}
	        } else if (Character.isLowerCase(ch)  && i > 0) { // 0 - A // a  //ALMartial -> AL Martial
	        	if (!(Character.isLowerCase(pCh) || Character.isUpperCase(pCh))) {
	        		ifC = true;
	        	}	        	
	        } else if (Character.isUpperCase(ch)  && i > 0) { // 0 a - // A  A
	        	if (!Character.isUpperCase(pCh)) {
	        		ifC = true;
	        	} else if ( i < str.length() - 1 && Character.isLowerCase(str.charAt(i + 1))) {
	        		ifC = true;
	        	}	        	
	        } else if (i > 0) {
	        	if (Character.isDigit(pCh) || Character.isLowerCase(pCh) || Character.isUpperCase(pCh) || Character.isWhitespace(pCh) ) {
	        		ifC = true;
	        	}
	        } 
	        
	    	if (ifC && !Character.isWhitespace(ch)) {
	    		//split s i
	    		int b = s + tB;
	    		int e = i + tB;
	        	if (Character.isWhitespace(pCh)) {
		        	e = i + tB - 1;
	        	} 
	    		map.put(b, e);
	    		s = i;
	        }
	        pCh = ch;
	    }

		int b = s + tB;
		int e = tB + str.length();
		map.put(b, e);
	}	    
    
    public static void annotSent(String text, TreeMap<Integer, Integer> map) {
    	// Split sentences on four whitespaces in a row.
    	// In reality, this splits everything into paragraphs and sections
    	// rather than sentences but the paragraphs are small enough that
    	// for now, it's not a big time drag.
    	Pattern pa = Pattern.compile("    ");    	
    	Matcher m = pa.matcher(text);
    	int pos = 0;
    	while (m.find(pos)) {
    		map.put( pos , m.start() );
    		pos = m.end();
    	}
    	
    	if (pos < text.length()) {
    		map.put(pos, text.length());    		
    	}
    }
    
    public static void annotTok(String text, TreeMap<Integer, Integer> map, int o) {
    	Pattern pa = Pattern.compile("[^\\s]+");    	
    	Matcher m = pa.matcher(text);
    	int pos = 0;
    	while (m.find(pos)) {
    		map.put(o + m.start(), o + m.end());
    		pos = m.end();
    	}    	
    }
	
	public static void addCon(JCas jCas, int b, int e, String ctype) {
    	
    	switch(ctype.toLowerCase()) {
    		case "labname":
    			addToIndex(new LabName(jCas), b, e);
    			break;
    		case "labvalue":
    			addToIndex(new LabValue(jCas), b, e);
    			break;
    		case "drug":  
    			addToIndex(new Drug(jCas), b, e);
    			break;
    		case "dosage":
    			addToIndex(new Dosage(jCas), b, e);
    			break;
    		case "route":
    			addToIndex(new Route(jCas), b, e);
    			break;
    		case "frequency":
    			addToIndex(new Frequency(jCas), b, e);
    			break;
    		case "duration":
    			addToIndex(new Duration(jCas), b, e);
    			break;
    		default:
    			System.out.println("no match: " + ctype);
    	}
    	
    }

    public static void addToIndex(Annotation s, int b, int e) {
    	s.setBegin(b);
    	s.setEnd(e);
    	s.addToIndexes();			
    }

    public static void readPredict(JCas jCas, ArrayList<String> inst) {
        
    	String del = "\t";
        String cTag = "O";
        String nTag = "O";

        String begin = "";
        String end = "";
        
        String sBegin = "0";

        boolean start = false;
							        
        for (int i = 0; i < inst.size(); i++) {

            String cStr = inst.get(i);
            String nStr = "";

            //System.out.println(cStr);
            
            if (i < inst.size() - 1) {
                nStr = inst.get(i + 1);
            } else {
                nTag = "O";
            }
                    
            String cStrA[] = cStr.split(del);
            String nStrA[] = nStr.split(del);
            
            //<unk>	60	61	0	1	110-01.txt	O	O
            //          -8      -7      -6      -5      -4              -3      -2      -1
            if (cStrA.length < 3) {
                cTag = "O";
            } else {
                begin = cStrA[cStrA.length - 4];
                end = cStrA[cStrA.length - 3];
                cTag = cStrA[cStrA.length - 1];
            }

            if (nStrA.length < 3) {
                nTag = "O";
            } else {
                nTag = nStrA[nStrA.length - 1];
            }
            // sequence adjustment start
            
            String cType = "O";
            String nType = "O";
            if (!cTag.equals("O")) {
                cType = cTag.replace("B-","").replace("I-", "");
            } 
            if (!nTag.equals("O")) {
                nType = nTag.replace("B-","").replace("I-", "");
            }        
            
            if (cTag.startsWith("B-")) {
                if (nTag.startsWith("I-")) {
                    if (!cType.equals(nType)) {
                        nTag = "I-" + cType;
                    }
                } 
            } else if (cTag.startsWith("I-")) {
                if (nTag.startsWith("I-")) {
                    if (!cType.equals(nType)) {
                        nTag = "I-" + cType;
                    }
                }                
            } else if (cTag.equals("O") && nTag.startsWith("I-")) {
                nTag = "O";
            } 
            
            // sequence adjustment end            

            if (cTag.startsWith("B-")) {
                
                if (nTag.startsWith("B-") || nTag.equals("O")) {

                    String ctype = cTag.replace("B-","");
                  
                    addCon(jCas, Integer.parseInt(begin), Integer.parseInt(end), ctype);
                    start = false;
                } else if (nTag.startsWith("I-")) {
                    start = true;
                    sBegin = begin;
                } 
            } else if (cTag.startsWith("I-")) {
                if ((nTag.startsWith("B-") || nTag.equals("O"))) {
                    if (start) {
                        String ctype = cTag.replace("I-","");

                        addCon(jCas, Integer.parseInt(sBegin), Integer.parseInt(end), ctype);
                        start = false;
                    }
                } else if (nTag.startsWith("I-")) {
                    start = true;
                }
            } else if (cTag.equals("O")) {
                start = false;
            }

        }
    }    
        
    public static String predict(String iStr, BufferedReader in, PrintWriter out) {
        String oStr = "";
        if (iStr != null) {
        	
        	String endMsgE = "--<eoscE>--"; 
        	iStr += endMsgE;
        	
        	byte[] bText = iStr.getBytes();
        	String nStr = new String(bText, UTF_8);         	
        	
            out.println(nStr);
            out.flush();
        }
        
        String str = "";
        try {
			while ((str = in.readLine()) != null) {
			    oStr += str + "\n";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return oStr;
    }
        
    public static void readTag(String oStr, ArrayList<String> outs) {

        BufferedReader reader = new BufferedReader(new StringReader(oStr));
        String str = "";
        try {
        	while ((str = reader.readLine()) != null) {
                outs.add(str.trim());
        	}
		} catch(IOException e) {
  		  e.printStackTrace();
  		}
    } 
    
    public static String purgeString(String in) {
        StringBuffer out = new StringBuffer(); // Used to hold the output.
        char c; // Used to reference the current character.

        if (in == null || ("".equals(in))) return ""; // vacancy test.
        for (int i = 0; i < in.length(); i++) {
            c = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
            if (Character.isAlphabetic(c) || Character.isDigit(c)) {
                out.append(c);
            }
        }
        return out.toString();
    }    
    
    public static void writeLabs(JCas jCas, String dir, String fname) throws FileNotFoundException {

		TreeMap<Integer, String> map = new TreeMap<>();
        for (LabName n : jCas.getAnnotationIndex(LabName.class)) {
        	map.put(n.getBegin(), n.getEnd() + " labname " + n.getCUI());
        }
        for (LabValue n : jCas.getAnnotationIndex(LabValue.class)) {
        	if (n.getLabName() != null) {
            	map.put(n.getBegin(), n.getEnd() + " labValue " + n.getLabName().getBegin() + "_" + n.getLabName().getEnd());
        	} else {
            	map.put(n.getBegin(), n.getEnd() + " labValue " + "none");       		
        	}
        }
        String pTxt = getHtml(fname, jCas.getDocumentText(), map);
        writeHtml(pTxt, dir, fname);
    }
    	
    public static void writeMeds(JCas jCas, String dir, String fname) throws FileNotFoundException {

		TreeMap<Integer, String> map = new TreeMap<>();
        for (Drug n : jCas.getAnnotationIndex(Drug.class)) {
        	map.put(n.getBegin(), n.getEnd() + " drug " + n.getCUI());
        }
        for (Dosage n : jCas.getAnnotationIndex(Dosage.class)) {
        	if (n.getDrug() != null) {
            	map.put(n.getBegin(), n.getEnd() + " dosage " + n.getDrug().getBegin() + "_" + n.getDrug().getEnd());        		
        	} else {
            	map.put(n.getBegin(), n.getEnd() + " dosage " + "none");        		        		
        	}
        }
        for (Route n : jCas.getAnnotationIndex(Route.class)) {
        	if (n.getDrug() != null) {
            	map.put(n.getBegin(), n.getEnd() + " route " + n.getDrug().getBegin() + "_" + n.getDrug().getEnd());        		
        	} else {
            	map.put(n.getBegin(), n.getEnd() + " route " + "none");        		        		
        	}
        }
        for (Frequency n : jCas.getAnnotationIndex(Frequency.class)) {
        	if (n.getDrug() != null) {
            	map.put(n.getBegin(), n.getEnd() + " frequency " + n.getDrug().getBegin() + "_" + n.getDrug().getEnd());        		
        	} else {
            	map.put(n.getBegin(), n.getEnd() + " frequency " + "none");        		        		
        	}
        }
        for (Duration n : jCas.getAnnotationIndex(Duration.class)) {
        	if (n.getDrug() != null) {
            	map.put(n.getBegin(), n.getEnd() + " duration " + n.getDrug().getBegin() + "_" + n.getDrug().getEnd());        		
        	} else {
            	map.put(n.getBegin(), n.getEnd() + " duration " + "none");        		        		
        	}
        }
        
        String pTxt = getHtml(fname, jCas.getDocumentText(), map);
        writeHtml(pTxt, dir, fname);
    }

    public static void writeHtml(String text, String dir, String fname) throws FileNotFoundException {    	
        PrintStream out = new PrintStream(new FileOutputStream(new File(dir, fname + ".html")));
		out.println(text);
		out.flush();
		out.close();    	
    }
    
    public static String getHtml(String fname, String text, TreeMap<Integer, String> map) {

    	StringBuilder sb = new StringBuilder();
    	
        String head = "<html>\n<head>\n<title>" + fname + "</title>\n";
        head += "</head>\n<body>\n";
        String foot = "</body>\n</html>";
        
        sb.append(head).append("\n");

        String tmp = "";
        int tEnd = 0;

        for (int start : map.keySet()) {
			String t[] = map.get(start).split(" ");
			int end = Integer.parseInt(t[0]);
			String ctype = t[1];
			String cTxt = text.substring(start, end);
			String info = t[2];
			//System.out.println(fname + " " + ctype + " " + cTxt + " " + start + " " + end);
					
			if (tEnd < start) {
				tmp += text.substring(tEnd, start);	
				
				String ttTxt = ctype + " " + start + "  " + end + " " + info;
				tmp += "<a href=\"www.com\" title='" + ttTxt + "'>";                
                if (ctype.equalsIgnoreCase("labname")) {
                	tmp += "<font color=\"" + "brown"  + "\"> <b>" + cTxt + "</b></font>";
                } else if (ctype.equalsIgnoreCase("labvalue")) {
                	tmp += "<font color=\"" + "blue"  + "\"> <b>" + cTxt + "</b></font>";
                } else if (ctype.equalsIgnoreCase("drug")) {
                	tmp += "<font color=\"" + "brown"  + "\"> <b>" + cTxt + "</b></font>";
                } else if (ctype.equalsIgnoreCase("dosage")) {
                	tmp += "<font color=\"" + "blue"  + "\"> <b>" + cTxt + "</b></font>";
                } else if (ctype.equalsIgnoreCase("route")) {
                	tmp += "<font color=\"" + "green"  + "\"> <b>" + cTxt + "</b></font>";
                } else if (ctype.equalsIgnoreCase("frequency")) {
                	tmp += "<font color=\"" + "purple"  + "\"> <b>" + cTxt + "</b></font>";
                } else if (ctype.equalsIgnoreCase("duration")) {
                	tmp += "<font color=\"" + "orange"  + "\"> <b>" + cTxt + "</b></font>";
                } 
                tmp += "</a>";							
            } else {            	
				String ttTxt = ctype + " " + start + "  " + end + " " + info;
				
				tmp += "<a href=\"www.com\" title='" + ttTxt + "'>";                
                if (ctype.equalsIgnoreCase("labname")) {
                	tmp += "<font color=\"" + "brown"  + "\"> <b>" + cTxt + "</b></font>";
                } else if (ctype.equalsIgnoreCase("labvalue")) {
                	tmp += "<font color=\"" + "blue"  + "\"> <b>" + cTxt + "</b></font>";
                } else if (ctype.equalsIgnoreCase("drug")) {
                	tmp += "<font color=\"" + "brown"  + "\"> <b>" + cTxt + "</b></font>";
                } else if (ctype.equalsIgnoreCase("dosage")) {
                	tmp += "<font color=\"" + "blue"  + "\"> <b>" + cTxt + "</b></font>";
                } else if (ctype.equalsIgnoreCase("route")) {
                	tmp += "<font color=\"" + "green"  + "\"> <b>" + cTxt + "</b></font>";
                } else if (ctype.equalsIgnoreCase("frequency")) {
                	tmp += "<font color=\"" + "purple"  + "\"> <b>" + cTxt + "</b></font>";
                } else if (ctype.equalsIgnoreCase("duration")) {
                	tmp += "<font color=\"" + "orange"  + "\"> <b>" + cTxt + "</b></font>";
                } 
                tmp += "</a>";							
            }
            
            if (tEnd < end) {
            	tEnd = end;
            }
        }
        
        tmp += text.substring(tEnd);
        tmp = tmp.replace("\n", "<br>\n");
        sb.append(tmp).append("\n");
        sb.append(foot).append("\n");
        
        return sb.toString();
    }
    
    
    // for v2 start
    public static String dicEntrySplitTokens(String str) {

        String nStr = "";
        
        if (str.trim().isEmpty()) {
            return "";
        }
        
        char pCh = str.charAt(0);
        int s = 0;
        for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                boolean ifC = false;

            if (Character.isDigit(ch) && i > 0) {
                if (!Character.isDigit(pCh)) {
                    ifC = true;
                }
            } else if (Character.isLowerCase(ch)  && i > 0) { // 0 - A // a  //ALMartial -> AL Martial
                if (!(Character.isLowerCase(pCh) || Character.isUpperCase(pCh))) {
                    ifC = true;
                }	        	
            } else if (Character.isUpperCase(ch)  && i > 0) { // 0 a - // A  A
                if (!Character.isUpperCase(pCh)) {
                    ifC = true;
                } else if ( i < str.length() - 1 && Character.isLowerCase(str.charAt(i + 1))) {
                    ifC = true;
                }	        	
            } else if (i > 0) {
                if (Character.isDigit(pCh) || Character.isLowerCase(pCh) || Character.isUpperCase(pCh) || Character.isWhitespace(pCh) ) {
                    ifC = true;
                }
            } 

            if (ifC && !Character.isWhitespace(ch)) {
                    //split s i
                int b = s;
                int e = i;        
                if (Character.isWhitespace(pCh)) {
                    e = i - 1;
                } 
                nStr += str.substring(b, e) + " ";
                s = i;
            }
            pCh = ch;
        }

        nStr += str.substring(s, str.length());
        
        //if (!str.equals(nStr)) {
        //    System.out.println(str + "   :   " + nStr);
        //}
        return nStr;
    }    
    
    public static String nms(String str) {
        return str.replace("|", "[-b-]").replace(":", "[-c-]");
    }
    
    public static String dcr(String str) {
        return str.replace("\n", " ").replaceAll("\\s+", " ").trim();
    }
    
    public static String getCUIMed(HashMap<String, String> m, String con) {

        String cui = "";
        if (m.containsKey(con)) {
            //System.out.println(con);
            cui = m.get(con);
        }         
        return cui;
    }
    
    public static HashSet<String> getCUILab(HashMap<String, HashSet<String>> m, String con) {

        HashSet<String> cui = new HashSet<>();
        if (m.containsKey(con)) {
            //System.out.println(con);
            cui.addAll(m.get(con));
        }         
        return cui;
    }

    public static String removeH(String con, HashSet<String> pSet) {

        String ss[] = con.split(" ");
        if (pSet.contains(ss[0])) {
            con = con.replaceFirst(ss[0],"").trim();
        }
        return con;
    }

    public static String removeT(String con) {

        int e = 0;
        for (int i = 0; i < con.length(); i++) {
            if (con.charAt(i) == ',' || con.charAt(i) == '(' || con.charAt(i) == '['
                    || Character.isDigit(con.charAt(i))) {
                e = i;
                break;
            }
            
        }
        if (e > 5 && e < con.length()) {
            //String tmp = con;
            con = con.substring(0, e).trim();
            //System.out.println(con + "  :  " + tmp);
        } else if (con.toLowerCase().contains("medication")) {
        	con = con.substring(0, con.indexOf("medication")).trim();
        } else if (con.toLowerCase().contains("or store brand")) {
        	con = con.substring(0, con.indexOf("or store brand")).trim();
        }
        return con;
    }    
    
    
}
