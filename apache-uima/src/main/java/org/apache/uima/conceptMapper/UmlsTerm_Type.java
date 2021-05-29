
/* First created by JCasGen Thu May 27 23:46:26 EDT 2021 */
package org.apache.uima.conceptMapper;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Annotation for dictionary lookup matches
 * Updated by JCasGen Thu May 27 23:46:26 EDT 2021
 * @generated */
public class UmlsTerm_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = UmlsTerm.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.apache.uima.conceptMapper.UmlsTerm");
 
  /** @generated */
  final Feature casFeat_PreferredTerm;
  /** @generated */
  final int     casFeatCode_PreferredTerm;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPreferredTerm(int addr) {
        if (featOkTst && casFeat_PreferredTerm == null)
      jcas.throwFeatMissing("PreferredTerm", "org.apache.uima.conceptMapper.UmlsTerm");
    return ll_cas.ll_getStringValue(addr, casFeatCode_PreferredTerm);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPreferredTerm(int addr, String v) {
        if (featOkTst && casFeat_PreferredTerm == null)
      jcas.throwFeatMissing("PreferredTerm", "org.apache.uima.conceptMapper.UmlsTerm");
    ll_cas.ll_setStringValue(addr, casFeatCode_PreferredTerm, v);}
    
  
 
  /** @generated */
  final Feature casFeat_ConceptCode;
  /** @generated */
  final int     casFeatCode_ConceptCode;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getConceptCode(int addr) {
        if (featOkTst && casFeat_ConceptCode == null)
      jcas.throwFeatMissing("ConceptCode", "org.apache.uima.conceptMapper.UmlsTerm");
    return ll_cas.ll_getStringValue(addr, casFeatCode_ConceptCode);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setConceptCode(int addr, String v) {
        if (featOkTst && casFeat_ConceptCode == null)
      jcas.throwFeatMissing("ConceptCode", "org.apache.uima.conceptMapper.UmlsTerm");
    ll_cas.ll_setStringValue(addr, casFeatCode_ConceptCode, v);}
    
  
 
  /** @generated */
  final Feature casFeat_ConceptType;
  /** @generated */
  final int     casFeatCode_ConceptType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getConceptType(int addr) {
        if (featOkTst && casFeat_ConceptType == null)
      jcas.throwFeatMissing("ConceptType", "org.apache.uima.conceptMapper.UmlsTerm");
    return ll_cas.ll_getStringValue(addr, casFeatCode_ConceptType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setConceptType(int addr, String v) {
        if (featOkTst && casFeat_ConceptType == null)
      jcas.throwFeatMissing("ConceptType", "org.apache.uima.conceptMapper.UmlsTerm");
    ll_cas.ll_setStringValue(addr, casFeatCode_ConceptType, v);}
    
  
 
  /** @generated */
  final Feature casFeat_TUI;
  /** @generated */
  final int     casFeatCode_TUI;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTUI(int addr) {
        if (featOkTst && casFeat_TUI == null)
      jcas.throwFeatMissing("TUI", "org.apache.uima.conceptMapper.UmlsTerm");
    return ll_cas.ll_getStringValue(addr, casFeatCode_TUI);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTUI(int addr, String v) {
        if (featOkTst && casFeat_TUI == null)
      jcas.throwFeatMissing("TUI", "org.apache.uima.conceptMapper.UmlsTerm");
    ll_cas.ll_setStringValue(addr, casFeatCode_TUI, v);}
    
  
 
  /** @generated */
  final Feature casFeat_BasicLevelConceptCode;
  /** @generated */
  final int     casFeatCode_BasicLevelConceptCode;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getBasicLevelConceptCode(int addr) {
        if (featOkTst && casFeat_BasicLevelConceptCode == null)
      jcas.throwFeatMissing("BasicLevelConceptCode", "org.apache.uima.conceptMapper.UmlsTerm");
    return ll_cas.ll_getStringValue(addr, casFeatCode_BasicLevelConceptCode);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setBasicLevelConceptCode(int addr, String v) {
        if (featOkTst && casFeat_BasicLevelConceptCode == null)
      jcas.throwFeatMissing("BasicLevelConceptCode", "org.apache.uima.conceptMapper.UmlsTerm");
    ll_cas.ll_setStringValue(addr, casFeatCode_BasicLevelConceptCode, v);}
    
  
 
  /** @generated */
  final Feature casFeat_BasicLevelConceptType;
  /** @generated */
  final int     casFeatCode_BasicLevelConceptType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getBasicLevelConceptType(int addr) {
        if (featOkTst && casFeat_BasicLevelConceptType == null)
      jcas.throwFeatMissing("BasicLevelConceptType", "org.apache.uima.conceptMapper.UmlsTerm");
    return ll_cas.ll_getStringValue(addr, casFeatCode_BasicLevelConceptType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setBasicLevelConceptType(int addr, String v) {
        if (featOkTst && casFeat_BasicLevelConceptType == null)
      jcas.throwFeatMissing("BasicLevelConceptType", "org.apache.uima.conceptMapper.UmlsTerm");
    ll_cas.ll_setStringValue(addr, casFeatCode_BasicLevelConceptType, v);}
    
  
 
  /** @generated */
  final Feature casFeat_enclosingSpan;
  /** @generated */
  final int     casFeatCode_enclosingSpan;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getEnclosingSpan(int addr) {
        if (featOkTst && casFeat_enclosingSpan == null)
      jcas.throwFeatMissing("enclosingSpan", "org.apache.uima.conceptMapper.UmlsTerm");
    return ll_cas.ll_getRefValue(addr, casFeatCode_enclosingSpan);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setEnclosingSpan(int addr, int v) {
        if (featOkTst && casFeat_enclosingSpan == null)
      jcas.throwFeatMissing("enclosingSpan", "org.apache.uima.conceptMapper.UmlsTerm");
    ll_cas.ll_setRefValue(addr, casFeatCode_enclosingSpan, v);}
    
  
 
  /** @generated */
  final Feature casFeat_matchedText;
  /** @generated */
  final int     casFeatCode_matchedText;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getMatchedText(int addr) {
        if (featOkTst && casFeat_matchedText == null)
      jcas.throwFeatMissing("matchedText", "org.apache.uima.conceptMapper.UmlsTerm");
    return ll_cas.ll_getStringValue(addr, casFeatCode_matchedText);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setMatchedText(int addr, String v) {
        if (featOkTst && casFeat_matchedText == null)
      jcas.throwFeatMissing("matchedText", "org.apache.uima.conceptMapper.UmlsTerm");
    ll_cas.ll_setStringValue(addr, casFeatCode_matchedText, v);}
    
  
 
  /** @generated */
  final Feature casFeat_matchedTokens;
  /** @generated */
  final int     casFeatCode_matchedTokens;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getMatchedTokens(int addr) {
        if (featOkTst && casFeat_matchedTokens == null)
      jcas.throwFeatMissing("matchedTokens", "org.apache.uima.conceptMapper.UmlsTerm");
    return ll_cas.ll_getRefValue(addr, casFeatCode_matchedTokens);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setMatchedTokens(int addr, int v) {
        if (featOkTst && casFeat_matchedTokens == null)
      jcas.throwFeatMissing("matchedTokens", "org.apache.uima.conceptMapper.UmlsTerm");
    ll_cas.ll_setRefValue(addr, casFeatCode_matchedTokens, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getMatchedTokens(int addr, int i) {
        if (featOkTst && casFeat_matchedTokens == null)
      jcas.throwFeatMissing("matchedTokens", "org.apache.uima.conceptMapper.UmlsTerm");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_matchedTokens), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_matchedTokens), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_matchedTokens), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setMatchedTokens(int addr, int i, int v) {
        if (featOkTst && casFeat_matchedTokens == null)
      jcas.throwFeatMissing("matchedTokens", "org.apache.uima.conceptMapper.UmlsTerm");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_matchedTokens), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_matchedTokens), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_matchedTokens), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public UmlsTerm_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_PreferredTerm = jcas.getRequiredFeatureDE(casType, "PreferredTerm", "uima.cas.String", featOkTst);
    casFeatCode_PreferredTerm  = (null == casFeat_PreferredTerm) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_PreferredTerm).getCode();

 
    casFeat_ConceptCode = jcas.getRequiredFeatureDE(casType, "ConceptCode", "uima.cas.String", featOkTst);
    casFeatCode_ConceptCode  = (null == casFeat_ConceptCode) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ConceptCode).getCode();

 
    casFeat_ConceptType = jcas.getRequiredFeatureDE(casType, "ConceptType", "uima.cas.String", featOkTst);
    casFeatCode_ConceptType  = (null == casFeat_ConceptType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ConceptType).getCode();

 
    casFeat_TUI = jcas.getRequiredFeatureDE(casType, "TUI", "uima.cas.String", featOkTst);
    casFeatCode_TUI  = (null == casFeat_TUI) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TUI).getCode();

 
    casFeat_BasicLevelConceptCode = jcas.getRequiredFeatureDE(casType, "BasicLevelConceptCode", "uima.cas.String", featOkTst);
    casFeatCode_BasicLevelConceptCode  = (null == casFeat_BasicLevelConceptCode) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_BasicLevelConceptCode).getCode();

 
    casFeat_BasicLevelConceptType = jcas.getRequiredFeatureDE(casType, "BasicLevelConceptType", "uima.cas.String", featOkTst);
    casFeatCode_BasicLevelConceptType  = (null == casFeat_BasicLevelConceptType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_BasicLevelConceptType).getCode();

 
    casFeat_enclosingSpan = jcas.getRequiredFeatureDE(casType, "enclosingSpan", "uima.tcas.Annotation", featOkTst);
    casFeatCode_enclosingSpan  = (null == casFeat_enclosingSpan) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_enclosingSpan).getCode();

 
    casFeat_matchedText = jcas.getRequiredFeatureDE(casType, "matchedText", "uima.cas.String", featOkTst);
    casFeatCode_matchedText  = (null == casFeat_matchedText) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_matchedText).getCode();

 
    casFeat_matchedTokens = jcas.getRequiredFeatureDE(casType, "matchedTokens", "uima.cas.FSArray", featOkTst);
    casFeatCode_matchedTokens  = (null == casFeat_matchedTokens) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_matchedTokens).getCode();

  }
}



    