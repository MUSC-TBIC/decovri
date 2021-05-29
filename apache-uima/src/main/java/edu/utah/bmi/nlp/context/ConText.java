package edu.utah.bmi.nlp.context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties;
import edu.musc.tbic.uima.Decovri;


/**
 * Class used to analyze UMLS concept context (based on the 'ConText' algorithm by Chapman et al.)
 * @author Julien Thibault
 * 
 * Adapted for OMOP CDM by Paul M. Heider
 *
 */
public class ConText {

    private Logger mLogger = LoggerFactory.getLogger( Decovri.class );
    
	public enum NegationContext{
		Affirmed, Negated, Possible;
	}
	public enum TemporalityContext{
		Recent, Historical, Hypothetical;
	}
	
	private static final int MAX_WINDOW = 15;
	
	public static final String PARAM_NEGEX_PHRASES = "negexPhrases";

    
    /**
     * Name of output file for writing a log of ConText processing
     */
    public static final String PARAM_CONTEXT_LOG = "ConTextLogFile";
    @ConfigurationParameter( name = PARAM_CONTEXT_LOG , 
                             description = "Filename for writing log of ConText processing" , 
                             mandatory = false )
    private String mConTextLogFile;
    
    /**
     * Name of configuration parameter that contains the filename of all negex rules
     */
    public static final String PARAM_NEGEX_PHRASE_FILE = "NegExPhraseFile";
    @ConfigurationParameter( name = PARAM_NEGEX_PHRASE_FILE , 
                             description = "Filename containing NegEx rules" , 
                             mandatory = false )
    private String mNegExPhraseFile;
	
	private File mLogFile;
	
	private Pattern regexPseudo;
	
	private Pattern regexNegPre;
	private Pattern regexNegPost;
	private Pattern regexPossPre;
	private Pattern regexPossPost;
	private Pattern regexNegEnd;
	
	private Pattern regexExpPre;
	private Pattern regexExpEnd;
	
	private Pattern regexHypoPre;
	private Pattern regexHypoEnd;
	private Pattern regexHypoExpEnd;
	
	private Pattern regexHistPre;
	private Pattern regexHist1w;
	private Pattern regexHistEnd;
	private Pattern regexHistExpEnd;
	
	private Pattern regexTime;
	private Pattern regexTimeFor;
	private Pattern regexTimeSince;
	
	private static final String regExUmlsTag = "\\[\\d+\\]"; //pattern to recognize UMLS concepts (like <C1234567>)
	
	private List<Note_Nlp_TableProperties> mappingResults;
	private static ConText _analyzer = null;
    
    /**
     * Name of configuration parameter that must be set to the full type description for concepts
     */
    public static final String PARAM_CONCEPTTYPE = "ConceptType";
    @ConfigurationParameter( name = PARAM_CONCEPTTYPE , 
                             description = "Full type description for concepts" , 
                             mandatory = false )
    private String mConceptType;
	
	/**
	 * Get instance of the context analyzer
	 * @param aContext Context analyzer object
	 * @return
	 * @throws ResourceInitializationException
	 * @throws IOException
	 */
	public static ConText getAnalyzer(UimaContext aContext) throws ResourceInitializationException, IOException 
	{
		if( _analyzer == null ){
			return new ConText(aContext);
		} else {
		    return _analyzer;
		}
	}
	
