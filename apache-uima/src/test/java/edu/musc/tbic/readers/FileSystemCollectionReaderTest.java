package edu.musc.tbic.readers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties;
import edu.musc.tbic.readers.FileSystemCollectionReader;
import edu.musc.tbic.textspans.TemplateSectionizer;

class FileSystemCollectionReaderTest {

    private static String testInputDir = System.getProperty( "user.dir" ) + "/data/test/in/txt";

    // create jcas object
    private static JCas mJcas = null;
    private static ArrayList<Note_Nlp_TableProperties> mAnnotations = null;
    
    // analysis engine descriptor needed for testing
    private static AnalysisEngineDescription fileSystemCollectionReader;

    @BeforeAll
    public static void beforeAll() throws UIMAException {
        //fileSystemCollectionReader = AnalysisEngineFactory.createEngineDescription(
        //        FileSystemCollectionReader.class ,
        //        FileSystemCollectionReader.PARAM_VERSION , "vUnitTest" );

        mJcas = JCasFactory.createJCas();
    }

    @BeforeEach
    public void beforeEach() {
        // reset all our variables before each run
        mJcas.reset();
        mAnnotations = new ArrayList<Note_Nlp_TableProperties>();
    }

    public void fileSystemCollectionReaderTest( String documentText )
            throws ResourceInitializationException, UIMAException {

        mJcas.setDocumentText( documentText );

        // Process jcas
        //SimplePipeline.runPipeline( mJcas , fileSystemCollectionReader );

        // Collect the annotations
        Collection<Note_Nlp_TableProperties> annot_collection = JCasUtil.select( mJcas , Note_Nlp_TableProperties.class );
        for( Note_Nlp_TableProperties annot : annot_collection ){
            mAnnotations.add( annot );
        }
    }

    @Test
    public void test0000() {
        String inputFilename = testInputDir + "/00003_demographics.txt"; 

        String contents = "";
        try {
            contents = new String( Files.readAllBytes( Paths.get( inputFilename ) ) );
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } 

        try {
        	fileSystemCollectionReaderTest( contents );
        } catch(UIMAException e) {
            e.printStackTrace();
        }

        // Check Size
        assertEquals( 0 , mAnnotations.size() );

    }

}
