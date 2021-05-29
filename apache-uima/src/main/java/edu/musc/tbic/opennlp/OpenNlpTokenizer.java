package edu.musc.tbic.opennlp;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

import edu.musc.tbic.uima.Decovri;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.Span;

import org.apache.ctakes.typesystem.type.syntax.BaseToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class OpenNlpTokenizer extends JCasAnnotator_ImplBase {
    
    private static final Logger mLogger = LogManager.getLogger( Decovri.class );

    /**
     * Name of directory containing all models. The default value of the
     * empty string can be used if each model needs to have a different
     * base directory
     */
    public static final String PARAM_MODELPATH = "mModelPath";
    @ConfigurationParameter( name = PARAM_MODELPATH , 
                             description = "Path containing all models to load" , 
                             mandatory = false )
    private String mModelPath;
    /**
     * Name of model file used for tokenization
     */
    public static final String PARAM_TOKENIZERMODEL = "mTokenizerModel";
    @ConfigurationParameter( name = PARAM_TOKENIZERMODEL , 
                             description = "Tokenizer model filename to load" , 
                             mandatory = true )
    private String mTokenizerModel;
    /**
     * Additional tokenization patch to run
     */
    public static final String PARAM_TOKENIZERPATCH = "mTokenizerPatch";
    @ConfigurationParameter( name = PARAM_TOKENIZERPATCH , 
                             description = "A patch function to run after the tokenizer model" , 
                             mandatory = false )
    private String mTokenizerPatch;
    /**
     * Name of model file used for part-of-speech tagging
     */
    public static final String PARAM_POSMODEL = "mPOSModel";
    @ConfigurationParameter( name = PARAM_POSMODEL , 
                             description = "Part-of-speech model filename to load" , 
                             mandatory = false )
    private String mPOSModel;
    
    private TokenizerME mTokenizer;
    private POSTaggerME mPosTagger;

    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize( context );
        //
        if( context.getConfigParameterValue( "mModelPath" ) == null ){
            mModelPath = "";
        } else {
            mModelPath = (String) context.getConfigParameterValue( "mModelPath" );
            // Add a final slash to the directory ending in case it wasn't provided
            if( ! mModelPath.substring( mModelPath.length() - 1 ).equals( "/" ) ){
                mModelPath = mModelPath + "/";
            }
        }
        //
        mTokenizerModel = (String) context.getConfigParameterValue( "mTokenizerModel" );
        //
        if( context.getConfigParameterValue( "mTokenizerPatch" ) == null ){
            mTokenizerPatch = "";
        } else {
            mTokenizerPatch = (String) context.getConfigParameterValue( "mTokenizerPatch" );
        }
        //
        if( context.getConfigParameterValue( "mPOSModel" ) == null ){
            mPOSModel = "";
        } else {
            mPOSModel = (String) context.getConfigParameterValue( "mPOSModel" );
        }
        
        try {
            TokenizerModel tmd = new TokenizerModel(new File( mModelPath + mTokenizerModel ) );
            mTokenizer = new TokenizerME(tmd);
            mLogger.debug("OpenNLP tokenizer model loaded");
            if( ! mPOSModel.equals( "" ) ){
                POSModel pmd = new POSModel(new File( mModelPath + mPOSModel ) );
                mPosTagger = new POSTaggerME(pmd);
                mLogger.debug( "OpenNLP part-of-speech model loaded" );                
            }
        } catch (IOException e) {
            mLogger.throwing(e);
            throw new ResourceInitializationException(e);
        }
        mLogger.debug("OpenNlpAnnotator Initialized");
    }

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        mLogger.debug("OpenNlpAnnotator Begin");
        
        int tokenNumber = 0;
        FSIterator<?> it = jCas.getAnnotationIndex( Sentence.type ).iterator();
        if( it.hasNext() ){
            while( it.hasNext() ){
                Sentence sentAnnot = (Sentence) it.next();
                String sentence = sentAnnot.getCoveredText();
                tokenNumber += splitIntoTokens( jCas , sentence , 
                        sentAnnot.getBegin() , tokenNumber );
            }
        } else {
            splitIntoTokens( jCas , jCas.getDocumentText() , 0 , 0 );
        }
    }
        
    private int splitIntoTokens( JCas jCas , String spanToSplit , 
                                 int sentStart , int tokenNumber ){
        Span[] tokSpans = mTokenizer.tokenizePos( spanToSplit );

        ArrayList<Span> nSs = new ArrayList<>();
        if( mTokenizerPatch.equals( "aggressive" ) ){
            //aggressive tokenization
            for( Span tokSpan : tokSpans ) {
                aggressiveTokenSplitter( tokSpan , nSs , spanToSplit );
            }
        } else {
            // Default is to convert the array of spans to an
            // ArrayList of spans.  Not super interesting in
            // the NOOP instance but a useful conversion if you
            // need to support additional splitting and merging
            for( Span tokSpan : tokSpans ) {
                noopTokenTemplate( tokSpan , nSs , spanToSplit );
            }            
        }

        String[] tokens = new String[nSs.size()];
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = nSs.get(i).getCoveredText( spanToSplit ).toString();
        }

        String[] tags = null;
        if( ! mPOSModel.equals( "" ) ){
            tags = mPosTagger.tag( tokens );
        }
        addBaseTokens(jCas, tags, nSs, sentStart , tokenNumber );
        
        return tokens.length;
    }

    private void addBaseTokens(JCas jCas, String[] tags, ArrayList<Span> nSs, int b, int tokenNumber ) {
        boolean hasPosTags = true;
        if( tags == null ){
            hasPosTags = false;
        }
        for (int i = 0; i < nSs.size(); i++) {
            Span tok = nSs.get(i);
            BaseToken t = new BaseToken(jCas, tok.getStart() + b, tok.getEnd() + b);
            t.setTokenNumber( tokenNumber + i );
            if( hasPosTags ){
                t.setPartOfSpeech( tags[ i ] );
            }
            t.addToIndexes();
        }
    }

    private void noopTokenTemplate( Span tok , ArrayList<Span> list , String sentence ) {
        list.add( tok );
    }

    private void aggressiveTokenSplitter(Span tok, ArrayList<Span> list, String sentence) {

        String str = tok.getCoveredText(sentence).toString();
        char pCh = str.charAt(0);
        int s = 0;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            boolean ifC = false;

            if (Character.isDigit(ch) && i > 0) {
                if (!Character.isDigit(pCh)) {
                    ifC = true;
                }
            } else if (Character.isLowerCase(ch) && i > 0) { // 0 - A // a  //ALMartial -> AL Martial
                if (!(Character.isLowerCase(pCh) || Character.isUpperCase(pCh))) {
                    ifC = true;
                }
            } else if (Character.isUpperCase(ch) && i > 0) { // 0 a - // A  A
                if (!Character.isUpperCase(pCh) || (i < str.length() - 1 && Character.isLowerCase(str.charAt(i + 1)))) {
                    ifC = true;
                }
            } else if (i > 0) {
                if (Character.isDigit(pCh) || Character.isLowerCase(pCh) || Character.isUpperCase(pCh) || Character.isWhitespace(pCh)) {
                    ifC = true;
                }
            }

            if (ifC && !Character.isWhitespace(ch)) {
                //split s i
                int b = s + tok.getStart();
                int e = i + tok.getStart();
                if (Character.isWhitespace(pCh)) {
                    e = i + tok.getStart() - 1;
                }
                Span sp = new Span(b, e);
                list.add(sp);
                s = i;
            }
            pCh = ch;
        }

        int b = s + tok.getStart();
        int e = tok.getStart() + str.length();
        Span sp = new Span(b, e);
        list.add(sp);
    }
}
