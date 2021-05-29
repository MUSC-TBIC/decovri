package edu.musc.tbic.concepts;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.ctakes.typesystem.type.textspan.Sentence;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.musc.tbic.concepts.UtilCov;
import edu.musc.tbic.omop_cdm.Note_TableProperties;
import edu.musc.tbic.uima.Decovri;
import edu.musc.tbic.uima.NoteSection;

public class PyAnnotatorViaSSL extends JCasAnnotator_ImplBase {

	public static final String PARAM_HOSTNAME = "Host";
	@ConfigurationParameter( name = PARAM_HOSTNAME , 
			 				 description = "Hostname that Python BiLSTM model is running on" , 
			 				 mandatory = true )
    private String mHostName;

    public static final String PARAM_PORTNUMBER = "Port";
    @ConfigurationParameter( name = PARAM_PORTNUMBER , 
                             description = "Port that Python BiLSTM model is listening on" , 
                             mandatory = true )
    private int mPortNumber;

    public static final String PARAM_KEYFILE = "KeyFile";
    @ConfigurationParameter( name = PARAM_KEYFILE , 
                             description = "Keystore file for creating SSL connection" , 
                             mandatory = true )
    private String mKeyFile;

	private Logger mLogger = LoggerFactory.getLogger( Decovri.class );
	
    SSLSocket kkSocket;
    PrintWriter out;
    BufferedReader in;
    
    public void initialize(UimaContext uimaContext)	throws ResourceInitializationException {
		super.initialize(uimaContext);
		
		// TODO - can be changed to uima 2 style ?
		mHostName = (String)(uimaContext.getConfigParameterValue("Host"));
		mPortNumber = (Integer)(uimaContext.getConfigParameterValue("Port"));
		// TODO - make this a real, valid parameter
		mKeyFile = (String)( uimaContext.getConfigParameterValue( "KeyFile" ) );
		//      "resources/keystore.jks";//(String)(uimaContext.getConfigParameterValue("KeyFile"));
    }        