	/**
	 * Initialization regex (load parameters from the configuration file)
	 * @throws IOException 
	 */
	private ConText(UimaContext aContext) throws ResourceInitializationException, IOException 
	{	
        if( aContext.getConfigParameterValue( "ConceptType" ) == null ){
            mConceptType = "org.apache.uima.conceptMapper.UmlsTerm";
        } else {
            mConceptType = (String) aContext.getConfigParameterValue( "ConceptType" );
        }
        
        if( aContext.getConfigParameterValue( "ConTextLogFile" ) == null ||
            ( (String)aContext.getConfigParameterValue( "ConTextLogFile" ) ).trim().equals( "" ) ){
            mLogFile = null;
        } else {
            mLogFile = new File( (String)aContext.getConfigParameterValue( "ConTextLogFile" ) );
            if( mLogFile.exists() ){
                mLogFile.delete();
            }
            mLogFile.createNewFile();
        }
        
		String[] regexes = {};
		if( aContext.getConfigParameterValue( "NegExPhraseFile" ) == null ){
		    regexes = (String[])aContext.getConfigParameterValue(PARAM_NEGEX_PHRASES);
		} else {
		    List<String> ruleList = new ArrayList<String>();
		    String ruleFilename = (String) aContext.getConfigParameterValue( "NegExPhraseFile" );
		    File ruleFile = new File( ruleFilename );
		    FileReader fr = new FileReader( ruleFile );  
		    BufferedReader br = new BufferedReader( fr );
		    String line;  
		    while( ( line = br.readLine() ) != null ){
		        if( line.trim().equals( "" ) ){
		            continue;
		        }
		        ruleList.add( line.trim() );
		    }  
		    fr.close();  
            regexes = ruleList.toArray( regexes );
		}
		String regex_PSEUDO = "";
		String regex_NEG_PRE = "";
		String regex_NEG_POST = "";
		String regex_POSS_PRE = "";
		String regex_POSS_POST = "";
		String regex_NEG_END = "";
		
		String regex_EXP_PRE = "";
		String regex_EXP_END = "";
		
		String regex_HYPO_PRE = "";
		String regex_HIST_PRE = "";
		String regex_HIST_1W = "";
		
		String regex_HYPO_END = "";
		String regex_HIST_END = "";
		String regex_HIST_EXP_END = "";
		String regex_HYPO_EXP_END = "";
		
		for (int i=0; i < regexes.length; i++)
		{
			int attrIndex = regexes[i].indexOf(',');
			int attrIndex2 = regexes[i].lastIndexOf(',');
			
			String phrase = regexes[i].substring(0,attrIndex).replaceAll(" ", "[\\\\s\\\\-]");
			String position = regexes[i].substring(attrIndex+1, attrIndex2);
			String contextType = regexes[i].substring(attrIndex2+1);
			
			if (position.compareTo("pseudo")==0)
			{
				regex_PSEUDO = regex_PSEUDO + "|" + phrase;
			}
			else if (position.compareTo("termin")==0)
			{
				if (contextType.compareTo("neg")==0)
					regex_NEG_END = regex_NEG_END + "|[\\s\\.]+" + phrase + "[\\s\\.\\:;\\,]+";
				else if (contextType.compareTo("hypo")==0)
					regex_HYPO_END = regex_HYPO_END + "|[\\s\\.]+" + phrase + "[\\s\\.\\:;\\,]+";
				else if (contextType.compareTo("hist")==0)
					regex_HIST_END = regex_HIST_END + "|[\\s\\.]+" + phrase + "[\\s\\.\\:;\\,]+";
				else if (contextType.compareTo("histexp")==0)
					regex_HIST_EXP_END = regex_HIST_EXP_END + "|[\\s\\.]+" + phrase + "[\\s\\.\\:;\\,]+";
				else if (contextType.compareTo("hypoexp")==0)
					regex_HYPO_EXP_END = regex_HYPO_EXP_END + "|[\\s\\.]+" + phrase + "[\\s\\.\\:;\\,]+";
				else if (contextType.compareTo("exp")==0)
					regex_EXP_END = regex_EXP_END + "|[\\s\\.]+" + phrase + "[\\s\\.\\:;\\,]+";
			}
			else if (position.compareTo("pre")==0)
			{
				if (contextType.compareTo("neg")==0)
					regex_NEG_PRE = regex_NEG_PRE + "|[\\s\\.]+" + phrase + "[\\s\\.\\:;\\,]+";
				else if (contextType.compareTo("poss")==0)
					regex_POSS_PRE = regex_POSS_PRE + "|[\\s\\.]+" + phrase + "[\\s\\.\\:;\\,]+";
				else if (contextType.compareTo("hypo")==0)
					regex_HYPO_PRE = regex_HYPO_PRE + "|[\\s\\.]+" + phrase + "[\\s\\.\\:;\\,]+";
				else if (contextType.compareTo("exp")==0)
					regex_EXP_PRE = regex_EXP_PRE + "|[\\s\\.]+" + phrase + "[\\s\\.\\:;\\,]+";
				else if (contextType.compareTo("hist")==0)
					regex_HIST_PRE = regex_HIST_PRE + "|[\\s\\.]+" + phrase + "[\\s\\.\\:;\\,]+";
				else if (contextType.compareTo("hist")==0)
					regex_HIST_1W = regex_HIST_1W + "|[\\s\\.]+" + phrase + "[\\s\\.\\:;\\,]+";
			}
			else if (position.compareTo("post")==0)
			{
				if (contextType.compareTo("neg")==0)
					regex_NEG_POST = regex_NEG_POST + "|[\\s\\.]+" + phrase + "[\\s\\.\\:;\\,]+";
				else if (contextType.compareTo("poss")==0)
					regex_POSS_POST = regex_POSS_POST + "|[\\s\\.]+" + phrase + "[\\s\\.\\:;\\,]+";
			}
			
		}
		if (regex_PSEUDO.length()>0)
			regexPseudo = Pattern.compile(regex_PSEUDO.substring(2));
		
		//negation context
		if (regex_NEG_PRE.length()>0)
			regexNegPre = Pattern.compile(regex_NEG_PRE.substring(1));
		if (regex_NEG_POST.length()>0)
			regexNegPost = Pattern.compile(regex_NEG_POST.substring(1));
		if (regex_NEG_END.length()>0)
			regexNegEnd = Pattern.compile(regex_NEG_END.substring(1));
		if (regex_POSS_PRE.length()>0)
			regexPossPre = Pattern.compile(regex_POSS_PRE.substring(1));
		if (regex_POSS_POST.length()>0)
			regexPossPost = Pattern.compile(regex_POSS_POST.substring(1));
		
		//temporality context
		if (regex_HIST_PRE.length()>0)
			regexHistPre = Pattern.compile(regex_HIST_PRE.substring(1));
		if (regex_HYPO_PRE.length()>0)
			regexHypoPre = Pattern.compile(regex_HYPO_PRE.substring(1));
		if (regex_HIST_1W.length()>0)
			regexHist1w = Pattern.compile(regex_HIST_1W.substring(1));
		if (regex_HIST_END.length()>0)
			regexHistEnd = Pattern.compile(regex_HIST_END.substring(1));
		if (regex_HYPO_END.length()>0)
			regexHypoEnd = Pattern.compile(regex_HYPO_END.substring(1));
		
		//experiencer and mixed
		if (regex_EXP_PRE.length()>0)
			regexExpPre = Pattern.compile(regex_EXP_PRE.substring(1));
		if (regex_EXP_END.length()>0)
			regexExpEnd = Pattern.compile(regex_EXP_END.substring(1));
		if (regex_HYPO_EXP_END.length()>0)
			regexHypoExpEnd = Pattern.compile(regex_HYPO_EXP_END.substring(1));
		if (regex_HIST_EXP_END.length()>0)
			regexHistExpEnd = Pattern.compile(regex_HIST_EXP_END.substring(1));
		
		
		mLogger.debug("-----------------------------------------------------");
		mLogger.debug("NEGEX CONFIG: ");
		mLogger.debug("\tMax window: " + MAX_WINDOW + " words");
		mLogger.debug("\tPRENEG:   " + regex_NEG_PRE.substring(1));
		mLogger.debug("\tPOSTNEG:  " + regex_NEG_POST.substring(1));
		mLogger.debug("\tPREPOSS:  " + regex_POSS_PRE.substring(1));
		//mLogger.info("POSTPOSS: " + regex_POSS_POST.substring(1));
		mLogger.debug("\tPSEUDO:   " + regex_PSEUDO.substring(1));
		mLogger.debug("\tNEGTERM:  " + regex_NEG_END.substring(1));
	}
	
