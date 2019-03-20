package performance.calculator;



import java.util.ArrayList;
import java.util.HashMap;
import utility.ContentLoader;
import loader.GoldsetLoader;
import utility.SelectedBugs;
import config.StaticData;

public class pCalculator {

    String repoName;
    String resultFile;
    int TOPK = 10;
    String catKey;
    ArrayList<Integer> selectedBugs;
    HashMap<Integer, ArrayList<String>> resultMap;

    static double stAcc = 0;
    static double peAcc = 0;
    static double nlAcc = 0;

    static double sumAcc = 0;
    static double sumMRR = 0;
    static double sumMAP = 0;
    static double sumMR = 0;

    static int totalBug = 0;

    double TopKAcc;
    double mapK;
    double mrrK;
    double mrK;

    public pCalculator(String repoName) {
        this.repoName = repoName;
        this.resultFile = StaticData.BLP2_EXP + "/CombinedResults/"
                + repoName + "/"+repoName+"combinedresults.txt";
        this.selectedBugs = SelectedBugs.getSelectedBugs(repoName);
        this.resultMap = this.extractResults();
    }

    public pCalculator(String repoName, int TOPK) {
        this.repoName = repoName;
        this.TOPK = TOPK;
        this.resultFile = StaticData.BLP2_EXP + "/CombinedResults/"
                + repoName + "/"+repoName+"combinedresults.txt";
        this.selectedBugs = SelectedBugs.getSelectedBugs(repoName);
        this.resultMap = this.extractResults();
    }

    public pCalculator(String repoName, String resKey, int TOPK) {
        this.repoName = repoName;
        this.TOPK = TOPK;
        this.resultFile = StaticData.BLP2_EXP + "/CombinedResults/"
                + repoName + "/"+repoName+"combinedresults.txt";
        this.selectedBugs = SelectedBugs.getSelectedBugs(repoName);
        this.resultMap = this.extractResults();
    }

    /*public AmaLgamPerformanceCalc(String repoName, String resKey, int TOPK,
            String catKey) {
        this.repoName = repoName;
        this.TOPK = TOPK;
        this.resultFile = StaticData.BRICK_EXP + "/amalgam-plus/result/"
                + repoName + "/" + resKey + ".txt";
        switch (catKey) {
        case "ST":
            this.selectedBugs = SelectedBugs.getStackSelectedBugs(repoName);
            break;
        case "PE":
            this.selectedBugs = SelectedBugs.getPESelectedBugs(repoName);
            break;
        case "NL":
            this.selectedBugs = SelectedBugs.getNLSelectedBugs(repoName);
            break;
        default:
            this.selectedBugs = SelectedBugs.getSelectedBugs(repoName);
            break;
        }
        this.resultMap = this.extractResults();
    }*/

   /* public AmaLgamPerformanceCalc(String repoName, int TOPK, String catKey,
            ArrayList<Integer> selectedBugs) {
        this.repoName = repoName;
        this.resultFile = StaticData.BRICK_EXP + "/amalgam-plus/result/"
                + repoName + ".txt";
        this.TOPK = TOPK;
        this.catKey = catKey;
        this.selectedBugs = selectedBugs;
        this.resultMap = this.extractResults();
    }

    public AmaLgamPerformanceCalc(String repoName, int TOPK,
            HashMap<Integer, ArrayList<String>> resultMap) {
        this.repoName = repoName;
        this.TOPK = TOPK;
        this.resultMap = resultMap;
        this.selectedBugs = new ArrayList<>(resultMap.keySet());
    }*/

    protected String refineFileURL(String fileURL) {
        int sIndex = fileURL.indexOf("ssystems");
        fileURL = fileURL.substring(sIndex);
        return fileURL;
    }

