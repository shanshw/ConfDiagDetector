package edu.washington.cs.conf.mutation;

import java.util.ArrayList;
import java.util.Collection;

import edu.washington.cs.conf.util.Utils;



public class DetectionWorkflow {

//	private ProgramRunner runner = null;
	private Collection<ExecResult> execResults = new ArrayList<>();

	private ReportGenerator reporter = new ReportGenerator();

	private UserManual manual = null;

	public DetectionWorkflow() {
		manual = new UserManual();
		//empty
	}


	public void addResult(ExecResult result){
		this.execResults.add(result);
	}
	public void detect(String filePath) {

		for(ExecResult result : execResults) {
			if(result.pass()) {
				continue; //do nothing
			}
			//get the config option and message
			String option = result.getMutatedOption();
			String message = result.getMessage();
			//check the adequancy of the error message
//			System.out.println(message);
			System.out.println(manual.if_empyt());
			MessageAdequacy adequancy = MessageAnalyzer.isMessageAdequate(result, manual);
			System.out.println(adequancy);
//			if(!adequancy.isAdequate()) {
				//generate a report
			reporter.addToReport(adequancy, result);
//			}
		}
		System.out.println("Number of messages in report: " + reporter.size());
		reporter.dumpReport(filePath);

	}

	public ReportGenerator getReport() {
		return this.reporter;
	}

}
