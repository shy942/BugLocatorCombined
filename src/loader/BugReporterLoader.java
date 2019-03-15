package loader;

import java.util.ArrayList;
import java.util.HashMap;
import utility.ContentLoader;
import config.StaticData;

public class BugReporterLoader {

	String repoName;
	HashMap<Integer, String> bugAuthorMap;
	HashMap<String, ArrayList<Integer>> authorMap;
	String authorFile;

	public BugReporterLoader(String repoName) {
		this.repoName = repoName;
		this.bugAuthorMap = new HashMap<>();
		this.authorMap = new HashMap<>();
		this.authorFile = StaticData.BLP2_EXP
				+ "/versionhistory/author/" + repoName + "AuthorInfo.txt";
		this.loadBRAuthors();
	}

	protected void loadBRAuthors() {
		// loading BR authors
		ArrayList<String> lines = ContentLoader
				.getAllLinesOptList(this.authorFile);
		for (String line : lines) {
			String[] parts = line.split("\\s+");
			if (parts.length == 2) {
				int bugID = Integer.parseInt(parts[0].trim());
				String authorName = parts[1].trim();
				this.bugAuthorMap.put(bugID, authorName);
				if (authorMap.containsKey(authorName)) {
					ArrayList<Integer> temp = authorMap.get(authorName);
					temp.add(bugID);
					authorMap.put(authorName, temp);
				} else {
					ArrayList<Integer> temp = new ArrayList<>();
					temp.add(bugID);
					authorMap.put(authorName, temp);
				}
			}
		}
	}

	public HashMap<Integer, String> loadBugAuthors() {
		return this.bugAuthorMap;
	}

	public HashMap<String, ArrayList<Integer>> loadAuthorMap() {
		return this.authorMap;
	}
}
