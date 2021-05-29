package edu.musc.tbic.concepts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.musc.covid.type.Drug;
import edu.musc.covid.type.LabName;
import edu.musc.tbic.uima.Decovri;

public class CUIAnnotator extends JCasAnnotator_ImplBase {

	private static Logger mLogger = LoggerFactory.getLogger( Decovri.class );
	
	// TODO - can be changed to uima 2 style ?
	public static final String PARAM_MEDDICFILE = "MedDicFile";
	@ConfigurationParameter( name = PARAM_MEDDICFILE , 
							 description = "Filtered MRCONSO file containing only RxNorm-related entries" , 
							 mandatory = true )
	private String mMedDicFile;
	
	public static final String PARAM_LABDICFILE = "LabDicFile";
	@ConfigurationParameter( name = PARAM_LABDICFILE , 
							 description = "Full MRCONSO file" , 
							 mandatory = true )
	private String mLabDicFile;
	
	public static final String PARAM_DBSTYFILE = "DbStyFile";
	@ConfigurationParameter( name = PARAM_DBSTYFILE , 
					 		 description = "MRSTY file" , 
					 		 mandatory = true )
	private String mDbStyFile;
        
    HashSet<String> pSet;
    HashMap<String, String> tKM;
    HashMap<String, String> tKS;

    HashMap<String, String> sty;
    HashMap<String, String> styName;
    
    HashMap<String, String> medDics;
    HashMap<String, HashSet<String>> labDics;
    
    public void initialize(UimaContext uimaContext)	throws ResourceInitializationException {
		super.initialize(uimaContext);

		// TODO - can be changed to uima 2 style ?
		mMedDicFile = (String)(uimaContext.getConfigParameterValue("MedDicFile"));
		mLabDicFile = (String)(uimaContext.getConfigParameterValue("LabDicFile"));
		mDbStyFile = (String)(uimaContext.getConfigParameterValue("DbStyFile"));

	    pSet = new HashSet<>();   
        String pA[] = {"a", "an", "the", "my", "your", "his", "her", "their", "its", "this", "that", "these", "those", "our", "any"};
        pSet.addAll(Arrays.asList(pA));
    		

        // for lab semantic types
        String ttM[] = {"Laboratory Procedure	T059", "Laboratory or Test Result	T034"};                
        String ttS[] = {"Antibiotic	T195", "Bacterium	T007", "Biologically Active Substance	T123", "Fungus	T004",
        		"Hormone	T125", "Immunologic Factor	T129", "Inorganic Chemical	T197", "Pharmacologic Substance	T121", "Virus	T005", "Vitamin	T127"};
        
        tKM = new HashMap<>();
        for (String t : ttM) {
            String s[] = t.split("\t");
            tKM.put(s[1], s[0]);
            //mLogger.debug(s[0] + "\t" + s[1]);
        }

        tKS = new HashMap<>();
        for (String t : ttS) {
            String s[] = t.split("\t");
            tKS.put(s[1], s[0]);
            //mLogger.debug(s[0] + "\t" + s[1]);
        }
        
        
        sty = new HashMap<>();
        styName = new HashMap<>();
        medDics = new HashMap<>();
        labDics = new HashMap<>();
		
        readMRSTY(mDbStyFile, sty, styName);            
        // for lab semantic types
        mLogger.info("load med dic start");
	    readMedDicFile(mMedDicFile, medDics);
	    mLogger.info("load med dic done");
		
	    mLogger.info("load lab dic start");
        readLabDicFile(mLabDicFile, labDics);
        mLogger.info("load lab dic done");
    }        

