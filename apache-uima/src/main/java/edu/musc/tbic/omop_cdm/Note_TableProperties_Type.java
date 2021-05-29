
/* First created by JCasGen Thu May 27 23:45:34 EDT 2021 */
package edu.musc.tbic.omop_cdm;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** A datastructure for storing all the properties associated with a note in the NOTE table
 * Updated by JCasGen Thu May 27 23:45:34 EDT 2021
 * @generated */
public class Note_TableProperties_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Note_TableProperties.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.musc.tbic.omop_cdm.Note_TableProperties");
 
  /** @generated */
  final Feature casFeat_note_id;
  /** @generated */
  final int     casFeatCode_note_id;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getNote_id(int addr) {
        if (featOkTst && casFeat_note_id == null)
      jcas.throwFeatMissing("note_id", "edu.musc.tbic.omop_cdm.Note_TableProperties");
    return ll_cas.ll_getStringValue(addr, casFeatCode_note_id);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNote_id(int addr, String v) {
        if (featOkTst && casFeat_note_id == null)
      jcas.throwFeatMissing("note_id", "edu.musc.tbic.omop_cdm.Note_TableProperties");
    ll_cas.ll_setStringValue(addr, casFeatCode_note_id, v);}
    
  
 
  /** @generated */
  final Feature casFeat_person_id;
  /** @generated */
  final int     casFeatCode_person_id;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPerson_id(int addr) {
        if (featOkTst && casFeat_person_id == null)
      jcas.throwFeatMissing("person_id", "edu.musc.tbic.omop_cdm.Note_TableProperties");
    return ll_cas.ll_getStringValue(addr, casFeatCode_person_id);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPerson_id(int addr, String v) {
        if (featOkTst && casFeat_person_id == null)
      jcas.throwFeatMissing("person_id", "edu.musc.tbic.omop_cdm.Note_TableProperties");
    ll_cas.ll_setStringValue(addr, casFeatCode_person_id, v);}
    
  
 
  /** @generated */
  final Feature casFeat_note_date;
  /** @generated */
  final int     casFeatCode_note_date;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getNote_date(int addr) {
        if (featOkTst && casFeat_note_date == null)
      jcas.throwFeatMissing("note_date", "edu.musc.tbic.omop_cdm.Note_TableProperties");
    return ll_cas.ll_getStringValue(addr, casFeatCode_note_date);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNote_date(int addr, String v) {
        if (featOkTst && casFeat_note_date == null)
      jcas.throwFeatMissing("note_date", "edu.musc.tbic.omop_cdm.Note_TableProperties");
    ll_cas.ll_setStringValue(addr, casFeatCode_note_date, v);}
    
  
 
  /** @generated */
  final Feature casFeat_note_datetime;
  /** @generated */
  final int     casFeatCode_note_datetime;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getNote_datetime(int addr) {
        if (featOkTst && casFeat_note_datetime == null)
      jcas.throwFeatMissing("note_datetime", "edu.musc.tbic.omop_cdm.Note_TableProperties");
    return ll_cas.ll_getStringValue(addr, casFeatCode_note_datetime);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNote_datetime(int addr, String v) {
        if (featOkTst && casFeat_note_datetime == null)
      jcas.throwFeatMissing("note_datetime", "edu.musc.tbic.omop_cdm.Note_TableProperties");
    ll_cas.ll_setStringValue(addr, casFeatCode_note_datetime, v);}
    
  
 
  /** @generated */
  final Feature casFeat_note_source_value;
  /** @generated */
  final int     casFeatCode_note_source_value;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getNote_source_value(int addr) {
        if (featOkTst && casFeat_note_source_value == null)
      jcas.throwFeatMissing("note_source_value", "edu.musc.tbic.omop_cdm.Note_TableProperties");
    return ll_cas.ll_getStringValue(addr, casFeatCode_note_source_value);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNote_source_value(int addr, String v) {
        if (featOkTst && casFeat_note_source_value == null)
      jcas.throwFeatMissing("note_source_value", "edu.musc.tbic.omop_cdm.Note_TableProperties");
    ll_cas.ll_setStringValue(addr, casFeatCode_note_source_value, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Note_TableProperties_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_note_id = jcas.getRequiredFeatureDE(casType, "note_id", "uima.cas.String", featOkTst);
    casFeatCode_note_id  = (null == casFeat_note_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_note_id).getCode();

 
    casFeat_person_id = jcas.getRequiredFeatureDE(casType, "person_id", "uima.cas.String", featOkTst);
    casFeatCode_person_id  = (null == casFeat_person_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_person_id).getCode();

 
    casFeat_note_date = jcas.getRequiredFeatureDE(casType, "note_date", "uima.cas.String", featOkTst);
    casFeatCode_note_date  = (null == casFeat_note_date) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_note_date).getCode();

 
    casFeat_note_datetime = jcas.getRequiredFeatureDE(casType, "note_datetime", "uima.cas.String", featOkTst);
    casFeatCode_note_datetime  = (null == casFeat_note_datetime) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_note_datetime).getCode();

 
    casFeat_note_source_value = jcas.getRequiredFeatureDE(casType, "note_source_value", "uima.cas.String", featOkTst);
    casFeatCode_note_source_value  = (null == casFeat_note_source_value) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_note_source_value).getCode();

  }
}



    