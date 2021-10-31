
/* First created by JCasGen Tue Oct 26 14:23:58 EDT 2021 */
package org.apache.ctakes.typesystem.type.textspan;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** A section of a clinical text, e.g., Diagnosis, Current Medications, Problem List.  Different segments often have differing sublanguages and clinical relevance.     
Equivalent to cTAKES: edu.mayo.bmi.uima.core.type.Segment
 * Updated by JCasGen Tue Oct 26 14:23:58 EDT 2021
 * @generated */
public class Segment_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Segment.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.apache.ctakes.typesystem.type.textspan.Segment");
 
  /** @generated */
  final Feature casFeat_id;
  /** @generated */
  final int     casFeatCode_id;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getId(int addr) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "org.apache.ctakes.typesystem.type.textspan.Segment");
    return ll_cas.ll_getStringValue(addr, casFeatCode_id);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setId(int addr, String v) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "org.apache.ctakes.typesystem.type.textspan.Segment");
    ll_cas.ll_setStringValue(addr, casFeatCode_id, v);}
    
  
 
  /** @generated */
  final Feature casFeat_preferredText;
  /** @generated */
  final int     casFeatCode_preferredText;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPreferredText(int addr) {
        if (featOkTst && casFeat_preferredText == null)
      jcas.throwFeatMissing("preferredText", "org.apache.ctakes.typesystem.type.textspan.Segment");
    return ll_cas.ll_getStringValue(addr, casFeatCode_preferredText);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPreferredText(int addr, String v) {
        if (featOkTst && casFeat_preferredText == null)
      jcas.throwFeatMissing("preferredText", "org.apache.ctakes.typesystem.type.textspan.Segment");
    ll_cas.ll_setStringValue(addr, casFeatCode_preferredText, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Segment_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_id = jcas.getRequiredFeatureDE(casType, "id", "uima.cas.String", featOkTst);
    casFeatCode_id  = (null == casFeat_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_id).getCode();

 
    casFeat_preferredText = jcas.getRequiredFeatureDE(casType, "preferredText", "uima.cas.String", featOkTst);
    casFeatCode_preferredText  = (null == casFeat_preferredText) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_preferredText).getCode();

  }
}



    