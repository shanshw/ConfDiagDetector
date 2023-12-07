package edu.washington.cs.conf.mutation;

import java.util.ArrayList;
import java.util.Collection;

import edu.washington.cs.conf.util.Utils;

public class DetectionWorkflow {

//	private ProgramRunner runner = null;

	private ReportGenerator reporter = new ReportGenerator();

	private UserManual manual = null;

	public DetectionWorkflow() {
		//empty
	}

//	public DetectionWorkflow(Class<? extends ProgramRunner> clz) {
//		try {
//			runner = clz.newInstance();
//		} catch (Exception e) {
//			System.out.println("Cannot instantiate class: " + clz);
//			e.printStackTrace();
//		}
//	}

//	public void setProgramRunner(ProgramRunner runner) {
//		Utils.checkNotNull(runner);
//		this.runner = runner;
//	}

	public void setUserManual(UserManual manual) {
		Utils.checkNotNull(manual);
		this.manual = manual;
	}

	public void setUserManual(){
		this.manual = new UserManual();
	}

	public void detect() {
//		Utils.checkNotNull(runner);
//		Utils.checkNotNull(reporter);

		//collect the results
//		runner.setUpEnv();
		String message1 = "Task: attempt_1701312551309_0035_m_000008_0 - exited : java.io.IOException: Initialization of all the collectors failed. Error in last collector was:java.lang.ArithmeticException: / by zero\tat org.apache.hadoop.mapred.MapTask.createSortingCollector(MapTask.java:424)\n" +
				"\tat org.apache.hadoop.mapred.MapTask.access$100(MapTask.java:83)\n" +
				"\tat org.apache.hadoop.mapred.MapTask$NewOutputCollector.<init>(MapTask.java:711)\n" +
				"\tat org.apache.hadoop.mapred.MapTask.runNewMapper(MapTask.java:783)\n" +
				"\tat org.apache.hadoop.mapred.MapTask.run(MapTask.java:348)\n" +
				"\tat org.apache.hadoop.mapred.YarnChild$2.run(YarnChild.java:178)\n" +
				"\tat java.base/java.security.AccessController.doPrivileged(Native Method)\n" +
				"\tat java.base/javax.security.auth.Subject.doAs(Subject.java:423)\n" +
				"\tat org.apache.hadoop.security.UserGroupInformation.doAs(UserGroupInformation.java:1899)\n" +
				"\tat org.apache.hadoop.mapred.YarnChild.main(YarnChild.java:172)\n" +
				"Caused by: java.lang.ArithmeticException: / by zero\n" +
				"\tat org.apache.hadoop.mapred.MapTask$MapOutputBuffer.setEquator(MapTask.java:1218)\n" +
				"\tat org.apache.hadoop.mapred.MapTask$MapOutputBuffer.init(MapTask.java:1006)\n" +
				"\tat org.apache.hadoop.mapred.MapTask.createSortingCollector(MapTask.java:409)\n" +
				"\t... 9 more";
		String mutatedConfig1 = "mapreduce.task.io.sort.mb";
		String value1 = "0";
		Status status1 = Status.Fail; //这里指的是Junit Test
		ExecResult excR = new ExecResult(message1,mutatedConfig1,value1,status1);
		Collection<ExecResult> execResults = new ArrayList<>();
		execResults.add(excR);
//		runner.clearEnv();

//		System.out.println("number of exec results: " + execResults.size());
		for(ExecResult result : execResults) {
//			System.out.println(result);
			//analyze it and generate the report
			if(result.pass()) {
				continue; //do nothing
			}
			//get the config option and message
			String option = result.getMutatedOption();
			String message = result.getMessage();
			//check the adequancy of the error message
			MessageAdequacy adequancy = MessageAnalyzer.isMessageAdequate(result, manual);
			System.out.println(adequancy);
			if(!adequancy.isAdequate()) {
				//generate a report
				reporter.addToReport(adequancy, result);
			}
		}

		System.out.println("Number of messages in report: " + reporter.size());

	}

	public ReportGenerator getReport() {
		return this.reporter;
	}

}
