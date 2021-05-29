
/* First created by JCasGen Thu May 27 23:45:56 EDT 2021 */
package edu.musc.tbic.uima;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Thu May 27 23:46:02 EDT 2021
 * @generated */
public class NoteSection_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = NoteSection.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.musc.tbic.uima.NoteSection");
 
  /** @generated */
  final Feature casFeat_SectionNumber;
  /** @generated */
  final int     casFeatCode_SectionNumber;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getSectionNumber(int addr) {
        if (featOkTst && casFeat_SectionNumber == null)
      jcas.throwFeatMissing("SectionNumber", "edu.musc.tbic.uima.NoteSection");
    return ll_cas.ll_getIntValue(addr, casFeatCode_SectionNumber);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSectionNumber(int addr, int v) {
        if (featOkTst && casFeat_SectionNumber == null)
      jcas.throwFeatMissing("SectionNumber", "edu.musc.tbic.uima.NoteSection");
    ll_cas.ll_setIntValue(addr, casFeatCode_SectionNumber, v);}
    
  
 
  /** @generated */
  final Feature casFeat_SectionDepth;
  /** @generated */
  final int     casFeatCode_SectionDepth;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getSectionDepth(int addr) {
        if (featOkTst && casFeat_SectionDepth == null)
      jcas.throwFeatMissing("SectionDepth", "edu.musc.tbic.uima.NoteSection");
    return ll_cas.ll_getIntValue(addr, casFeatCode_SectionDepth);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSectionDepth(int addr, int v) {
        if (featOkTst && casFeat_SectionDepth == null)
      jcas.throwFeatMissing("SectionDepth", "edu.musc.tbic.uima.NoteSection");
    ll_cas.ll_setIntValue(addr, casFeatCode_SectionDepth, v);}
    
  
 
  /** @generated */
  final Feature casFeat_SectionId;
  /** @generated */
  final int     casFeatCode_SectionId;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSectionId(int addr) {
        if (featOkTst && casFeat_SectionId == null)
      jcas.throwFeatMissing("SectionId", "edu.musc.tbic.uima.NoteSection");
    return ll_cas.ll_getStringValue(addr, casFeatCode_SectionId);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSectionId(int addr, String v) {
        if (featOkTst && casFeat_SectionId == null)
      jcas.throwFeatMissing("SectionId", "edu.musc.tbic.uima.NoteSection");
    ll_cas.ll_setStringValue(addr, casFeatCode_SectionId, v);}
    
  
 
  /** @generated */
  final Feature casFeat_beginHeader;
  /** @generated */
  final int     casFeatCode_beginHeader;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getBeginHeader(int addr) {
        if (featOkTst && casFeat_beginHeader == null)
      jcas.throwFeatMissing("beginHeader", "edu.musc.tbic.uima.NoteSection");
    return ll_cas.ll_getIntValue(addr, casFeatCode_beginHeader);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setBeginHeader(int addr, int v) {
        if (featOkTst && casFeat_beginHeader == null)
      jcas.throwFeatMissing("beginHeader", "edu.musc.tbic.uima.NoteSection");
    ll_cas.ll_setIntValue(addr, casFeatCode_beginHeader, v);}
    
  
 
  /** @generated */
  final Feature casFeat_endHeader;
  /** @generated */
  final int     casFeatCode_endHeader;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getEndHeader(int addr) {
        if (featOkTst && casFeat_endHeader == null)
      jcas.throwFeatMissing("endHeader", "edu.musc.tbic.uima.NoteSection");
    return ll_cas.ll_getIntValue(addr, casFeatCode_endHeader);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setEndHeader(int addr, int v) {
        if (featOkTst && casFeat_endHeader == null)
      jcas.throwFeatMissing("endHeader", "edu.musc.tbic.uima.NoteSection");
    ll_cas.ll_setIntValue(addr, casFeatCode_endHeader, v);}
    
  
 
  /** @generated */
  final Feature casFeat_modifiers;
  /** @generated */
  final int     casFeatCode_modifiers;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getModifiers(int addr) {
        if (featOkTst && casFeat_modifiers == null)
      jcas.throwFeatMissing("modifiers", "edu.musc.tbic.uima.NoteSection");
    return ll_cas.ll_getStringValue(addr, casFeatCode_modifiers);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setModifiers(int addr, String v) {
        if (featOkTst && casFeat_modifiers == null)
      jcas.throwFeatMissing("modifiers", "edu.musc.tbic.uima.NoteSection");
    ll_cas.ll_setStringValue(addr, casFeatCode_modifiers, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public NoteSection_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_SectionNumber = jcas.getRequiredFeatureDE(casType, "SectionNumber", "uima.cas.Integer", featOkTst);
    casFeatCode_SectionNumber  = (null == casFeat_SectionNumber) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_SectionNumber).getCode();

 
    casFeat_SectionDepth = jcas.getRequiredFeatureDE(casType, "SectionDepth", "uima.cas.Integer", featOkTst);
    casFeatCode_SectionDepth  = (null == casFeat_SectionDepth) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_SectionDepth).getCode();

 
    casFeat_SectionId = jcas.getRequiredFeatureDE(casType, "SectionId", "uima.cas.String", featOkTst);
    casFeatCode_SectionId  = (null == casFeat_SectionId) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_SectionId).getCode();

 
    casFeat_beginHeader = jcas.getRequiredFeatureDE(casType, "beginHeader", "uima.cas.Integer", featOkTst);
    casFeatCode_beginHeader  = (null == casFeat_beginHeader) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_beginHeader).getCode();

 
    casFeat_endHeader = jcas.getRequiredFeatureDE(casType, "endHeader", "uima.cas.Integer", featOkTst);
    casFeatCode_endHeader  = (null == casFeat_endHeader) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_endHeader).getCode();

 
    casFeat_modifiers = jcas.getRequiredFeatureDE(casType, "modifiers", "uima.cas.String", featOkTst);
    casFeatCode_modifiers  = (null == casFeat_modifiers) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_modifiers).getCode();

  }
}



    