
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
public class LabValue_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = LabValue.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.musc.covid.type.LabValue");
 
  /** @generated */
  final Feature casFeat_LabName;
  /** @generated */
  final int     casFeatCode_LabName;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getLabName(int addr) {
        if (featOkTst && casFeat_LabName == null)
      jcas.throwFeatMissing("LabName", "edu.musc.covid.type.LabValue");
    return ll_cas.ll_getRefValue(addr, casFeatCode_LabName);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setLabName(int addr, int v) {
        if (featOkTst && casFeat_LabName == null)
      jcas.throwFeatMissing("LabName", "edu.musc.covid.type.LabValue");
    ll_cas.ll_setRefValue(addr, casFeatCode_LabName, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public LabValue_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_LabName = jcas.getRequiredFeatureDE(casType, "LabName", "edu.musc.covid.type.LabName", featOkTst);
    casFeatCode_LabName  = (null == casFeat_LabName) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_LabName).getCode();

  }
}



    