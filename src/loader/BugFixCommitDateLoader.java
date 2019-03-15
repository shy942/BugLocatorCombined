package loader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utility.ContentLoader;
import config.StaticData;

public class BugFixCommitDateLoader {

	String repoName;
	HashMap<Date, ArrayList<Integer>> dateCommitMap;
	String commitLogFile;

	public BugFixCommitDateLoader(String repoName) {
		this.repoName = repoName;
		this.dateCommitMap = new HashMap<>();
		this.commitLogFile = StaticData.BLP2_EXP
				+ "/versionhistory/commitlog/" + repoName + ".txt";
	}

	protected int extractBugID(String title) {
		int bugID = -1;
		String numRegex = "((Bug|bug)\\s+\\d+)|id=\\d+|SLING-\\d+";
		Pattern p = Pattern.compile(numRegex);
		if (title.contains("Bug") || title.contains("Fix")
				|| title.contains("bug") || title.contains("fix")) {
			Matcher m = p.matcher(title);
			while (m.find()) {
				String bugIDStr = title.substring(m.start(), m.end());
				String[] parts = bugIDStr.split("\\s+");
				if (parts.length == 2) {
					bugID = Integer.parseInt(parts[1].trim());
					break;
				}
			}
		}
		return bugID;
	}

	public HashMap<Date, ArrayList<Integer>> loadDateCommitMap() {
		// loading the date commit map
		ArrayList<String> lines = ContentLoader
				.getAllLinesOptList(this.commitLogFile);
		HashMap<String, ArrayList<Integer>> dateCLMap = new HashMap<>();

		for (String line : lines) {
			String[] parts = line.split("\\s+");

			String dateKey = parts[0].trim();
			String title = line.replaceFirst(parts[0], "\t").trim();
			int bugID = extractBugID(title);
			if (bugID > 0) {
				if (dateCLMap.containsKey(dateKey)) {
					ArrayList<Integer> bugIDs = dateCLMap.get(dateKey);
					bugIDs.add(bugID);
					dateCLMap.put(dateKey, bugIDs);
				} else {
					ArrayList<Integer> bugIDs = new ArrayList<>();
					bugIDs.add(bugID);
					dateCLMap.put(dateKey, bugIDs);
				}
			}
		}
		// now transform the dateKey to dates
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (String dateKey : dateCLMap.keySet()) {
			ArrayList<Integer> bugIDs = dateCLMap.get(dateKey);
			try {
				Date date = sdf.parse(dateKey);
				this.dateCommitMap.put(date, bugIDs);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return this.dateCommitMap;
	}

	public static void main(String[] args) {
		String repoName = "ecf";
		System.out.println(new BugFixCommitDateLoader(repoName)
				.loadDateCommitMap());

	}
}
