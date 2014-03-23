package edu.unibamberg.hamann.evalTags;

import com.graphhopper.GraphHopper;

public class EvaluatorTask implements Runnable {

	private BoundingBox bbox;
	private String tag;
	private int activeThreads;
	private GraphHopper tgh;
	
	public EvaluatorTask(GraphHopper tgh, int activeThreads, BoundingBox bbox, String tag) {
		
	this.bbox = bbox;	
	this.activeThreads = activeThreads;	
	this.tag = tag;
	this.tgh = tgh;
		
	}
	
	@Override
	public void run() {
	
		
		Evaluator evaluator = new Evaluator();		
		
		evaluator.evaluateBoundingBox(tgh, bbox, tag);
		
		activeThreads=activeThreads-1;
	}

}
