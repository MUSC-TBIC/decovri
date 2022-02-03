package edu.musc.tbic.readers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.musc.tbic.uima.Decovri;
import edu.musc.tbic.omop_cdm.Note_TableProperties;

public class OMOP_CDM_CollectionReader extends CollectionReader_ImplBase {
    
    private Logger mLogger = LoggerFactory.getLogger( Decovri.class );

    public static final String PARAM_VERSION = "Version";
    @ConfigurationParameter( name = PARAM_VERSION , 
                             description = "NLP system version number needed for logging" , 
                             mandatory = true )
    private String mVersion;
    private String mNlpSystem;
    
    public static final String PARAM_WATERMARK = "Watermark";
    @ConfigurationParameter( name = PARAM_WATERMARK , 
                             description = "Lowest note_id to pull" , 
                             mandatory = true )
    private int mWatermark;
    private File mWatermarkFile;
    
    public static final String PARAM_DBCONNECTION = "DatabaseConnection";
    @ConfigurationParameter( name = PARAM_DBCONNECTION , 
                             description = "Properties file to pull database connection properties from" , 
                             mandatory = false )
    private Properties mProperties;
    
    /** Counters and metrics. */
    private int mCurrentFile;
    private int mFileCount;
    private int mEmptyFiles;
    private int mFailedFiles;
    private int mRowCount;
    private int mFailedRows;
    private int mTotalRows;
    
    // pull these values from a parameters config file
    private String mDbms = null;
    private String mServer = null;
    private String mUsername = null;
    private String mPassword = null;
    private String mDatabase = null;
    private String mSchema = null;
    
    private Connection mCon;
    private Statement mStmt;
    private ResultSet mRs;
    
    private Integer mCurrentIndex;
    
