package edu.musc.tbic.textspans;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.musc.tbic.uima.NoteSection;
import edu.musc.tbic.textspans.TemplateSectionizer;

class SectionizerTest {

    private static String testInputDir = System.getProperty( "user.dir" ) + "/data/test/in/txt";

    // create jcas object
    private static JCas mJcas = null;
    private static ArrayList<NoteSection> mSectionAnnotations = null;
    
    // analysis engine descriptor needed for testing
    private static AnalysisEngineDescription mNoteSectionizer;

    private String medicine_001 = "Ofloxacin 200 mg p.o. q 12\r\n" + 
            "\r\n" + 
            "klonopin one tablet twice-a-day for 2 weeks\r\n" + 
            "\r\n" + 
            "After 12/5 INR at 1.5, Coumadin was set to 10mg PO qDay for 1 week, followed by new INR.\r\n" + 
            "";

    @BeforeAll
    public static void beforeAll() throws UIMAException {
        mNoteSectionizer = AnalysisEngineFactory.createEngineDescription(
                TemplateSectionizer.class );

        mJcas = JCasFactory.createJCas();
    }

    @BeforeEach
    public void beforeEach() {
        // reset all our variables before each run
        mJcas.reset();
        mSectionAnnotations = new ArrayList<NoteSection>();
    }

    public void splitIntoSections( String documentText )
            throws ResourceInitializationException, UIMAException {

        mJcas.setDocumentText( documentText );

        // Process jcas
        SimplePipeline.runPipeline( mJcas , mNoteSectionizer );

        // Collect the annotations
        FSIndex<NoteSection> section_index = mJcas.getAnnotationIndex( NoteSection.type );
        Iterator<NoteSection> section_iter = section_index.iterator();
        // Iterate while there is a next annotation and add to ArrayList
        while ( section_iter.hasNext() ) {
            NoteSection section_props = (NoteSection)section_iter.next();
            mSectionAnnotations.add( section_props );
        }
    }

    @Test
    public void testBackMatter() {
        try {
            splitIntoSections( medicine_001 );
        } catch(UIMAException e) {
            e.printStackTrace();
        }

        // Make sure we have at least one section
        assertTrue( mSectionAnnotations.size() > 0 );

        // Verify we have exactly one section
        assertEquals( 1 , mSectionAnnotations.size() );
        // And that section should be named 'Back Matter'
        assertEquals( "Unknown/Unclassified" , mSectionAnnotations.get( 0 ).getSectionId() );

    }   

    @Test
    public void test00001_sections() {
        String inputFilename = testInputDir + "/00001_sections.txt"; 

        String contents = "";
        try {
            contents = new String( Files.readAllBytes( Paths.get( inputFilename ) ) );
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } 

        try {
            splitIntoSections( contents );
        } catch(UIMAException e) {
            e.printStackTrace();
        }

        // Check Size
        assertEquals( 10 , mSectionAnnotations.size() );

        assertEquals( "Unknown/Unclassified" , mSectionAnnotations.get( 0 ).getSectionId() );
//        assertEquals( "Diagnoses" , mSectionAnnotations.get( 1 ).getSectionId() );
//        assertEquals( "Diagnoses" , mSectionAnnotations.get( 2 ).getSectionId() );

    }

    @Test
    public void test00002_sections() {
        String inputFilename = testInputDir + "/00002_sections.txt"; 

        String contents = "";
        try {
            contents = new String( Files.readAllBytes( Paths.get( inputFilename ) ) );
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } 

        try {
            splitIntoSections( contents );
        } catch(UIMAException e) {
            e.printStackTrace();
        }

        // Check Size
        assertEquals( 5 , mSectionAnnotations.size() );

        assertEquals( "Date/Time" , mSectionAnnotations.get( 0 ).getSectionId() );
        assertEquals( "Unknown/Unclassified" , mSectionAnnotations.get( 1 ).getSectionId() );
//        assertEquals( "Medications" , mSectionAnnotations.get( 2 ).getSectionId() );

    }

}