	private String mapNameToTermExists( NegationContext currentNegationContext ){
	    String term_exists = "";
	    if( currentNegationContext == NegationContext.Affirmed ){
	        term_exists = "y";
	    } else if( currentNegationContext == NegationContext.Negated ){
            term_exists = "n";
        } else if( currentNegationContext == NegationContext.Possible ){
            term_exists = "p";
        }
	    return( term_exists );
	}
	
	/**
	 * Pre-processing on the sentence (replace UMLS and negation terms by keywords)
	 * @param sent
	 * @return Tagged sentence (UMLS concepts and context base terms)
	 * @throws Exception 
	 */
	private String preProcessSentence(String sent) throws Exception
	{
		//mLogger.info("--------------------------------");
		//mLogger.info(sent);
		String sentenceTagged = " " + sent.replaceAll("\\s+", " ").toLowerCase();
		
		int lastOffset = 0;
		int charOffset=0;
		
		//replacing UMLS concepts with special tag
		for (int i=0;i<mappingResults.size();i++)
		{
			String tag="";
			Note_Nlp_TableProperties mapping = mappingResults.get(i);
			String umlsConcept = mapping.getCoveredText().replaceAll("\\s+", " ").toLowerCase();
			tag = " [" + i + "] ";
			//mLogger.info("\t" + sentenceTagged);
			//mLogger.info("\tconcept: " + umlsConcept + " " + tag);
			
			int conceptIndex = sentenceTagged.indexOf(umlsConcept);
			if (conceptIndex != -1)
			{
				charOffset = conceptIndex;
				sentenceTagged = sentenceTagged.substring(0,charOffset) + tag + sentenceTagged.substring(charOffset+umlsConcept.length());
				lastOffset = charOffset + tag.length();
			}
			else //overlapping concepts
			{
				mLogger.warn("[ConText]: Overlapping between concepts ("+ (i-1) +") and ("+ i +")\nLast offset: " + lastOffset);
				if (lastOffset<sentenceTagged.length()-2)
					sentenceTagged = sentenceTagged.substring(0, lastOffset) + tag + sentenceTagged.substring(lastOffset+1);
				else
					sentenceTagged = sentenceTagged + " " + tag;
				lastOffset = lastOffset + tag.length();
				//throw new Exception("[ConText] Cannot find '" + umlsConcept + "' in original sentence ('"+ sentenceTagged +"')");
			}
		}
		
		//replacing negation phrases with corresponding tags
		
		//negation phrases
		if (regexPseudo != null){
			Matcher m0 = regexPseudo.matcher(sentenceTagged);
			sentenceTagged = m0.replaceAll(" <NEG_PSEUDO> ");
		}
		if (regexNegPre != null){
			Matcher m1 = regexNegPre.matcher(sentenceTagged);
			sentenceTagged = m1.replaceAll(" <NEG_PRE> ");
		}
		if (regexPossPre != null){
			Matcher m2 = regexPossPre.matcher(sentenceTagged);
			sentenceTagged = m2.replaceAll(" <POSS_PRE> ");
		}
		if (regexNegPost != null){
			Matcher m3 = regexNegPost.matcher(sentenceTagged);
			sentenceTagged = m3.replaceAll(" <NEG_POST> ");
		}
		if (regexPossPost != null){
			Matcher m4 = regexPossPost.matcher(sentenceTagged);
			sentenceTagged = m4.replaceAll(" <POSS_POST> ");
		}
		if (regexNegEnd != null){
			Matcher m5 = regexNegEnd.matcher(sentenceTagged);
			sentenceTagged = m5.replaceAll(" <NEG_END> ");
		}
		
		//experiencer phrases
		if (regexExpPre != null){
			Matcher m6 = regexExpPre.matcher(sentenceTagged);
			sentenceTagged = m6.replaceAll(" <EXP_PRE> ");
		}
		if (regexExpEnd != null){
			Matcher m14 = regexExpEnd.matcher(sentenceTagged);
			sentenceTagged = m14.replaceAll(" <EXP_END> ");
		}
		
		//hypothesis
		if (regexHypoPre != null){
			Matcher m7 = regexHypoPre.matcher(sentenceTagged);
			sentenceTagged = m7.replaceAll(" <HYPO_PRE> ");
		}
		if (regexHypoEnd != null){
			Matcher m10 = regexHypoEnd.matcher(sentenceTagged);
			sentenceTagged = m10.replaceAll(" <HYPO_END> ");
		}
		
		//temporality
		if (regexHistPre != null){
			Matcher m8 = regexHistPre.matcher(sentenceTagged);
			sentenceTagged = m8.replaceAll(" <HIST_PRE> ");
		}
		if (regexHist1w != null){
			Matcher m9 = regexHist1w.matcher(sentenceTagged);
			sentenceTagged = m9.replaceAll(" <HIST_1W> ");
		}
		if (regexHistEnd != null){
			Matcher m12 = regexHistEnd.matcher(sentenceTagged);
			sentenceTagged = m12.replaceAll(" <HIST_END> ");
		}
		
		// mixed
		if (regexHypoExpEnd != null){
			Matcher m11 = regexHypoExpEnd.matcher(sentenceTagged);
			sentenceTagged = m11.replaceAll(" <HYPO_EXP_END> ");
		}
		if (regexHistExpEnd != null){
			Matcher m13 = regexHistExpEnd.matcher(sentenceTagged);
			sentenceTagged = m13.replaceAll(" <HIST_EXP_END> ");
		}
		
		//time 
		regexTime = Pattern.compile("((1[4-9]|[1-9]?[2-9][0-9])[ |-][day|days] of)|" +
				"(([2-9]|[1-9][0-9])[ |-][week|weeks] of)|" +
				"(([1-9]?[0-9])[ |-][month|months|year|years] of)");//pattern to recognize expressions of >14 days
		regexTimeFor = Pattern.compile("[for|over] the [last|past] (((1[4-9]|[1-9]?[2-9][0-9])[ |-][day|days] of)|" +
				"(([2-9]|[1-9][0-9])[ |-][week|weeks] of)|" +
				"(([1-9]?[0-9])[ |-][month|months|year|years] of))");//other pattern to recognize expressions of >14 days
		regexTimeSince = Pattern.compile("since [last|the last]? ((([2-9]|[1-9][0-9]) weeks ago)|" +
				"(([1-9]?[0-9])? [month|months|year|years] ago)|" +
				"([january|february|march|april|may|june|july|august|september|october|november|december|spring|summer|fall|winter]))");
		Matcher mTime = regexTimeFor.matcher(sentenceTagged);
		sentenceTagged = mTime.replaceAll(" <TIME_PRE> ");
		mTime = regexTime.matcher(sentenceTagged);
		sentenceTagged = mTime.replaceAll(" <TIME_PRE> ");
		mTime = regexTimeSince.matcher(sentenceTagged);
		sentenceTagged = mTime.replaceAll(" <TIME_POST> ");
		
		return sentenceTagged;
	}
	