    /**
     * @see org.apache.uima.collection.CollectionReader_ImplBase#initialize()
     */
    public void initialize() throws ResourceInitializationException {
        mProperties = new Properties();
        try {
            String database_connection_filename = (String) getConfigParameterValue( PARAM_DBCONNECTION );
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
    
        String filtered_nlp_systems = "";
        if( mProperties.containsKey( "nlp.systems" ) ){
            String[] system_list = mProperties.getProperty( "nlp.systems" ).split( "," );
            if( system_list.length == 1 ){
                filtered_nlp_systems = "WHERE nn.NLP_SYSTEM in ( '" + system_list[ 0 ].trim() + "' )";
            } else {
                for( int i = 0 ; i < system_list.length ; i++ ){
                    system_list[ i ] = system_list[ i ].trim();                 
                }
                filtered_nlp_systems = "WHERE nn.NLP_SYSTEM in ( '" +
                    String.join( "' , '" , system_list ) +
                    "' )";
            }
            mLogger.info( "Filtering out notes already processed by the following NLP_SYSTEMs: " + filtered_nlp_systems );
        } else {
            mLogger.warn( "No restrictions put on NLP_SYSTEMs for filtering notes" );
        }

        String filtered_note_types = "";
        if( mProperties.containsKey( "filter.note_type.field" ) &
            ! mProperties.getProperty( "filter.note_type.field" ).trim().equals( "" ) &
            mProperties.containsKey( "filter.note_type.values" ) &
            ! mProperties.getProperty( "filter.note_type.values" ).trim().equals( "" ) ){
            String note_type_field = mProperties.getProperty( "filter.note_type.field" );
            String[] note_types_list = mProperties.getProperty( "filter.note_type.values" ).split( "," );
            if( note_types_list.length == 1 ){
                filtered_note_types = "AND nt." + note_type_field +
                    " in ( '" + note_types_list[ 0 ].trim() + "' )";
            } else {
                for( int i = 0 ; i < note_types_list.length ; i++ ){
                    note_types_list[ i ] = note_types_list[ i ].trim();                 
                }
                filtered_note_types = "AND nt." + note_type_field +
                    " in ( '" +
                    String.join( "' , '" , note_types_list ) +
                    "' )";
            }
            mLogger.info( "Filtering to include only those notes of the following type: " + filtered_note_types );
        } else {
            mLogger.warn( "No restrictions put on NOTE_TYPES for filtering notes" );
        }
        
        // Initialize the watermark id to whatever was passed as
        // an analysis engine parameter.  Then check for a 
        // watermark_file to read a more current value from.
        // When the file is not present, initialize its value
        // to the parameter value. If no water_file is provided,
        // then we won't keep track of this value for later.
        // TODO - allow the watermark_file to be provided as
        //        an AE parameter, too
        mWatermark = (int) getConfigParameterValue( "Watermark" );
        if( mProperties.containsKey( "fs.watermark_file" ) &&
            ! mProperties.getProperty( "fs.watermark_file" ).trim().equals( "" ) ){
            mWatermarkFile = new File( mProperties.getProperty( "fs.watermark_file" ) );
            BufferedReader br = null;
            FileReader fr = null;
            try {
                if( !mWatermarkFile.exists() ) {
                    mLogger.info( "Creating watermark file (" + mWatermarkFile.toString() + ") " +
                                  "and initializing to " + String.valueOf( mWatermark ) ); 
                    mWatermarkFile.createNewFile();
                } else {
                    fr = new FileReader( mWatermarkFile );
                    br = new BufferedReader( fr );
                    // read the first (and only) line 
                    String line = br.readLine();
                    if( line == null || 
                        line.trim().equals( "" ) ){
                        mWatermark = 0;
                    } else {
                        mWatermark = Integer.parseInt( line.trim() );
                    }
                }
            } catch (IOException e) {
                mLogger.error("IOException: %s%n", e);
            } finally {
                try {
                    if (br != null){
                        br.close();
                    }
                    if (fr != null){
                        fr.close();
                    }
                } catch (IOException ex) {
                    mLogger.error("IOException: %s%n", ex);
                }
            }
        }
        
        mCurrentIndex = 0;
        
        // max_watermark determines the upperbound we set on the range of note_ids
        // that we're willing to pull from:
        //     mWatermark <= note_id < ( mWaterMark + max_watermark )
        // Within that range of *possible* note_ids, limitString determines how
        // many we actually pull.  For example, out of a 1k possible notes, only pull
        // the first 10. Set the limitString to -1 to try to pull the full batch_window
        // amount.
        // TODO - add try-catch block
        String limitString = mProperties.getProperty( "db.batch_size" );
        if( limitString.equals( "-1" ) ){
            limitString = "";
        }
        //
        if( limitString == "" ){
            mLogger.info( "Pulling notes starting after note_id " + mWatermark );
        } else {
            mLogger.info( "Pulling at most " + limitString + " notes starting after note_id " + mWatermark );
        }
        
        mCon = null;
        mStmt = null;
        mRs = null;
        try {
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
                                                    // TODO - make the database connection details portable/configurable
                                                    ";domain=CLINLAN;useNTLMv2=true" +
                                                    ";integratedSecurity=false" ,
                                                    mUsername , 
                                                    mPassword );
            } else {
                mLogger.error( "Unrecognized DBMS type:  '" + mDbms + "'" );
            }
            mStmt = mCon.createStatement();
            if( mDbms.equalsIgnoreCase( "sqlserver" ) ){
                if( limitString != "" ){
                    limitString = "TOP(" + limitString + ") ";
                }
                mRs = mStmt.executeQuery( "SELECT " + limitString +
                                          "    nt.note_id , nt.person_id , nt.note_text " +
                                          "    FROM " + mSchema + ".note as nt " +
                                          "    LEFT JOIN ( SELECT DISTINCT nn.NOTE_ID " +
                                          "               FROM " + mDatabase + "." + mSchema + ".note_nlp as nn " +
                                          "               " + filtered_nlp_systems +
                                          "              ) nnj " +
                                          "    ON nt.NOTE_ID = nnj.NOTE_ID " +
                                          "    WHERE nt.note_id > '" + mWatermark + "' " +
                                          "    AND nnj.NOTE_ID is NULL " +
                                          "    " + filtered_note_types +
                                          "    ORDER BY nt.note_id" );
            } else if( mDbms.equalsIgnoreCase( "oracle" ) ){
                if( limitString != "" ){
                    limitString = " ROWNUM " + limitString;
                }
                mRs = mStmt.executeQuery( "ALTER SESSION SET CURRENT_SCHEMA = " + mSchema );
                mRs = mStmt.executeQuery( "SELECT note_id , patient_mrn , full_text " +
                                          "FROM NOTE " +
                                          "WHERE note_id > " + mWatermark + " " +
                                          filtered_note_types +
                                          limitString );
                // TODO - look up oracle order by syntax
                // TODO - add filtering logic to oracle syntax
            }
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace(); 
        } catch ( NullPointerException e ) {
            e.printStackTrace();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        mLogger.debug( "NOTE connetion initialized" );
    }
    