    protected HashMap<Integer, ArrayList<String>> extractResults() {
        ArrayList<String> lines = ContentLoader
                .getAllLinesOptList(this.resultFile);
        HashMap<Integer, ArrayList<String>> resultMap = new HashMap<>();
        for (String line : lines) {
            if (line.trim().isEmpty())
                continue;
            String[] parts = line.trim().split("\\s+");

            int bugID = Integer.parseInt(parts[0].trim());
            if (this.selectedBugs.contains(bugID)) {
                // System.out.println(bugID);
                String fileURL = parts[1].trim();
                //fileURL = refineFileURL(fileURL);
                if (resultMap.containsKey(bugID)) {
                    ArrayList<String> files = resultMap.get(bugID);
                    files.add(fileURL);
                    resultMap.put(bugID, files);
                } else {
                    ArrayList<String> files = new ArrayList<>();
                    files.add(fileURL);
                    resultMap.put(bugID, files);
                }
            }
        }
        return resultMap;
    }

    public double getMAPMRRK() {
        this.calculateBIRPerformance();
        return this.getMAPK() + this.getMRRK();
    }

    protected void calculateBIRPerformance() {

        double sumPrecK = 0;
        double sumRecK = 0;
        double sumrrk = 0;
       // System.out.println(resultMap);
        for (int bugID : this.selectedBugs) {
            if (resultMap.containsKey(bugID)) {
                
                //if(bugID!=187316)continue;
                
                ArrayList<String> resultFiles = resultMap.get(bugID);
                // System.out.println(resultFiles.size());
                ArrayList<String> goldFiles = GoldsetLoader.goldsetLoader(
                        repoName, bugID);
                boolean found = checkSolutionFound(goldFiles, resultFiles);
                if (found) {
                    TopKAcc++;
                    //System.out.println(bugID);
                    double preck = getAvgPrecisionK(resultFiles, goldFiles,
                            TOPK);
                    sumPrecK += preck;
                    double reck = getRecallK(resultFiles, goldFiles, TOPK);
                    sumRecK += reck;
                    double rrK = getRRank(resultFiles, goldFiles, TOPK);
                    sumrrk += rrK;
                }
            }
        }
        // now get the mean
        //System.out.println(this.selectedBugs.size());
        //System.out.println(TopKAcc);
        this.TopKAcc = TopKAcc / this.selectedBugs.size();
        this.mapK = sumPrecK / this.selectedBugs.size();
        this.mrrK = sumrrk / this.selectedBugs.size();
        this.mrK = sumRecK / this.selectedBugs.size();

        System.out.println(this.TopKAcc + ",\t" + this.mrrK + ",\t" + this.mapK
              + ",\t" + this.mrK);
        
        //System.out.println(this.getTopKAcc()+",");
        //System.out.println(this.getMRRK()+",");
        //System.out.println(this.getMAPK()+",");

        sumAcc += this.TopKAcc;
        sumMRR += this.mrrK;
        sumMAP += this.mapK;
        sumMR += this.mrK;

    }

    protected double getTopKAcc() {
        return this.TopKAcc;
    }

    protected double getMAPK() {
        return this.mapK;
    }

    protected double getMRRK() {
        return this.mrrK;
    }

    protected double getMRK() {
        return this.mrK;
    }

