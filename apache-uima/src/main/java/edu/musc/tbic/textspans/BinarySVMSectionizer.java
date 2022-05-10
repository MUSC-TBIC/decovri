package edu.musc.tbic.textspans;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

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

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import edu.musc.tbic.libsvm.Utils;

public class BinarySVMSectionizer extends JCasAnnotator_ImplBase {
        
    private Logger mLogger = LoggerFactory.getLogger( Decovri.class );
    
    /**
     * Name of configuration parameter that must be set to filename containing training data for a new model
     */
    public static final String PARAM_SECTIONTRAININGFILE = "SectionTrainingFile";
    @ConfigurationParameter( name = PARAM_SECTIONTRAININGFILE , 
                             description = "File containing section header training file" , 
                             mandatory = false )
    private String mSectionTrainingFile;
    
    /**
     * Name of configuration parameter that must be set to filename used for saving and loading models
     */
    public static final String PARAM_SECTIONMODEL = "SectionModelFile";
    @ConfigurationParameter( name = PARAM_SECTIONMODEL , 
                             description = "File containing section header model file" , 
                             mandatory = false )
    private String mSectionModelFile;
    private CSVParser mTrainingDataParser;
    private svm_problem mProblem;
    private svm_parameter mParams;
    private svm_model mSectionModel;
    
