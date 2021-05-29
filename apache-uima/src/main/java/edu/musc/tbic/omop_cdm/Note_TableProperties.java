

/* First created by JCasGen Thu May 27 23:45:34 EDT 2021 */
package edu.musc.tbic.omop_cdm;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** A datastructure for storing all the properties associated with a note in the NOTE table
 * Updated by JCasGen Thu May 27 23:45:34 EDT 2021
 * XML source: /Users/pmh/git/decovri/apache-uima/src/main/resources/desc/types/Note_TableProperties.xml
 * @generated */
public class Note_TableProperties extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Note_TableProperties.class);
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
  protected Note_TableProperties() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Note_TableProperties(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Note_TableProperties(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Note_TableProperties(JCas jcas, int begin, int end) {
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
  //* Feature: note_id

  /** getter for note_id - gets A unique identifier for each note.
   * @generated
   * @return value of the feature 
   */
  public String getNote_id() {
    if (Note_TableProperties_Type.featOkTst && ((Note_TableProperties_Type)jcasType).casFeat_note_id == null)
      jcasType.jcas.throwFeatMissing("note_id", "edu.musc.tbic.omop_cdm.Note_TableProperties");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Note_TableProperties_Type)jcasType).casFeatCode_note_id);}
    
  /** setter for note_id - sets A unique identifier for each note. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setNote_id(String v) {
    if (Note_TableProperties_Type.featOkTst && ((Note_TableProperties_Type)jcasType).casFeat_note_id == null)
      jcasType.jcas.throwFeatMissing("note_id", "edu.musc.tbic.omop_cdm.Note_TableProperties");
    jcasType.ll_cas.ll_setStringValue(addr, ((Note_TableProperties_Type)jcasType).casFeatCode_note_id, v);}    
   
    
  //*--------------*
  //* Feature: person_id

  /** getter for person_id - gets A foreign key identifier to the Person about whom the Note was recorded. The demographic details of that Person are stored in the PERSON table.
   * @generated
   * @return value of the feature 
   */
  public String getPerson_id() {
    if (Note_TableProperties_Type.featOkTst && ((Note_TableProperties_Type)jcasType).casFeat_person_id == null)
      jcasType.jcas.throwFeatMissing("person_id", "edu.musc.tbic.omop_cdm.Note_TableProperties");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Note_TableProperties_Type)jcasType).casFeatCode_person_id);}
    
  /** setter for person_id - sets A foreign key identifier to the Person about whom the Note was recorded. The demographic details of that Person are stored in the PERSON table. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPerson_id(String v) {
    if (Note_TableProperties_Type.featOkTst && ((Note_TableProperties_Type)jcasType).casFeat_person_id == null)
      jcasType.jcas.throwFeatMissing("person_id", "edu.musc.tbic.omop_cdm.Note_TableProperties");
    jcasType.ll_cas.ll_setStringValue(addr, ((Note_TableProperties_Type)jcasType).casFeatCode_person_id, v);}    
   
    
  //*--------------*
  //* Feature: note_date

  /** getter for note_date - gets The date the note was recorded.
   * @generated
   * @return value of the feature 
   */
  public String getNote_date() {
    if (Note_TableProperties_Type.featOkTst && ((Note_TableProperties_Type)jcasType).casFeat_note_date == null)
      jcasType.jcas.throwFeatMissing("note_date", "edu.musc.tbic.omop_cdm.Note_TableProperties");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Note_TableProperties_Type)jcasType).casFeatCode_note_date);}
    
  /** setter for note_date - sets The date the note was recorded. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setNote_date(String v) {
    if (Note_TableProperties_Type.featOkTst && ((Note_TableProperties_Type)jcasType).casFeat_note_date == null)
      jcasType.jcas.throwFeatMissing("note_date", "edu.musc.tbic.omop_cdm.Note_TableProperties");
    jcasType.ll_cas.ll_setStringValue(addr, ((Note_TableProperties_Type)jcasType).casFeatCode_note_date, v);}    
   
    
  //*--------------*
  //* Feature: note_datetime

  /** getter for note_datetime - gets The date and time the note was recorded.
   * @generated
   * @return value of the feature 
   */
  public String getNote_datetime() {
    if (Note_TableProperties_Type.featOkTst && ((Note_TableProperties_Type)jcasType).casFeat_note_datetime == null)
      jcasType.jcas.throwFeatMissing("note_datetime", "edu.musc.tbic.omop_cdm.Note_TableProperties");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Note_TableProperties_Type)jcasType).casFeatCode_note_datetime);}
    
  /** setter for note_datetime - sets The date and time the note was recorded. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setNote_datetime(String v) {
    if (Note_TableProperties_Type.featOkTst && ((Note_TableProperties_Type)jcasType).casFeat_note_datetime == null)
      jcasType.jcas.throwFeatMissing("note_datetime", "edu.musc.tbic.omop_cdm.Note_TableProperties");
    jcasType.ll_cas.ll_setStringValue(addr, ((Note_TableProperties_Type)jcasType).casFeatCode_note_datetime, v);}    
   
    
  //*--------------*
  //* Feature: note_source_value

  /** getter for note_source_value - gets The source value associated with the origin of the Note - varchar(50)
   * @generated
   * @return value of the feature 
   */
  public String getNote_source_value() {
    if (Note_TableProperties_Type.featOkTst && ((Note_TableProperties_Type)jcasType).casFeat_note_source_value == null)
      jcasType.jcas.throwFeatMissing("note_source_value", "edu.musc.tbic.omop_cdm.Note_TableProperties");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Note_TableProperties_Type)jcasType).casFeatCode_note_source_value);}
    
  /** setter for note_source_value - sets The source value associated with the origin of the Note - varchar(50) 
   * @generated
   * @param v value to set into the feature 
   */
  public void setNote_source_value(String v) {
    if (Note_TableProperties_Type.featOkTst && ((Note_TableProperties_Type)jcasType).casFeat_note_source_value == null)
      jcasType.jcas.throwFeatMissing("note_source_value", "edu.musc.tbic.omop_cdm.Note_TableProperties");
    jcasType.ll_cas.ll_setStringValue(addr, ((Note_TableProperties_Type)jcasType).casFeatCode_note_source_value, v);}    
  }

    