    private void updateWatermark( int note_id ){
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            if( !mWatermarkFile.exists() ) {
                mLogger.error( "Failed to locate watermark file (" + mWatermarkFile.toString() + "). " +
                               "It should have already been initialized" ); 
            } else {
                mLogger.debug( "Updating watermark file (" + mWatermarkFile.toString() + ") " +
                               "to " + String.valueOf( note_id )); 
                fw = new FileWriter( mWatermarkFile );
                bw = new BufferedWriter( fw );
                bw.write( note_id + "\n" );
                bw.close();
            }
        } catch (IOException e) {
            mLogger.error("IOException: %s%n", e);
        } finally {
            try {
                if (fw != null){
                    fw.close();
                }
                if (bw != null){
                    bw.close();
                }
            } catch (IOException ex) {
                mLogger.error("IOException: %s%n", ex);
            }
        }
    }

    private Boolean getNextNote( JCas jcas ) throws IOException, CollectionException {
        Integer note_id = null;
        Integer person_id = null;
        String note_text = null;
        try {
            // Grab the next record in the database
            mRs.next();
            note_id = mRs.getInt( 1 );
            person_id = mRs.getInt( 2 );
            note_text = mRs.getString( 3 );
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // As long as we pulled a valid NOTE_ID, increment
        // the latest watermark_id to this value so we'll
        // pick up the next run *after* this note.
        if( note_id != null &
            mWatermarkFile != null ){
            updateWatermark( note_id );            
        }
        
        // TODO - consider a failure here (null note_id, person_id, or 
        // note_text) as a failed document and log appropriately
        if( note_id != null &&
            person_id != null &&
            note_text != null ){
            // put document in CAS
            jcas.setDocumentText( note_text );
            // Add the NOTE properties to the CAS
            Note_TableProperties note_props = new Note_TableProperties( jcas );
            note_props.setNote_id( String.valueOf( note_id ) );
            note_props.setPerson_id( String.valueOf( person_id ) );
            note_props.addToIndexes();
        }
        
        return true;
    }
    
    public void getNext(CAS aCAS) throws IOException, CollectionException {
        JCas jcas;
        try {
            jcas = aCAS.getJCas();
        } catch (CASException e) {
            throw new CollectionException(e);
        }
        
        Boolean success_flag = getNextNote( jcas );
        
        if( success_flag ){
            mFileCount++;
            mCurrentIndex++;
        }
        
    }
    
    public boolean hasNext() throws IOException, CollectionException {
        try {
            if( !mRs.isLast() &&
                ( mRs.isBeforeFirst() ||
                  ( mRs.getRow() != 0 ) ) ){
                return true;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    
    public Progress[] getProgress() {
        // TODO - generate a reasonable value for the progress
        return new Progress[] { new ProgressImpl( mCurrentIndex , 1 , Progress.ENTITIES ) };
    }

    public void close() throws IOException {
        try {
            if( mRs != null ) {
                mRs.close();
            }
            if( mStmt != null ) {
                mStmt.close();
            }
            if( mCon != null ) {
                mCon.close();
                mLogger.info( "NOTE connection closed." );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mLogger.info( "Notes pulled from the DataMart: " + String.valueOf( mCurrentIndex ) );
    }
    
    public void destroy() {
        mLogger.info( "Unique files pulled from the DataMart: " + String.valueOf( mFileCount ) );
        mLogger.info( "Unique empty files pulled from the DataMart: " + String.valueOf( mEmptyFiles ) );
        // TODO - find correct place to track/capture failed files
        //      mLogger.info( "Unique files failed to be parsed for the DataMart: " + String.valueOf( mFailedFiles ) );
    }

}