    private int mSectionCount;
        
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);
        
        if( aContext.getConfigParameterValue( "SectionTrainingFile" ) == null ){
        	mSectionTrainingFile = "";
        } else {
        	mSectionTrainingFile = (String) aContext.getConfigParameterValue( "SectionTrainingFile" );
        }
        
        mSectionModelFile = (String) aContext.getConfigParameterValue( "SectionModelFile" );
        if( mSectionModelFile == null ||
        	mSectionModelFile.equals( "" ) ){
        	mSectionModelFile = "resources/binary-svm-sectionizer.model";
        }
        
        // If a training file was provided, then we want to pre-train a model before running the pipeline
        if( ! mSectionTrainingFile.equals( "" ) ) {
        	mLogger.info( "Loading Excel-style TSV file with training data: " + mSectionTrainingFile );
        	mParams = new svm_parameter();
        	// default values
        	mParams.svm_type = svm_parameter.C_SVC;
        	mParams.kernel_type = svm_parameter.RBF;
        	mParams.degree = 3;
        	mParams.gamma = 0;	// 1/num_features
        	mParams.coef0 = 0;
        	mParams.nu = 0.5;
        	mParams.cache_size = 100;
        	mParams.C = 1;
        	mParams.eps = 1e-3;
        	mParams.p = 0.1;
        	mParams.shrinking = 1;
        	mParams.probability = 0;
        	mParams.nr_weight = 0;
        	mParams.weight_label = new int[0];
        	mParams.weight = new double[0];
        	try {
        		FileReader fr = new FileReader( mSectionTrainingFile );
        		mTrainingDataParser = new CSVParser( fr , CSVFormat.EXCEL
        				.withHeader( "sample_serial_number" , 
        						"uppercase" , "title_case" , 
        						"ending_colon" , 
        						"contain_verb" , 
        						"percentile" , 
        						"filename" , 
        						"idx1" , "idx2" , 
        						"content" , 
        						"isHeader" )
        				.withDelimiter( ',' )
        				.withQuote( '"' )
        				.withTrim());

        		Vector<Double> vy = new Vector<Double>();
        		Vector<svm_node[]> vx = new Vector<svm_node[]>();
        		int max_index = 0;

        		for( CSVRecord csvRecord : mTrainingDataParser ) {
        			if( csvRecord.get( "sample_serial_number" ).equals( "" ) ) {
        				continue;
        			}
        			Boolean isHeader = false;
        			if( csvRecord.get( "isHeader" ).equalsIgnoreCase( "True" ) ) {
        				isHeader = true;
        				vy.addElement( 1.0 );
        			} else {
        				vy.addElement( 0.0 );
        			}

        			int m = 5;
        			svm_node[] x = new svm_node[m];
        			// All Uppercase?
        			x[0] = new svm_node();
        			x[0].index = 1;
        			if( csvRecord.get( "uppercase" ).equalsIgnoreCase( "True" ) ) {
        				x[0].value = 1.0;
        			} else {
        				x[0].value = 0.0;
        			}
        			// all title case?
        			x[1] = new svm_node();
        			x[1].index = 2;
        			if( csvRecord.get( "title_case" ).equalsIgnoreCase( "True" ) ) {
        				x[1].value = 1.0;
        			} else {
        				x[1].value = 0.0;
        			}
        			// End in a colon?
        			x[2] = new svm_node();
        			x[2].index = 3;
        			if( csvRecord.get( "ending_colon" ).equalsIgnoreCase( "True" ) ) {
        				x[2].value = 1.0;
        			} else {
        				x[2].value = 0.0;
        			}
        			// Does it contain a verb?
        			x[3] = new svm_node();
        			x[3].index = 4;
        			if( csvRecord.get( "contain_verb" ).equalsIgnoreCase( "True" ) ) {
        				x[3].value = 1.0;
        			} else {
        				x[3].value = 0.0;
        			}
        			// Percentile
        			x[4] = new svm_node();
        			x[4].index = 5;
        			try {
        				x[4].value = Utils.atof( csvRecord.get( "percentile" ) );
        			} catch ( NumberFormatException e) {
        				// TODO Auto-generated catch block
        				System.out.println( csvRecord.get( "percentile" ) );
        				e.printStackTrace();
        			}

        			if( m>0 ) {
        				max_index = Math.max(max_index, x[m-1].index);
        			}
        			vx.addElement(x);
        		}

        		mProblem = new svm_problem();
        		mProblem.l = vy.size();
        		mProblem.x = new svm_node[mProblem.l][];
        		for( int i=0 ; i < mProblem.l ; i++ ) {
        			mProblem.x[i] = vx.elementAt(i);
        		}
        		mProblem.y = new double[mProblem.l];
        		for( int i=0 ; i < mProblem.l ; i++ ) {
        			mProblem.y[i] = vy.elementAt(i);
        		}	

        	} catch (FileNotFoundException e1) {
        		// TODO Auto-generated catch block
        		e1.printStackTrace();
        	} catch (IOException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}

        	String error_msg = svm.svm_check_parameter( mProblem , mParams );

        	if( error_msg != null ){
        		System.err.print("ERROR: "+error_msg+"\n");
        		System.exit(1);
        	}

        	//svm_train.do_cross_validation( mProblem , mParams );

        	mSectionModel = svm.svm_train( mProblem , mParams );
        	try {
        		svm.svm_save_model( mSectionModelFile , mSectionModel );
        	} catch (IOException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
        }
        
        // We always work off of the model on disk to make sure there is no airgap 
        // between processing and reproducible runs
		try {
			mSectionModel = svm.svm_load_model( mSectionModelFile );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
        
    ///////////////////////////////////////////////////////
    
        
    ///////////////////////////////////////////////////////
    @Override
    public void process( JCas aJCas ) throws AnalysisEngineProcessException {
        // get document text from JCas
        String docText = aJCas.getDocumentText();
                
        if( docText.length() == 0 ){
            // TODO - extract NOTE_ID for metrics logging
            //                      mLogger.info( "Note '" + note_id + "' is empty. No sections created." );
            mLogger.info( "Note is empty. No sections created." );
            return;
        }

        mSectionCount = 0;
        
        // TODO : Iterate over every sentence and make a prediction
                
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
                //              System.out.println( "-- FrontMatter --\t[ 1 - " + ( start_offset - 1 ) + " ]" );
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
                //                      System.out.println( this_section.getSectionId() + "\t[ " + start_offset + " - " + end_offset + " ]" );
                last_section = this_section;
            } else {
                if( last_subsection != null ){
                    last_subsection.setEnd( start_offset - 1 );
                }
                // TODO - convert these to debug statements
                //                      System.out.println( "\t" + this_section.getSectionId() + "\t[ " + start_offset + " - " + end_offset + " ]" );
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
