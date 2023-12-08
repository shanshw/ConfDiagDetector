package edu.washington.cs.conf.mutation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.tuple.Pair;

//generate the error report for inadequate error messages
public class ReportGenerator {
	
	/**
	 * need to keep the full context
	 * */
	
	private Collection<Pair<MessageAdequacy, ExecResult>> data =
		new ArrayList<Pair<MessageAdequacy, ExecResult>>();

	public void addToReport(MessageAdequacy adequancy, ExecResult result) {
		//generate the report to describe the message inadequacy
		data.add(Pair.of(adequancy, result));
	}
	
	public void dumpReport(String outputPath) {
		Path fatherDir = Paths.get(outputPath).getParent();
		Path newOutputFile = Paths.get(fatherDir.toUri()).resolve("empty_report.txt");
		if (data.size() != 0){
			newOutputFile = Paths.get(fatherDir.toUri()).resolve("empty.txt");
		}
		System.out.println(newOutputFile);
		outputPath = newOutputFile.toString();
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
			for (Pair<MessageAdequacy, ExecResult> pair : data) {
				// Assuming MessageAdequacy and ExecResult have toString methods
				writer.write(pair.getLeft().toString() + ", " + pair.getRight().toString());
				writer.newLine();
			}
			System.out.println("Report has been dumped to " + outputPath);
		} catch (IOException e) {
			e.printStackTrace();
			// Handle the exception as needed
		}
	}

	public Collection<Pair<MessageAdequacy, ExecResult>> getCategorizedReports() {
		throw new RuntimeException();
	}
	
	public int size() {
		return data.size();
	}
}