    private Boolean skippableSection( JCas aJCas , int start_offset , int end_offset ){
		FSIndex<NoteSection> sect_index = aJCas.getAnnotationIndex( NoteSection.type );
        Iterator<NoteSection> sect_iter = sect_index.iterator();
        while( sect_iter.hasNext() ){
        	NoteSection this_section = (NoteSection)sect_iter.next();
        	int start_sect_offset = this_section.getBegin();
        	int end_sect_offset = this_section.getEnd();
        	String section_id = this_section.getSectionId();
        	String section_modifiers = this_section.getModifiers();
        	// Sentence is fully within margin of section span
        	if( start_sect_offset <= start_offset &
        		end_offset <= end_sect_offset ){
        		if( section_id.equals( "Template" ) |
    				section_id.equals( "Administration" ) |
                    ( section_modifiers != null &&
                            section_modifiers.contains( "skip=true" ) ) ){
        			return true;
        		} else {
        			return false;
        		}
        	// This sentence overlaps with a skippable section
        	} else if( ( start_offset <= start_sect_offset &
        			     start_sect_offset <= end_offset ) |
        			   ( start_offset <= end_sect_offset &
        			     end_sect_offset <= end_offset ) ){
        		if( section_id.equals( "Template" ) |
        			section_id.equals( "Administration" ) |
        			( section_modifiers != null &&
        			        section_modifiers.contains( "skip=true" ) ) ){
        			return true;
        		} else {
        			return false;
        		}
        	}
		}
        
		return false;
	}
    
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		
		try {
		    String note_id = "";
	        FSIndex<Note_TableProperties> note_props_index = jCas.getAnnotationIndex( Note_TableProperties.type );
	        Iterator<Note_TableProperties> note_props_iter = note_props_index.iterator();   
	        if( note_props_iter.hasNext() ) {
	            Note_TableProperties note_props = (Note_TableProperties)note_props_iter.next();
	            note_id = note_props.getNote_id();
	        } else {
	            FSIterator<?> it = jCas.getAnnotationIndex(SourceDocumentInformation.type).iterator();
	            if( it.hasNext() ){
	                SourceDocumentInformation fileLoc = (SourceDocumentInformation) it.next();
	                note_id = fileLoc.getUri().toString();
	            }
	            if( note_id.endsWith( ".txt" ) ){
	                note_id = note_id.substring( 0 , note_id.length() - 4 );
	            }
	        }
	        
	    	String text = jCas.getDocumentText();
			
	    	// Collect all the sentences and their relative spans
	    	// TODO - refactor this iteration and the next into a cleaner run
//	    	TreeMap<Integer, Integer> sentences = new TreeMap<>();
//	    	UtilCov.annotSent(text, sentences);
			TreeMap<Integer, Integer> sentences = new TreeMap<>();
			Collection<Sentence> sentence_collection = JCasUtil.select( jCas , Sentence.class );
			for( Sentence current_sentence : sentence_collection ){
			    sentences.put( current_sentence.getBegin() , 
			            current_sentence.getEnd() );
			}
			
			StringBuilder sb = new StringBuilder();			
		    int sI = 1;
			for (int b: sentences.keySet()) {
				int e = sentences.get(b);
				String sTxt = text.substring(b, e);
				if( skippableSection( jCas , b , e ) ){
					// TODO - convert this to debug print
//					System.err.println( "Skipping sentence: " + sTxt );
					continue;
				}
				if( sTxt.trim().isEmpty() ){
					continue;
				}
						
				TreeMap<Integer, Integer> tokens = new TreeMap<>();
				UtilCov.annotTok(sTxt, tokens, b);
				
				TreeMap<Integer, Integer> aTokens = new TreeMap<>();
			    for (int tB : tokens.keySet()) {
			    	int tE = tokens.get(tB);
			    	UtilCov.splitTokens(text.substring(tB, tE), tB, aTokens);					
				}
			    
			    int tI = 0;
			    for (int tB : aTokens.keySet()) {
			    	int tE = aTokens.get(tB);
			    	String tokStr = text.substring(tB, tE);
			    	sb.append(tokStr + "\t" + tB + "\t" + tE + "\t" + tI + "\t" + sI + "\t" + note_id + "\t" + "O" + "\n");
			    	tI++;
			    }
				sb.append("\n");	
				sI++;
			}
			
			connect();
			String oStr = UtilCov.predict(sb.toString(), in, out);
			ArrayList<String> outs = new ArrayList<>();		
			UtilCov.readTag(oStr, outs);

			UtilCov.readPredict(jCas, outs);		
			disconnect();
			
		} catch (IOException e) {
		    System.err.println("Med/Lab annotation: IOException: " + e.getMessage());
			//e.printStackTrace();
		}
	}
	
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		/*
		try {
			connect();	
			endMsg();
			disconnect();			
		} catch (IOException e) {
		    System.err.println("Lab annotation: IOException: " + e.getMessage());
			//e.printStackTrace();
		}
		*/	
	}    
	
	private void connect() throws IOException {
		// 
		System.setProperty( "javax.net.ssl.trustStore" , mKeyFile );
		SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
		mLogger.info( "Creating SSL connection: " + mHostName + ":" + mPortNumber );
		kkSocket = (SSLSocket)factory.createSocket( mHostName , mPortNumber );
	    out = new PrintWriter( kkSocket.getOutputStream() , true) ;
	    in = new BufferedReader( new BufferedReader( new InputStreamReader( kkSocket.getInputStream() ) ) );
    }
    
	private void disconnect() throws IOException {   	
    	if (in != null) {
			in.close();
    	}
    	if (out != null) {
    		out.close();
    	}
    	if (!kkSocket.isClosed()) {
    		kkSocket.close();
    	}	
	}
    
	private void endMsg() {
		String endMsg = "--<eosc>--";
		
    	byte[] bText = endMsg.getBytes();
    	String nStr = new String(bText, UTF_8);         	            
        out.println(nStr);        
    }
    
	
}
