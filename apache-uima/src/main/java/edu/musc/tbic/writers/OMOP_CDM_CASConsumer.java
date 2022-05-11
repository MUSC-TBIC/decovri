package edu.musc.tbic.writers;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.conceptMapper.UmlsTerm;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.musc.tbic.uima.Decovri;
import edu.musc.covid.type.Dosage;
import edu.musc.covid.type.Drug;
import edu.musc.covid.type.Duration;
import edu.musc.covid.type.Frequency;
import edu.musc.covid.type.LabName;
import edu.musc.covid.type.LabValue;
import edu.musc.covid.type.Route;
import edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties;
import edu.musc.tbic.omop_cdm.Note_TableProperties;
import edu.musc.tbic.uima.NoteSection;

public class OMOP_CDM_CASConsumer extends JCasAnnotator_ImplBase {
        
    private Logger mLogger = LoggerFactory.getLogger( Decovri.class );
        
    public static final String PARAM_VERSION = "Version";
    @ConfigurationParameter( name = PARAM_VERSION , 
                             description = "NLP system version number needed for logging" , 
                             mandatory = true )
    private String mVersion;
    private String mNlpSystem;
    
    public static final String PARAM_AUTOINCREMENT = "AutoIncrement";
    @ConfigurationParameter( name = PARAM_AUTOINCREMENT , 
                             description = "Let the database determine the NOTE_NLP/NOTE_SEGMENT unique key (Default to true)" , 
                             mandatory = false )
    private Boolean mAutoIncrement;
    
    public static final String PARAM_DBCONNECTION = "DatabaseConnection";
    @ConfigurationParameter( name = PARAM_DBCONNECTION , 
                             description = "Properties file to pull database connection properties from" , 
                             mandatory = false )
    private Properties mProperties;
        
    public static final String PARAM_WRITETODISK = "WriteToDisk";
    @ConfigurationParameter( name = PARAM_WRITETODISK , 
                             description = "Write to disk rather than the database (Used for testing)" , 
                             mandatory = true )
    private Boolean mWriteToDisk;
    private String mNoteNlpFilename;
        
    private String mUsername;
    private String mPassword;
    private String mDbms;
    private String mServer;
    private String mDatabase;
    private String mSchema;     
        
    private TreeMap<String, String> mSectionConceptCodes;
        
    private Connection mCon;
        
    private int mNoteCount;
    private int mFailedNotes;
    private int mNoteSections;
    private int mSectionlessNotes;
    private int mNoteExtractions;
    private int mExtractionlessNotes;
        
    private ArrayList<Note_Nlp_TableProperties> mFilteredAnnotations;
        