	/**
	 * Context analysis on the given sentence.
	 * @param mappings List of UMLS concepts in the sentence
	 * @param sent Sentence to anlayze
	 * @param sectionID LOINC ID of the section title
	 */
	public void applyContext(List<Note_Nlp_TableProperties> mappings, String sentence, String sectionID) throws Exception
	{
		mappingResults = mappings;
		
		//pre-processing on the sentence (replace UMLS and negation terms by keywords)
		String tagged = preProcessSentence(sentence);
		
		//tokenizing the sentence in words
		String[] words =  tagged.split("[,;\\s]+");
		
		applyNegEx(words);
		applyTemporality(words, sectionID);
		applyExperiencer(words, sectionID);
		
		//dump tagged sentence
		dumpContextSentence(sentence,tagged);
	}
	
	/**
	 * Apply NegEx algorithm to find negation context of the UMLS concepts found in the sentence
	 * @return
	 */
	public void applyNegEx(String[] words) throws Exception
	{
		//Going from one negation to another, and creating the appropriate window
		int m = 0;
		List<String> window = new ArrayList<String>();
		
		//for each word in the sentence
		while (m < words.length)
		{
			//IF word is a pseudo-negation, skips to the next word
			if(words[m].equals("<NEG_PSEUDO>"))
			{
				m++;
			}
			//IF word is a pre-UMLS concept negation or possible...
			else if(words[m].matches("<NEG_PRE>|<PREP>"))
			{
				//find window (default is six words after the negation phrase)
				int maxWindow = MAX_WINDOW;
				if (words.length < m + maxWindow) maxWindow = words.length - m;
				for(int o=1; o < maxWindow; o++)
				{
					if(words[m+o].matches("<NEG_PRE>|<PREP>|<NEG_POST>|<POSS_POST>|<NEG_END>"))
						break;
					else window.add(words[m+o]);
				}
				
				//get type of Negation
				NegationContext currentNegationContext = NegationContext.Affirmed;
				if (words[m].equals("<NEG_PRE>")) {
					currentNegationContext = NegationContext.Negated;
				}
				else if(words[m].equals("<POSS_PRE>")) 
					currentNegationContext = NegationContext.Possible;
				
				//check if there are UMLS concepts in the window
				for(int w=0; w<window.size(); w++) {
					if(window.get(w).matches(regExUmlsTag)){
						String umlsWord = window.get(w);
						int index = Integer.parseInt(umlsWord.replaceAll("\\[|\\]",""));
						mappingResults.get(index).setTerm_exists( mapNameToTermExists( currentNegationContext ) );
					}
				}
				window.clear();
				m++;
			}
			//IF word a post-UMLS concept negation or possible
			else if(words[m].matches("<NEG_POST>|<POSS_POST>"))
			{
				//find window (default is six words before the negation phrase)
				int maxWindow = MAX_WINDOW;
				if (m < maxWindow) maxWindow = m;
				for(int o=1; o < maxWindow; o++) {
					if(words[m-o].matches("<NEG_PRE>|<POSS_PRE>|<NEG_POST>|<POSS_POST>|<NEG_END>"))
						break;
					else
						window.add(words[m-o]);
				}
				
				//get type of Negation
				NegationContext currentNegationContext = NegationContext.Affirmed;
				if (words[m].equals("<NEG_POST>")){
					currentNegationContext = NegationContext.Negated;
				}
				else if(words[m].equals("<POSS_POST>")) 
					currentNegationContext = NegationContext.Possible;
					
				//check if there are UMLS concepts in the window
				for(int w=0; w<window.size(); w++) {
					if(window.get(w).matches(regExUmlsTag)){
						String umlsWord = window.get(w);
						int index = Integer.parseInt(umlsWord.replaceAll("\\[|\\]",""));
						mappingResults.get(index).setTerm_exists( mapNameToTermExists( currentNegationContext ) );
					}
				}
				window.clear();
				m++;
			}
			//IF word not a negation or conjunction skip
			else{
				m++;
			}
		}
	}
	
