package edu.musc.tbic.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.conceptMapper.UmlsTerm;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.musc.tbic.uima.Decovri;
import edu.utah.bmi.nlp.core.SimpleParser;
import edu.utah.bmi.nlp.core.Span;
import edu.utah.bmi.nlp.fastcontext.FastContext;

import org.apache.ctakes.typesystem.type.textspan.Sentence;
import edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties;

public class FastContextAnnotator extends JCasAnnotator_ImplBase {

    private static final Logger mLogger = LoggerFactory.getLogger( Decovri.class );

    /**
     * Name of configuration parameter that must be set to the full type description for sentences
     */
    public static final String PARAM_SENTENCETYPE = "SentenceType";
    @ConfigurationParameter( name = PARAM_SENTENCETYPE , 
                             description = "Full type description for sentences" , 
                             mandatory = false )
    private String mSentenceType;

    /**
     * Name of configuration parameter that points to the context rule file
     * Available via:
     *   https://github.com/jianlins/FastContext/blob/master/conf/context.txt
     */
    public static final String PARAM_RULEFILE = "RuleFile";
    @ConfigurationParameter( name = PARAM_RULEFILE , 
                             description = "Context rule file" , 
                             mandatory = false )
    private String mRuleFile;

    FastContext fcEngine;

