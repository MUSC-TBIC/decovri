package edu.musc.tbic.textspans;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.musc.tbic.uima.Decovri;
import edu.musc.tbic.uima.NoteSection;

public class TemplateSectionizer extends JCasAnnotator_ImplBase {
	
	private Logger mLogger = LoggerFactory.getLogger( Decovri.class );
    
    /**
     * Name of configuration parameter that must be set to the full type description for sentences
     */
    public static final String PARAM_SECTIONTEMPLATES = "SectionTemplateFile";
    @ConfigurationParameter( name = PARAM_SECTIONTEMPLATES , 
                             description = "File containing section header templates" , 
                             mandatory = false )
    private String mSectionTemplateFile;
    private CSVParser mSectionTemplateParser;
    private TreeMap<String, String> mSectionTemplateToType;
    private TreeMap<String, Integer> mSectionTemplateToDepth;
    private TreeMap<String, String> mSectionTemplateToModifier;
    
    private Pattern mUnderline = Pattern.compile( "-+" );
	
	private int mSectionCount;
	
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);
        
        if( aContext.getConfigParameterValue( "SectionTemplateFile" ) == null ){
            mSectionTemplateFile = "resources/section_templates.tsv";
        } else {
            // TODO - add this to pipeline.properties file
            mSectionTemplateFile = (String) aContext.getConfigParameterValue( "SectionTemplateFile" );
        }
        
        mLogger.info( "Loading Excel-style TSV file: " + mSectionTemplateFile );
        mSectionTemplateToType = new TreeMap<>();
        mSectionTemplateToDepth = new TreeMap<>();
        mSectionTemplateToModifier = new TreeMap<>();
        try {
            FileReader fr = new FileReader( mSectionTemplateFile );
            mSectionTemplateParser = new CSVParser( fr , CSVFormat.EXCEL
                    .withHeader( "Template", "SectionType", "Depth", "Modifiers" )
                    .withDelimiter( '\t' )
                    .withTrim());
            for( CSVRecord csvRecord : mSectionTemplateParser ) {
                // TODO - make this more robust to malformed lines
                String template = csvRecord.get( "Template" );
                if( template.trim().equals( "" ) ){
                    continue;
                }
                String section_type = csvRecord.get( "SectionType" );
                mSectionTemplateToType.put( template , section_type );
                int depth = 0;
                if( ! csvRecord.get( "Depth" ).trim().equals( "" ) ){
                    depth = Integer.parseInt( csvRecord.get( "Depth" ) );
                }
                mSectionTemplateToDepth.put( template , depth );
                String term_modifiers = (String)csvRecord.get( "Modifiers" );
                mSectionTemplateToModifier.put( template , term_modifiers ); 
            }   
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
	
	///////////////////////////////////////////////////////
	private void matchHeader( JCas aJCas , String docText , 
			 				  String headerFormat , 
			 				  String sectionType , int sectionDepth ,
			 				  String modifiers ){
		Pattern headerPattern = Pattern.compile( headerFormat , Pattern.CASE_INSENSITIVE );
		Matcher matcher = headerPattern.matcher( docText );
		int pos = 0;
		int section_start = -1;
		int section_end = -1;
		while( matcher.find( pos ) ) {
			section_start = matcher.start();
			section_end = matcher.end();

			NoteSection annotation = new NoteSection( aJCas );
			annotation.setBegin( section_start );
			annotation.setEnd( section_end );
			annotation.setBeginHeader( section_start );
			annotation.setEndHeader( section_end );
			
			Matcher underlineMatcher = mUnderline.matcher( docText );
			if( underlineMatcher.find( section_end ) ){
				int underline_start = underlineMatcher.start();
				int underline_end = underlineMatcher.end();
				if( underline_start < section_end + 2 ){
					section_end = underline_end;
				}
			}
			annotation.setSectionId( sectionType );
			annotation.setSectionDepth( sectionDepth );
			annotation.setModifiers( modifiers );
			mSectionCount++;
			annotation.setSectionNumber( mSectionCount );
			annotation.addToIndexes();
			pos = matcher.end();
		}
	}
	
	////////////////////////////////////////
	// Default values for sectionDepth = 0 and modifiers = ""
	private void matchHeader( JCas aJCas , String docText , 
			  				  String headerFormat , 
			  				  String sectionType ){
		matchHeader( aJCas , docText , 
					 headerFormat , 
					 sectionType , 0 , "" );
	}
	
	////////////////////////////////////////
	// Default value for sectionDepth = 0
	private void matchHeader( JCas aJCas , String docText , 
			  				  String headerFormat , 
			  				  String sectionType , String modifiers ){
		matchHeader( aJCas , docText , 
					 headerFormat , 
					 sectionType , 0 , modifiers );
	}
	
	////////////////////////////////////////
	// Default value for modifiers = ""
	private void matchHeader( JCas aJCas , String docText , 
			  				  String headerFormat , 
			  				  String sectionType , int sectionDepth ){
		matchHeader( aJCas , docText , 
					 headerFormat , 
					 sectionType , sectionDepth , "" );
	}
	
	///////////////////////////////////////////////////////
	@Override
	public void process( JCas aJCas ) throws AnalysisEngineProcessException {
		// get document text from JCas
		String docText = aJCas.getDocumentText();
		
		if( docText.length() == 0 ){
			// TODO - extract NOTE_ID for metrics logging
//			mLogger.info( "Note '" + note_id + "' is empty. No sections created." );
			mLogger.info( "Note is empty. No sections created." );
			return;
		}

		mSectionCount = 0;
		
		for( Entry<String, String> entry : mSectionTemplateToType.entrySet() ){
		    String template = entry.getKey();
		    String section_type = entry.getValue();
		    int depth = mSectionTemplateToDepth.get( template );
		    String term_modifiers = mSectionTemplateToModifier.get( template );
		    matchHeader( aJCas , docText , 
		            template , 
		            section_type ,
		            depth ,
		            term_modifiers );
		}
		
		///////////////////////////////////////////////////////////////
		List<NoteSection> rawHeaders = new ArrayList<NoteSection> ();
		FSIndex<NoteSection> sect_index = aJCas.getAnnotationIndex( NoteSection.type );
        Iterator<NoteSection> sect_iter = sect_index.iterator();
        while( sect_iter.hasNext() ){
        	NoteSection this_section = (NoteSection)sect_iter.next();
        	rawHeaders.add( this_section );
        }
        NoteSection last_section = null;
        NoteSection last_subsection = null;
        for( NoteSection this_section : rawHeaders ){
        	this_section.removeFromIndexes();
        	int start_offset = this_section.getBegin();
        	int end_offset = this_section.getEnd();
        	if( last_section == null & start_offset > 1 ){
        		// TODO - convert these to debug statements
//            	System.out.println( "-- FrontMatter --\t[ 1 - " + ( start_offset - 1 ) + " ]" );
            	NoteSection annotation = new NoteSection( aJCas );
    			annotation.setBegin( 1 );
    			annotation.setEnd( ( start_offset - 1 ) );
    			annotation.setBeginHeader( -1 );
    			annotation.setEndHeader( -1 );
    			annotation.setSectionId( "Unknown/Unclassified" );
    			annotation.setSectionDepth( 0 );
    			annotation.setModifiers( "" );
    			mSectionCount++;
    			annotation.setSectionNumber( mSectionCount );
    			annotation.addToIndexes();
        	}
        	if( this_section.getSectionDepth() == 0 ){
	        	if( last_section != null ){
	        		last_section.setEnd( start_offset - 1 );
	        	}
	        	if( last_subsection != null ){
	        		last_subsection.setEnd( start_offset - 1 );
	        		last_subsection = null;
	        	}
        		// TODO - convert these to debug statements
//	        	System.out.println( this_section.getSectionId() + "\t[ " + start_offset + " - " + end_offset + " ]" );
	        	last_section = this_section;
        	} else {
	        	if( last_subsection != null ){
	        		last_subsection.setEnd( start_offset - 1 );
	        	}
        		// TODO - convert these to debug statements
//        		System.out.println( "\t" + this_section.getSectionId() + "\t[ " + start_offset + " - " + end_offset + " ]" );
	        	last_subsection = this_section;
        	}
        	this_section.addToIndexes();
        }
        ////////////////////////////////
        // TODO - find a more appropriate section id for this type
        if( mSectionCount == 0 ){
        	// TODO - extract NOTE_ID for metrics logging
//            mLogger.info( "Note '" + note_id + "' contains only back matter section" );
            mLogger.info( "Note contains only back matter section" );
        	NoteSection annotation = new NoteSection( aJCas );
			annotation.setBegin( 0 );
			annotation.setEnd( docText.length() );
			annotation.setBeginHeader( -1 );
			annotation.setEndHeader( -1 );
			annotation.setSectionId( "Unknown/Unclassified" );
			annotation.setSectionDepth( 0 );
			annotation.setModifiers( "" );
			mSectionCount++;
			annotation.setSectionNumber( mSectionCount );
			annotation.addToIndexes();
        } else {
        	// TODO - extract NOTE_ID for metrics logging
//            mLogger.info( "Note '" + note_id + "' split into " + mSectionCount + " sections" );
            mLogger.info( "Note split into " + mSectionCount + " sections" );
        }
	}
}
