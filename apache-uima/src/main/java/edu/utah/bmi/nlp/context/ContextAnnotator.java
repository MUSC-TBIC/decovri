package edu.utah.bmi.nlp.context;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.musc.tbic.uima.Decovri;
import edu.utah.bmi.nlp.context.ConText;
import edu.musc.tbic.uima.NoteSection;
import org.apache.ctakes.typesystem.type.textspan.Sentence;
import org.apache.uima.conceptMapper.UmlsTerm;
import edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties;

public class ContextAnnotator extends JCasAnnotator_ImplBase {

    private Logger mLogger = LoggerFactory.getLogger( Decovri.class );
    
	private ConText _contextAnalyzer;
    
    /**
     * Name of configuration parameter that must be set to the full type description for sentences
     */
    public static final String PARAM_SENTENCETYPE = "SentenceType";
    @ConfigurationParameter( name = PARAM_SENTENCETYPE , 
                             description = "Full type description for sentences" , 
                             mandatory = false )
    private String mSentenceType;
    
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
		
		try
		{
			//initialize sets of semantic types to distinct drugs and diseases
			mLogger.debug("Loading ConText Analyzer");
			
			//instantiate new context analyzer
			_contextAnalyzer = ConText.getAnalyzer(aContext);
			
			mLogger.debug("ConText Analyzer loaded");
			
		}
		catch (Exception ace){
			throw new ResourceInitializationException(ace);
		}
	}
	
	private void processSection( JCas cas , 
	        String currentSectionId ,
	        int lastSectionBegin , 
	        int nextSectionBegin ) throws Exception{
	    // Go through all the sentences using select since subiterator doesn't play nicely with uimaFIT
	    // cf. https://issues.apache.org/jira/browse/CTAKES-16
	    Collection<Sentence> sentences = JCasUtil.select( cas , Sentence.class );

        ArrayList<Note_Nlp_TableProperties> concepts = new ArrayList<Note_Nlp_TableProperties>();

	    int sentenceCount = 0;
	    int conceptCount = 0;
	    for( Sentence current_sentence : sentences ){
	        if( current_sentence.getBegin() < lastSectionBegin || 
	                current_sentence.getBegin() >= nextSectionBegin ) {
	            continue;
	        }
	        sentenceCount++;

	        // Go through all the concepts using select since subiterator doesn't play nicely with uimaFIT
	        // cf. https://issues.apache.org/jira/browse/CTAKES-16
	        Collection<UmlsTerm> original_concepts = JCasUtil.select( cas , UmlsTerm.class );
	        for( UmlsTerm original_concept : original_concepts ){
	            if( original_concept.getBegin() < current_sentence.getBegin() || 
	                    original_concept.getEnd() >= current_sentence.getEnd() ) {
	                continue;
	            }
	            conceptCount++;
	            Note_Nlp_TableProperties concept = new Note_Nlp_TableProperties( cas );
	            concept.setBegin( original_concept.getBegin() );
	            concept.setEnd( original_concept.getEnd() );
	            concept.setOffset( String.valueOf( original_concept.getBegin() ) );
	            concept.setLexical_variant( original_concept.getCoveredText() );
	            // TODO - add section details
	            // TODO - should we just force UmlsTerm
	            concept.setNote_nlp_source_concept_id( original_concept.getConceptCode() );
	            // TODO - pull the snippet from here with the sentence start/end as the maximal spans
                concept.setSnippet( "" );
	            // Default to exists
	            concept.setTerm_exists( "y" );
	            // Initialize temporal and modifiers to empty
	            concept.setTerm_temporal( "" );
	            concept.setTerm_modifiers( "" );
	            // TODO - pass NLP System details through
	            //concept.setNlp_system( mNlpSystem );
	            concepts.add(concept);
	            concept.addToIndexes();
	        }
	        //analyze context for concepts in the current sentence
	        _contextAnalyzer.applyContext( concepts ,
	                current_sentence.getCoveredText() , 
	                currentSectionId );

	        //move to next sentence
	        concepts.clear();
	    }
//        mLogger.info( "\tSentences: " + String.valueOf( sentenceCount ) );
//        mLogger.info( "\tConcepts:  " + String.valueOf( conceptCount ) );
	}

	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException
	{
		try{
			// Go through all the sections
			Collection<NoteSection> sections = JCasUtil.select( cas , NoteSection.class );
//			mLogger.info( "Sections found: " + String.valueOf( sections.size() ) );
			int lastSectionBegin = -1;
			String lastSectionId = "Unknown/Unclassified";
			for( NoteSection current_section : sections ){
			    // TODO - verify that JCasUtil.select is guaranteed to return in sorted order
			    if( lastSectionBegin == -1 ){
			        lastSectionBegin = current_section.getBegin();
			        continue;
			    }
			    processSection( cas , 
			            lastSectionId , 
			            lastSectionBegin , 
			            current_section.getBegin() );
                lastSectionBegin = current_section.getBegin();
                lastSectionId = current_section.getSectionId();
			}
            processSection( cas , 
                    lastSectionId , 
                    lastSectionBegin , 
                    -1 );
		} catch(Exception e){
			throw new AnalysisEngineProcessException(e);
		}
	}
}
