package edu.musc.tbic.uima;

import edu.musc.tbic.uima.Decovri;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties;
import edu.musc.tbic.omop_cdm.Note_TableProperties;

public class PatientDemographics extends JCasAnnotator_ImplBase {

	public static final String PARAM_VERSION = "Version";
	@ConfigurationParameter( name = PARAM_VERSION , 
							 description = "NLP system version number needed for logging" , 
							 mandatory = true )
	private String mVersion;
	private String mNlpSystem;
	
	 private Logger mLogger = LoggerFactory.getLogger( Decovri.class );
		
	  /**
	   * Initialize.
	   *
	   * @throws ResourceInitializationException the resource initialization exception
	   * @see org.apache.uima.collection.CollectionReader_ImplBase#initialize()
	   */
	 public void initialize(UimaContext uimaContext) throws ResourceInitializationException {
		 mVersion = (String)(uimaContext.getConfigParameterValue( PARAM_VERSION ));
		 mNlpSystem = "Decovri " + mVersion;
	 }
	
	///////////////////////////////////////////////////////
	 private void matchPosNeg( JCas aJCas , 
			 String note_id ,
			 String docText , 
			 String positive_string , 
			 String negative_string , 
			 String positive_concept_code ){
		 matchPosNeg( aJCas , 
				 note_id ,
				 docText , 
				 positive_string , 
				 negative_string , 
				 positive_concept_code ,
				 true ,
				 "" );
	 }
	 ///////////////////////////
	 private void matchPosNeg( JCas aJCas , 
			 String note_id ,
			 String docText , 
			 String positive_string , 
			 String negative_string , 
			 String positive_concept_code ,
			 String term_modifiers ){
		 matchPosNeg( aJCas , 
				 note_id ,
				 docText , 
				 positive_string , 
				 negative_string , 
				 positive_concept_code ,
				 true ,
				 term_modifiers );
	 }
	 ///////////////////////////
	 private void matchPosNeg( JCas aJCas , 
			 String note_id ,
			 String docText , 
			 String positive_string , 
			 String negative_string , 
			 String positive_concept_code ,
			 Boolean autosplit_flag ){
		 matchPosNeg( aJCas , 
				 note_id ,
				 docText , 
				 positive_string , 
				 negative_string , 
				 positive_concept_code ,
				 autosplit_flag ,
				 "" );
	 }
	 ///////////////////////////
	 private void matchPosNeg( JCas aJCas , 
			String note_id ,
			String docText , 
			String positive_string , 
			String negative_string , 
			String positive_concept_code ,
			Boolean autosplit_flag ,
			String term_modifiers ){
		if( autosplit_flag ){
			positive_string = String.join( "\\s+" , positive_string.split( " " ) );
			negative_string = String.join( "\\s+" , negative_string.split( " " ) );
		}
		Pattern templatePattern = Pattern.compile( positive_string , Pattern.CASE_INSENSITIVE );
		Matcher matcher = templatePattern.matcher( docText );
		int pos = 0;
		int start_positive = -1;
		int end_positive = -1;
		if( matcher.find( pos ) ) {
			start_positive = matcher.start();
			end_positive = matcher.end();
		}
		///////////
		templatePattern = Pattern.compile( negative_string , Pattern.CASE_INSENSITIVE );
		matcher = templatePattern.matcher( docText );
		pos = 0;
		int start_negative = -1;
		int end_negative = -1;
		if( matcher.find( pos ) ) {
			start_negative = matcher.start();
			end_negative = matcher.end();
		}
		///////////
		if( start_positive > 0 && start_negative > 0 ){
			// Huh, both are true.
			// TODO - troubleshoot these classes
			mLogger.warn( "Positive and negative evidence found for concept C" + positive_concept_code );
		} else if( start_positive > 0 && start_negative == -1 ){
			// The SNIPPET as the 20 characters +/- the section header
    		int start_snippet = Math.max( start_positive - 20 , 1 );
    		int end_snippet = Math.min( end_positive + 20 ,  docText.length() );
    		String section_snippet = docText.substring( start_snippet , end_snippet );    		
			addNoteNlpRow( aJCas , note_id ,
			               String.valueOf( start_positive ) ,
						   section_snippet ,
						   docText.substring( start_positive , end_positive ) ,
						   positive_concept_code ,
						   "y" ,
						   term_modifiers );
		} else if( start_positive == -1 && start_negative > 0 ){
			// The SNIPPET as the 20 characters +/- the section header
    		int start_snippet = Math.max( start_negative - 20 , 1 );
    		int end_snippet = Math.min( end_negative + 20 ,  docText.length() );
    		String section_snippet = docText.substring( start_snippet , end_snippet );    		
			addNoteNlpRow( aJCas , note_id ,
					       String.valueOf( start_negative ) ,
						   section_snippet ,
						   docText.substring( start_negative , end_negative ) ,
						   positive_concept_code ,
						   "n" ,
						   term_modifiers );
		}
	}
	
