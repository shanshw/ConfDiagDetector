package edu.washington.cs.conf.mutation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import edu.washington.cs.conf.util.Utils;

public class UserManual {

	private static Map<String, String> options = new LinkedHashMap<String, String>();

	//the file format
	//option name: description (split from the first : )
	static String SPLIT = ":";

	//read options from file
//	public UserManual() {
//		String filePath = "C:\\Users\\casty\\Downloads\\config-errors-master\\config-errors-master\\configuration-detector\\resources\\conf_with_missing_symbol.out";
//		List<String> lines = Files.readWholeNoExp(filePath);
//		for(String line : lines) {
//			if(line.trim().length() == 0) {
//				continue;
//			}
//			int index = line.indexOf(SPLIT);
//			System.out.println(line);
//			if(index == -1) {
//				throw new RuntimeException("Invalid line: " + line);
//			}
//			String optionName = line.substring(0, index);
//			String description = (index + SPLIT.length() == line.length() - 1)
//			    ? "" : line.substring(index + SPLIT.length());
//			this.addOptionDescription(optionName, description);
//		}
//
//	}
	public UserManual(){
//		String filePath = "D:\\Project\\ReSearch\\AutoLogPlus\\baseLine\\configuration-detector\\main\\edu\\washington\\cs\\conf\\mutation\\conf.out";

		String filePath = "./conf.out";
//		String filePath="D:\\Project\\ReSearch\\AutoLogPlus\\baseLine\\configuration-detector\\main\\edu\\washington\\cs\\conf\\mutation\\conf.out";
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			// 读取 CSV 文件的第一行，即表头
			String headerLine = reader.readLine();
			if (headerLine != null) {
				// 分割表头，获取 "property" 和 "description" 列的索引
//				String[] headers = headerLine.split(":");
				int propertyIndex = 0;
				int descriptionIndex = 1;

				// 读取每一行数据并提取 "property" 和 "description" 列的值
				String line;
				while ((line = reader.readLine()) != null) {
					String[] columns = line.split(":");
					if (propertyIndex >= 0 && descriptionIndex >= 0 && columns.length > descriptionIndex) {
						String property = columns[propertyIndex].trim();
						String description = columns[descriptionIndex].trim();
						this.addOptionDescription(property, description);
//						System.out.println(line);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}



	}
//	UserManual() {
//
//	}

	private static int findColumnIndex(String[] headers, String columnName) {
		for (int i = 0; i < headers.length; i++) {
			if (headers[i].trim().equalsIgnoreCase(columnName)) {
				return i;
			}
		}
		return -1;
	}

	void addOptionDescription(String optionName, String description) {
		Utils.checkTrue(!options.containsKey(optionName), "Already contain: " + optionName);
		//add to the option map
		options.put(optionName, description);
	}

	public String getDescription(String option) {
		if(options.isEmpty())
			System.out.println("Options is empty!");
		Utils.checkTrue(options.containsKey(option));
		return options.get(option);
	}

	public Set<String> getAllOptions() {
		return this.options.keySet();
	}

	public Collection<String> getAllTextDesc() {
		return this.options.values();
	}

	public boolean if_empyt() {return options.isEmpty();}
}