package utility;

import java.util.ArrayList;



public class SelectedBugs {

	public static ArrayList<Integer> getSelectedBugs(String repoName) {
		String bugFile = "E:\\PhD\\BugLocatorP2\\selectedbugs\\" +repoName+"\\"
				+ "selectedbug"+repoName+".txt";
		return ContentLoader.getAllLinesInt(bugFile);
	}

	/*public static ArrayList<Integer> getStackSelectedBugs(String repoName) {
		String bugFile = StaticData.BRICK_EXP + "/selectedbug/" + repoName
				+ "/selectedbugs-ST.txt";
		return ContentLoader.getAllLinesInt(bugFile);
	}

	public static ArrayList<Integer> getPESelectedBugs(String repoName) {
		String bugFile = StaticData.BRICK_EXP + "/selectedbug/" + repoName
				+ "/selectedbugs-PE.txt";
		return ContentLoader.getAllLinesInt(bugFile);
	}*/

	/*public static ArrayList<Integer> getNLSelectedBugs(String repoName) {
		String bugFile = StaticData.BRICK_EXP + "/selectedbug/" + repoName
				+ "/selectedbugs-NL.txt";
		return ContentLoader.getAllLinesInt(bugFile);
	}

	public static ArrayList<Integer> getHQBaselineBugs(String repoName) {
		ArrayList<Integer> toogood = new ArrayList<>();
		String baseRankFile = StaticData.BRICK_EXP + "/baseline/rank/"
				+ repoName + ".txt";
		ArrayList<String> lines = ContentLoader
				.getAllLinesOptList(baseRankFile);
		for (String line : lines) {
			String[] parts = line.split("\\s+");
			int bugID = Integer.parseInt(parts[0].trim());
			int rank = Integer.parseInt(parts[1].trim());
			if (rank == 1) {
				toogood.add(bugID);
			}
		}
		return toogood;
	}
	
	public static ArrayList<Integer> getSampledBugs(String repoName,
			int sampleNo) {
		String bugFile = StaticData.BRICK_EXP + "/selectedbug/" + repoName
				+ "/sampled-" + sampleNo + ".txt";
		return ContentLoader.getAllLinesInt(bugFile);
	}*/
}
