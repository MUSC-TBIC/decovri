
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
public class Duration_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Duration.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.musc.covid.type.Duration");
 
  /** @generated */
  final Feature casFeat_drug;
  /** @generated */
  final int     casFeatCode_drug;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getDrug(int addr) {
        if (featOkTst && casFeat_drug == null)
      jcas.throwFeatMissing("drug", "edu.musc.covid.type.Duration");
    return ll_cas.ll_getRefValue(addr, casFeatCode_drug);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDrug(int addr, int v) {
        if (featOkTst && casFeat_drug == null)
      jcas.throwFeatMissing("drug", "edu.musc.covid.type.Duration");
    ll_cas.ll_setRefValue(addr, casFeatCode_drug, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Duration_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_drug = jcas.getRequiredFeatureDE(casType, "drug", "edu.musc.covid.type.Drug", featOkTst);
    casFeatCode_drug  = (null == casFeat_drug) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_drug).getCode();

  }
}



    