    /**
     * Initialization before processing the CAS (load parameters from the configuration file)
     */
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);

        if( aContext.getConfigParameterValue( "SentenceType" ) == null ){
            mSentenceType = "org.apache.ctakes.typesystem.type.textspan.Sentence";
        } else {
            mSentenceType = (String) aContext.getConfigParameterValue( "SentenceType" );
        }

        if( aContext.getConfigParameterValue( "RuleFile" ) == null ){
            mRuleFile = "resources/FastContext_rules.txt";
        } else {
            mRuleFile = (String) aContext.getConfigParameterValue( "RuleFile" );
        }

        fcEngine = new FastContext( mRuleFile , false );

    }

    @Override
    public void process(JCas cas) throws AnalysisEngineProcessException
    {
        try{
            // Go through all the sentences using select since subiterator doesn't play nicely with uimaFIT
            // cf. https://issues.apache.org/jira/browse/CTAKES-16
            Collection<Sentence> sentences = JCasUtil.select( cas , Sentence.class );

            int sentenceCount = 0;
            int conceptCount = 0;
            for( Sentence current_sentence : sentences ){
                sentenceCount++;
                String sentenceText = current_sentence.getCoveredText();
                ArrayList<Span> sentenceTokenSpans = SimpleParser.tokenizeOnWhitespaces( sentenceText );
                // TODO - It's only worth building this map if there is a concept in this sentence
                HashMap<Integer, Integer> offset2TokenMap = null;

                // Go through all the concepts using select since subiterator doesn't play nicely with uimaFIT
                // cf. https://issues.apache.org/jira/browse/CTAKES-16
                Collection<UmlsTerm> original_concepts = JCasUtil.select( cas , UmlsTerm.class );
                for( UmlsTerm original_concept : original_concepts ){
                    if( original_concept.getBegin() < current_sentence.getBegin() || 
                            original_concept.getEnd() >= current_sentence.getEnd() ) {
                        continue;
                    }
                    // TODO - It's only worth building this map if there is a concept in this sentence
                    if( offset2TokenMap == null ){
                        offset2TokenMap = new HashMap<>();
                        int tokenId = 0;
                        for( Span tokenSpan : sentenceTokenSpans ){
                            for( int i = tokenSpan.getBegin() ;
                                    i <= tokenSpan.getEnd() ; 
                                    i++ ){
                                offset2TokenMap.put( i ,  tokenId );
                            }
                            tokenId++;
                        }
                    }
                    int conceptRelativeBegin = original_concept.getBegin() - current_sentence.getBegin();
                    int conceptRelativeEnd = original_concept.getEnd() - current_sentence.getBegin();
                    int conceptTokenBegin = offset2TokenMap.get( conceptRelativeBegin );
                    int conceptTokenEnd = offset2TokenMap.get( conceptRelativeEnd );
                    conceptCount++;
                    Note_Nlp_TableProperties concept = new Note_Nlp_TableProperties( cas );
                    concept.setBegin( original_concept.getBegin() );
                    concept.setEnd( original_concept.getEnd() );
                    concept.setOffset( String.valueOf( original_concept.getBegin() ) );
                    concept.setLexical_variant( original_concept.getCoveredText() );
                    // TODO - add section details
                    // TODO - add OntologyConceptID iterator
                    String basic_level_concept_code = original_concept.getBasicLevelConceptCode();
                    if( basic_level_concept_code == null ||
                        basic_level_concept_code.equals( "" ) ){
                        basic_level_concept_code = original_concept.getConceptCode();
                    }
                    concept.setNote_nlp_source_concept_id( original_concept.getConceptCode() );
                    // TODO - pull the snippet from here with the sentence start/end as the maximal spans
                    concept.setSnippet( "" );
                    // Default to exists
                    concept.setTerm_exists( "y" );
                    // Initialize temporal and modifiers to empty
                    concept.setTerm_temporal( "" );
                    if( basic_level_concept_code == null ||
                        basic_level_concept_code.equals( "" ) ){
                        concept.setTerm_modifiers( "basicLevelConcept=" + 
                                                   original_concept.getConceptCode() );
                    } else {
                        concept.setTerm_modifiers( "basicLevelConcept=" + 
                                                   basic_level_concept_code );
                    }
                    // TODO - pass NLP System details through
                    //concept.setNlp_system( mNlpSystem );
                    ////
                    HashMap<String, String> contextAttributes = new HashMap<>();
                    boolean errorFlag = false;
                    try{
                        setConTxt( "neg" , "hyp" , "exp" , "his" , 
                                sentenceText , sentenceTokenSpans , 
                                conceptTokenBegin , conceptTokenEnd , 
                                contextAttributes );
                    } catch( Exception e ){
                        errorFlag = true;
                        mLogger.warn( "NullPointerException. Treating as default: " + 
                                       conceptTokenBegin + " - " + conceptTokenEnd );
                    }
                    ////
                    String conditionalValue = "false";
                    String genericValue = "false";
                    String historicalValue = "0";
                    String polarityValue = "1";
                    String subjectValue = "patient";
                    String uncertaintyValue = "0";
                    // @FEATURE_VALUES|Negation|affirm|negated
                    // @FEATURE_VALUES|Certainty|certain|uncertain
                    // @FEATURE_VALUES|Temporality|present|historical|hypothetical
                    // @FEATURE_VALUES|Experiencer|patient|nonpatient
                    // No feature values impact "conditional" or "generic"
                    // - conditionalValue = "true"
                    // - genericValue = "true"
                    String predAttr = "present";
                    if( contextAttributes.get( "exp" ).equalsIgnoreCase( "nonpatient" ) ){
                        subjectValue = "not patient";
                        predAttr = "not_patient";
                    } else if( contextAttributes.get( "hyp" ).equalsIgnoreCase( "uncertain" ) ){
                        uncertaintyValue = "1";
                        predAttr = "uncertain";
                    } else if( contextAttributes.get( "neg" ).equalsIgnoreCase( "negated" ) ){
                        polarityValue = "-1";
                        predAttr = "negated";
                    } else if( contextAttributes.get( "his" ).equalsIgnoreCase( "hypothetical" ) ){
                        historicalValue = "1";
                        predAttr = "hypothetical";
                    } else if( contextAttributes.get( "his" ).equalsIgnoreCase( "historical" ) ){
                        historicalValue = "-1";
                    }
                    // Update the term_exists flag to match
                    if( conditionalValue.equals( "false" ) &&
                            genericValue.equals( "false" ) &&
                            historicalValue.equals( "0" ) &&
                            polarityValue.equals( "1" ) &&
                            subjectValue.equals( "patient" ) &&
                            uncertaintyValue.equals( "0" ) ){
                        concept.setTerm_exists( "y" );
                    } else {
                        concept.setTerm_exists( "n" );
                    }
                    // String all the modifier values together with a semicolon
                    String termModifiers = String.join( ";" ,
                            "basicLevelConcept=" + basic_level_concept_code ,
                            "conditional=" + conditionalValue ,
                            "generic=" + genericValue ,
                            "historical=" + historicalValue ,
                            "polarity=" + polarityValue ,
                            "subject=" + subjectValue ,
                            "uncertainty=" + uncertaintyValue );
                    concept.setTerm_modifiers( termModifiers );
                    ////
                    concept.addToIndexes();
                }
            }
        } catch(Exception e){
            throw new AnalysisEngineProcessException(e);
        }
    }

    /* */
    public void setConTxt( String lblNegated , String lblHypothetical , 
            String lblExperiencer , String lblHistorical , 
            String sentenceText , ArrayList<Span> sentenceTokenSpans , 
            int conceptTokenBegin , int conceptTokenEnd , 
            HashMap<String, String> contextAttributes ) {

        String negValue = "affirm";
        String hypValue = "certain";
        String expValue = "patient";
        String hisValue = "present";
        // Initialize everything to its default value in case something goes wrong
        contextAttributes.put( lblNegated , negValue );
        contextAttributes.put( lblHypothetical , hypValue );
        contextAttributes.put( lblExperiencer , expValue );
        contextAttributes.put( lblHistorical , hisValue );
        
        ArrayList<String> res = fcEngine.processContext( sentenceTokenSpans , 
                conceptTokenBegin , conceptTokenEnd , 
                sentenceText , 
                30 );

        for (String re : res) {
            if (re.equalsIgnoreCase("negated")) {
                negValue = "negated";
            }
            if (re.equalsIgnoreCase("nonpatient")) {
                expValue = "nonpatient";
            }
            if (re.equalsIgnoreCase("uncertain")) {
                hypValue = "uncertain";
            }
            if (re.equalsIgnoreCase("historical")) {
                hisValue = "historical";
            }
            if (re.equalsIgnoreCase("hypothetical")) {
                hisValue = "hypothetical";
            }
        }

        contextAttributes.put( lblNegated , negValue );
        contextAttributes.put( lblHypothetical , hypValue );
        contextAttributes.put( lblExperiencer , expValue );
        contextAttributes.put( lblHistorical , hisValue );
    }

    public void destroy() {
    }

}
