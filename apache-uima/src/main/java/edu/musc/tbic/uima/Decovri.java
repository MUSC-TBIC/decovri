package edu.musc.tbic.uima;

import java.io.File;
import java.io.FileWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import edu.musc.tbic.readers.FileSystemCollectionReader;
import edu.musc.tbic.readers.OMOP_CDM_CollectionReader;

import org.apache.ctakes.core.ae.SimpleSegmentAnnotator;
import org.apache.ctakes.core.ae.SentenceDetector;

import edu.musc.tbic.opennlp.OpenNlpTokenizer;
import edu.musc.tbic.textspans.TemplateSectionizer;
//import edu.musc.tbic.sentencePatch.delimPatcher;
//import edu.musc.tbic.textspans.WekaSectionizer;
import edu.musc.tbic.uima.PatientDemographics;
import org.apache.uima.conceptMapper.ConceptMapper;
import org.apache.uima.conceptMapper.support.dictionaryResource.DictionaryResource_impl;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createDependencyAndBind;


import edu.musc.tbic.concepts.PyAnnotatorViaSSL;
import edu.musc.tbic.concepts.CUIAnnotator;
import edu.musc.tbic.concepts.RelAnnotator;
import edu.musc.tbic.context.ContextAnnotator;
import edu.musc.tbic.context.FastContextAnnotator;
import edu.utah.bmi.nlp.context.ConText;
import edu.musc.tbic.writers.OMOP_CDM_CASConsumer;
import edu.musc.tbic.writers.AnnotatedSectionWriter;
import edu.musc.tbic.writers.AnnotatedTextWriter;
import edu.musc.tbic.writers.XmlWriter;

/**
 * 
 * 
 * 
 * 
 */

public class Decovri extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {

	private static final Logger mLogger = LoggerFactory.getLogger( Decovri.class );
	private static Boolean mTestFlag;
	
	private static String mVersion;
	
	// Connection values are set in database_connection.properties
	static String dbms = null;
	static String dbHost = null;
    static String dbPort = null;
    static String dbDatabase = null;
    static String dbSchema = null;
    static String dbUsername = null;
    static String dbPassword = null;
    
    static int maxNotes = 10;
    
    static String documentType = null;
    
    static DocumentBuilderFactory mFactory;
    static DocumentBuilder mDomBuilder;

