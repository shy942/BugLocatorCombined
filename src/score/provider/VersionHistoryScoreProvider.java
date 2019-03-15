package score.provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import utility.FileMapLoader;
import loader.GoldsetLoader;
import loader.BugFixCommitDateLoader;
import loader.BugReportDateLoader;


public class VersionHistoryScoreProvider {

	HashMap<Integer, Date> brDateMap;
	HashMap<Date, ArrayList<Integer>> dateBFCommitMap;
	HashMap<Integer, String> fileKeyMap;
	String repoName;
	int bugID;
	combinedScore comSc;

	public VersionHistoryScoreProvider(int bugID, String repoName) {
		this.repoName = repoName;
		this.bugID = bugID;
		this.brDateMap = new BugReportDateLoader(repoName).loadBugReportDate();
		this.dateBFCommitMap = new BugFixCommitDateLoader(repoName)
				.loadDateCommitMap();
		this.fileKeyMap = FileMapLoader.loadKeyMap(repoName);
	}

	public VersionHistoryScoreProvider(int bugID, String repoName, combinedScore comSc) {
		this.repoName = repoName;
		this.bugID = bugID;
		this.comSc = comSc;
		this.brDateMap = comSc.brDateMap;
		this.dateBFCommitMap = comSc.dateBFCommitMap;
		this.fileKeyMap = comSc.fileKeyMap;
	}

	protected HashMap<Integer, Integer> collectCandidateFixedBugTime() {
		HashMap<Integer, Integer> candidateBugTimeMap = new HashMap<>();
		if (this.brDateMap.containsKey(bugID)) {
			Date reportDate = this.brDateMap.get(bugID);
			for (Date cdate : this.dateBFCommitMap.keySet()) {
			    long reportTime=reportDate.getTime();
			    long cdateTime=cdate.getTime();
				long diff = reportDate.getTime() - cdate.getTime();
				if (diff > 0) {
					long dayDiff = TimeUnit.DAYS.convert(diff,
							TimeUnit.MILLISECONDS);
					if (dayDiff > 0 && dayDiff <= 15) {
						ArrayList<Integer> bugIDs = dateBFCommitMap.get(cdate);
						for (int cbugID : bugIDs) {
							candidateBugTimeMap.put(cbugID, (int) dayDiff);
						}
					}
				}
			}
		}
		return candidateBugTimeMap;
	}

	protected HashSet<Integer> getGoldsetFileIDs(int cbugID) {
		ArrayList<String> files = GoldsetLoader.goldsetLoader(repoName, cbugID);
		HashSet<Integer> temp = new HashSet<>();
		for (int key : this.fileKeyMap.keySet()) {
			String canonical = this.fileKeyMap.get(key);
			for (String fileURL : files) {
				String nfileURL = fileURL.replaceAll("/", ".");
				if (canonical.endsWith(nfileURL)) {
					temp.add(key);
					break;
				}
			}
			if (files.size() == temp.size())
				break;
		}
		return temp;
	}

	protected HashSet<Integer> getGoldsetFileIDsOpt(int cbugID) {
		HashSet<Integer> golds = new HashSet<>();
		if (this.comSc.goldmap.containsKey(cbugID)) {
			golds.addAll(this.comSc.goldmap.get(cbugID));
		}
		return golds;
	}

	protected double calculateScore(int tc) {
		double k = 15;
		double expression = 12 * (1 - ((k - tc) / k));
		double totalExp = 1 + Math.exp(expression);
		if (totalExp != 0) {
			return 1.0 / totalExp;
		}
		return 0;
	}

	protected HashMap<Integer, Double> getSuspicionScores() {
		HashMap<Integer, Integer> candidateBugTimeMap = collectCandidateFixedBugTime();
		HashMap<Integer, Double> scoreMap = new HashMap<>();
		for (int cbugID : candidateBugTimeMap.keySet()) {
			int dayDiff = candidateBugTimeMap.get(cbugID);
			HashSet<Integer> changedFiles = getGoldsetFileIDsOpt(cbugID);
			for (int fileID : changedFiles) {
				double suspicion = calculateScore(dayDiff);
				if (scoreMap.containsKey(fileID)) {
					double updated = scoreMap.get(fileID) + suspicion;
					scoreMap.put(fileID, updated);
				} else {
					scoreMap.put(fileID, suspicion);
				}
			}
		}
		return scoreMap;
	}

	public static void main(String[] args) {
		String repoName = "Eclipse";
		int bugID = 75967;
		System.out.println(new VersionHistoryScoreProvider(bugID, repoName, new combinedScore(repoName))
				.getSuspicionScores());
	}
}
