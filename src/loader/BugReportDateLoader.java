package loader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import utility.ContentLoader;
import config.StaticData;

public class BugReportDateLoader {

	String repoName;
	HashMap<Integer, Date> brDateMap;
	String dateFile;

	public BugReportDateLoader(String repoName) {
		this.repoName = repoName;
		this.brDateMap = new HashMap<>();
		this.dateFile = StaticData.BLP2_EXP
				+ "/versionhistory/date/" + repoName + "dateInfo.txt";
	}

	public HashMap<Integer, Date> loadBugReportDate() {
		// loading the bug report date
		ArrayList<String> lines = ContentLoader
				.getAllLinesOptList(this.dateFile);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (String line : lines) {
			int bugID = Integer.parseInt(line.split("\\s+")[0].trim());
			String date = line.replaceFirst(bugID + "", " ").trim();
			try {
				Date rdate = sdf.parse(date);
				this.brDateMap.put(bugID, rdate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.brDateMap;
	}

	public static void main(String[] args) {
		String repoName="ecf";
		System.out.println(new BugReportDateLoader(repoName).loadBugReportDate());
	}
}
