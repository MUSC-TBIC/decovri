
/* First created by JCasGen Sat May 29 12:51:13 EDT 2021 */
package edu.musc.covid.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Sat May 29 12:51:13 EDT 2021
 * @generated */
public class LabName_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = LabName.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.musc.covid.type.LabName");
 
  /** @generated */
  final Feature casFeat_CUI;
  /** @generated */
  final int     casFeatCode_CUI;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getCUI(int addr) {
        if (featOkTst && casFeat_CUI == null)
      jcas.throwFeatMissing("CUI", "edu.musc.covid.type.LabName");
    return ll_cas.ll_getStringValue(addr, casFeatCode_CUI);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCUI(int addr, String v) {
        if (featOkTst && casFeat_CUI == null)
      jcas.throwFeatMissing("CUI", "edu.musc.covid.type.LabName");
    ll_cas.ll_setStringValue(addr, casFeatCode_CUI, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public LabName_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_CUI = jcas.getRequiredFeatureDE(casType, "CUI", "uima.cas.String", featOkTst);
    casFeatCode_CUI  = (null == casFeat_CUI) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_CUI).getCode();

  }
}



    