	/**
	 * Temporality analysis
	 * @return
	 */
	public void applyTemporality(String[] words, String sectionID) throws Exception
	{

		List<String> window = new ArrayList<String>();
		
		//IF the sentence is in a past history section, then all concepts detected are historical
		if(sectionID!=null && sectionID.equals("11348-0"))
		{
			for (int i=0; i < mappingResults.size(); i++){
				mappingResults.get(i).setTerm_temporal( TemporalityContext.Historical.name() );
			}
		}
	
		//Going from one temporality term to another, and creating the appropriate window
		int mm = 0;
		while(mm<words.length)
		{
			//IF word is a pseudo-negation, skips to the next word
			if(words[mm].equals("<NEG_PSEUDO>")) mm++;
	
			//IF word is a pre-UMLS hypothetical trigger term
			else if(words[mm].equals("<HYPO_PRE>")){

				//expands window until end of sentence, termination term, or other negation/possible trigger term
				for(int o=1; (mm+o)<words.length; o++) {
					if(words[mm+o].equals("<HYPO_END>|<HYPO_EXP_END>|<HYPO_PRE>")) {
						break;//window decreased to right before other negation or conjunction
					}
					else 
						window.add(words[mm+o]);
				}
				//check if there are UMLS concepts in the window
				for(int w=0; w<window.size(); w++) {
					if(window.get(w).matches(regExUmlsTag)){
						String umlsWord = window.get(w);
						int index = Integer.parseInt(umlsWord.replaceAll("\\[|\\]",""));
						mappingResults.get(index).setTerm_temporal( TemporalityContext.Hypothetical.name() );
					}
				}
				window.clear();
				mm++;
			}
			//IF word a pre-UMLS historical trigger term
			else if(words[mm].matches("<HIST_PRE>|<TIME_PRE>")){

				//expands window until end of sentence, termination term, or other negation/possible trigger term
				for(int o=1; (mm+o)<words.length; o++) {
					if(words[mm+o].matches("<HIST_END>|<HIST_EXP_END>|<HIST_PRE>|<HIST_1W>")) {
						break;//window decreased to right after other negation or conjunction
					}
					else window.add(words[mm+o]);
				}
				//check if there are UMLS concepts in the window
				for(int w=0; w<window.size(); w++) {
					if(window.get(w).matches(regExUmlsTag)){
						String umlsWord = window.get(w);
						int index = Integer.parseInt(umlsWord.replaceAll("\\[|\\]",""));
						mappingResults.get(index).setTerm_temporal( TemporalityContext.Historical.name() );
					}
				}
				window.clear();
				mm++;
			}
			//IF word a post-UMLS historical trigger term
			else if(words[mm].equals("<TIME_POST>")){

				//expands window until end of sentence, termination term, or other negation/possible trigger term
				for(int o=1; (mm-o)>=0; o++) {
					if(words[mm-o].matches("<HIST_END>|<HIST_EXP_END>|<HIST_PRE>|<HIST_1W>")) {
						break;//window decreased to right after other negation or conjunction
					}
					else window.add(words[mm-o]);
				}
				//check if there are UMLS concepts in the window
				for(int w=0; w<window.size(); w++) {
					if(window.get(w).matches(regExUmlsTag)){
						String umlsWord = window.get(w);
						int index = Integer.parseInt(umlsWord.replaceAll("\\[|\\]",""));
						mappingResults.get(index).setTerm_temporal( TemporalityContext.Historical.name() );
					}
				}
				window.clear();
				mm++;
			}
			else mm++;
		}
	}
	