	///////////////////////////////////////////////////////
	private void matchPositionalExtractor( JCas aJCas , 
			String note_id ,
			String docText , 
			String concept_prefix , 
			String concept_pattern ){
		matchPositionalExtractor( aJCas , 
				note_id ,
				docText , 
				concept_prefix , 
				concept_pattern ,
				true );
	}
	///////////////////////////////
	private void matchPositionalExtractor( JCas aJCas , 
				String note_id ,
				String docText , 
				String concept_prefix , 
				String concept_pattern ,
				Boolean autosplit_flag ){
		if( autosplit_flag ){
			concept_pattern = String.join( "\\s+" , concept_pattern.split( " " ) );
		}
		Pattern templatePattern = Pattern.compile( concept_pattern , Pattern.CASE_INSENSITIVE );
		Matcher matcher = templatePattern.matcher( docText );
		int pos = 0;
		int start_offset = -1;
        int start_snippet = -1;
		int end_snippet = -1;
		while( matcher.find() ) {
		    start_offset = matcher.start( 1 );
		    start_snippet = matcher.start();
		    end_snippet = matcher.end();
            String snippet = docText.substring( start_snippet , end_snippet );
			String lexical_variant = matcher.group( 1 );
			String concept_code = "0";
			if( concept_prefix.equals( "Patient Gender" ) ){
				if( lexical_variant.equalsIgnoreCase( "female" ) || 
						lexical_variant.equalsIgnoreCase( "woman" ) ){
					concept_code = "0086287";
				} else if( lexical_variant.equalsIgnoreCase( "male" ) || 
						lexical_variant.equalsIgnoreCase( "man" ) ){
					concept_code = "0086582";
				} else if( lexical_variant.equalsIgnoreCase( "intersex" ) ){
					concept_code = "1704620";
				} else if( lexical_variant.equalsIgnoreCase( "transsexual" ) ){
					concept_code = "0558141";
				} else if( lexical_variant.equalsIgnoreCase( "indeterminate sex" ) ){
					concept_code = "0278457";
				}
            } else if( concept_prefix.equals( "Patient Age" ) ){
                // https://uts.nlm.nih.gov/uts/umls/concept/C0001779
                concept_code = "0001779";
            } else if( concept_prefix.equals( "Patient Height" ) ){
                concept_code = "0005890";
            } else if( concept_prefix.equals( "Patient Weight" ) ){
                concept_code = "0005910";
            }
			addNoteNlpRow( aJCas , note_id ,
					String.valueOf( start_offset ) ,
					snippet ,
					lexical_variant ,
					concept_code ,
					"" );
		}
	}
	
	///////////////////////////////////////////////////////
	private void addNoteNlpRow( JCas aJCas ,
			String note_id ,
			String start_offset ,
			String snippet ,
			String lexical_variant ,
			String source_concept ,
			String term_exists ){
		addNoteNlpRow( aJCas ,
				note_id ,
				start_offset ,
				snippet ,
				lexical_variant ,
				source_concept ,
				term_exists ,
				"" );
	}

	////////////////////////
	private void addNoteNlpRow( JCas aJCas ,
			String note_id ,
			String start_offset ,
			String snippet ,
			String lexical_variant ,
			String source_concept ,
			String term_exists ,
			String term_modifiers ){
		if( lexical_variant == null || lexical_variant.trim().equals( "" ) ){
			return;
		}
		Note_Nlp_TableProperties annotation;
		// NOTE_NLP_ID should be determined at time of database write
		annotation = new Note_Nlp_TableProperties( aJCas );
		annotation.setNote_id( note_id );
		// Patient current history
		// TODO - look this up rather than use a hard-coded value
		annotation.setSection_concept_id( 2000003007 );
		// set the SNIPPET to the prompt this value is a response to
		annotation.setSnippet( snippet );
		annotation.setOffset( start_offset );
		annotation.setLexical_variant( lexical_variant );
		if( ! start_offset.equals( "0" ) ){
		    int begin_offset = Integer.valueOf( start_offset );
		    annotation.setBegin( begin_offset );
		    annotation.setEnd( begin_offset + lexical_variant.length() );
		}
		annotation.setNote_nlp_concept_id( 0 );
		annotation.setNote_nlp_source_concept_id( source_concept );
		annotation.setNlp_system( mNlpSystem );
		// NLP_DATE
		// NLP_DATETIME
		if( lexical_variant.equalsIgnoreCase( "yes" ) ){
			annotation.setTerm_exists( "y" );
		} else if( lexical_variant.equalsIgnoreCase( "no" ) ){
			annotation.setTerm_exists( "n" );
		} else {
			annotation.setTerm_exists( term_exists );
		}
		annotation.setTerm_temporal( "" );
		annotation.setTerm_modifiers( term_modifiers );
		annotation.addToIndexes();
	}
	
