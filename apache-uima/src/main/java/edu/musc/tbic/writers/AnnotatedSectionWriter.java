package edu.musc.tbic.writers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.apache.ctakes.typesystem.type.syntax.BaseToken;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.conceptMapper.UmlsTerm;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties;
import edu.musc.tbic.omop_cdm.Note_TableProperties;
import edu.musc.tbic.uima.Decovri;
import edu.musc.tbic.uima.NoteSection;

public class AnnotatedSectionWriter extends JCasAnnotator_ImplBase {

	private static final Logger mLogger = LoggerFactory.getLogger( Decovri.class );

	/**
	 * Name of configuration parameter that must be set to the path of a directory into which the
	 * output files will be written.
	 */
	public static final String PARAM_OUTPUTDIR = "OutputDirectory";
	@ConfigurationParameter( name = PARAM_OUTPUTDIR, 
			description = "Output directory to write annotated section files", 
			mandatory = true)
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
	@ConfigurationParameter( name = PARAM_ERRORDIR, 
			description = "Output directory to write broken files", 
			mandatory = true)
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
			if( note_id.endsWith( ".txt" ) ||
					note_id.endsWith( ".xmi" ) ){
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
		
		String outTxtName = note_id + ".txt";
		String outCsvName = note_id + ".csv";
		File txtFile = new File( leafOutputDirectory , outTxtName );
		File csvFile = new File( leafOutputDirectory , outCsvName );
		// TODO - streamline by only creating this path when needed
		//              File errFile = new File( mErrorDir, outFileName );

		// serialize XCAS and write to output file
		try {
			writeAnnotatedText( aJCas , txtFile , csvFile );
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
	 * Extract the concepts of interest and write them to a disk
	 * in a tab-delimited format
	 *
	 * @param aCas CAS to serialize
	 * @param name output file
	 * @throws SAXException
	 * @throws Exception
	 * @throws ResourceProcessException
	 */
	private void writeAnnotatedText( JCas aJCas , File txtName , File csvName ) throws IOException, SAXException {
		FileWriter txtWriter = null;
		FileWriter csvWriter = null;
		String note_text = aJCas.getDocumentText();

		String divider = "|";

		FSIndex<NoteSection> section_index = aJCas.getAnnotationIndex( NoteSection.type );
		Iterator<NoteSection> section_iter = section_index.iterator();   
		try{
			txtWriter = new FileWriter( txtName );
			csvWriter = new FileWriter( csvName );
			while ( section_iter.hasNext() ) {
				NoteSection section_props = (NoteSection)section_iter.next();
				int start_offset = section_props.getBegin();
				int end_offset = section_props.getEnd();
				int start_header_offset = section_props.getBeginHeader();
				int end_header_offset = section_props.getEndHeader();
				// The LEXICAL_VARIANT is just the section header, if one exists
				// If there is no section header, then the lexical variant is
				// empty.
				String lexical_variant = "";
				if( start_header_offset != -1 ){
					lexical_variant = note_text.substring( start_header_offset , end_header_offset );
				} else {
					lexical_variant = "";
				}
				String section_id = section_props.getSectionId();
				//            String concept_code = mSectionConceptCodes.getOrDefault( section_props.getSectionId() ,
				//                                                                     "0" );
				// TODO - convert these to "S0001" format
				//            int source_concept_id = Integer.valueOf( concept_code ) - 2000003000;
				String section_modifiers = section_props.getModifiers();
				////
				FSIterator<?> concepts = aJCas.getAnnotationIndex(UmlsTerm.type).subiterator( section_props );      
				int i = 0;
				txtWriter.append( String.format( "%5s" , String.valueOf( start_offset ) ) + " - " +
						String.format( "%5s" , String.valueOf( end_offset ) ) + " " + divider +
						String.format( "%30s" , section_id ) + " " + divider + "" +
						String.format( "%30s" , lexical_variant ) + " " + divider + "" +
						String.format( "%30s" , section_modifiers ) + " " + divider + "" +
						//                           String.format( "%25s" , subsection ) + " " + divider + " " +
						//                           line + 
						"\n" );
				while( concepts.hasNext() ) {          
					UmlsTerm annot = (UmlsTerm) concepts.next();                    
					txtWriter.append( "\t" + annot.getCoveredText() + "\t" + annot.getConceptCode() + "\n" );
					i++; 
				}
				////
				csvWriter.append( String.valueOf( start_offset ) + "\t" +
						String.valueOf( end_offset ) + "\t" +
						section_id + "\t" +
						lexical_variant + "\t" +
						section_modifiers + "\t" +
						"\n" );
			}

		} finally {
			if ( txtWriter != null ) {
				txtWriter.flush();
				txtWriter.close();
			}
			if ( csvWriter != null ) {
				csvWriter.flush();
				csvWriter.close();
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