    public void readMRSTY(String file, HashMap<String, String> map, HashMap<String, String> mapName) {

        String str = "";
        {
            BufferedReader txtin = null;
            try {
                txtin = new BufferedReader(new FileReader(file));
                while ((str = txtin.readLine()) != null) {
                    //C0000039|T109|A1.4.1.2.1|Organic Chemical|AT45562015|256|
                    String s[] = str.split("\\|", -1);
                    String cui = s[0];
                    String sty = s[1];
                    String name = s[3];
                    
                    map.put(cui, sty);
                    if (!mapName.containsKey(sty)) {
                        mapName.put(sty, name);
                    }
                }
                
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    txtin.close();
                } catch (Exception ex) {

               }
            }
        }
    }        
    
    public static void readMedDicFile(String fileName, HashMap<String, String> map) {

        String str = "";
        BufferedReader txtin = null;
        try {
            txtin = new BufferedReader(new FileReader(fileName));
            
            while ((str = txtin.readLine()) != null) {
                String s[] = str.split("\\|", -1);
                /*
                if (s.length != 19) {
                    mLogger.debug(str + " " + s.length);
                }
                */
                //0 cui
                //11 SAB
                //13 CODE
                String cui = s[0];
                String lang = s[1];
                String stt = s[4];
                //String sab = s[11];
                String tty = s[12];
                //String code = s[13];
                
                //C0002800|ENG|P|L0039089|PF|S0090601|Y|A10336519|23345|768||RXNORM|BN|768|Synflex|0|O|256|

                if (!lang.equalsIgnoreCase("ENG")) {
                    continue;
                }
                if (stt.equalsIgnoreCase("VC")) {
                    continue;
                }
                if (tty.equalsIgnoreCase("FN") 
                        || tty.equalsIgnoreCase("DF") 
                        || tty.equalsIgnoreCase("OAF") 
                        || tty.equalsIgnoreCase("OF")
                        || tty.equalsIgnoreCase("MTH_OAF") 
                        || tty.equalsIgnoreCase("MTH_FN") 
                        || tty.equalsIgnoreCase("MTH_OF")) {
                    continue;
                }        
                String rLex = s[14];
                if (rLex.contains("-RETIRED-") || rLex.contains(" NOS")) {
                    continue;
                }                                    
                if (rLex.matches("[0-9]+ HR.*")) {
                    continue;
                }                 

                String lex = UtilCov.dicEntrySplitTokens(rLex).toLowerCase();
                lex = lex.replace("&", "and").replace("\"", "").replace("<", "").replace(">", "").replace("\n", " ");
                lex = UtilCov.nms(lex).trim();
                
                if (lex.isEmpty()) {
                    continue;
                }

                if (!map.containsKey(lex)) {
                    map.put(lex, cui);
                }
            }                   
            
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                txtin.close();
            } catch (Exception ex) {

           }
        }

    }        
    
    public static void readLabDicFile(String fileName, HashMap<String, HashSet<String>> map) {

        String str = "";
        {
            BufferedReader txtin = null;
            try {
                txtin = new BufferedReader(new FileReader(fileName));
                
                while ((str = txtin.readLine()) != null) {
                    String s[] = str.split("\\|", -1);
                    if (s.length != 19) {
                        mLogger.debug(str + " " + s.length);
                    }
                    //0 cui
                    //11 SAB
                    //13 CODE
                    String cui = s[0];
                    String lang = s[1];
                    //String pre = s[2]; // P S
                    //String stt = s[4];
                    String sab = s[11];
                    String tty = s[12];
                    //String code = s[13];
                    
                    //C0002800|ENG|P|L0039089|PF|S0090601|Y|A10336519|23345|768||RXNORM|BN|768|Synflex|0|O|256|

                    if (!lang.equalsIgnoreCase("ENG")) {
                        continue;
                    }
                    if (sab.equalsIgnoreCase("RXNORM")) {
                        continue;
                    }
                    
                    if (tty.equalsIgnoreCase("FN") 
                            || tty.equalsIgnoreCase("DF") 
                            || tty.equalsIgnoreCase("OAF") 
                            || tty.equalsIgnoreCase("OF")
                            || tty.equalsIgnoreCase("MTH_OAF") 
                            || tty.equalsIgnoreCase("MTH_FN") 
                            || tty.equalsIgnoreCase("MTH_OF") 
                            ) {
                        continue;
                    }        
                    String rLex = s[14];
                    if (rLex.contains("-RETIRED-") || rLex.contains(" NOS")) {
                        continue;
                    }                    
                    
                    if (rLex.matches("[0-9]+ HR.*")) {
                        continue;
                    }                 
                    
                    String lex = UtilCov.dicEntrySplitTokens(rLex).toLowerCase();
                    lex = lex.replace("&", "and").replace("\"", "").replace("<", "").replace(">", "").replace("\n", " ");
                    lex = UtilCov.nms(lex).trim();
                    
                    if (lex.isEmpty()) {
                        continue;
                    }
                    
                    if (map.containsKey(lex)) {
                        HashSet<String> es = map.get(lex);
                        if (!es.contains(cui)) {
                            es.add(cui);
                        }
                    } else {
                        HashSet<String> es = new HashSet<>();
                        es.add(cui);
                        map.put(lex, es);
                    }                    
               }                   
                
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    txtin.close();
                } catch (Exception ex) {

               }
            }
        }

    }        
    
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		assignMedCUI(jCas);		
		assignLabCUI(jCas);
	}
	
	public void assignMedCUI(JCas jCas) throws AnalysisEngineProcessException {
		
        for (Drug n : jCas.getAnnotationIndex(Drug.class)) {
        	String con = n.getCoveredText();
            con = UtilCov.dcr(UtilCov.dicEntrySplitTokens(con).toLowerCase().trim());
            
            String cui = UtilCov.getCUIMed(medDics, con);
            if (!cui.isEmpty()) {
            	n.setCUI(cui);
                continue;
            }

            if (!con.equals(UtilCov.removeH(con, pSet))) {
                con = UtilCov.removeH(con, pSet);
                cui = UtilCov.getCUIMed(medDics, con);
                if (!cui.isEmpty()) {
                	n.setCUI(cui);
                    continue;
                }
            }
            
            if (!con.equals(UtilCov.removeT(con))) {
                con = UtilCov.removeT(con);
                cui = UtilCov.getCUIMed(medDics, con);
                if (!cui.isEmpty()) {
                	n.setCUI(cui);
                    continue;
                }
            }
        	n.setCUI("CUI-less");
        }
	}
	
	public void assignLabCUI(JCas jCas) throws AnalysisEngineProcessException {
		
        for (LabName n : jCas.getAnnotationIndex(LabName.class)) {
        	String con = n.getCoveredText();
            con = UtilCov.dcr(UtilCov.dicEntrySplitTokens(con).toLowerCase().trim());
            
            HashSet<String> cui = new HashSet<>();
            HashSet<String> tmp = UtilCov.getCUILab(labDics, con);
            if (!tmp.isEmpty()) {
                cui.addAll(tmp);                
                n.setCUI(setCUILab(cui));
                continue;
            }

            if (!con.equals(UtilCov.removeH(con, pSet))) {
                con = UtilCov.removeH(con, pSet);
                tmp = UtilCov.getCUILab(labDics, con);
                if (!tmp.isEmpty()) {
                    cui.addAll(tmp);                
                    n.setCUI(setCUILab(cui));
                    continue;
                }
            }
            
            if (!con.equals(UtilCov.removeT(con))) {
                con = UtilCov.removeT(con);
                tmp = UtilCov.getCUILab(labDics, con);
                if (!tmp.isEmpty()) {
                    cui.addAll(tmp);                
                    n.setCUI(setCUILab(cui));
                    continue;
                }
            }
        	n.setCUI("CUI-less");
        }
	}        
        	
    public String setCUILab(HashSet<String> cuis) {
    	
        String cuiA = "CUI-less";
        for (String cui : cuis) {
            String st = sty.get(cui); //T195 
            if (tKM.containsKey(st)) {
                cuiA = cui;
                break;
            }
        }
        String cF = "CUI-less";
        boolean ifF = true;
        if (cuiA.equalsIgnoreCase("CUI-less")) {
            for (String cui : cuis) {
                if (ifF) {
                    cF = cui;
                    ifF = false;
                }
                String st = sty.get(cui); //T195 
                if (tKS.containsKey(st)) {
                    cuiA = cui;
                    break;
                }
            }                    
        }

        if (cuiA.equalsIgnoreCase("CUI-less")
                && !cF.equalsIgnoreCase("CUI-less")) {
            cuiA = cF;
        }
        
        return cuiA;
    }    	

}