    public void initialize( UimaContext context ) throws ResourceInitializationException {
        mVersion = (String)context.getConfigParameterValue( PARAM_VERSION );
        // mVersion is pulled from the pom.xml project.version value
        mNlpSystem = "Decovri " + mVersion;

        // TODO - add autoincrement to the db_connection.properties file
        if( context.getConfigParameterValue( PARAM_AUTOINCREMENT ) == null ){
            mAutoIncrement = true;
        } else {
            mAutoIncrement = (Boolean)context.getConfigParameterValue( PARAM_AUTOINCREMENT );
        }
        mWriteToDisk = (Boolean)context.getConfigParameterValue( PARAM_WRITETODISK );
                
        mProperties = new Properties();
        try {
            String database_connection_filename = (String)context.getConfigParameterValue( PARAM_DBCONNECTION );
            if( database_connection_filename == null ){
                database_connection_filename = "database_connection.properties";
            }
            mProperties.load( Decovri.class.getClassLoader().getResourceAsStream( database_connection_filename ) );
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        mServer = mProperties.getProperty( "db.host" );
        if( mProperties.getProperty( "db.port" ) != null ){
            mServer += ":" + mProperties.getProperty( "db.port" );
        }
        mUsername = mProperties.getProperty( "db.username" );
        mPassword = mProperties.getProperty( "db.password" );
        mDbms = mProperties.getProperty( "dbms" );
        mDatabase = mProperties.getProperty( "db.database" );
        mSchema = mProperties.getProperty( "db.note_schema" );
                
        mCon = null;
                
        if( mWriteToDisk ){
            if( mProperties.getProperty( "fs.out.note_nlp_file" ) != null ){
                mNoteNlpFilename = mProperties.getProperty( "fs.out.note_nlp_file" );
            } else {
                mNoteNlpFilename = "/tmp/note_nlp.tsv";
            }
            try( CSVPrinter printer = new CSVPrinter( new FileWriter( mNoteNlpFilename ) , 
                                                      CSVFormat.EXCEL ) ) {
                printer.printRecord( "note_nlp_id" ,
                                     "note_id" ,
                                     "section_concept_id" ,
                                     "snippet" ,
                                     "nlp_system" ,
                                     "offset" ,
                                     "lexical_variant" ,
                                     "concept_id" ,
                                     "source_concept_id" ,
                                     "term_exists" ,
                                     "term_modifiers" );
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            mLogger.debug( "NOTE_NLP local file (" + mNoteNlpFilename + ") initialized" );
        } else {
            try {
                mLogger.info( "Connecting to " + mDbms + " server at " + mServer + " for NOTE_NLP insertions" );

                if( mDbms.equalsIgnoreCase( "oracle" ) ){
                    Class.forName( "oracle.jdbc.driver.OracleDriver" );
                    mCon = DriverManager.getConnection( "jdbc:oracle:thin:" +
                                                        mUsername + "/" + mPassword + 
                                                        "@" + mServer + ":" + mDatabase );
                } else if( mDbms.equalsIgnoreCase( "sqlserver" ) ){
                    Class.forName( "net.sourceforge.jtds.jdbc.Driver" );
                    mCon = DriverManager.getConnection( "jdbc:jtds:sqlserver:" +
                                                        "//" + mServer +
                                                        "/" + mDatabase + 
                                                        ";authenticationScheme=nativeAuthentication" +
                                                        // TODO - make this connection config more configurable
                                                        ";domain=CLINLAN;useNTLMv2=true" +
                                                        ";integratedSecurity=false" ,
                                                        mUsername , 
                                                        mPassword );
                } else {
                    mLogger.error( "Unrecognized DBMS type:  '" + mDbms + "'" );
                }
            } catch ( ClassNotFoundException e ) {
                e.printStackTrace(); 
            } catch ( NullPointerException e ) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            mLogger.debug( "NOTE_NLP connection initialized" );
        }
                
        // 
        mNoteCount = 0;
        mFailedNotes = 0;
        mNoteSections = 0;
        mSectionlessNotes = 0;
        mNoteExtractions = 0;
        mExtractionlessNotes = 0;
                
        // TODO - load in the concept codes on-the-fly rather than use
        //        this hard-coded list
        mSectionConceptCodes = new TreeMap<>();
        mSectionConceptCodes.put( "No matching concept" , "0" );
        mSectionConceptCodes.put( "Front Matter" , "2000003001" );
        mSectionConceptCodes.put( "Template" , "2000003002" );
        mSectionConceptCodes.put( "Date/Time" , "2000003003" );
        mSectionConceptCodes.put( "Providers" , "2000003004" );
        mSectionConceptCodes.put( "Patient information" , "2000003005" );
        mSectionConceptCodes.put( "Administration" , "2000003006" );
        mSectionConceptCodes.put( "Patient current history" , "2000003007" );
        mSectionConceptCodes.put( "Prompt" , "2000003008" );
        mSectionConceptCodes.put( "Medication history" , "2000003009" );
        mSectionConceptCodes.put( "Allergies" , "2000003010" );
        mSectionConceptCodes.put( "Follow-up/Instructions" , "2000003011" );
        mSectionConceptCodes.put( "Medication prescriptions" , "2000003012" );
        mSectionConceptCodes.put( "Diagnoses" , "2000003013" );
        mSectionConceptCodes.put( "Back Matter" , "2000003014" );
    }
        
    ////////////////////////
    private void addNoteNlpRow( JCas aJCas ,
                                String note_id ,
                                String section_concept_id ,
                                String start_offset ,
                                String snippet ,
                                String lexical_variant ,
                                String concept_id ,
                                String source_concept ,
                                String term_exists ,
                                String term_modifiers ){
        if( lexical_variant == null || lexical_variant.trim().equals( "" ) ){
            return;
        }
        int begin_offset = Integer.valueOf( start_offset );
        for( int i = 0 ; i < mFilteredAnnotations.size() ; i++ ){
            Note_Nlp_TableProperties annotation = mFilteredAnnotations.get( i );
            if( annotation.getBegin() == begin_offset ){
                return;
            }
        }
        Note_Nlp_TableProperties annotation;
        // NOTE_NLP_ID should be determined at time of database write
        annotation = new Note_Nlp_TableProperties( aJCas );
        annotation.setNote_id( note_id );
        // Patient current history
        // TODO - look this up rather than use a hard-coded value
        annotation.setSection_concept_id( Integer.valueOf( section_concept_id ) );
        // set the SNIPPET to the prompt this value is a response to
        annotation.setSnippet( snippet );
        annotation.setOffset( start_offset );
        annotation.setLexical_variant( lexical_variant );
        if( ! start_offset.equals( "0" ) ){
            annotation.setBegin( begin_offset );
            annotation.setEnd( begin_offset + lexical_variant.length() );
        }
        annotation.setNote_nlp_concept_id( Integer.valueOf( concept_id ) );
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
        mFilteredAnnotations.add( annotation );
    }
        
    public void insertNoteNlpRow( JCas aJCas ,
                                  String concept_table ,
                                  int note_nlp_id , int note_id , 
                                  int section_concept_id ,
                                  String nlp_system ,
                                  String snippet , 
                                  String offset ,
                                  String lexical_variant ,
                                  int concept_id ,
                                  int source_concept_id ,
                                  String term_exists ,
                                  String term_modifiers ){
        PreparedStatement prepStmt = null;
        ResultSet rs = null;
        if( lexical_variant.length() > 250 ){
            lexical_variant = lexical_variant.substring( 0 , 249 );
            if( term_modifiers.equals( "" ) ){
                term_modifiers = "lexical_variant_truncated=true";
            } else {
                term_modifiers = term_modifiers + ";lexical_variant_truncated=true";
            }
        }
        if( mWriteToDisk ){
            try( CSVPrinter printer = new CSVPrinter( new FileWriter( mNoteNlpFilename ,
                                                                      true ) , 
                                                      CSVFormat.EXCEL ) ) {
                // TODO - we may need to add guardrails here to make sure all values are
                //        present and of the expected type
                // TODO - write this as TSV:  allow specifying filename or directory?
                printer.printRecord( note_nlp_id ,
                                     note_id ,
                                     section_concept_id ,
                                     snippet.replaceAll("[\\r\\n]", "\\\\n") ,
                                     nlp_system ,
                                     offset ,
                                     lexical_variant.replaceAll("[\\r\\n]+", "\\\\n") ,
                                     concept_id ,
                                     source_concept_id ,
                                     term_exists ,
                                     term_modifiers );
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                if( mAutoIncrement ){
                    prepStmt = mCon.prepareStatement( "INSERT INTO " + mSchema + "." + concept_table
                                                      + " ( "
                                                      + "note_id , "
                                                      + "section_concept_id , "
                                                      + "nlp_system , "
                                                      + "nlp_date , nlp_datetime ,"
                                                      + "snippet , " 
                                                      + "offset , "
                                                      + "lexical_variant , "
                                                      + "note_nlp_concept_id , "
                                                      + "note_nlp_source_concept_id , "
                                                      + "term_exists , "
                                                      + "term_modifiers ) "
                                                      + "VALUES ( ? , ? , ? , "
                                                      + "CURRENT_TIMESTAMP , CURRENT_TIMESTAMP , "
                                                      + "? , ? , ? , ? , ? , ? , ? )" );
                    prepStmt.setInt(     1 , note_id );
                    prepStmt.setInt(     2 , section_concept_id );
                    prepStmt.setString(  3 , nlp_system  );
                    // nlp_date
                    // nlp_datetime
                    prepStmt.setString(  4 , snippet );
                    prepStmt.setString(  5 , offset );
                    prepStmt.setString(  6 , lexical_variant );
                    prepStmt.setInt(     7 , concept_id );
                    prepStmt.setInt(     8 , source_concept_id );
                    prepStmt.setString(  9 , term_exists );
                    prepStmt.setString( 10 , term_modifiers );
                } else {                
                    prepStmt = mCon.prepareStatement( "INSERT INTO " + mSchema + "." + concept_table 
                                                      + " ( "
                                                      + "note_nlp_id , "
                                                      + "note_id , "
                                                      + "section_concept_id , "
                                                      + "nlp_system , "
                                                      + "nlp_date , nlp_datetime ,"
                                                      + "snippet , " 
                                                      + "offset , "
                                                      + "lexical_variant , "
                                                      + "note_nlp_concept_id , "
                                                      + "note_nlp_source_concept_id , "
                                                      + "term_exists , "
                                                      + "term_modifiers ) "
                                                      + "VALUES ( ? , ? , ? , ? , "
                                                      + "CURRENT_TIMESTAMP , CURRENT_TIMESTAMP , "
                                                      + "? , ? , ? , ? , ? , ? , ? )" );
                    prepStmt.setInt(     1 , note_nlp_id );
                    prepStmt.setInt(     2 , note_id );
                    prepStmt.setInt(     3 , section_concept_id );
                    prepStmt.setString(  4 , nlp_system  );
                    // nlp_date
                    // nlp_datetime
                    prepStmt.setString(  5 , snippet );
                    prepStmt.setString(  6 , offset );
                    prepStmt.setString(  7 , lexical_variant );
                    prepStmt.setInt(     8 , concept_id );
                    prepStmt.setInt(     9 , source_concept_id );
                    prepStmt.setString( 10 , term_exists );
                    prepStmt.setString( 11 , term_modifiers );
                }
                prepStmt.executeUpdate();
            } catch ( NullPointerException e ) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if( rs != null ) {
                        rs.close();
                    }
                    if( prepStmt != null ) {
                        prepStmt.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
        
    public void insertTerm( JCas aJCas ,
                            int note_nlp_id , int note_id , 
                            int section_concept_id ,
                            String nlp_system ,
                            String snippet , 
                            String offset ,
                            String lexical_variant ,
                            int concept_id ,
                            int source_concept_id ,
                            String term_exists ,
                            String term_modifiers ){
        if( snippet.length() > 250 ){
            snippet = snippet.substring( 0 , 249 );
            if( term_modifiers.equals( "" ) ){
                term_modifiers = "snippet_truncated=true";
            } else {
                term_modifiers = term_modifiers + ";snippet_truncated=true";
            }
        }
        insertNoteNlpRow( aJCas ,
                          "NOTE_NLP" ,
                          note_nlp_id , note_id , 
                          section_concept_id ,
                          nlp_system ,
                          snippet , 
                          offset ,
                          lexical_variant ,
                          concept_id ,
                          source_concept_id ,
                          term_exists ,
                          term_modifiers );
    }
        
    public void insertSection( JCas aJCas ,
                               int note_nlp_id , int note_id , 
                               int section_concept_id ,
                               String nlp_system ,
                               String snippet , 
                               String offset ,
                               String lexical_variant ,
                               int concept_id ,
                               int source_concept_id ,
                               String term_modifiers ){
        // TODO - skipping this until we can guarantee that a NOTE_SEGMENTs table exists
        //              insertNoteNlpRow( aJCas ,
        //                                "NOTE_SEGMENTS" ,
        //                                                note_nlp_id , note_id , 
        //                                                section_concept_id ,
        //                                                nlp_system ,
        //                                                snippet , 
        //                                                offset ,
        //                                                lexical_variant ,
        //                                                concept_id ,
        //                                                source_concept_id ,
        //                                                "" , // term_exists isn't obviously meaningful
        //                                                term_modifiers );
    }
        
    private int getMaxId( String concept_table ){
        Statement stmt = null;
        ResultSet rs = null;
        int max_id = 0;
        if( mWriteToDisk ){
            max_id++;
        } else {
            try {
                stmt = mCon.createStatement();
                // Get the next row id
                rs = stmt.executeQuery( "SELECT COALESCE( MAX( NOTE_NLP_ID ) , 0 ) FROM " + mSchema + "." + concept_table );
                rs.next();
                max_id = rs.getInt( 1 );
            } catch ( NullPointerException e ) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if( rs != null ) {
                        rs.close();
                    }
                    if( stmt != null ) {
                        stmt.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return max_id;
    }
        
    @Override
    public void process( JCas aJCas ) throws AnalysisEngineProcessException {
        mNoteCount++;
        // Assume we fail in processing this note unless we make it to the
        // end of this function
        mFailedNotes++;
                
        if( mWriteToDisk ){
            mFilteredAnnotations = new ArrayList<Note_Nlp_TableProperties>();
        }
                
        String note_id = "";
        String note_text = aJCas.getDocumentText();
                
        FSIndex<Note_TableProperties> note_props_index = aJCas.getAnnotationIndex( Note_TableProperties.type );
        Iterator<Note_TableProperties> note_props_iter = note_props_index.iterator();
        if ( note_props_iter.hasNext() ) {
            Note_TableProperties note_props = (Note_TableProperties)note_props_iter.next();
            note_id = note_props.getNote_id();
        } else {
            // TODO - this should be guaranteed to already exist in a Note_TaleProperties
            //        annotation by all collection readers so we don't have to patch
            //        every CAS Consumer the same way.
            FSIterator<?> it = aJCas.getAnnotationIndex(SourceDocumentInformation.type).iterator();
            if( it.hasNext() ){
                SourceDocumentInformation fileLoc = (SourceDocumentInformation) it.next();
                note_id = fileLoc.getUri().toString();
            }
            if( note_id.endsWith( ".txt" ) ){
                // Strip off the file ending
                note_id = note_id.substring( 0 , note_id.length() - 4 );
                int id_prefix = 0;
                while( id_prefix < note_id.length() &&
                       Character.isDigit( note_id.charAt( id_prefix ) ) ){
                    id_prefix++;
                }
                if( id_prefix == 0 ){
                    mLogger.warn( "Unable to extract a note_id from the note URI (" + note_id + "). Using '123456789' as a replacement." );
                    note_id = "123456789";
                } else {
                    note_id = note_id.substring( 0 , id_prefix );
                }
            }
        }
        
        int note_nlp_id = -1;
        int note_segments_id = -1;
        if( ! mAutoIncrement ){
            note_nlp_id = getMaxId( "NOTE_NLP" );
            //          note_segments_id = getMaxId( "NOTE_SEGMENTS" );
        }
        int segments_added = 0;
        int terms_added = 0;

        ArrayList < AbstractMap.SimpleImmutableEntry <Integer,Integer> > skipSections =
            new ArrayList < AbstractMap.SimpleImmutableEntry <Integer,Integer> >();
        ArrayList < AbstractMap.SimpleImmutableEntry <Integer,Integer> > sectionHeaders =
            new ArrayList < AbstractMap.SimpleImmutableEntry <Integer,Integer> >();
        ArrayList < AbstractMap.SimpleImmutableEntry <Integer,Integer> > validSections =
            new ArrayList < AbstractMap.SimpleImmutableEntry <Integer,Integer> >();
        TreeMap<String,String> sectionSpan2Id = new TreeMap<>();
        FSIndex<NoteSection> section_index = aJCas.getAnnotationIndex( NoteSection.type );
        Iterator<NoteSection> section_iter = section_index.iterator();   
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
            // The SNIPPET as the entire section (header + contents)
            String full_section = note_text.substring( start_offset , end_offset );
            // The SNIPPET as the 20 characters +/- the section header
            int start_snippet = Math.max( start_offset - 20 , 1 );
            int end_snippet = Math.min( end_offset + 20 ,  note_text.length() );
            String section_snippet = note_text.substring( start_snippet , end_snippet );
            // TODO - clean up the types here between _code (str) and _id (int)
            String concept_code = mSectionConceptCodes.getOrDefault( section_props.getSectionId() ,
                                                                     "0" );
            // TODO - convert these to "S0001" format
            int source_concept_id = Integer.valueOf( concept_code ) - 2000003000;
            String section_modifiers = section_props.getModifiers();
            if( section_modifiers != null && section_modifiers.contains( "skip=true" ) ){
                skipSections.add( new AbstractMap.SimpleImmutableEntry<>( start_offset , end_offset ) );
            } else {
                validSections.add( new AbstractMap.SimpleImmutableEntry<>( start_offset , end_offset ) );
                String section_span = String.format( "[ %d - %d ]" , start_offset , end_offset );
                sectionSpan2Id.put( section_span , 
                                    section_props.getSectionId() );
                // If there is a header to this section, we don't want to match concepts
                // found within the header
                if( start_header_offset != -1 ) {
                    sectionHeaders.add( new AbstractMap.SimpleImmutableEntry<>( start_header_offset , 
                                                                                end_header_offset ) );
                }
            }
            // Add the full contents of the section to the NOTE_SEGMENTS table
            if( ! mAutoIncrement ){
                note_segments_id++;
            }
            insertSection( aJCas, 
                           note_segments_id , Integer.valueOf( note_id ) ,
                           Integer.valueOf( concept_code ) ,
                           mNlpSystem , 
                           full_section , 
                           String.valueOf( start_offset ) ,
                           lexical_variant ,
                           Integer.valueOf( concept_code ) ,
                           source_concept_id ,
                           section_modifiers );
            segments_added++;
        }
        // If we didn't extract any sections, we still want to add a row
        // to the NOTE_SEGMENTS table for a record that the note was
        // processed accordingly.
        if( segments_added == 0 ){
            mSectionlessNotes++;
            if( ! mAutoIncrement ){
                note_segments_id++;
            }
            insertSection( aJCas, 
                           note_segments_id , Integer.valueOf( note_id ) ,
                           0 ,
                           mNlpSystem , 
                           "No Extractions" , 
                           "-1" ,
                           "No Extractions" ,
                           -1 ,
                           -1 ,
                           "" );
        } else {
            mNoteSections += segments_added;
        }
        
        ///////
        FSIndex<Note_Nlp_TableProperties> term_index = aJCas.getAnnotationIndex( Note_Nlp_TableProperties.type );
        Iterator<Note_Nlp_TableProperties> term_iter = term_index.iterator();   
        while ( term_iter.hasNext() ) {
            Note_Nlp_TableProperties lexical_entry = (Note_Nlp_TableProperties)term_iter.next();
            int start_offset = Integer.valueOf( lexical_entry.getOffset() );
            int end_offset = start_offset + lexical_entry.getLexical_variant().length();
            // We can skip 2-letter entries
            if( end_offset - start_offset < 3 ){
                continue;
            }
            ////////////////////////////////
            String section_type = "No matching concept";
            // TODO - explicitly only search for concepts without sections already included
            // Check for matches within templatic sections
            for( AbstractMap.SimpleImmutableEntry <Integer,Integer> this_pair : skipSections ){ 
                int start_section = this_pair.getKey();
                int end_section = this_pair.getValue();
                if( start_section <= start_offset &
                    end_offset <= end_section ){
                    section_type = "Template";
                    break;
                }
            }
            // Check for matches within section headers
            for( AbstractMap.SimpleImmutableEntry <Integer,Integer> this_pair : sectionHeaders ){ 
                int start_section = this_pair.getKey();
                int end_section = this_pair.getValue();
                if( start_section <= start_offset &
                    end_offset <= end_section ){
                    section_type = "SectionHeader";
                    break;
                }
            }
            if( section_type == "Template" |
                section_type == "SectionHeader" ){
                continue;
            }
            ////
            // Check for matches within section headers
            int start_section = -1;
            int end_section = -1;
            for( AbstractMap.SimpleImmutableEntry <Integer,Integer> this_pair : validSections ){ 
                start_section = this_pair.getKey();
                end_section = this_pair.getValue();
                if( start_section <= start_offset &
                    end_offset <= end_section ){
                    String section_span = String.format( "[ %d - %d ]" , start_section , end_section );
                    section_type = sectionSpan2Id.get( section_span );
                    break;
                }
            }
            ////////////////////
            int section_concept_code = Integer.valueOf( mSectionConceptCodes.getOrDefault( section_type ,
                                                                                           "0" ) );
            String lexical_variant = lexical_entry.getLexical_variant();
            String snippet = lexical_entry.getSnippet();
            // TODO - lodge a bug report with these FPs
            // The virtual visit type string causes false positives
            // As can drug names
            if( snippet != null &&
                ( snippet.contains( "virtual visit for" ) |
                  snippet.contains( "Vicks NyQuil Cold/Flu" ) ) ){
                continue;
            }
            ////////
            if( start_section == -1 ){
                mLogger.warn( "Illegal section offsets found in '" + 
                              note_id + "' with start_section = " + 
                              start_section +
                              " in relation to lexical_variant '" + lexical_variant +
                              "' at [ " + start_offset + " - " + 
                              end_offset + " ]" );
                continue;
            }

            // If this source concept id is a CUI (with the initial C), then strip it
            // down to just the numerical part.
            // TODO - replace CUIs here with the actual OMOP CDM source concept it from the
            //        CONCEPT table
            String raw_source_concept_id = lexical_entry.getNote_nlp_source_concept_id();
            while( ! Character.isDigit( raw_source_concept_id.charAt( 0 ) ) ){
                raw_source_concept_id = raw_source_concept_id.substring( 1 );
            }
            int source_concept_id = Integer.parseInt( raw_source_concept_id );
                
            ////////
            if( ! mAutoIncrement ){
                note_nlp_id++;
            }
            insertNoteNlpRow( aJCas ,
                              "NOTE_NLP" ,
                              note_nlp_id ,
                              Integer.parseInt( note_id ) ,
                              section_concept_code ,
                              mNlpSystem ,
                              snippet , 
                              lexical_entry.getOffset() ,
                              lexical_variant ,
                              lexical_entry.getNote_nlp_concept_id() ,
                              source_concept_id ,
                              lexical_entry.getTerm_exists() ,
                              lexical_entry.getTerm_modifiers() );
            terms_added++;
        }

        ///////////////////////////////////////////////////
        /////// Medications (including allergies)
        FSIndex<Drug> meds_index = aJCas.getAnnotationIndex( Drug.type );
        Iterator<Drug> meds_iter = meds_index.iterator();   
        while ( meds_iter.hasNext() ) {
            Drug lexical_entry = (Drug)meds_iter.next();
            int start_offset = lexical_entry.getBegin();
            int end_offset = lexical_entry.getEnd();
            ////////////////////////////////
            String section_type = "No matching concept";
            // Check for matches within sections
            int start_section = -1;
            int end_section = -1;
            for( AbstractMap.SimpleImmutableEntry <Integer,Integer> this_pair : validSections ){ 
                start_section = this_pair.getKey();
                end_section = this_pair.getValue();
                if( start_section <= start_offset &
                    end_offset <= end_section ){
                    String section_span = String.format( "[ %d - %d ]" , start_section , end_section );
                    section_type = sectionSpan2Id.get( section_span );
                    break;
                }
            }
            ////////////////////
            if( ! section_type.equals( "Medication history" ) &
                ! section_type.equals( "Allergies" ) &
                ! section_type.equals( "Follow-up/Instructions" ) &
                ! section_type.equals( "Medication prescriptions" ) ){
                continue;
            }
            ////////////////////
            int section_concept_code = Integer.valueOf( mSectionConceptCodes.getOrDefault( section_type ,
                                                                                           "0" ) );
            String lexical_variant = note_text.substring( start_offset , end_offset );
            int start_snippet = Math.max( start_offset - 20 , 1 );
            int end_snippet = Math.min( end_offset + 20 ,  note_text.length() );
            String snippet = note_text.substring( start_snippet , end_snippet );
            // TODO - look up the CUI in the concept table and link appropriately
            String source_cui = lexical_entry.getCUI();
            int cui_suffix = 0;
            if( source_cui != null ){
                if( source_cui.equals( "" ) ||
                    source_cui.equals( "CUI-less" ) ){
                    cui_suffix = 0;
                } else {
                    while( ! source_cui.equals( "" ) && ! Character.isDigit( source_cui.charAt( 0 ) ) ){
                        source_cui = source_cui.substring( 1 );
                    }
                    if( source_cui.equals( "" ) ){
                        cui_suffix = 0;
                    } else {
                        cui_suffix = Integer.valueOf( source_cui );
                    }
                }
            }
            ////////////////////
            ArrayList<String> attribute_modifiers = new ArrayList<String>();
            FSIndex<Dosage> dosage_index = aJCas.getAnnotationIndex( Dosage.type );
            Iterator<Dosage> dosage_iter = dosage_index.iterator();   
            while ( dosage_iter.hasNext() ) {
                Dosage dosage_entry = (Dosage)dosage_iter.next();
                if( dosage_entry.getDrug() == lexical_entry ){
                    attribute_modifiers.add( "dosage=" + dosage_entry.getCoveredText() );
                    break;
                }
            }
            FSIndex<Duration> dur_index = aJCas.getAnnotationIndex( Duration.type );
            Iterator<Duration> dur_iter = dur_index.iterator();   
            while ( dur_iter.hasNext() ) {
                Duration dur_entry = (Duration)dur_iter.next();
                if( dur_entry.getDrug() == lexical_entry ){
                    attribute_modifiers.add( "duration=" + dur_entry.getCoveredText() );
                    break;
                }
            }
            FSIndex<Frequency> freq_index = aJCas.getAnnotationIndex( Frequency.type );
            Iterator<Frequency> freq_iter = freq_index.iterator();   
            while ( freq_iter.hasNext() ) {
                Frequency freq_entry = (Frequency)freq_iter.next();
                if( freq_entry.getDrug() == lexical_entry ){
                    attribute_modifiers.add( "frequency=" + freq_entry.getCoveredText() );
                    break;
                }
            }
            FSIndex<Route> route_index = aJCas.getAnnotationIndex( Route.type );
            Iterator<Route> route_iter = route_index.iterator();   
            while ( route_iter.hasNext() ) {
                Route route_entry = (Route)route_iter.next();
                if( route_entry.getDrug() == lexical_entry ){
                    attribute_modifiers.add( "route=" + route_entry.getCoveredText() );
                    break;
                }
            }
            String term_modifiers = "";
            if( attribute_modifiers.size() > 0 ){
                term_modifiers = String.join( ";" ,  attribute_modifiers );
            }
            ////////
            if( ! mAutoIncrement ){
                note_nlp_id++;
            }
            insertTerm( aJCas, 
                        note_nlp_id , Integer.valueOf( note_id ) ,
                        section_concept_code ,
                        mNlpSystem , 
                        snippet , 
                        String.valueOf( start_offset ) ,
                        lexical_variant ,
                        0 ,
                        cui_suffix ,
                        "" ,
                        term_modifiers );
            terms_added++;
        }

        ///////////////////////////////////////////////////
        /////// Laboratory test names
        // TODO - could this be refactored to re-use the medications
        //        code block above?
        FSIndex<LabName> labs_index = aJCas.getAnnotationIndex( LabName.type );
        Iterator<LabName> labs_iter = labs_index.iterator();   
        while ( labs_iter.hasNext() ) {
            LabName lexical_entry = (LabName)labs_iter.next();
            int start_offset = lexical_entry.getBegin();
            int end_offset = lexical_entry.getEnd();
            ////////////////////////////////
            String section_type = "No matching concept";
            // Check for matches within sections
            int start_section = -1;
            int end_section = -1;
            for( AbstractMap.SimpleImmutableEntry <Integer,Integer> this_pair : validSections ){ 
                start_section = this_pair.getKey();
                end_section = this_pair.getValue();
                if( start_section <= start_offset &
                    end_offset <= end_section ){
                    String section_span = String.format( "[ %d - %d ]" , start_section , end_section );
                    section_type = sectionSpan2Id.get( section_span );
                    break;
                }
            }
            ////////////////////
            // TODO - we haven't seen enough lab test names in real notes
            //        to have a good sense of which sections we'd want to
            //        focus on (beyond the skip=true sections already having
            //        been filtered out prior to extraction).
            //          if( ! section_type.equals( "..." ) &
            //                  ! ... ){
            //                  continue;
            //          }
            ////////////////////
            int section_concept_code = Integer.valueOf( mSectionConceptCodes.getOrDefault( section_type ,
                                                                                           "0" ) );
            String lexical_variant = note_text.substring( start_offset , end_offset );
            int start_snippet = Math.max( start_offset - 20 , 1 );
            int end_snippet = Math.min( end_offset + 20 ,  note_text.length() );
            String snippet = note_text.substring( start_snippet , end_snippet );
            // TODO - look up the CUI in the concept table and link appropriately
            String source_cui = lexical_entry.getCUI();
            int cui_suffix = 0;
            if( source_cui != null ){
                if( source_cui.equals( "" ) ||
                    source_cui.equals( "CUI-less" ) ){
                    cui_suffix = 0;
                } else {
                    while( ! source_cui.equals( "" ) && ! Character.isDigit( source_cui.charAt( 0 ) ) ){
                        source_cui = source_cui.substring( 1 );
                    }
                    if( source_cui.equals( "" ) ){
                        cui_suffix = 0;
                    } else {
                        cui_suffix = Integer.valueOf( source_cui );
                    }
                }
            }
            ////////////////////
            ArrayList<String> attribute_modifiers = new ArrayList<String>();
            FSIndex<LabValue> labval_index = aJCas.getAnnotationIndex( LabValue.type );
            Iterator<LabValue> labval_iter = labval_index.iterator();   
            while ( labval_iter.hasNext() ) {
                LabValue labval_entry = (LabValue)labval_iter.next();
                if( labval_entry.getLabName() == lexical_entry ){
                    attribute_modifiers.add( "labValue=" + labval_entry.getCoveredText() );
                    break;
                }
            }
            String term_modifiers = "";
            if( attribute_modifiers.size() > 0 ){
                term_modifiers = String.join( ";" ,  attribute_modifiers );
            }
            ////////
            if( ! mAutoIncrement ){
                note_nlp_id++;
            }
            insertTerm( aJCas, 
                        note_nlp_id , Integer.valueOf( note_id ) ,
                        section_concept_code ,
                        mNlpSystem , 
                        snippet , 
                        String.valueOf( start_offset ) ,
                        lexical_variant ,
                        0 ,
                        cui_suffix ,
                        "" ,
                        term_modifiers );
            terms_added++;
        }
        
        ///////////////////////////////////////////////////
        if( terms_added == 0 ){
            mExtractionlessNotes++;
            if( ! mAutoIncrement ){
                note_nlp_id++;
            }
            insertTerm( aJCas, 
                        note_nlp_id , Integer.valueOf( note_id ) ,
                        0 ,
                        mNlpSystem , 
                        "No Extractions" , 
                        "-1" ,
                        "No Extractions" ,
                        -1 ,
                        -1 ,
                        "" ,
                        "" );
        } else {
            mNoteExtractions += terms_added;
        }
        
        // We made it all the way to the bottom so we can consider this note
        // to *not* be a processing failure.
        mFailedNotes--;

        if( mWriteToDisk ){
            for( int i = 0 ; i < mFilteredAnnotations.size() ; i++ ){
                Note_Nlp_TableProperties annotation = mFilteredAnnotations.get( i );
                annotation.addToIndexes();
            }
        }
    }

    public void destroy() {
        try {
            if( mCon != null ) {
                mCon.close();
                mLogger.info( "NOTE_NLP connection closed." );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mLogger.info( "Unique notes parsed for the DataMart: " + String.valueOf( mNoteCount ) );
        mLogger.info( "Unique notes failed to be written to the DataMart: " + String.valueOf( mFailedNotes ) );
        mLogger.info( "Unique note sections written to the DataMart: " + String.valueOf( mNoteSections ) );
        mLogger.info( "Unique notes with no sections written to the DataMart: " + String.valueOf( mSectionlessNotes ) );
        mLogger.info( "Unique note extractions written to the DataMart: " + String.valueOf( mNoteExtractions ) );
        mLogger.info( "Unique notes with no extractions written to the DataMart: " + String.valueOf( mExtractionlessNotes ) );
    }
}
