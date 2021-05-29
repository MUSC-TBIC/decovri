

/* First created by JCasGen Thu May 27 23:46:26 EDT 2021 */
package org.apache.uima.conceptMapper;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;


/** Annotation for dictionary lookup matches
 * Updated by JCasGen Thu May 27 23:46:26 EDT 2021
 * XML source: /Users/pmh/git/decovri/apache-uima/src/main/resources/desc/types/UmlsTerm.xml
 * @generated */
public class UmlsTerm extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(UmlsTerm.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected UmlsTerm() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public UmlsTerm(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public UmlsTerm(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public UmlsTerm(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: PreferredTerm

  /** getter for PreferredTerm - gets canonical form
   * @generated
   * @return value of the feature 
   */
  public String getPreferredTerm() {
    if (UmlsTerm_Type.featOkTst && ((UmlsTerm_Type)jcasType).casFeat_PreferredTerm == null)
      jcasType.jcas.throwFeatMissing("PreferredTerm", "org.apache.uima.conceptMapper.UmlsTerm");
    return jcasType.ll_cas.ll_getStringValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_PreferredTerm);}
    
  /** setter for PreferredTerm - sets canonical form 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPreferredTerm(String v) {
    if (UmlsTerm_Type.featOkTst && ((UmlsTerm_Type)jcasType).casFeat_PreferredTerm == null)
      jcasType.jcas.throwFeatMissing("PreferredTerm", "org.apache.uima.conceptMapper.UmlsTerm");
    jcasType.ll_cas.ll_setStringValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_PreferredTerm, v);}    
   
    
  //*--------------*
  //* Feature: ConceptCode

  /** getter for ConceptCode - gets UMLS Metathesaurus Concept Code for the matched term
   * @generated
   * @return value of the feature 
   */
  public String getConceptCode() {
    if (UmlsTerm_Type.featOkTst && ((UmlsTerm_Type)jcasType).casFeat_ConceptCode == null)
      jcasType.jcas.throwFeatMissing("ConceptCode", "org.apache.uima.conceptMapper.UmlsTerm");
    return jcasType.ll_cas.ll_getStringValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_ConceptCode);}
    
  /** setter for ConceptCode - sets UMLS Metathesaurus Concept Code for the matched term 
   * @generated
   * @param v value to set into the feature 
   */
  public void setConceptCode(String v) {
    if (UmlsTerm_Type.featOkTst && ((UmlsTerm_Type)jcasType).casFeat_ConceptCode == null)
      jcasType.jcas.throwFeatMissing("ConceptCode", "org.apache.uima.conceptMapper.UmlsTerm");
    jcasType.ll_cas.ll_setStringValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_ConceptCode, v);}    
   
    
  //*--------------*
  //* Feature: ConceptType

  /** getter for ConceptType - gets Source reference
					for concept code (e.g., CUI,
					SNOMEDCT_US, etc.)
   * @generated
   * @return value of the feature 
   */
  public String getConceptType() {
    if (UmlsTerm_Type.featOkTst && ((UmlsTerm_Type)jcasType).casFeat_ConceptType == null)
      jcasType.jcas.throwFeatMissing("ConceptType", "org.apache.uima.conceptMapper.UmlsTerm");
    return jcasType.ll_cas.ll_getStringValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_ConceptType);}
    
  /** setter for ConceptType - sets Source reference
					for concept code (e.g., CUI,
					SNOMEDCT_US, etc.) 
   * @generated
   * @param v value to set into the feature 
   */
  public void setConceptType(String v) {
    if (UmlsTerm_Type.featOkTst && ((UmlsTerm_Type)jcasType).casFeat_ConceptType == null)
      jcasType.jcas.throwFeatMissing("ConceptType", "org.apache.uima.conceptMapper.UmlsTerm");
    jcasType.ll_cas.ll_setStringValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_ConceptType, v);}    
   
    
  //*--------------*
  //* Feature: TUI

  /** getter for TUI - gets UMLS Metathesaurus TUI for the matched term
   * @generated
   * @return value of the feature 
   */
  public String getTUI() {
    if (UmlsTerm_Type.featOkTst && ((UmlsTerm_Type)jcasType).casFeat_TUI == null)
      jcasType.jcas.throwFeatMissing("TUI", "org.apache.uima.conceptMapper.UmlsTerm");
    return jcasType.ll_cas.ll_getStringValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_TUI);}
    
  /** setter for TUI - sets UMLS Metathesaurus TUI for the matched term 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTUI(String v) {
    if (UmlsTerm_Type.featOkTst && ((UmlsTerm_Type)jcasType).casFeat_TUI == null)
      jcasType.jcas.throwFeatMissing("TUI", "org.apache.uima.conceptMapper.UmlsTerm");
    jcasType.ll_cas.ll_setStringValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_TUI, v);}    
   
    
  //*--------------*
  //* Feature: BasicLevelConceptCode

  /** getter for BasicLevelConceptCode - gets UMLS Metathesaurus Concept Code for the basic level term the matched terms fits within
   * @generated
   * @return value of the feature 
   */
  public String getBasicLevelConceptCode() {
    if (UmlsTerm_Type.featOkTst && ((UmlsTerm_Type)jcasType).casFeat_BasicLevelConceptCode == null)
      jcasType.jcas.throwFeatMissing("BasicLevelConceptCode", "org.apache.uima.conceptMapper.UmlsTerm");
    return jcasType.ll_cas.ll_getStringValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_BasicLevelConceptCode);}
    
  /** setter for BasicLevelConceptCode - sets UMLS Metathesaurus Concept Code for the basic level term the matched terms fits within 
   * @generated
   * @param v value to set into the feature 
   */
  public void setBasicLevelConceptCode(String v) {
    if (UmlsTerm_Type.featOkTst && ((UmlsTerm_Type)jcasType).casFeat_BasicLevelConceptCode == null)
      jcasType.jcas.throwFeatMissing("BasicLevelConceptCode", "org.apache.uima.conceptMapper.UmlsTerm");
    jcasType.ll_cas.ll_setStringValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_BasicLevelConceptCode, v);}    
   
    
  //*--------------*
  //* Feature: BasicLevelConceptType

  /** getter for BasicLevelConceptType - gets Source reference
					for the basic level concept
					code (e.g., CUI,
					SNOMEDCT_US, etc.)
   * @generated
   * @return value of the feature 
   */
  public String getBasicLevelConceptType() {
    if (UmlsTerm_Type.featOkTst && ((UmlsTerm_Type)jcasType).casFeat_BasicLevelConceptType == null)
      jcasType.jcas.throwFeatMissing("BasicLevelConceptType", "org.apache.uima.conceptMapper.UmlsTerm");
    return jcasType.ll_cas.ll_getStringValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_BasicLevelConceptType);}
    
  /** setter for BasicLevelConceptType - sets Source reference
					for the basic level concept
					code (e.g., CUI,
					SNOMEDCT_US, etc.) 
   * @generated
   * @param v value to set into the feature 
   */
  public void setBasicLevelConceptType(String v) {
    if (UmlsTerm_Type.featOkTst && ((UmlsTerm_Type)jcasType).casFeat_BasicLevelConceptType == null)
      jcasType.jcas.throwFeatMissing("BasicLevelConceptType", "org.apache.uima.conceptMapper.UmlsTerm");
    jcasType.ll_cas.ll_setStringValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_BasicLevelConceptType, v);}    
   
    
  //*--------------*
  //* Feature: enclosingSpan

  /** getter for enclosingSpan - gets span that this NoTerm is contained within (i.e. its sentence)
   * @generated
   * @return value of the feature 
   */
  public Annotation getEnclosingSpan() {
    if (UmlsTerm_Type.featOkTst && ((UmlsTerm_Type)jcasType).casFeat_enclosingSpan == null)
      jcasType.jcas.throwFeatMissing("enclosingSpan", "org.apache.uima.conceptMapper.UmlsTerm");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_enclosingSpan)));}
    
  /** setter for enclosingSpan - sets span that this NoTerm is contained within (i.e. its sentence) 
   * @generated
   * @param v value to set into the feature 
   */
  public void setEnclosingSpan(Annotation v) {
    if (UmlsTerm_Type.featOkTst && ((UmlsTerm_Type)jcasType).casFeat_enclosingSpan == null)
      jcasType.jcas.throwFeatMissing("enclosingSpan", "org.apache.uima.conceptMapper.UmlsTerm");
    jcasType.ll_cas.ll_setRefValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_enclosingSpan, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: matchedText

  /** getter for matchedText - gets 
   * @generated
   * @return value of the feature 
   */
  public String getMatchedText() {
    if (UmlsTerm_Type.featOkTst && ((UmlsTerm_Type)jcasType).casFeat_matchedText == null)
      jcasType.jcas.throwFeatMissing("matchedText", "org.apache.uima.conceptMapper.UmlsTerm");
    return jcasType.ll_cas.ll_getStringValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_matchedText);}
    
  /** setter for matchedText - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setMatchedText(String v) {
    if (UmlsTerm_Type.featOkTst && ((UmlsTerm_Type)jcasType).casFeat_matchedText == null)
      jcasType.jcas.throwFeatMissing("matchedText", "org.apache.uima.conceptMapper.UmlsTerm");
    jcasType.ll_cas.ll_setStringValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_matchedText, v);}    
   
    
  //*--------------*
  //* Feature: matchedTokens

  /** getter for matchedTokens - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getMatchedTokens() {
    if (UmlsTerm_Type.featOkTst && ((UmlsTerm_Type)jcasType).casFeat_matchedTokens == null)
      jcasType.jcas.throwFeatMissing("matchedTokens", "org.apache.uima.conceptMapper.UmlsTerm");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_matchedTokens)));}
    
  /** setter for matchedTokens - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setMatchedTokens(FSArray v) {
    if (UmlsTerm_Type.featOkTst && ((UmlsTerm_Type)jcasType).casFeat_matchedTokens == null)
      jcasType.jcas.throwFeatMissing("matchedTokens", "org.apache.uima.conceptMapper.UmlsTerm");
    jcasType.ll_cas.ll_setRefValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_matchedTokens, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for matchedTokens - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public TOP getMatchedTokens(int i) {
    if (UmlsTerm_Type.featOkTst && ((UmlsTerm_Type)jcasType).casFeat_matchedTokens == null)
      jcasType.jcas.throwFeatMissing("matchedTokens", "org.apache.uima.conceptMapper.UmlsTerm");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_matchedTokens), i);
    return (TOP)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_matchedTokens), i)));}

  /** indexed setter for matchedTokens - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setMatchedTokens(int i, TOP v) { 
    if (UmlsTerm_Type.featOkTst && ((UmlsTerm_Type)jcasType).casFeat_matchedTokens == null)
      jcasType.jcas.throwFeatMissing("matchedTokens", "org.apache.uima.conceptMapper.UmlsTerm");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_matchedTokens), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((UmlsTerm_Type)jcasType).casFeatCode_matchedTokens), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    