    /*
    @Deprecated
    protected void calculatePerformance() {
        // calculate the performance of ST bugs
        ArrayList<Integer> stackBugs = SelectedBugs
                .getStackSelectedBugs(repoName);
        ArrayList<Integer> peBugs = SelectedBugs.getPESelectedBugs(repoName);
        ArrayList<Integer> nlBugs = SelectedBugs.getNLSelectedBugs(repoName);
        ArrayList<Integer> allBugs = SelectedBugs.getSelectedBugs(repoName);

        ArrayList<String> lines = ContentLoader
                .getAllLinesList(this.resultFile);

        HashMap<Integer, ArrayList<String>> resultMap = new HashMap<>();
        for (String line : lines) {
            String[] parts = line.trim().split("\\s+");
            int bugID = Integer.parseInt(parts[0]);
            if (!allBugs.contains(bugID))
                continue;
            // System.out.println(bugID);
            String fileURL = parts[2].trim();
            if (resultMap.containsKey(bugID)) {
                ArrayList<String> files = resultMap.get(bugID);
                files.add(fileURL);
                resultMap.put(bugID, files);
            } else {
                ArrayList<String> files = new ArrayList<>();
                files.add(fileURL);
                resultMap.put(bugID, files);
            }
        }

        // now calculate the performance
        int stackSuccess = 0;
        int peSuccess = 0;
        int nlSuccess = 0;
        int totalFound = 0;

        double stackSumPrecK = 0;
        double peSumPrecK = 0;
        double nlSumPrecK = 0;
        double sumPrecK = 0;

        double stackSumRRK = 0;
        double peSumRRK = 0;
        double nlSumRRK = 0;
        double sumRRK = 0;

        double stackSumRecallK = 0;
        double peSumRecallK = 0;
        double nlSumRecallK = 0;
        double sumRecallK = 0;

        for (int bugID : allBugs) {
            if (resultMap.containsKey(bugID)) {
                ArrayList<String> resultFiles = resultMap.get(bugID);
                // System.out.println(resultFiles.size());
                ArrayList<String> goldFiles = GoldsetLoader.goldsetLoader(
                        repoName, bugID);
                boolean found = checkSolutionFound(goldFiles, resultFiles);
                double preck = getPrecisionK(resultFiles, goldFiles, TOPK);
                double reck = getRecallK(resultFiles, goldFiles, TOPK);
                double rrK = getRRank(resultFiles, goldFiles, TOPK);

                if (found) {
                    if (stackBugs.contains(bugID)) {
                        stackSuccess++;
                        stackSumPrecK += preck;
                        stackSumRecallK += reck;
                        stackSumRRK += rrK;
                    } else if (peBugs.contains(bugID)) {
                        peSuccess++;
                        peSumPrecK += preck;
                        peSumRecallK += reck;
                        peSumRRK += rrK;
                    } else if (nlBugs.contains(bugID)) {
                        nlSuccess++;
                        nlSumPrecK += preck;
                        nlSumRecallK += reck;
                        nlSumRRK += rrK;
                    }
                    totalFound++;
                    sumPrecK += preck;
                    sumRecallK += reck;
                    sumRRK += rrK;
                } else {
                    // System.err.println(bugID);
                }
            }
        }

        // now show the performance
        System.out.println((double) stackSuccess / stackBugs.size() + "\t"
                + stackSumRRK / stackBugs.size() + "\t" + stackSumPrecK
                / stackBugs.size() + "\t" + stackSumRecallK / stackBugs.size());
        System.out.println((double) nlSuccess / nlBugs.size() + "\t" + nlSumRRK
                / nlBugs.size() + "\t" + nlSumPrecK / nlBugs.size() + "\t"
                + nlSumRecallK / nlBugs.size());
        System.out.println((double) peSuccess / peBugs.size() + "\t" + peSumRRK
                / peBugs.size() + "\t" + peSumPrecK / peBugs.size() + "\t"
                + peSumRecallK / peBugs.size());

        totalBug += allBugs.size();
    }
*/
    protected ArrayList<String> getCanonicalURLs(ArrayList<String> goldFiles) {
        ArrayList<String> canonicalList = new ArrayList<>();
        for (String fileURL : goldFiles) {
            String canonical = fileURL.replace('/', '.');
            canonicalList.add(canonical);
        }
        return canonicalList;
    }

    protected boolean checkSolutionFound(ArrayList<String> goldFiles,
            ArrayList<String> resultFiles) {
        // check if the solution matches with the results
        ArrayList<String> temp = getCanonicalURLs(goldFiles);
        // now compare the performance
        boolean found = false;
        int index = 0;
        outer: for (String resultFile : resultFiles) {
            index++;
            for (String goldFile : temp) {
                if (resultFile.endsWith(goldFile)) {
                    found = true;
                    //System.out.println(index);
                    return found;
                }
            }
            if (index == TOPK)
                break;
        }
        return found;
    }

