package score.provider;



import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import utility.FileMapLoader;
import utility.MiscUtility;
import utility.SelectedBugs;
import loader.BugFixCommitDateLoader;
import loader.BugReportDateLoader;
import loader.BugReporterLoader;
import loader.GoldsetLoader;

public class combinedScore {

    public HashMap<Integer, Date> brDateMap;
    public HashMap<Date, ArrayList<Integer>> dateBFCommitMap;
    public HashMap<Integer, String> fileKeyMap;
    //public ArrayList<Integer> selectedSTBugs;
    public HashMap<Integer, String> bugAuthorMap;
    public HashMap<String, ArrayList<Integer>> authorMap;
    //HashMap<Integer, HashMap<Integer, Double>> buglocatorScoreMap;
    HashMap<Integer, HashMap<Integer, Double>> bluirScoreMap;
    HashMap<Integer, HashMap<Integer, Double>> bluamirScoreMap;
    //HashMap<Integer, HashMap<Integer, Double>> brickScoreMap;
    String repoName;
    HashMap<Integer, ArrayList<Integer>> goldmap;

    public combinedScore(String repoName) {
        this.brDateMap = new HashMap<>();
        this.dateBFCommitMap = new HashMap<>();
        this.fileKeyMap = new HashMap<>();
        //this.selectedSTBugs = new ArrayList<>();
        this.bugAuthorMap = new HashMap<>();
        this.authorMap = new HashMap<>();
        this.repoName = repoName;
        //this.buglocatorScoreMap = new HashMap<>();
        this.bluirScoreMap = new HashMap<>();
        this.bluamirScoreMap=new HashMap<>();
        //this.brickScoreMap = new HashMap<>();
        this.goldmap = new HashMap<>();

        this.loadHeavyMetals();
    }

    protected void loadHeavyMetals() {
        // loading the heavy items
        this.brDateMap = new BugReportDateLoader(repoName).loadBugReportDate();
        this.dateBFCommitMap = new BugFixCommitDateLoader(repoName)
                .loadDateCommitMap();
        this.fileKeyMap = FileMapLoader.loadKeyMap(repoName);
        //this.selectedSTBugs = SelectedBugs.getStackSelectedBugs(repoName);
        BugReporterLoader brloader = new BugReporterLoader(repoName);
        this.bugAuthorMap = brloader.loadBugAuthors();
        this.authorMap = brloader.loadAuthorMap();

        // loading gold map
        this.goldmap = GoldsetLoader.getGoldsetWithFileIDs(repoName);
    }

    public void loadBLUiRScores() {
        BLUiRScoreProvider bluir = new BLUiRScoreProvider(repoName, this);
        this.bluirScoreMap = bluir.extractBLUiRScores();
    }

    public void loadBLuAMIRScores()
    {
        BLuAMIRScoreProvider bluamir = new BLuAMIRScoreProvider(repoName, this);
        this.bluamirScoreMap =bluamir.extractBLuAMIRScores();
    }
    
    /*public void loadBLScores() {
        BugLocatorScoreProvider blocator = new BugLocatorScoreProvider(
                repoName, this);
        this.buglocatorScoreMap = blocator.extractBugLocatorScores();
    }*/

    /*public void loadBRScores() {
        BRICKScoreProvider brick = new BRICKScoreProvider(repoName, this);
        this.brickScoreMap = brick.extractBRICKScores();
    }*/
}

