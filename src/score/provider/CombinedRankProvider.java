package score.provider;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import config.StaticData;
import utility.ContentWriter;
import utility.ItemSorter;
import utility.SelectedBugs;

public class CombinedRankProvider {

    String repoName;
    ArrayList<Integer> selectedBugs = new ArrayList<>();
    combinedScore comSc;
    int TOPK = -1; // not initialized
    String resultFile;

    public CombinedRankProvider(String repoName) {
        this.repoName = repoName;
        this.selectedBugs = SelectedBugs.getSelectedBugs(repoName);
        this.comSc = new combinedScore(repoName);
        this.comSc.loadBLUiRScores();
        this.comSc.loadBLuAMIRScores();
        //this.comSc.loadBLScores();
        this.resultFile = StaticData.BLP2_EXP + "/CombinedResults/"
                + repoName + "/"+repoName +"combinedresults.txt";
    }

    public CombinedRankProvider(String repoName, int TOPK) {
        this.repoName = repoName;
        this.TOPK = TOPK;
        this.selectedBugs = SelectedBugs.getSelectedBugs(repoName);
        this.comSc = new combinedScore(repoName);
        this.comSc.loadBLUiRScores();
        this.comSc.loadBLuAMIRScores();
        //this.comSc.loadBLScores();
        this.resultFile = StaticData.BLP2_EXP + "/CombinedResults/"
                + repoName + "/"+repoName +"combinedresults.txt";
    }

    public CombinedRankProvider(String repoName, String resKey, int TOPK) {
        this.repoName = repoName;
        this.TOPK = TOPK;
        this.selectedBugs = SelectedBugs.getSelectedBugs(repoName);
        this.comSc = new combinedScore(repoName);
        this.comSc.loadBLUiRScores();
        this.comSc.loadBLuAMIRScores();
        //this.comSc.loadBLScores();
        //this.comSc.loadBRScores();
        this.resultFile = StaticData.BLP2_EXP + "/CombinedResults/"
                + repoName + "/"+repoName +"combinedresults.txt";
    }

    public CombinedRankProvider(String repoName, int TOPK, combinedScore comSc,
            ArrayList<Integer> selectedBugs) {
        // weight tuning purpose only
        this.repoName = repoName;
        this.TOPK = TOPK;
        this.selectedBugs = selectedBugs;
        this.comSc = comSc;
        this.comSc.loadBLuAMIRScores();
        this.comSc.loadBLUiRScores();
        // this.amal.loadBLScores();
    }

    protected HashMap<Integer, Double> developFinalRankMap(double weight,
            HashMap<Integer, Double> myMap,
            HashMap<Integer, Double> individualMap) {
        if (weight > 0.0) {
            for (int fileID : individualMap.keySet()) {
                double score = individualMap.get(fileID);
                if (myMap.containsKey(fileID)) {
                    double updated = myMap.get(fileID) + score*weight;
                    myMap.put(fileID, updated);
                } else {
                    myMap.put(fileID, score*weight);
                }
            }
        }
        return myMap;
    }

   /* public double getFitness(int[] weights) {
        HashMap<Integer, ArrayList<String>> resultMap = collectAmaLgamResultRanks(weights);
        AmaLgamPerformanceCalc pcalc = new AmaLgamPerformanceCalc(repoName, 10,
                resultMap);
        double mapmrrk = pcalc.getMAPMRRK();
        return Math.exp(mapmrrk);
    }
*/
    protected HashMap<Integer, Double> calculateFinalScores(double[] weights,
            //HashMap<Integer, Double> blocator, 
            HashMap<Integer, Double> bluamir,
            HashMap<Integer, Double> bluir,
            HashMap<Integer, Double> vhistory,
           // HashMap<Integer, Double> stboosting,
            HashMap<Integer, Double> authorInfo
            //HashMap<Integer, Double> brickMap
            ) {
        // order of the parameters matter
        HashMap<Integer, Double> myMap = new HashMap<>();
        myMap = developFinalRankMap(weights[0], myMap, bluamir);
        myMap = developFinalRankMap(weights[1], myMap, bluir);
        myMap = developFinalRankMap(weights[2], myMap, vhistory);
       // myMap = developFinalRankMap(weights[3], myMap, stboosting);
        myMap = developFinalRankMap(weights[3], myMap, authorInfo);
        //myMap = developFinalRankMap(weights[5], myMap, brickMap);
        //System.out.println(myMap);
        return myMap;
    }

    /*
    @Deprecated
    protected HashMap<Integer, ArrayList<String>> collectAmaLgamResultRanks(
            int[] weights) {
        HashMap<Integer, ArrayList<String>> resultMap = new HashMap<>();
        for (int bugID : this.selectedBugs) {
            // bug-locator
            HashMap<Integer, Double> blocatorMap = new HashMap<>();
            if (comSc.buglocatorScoreMap.containsKey(bugID)) {
                blocatorMap = amal.buglocatorScoreMap.get(bugID);
            }
            // bluir score
            HashMap<Integer, Double> bluirMap = new HashMap<>();
            if (amal.bluirScoreMap.containsKey(bugID)) {
                bluirMap = amal.bluirScoreMap.get(bugID);
            }

            // version history
            VersionHistoryScoreProvider vhp = new VersionHistoryScoreProvider(
                    bugID, repoName, amal);
            HashMap<Integer, Double> vhMap = vhp.getSuspicionScores();

            // stack trace score
            StackTraceScoreProvider stp = new StackTraceScoreProvider(bugID,
                    repoName, amal);
            HashMap<Integer, Double> stMap = stp.getStackTraceScores();

            // author scores
            AuthorScoreProvider asp = new AuthorScoreProvider(repoName, bugID,
                    amal);
            HashMap<Integer, Double> asMap = asp.collectAuthorScores();

            // now add all scores
            HashMap<Integer, Double> myMap = new HashMap<>();
            myMap = calculateFinalScores(weights, blocatorMap, bluirMap, vhMap,
                    stMap, asMap, null); // brick map not provided

            // now do the sorting
            List<Map.Entry<Integer, Double>> sorted = ItemSorter
                    .sortHashMapIntDouble(myMap);
            ArrayList<String> temp = new ArrayList<>();

            for (Map.Entry<Integer, Double> entry : sorted) {
                String line = amal.fileKeyMap.get(entry.getKey());
                temp.add(line);
                if (temp.size() == TOPK) {
                    break;
                }
            }
            resultMap.put(bugID, temp);
        }
        return resultMap;

    }*/