    protected boolean checkEntryFound(ArrayList<String> goldFiles,
            String resultEntry) {
        ArrayList<String> canonicalGoldFiles = getCanonicalURLs(goldFiles);
        for (String goldFile : canonicalGoldFiles) {
            if (resultEntry.endsWith(goldFile)) {
                return true;
            }
        }
        return false;
    }

    protected double getRRank(ArrayList<String> rapis, ArrayList<String> gapis,
            int K) {
        K = rapis.size() < K ? rapis.size() : K;
        double rrank = 0;
        for (int i = 0; i < K; i++) {
            String api = rapis.get(i);
            if (checkEntryFound(gapis, api)) {
                //make it double
                rrank = 1.0 / (i + 1);
                break;
            }
        }
        return rrank;
    }

    protected double getPrecisionK(ArrayList<String> rapis,
            ArrayList<String> gapis, int K) {
        // getting precision at K
        if (rapis.size() > 0)
            K = rapis.size() < K ? rapis.size() : K;
        double found = 0;
        for (int index = 0; index < K; index++) {
            String api = rapis.get(index);
            if (checkEntryFound(gapis, api)) {
                found++;
            }
        }
        return found / K;
    }

    protected double getAvgPrecisionK(ArrayList<String> rapis,
            ArrayList<String> gapis, int K) {
        double linePrec = 0;
        K = rapis.size() < K ? rapis.size() : K;
        double found = 0;
        for (int index = 0; index < K; index++) {
            String api = rapis.get(index);
            if (checkEntryFound(gapis, api)) {
                found++;
                linePrec += (found / (index + 1));
            }
        }
        if (found == 0)
            return 0;

        return linePrec / found;
    }

    protected double getRecallK(ArrayList<String> rapis,
            ArrayList<String> gapis, int K) {
        // getting recall at K
        K = rapis.size() < K ? rapis.size() : K;
        double found = 0;
        for (int index = 0; index < K; index++) {
            String api = rapis.get(index);
            if (checkEntryFound(gapis, api)) {
                found++;
            }
        }
        return found / gapis.size();
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        /*String[] repos = { "ecf", "eclipse.jdt.core", "eclipse.jdt.debug",
                "eclipse.jdt.ui", "eclipse.pde.ui", "tomcat70" };*/
        
        String[] repos={"Eclipse"};
        
        
        //String resKey = "res-0-bluir-0-st-0-0-v3";
        //String resKey = "res-bl-bluir-vhc-st-as-0-v3";
        String resKey = "res-bl-bluir-vhc-st-as-0-v3-1000";
        //String resKey="res-bl-bluir-vhc-st-as-brick-v7-UL";
        //String resKey="res-bl-bluir-vhc-st-as-brick-v7-UL";
        //String resKey="res-bl-bluir-vhc-st-as-0-v4-15";
        //String resKey="res-0-bluir-0-st-0-brick-v7-UL-15";
        
        String catKey = "NL";
        int TOPK = 1000;

        int[] topks={1,5,10};
        for (int topk = 1; topk <= 10; topk++) 
        {
        //for(int topk:topks)   {
        //int topk=1;
        TOPK = topk;
            for (String repoName : repos) {
                // String repoName = "ecf";
                // System.out.println("Repo:" + repoName);
                new pCalculator(repoName, resKey, TOPK)
                        .calculateBIRPerformance();
            }
            
            //System.out.println();
            //System.out.println(sumAcc / repos.length + "\t" + sumMRR
                    /// repos.length + "\t" + sumMAP / repos.length + "\t"
                   // + sumMR / repos.length); 
            
            
            pCalculator.sumAcc=0;
            pCalculator.sumMRR=0;
            pCalculator.sumMAP=0;
            pCalculator.sumMR=0;
            
        }
        System.out.println("Total bugs:" + totalBug);
    }
}
