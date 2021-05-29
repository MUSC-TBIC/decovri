

/* First created by JCasGen Fri May 28 19:40:41 EDT 2021 */
package edu.musc.tbic.omop_cdm;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** A datastructure for storing all the properties
      associated with NLP extracted information in the NOTE_NLP
      table
 * Updated by JCasGen Fri May 28 19:40:45 EDT 2021
 * XML source: /Users/pmh/git/decovri/apache-uima/src/main/resources/desc/types/Note_Nlp_TableProperties.xml
 * @generated */
public class Note_Nlp_TableProperties extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Note_Nlp_TableProperties.class);
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
  protected Note_Nlp_TableProperties() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Note_Nlp_TableProperties(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Note_Nlp_TableProperties(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Note_Nlp_TableProperties(JCas jcas, int begin, int end) {
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
  //* Feature: note_nlp_id

  /** getter for note_nlp_id - gets A unique identifier for each note.
   * @generated
   * @return value of the feature 
   */
  public String getNote_nlp_id() {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_note_nlp_id == null)
      jcasType.jcas.throwFeatMissing("note_nlp_id", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_note_nlp_id);}
    
  /** setter for note_nlp_id - sets A unique identifier for each note. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setNote_nlp_id(String v) {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_note_nlp_id == null)
      jcasType.jcas.throwFeatMissing("note_nlp_id", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    jcasType.ll_cas.ll_setStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_note_nlp_id, v);}    
   
    
  //*--------------*
  //* Feature: note_id

  /** getter for note_id - gets A unique identifier for each note.
   * @generated
   * @return value of the feature 
   */
  public String getNote_id() {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_note_id == null)
      jcasType.jcas.throwFeatMissing("note_id", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_note_id);}
    
  /** setter for note_id - sets A unique identifier for each note. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setNote_id(String v) {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_note_id == null)
      jcasType.jcas.throwFeatMissing("note_id", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    jcasType.ll_cas.ll_setStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_note_id, v);}    
   
    
  //*--------------*
  //* Feature: section_concept_id

  /** getter for section_concept_id - gets A foreign key to the predefined Concept in the
          Standardized Vocabularies representing the section of the
          extracted term.
   * @generated
   * @return value of the feature 
   */
  public int getSection_concept_id() {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_section_concept_id == null)
      jcasType.jcas.throwFeatMissing("section_concept_id", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_section_concept_id);}
    
  /** setter for section_concept_id - sets A foreign key to the predefined Concept in the
          Standardized Vocabularies representing the section of the
          extracted term. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSection_concept_id(int v) {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_section_concept_id == null)
      jcasType.jcas.throwFeatMissing("section_concept_id", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    jcasType.ll_cas.ll_setIntValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_section_concept_id, v);}    
   
    
  //*--------------*
  //* Feature: snippet

  /** getter for snippet - gets A small window of text surrounding the term.
   * @generated
   * @return value of the feature 
   */
  public String getSnippet() {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_snippet == null)
      jcasType.jcas.throwFeatMissing("snippet", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_snippet);}
    
  /** setter for snippet - sets A small window of text surrounding the term. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSnippet(String v) {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_snippet == null)
      jcasType.jcas.throwFeatMissing("snippet", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    jcasType.ll_cas.ll_setStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_snippet, v);}    
   
    
  //*--------------*
  //* Feature: offset

  /** getter for offset - gets Character offset of the extracted term in the
          input note.
   * @generated
   * @return value of the feature 
   */
  public String getOffset() {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_offset == null)
      jcasType.jcas.throwFeatMissing("offset", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_offset);}
    
  /** setter for offset - sets Character offset of the extracted term in the
          input note. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setOffset(String v) {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_offset == null)
      jcasType.jcas.throwFeatMissing("offset", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    jcasType.ll_cas.ll_setStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_offset, v);}    
   
    
  //*--------------*
  //* Feature: lexical_variant

  /** getter for lexical_variant - gets Raw text extracted from the NLP tool.
   * @generated
   * @return value of the feature 
   */
  public String getLexical_variant() {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_lexical_variant == null)
      jcasType.jcas.throwFeatMissing("lexical_variant", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_lexical_variant);}
    
  /** setter for lexical_variant - sets Raw text extracted from the NLP tool. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setLexical_variant(String v) {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_lexical_variant == null)
      jcasType.jcas.throwFeatMissing("lexical_variant", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    jcasType.ll_cas.ll_setStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_lexical_variant, v);}    
   
    
  //*--------------*
  //* Feature: note_nlp_concept_id

  /** getter for note_nlp_concept_id - gets A foreign key to the predefined Concept in the
          Standardized Vocabularies reflecting the normalized concept
          for the extracted term. Domain of the term is represented as
          part of the Concept table.
   * @generated
   * @return value of the feature 
   */
  public int getNote_nlp_concept_id() {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_note_nlp_concept_id == null)
      jcasType.jcas.throwFeatMissing("note_nlp_concept_id", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_note_nlp_concept_id);}
    
  /** setter for note_nlp_concept_id - sets A foreign key to the predefined Concept in the
          Standardized Vocabularies reflecting the normalized concept
          for the extracted term. Domain of the term is represented as
          part of the Concept table. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setNote_nlp_concept_id(int v) {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_note_nlp_concept_id == null)
      jcasType.jcas.throwFeatMissing("note_nlp_concept_id", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    jcasType.ll_cas.ll_setIntValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_note_nlp_concept_id, v);}    
   
    
  //*--------------*
  //* Feature: note_nlp_source_concept_id

  /** getter for note_nlp_source_concept_id - gets A foreign key to a Concept that refers to the
          code in the source vocabulary used by the NLP
          system
   * @generated
   * @return value of the feature 
   */
  public String getNote_nlp_source_concept_id() {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_note_nlp_source_concept_id == null)
      jcasType.jcas.throwFeatMissing("note_nlp_source_concept_id", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_note_nlp_source_concept_id);}
    
  /** setter for note_nlp_source_concept_id - sets A foreign key to a Concept that refers to the
          code in the source vocabulary used by the NLP
          system 
   * @generated
   * @param v value to set into the feature 
   */
  public void setNote_nlp_source_concept_id(String v) {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_note_nlp_source_concept_id == null)
      jcasType.jcas.throwFeatMissing("note_nlp_source_concept_id", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    jcasType.ll_cas.ll_setStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_note_nlp_source_concept_id, v);}    
   
    
  //*--------------*
  //* Feature: nlp_system

  /** getter for nlp_system - gets Name and version of the NLP system that
          extracted the term. Useful for data provenance.
   * @generated
   * @return value of the feature 
   */
  public String getNlp_system() {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_nlp_system == null)
      jcasType.jcas.throwFeatMissing("nlp_system", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_nlp_system);}
    
  /** setter for nlp_system - sets Name and version of the NLP system that
          extracted the term. Useful for data provenance. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setNlp_system(String v) {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_nlp_system == null)
      jcasType.jcas.throwFeatMissing("nlp_system", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    jcasType.ll_cas.ll_setStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_nlp_system, v);}    
   
    
  //*--------------*
  //* Feature: nlp_date

  /** getter for nlp_date - gets The date of the note processing. Useful for
          data provenance.
   * @generated
   * @return value of the feature 
   */
  public String getNlp_date() {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_nlp_date == null)
      jcasType.jcas.throwFeatMissing("nlp_date", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_nlp_date);}
    
  /** setter for nlp_date - sets The date of the note processing. Useful for
          data provenance. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setNlp_date(String v) {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_nlp_date == null)
      jcasType.jcas.throwFeatMissing("nlp_date", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    jcasType.ll_cas.ll_setStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_nlp_date, v);}    
   
    
  //*--------------*
  //* Feature: nlp_datetime

  /** getter for nlp_datetime - gets The date and time of the note
          processing. Useful for data provenance.
   * @generated
   * @return value of the feature 
   */
  public String getNlp_datetime() {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_nlp_datetime == null)
      jcasType.jcas.throwFeatMissing("nlp_datetime", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_nlp_datetime);}
    
  /** setter for nlp_datetime - sets The date and time of the note
          processing. Useful for data provenance. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setNlp_datetime(String v) {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_nlp_datetime == null)
      jcasType.jcas.throwFeatMissing("nlp_datetime", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    jcasType.ll_cas.ll_setStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_nlp_datetime, v);}    
   
    
  //*--------------*
  //* Feature: term_exists

  /** getter for term_exists - gets A summary modifier that signifies presence or
          absence of the term for a given patient. Useful for quick
          querying.
   * @generated
   * @return value of the feature 
   */
  public String getTerm_exists() {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_term_exists == null)
      jcasType.jcas.throwFeatMissing("term_exists", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_term_exists);}
    
  /** setter for term_exists - sets A summary modifier that signifies presence or
          absence of the term for a given patient. Useful for quick
          querying. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTerm_exists(String v) {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_term_exists == null)
      jcasType.jcas.throwFeatMissing("term_exists", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    jcasType.ll_cas.ll_setStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_term_exists, v);}    
   
    
  //*--------------*
  //* Feature: term_temporal

  /** getter for term_temporal - gets An optional time modifier associated with the
          extracted term. (for now “past” or “present”
          only). Standardize it later.
   * @generated
   * @return value of the feature 
   */
  public String getTerm_temporal() {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_term_temporal == null)
      jcasType.jcas.throwFeatMissing("term_temporal", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_term_temporal);}
    
  /** setter for term_temporal - sets An optional time modifier associated with the
          extracted term. (for now “past” or “present”
          only). Standardize it later. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTerm_temporal(String v) {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_term_temporal == null)
      jcasType.jcas.throwFeatMissing("term_temporal", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    jcasType.ll_cas.ll_setStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_term_temporal, v);}    
   
    
  //*--------------*
  //* Feature: term_modifiers

  /** getter for term_modifiers - gets A compact description of all the modifiers of
          the specific term extracted by the NLP system. (e.g. “son
          has rash” ? “negated=no,subject=family,
          certainty=undef,conditional=false,general=false”).
   * @generated
   * @return value of the feature 
   */
  public String getTerm_modifiers() {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_term_modifiers == null)
      jcasType.jcas.throwFeatMissing("term_modifiers", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_term_modifiers);}
    
  /** setter for term_modifiers - sets A compact description of all the modifiers of
          the specific term extracted by the NLP system. (e.g. “son
          has rash” ? “negated=no,subject=family,
          certainty=undef,conditional=false,general=false”). 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTerm_modifiers(String v) {
    if (Note_Nlp_TableProperties_Type.featOkTst && ((Note_Nlp_TableProperties_Type)jcasType).casFeat_term_modifiers == null)
      jcasType.jcas.throwFeatMissing("term_modifiers", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    jcasType.ll_cas.ll_setStringValue(addr, ((Note_Nlp_TableProperties_Type)jcasType).casFeatCode_term_modifiers, v);}    
  }

    