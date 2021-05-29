

/* First created by JCasGen Thu May 27 23:45:56 EDT 2021 */
package edu.musc.tbic.uima;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu May 27 23:46:02 EDT 2021
 * XML source: /Users/pmh/git/decovri/apache-uima/src/main/resources/desc/types/NoteSection.xml
 * @generated */
public class NoteSection extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(NoteSection.class);
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
  protected NoteSection() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public NoteSection(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public NoteSection(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public NoteSection(JCas jcas, int begin, int end) {
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
  //* Feature: SectionNumber

  /** getter for SectionNumber - gets 
   * @generated
   * @return value of the feature 
   */
  public int getSectionNumber() {
    if (NoteSection_Type.featOkTst && ((NoteSection_Type)jcasType).casFeat_SectionNumber == null)
      jcasType.jcas.throwFeatMissing("SectionNumber", "edu.musc.tbic.uima.NoteSection");
    return jcasType.ll_cas.ll_getIntValue(addr, ((NoteSection_Type)jcasType).casFeatCode_SectionNumber);}
    
  /** setter for SectionNumber - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSectionNumber(int v) {
    if (NoteSection_Type.featOkTst && ((NoteSection_Type)jcasType).casFeat_SectionNumber == null)
      jcasType.jcas.throwFeatMissing("SectionNumber", "edu.musc.tbic.uima.NoteSection");
    jcasType.ll_cas.ll_setIntValue(addr, ((NoteSection_Type)jcasType).casFeatCode_SectionNumber, v);}    
   
    
  //*--------------*
  //* Feature: SectionDepth

  /** getter for SectionDepth - gets Given an hierarchical section schema, how deep is the current section ( 0 = root level/major category)
   * @generated
   * @return value of the feature 
   */
  public int getSectionDepth() {
    if (NoteSection_Type.featOkTst && ((NoteSection_Type)jcasType).casFeat_SectionDepth == null)
      jcasType.jcas.throwFeatMissing("SectionDepth", "edu.musc.tbic.uima.NoteSection");
    return jcasType.ll_cas.ll_getIntValue(addr, ((NoteSection_Type)jcasType).casFeatCode_SectionDepth);}
    
  /** setter for SectionDepth - sets Given an hierarchical section schema, how deep is the current section ( 0 = root level/major category) 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSectionDepth(int v) {
    if (NoteSection_Type.featOkTst && ((NoteSection_Type)jcasType).casFeat_SectionDepth == null)
      jcasType.jcas.throwFeatMissing("SectionDepth", "edu.musc.tbic.uima.NoteSection");
    jcasType.ll_cas.ll_setIntValue(addr, ((NoteSection_Type)jcasType).casFeatCode_SectionDepth, v);}    
   
    
  //*--------------*
  //* Feature: SectionId

  /** getter for SectionId - gets Type (or concept id) of current section
   * @generated
   * @return value of the feature 
   */
  public String getSectionId() {
    if (NoteSection_Type.featOkTst && ((NoteSection_Type)jcasType).casFeat_SectionId == null)
      jcasType.jcas.throwFeatMissing("SectionId", "edu.musc.tbic.uima.NoteSection");
    return jcasType.ll_cas.ll_getStringValue(addr, ((NoteSection_Type)jcasType).casFeatCode_SectionId);}
    
  /** setter for SectionId - sets Type (or concept id) of current section 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSectionId(String v) {
    if (NoteSection_Type.featOkTst && ((NoteSection_Type)jcasType).casFeat_SectionId == null)
      jcasType.jcas.throwFeatMissing("SectionId", "edu.musc.tbic.uima.NoteSection");
    jcasType.ll_cas.ll_setStringValue(addr, ((NoteSection_Type)jcasType).casFeatCode_SectionId, v);}    
   
    
  //*--------------*
  //* Feature: beginHeader

  /** getter for beginHeader - gets The start offset for this section's header (-1 if no header)
   * @generated
   * @return value of the feature 
   */
  public int getBeginHeader() {
    if (NoteSection_Type.featOkTst && ((NoteSection_Type)jcasType).casFeat_beginHeader == null)
      jcasType.jcas.throwFeatMissing("beginHeader", "edu.musc.tbic.uima.NoteSection");
    return jcasType.ll_cas.ll_getIntValue(addr, ((NoteSection_Type)jcasType).casFeatCode_beginHeader);}
    
  /** setter for beginHeader - sets The start offset for this section's header (-1 if no header) 
   * @generated
   * @param v value to set into the feature 
   */
  public void setBeginHeader(int v) {
    if (NoteSection_Type.featOkTst && ((NoteSection_Type)jcasType).casFeat_beginHeader == null)
      jcasType.jcas.throwFeatMissing("beginHeader", "edu.musc.tbic.uima.NoteSection");
    jcasType.ll_cas.ll_setIntValue(addr, ((NoteSection_Type)jcasType).casFeatCode_beginHeader, v);}    
   
    
  //*--------------*
  //* Feature: endHeader

  /** getter for endHeader - gets The end offset for this section's header (-1 if no header)
   * @generated
   * @return value of the feature 
   */
  public int getEndHeader() {
    if (NoteSection_Type.featOkTst && ((NoteSection_Type)jcasType).casFeat_endHeader == null)
      jcasType.jcas.throwFeatMissing("endHeader", "edu.musc.tbic.uima.NoteSection");
    return jcasType.ll_cas.ll_getIntValue(addr, ((NoteSection_Type)jcasType).casFeatCode_endHeader);}
    
  /** setter for endHeader - sets The end offset for this section's header (-1 if no header) 
   * @generated
   * @param v value to set into the feature 
   */
  public void setEndHeader(int v) {
    if (NoteSection_Type.featOkTst && ((NoteSection_Type)jcasType).casFeat_endHeader == null)
      jcasType.jcas.throwFeatMissing("endHeader", "edu.musc.tbic.uima.NoteSection");
    jcasType.ll_cas.ll_setIntValue(addr, ((NoteSection_Type)jcasType).casFeatCode_endHeader, v);}    
   
    
  //*--------------*
  //* Feature: modifiers

  /** getter for modifiers - gets Modifiers (key/value pairs) associated with the given section
   * @generated
   * @return value of the feature 
   */
  public String getModifiers() {
    if (NoteSection_Type.featOkTst && ((NoteSection_Type)jcasType).casFeat_modifiers == null)
      jcasType.jcas.throwFeatMissing("modifiers", "edu.musc.tbic.uima.NoteSection");
    return jcasType.ll_cas.ll_getStringValue(addr, ((NoteSection_Type)jcasType).casFeatCode_modifiers);}
    
  /** setter for modifiers - sets Modifiers (key/value pairs) associated with the given section 
   * @generated
   * @param v value to set into the feature 
   */
  public void setModifiers(String v) {
    if (NoteSection_Type.featOkTst && ((NoteSection_Type)jcasType).casFeat_modifiers == null)
      jcasType.jcas.throwFeatMissing("modifiers", "edu.musc.tbic.uima.NoteSection");
    jcasType.ll_cas.ll_setStringValue(addr, ((NoteSection_Type)jcasType).casFeatCode_modifiers, v);}    
  }

    