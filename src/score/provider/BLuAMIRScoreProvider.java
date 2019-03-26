package score.provider;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import config.StaticData;
import utility.ContentLoader;
import utility.MiscUtility;
import utility.SelectedBugs;

public class BLuAMIRScoreProvider {

    String repoName;
    String resultFile;
    HashMap<Integer, String> fileKeyMap;
    ArrayList<Integer> selectedBugs;

    public BLuAMIRScoreProvider(String repoName, combinedScore amal) {
        this.repoName = repoName;
        this.resultFile = StaticData.BLP2_EXP
                + "/results/" + repoName + "/BLuAMIR/results"+repoName+"AsSc.txt";
        this.fileKeyMap = amal.fileKeyMap;
        this.selectedBugs = SelectedBugs.getSelectedBugs(repoName);
    }

    protected HashMap<Integer, HashMap<Integer, Double>> extractBLuAMIRScores() {
        ArrayList<String> lines = ContentLoader
                .getAllLinesOptList(this.resultFile);
        HashMap<Integer, HashMap<String, Double>> resultMap = new HashMap<>();
        HashSet<String> uniqueFiles = new HashSet<>();
        
        for (String line : lines) {
            System.out.println(line);
            String[] parts = line.trim().split(",");
            int bugID = Integer.parseInt(parts[0]);
            if (this.selectedBugs.contains(bugID)) {
                //int indexline=0;
                // System.out.println(bugID);
                String fileURL = parts[1].trim();
                uniqueFiles.add(fileURL);
                double score = Double.parseDouble(parts[2].trim());
                if (score > 0) {
                    if (resultMap.containsKey(bugID)) {
                        HashMap<String, Double> fileMap = resultMap.get(bugID);
                        fileMap.put(fileURL, score);
                        resultMap.put(bugID, fileMap);
                    } else {
                        HashMap<String, Double> fileMap = new HashMap<>();
                        fileMap.put(fileURL, score);
                        resultMap.put(bugID, fileMap);
                    }
                }
            }
        }

        // now get file 2 ID mapping
        HashMap<String, Integer> file2IDMap = getFile2IDMap(uniqueFiles);
        HashMap<Integer, HashMap<Integer, Double>> scoreMap = new HashMap<>();
        for (int bugID : resultMap.keySet()) {
            HashMap<String, Double> fileMap = resultMap.get(bugID);
            HashMap<String, Double> sortedFileMap=MiscUtility.sortByValues(fileMap);
            HashMap<Integer, Double> temp = new HashMap<>();
            for (String fileName : sortedFileMap.keySet()) {
                if (file2IDMap.containsKey(fileName)) {
                    int fileID = file2IDMap.get(fileName);
                    temp.put(fileID, fileMap.get(fileName));
                }
            }
            scoreMap.put(bugID, temp);
        }

        return scoreMap;
    }

    protected HashMap<String, Integer> getFile2IDMap(HashSet<String> uniqueFiles) {
        HashMap<String, Integer> file2IDMap = new HashMap<>();
        for (int fileID : this.fileKeyMap.keySet()) {
            String canonical = this.fileKeyMap.get(fileID);
            for (String myFile : uniqueFiles) {
                if (canonical.equalsIgnoreCase(myFile)==true) {
                    file2IDMap.put(myFile, fileID);
                    break;
                }
            }
        }
        return file2IDMap;
    }

    public static void main(String[] args) {
        String repoName = "ZXing";
        combinedScore comSc = new combinedScore(repoName);
        System.out.println(new BLuAMIRScoreProvider(repoName, comSc)
                .extractBLuAMIRScores());
    }
}
