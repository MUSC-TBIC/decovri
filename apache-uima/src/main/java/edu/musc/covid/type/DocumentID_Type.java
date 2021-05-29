
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
public class DocumentID_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = DocumentID.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.musc.covid.type.DocumentID");
 
  /** @generated */
  final Feature casFeat_documentId;
  /** @generated */
  final int     casFeatCode_documentId;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDocumentId(int addr) {
        if (featOkTst && casFeat_documentId == null)
      jcas.throwFeatMissing("documentId", "edu.musc.covid.type.DocumentID");
    return ll_cas.ll_getStringValue(addr, casFeatCode_documentId);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDocumentId(int addr, String v) {
        if (featOkTst && casFeat_documentId == null)
      jcas.throwFeatMissing("documentId", "edu.musc.covid.type.DocumentID");
    ll_cas.ll_setStringValue(addr, casFeatCode_documentId, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public DocumentID_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_documentId = jcas.getRequiredFeatureDE(casType, "documentId", "uima.cas.String", featOkTst);
    casFeatCode_documentId  = (null == casFeat_documentId) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_documentId).getCode();

  }
}



    