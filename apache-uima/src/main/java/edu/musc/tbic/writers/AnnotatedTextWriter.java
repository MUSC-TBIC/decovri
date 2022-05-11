package edu.musc.tbic.writers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import edu.musc.tbic.uima.Decovri;
import edu.musc.tbic.omop_cdm.Note_TableProperties;
import edu.musc.tbic.uima.NoteSection;

public class AnnotatedTextWriter extends JCasAnnotator_ImplBase {

	private static final Logger mLogger = LoggerFactory.getLogger( Decovri.class );
	
	/**
	 * Name of configuration parameter that must be set to the path of a directory into which the
	 * output files will be written.
	 */
	public static final String PARAM_OUTPUTDIR = "OutputDirectory";
	@ConfigurationParameter(name = PARAM_OUTPUTDIR, description = "Output directory to write xmi files", mandatory = true)
	private String mOutputDir;

	/**
	 * Name of configuration parameter that must be set to the depth of directory/directories into which the
	 * output files will be written.
	 */
	public static final String PARAM_OUTPUTDEPTH = "OutputDepth";
	@ConfigurationParameter( name = PARAM_OUTPUTDEPTH, 
			description = "Output directory depth to write annotated section files (0 means all in one folder; 2 means two levels deep)", 
			mandatory = false)
	private int mOutputDepth;
	
	/**
	 * Name of configuration parameter that must be set to the path of a directory into which the
	 * output files containing some sort of unrecoverable error will be written. These files are
	 * to be considered outside the pipeline due to some deep parsing, processing, or writing
	 * failure and can be manually reviewed at a later date.
	 */
	public static final String PARAM_ERRORDIR = "ErrorDirectory";
	@ConfigurationParameter(name = PARAM_ERRORDIR, description = "Output directory to write broken files", mandatory = true)
	private String mErrorDir;

	private int mTotalDocs;
	private int mGoodDocs;
	private int mBadDocs;
    
    public void initialize( UimaContext context ) throws ResourceInitializationException {
    	mTotalDocs = 0;
		mGoodDocs = 0;
		mBadDocs = 0;
		
		mOutputDir = (String) context.getConfigParameterValue( "OutputDirectory" );
		File outputDirectory = new File( mOutputDir );
		if ( !outputDirectory.exists() ) {
			outputDirectory.mkdirs();
		}
		if( context.getConfigParameterValue( "OutputDepth" ) == null ){
			mOutputDepth = 0;
		} else {
			mOutputDepth = (int) context.getConfigParameterValue( "OutputDepth" );
		}
		mErrorDir = (String) context.getConfigParameterValue( "ErrorDirectory" );
		
    }

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
    	mTotalDocs++;
    	
		String note_id = "";
		FSIndex<Note_TableProperties> note_props_index = aJCas.getAnnotationIndex( Note_TableProperties.type );
        Iterator<Note_TableProperties> note_props_iter = note_props_index.iterator();   
        if( note_props_iter.hasNext() ) {
        	Note_TableProperties note_props = (Note_TableProperties)note_props_iter.next();
            note_id = note_props.getNote_id();
        } else {
        	FSIterator<?> it = aJCas.getAnnotationIndex(SourceDocumentInformation.type).iterator();
    		if( it.hasNext() ){
    			SourceDocumentInformation fileLoc = (SourceDocumentInformation) it.next();
    			note_id = fileLoc.getUri().toString();
    		}
    		if( note_id.endsWith( ".txt" ) ){
    			note_id = note_id.substring( 0 , note_id.length() - 4 );
    		}
        }
        
        String leafOutputDirectory = mOutputDir;
		// TODO - make this flexible enough to handle arbitrary depths
		if( mOutputDepth == 2 ) {
			String shallowFolder = note_id.substring( note_id.length() - 2 , note_id.length() );
			String deepFolder = note_id.substring( note_id.length() - 4 , note_id.length() - 2 );
			File shallowOutputDirectory = new File( mOutputDir + "/" + shallowFolder );
			if ( !shallowOutputDirectory.exists() ) {
				shallowOutputDirectory.mkdirs();
			}
			File deepOutputDirectory = new File( mOutputDir + "/" + shallowFolder + "/" + deepFolder );
			if ( !deepOutputDirectory.exists() ) {
				deepOutputDirectory.mkdirs();
			}
			leafOutputDirectory = deepOutputDirectory.toString();
		}
		
    	String outFileName = note_id + ".txt";
		File txtFile = new File( leafOutputDirectory , outFileName );
		// TODO - streamline by only creating this path when needed
//		File errFile = new File( mErrorDir, outFileName );
		