    public static void main(String[] args) throws ResourceInitializationException, UIMAException, IOException {

    	final Properties project_properties = new Properties();
    	final Properties pipeline_properties = new Properties();
    	project_properties.load( Decovri.class.getClassLoader().getResourceAsStream( "project.properties" ) );
    	mVersion = project_properties.getProperty( "version" );
    	
    	// create Options object
    	Options options = new Options();
    	// add option
    	options.addOption( "h" , "help" , false , "Display this help screen" );
    	options.addOption( "v" , "version" , false , "Display Decovri build version" );
    	// TODO
    	options.addOption( "c" , false , "Display Decovri configuration settings" );
    	// TODO
    	options.addOption( "s" , "soft-load" , false , "Soft-load all resources in all modules and report progress" );
    	//
        options.addOption( "p" , "pipeline-properties" , true , "Load the provided pipeline.properties file rather than the default" );
        //
        String database_properties_filename = "database_connection.properties";
        options.addOption( "d" , "database-properties" , true , 
                "Load the provided " + database_properties_filename + " file rather than the default" );
        CommandLineParser parser = new DefaultParser();
    	try {
            CommandLine cmd = parser.parse( options , args );
	        
	        if( cmd.hasOption( "help" ) ){
	            help( options );
	        }   
	        
	        if( cmd.hasOption( "pipeline-properties" ) ){
	            String pipeline_properties_file = cmd.getOptionValue( "pipeline-properties" );
	            mLogger.debug( "Loading non-default pipeline.properties file: " + pipeline_properties_file );
	            pipeline_properties.load( Decovri.class.getClassLoader().getResourceAsStream( pipeline_properties_file ) );
	        } else {
	            try {
	                pipeline_properties.load( Decovri.class.getClassLoader().getResourceAsStream( "pipeline.properties" ) );
	            } catch( NullPointerException e )
	            {
	                mLogger.error( "Unable to load pipeline.properties file. Copy over the template pipeline.properties.TEMPLATE or use the --pipeline-properties command-line option to specify an alternate file." );
                    help( options );
	            }
	        }
	        if( cmd.hasOption( "database-properties" ) ){
                database_properties_filename = cmd.getOptionValue( "database-properties" );
                mLogger.debug( "Loading non-default database_connection.properties file: " + database_properties_filename );
            }
	        
	        if( cmd.hasOption( "version" ) ){
	            System.out.println( "Configured to run resources for Decovri Screening Pipeline (Decovri) " + mVersion );
	            System.exit(0);
	        } else if( cmd.hasOption( "c" ) ){
	            System.out.println( "Configured to run resources for Decovri Screening Pipeline (Decovri) " + mVersion + "\n" +
	                    "\nThis option has not yet been implemented." );
	            System.exit(0);
	        } else if( cmd.hasOption( "soft-load" ) ){
	            System.out.println( "Configured to run resources for Decovri Screening Pipeline (Decovri) " + mVersion + "\n" +
	                    "\nThis option has not yet been implemented." );
	            System.exit(0);
	        }
    	} catch( ParseException exp ) {
    		// oops, something went wrong
    		mLogger.error( "Parsing failed.  Reason: " + exp.getMessage() );
		}

    	mLogger.info( "Loading resources for Decovri Screening Pipeline (Decovri) " + mVersion );
		
		///////////////////////////////////////////////////
		String pipeline_test_flag = pipeline_properties.getProperty( "fit.test_flag" );
		String pipeline_reader = pipeline_properties.getProperty( "fit.reader" );
		String pipeline_engines = pipeline_properties.getProperty( "fit.engines" );
		String pipeline_writers = pipeline_properties.getProperty( "fit.writers" );
		ArrayList<String> pipeline_modules = new ArrayList<String>();
		/////////////////////
		if( pipeline_test_flag.equalsIgnoreCase( "true" ) ){
			mTestFlag = true;
			mLogger.info( "Test flag set");
		} else if( pipeline_test_flag.equalsIgnoreCase( "false" ) ){
			mTestFlag = false;
		} else {
			mLogger.error( "Unrecognized test_flag value in pipeline.properties file (defaulting to 'true'):  '" + pipeline_test_flag + "'" );
			mTestFlag = true;
			mLogger.info( "Test flag set");
		}
		/////////////////////
		// Nothing special to do here other than make sure it is a valid/known reader
		if( pipeline_reader.equals( "Text Reader" ) ||
		    pipeline_reader.equals( "OMOP CDM Reader" ) ){
			pipeline_modules.add( pipeline_reader );
		} else {
			mLogger.error( "Unrecognized reader in pipeline.properties file:  '" + pipeline_reader + "'" );
		}
		/////////////////////
		//Nothing special to do here other than make sure it is a valid/known engine
		if( pipeline_engines.trim().equals( "" ) ){
			mLogger.debug( "No analysis engines added from the pipeline.properties file" );
		} else {
			for( String engine: pipeline_engines.split( "," ) ){
				engine = engine.trim();
				if( engine.equals( "cTAKES SBD" ) |
				    engine.equals( "OpenNLP Tokenizer" ) |
                    engine.equals( "Template Sectionizer" ) |
                    engine.equals( "SVM Sectionizer" ) |
	                engine.equals( "Demographics" ) |
                    engine.equals( "Dynamic ConceptMapper" ) |
                    engine.equals( "ConText" ) |
                    engine.equals( "FastContext" ) |
                    engine.equals( "Labs" ) |
                    engine.equals( "Meds" ) |
                    engine.equals( "NEN" ) |
                    engine.equals( "Attributes" ) ){
					pipeline_modules.add( engine );
				} else {
					mLogger.error( "Unrecognized analysis engine in pipeline.properties file:  '" + engine + "'" );
				}
			}
		}
		/////////////////////
		//Nothing special to do here other than make sure it is a valid/known writer
		for( String writer: pipeline_writers.split( "," ) ){
			writer = writer.trim();
			if( writer.equals( "Annotated Section Writer" ) |
			    writer.equals( "Annotated Text Out" ) |
	            writer.equals( "XML Out" ) |
				writer.equals( "OMOP CDM Writer" ) ){
				pipeline_modules.add( writer );
			} else if( writer.equals( "" ) ) {
			    mLogger.info( "No writer specified in the pipeline.properties file." );
			} else {
				mLogger.error( "Unrecognized writer in pipeline.properties file:  '" + writer + "'" );
			}
		}
	
		///////////////////////////////////////////////////
        CollectionReaderDescription collectionReader = null;
        AggregateBuilder builder = new AggregateBuilder();
        
        ///////////////////////////////////////////////////
        if( pipeline_modules.contains( "Text Reader" ) ){
            ////////////////////////////////////
            // Initialize plain text reader
            String sampleDir = "data/input";
            if( pipeline_properties.containsKey( "fs.in.text" ) ){
                sampleDir = pipeline_properties.getProperty( "fs.in.text" );
            }
            if (sampleDir.startsWith("~")){
                mLogger.warn("Input directory starts with '~', did you mean to use your full home path?");
            }
            mLogger.info( "Loading module 'Text Reader' for " + sampleDir );
            collectionReader = CollectionReaderFactory.createReaderDescription(
                    FileSystemCollectionReader.class ,
                    FileSystemCollectionReader.PARAM_INPUTDIR , sampleDir );
        } else if( pipeline_modules.contains( "OMOP CDM Reader" ) ){
            ////////////////////////////////////
            // Initialize database reader
            mLogger.info( "Loading module 'OmopCdmReader'" );
            collectionReader = CollectionReaderFactory.createReaderDescription(
                    OMOP_CDM_CollectionReader.class , 
                    OMOP_CDM_CollectionReader.PARAM_VERSION , mVersion ,
                    OMOP_CDM_CollectionReader.PARAM_WATERMARK , 0 ,
                    OMOP_CDM_CollectionReader.PARAM_DBCONNECTION , database_properties_filename );
        }
        
        ///////////////////////////////////////////////////
        // Sentence Splitters
        ///////////////////////////////////////////////////
        String sentence_type = null;
        ////////////////////////////////////
        if( pipeline_modules.contains( "cTAKES SBD" ) ){
            mLogger.info( "Loading cTAKES SentenceDetectorAnnotator" );
            sentence_type = "org.apache.ctakes.typesystem.type.textspan.Sentence";
            AnalysisEngineDescription ctakesSimpleSegments = AnalysisEngineFactory.createEngineDescription(
                    SimpleSegmentAnnotator.class );
            builder.add( ctakesSimpleSegments );
            AnalysisEngineDescription ctakesSentence = AnalysisEngineFactory.createEngineDescription(
                    SentenceDetector.class ,
                    SentenceDetector.PARAM_SD_MODEL_FILE , "ctakesModels/sd-med-model.zip" );
            builder.add( ctakesSentence );
//            mLogger.info( "Loading Sentence deliminator patch" );
//            AnalysisEngineDescription postSentPatch = AnalysisEngineFactory.createEngineDescription(
//                    delimPatcher.class
//                    );
//            builder.add(postSentPatch);
        }
 
        ///////////////////////////////////////////////////
        // Tokenizers
        ///////////////////////////////////////////////////
        String conceptMapper_token_type = null;
        File tmpTokenizerDescription = null;
        if( pipeline_modules.contains( "OpenNLP Tokenizer" ) ){
            mLogger.info( "Loading OpenNLP's en-token and en-pos-maxent models with aggressive patch" );
            conceptMapper_token_type = "org.apache.ctakes.typesystem.type.syntax.BaseToken";
            AnalysisEngineDescription openNlpTokenizer = AnalysisEngineFactory.createEngineDescription(
                    OpenNlpTokenizer.class ,
                    OpenNlpTokenizer.PARAM_MODELPATH , "resources/openNlpModels/" ,
                    OpenNlpTokenizer.PARAM_TOKENIZERMODEL , "en-token.bin" ,
                    OpenNlpTokenizer.PARAM_TOKENIZERPATCH , "aggressive" ,
                    OpenNlpTokenizer.PARAM_POSMODEL , "en-pos-maxent.bin" );
            tmpTokenizerDescription = File.createTempFile("prefix_", "_suffix");
            tmpTokenizerDescription.deleteOnExit();
            try {
                openNlpTokenizer.toXML(new FileWriter(tmpTokenizerDescription));
            } catch (SAXException e) {
                // TODO - add something here
            }
            builder.add( openNlpTokenizer );
//        } else {
//            mLogger.error( "Unrecognized tokenizer option: " + pipeline_tokenizer );
        }

        ////////////////////////////////////////////////////
        // Sectionizer
        ///////////////////////////////////////////////////
        if( pipeline_modules.contains( "Template Sectionizer" ) ){
            String template_file = "";
            AnalysisEngineDescription templateSectionizer = null;
            if( pipeline_properties.containsKey( "sectionizer.template_file" ) ){
                template_file = pipeline_properties.getProperty( "sectionizer.template_file" );
            }
            if (template_file == null || template_file.equals("")) {
                mLogger.info( "Loading module 'TemplateSectionizer' with default values" );
                templateSectionizer = AnalysisEngineFactory.createEngineDescription(
                        TemplateSectionizer.class );
            }else {
                mLogger.info( "Loading module 'TemplateSectionizer' with " + template_file );
                templateSectionizer = AnalysisEngineFactory.createEngineDescription(
                        TemplateSectionizer.class,
                        TemplateSectionizer.PARAM_SECTIONTEMPLATES , template_file);
            }
            builder.add(templateSectionizer );         
        } else {
            mLogger.warn( "No known sectionizer provided" );
        }

        ////////////////////////////////////
        // Patient Demographics
        ////////////////////////////////////
        if( pipeline_modules.contains( "Demographics" ) ){
            mLogger.info( "Loading module 'Demographics'" );
            AnalysisEngineDescription patientDemographics = AnalysisEngineFactory.createEngineDescription(
                    PatientDemographics.class ,
                    PatientDemographics.PARAM_VERSION , mVersion );
            builder.add( patientDemographics );
        }

        ////////////////////////////////////////////////////////////////////////
        // ConceptMapper pipeline adapted from example by Luca Foppiano:
        // - https://github.com/lfoppiano/uima-fit-sample-pipeline
        // - https://github.com/lfoppiano/uima-fit-sample-pipeline/blob/master/src/main/java/org/foppiano/uima/fit/tutorial/Pipeline2.java
        String[] conceptFeatureList = new String[]{ "PreferredTerm" ,
                "TUI",
                "ConceptCode","ConceptType" ,
                "BasicLevelConceptCode","BasicLevelConceptType" };
        String[] conceptAttributeList = new String[]{ "canonical" ,
                "umlsTui",
                "conceptCode" , "conceptType" ,
                "basicLevelConceptCode" , "basicLevelConceptType" };

        ////////////////////////////////////
        // Dynamically Loaded ConceptMapper Dictionaries
        if( pipeline_modules.contains( "Dynamic ConceptMapper" ) ){
            mLogger.info( "Checking for dynamically defined ConceptMapper files" );
            for( Entry<Object, Object> entry : pipeline_properties.entrySet() ) {
                String property_key = (String) entry.getKey();
                if( property_key.startsWith( "conceptMapper.dictionaryPath" ) ) {
                    String dictionary_name = property_key.substring( "conceptMapper.dictionaryPath".length() );
                    if( dictionary_name.startsWith( "." ) ){
                        dictionary_name = dictionary_name.substring( 1 );
                    }
                    String dictionary_path = pipeline_properties.getProperty( property_key );
                    //
                    mLogger.info( "Loading module '" + dictionary_name + "ConceptMapper' (" + 
                            dictionary_path + ")" );
                    AnalysisEngineDescription dynamicConceptMapper = AnalysisEngineFactory.createEngineDescription(
                            ConceptMapper.class,
                            "TokenizerDescriptorPath", tmpTokenizerDescription.getAbsolutePath(),
                            "LanguageID", "en",
                            //                        ConceptMapper.PARAM_ORDERINDEPENDENTLOOKUP , "" , // boolean
                            //                        ConceptMapper.PARAMVALUE_CONTIGUOUSMATCH , "" , // String
                            //                        ConceptMapper.PARAMVALUE_SKIPANYMATCH , "" , // String
                            //                        ConceptMapper.PARAMVALUE_SKIPANYMATCHALLOWOVERLAP , "" , // String
                            ConceptMapper.PARAM_SEARCHSTRATEGY , "ContiguousMatch" , // String
                            ConceptMapper.PARAM_FINDALLMATCHES , false , // String
                            ConceptMapper.PARAM_TOKENANNOTATION, conceptMapper_token_type ,
                            ConceptMapper.PARAM_ANNOTATION_NAME, "org.apache.uima.conceptMapper.UmlsTerm",
                            "SpanFeatureStructure", "uima.tcas.DocumentAnnotation",
                            ConceptMapper.PARAM_FEATURE_LIST, conceptFeatureList ,
                            ConceptMapper.PARAM_ATTRIBUTE_LIST, conceptAttributeList
                            );
                    createDependencyAndBind( 
                            dynamicConceptMapper , 
                            "DictionaryFile" , 
                            DictionaryResource_impl.class , 
                            "file:" + dictionary_path );
                    builder.add( dynamicConceptMapper );
                }
            }
        }

        ////////////////////////////////////
        // LabPyAnnotator
        if( pipeline_modules.contains( "Labs" ) ){
            String labs_host = "localhost";
            if( pipeline_properties.containsKey( "py.labs.host" ) ){
                labs_host = pipeline_properties.getProperty( "py.labs.host" );
            }
            int labs_port = 4454;
            if( pipeline_properties.containsKey( "py.labs.port" ) ){
                labs_port = Integer.valueOf( pipeline_properties.getProperty( "py.labs.port" ) );
            }    
            String keystore_file = "resources/keystore.jks";
            if( pipeline_properties.containsKey( "py.labs.keyfile" ) ){
                keystore_file = pipeline_properties.getProperty( "py.labs.keyfile" );
            }
            mLogger.info( "Loading module 'labPyAnnotator'" );
            AnalysisEngineDescription labPyAnnotator = AnalysisEngineFactory.createEngineDescription(
                    PyAnnotatorViaSSL.class ,
                    PyAnnotatorViaSSL.PARAM_HOSTNAME , labs_host ,
                    PyAnnotatorViaSSL.PARAM_PORTNUMBER , labs_port ,
                    PyAnnotatorViaSSL.PARAM_KEYFILE , keystore_file );
            builder.add( labPyAnnotator );
        }       
        ////////////////////////////////////
        // MedPyAnnotator
        if( pipeline_modules.contains( "Meds" ) ){
            String meds_host = "localhost";
            if( pipeline_properties.containsKey( "py.meds.host" ) ){
                meds_host = pipeline_properties.getProperty( "py.meds.host" );
            }
            int meds_port = 4455;
            if( pipeline_properties.containsKey( "py.meds.port" ) ){
                meds_port = Integer.valueOf( pipeline_properties.getProperty( "py.meds.port" ) );
            }
            String keystore_file = "resources/keystore.jks";
            if( pipeline_properties.containsKey( "py.labs.keyfile" ) ){
                keystore_file = pipeline_properties.getProperty( "py.meds.keyfile" );
            }   
            mLogger.info( "Loading module 'medPyAnnotator'" );
            AnalysisEngineDescription medPyAnnotator = AnalysisEngineFactory.createEngineDescription(
                    PyAnnotatorViaSSL.class ,
                    PyAnnotatorViaSSL.PARAM_HOSTNAME , meds_host ,
                    PyAnnotatorViaSSL.PARAM_PORTNUMBER , meds_port ,
                    PyAnnotatorViaSSL.PARAM_KEYFILE , keystore_file );
            builder.add( medPyAnnotator );
        }
        ////////////////////////////////////
        if( pipeline_modules.contains( "Labs" ) ||
        	pipeline_modules.contains( "Meds" ) ){
    		////////////////////////////////////
    		// CUIAnnotator
        	if( pipeline_modules.contains( "NEN" ) ){
                mLogger.info( "Loading module 'cuiAnnotator'" );
            		AnalysisEngineDescription cuiAnnotator = AnalysisEngineFactory.createEngineDescription(
            				CUIAnnotator.class ,
            				CUIAnnotator.PARAM_LABDICFILE , "resources/MRCONSO.RRF" ,
            				CUIAnnotator.PARAM_DBSTYFILE , "resources/MRSTY.RRF" ,
            				CUIAnnotator.PARAM_MEDDICFILE , "resources/MRCONSO_f.RRF" );
        			builder.add( cuiAnnotator );
            }
            ////////////////////////////////////
            // RelAnnotator
        	if( pipeline_modules.contains( "Attributes" ) ){
                mLogger.info( "Loading module 'relationsAnnotator'" );
        		AnalysisEngineDescription relationsAnnotator = AnalysisEngineFactory.createEngineDescription(
        				RelAnnotator.class );
        		builder.add( relationsAnnotator );
        	}
        }

        ////////////////////////////////////////////////////////////////////////
        // Contextual Attribute Extractors
        // - These modules pre-suppose all concept extraction has already
        //   occurred
        // - TODO : These modules also only target UmlsTerm annotations. We
        //          need to extend or convert this to support IdentifiedAnnotation
        //          annotations to pull in SHARPn annotations.
        
        ////////////////////////////////////
        // ConText
        if( pipeline_modules.contains( "ConText" ) ){
            mLogger.info( "Loading module 'ConText'" );
            String context_log_file = "";
            if( pipeline_properties.containsKey( "context.log_file" ) ){
                context_log_file = pipeline_properties.getProperty( "context.log_file" );
            }
            AnalysisEngineDescription conText = AnalysisEngineFactory.createEngineDescription(
                    ContextAnnotator.class,
                    ContextAnnotator.PARAM_SENTENCETYPE, sentence_type ,
                    ConText.PARAM_NEGEX_PHRASE_FILE , "resources/dict/ConText_rules.txt" ,
                    ConText.PARAM_CONTEXT_LOG , context_log_file
                    );
            builder.add( conText );
        }
        
        ////////////////////////////////////
        // ConText
        if( pipeline_modules.contains( "FastContext" ) ){
            mLogger.info( "Loading module 'FastContext'" );
            String ruleFile = null;
            if( pipeline_properties.containsKey( "fastcontext.rulefile" ) ){
                ruleFile = pipeline_properties.getProperty( "fastcontext.rulefile" );
            }
            AnalysisEngineDescription fastContext = AnalysisEngineFactory.createEngineDescription(
                    FastContextAnnotator.class,
                    FastContextAnnotator.PARAM_SENTENCETYPE, sentence_type , 
//                  FastContextAnnotator.PARAM_CLASSMAP , classMap ,
                    FastContextAnnotator.PARAM_RULEFILE , ruleFile
                    );
            builder.add( fastContext );
        }
        
        // END Contextual Attribute Extractors
        ////////////////////////////////////////////////////////////////////////


        ////////////////////////////////////
        // Initialize annotated text writer
        ////////////////////////////////////
        if( pipeline_modules.contains( "Annotated Text Out" ) ){
            // The default values for these output directories are
            // determined by whether it is a test run or a 
            // production run...
            String txt_output_dir = "";
            String txt_error_dir = "";
            if( mTestFlag ){
                mLogger.info( "Loading module 'txtWriter' for test" );
                txt_output_dir = "/tmp/decovri/test_out";
                txt_error_dir = "/tmp/decovri/test_error";
            } else {
                mLogger.info( "Loading module 'txtWriter' for production" );
                txt_output_dir = "/data/software/Decovri/data/out/v" + mVersion;
                txt_error_dir = "/data/software/Decovri/data/out/error";
            }
            // ...However, these values are overwritten if set
            // in the pipeline.properties file
            if( pipeline_properties.containsKey( "fs.out.txt" ) ){
                txt_output_dir = pipeline_properties.getProperty( "fs.out.txt" );
                if (txt_output_dir.startsWith("~")){
                    mLogger.warn("Output (.txt) directory starts with '~', did you mean to use your full home path?");
                }
                mLogger.debug( "Setting annotated txt output directory: " + txt_output_dir );
            }
            if( pipeline_properties.containsKey( "fs.error.txt" ) ){
                txt_error_dir = pipeline_properties.getProperty( "fs.error.txt" );
                mLogger.debug( "Setting annotated txt error directory: " + txt_error_dir );
            }
            AnalysisEngineDescription txtWriterTest = AnalysisEngineFactory.createEngineDescription(
                    AnnotatedTextWriter.class , 
                    AnnotatedTextWriter.PARAM_OUTPUTDIR , txt_output_dir ,
                    AnnotatedTextWriter.PARAM_ERRORDIR , txt_error_dir );
                	builder.add( txtWriterTest );
        }

        ////////////////////////////////////
        // Initialize XMI and tsv writer
        AnalysisEngineDescription txtSectionWriter = null;
        if( pipeline_modules.contains( "Annotated Section Writer" ) ){
            // The default values for these output directories are
            // determined by whether it is a test run or a 
            // production run...
            String section_output_dir = "";
            String section_error_dir = "";
            if( mTestFlag ){
                mLogger.info( "Loading module 'txtSectionWriter' for test" );
                section_output_dir = "/tmp/decovri/test_out";
                section_error_dir = "/tmp/decovri/test_error";
            } else {
                mLogger.info( "Loading module 'txtSectionWriter' for production" );
                section_output_dir = "/data/software/Decovri/data/out/v" + mVersion;
                section_error_dir = "/data/software/Decovri/data/out/error";
            }
            // ...However, these values are overwritten if set
            // in the pipeline.properties file
            if( pipeline_properties.containsKey( "fs.out.sections" ) ){
                section_output_dir = pipeline_properties.getProperty( "fs.out.sections" );
                mLogger.debug( "Setting annotated txt output directory: " + section_output_dir );
            }
            if( pipeline_properties.containsKey( "fs.error.sections" ) ){
                section_error_dir = pipeline_properties.getProperty( "fs.error.sections" );
                mLogger.debug( "Setting annotated txt error directory: " + section_error_dir );
            }
            // Then we use these values to construct our writer
            txtSectionWriter = AnalysisEngineFactory.createEngineDescription(
                    AnnotatedSectionWriter.class , 
                    AnnotatedSectionWriter.PARAM_OUTPUTDIR , section_output_dir ,
                    AnnotatedSectionWriter.PARAM_ERRORDIR , section_error_dir );
            if( ! mTestFlag ){
                builder.add( txtSectionWriter );
            }
        }
        
        ////////////////////////////////////
        // Initialize XMI and tsv writer
        AnalysisEngineDescription xmlWriter = null;
        if( pipeline_modules.contains( "XML Out" ) ){
            // The default values for these output directories are
            // determined by whether it is a test run or a 
            // production run...
            String xml_output_dir = "";
            String xml_error_dir = "";
            if( mTestFlag ){
        	    mLogger.info( "Loading module 'xmlWriter' for test" );
        	    xml_output_dir = "/tmp/decovri/test_out";
        	    xml_error_dir = "/tmp/decovri/test_error";
        	} else {
        	    mLogger.info( "Loading module 'xmlWriter' for production" );
                xml_output_dir = "/data/software/Decovri/data/out/v" + mVersion;
                xml_error_dir = "/data/software/Decovri/data/out/error";
        	}
            // ...However, these values are overwritten if set
            // in the pipeline.properties file
            if( pipeline_properties.containsKey( "fs.out.xmi" ) ){
                xml_output_dir = pipeline_properties.getProperty( "fs.out.xmi" );
                if (xml_output_dir.startsWith("~")){
                    mLogger.warn("Output directory(.xml) starts with '~', did you mean to use your full home path?");
                }
                mLogger.debug( "Setting XML output directory: " + xml_output_dir );
            }
            if( pipeline_properties.containsKey( "fs.error.xmi" ) ){
                xml_error_dir = pipeline_properties.getProperty( "fs.error.xmi" );
                mLogger.debug( "Setting XML error directory: " + xml_error_dir );
            }
            // Then we use these values to construct our writer
            xmlWriter = AnalysisEngineFactory.createEngineDescription(
                    XmlWriter.class , 
                    XmlWriter.PARAM_OUTPUTDIR , xml_output_dir ,
                    XmlWriter.PARAM_ERRORDIR , xml_error_dir );
            if( ! mTestFlag ){
                builder.add( xmlWriter );
            }
        }
        if( pipeline_modules.contains( "OMOP CDM Writer" ) ){
            Boolean write_to_disk_flag = true;
        	if( mTestFlag ){
                mLogger.info( "Loading module 'dataMartWriter' for test" );
        	    write_to_disk_flag = true;
        	} else {
                mLogger.info( "Loading module 'dataMartWriter' for production" );
        	    write_to_disk_flag = false;
        	}
            AnalysisEngineDescription dataMartWriter = AnalysisEngineFactory.createEngineDescription(
                    OMOP_CDM_CASConsumer.class, 
                    OMOP_CDM_CASConsumer.PARAM_VERSION , mVersion ,
                    OMOP_CDM_CASConsumer.PARAM_AUTOINCREMENT , false ,
                    OMOP_CDM_CASConsumer.PARAM_DBCONNECTION , database_properties_filename ,
                    OMOP_CDM_CASConsumer.PARAM_WRITETODISK , write_to_disk_flag );
            builder.add( dataMartWriter );
        }
        // If we're running in test mode, we actually want to run the database
        // writer prior to the XMI writer. This allows the database writer to do
        // any special cleaning and filtering prior to writing out the XMI
        // (usually for evaluation).
        if( pipeline_modules.contains( "Annotated Section Writer" ) && mTestFlag ){
            builder.add( txtSectionWriter );
        }
        if( pipeline_modules.contains( "XML Out" ) && mTestFlag ){
            builder.add( xmlWriter );
        }

    	SimplePipeline.runPipeline( collectionReader , builder.createAggregateDescription() );
        
    }

    private static void help( Options options ) {
    	// This prints out some help
    	HelpFormatter formater = new HelpFormatter();
    	formater.printHelp( "edu.musc.tbic.uima.Decovri" , options );
    		
    	System.exit(0);
    }	
    
    public void process(JCas arg0) throws AnalysisEngineProcessException {
        // TODO Auto-generated method stub
        
    }
}