    protected void collectCombinedResultRanksBasic(double[] weights) {
        // collect AmalGam ranks
        ArrayList<String> results = new ArrayList<>();

        for (int bugID : this.selectedBugs) {
            
            //if(bugID!=112597)continue;

            /*// bug-locator
            HashMap<Integer, Double> blocatorMap = new HashMap<>();
            if (amal.buglocatorScoreMap.containsKey(bugID)) {
                blocatorMap = amal.buglocatorScoreMap.get(bugID);
            }*/
            
            // bluAMIR score
            HashMap<Integer, Double> bluamirMap = new HashMap<>();
            if (comSc.bluamirScoreMap.containsKey(bugID)) {
                bluamirMap = comSc.bluamirScoreMap.get(bugID);
            }

            // bluir score
            HashMap<Integer, Double> bluirMap = new HashMap<>();
            if (comSc.bluirScoreMap.containsKey(bugID)) {
                bluirMap = comSc.bluirScoreMap.get(bugID);
            }

            /*// BRICK scores
            HashMap<Integer, Double> brickMap = new HashMap<>();
            if (amal.brickScoreMap.containsKey(bugID)) {
                brickMap = amal.brickScoreMap.get(bugID);
            }*/

            // version history
            VersionHistoryScoreProvider vhp = new VersionHistoryScoreProvider(
                    bugID, repoName, comSc);
            HashMap<Integer, Double> vhMap = vhp.getSuspicionScores();

            // stack trace score
            //StackTraceScoreProvider stp = new StackTraceScoreProvider(bugID,
                    //repoName, amal);
            //HashMap<Integer, Double> stMap = stp.getStackTraceScores();

            // author scores
            AuthorScoreProvider asp = new AuthorScoreProvider(repoName, bugID,
                    comSc);
            HashMap<Integer, Double> asMap = asp.collectAuthorScores();

            // now add all scores
            // int weights[] = { 1, 1, 1, 1, 1 };
            System.out.println(bugID);
            //if(vhMap.size()>0){
            System.out.println("BLuAMIR map: "+bluamirMap);
            System.out.println("BLUiR map: "+bluirMap);
            System.out.println("Verion map: "+vhMap);
            System.out.println("Author map: "+asMap);
            //System.out.println(bluirMap);
            //System.out.println(vhMap);
            //System.out.println(asMap);
            HashMap<Integer, Double> myMap = new HashMap<>();
            myMap = calculateFinalScores(weights, 
                    //blocatorMap, 
                    bluamirMap,
                    bluirMap, 
                    vhMap,
                    //stMap, 
                    asMap
                    //brickMap
                    );

            // now do the sorting
            List<Map.Entry<Integer, Double>> sorted = ItemSorter
                    .sortHashMapIntDouble(myMap);
            ArrayList<String> temp = new ArrayList<>();
            System.out.println(sorted);
            for (Map.Entry<Integer, Double> entry : sorted) {
                String line = bugID + "\t"
                        + comSc.fileKeyMap.get(entry.getKey());
                temp.add(line);
                if (temp.size() == TOPK) {
                    break;
                }
            }
            // adding the ranked results
            results.addAll(temp);

            // System.out.println(bugID);
        }
        // now save the output
        ContentWriter.writeContent(resultFile, results);
        System.out.println("Done:" + repoName);
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        long start = System.currentTimeMillis();
        /*String[] repos = { "ecf", "eclipse.jdt.core", "eclipse.jdt.debug",
                "eclipse.jdt.ui", "eclipse.pde.ui", "tomcat70" };*/
        
        String[] repos={"SWT"};
        
        //BRICKScoreProvider.TOP_CUT_FROM_BRICK=100000;
        
        for (String repoName : repos) {
            // String repoName = "ecf";
            //String resKey = "res-bl-bluir-vhc-st-as-brick-v7-UL-15";
            //String resKey = "res-bl-bluir-vhc-0-0-0-v4";
            //String resKey="brtracer-bl-0-0-st-0-0-v3";
            //String resKey="res-bl-bluir-vhc-st-as-0-v4-15";
            //String resKey="res-0-bluir-0-st-0-brick-v7-UL-15";
            //String resKey="res-bl-bluir-vhc-st-as-0-v3-1000";
            
            
            int TOPK = 1000;
            double[] weights = new double[] {0, 1 , 0, 0};
            new CombinedRankProvider(repoName, TOPK)
                    .collectCombinedResultRanksBasic(weights);
        }
        long end = System.currentTimeMillis(); 
        System.out.println("Time needed:" + (end - start) / 1000 + " s");
    }
}