	///////////////////////////////////////////////////////
	@Override
	public void process( JCas aJCas ) throws AnalysisEngineProcessException {
		String note_id = "";
		
        FSIndex<Note_TableProperties> note_props_index = aJCas.getAnnotationIndex( Note_TableProperties.type );
        Iterator<Note_TableProperties> note_props_iter = note_props_index.iterator();
        if ( note_props_iter.hasNext() ) {
        	Note_TableProperties note_props = (Note_TableProperties)note_props_iter.next();
            note_id = note_props.getNote_id();
        } else {
        	// TODO - this should be guaranteed to already exist in a Note_TableProperties
        	//        annotation by all collection readers so we don't have to patch
        	//        every CAS Consumer the same way.
        	FSIterator<?> it = aJCas.getAnnotationIndex(SourceDocumentInformation.type).iterator();
    		if( it.hasNext() ){
    			SourceDocumentInformation fileLoc = (SourceDocumentInformation) it.next();
    			note_id = fileLoc.getUri().toString();
    		}
    		if( note_id.endsWith( ".txt" ) ){
    			note_id = note_id.substring( 0 , note_id.length() - 4 );
    		}
        }
		
		// get document text from JCas
		String docText = aJCas.getDocumentText();
		
		///////////////////////////////////////////////////////////////
		matchPosNeg( aJCas , note_id , docText , 
				"smokes or uses smokeless tobacco." , 
				"does not smoke or use smokeless tobacco" ,
				// Tobacco use disorder, Smoker
				// TODO - fix C prefix on this CUI when concept table is fixed
				"0040336" );
		matchPosNeg( aJCas , note_id , docText , 
				"is pregnant" , 
				"denies pregnancy" ,
				// Pregnant
				// TODO - fix C prefix on this CUI when concept table is fixed
				"0549206" );
		matchPosNeg( aJCas , note_id , docText , 
				"is a healthcare worker or works in a healthcare facility" ,
				"is not a healthcare worker and does not work in a healthcare facility" ,
				// Pregnant
				// TODO - fix C prefix on this CUI when concept table is fixed
				"0018724" );
		// Travel
		matchPosNeg( aJCas , note_id , docText , 
				"has traveled internationally or to the areas where COVID-19 \\(Coronavirus\\) is widespread" ,
				"(" + "has not traveled internationally or to the areas where COVID-19 \\(Coronavirus\\) is widespread" +
				"|" + "has not traveled internationally in the last 14 days before the start of his symptoms" + ")" ,
				// Pregnant
				// TODO - fix C prefix on this CUI when concept table is fixed
				"0" ,
				"exposureRisk=travel" );
		// Contact with confirmed patient
		matchPosNeg( aJCas , note_id , docText , 
				"has had( a)? close contact with a( suspected or)? laboratory( |-)confirmed( positive)? COVID-19 patient within 14 days of symptom onset" ,
				"has not had( a)? close contact with a( suspected or)? laboratory( |-)confirmed( positive)? COVID-19 patient within 14 days of symptom onset" ,
				// Pregnant
				// TODO - fix C prefix on this CUI when concept table is fixed
				"0" ,
				"exposureRisk=laboratory-confirmed contact" );
		// Contact with suspected patient
		matchPosNeg( aJCas , note_id , docText , 
				"has had( a)? close contact with a suspected COVID-19 patient within 14 days of symptom onset" ,
				"has not had( a)? close contact with a suspected COVID-19 patient within 14 days of symptom onset" ,
				// Pregnant
				// TODO - fix C prefix on this CUI when concept table is fixed
				"0" ,
				"exposureRisk=suspected contact" );
		
		///////////////////////////////////////////////////////////////
		// TODO - should we treat these differently with real snippets?
		matchPositionalExtractor( aJCas , note_id , docText ,
                "Patient Age" ,
                "([0-9]+)[ -](year-old|yo|y.o.)" ,
                false );
        matchPositionalExtractor( aJCas , note_id , docText ,
				"Patient Weight" ,
				"Weight: ([0-9]+ lbs)" ,
				false );
        matchPositionalExtractor( aJCas , note_id , docText ,
				"Patient Height" ,
				"Height: +([0-9]+ ft [0-9]+ in)" ,
				false );
		matchPositionalExtractor( aJCas , note_id , docText , 
				"Patient Gender" ,
				"\\s([a-zA-Z]+) who initiated a virtual visit" );
        matchPositionalExtractor( aJCas , note_id , docText , 
                "Patient Gender" ,
                "\\s(female|gentleman|lady|male|man|woman) (?!who)" );
	}
}
