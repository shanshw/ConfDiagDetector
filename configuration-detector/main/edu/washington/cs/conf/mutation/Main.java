package edu.washington.cs.conf.mutation;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * Author: shanshw
 * Date:   2023/12/7
 * Time:   22:11
 */
public class Main {
    public static void main(String[] args)
    {
        if (args.length != 3) {
            System.out.println("Usage: <csvFilePath> <configName> <configValue>");
            return;
        }
//            String csvFilePath = "D:\\Project\\ReSearch\\AutoLogPlus\\baseLine\\configuration-detector\\main\\edu\\washington\\cs\\conf\\resources\\terasort_container_1701312551309_0035_01_000001\\parsed_output\\anomaly_logs.csv";
//            String config = "yarn.client.nodemanager-connect.retry-interval-ms";
//            String value = "-1836965711";

        String csvFilePath = args[0];
        String config = args[1];
        String value = args[2];

            // 读取 CSV 文件中 "content" 列的内容并存储在 List 中
            readContentColumn(csvFilePath, config, value);

    }

    private static void readContentColumn (String csvFilePath,String config, String value) {
        List<String> contentList = new ArrayList<>();
        DetectionWorkflow df = new DetectionWorkflow();
        try (CSVParser parser = CSVParser.parse(new FileReader(csvFilePath), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : parser) {
                // 获取 "content" 列的值
                String content = record.get("content").trim();
                String message = content.split("\t")[1].trim();
                if(message.equals(" "))
                    continue;
//                System.out.println(message);
                ExecResult result = new ExecResult(message,config,value,Status.Fail);
                df.addResult(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // 处理异常
        }
    df.detect(csvFilePath);
//        return contentList;
    }
}
