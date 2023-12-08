package edu.washington.cs.conf.mutation;

import static org.junit.Assert.*;

public class DetectionWorkflowTest {

    @org.junit.Test
    public void detect() {
        DetectionWorkflow df = new DetectionWorkflow();
//        df.setUserManual();
//        df.detect();
        ReportGenerator report = df.getReport();
        report.dumpReport("./report.txt");
    }
}