	/**
	 * Experiencer analysis
	 * @return
	 */
	public void applyExperiencer(String[] words, String sectionID) throws Exception
	{
		List<String> window = new ArrayList<String>();
		
		//Going from one experiencer term to another, and creating the appropriate window
		int mm = 0;
		while(mm<words.length){
			//IF word is a pseudo-negation, skips to the next word
			if(words[mm].equals("<NEG_PSEUDO>")) mm++;
	
			//IF word is a pre-UMLS experiencer trigger term
			else if(words[mm].equals("<EXP_PRE>"))
			{
				//expands window until end of sentence, termination term, or other negation/possible trigger term
				for(int o=1; (mm+o)<words.length; o++) {
					if(words[mm+o].equals("<EXP_END>|<HIST_EXP_END>|<HYPO_EXP_END>|<EXP_PRE>")) {
						break;//window decreased to right before other negation or conjunction
					}
					else window.add(words[mm+o]);
				}
				for(int w=0; w<window.size(); w++) {
					if(window.get(w).matches(regExUmlsTag)){
						String umlsWord = window.get(w); 
						int index = Integer.parseInt(umlsWord.replaceAll("\\[|\\]",""));
						// TODO - make this a cleaner, more robust addition that checks for
						//        previous values, doesn't add extra semi-colons, etc.
						String current_modifiers = mappingResults.get(index).getTerm_modifiers();
						if( current_modifiers.equals( "" ) ){
						    mappingResults.get(index).setTerm_modifiers( "NotPatient=true" );
						} else {
						    mappingResults.get(index).setTerm_modifiers( mappingResults.get(index).getTerm_modifiers() + ";NotPatient=true" );
						}
					}
				}
				window.clear();
				mm++;
			}
			else mm++;
		}
	}
	
