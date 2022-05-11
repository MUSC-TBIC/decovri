package edu.musc.tbic.writers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.conceptMapper.UmlsTerm;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.XMLSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import edu.musc.tbic.uima.Decovri;
import edu.musc.tbic.omop_cdm.Note_TableProperties;
import edu.musc.tbic.uima.NoteSection;

//import edu.musc.tbic.omop_cdm.NoteNlp_TableProperties;

public class XmlWriter extends JCasAnnotator_ImplBase {

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
    	String modelFileName = null;
    	
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
        mLogger.debug( "Writing note_id '" + note_id + "' to disk" );
        
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
        
    	String outFileName = note_id + ".xmi";
		String tsvFileName = note_id + ".tsv";
		File outFile = new File( leafOutputDirectory , outFileName );
		File tsvFile = new File( leafOutputDirectory , tsvFileName );
//		String txtFileName = note_id + ".txt";
//		File txtFile = new File( mOutputDir, txtFileName );
		// TODO - streamline by only creating this path when needed
		File errFile = new File( mErrorDir, outFileName );
		
		// serialize XCAS and write to output file
		try {
			writeTsv( aJCas , tsvFile );
//			writeTxt( aJCas , txtFile );
			writeXmi( aJCas.getCas() , outFile, errFile , modelFileName );
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

    /**
	 * Serialize a CAS to a file in XMI format
	 *
	 * @param aCas CAS to serialize
	 * @param name output file
	 * @throws SAXException
	 * @throws Exception
	 * @throws ResourceProcessException
	 */
	private void writeXmi( CAS aCas, File name ) throws IOException, SAXException {
		FileOutputStream out = null;

		try {
			// write XMI
			out = new FileOutputStream( name );
			XmiCasSerializer ser = new XmiCasSerializer( aCas.getTypeSystem(),null, true );
			XMLSerializer xmlSer = new XMLSerializer( out, true );
			ser.serialize( aCas, xmlSer.getContentHandler() );
		} finally {
			if ( out != null ) {
				out.close();
			}
		}
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
	private void writeTsv( JCas aJCas , File name ) throws IOException, SAXException {
		ArrayList < AbstractMap.SimpleImmutableEntry <Integer,Integer> > templateSections =
        		new ArrayList < AbstractMap.SimpleImmutableEntry <Integer,Integer> >();
		ArrayList < AbstractMap.SimpleImmutableEntry <Integer,Integer> > sectionHeaders =
        		new ArrayList < AbstractMap.SimpleImmutableEntry <Integer,Integer> >();
        FSIndex<NoteSection> section_index = aJCas.getAnnotationIndex( NoteSection.type );
        Iterator<NoteSection> section_iter = section_index.iterator();   
        while ( section_iter.hasNext() ) {
        	NoteSection section_props = (NoteSection)section_iter.next();
        	int start_offset = section_props.getBegin();
    		int end_offset = section_props.getEnd();
    		if( section_props.getSectionId() == "Template" ){
        		templateSections.add( new AbstractMap.SimpleImmutableEntry<>( start_offset , end_offset ) );
        	} else if( section_props.getSectionId() == "SectionHeader" ){
        		sectionHeaders.add( new AbstractMap.SimpleImmutableEntry<>( start_offset , end_offset ) );
        	}
        }
        
        FileWriter writer = new FileWriter( name );
        
		try {
			FSIndex<UmlsTerm> term_index = aJCas.getAnnotationIndex( UmlsTerm.type );
            Iterator<UmlsTerm> term_iter = term_index.iterator();   
            while ( term_iter.hasNext() ) {
            	UmlsTerm lexical_entry = (UmlsTerm)term_iter.next();
            	int start_offset = lexical_entry.getBegin();
            	int end_offset = lexical_entry.getEnd();
            	String section_type = "Unknown";
            	// Check for matches within templatic sections
    			for( AbstractMap.SimpleImmutableEntry <Integer,Integer> this_pair : templateSections ){ 
    	            int start_section = this_pair.getKey();
    	    		int end_section = this_pair.getValue();
    	    		if( start_section <= start_offset &
    	    				end_offset <= end_section ){
    	    			section_type = "Template";
    	    			continue;
    	    		}
    	        }
    			// Check for matches within section headers
    			for( AbstractMap.SimpleImmutableEntry <Integer,Integer> this_pair : sectionHeaders ){ 
    	            int start_section = this_pair.getKey();
    	    		int end_section = this_pair.getValue();
    	    		if( start_section <= start_offset &
    	    				end_offset <= end_section ){
    	    			section_type = "SectionHeader";
    	    			continue;
    	    		}
    	        }
            	String snippet = lexical_entry.getCoveredText();
            	String lexical_variant = lexical_entry.getCoveredText();//lexical_entry.getMatchedText();
            	String concept_code = lexical_entry.getConceptCode();
            	int concept_id = -1;
            	if( Character.isDigit( concept_code.charAt( 0 ) ) ){
            		concept_id = Integer.valueOf( concept_code );
            	} else {
            		concept_id = Integer.valueOf( concept_code.substring( 1 ) );
            	}
            	String head_code = lexical_entry.getBasicLevelConceptCode();
            	int head_id = -1;
            	if( head_code == null ){
            		head_id = concept_id;
            	} else if( Character.isDigit( head_code.charAt( 0 ) ) ){
            		head_id = Integer.valueOf( head_code );
            	} else {
            		head_id = Integer.valueOf( head_code.substring( 1 ) );
            	}
	    		writer.append( section_type + "\t" + 
	    					   start_offset + "\t" + end_offset + "\t" +
	    					   snippet.replaceAll("[\\r\\n]", "\\\\n") + "\t" + 
	    					   concept_code +"\t" + head_code + "\n" );
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
	
	/**
	 * Serialize a CAS to a file in XMI format
	 *
	 * @param aCas CAS to serialize
	 * @param name output file
	 * @throws SAXException
	 * @throws AnalysisEngineProcessException 
	 * @throws Exception
	 * @throws ResourceProcessException
	 */
	private void writeXmi( CAS aCas, File name, File error_name, String modelFileName ) throws IOException, SAXException, AnalysisEngineProcessException {
		FileOutputStream out = null;
		BufferedWriter error_out = null;

		try {
			// write XMI
			out = new FileOutputStream( name );
			XmiCasSerializer ser = new XmiCasSerializer( aCas.getTypeSystem() );
			XMLSerializer xmlSer = new XMLSerializer( out, true );
			ser.serialize( aCas, xmlSer.getContentHandler() );
		} catch ( SAXParseException e ) {
			mBadDocs++;
			// TODO - this can be made much more efficient rather than repeating
			//        the extraction twice in case of error
			JCas jcas;
			try {
				jcas = aCas.getJCas();
			} catch ( CASException e1 ) {
				e1.printStackTrace();
				throw new AnalysisEngineProcessException( e1 );
			}
			String note_id = "";
			String note_source_value = "";
			FSIndex<Note_TableProperties> note_props_index = jcas.getAnnotationIndex( Note_TableProperties.type );
	        Iterator<Note_TableProperties> note_props_iter = note_props_index.iterator();   
	        while ( note_props_iter.hasNext() ) {
	        	Note_TableProperties note_props = (Note_TableProperties)note_props_iter.next();
	        	note_id = note_props.getNote_id();
//	        	note_source_value = note_props.getNote_source_value();
	        }
			mLogger.error( "SAXParseError when trying to write CAS for note_id '" + note_id + "' to file. " +
							"Check fs.error_directory for details." );
			// We only need to create the error file directory *if* we run 
			// across a bum file
			File errorDirectory = new File( mErrorDir );
			if ( !errorDirectory.exists() ) {
				errorDirectory.mkdirs();
			}
			error_out = new BufferedWriter( new FileWriter( error_name ) );
//			error_out.write( "Note Source Value:  " + note_source_value + "\n\n" );
			error_out.append( "Caught SAXParseException:  " + e + "\n\n" );
        } finally {
			if ( out != null ) {
				out.close();
			}
			if ( error_out != null ) {
				error_out.close();
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