		// serialize XCAS and write to output file
		try {
			writeAnnotatedText( aJCas , txtFile );
			mGoodDocs++;
		} catch ( IOException e ) {
			mBadDocs++;
			throw new AnalysisEngineProcessException( e );
		} catch ( SAXParseException e ) {
			mBadDocs++;
            throw new AnalysisEngineProcessException( e );
        } catch ( SAXException e ) {
			mBadDocs++;
            throw new AnalysisEngineProcessException( e );
        }
    }

	private String containingSection( JCas aJCas , int start_offset , int end_offset ){
		String section = "Front Matter";
		
		FSIndex<NoteSection> sect_index = aJCas.getAnnotationIndex( NoteSection.type );
        Iterator<NoteSection> sect_iter = sect_index.iterator();
        while( sect_iter.hasNext() ){
        	NoteSection this_section = (NoteSection)sect_iter.next();
        	if( this_section.getSectionDepth() > 0 ){
        		continue;
        	}
        	int start_sect_offset = this_section.getBegin();
        	int end_sect_offset = this_section.getEnd();
        	if( start_sect_offset <= start_offset &
        		end_sect_offset >= end_offset ){
        		section = this_section.getSectionId();
        		break;
        	} else if( start_sect_offset <= start_offset &
        			   start_offset < end_sect_offset &
        			   end_sect_offset < end_offset ){
                section = this_section.getSectionId() + ", etc.";
                break;
        	} else {
        		section = "Back Matter";
        	}
		}
		return section;
	}
	
	private String containingSubsection( JCas aJCas , int start_offset , int end_offset ){
		String subsection = "";
		
		FSIndex<NoteSection> sect_index = aJCas.getAnnotationIndex( NoteSection.type );
        Iterator<NoteSection> sect_iter = sect_index.iterator();
        while( sect_iter.hasNext() ){
        	NoteSection this_section = (NoteSection)sect_iter.next();
        	if( this_section.getSectionDepth() == 0 ){
        		continue;
        	}
        	int start_sect_offset = this_section.getBegin();
        	int end_sect_offset = this_section.getEnd();
        	if( start_sect_offset <= start_offset &
        		end_offset <= end_sect_offset ){
            		if( subsection == "" ){
            			subsection = this_section.getSectionId();
            		} else {
            			subsection = subsection + ", etc.";
                        break;
            		}
        	} else if( start_offset <= start_sect_offset &
        	    start_sect_offset <= end_offset ){
        		if( subsection == "" ){
        			subsection = this_section.getSectionId();
        		} else {
        			subsection = subsection + ", etc.";
                    break;
        		}
        	} else if( start_sect_offset >= end_offset ){
        		break;
        	}
		}
        
		return subsection;
	}
	
	private String skipSection( JCas aJCas , int start_offset , int end_offset ){
		FSIndex<NoteSection> sect_index = aJCas.getAnnotationIndex( NoteSection.type );
        Iterator<NoteSection> sect_iter = sect_index.iterator();
        while( sect_iter.hasNext() ){
        	NoteSection this_section = (NoteSection)sect_iter.next();
        	int start_sect_offset = this_section.getBegin();
        	int end_sect_offset = this_section.getEnd();
        	// Line is fully within margin of section span
        	if( start_sect_offset <= start_offset &
        		end_offset <= end_sect_offset ){
        		if( this_section.getModifiers() != null &&
        		        this_section.getModifiers().contains( "skip=true" ) ){
        			if( this_section.getSectionDepth() == 0 ){
                		return "X";
                	} else {
                		return "x";
                	}
        		} else {
        			return "|";
        		}
        	} else if( ( start_offset <= start_sect_offset &
        			     start_sect_offset <= end_offset ) |
        			   ( start_offset <= end_sect_offset &
        			     end_sect_offset <= end_offset ) ){
        		if( this_section.getModifiers() != null &&
                        this_section.getModifiers().contains( "skip=true" ) ){
        			return "x";
        		} else {
        			return "|";
        		}
        	}
		}
        
		return "|";
	}
	
	/**
	 * Extract the concepts of interest and write them to a disk
	 * in a tab-delimited format
	 *
	 * @param aCas CAS to serialize
	 * @param name output file
	 * @throws SAXException
	 * @throws Exception
	 * @throws ResourceProcessException
	 */
	private void writeAnnotatedText( JCas aJCas , File name ) throws IOException, SAXException {
		FileWriter writer = new FileWriter( name );
		String docText = aJCas.getDocumentText();
		int start_offset = 0;
		int end_offset = 0;
		try {
			String last_section = "";
			String last_subsection = "";
			for( String line : docText.split( "  " ) ){
				end_offset = start_offset + line.length();
				////////////////
				String section = containingSection( aJCas , start_offset , end_offset );
				String subsection = containingSubsection( aJCas , start_offset , end_offset );
				String divider = "|";
				if( section.equals( "Template" ) |
					section.equals( "Administration" ) ){
					divider = "X";
				} else if( subsection.equals( "Template" ) ){
					divider = "x";
				} else {
					divider = skipSection( aJCas , start_offset , end_offset );
				}
				////////////////
				if( section == last_section ){
					section = "";
				} else {
					last_section = section;
				}
				if( subsection == last_subsection ){
					subsection = "";
				} else {
					last_subsection = subsection;
				}
				writer.append( String.format( "%5s" , String.valueOf( start_offset ) ) + " - " +
							   String.format( "%5s" , String.valueOf( end_offset ) ) + " " + divider +
							   String.format( "%30s" , section ) + " " + divider + "" +
							   String.format( "%25s" , subsection ) + " " + divider + " " +
						   	   line + "\n" );
				start_offset = end_offset + 2;
			}
		} finally {
			if ( writer != null ) {
				writer.flush();
				writer.close();
			}
		}
	}

    /**
	 * Extract just the text content of a note and write it to disk
	 *
	 * @param aCas CAS to serialize
	 * @param name output file
	 * @throws SAXException
	 * @throws Exception
	 * @throws ResourceProcessException
	 */
	private void writeTxt( JCas aJCas , File name ) throws IOException, SAXException {
		FileWriter writer = new FileWriter( name );
		String docText = aJCas.getDocumentText();
		try {
			writer.append( docText + "\n" );
		} finally {
			if ( writer != null ) {
				writer.flush();
				writer.close();
			}
		}
	}

	public void close() throws IOException {
		// TODO - why isn't this run?
		mLogger.info( "Total Notes to Write = " + String.valueOf( mTotalDocs ) + 
					  " , Successful Notes = " + String.valueOf( mGoodDocs ) + 
					  " , Broken Notes = " + String.valueOf( mBadDocs ) );
	}
    
}
