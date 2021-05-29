package edu.musc.tbic.concepts;

import java.util.TreeMap;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import edu.musc.covid.type.Dosage;
import edu.musc.covid.type.Drug;
import edu.musc.covid.type.Duration;
import edu.musc.covid.type.Frequency;
import edu.musc.covid.type.LabName;
import edu.musc.covid.type.LabValue;
import edu.musc.covid.type.Route;

public class RelAnnotator extends JCasAnnotator_ImplBase {

    public void initialize(UimaContext uimaContext)	throws ResourceInitializationException {
		super.initialize(uimaContext);
    }        

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {

		setMedRel(jCas);
		setLabRel(jCas);
		
	}

	private int shorten(JCas jCas, int b, int e) {
		
		Annotation bet = new Annotation(jCas);
		bet.setBegin(b);
		bet.setEnd(e);

		int len = 0;
		FSIterator<Annotation> sub = jCas.getAnnotationIndex().subiterator(bet);
		while (sub.hasNext()) {
			Annotation annot = sub.next();
			if (annot.getClass().equals(Dosage.class) || annot.getClass().equals(Duration.class) || 
					annot.getClass().equals(Frequency.class) || annot.getClass().equals(Route.class)) {
				len += annot.getEnd() - annot.getBegin();
			}
		}
		
		return e - b - len;
	}
	
	private Drug setDrug(JCas jCas, TreeMap<Integer, Drug> map, int begin, int end) {
		
		Drug pre = null;
		Drug next = null;
		
		int pM = 0;
		int nM = Integer.MAX_VALUE;
		for (int ns : map.keySet()) {
			Drug l = map.get(ns);
        	int ne = l.getEnd();
        	// find the closest preceding drug
        	if (ne <= begin) {
        		if (pM < ne) {
        			pM = ne;
        			pre = l;
        		}        		
        	}
        	// find the closest following drug
        	if (end <= ns) {
        		if (ns < nM) {
        			nM = ns;
        			next = l;
        		}        		
        	}
        }	
		
		if (pre != null && shorten(jCas, pM, begin) < 250) {
			return pre;
		} else if (next != null && shorten(jCas, end, nM) < 50) {
			return next;
		}
		
        return null;
	}
	
	private LabName setLabName(TreeMap<Integer, LabName> map, int begin, int end) {
		
		LabName pre = null;
		LabName next = null;
		
		int pM = 0;
		int nM = Integer.MAX_VALUE;
		for (int ns : map.keySet()) {
        	LabName l = map.get(ns);
        	int ne = l.getEnd();
        	// find the closest preceding lab name
        	if (ne <= begin) {
        		if (pM < ne) {
        			pM = ne;
        			pre = l;
        		}        		
        	}
        	// find the closest following lab name
        	if (end <= ns) {
        		if (ns < nM) {
        			nM = ns;
        			next = l;
        		}        		
        	}
        }	
		
		if (pre != null && begin - pM < 50) {
			return pre;
		} else if (next != null && nM - end < 10) {
			return next;
		}
		
        return null;
	}

	public void setMedRel(JCas jCas) throws AnalysisEngineProcessException {
		
		TreeMap<Integer, Drug> dMap = new TreeMap<>();		
        for (Drug n : jCas.getAnnotationIndex(Drug.class)) {
        	dMap.put(n.getBegin(), n);
        }
        
        for (Dosage n : jCas.getAnnotationIndex(Dosage.class)) {        	
        	n.setDrug(setDrug(jCas, dMap, n.getBegin(), n.getEnd()));        	
        }
        for (Route n : jCas.getAnnotationIndex(Route.class)) {        	
        	n.setDrug(setDrug(jCas, dMap, n.getBegin(), n.getEnd()));        	
        }
        for (Frequency n : jCas.getAnnotationIndex(Frequency.class)) {        	
        	n.setDrug(setDrug(jCas, dMap, n.getBegin(), n.getEnd()));        	
        }
        for (Duration n : jCas.getAnnotationIndex(Duration.class)) {        	
        	n.setDrug(setDrug(jCas, dMap, n.getBegin(), n.getEnd()));        	
        }
        
	}
	
	public void setLabRel(JCas jCas) throws AnalysisEngineProcessException {
		
		TreeMap<Integer, LabName> lMap = new TreeMap<>();		
        for (LabName n : jCas.getAnnotationIndex(LabName.class)) {
        	lMap.put(n.getBegin(), n);
        }
        
        for (LabValue n : jCas.getAnnotationIndex(LabValue.class)) {        	
        	n.setLabName(setLabName(lMap, n.getBegin(), n.getEnd()));        	
        }
        
	}	

}
