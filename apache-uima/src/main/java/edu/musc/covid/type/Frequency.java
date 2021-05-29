

/* First created by JCasGen Sat May 29 12:51:13 EDT 2021 */
package edu.musc.covid.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Sat May 29 12:51:13 EDT 2021
 * XML source: /Users/pmh/git/decovri/apache-uima/src/main/resources/desc/types/TypeSystem.xml
 * @generated */
public class Frequency extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Frequency.class);
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
  protected Frequency() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Frequency(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Frequency(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Frequency(JCas jcas, int begin, int end) {
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
  //* Feature: drug

  /** getter for drug - gets 
   * @generated
   * @return value of the feature 
   */
  public Drug getDrug() {
    if (Frequency_Type.featOkTst && ((Frequency_Type)jcasType).casFeat_drug == null)
      jcasType.jcas.throwFeatMissing("drug", "edu.musc.covid.type.Frequency");
    return (Drug)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Frequency_Type)jcasType).casFeatCode_drug)));}
    
  /** setter for drug - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setDrug(Drug v) {
    if (Frequency_Type.featOkTst && ((Frequency_Type)jcasType).casFeat_drug == null)
      jcasType.jcas.throwFeatMissing("drug", "edu.musc.covid.type.Frequency");
    jcasType.ll_cas.ll_setRefValue(addr, ((Frequency_Type)jcasType).casFeatCode_drug, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    