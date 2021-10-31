package edu.musc.tbic.uima;

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
import edu.musc.tbic.uima.PatientDemographics;
import edu.musc.tbic.textspans.TemplateSectionizer;

class PatientDemographicsTest {

    private static String testInputDir = System.getProperty( "user.dir" ) + "/data/test/in/txt";

    // create jcas object
    private static JCas mJcas = null;
    private static ArrayList<Note_Nlp_TableProperties> mAnnotations = null;
    
    // analysis engine descriptor needed for testing
    private static AnalysisEngineDescription patientDemographics;

    @BeforeAll
    public static void beforeAll() throws UIMAException {
        patientDemographics = AnalysisEngineFactory.createEngineDescription(
                PatientDemographics.class ,
                PatientDemographics.PARAM_VERSION , "vUnitTest" );

        mJcas = JCasFactory.createJCas();
    }

    @BeforeEach
    public void beforeEach() {
        // reset all our variables before each run
        mJcas.reset();
        mAnnotations = new ArrayList<Note_Nlp_TableProperties>();
    }

    public void tagDemographics( String documentText )
            throws ResourceInitializationException, UIMAException {

        mJcas.setDocumentText( documentText );

        // Process jcas
        SimplePipeline.runPipeline( mJcas , patientDemographics );

        // Collect the annotations
        Collection<Note_Nlp_TableProperties> annot_collection = JCasUtil.select( mJcas , Note_Nlp_TableProperties.class );
        for( Note_Nlp_TableProperties annot : annot_collection ){
            mAnnotations.add( annot );
        }
    }

    @Test
    public void test00003_demographics() {
        String inputFilename = testInputDir + "/00003_demographics.txt"; 

        String contents = "";
        try {
            contents = new String( Files.readAllBytes( Paths.get( inputFilename ) ) );
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } 

        try {
            tagDemographics( contents );
        } catch(UIMAException e) {
            e.printStackTrace();
        }

        // Check Size
        assertEquals( 3 , mAnnotations.size() );

        // Age = C0001779
        assertEquals( "0001779" , mAnnotations.get( 0 ).getNote_nlp_source_concept_id() );
        assertEquals( "64" , mAnnotations.get( 0 ).getOffset() );
        // Gender female = C0086287
        assertEquals( "0086287" , mAnnotations.get( 1 ).getNote_nlp_source_concept_id() );
        assertEquals( "76" , mAnnotations.get( 1 ).getOffset() );
        // Patient height = C0005890
        assertEquals( "0005890" , mAnnotations.get( 2 ).getNote_nlp_source_concept_id() );
        assertEquals( "195" , mAnnotations.get( 2 ).getOffset() );

    }

}