	/**
	 * Log tagged sentence
	 * @param origSent
	 * @param taggedSent
	 * @throws IOException
	 */
	public void dumpContextSentence(String origSent, String taggedSent) throws IOException
	{	
	    if( mLogFile == null ){
	        return;
	    }
		if (mappingResults.size() > 0) {
			BufferedWriter fw = new BufferedWriter(new FileWriter( mLogFile , 
			                                                       true ) );
			
			fw.append("\n\n==========================================================");
			fw.append("\nOriginal sentence:\n");
			fw.append(origSent.replaceAll("\\s+", " ").trim());
			fw.append("\n--------------------------------------------------------------");
			fw.append("\nTagged sentence:\n");
			fw.append(taggedSent.replaceAll("\\s+", " ").trim());
			fw.append("\n--------------------------------------------------------------");
			fw.append("\nUMLS concepts:");
			for (int i=0;i<mappingResults.size();i++)
			{
				Note_Nlp_TableProperties map = mappingResults.get(i);
				String contextStr = "Patient, ";
				if( !map.getTerm_modifiers().contains( "NotPatient=true" ) ){
					contextStr = "Other, ";
				}
				contextStr = contextStr + map.getTerm_temporal() + ", " + map.getTerm_exists();
				fw.append("\n[" + i + "] '" + map.getCoveredText().replaceAll("\\s+", " ") + "' [" + map.getNote_nlp_source_concept_id() + "] ( " + contextStr + " )");
			}
			fw.close();
		}
	}
}
