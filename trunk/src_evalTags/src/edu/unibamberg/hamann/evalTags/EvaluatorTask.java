package edu.unibamberg.hamann.evalTags;

import com.graphhopper.GraphHopper;

public class EvaluatorTask implements Runnable {

	private BoundingBox bbox;
	private String tag;
	private GraphHopper tgh;
	
	public EvaluatorTask(GraphHopper tgh, BoundingBox bbox, String tag) {
		
	this.bbox = bbox;	
	this.tag = tag;
	this.tgh = tgh;
		
	}
	
	@Override
	public void run() {
	
		
		Evaluator evaluator = new Evaluator();		
		
		evaluator.evaluateBoundingBox(tgh, bbox, tag);
		
		Main.activeThreads=Main.activeThreads-1;
		
	}

}
