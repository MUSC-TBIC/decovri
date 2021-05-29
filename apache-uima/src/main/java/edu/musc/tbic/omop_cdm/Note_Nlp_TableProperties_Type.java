
/* First created by JCasGen Fri May 28 19:40:41 EDT 2021 */
package edu.musc.tbic.omop_cdm;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** A datastructure for storing all the properties
      associated with NLP extracted information in the NOTE_NLP
      table
 * Updated by JCasGen Fri May 28 19:40:45 EDT 2021
 * @generated */
public class Note_Nlp_TableProperties_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Note_Nlp_TableProperties.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
 
  /** @generated */
  final Feature casFeat_note_nlp_id;
  /** @generated */
  final int     casFeatCode_note_nlp_id;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getNote_nlp_id(int addr) {
        if (featOkTst && casFeat_note_nlp_id == null)
      jcas.throwFeatMissing("note_nlp_id", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return ll_cas.ll_getStringValue(addr, casFeatCode_note_nlp_id);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNote_nlp_id(int addr, String v) {
        if (featOkTst && casFeat_note_nlp_id == null)
      jcas.throwFeatMissing("note_nlp_id", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    ll_cas.ll_setStringValue(addr, casFeatCode_note_nlp_id, v);}
    
  
 
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
      jcas.throwFeatMissing("note_id", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return ll_cas.ll_getStringValue(addr, casFeatCode_note_id);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNote_id(int addr, String v) {
        if (featOkTst && casFeat_note_id == null)
      jcas.throwFeatMissing("note_id", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    ll_cas.ll_setStringValue(addr, casFeatCode_note_id, v);}
    
  
 
  /** @generated */
  final Feature casFeat_section_concept_id;
  /** @generated */
  final int     casFeatCode_section_concept_id;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getSection_concept_id(int addr) {
        if (featOkTst && casFeat_section_concept_id == null)
      jcas.throwFeatMissing("section_concept_id", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return ll_cas.ll_getIntValue(addr, casFeatCode_section_concept_id);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSection_concept_id(int addr, int v) {
        if (featOkTst && casFeat_section_concept_id == null)
      jcas.throwFeatMissing("section_concept_id", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    ll_cas.ll_setIntValue(addr, casFeatCode_section_concept_id, v);}
    
  
 
  /** @generated */
  final Feature casFeat_snippet;
  /** @generated */
  final int     casFeatCode_snippet;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSnippet(int addr) {
        if (featOkTst && casFeat_snippet == null)
      jcas.throwFeatMissing("snippet", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return ll_cas.ll_getStringValue(addr, casFeatCode_snippet);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSnippet(int addr, String v) {
        if (featOkTst && casFeat_snippet == null)
      jcas.throwFeatMissing("snippet", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    ll_cas.ll_setStringValue(addr, casFeatCode_snippet, v);}
    
  
 
  /** @generated */
  final Feature casFeat_offset;
  /** @generated */
  final int     casFeatCode_offset;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getOffset(int addr) {
        if (featOkTst && casFeat_offset == null)
      jcas.throwFeatMissing("offset", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return ll_cas.ll_getStringValue(addr, casFeatCode_offset);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setOffset(int addr, String v) {
        if (featOkTst && casFeat_offset == null)
      jcas.throwFeatMissing("offset", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    ll_cas.ll_setStringValue(addr, casFeatCode_offset, v);}
    
  
 
  /** @generated */
  final Feature casFeat_lexical_variant;
  /** @generated */
  final int     casFeatCode_lexical_variant;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getLexical_variant(int addr) {
        if (featOkTst && casFeat_lexical_variant == null)
      jcas.throwFeatMissing("lexical_variant", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return ll_cas.ll_getStringValue(addr, casFeatCode_lexical_variant);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setLexical_variant(int addr, String v) {
        if (featOkTst && casFeat_lexical_variant == null)
      jcas.throwFeatMissing("lexical_variant", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    ll_cas.ll_setStringValue(addr, casFeatCode_lexical_variant, v);}
    
  
 
  /** @generated */
  final Feature casFeat_note_nlp_concept_id;
  /** @generated */
  final int     casFeatCode_note_nlp_concept_id;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getNote_nlp_concept_id(int addr) {
        if (featOkTst && casFeat_note_nlp_concept_id == null)
      jcas.throwFeatMissing("note_nlp_concept_id", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return ll_cas.ll_getIntValue(addr, casFeatCode_note_nlp_concept_id);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNote_nlp_concept_id(int addr, int v) {
        if (featOkTst && casFeat_note_nlp_concept_id == null)
      jcas.throwFeatMissing("note_nlp_concept_id", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    ll_cas.ll_setIntValue(addr, casFeatCode_note_nlp_concept_id, v);}
    
  
 
  /** @generated */
  final Feature casFeat_note_nlp_source_concept_id;
  /** @generated */
  final int     casFeatCode_note_nlp_source_concept_id;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getNote_nlp_source_concept_id(int addr) {
        if (featOkTst && casFeat_note_nlp_source_concept_id == null)
      jcas.throwFeatMissing("note_nlp_source_concept_id", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return ll_cas.ll_getStringValue(addr, casFeatCode_note_nlp_source_concept_id);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNote_nlp_source_concept_id(int addr, String v) {
        if (featOkTst && casFeat_note_nlp_source_concept_id == null)
      jcas.throwFeatMissing("note_nlp_source_concept_id", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    ll_cas.ll_setStringValue(addr, casFeatCode_note_nlp_source_concept_id, v);}
    
  
 
  /** @generated */
  final Feature casFeat_nlp_system;
  /** @generated */
  final int     casFeatCode_nlp_system;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getNlp_system(int addr) {
        if (featOkTst && casFeat_nlp_system == null)
      jcas.throwFeatMissing("nlp_system", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return ll_cas.ll_getStringValue(addr, casFeatCode_nlp_system);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNlp_system(int addr, String v) {
        if (featOkTst && casFeat_nlp_system == null)
      jcas.throwFeatMissing("nlp_system", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    ll_cas.ll_setStringValue(addr, casFeatCode_nlp_system, v);}
    
  
 
  /** @generated */
  final Feature casFeat_nlp_date;
  /** @generated */
  final int     casFeatCode_nlp_date;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getNlp_date(int addr) {
        if (featOkTst && casFeat_nlp_date == null)
      jcas.throwFeatMissing("nlp_date", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return ll_cas.ll_getStringValue(addr, casFeatCode_nlp_date);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNlp_date(int addr, String v) {
        if (featOkTst && casFeat_nlp_date == null)
      jcas.throwFeatMissing("nlp_date", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    ll_cas.ll_setStringValue(addr, casFeatCode_nlp_date, v);}
    
  
 
  /** @generated */
  final Feature casFeat_nlp_datetime;
  /** @generated */
  final int     casFeatCode_nlp_datetime;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getNlp_datetime(int addr) {
        if (featOkTst && casFeat_nlp_datetime == null)
      jcas.throwFeatMissing("nlp_datetime", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return ll_cas.ll_getStringValue(addr, casFeatCode_nlp_datetime);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNlp_datetime(int addr, String v) {
        if (featOkTst && casFeat_nlp_datetime == null)
      jcas.throwFeatMissing("nlp_datetime", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    ll_cas.ll_setStringValue(addr, casFeatCode_nlp_datetime, v);}
    
  
 
  /** @generated */
  final Feature casFeat_term_exists;
  /** @generated */
  final int     casFeatCode_term_exists;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTerm_exists(int addr) {
        if (featOkTst && casFeat_term_exists == null)
      jcas.throwFeatMissing("term_exists", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return ll_cas.ll_getStringValue(addr, casFeatCode_term_exists);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTerm_exists(int addr, String v) {
        if (featOkTst && casFeat_term_exists == null)
      jcas.throwFeatMissing("term_exists", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    ll_cas.ll_setStringValue(addr, casFeatCode_term_exists, v);}
    
  
 
  /** @generated */
  final Feature casFeat_term_temporal;
  /** @generated */
  final int     casFeatCode_term_temporal;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTerm_temporal(int addr) {
        if (featOkTst && casFeat_term_temporal == null)
      jcas.throwFeatMissing("term_temporal", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return ll_cas.ll_getStringValue(addr, casFeatCode_term_temporal);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTerm_temporal(int addr, String v) {
        if (featOkTst && casFeat_term_temporal == null)
      jcas.throwFeatMissing("term_temporal", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    ll_cas.ll_setStringValue(addr, casFeatCode_term_temporal, v);}
    
  
 
  /** @generated */
  final Feature casFeat_term_modifiers;
  /** @generated */
  final int     casFeatCode_term_modifiers;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTerm_modifiers(int addr) {
        if (featOkTst && casFeat_term_modifiers == null)
      jcas.throwFeatMissing("term_modifiers", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    return ll_cas.ll_getStringValue(addr, casFeatCode_term_modifiers);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTerm_modifiers(int addr, String v) {
        if (featOkTst && casFeat_term_modifiers == null)
      jcas.throwFeatMissing("term_modifiers", "edu.musc.tbic.omop_cdm.Note_Nlp_TableProperties");
    ll_cas.ll_setStringValue(addr, casFeatCode_term_modifiers, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Note_Nlp_TableProperties_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_note_nlp_id = jcas.getRequiredFeatureDE(casType, "note_nlp_id", "uima.cas.String", featOkTst);
    casFeatCode_note_nlp_id  = (null == casFeat_note_nlp_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_note_nlp_id).getCode();

 
    casFeat_note_id = jcas.getRequiredFeatureDE(casType, "note_id", "uima.cas.String", featOkTst);
    casFeatCode_note_id  = (null == casFeat_note_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_note_id).getCode();

 
    casFeat_section_concept_id = jcas.getRequiredFeatureDE(casType, "section_concept_id", "uima.cas.Integer", featOkTst);
    casFeatCode_section_concept_id  = (null == casFeat_section_concept_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_section_concept_id).getCode();

 
    casFeat_snippet = jcas.getRequiredFeatureDE(casType, "snippet", "uima.cas.String", featOkTst);
    casFeatCode_snippet  = (null == casFeat_snippet) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_snippet).getCode();

 
    casFeat_offset = jcas.getRequiredFeatureDE(casType, "offset", "uima.cas.String", featOkTst);
    casFeatCode_offset  = (null == casFeat_offset) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_offset).getCode();

 
    casFeat_lexical_variant = jcas.getRequiredFeatureDE(casType, "lexical_variant", "uima.cas.String", featOkTst);
    casFeatCode_lexical_variant  = (null == casFeat_lexical_variant) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_lexical_variant).getCode();

 
    casFeat_note_nlp_concept_id = jcas.getRequiredFeatureDE(casType, "note_nlp_concept_id", "uima.cas.Integer", featOkTst);
    casFeatCode_note_nlp_concept_id  = (null == casFeat_note_nlp_concept_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_note_nlp_concept_id).getCode();

 
    casFeat_note_nlp_source_concept_id = jcas.getRequiredFeatureDE(casType, "note_nlp_source_concept_id", "uima.cas.String", featOkTst);
    casFeatCode_note_nlp_source_concept_id  = (null == casFeat_note_nlp_source_concept_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_note_nlp_source_concept_id).getCode();

 
    casFeat_nlp_system = jcas.getRequiredFeatureDE(casType, "nlp_system", "uima.cas.String", featOkTst);
    casFeatCode_nlp_system  = (null == casFeat_nlp_system) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_nlp_system).getCode();

 
    casFeat_nlp_date = jcas.getRequiredFeatureDE(casType, "nlp_date", "uima.cas.String", featOkTst);
    casFeatCode_nlp_date  = (null == casFeat_nlp_date) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_nlp_date).getCode();

 
    casFeat_nlp_datetime = jcas.getRequiredFeatureDE(casType, "nlp_datetime", "uima.cas.String", featOkTst);
    casFeatCode_nlp_datetime  = (null == casFeat_nlp_datetime) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_nlp_datetime).getCode();

 
    casFeat_term_exists = jcas.getRequiredFeatureDE(casType, "term_exists", "uima.cas.String", featOkTst);
    casFeatCode_term_exists  = (null == casFeat_term_exists) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_term_exists).getCode();

 
    casFeat_term_temporal = jcas.getRequiredFeatureDE(casType, "term_temporal", "uima.cas.String", featOkTst);
    casFeatCode_term_temporal  = (null == casFeat_term_temporal) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_term_temporal).getCode();

 
    casFeat_term_modifiers = jcas.getRequiredFeatureDE(casType, "term_modifiers", "uima.cas.String", featOkTst);
    casFeatCode_term_modifiers  = (null == casFeat_term_modifiers) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_term_modifiers).getCode();

  }
}



    