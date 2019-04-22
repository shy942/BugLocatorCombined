package proposed;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import Graph.TokenRankProvider;
import Graph.WordNetworkMaker;

import proposed.QueryToken;
import score.provider.rVSMScoreProviderForTF;
import score.provider.rVSMscoreProvider;
import utility.ContentLoader;
import utility.MiscUtility;

public class TextRankBased {
    
       String corpusname;
       String corpusFolderPath;
       HashMap<String, HashMap<String, QueryToken>> queryTextRankScore;
       HashMap<String, HashMap<String, QueryToken>> corpusTextRankScore;
       HashMap<String, ArrayList<String>> queryall;
       String queryFolderPath;
       public TextRankBased(String corpusname, String corpusFolderPath, String queryFolderPath)
       {
           this.corpusname=corpusname;
           this.corpusFolderPath=corpusFolderPath;
           this.queryFolderPath=queryFolderPath;
           this.queryTextRankScore= new HashMap<>();
           this.corpusTextRankScore=new HashMap<>();
           this.queryall=new HashMap<>();
       }

       public static void main(String[] args) {
        // TODO Auto-generated method stub
     
        String base="E:\\PhD\\TextRankBased\\";
        String corpusname="ecf";
        String cospusFolderPath="E:\\PhD\\TextRankBased\\processsedFolderBase\\";
        String queryFolderPath="E:\\PhD\\TextRankBased\\BR-Query\\"+corpusname+"-query.txt";
        TextRankBased obj=new TextRankBased(corpusname, cospusFolderPath, queryFolderPath);
        String type="COM";
        obj.queryOnlyTRbased(base, corpusname, cospusFolderPath, queryFolderPath, type);
        //(base, corpusname, cospusFolderPath, queryFolderPath, type);
        //(base, corpusname, cospusFolderPath, queryFolderPath, type);
        //(base, corpusname, cospusFolderPath, queryFolderPath, type);
        
        /*TextRankBased obj=new TextRankBased(corpusname, cospusFolderPath);
        obj.getTextRankForAll();
        obj.testWithQuery(queryFolderPath);
        MiscUtility.showFullMap(obj.queryTextRankScore);
       
        new rVSMscoreProvider(base, corpusname, obj.queryTextRankScore, obj.corpusTextRankScore).rVSMcalculatorManager();*/
     }
       public void bothTRbasedtesing(String base, String corpusname, String cospusFolderPath, String queryFolderPath, String type)
       {
              TextRankBased obj=new TextRankBased(corpusname, cospusFolderPath, queryFolderPath);
              //Calculate Corpus TF score
              obj.getTextRankForAll();
              obj.testWithQuery(queryFolderPath);
              HashMap<String, HashMap<String, Double>> queryTFscore=obj.convertTRtoTFforQuery();
            
              HashMap<String, HashMap<String, Double>> corpusTFscore=obj.convertTRtoTFforCorpus();
              new rVSMScoreProviderForTF(base, corpusname, queryTFscore, corpusTFscore).rVSMcalculatorManager(type);
       }
    public void queryOnlyTRbased(String base, String corpusname, String cospusFolderPath, String queryFolderPath, String type)
    {
           TextRankBased obj=new TextRankBased(corpusname, cospusFolderPath, queryFolderPath);
           //Calculate Corpus TF score
           TFiDFBased tfObj=new TFiDFBased(corpusname, cospusFolderPath, queryFolderPath);
           tfObj.getTFscoreAllcorpus();
           obj.testWithQuery(queryFolderPath);
           HashMap<String, HashMap<String, Double>> queryTFscore=obj.convertTRtoTFforQuery();
           MiscUtility.showResult(10, queryTFscore);
          
           new rVSMScoreProviderForTF(base, corpusname, queryTFscore, tfObj.corpusTFscore).rVSMcalculatorManager(type);
    }
       
    public HashMap<String, HashMap<String, Double>> convertTRtoTFforQuery()
    {
        HashMap<String, HashMap<String, Double>> queryTFscore= new HashMap<>();
        HashMap<String, ArrayList<String>> top10TRbased=new HashMap<>();
        for(String qid: this.queryTextRankScore.keySet())
        {
            HashMap<String, QueryToken> queryInfo=this.queryTextRankScore.get(qid);
            HashMap<String, Double> queryTFinfo=new HashMap<>();
            for(String qword: queryInfo.keySet()){
                QueryToken tokendb=queryInfo.get(qword);
                
                double qTF=tokendb.tokenRankScore;
                queryTFinfo.put(qword, qTF);
            }
            HashMap<String, Double> sortedQueryTFinfo=MiscUtility.sortByValues(queryTFinfo);
            int count=0;
            ArrayList<String> list=new ArrayList<>();
            for(String sqword:sortedQueryTFinfo.keySet())
            {
                count++;
                if(count>10)break;
                list.add(sqword);
                
            }
            //System.out.println(tokenRankMap);
            //showMap(tokenRankMap);
            //queryTFscore.put(qid, queryTFinfo);
            top10TRbased.put(qid, list);
        }
        System.out.println("I'm from convertTRtoTFforQuery");
        MiscUtility.showResult(10, top10TRbased);
        HashMap<String, HashMap<String, Double>> queryTFscoreTop10=findqueryTFScore(top10TRbased);
        return queryTFscoreTop10;
    }
    
   public HashMap<String, HashMap<String, Double>> findqueryTFScore(HashMap<String, ArrayList<String>> top10TRbased)
   {
       HashMap<String, HashMap<String, Double>> queryTFscoreTop10= new HashMap<>();
       TFiDFBased tfObj=new TFiDFBased(this.corpusname, this.corpusFolderPath, this.queryFolderPath);
       tfObj.testWithQuery(this.queryFolderPath);
       for(String qid:tfObj.queryTFscore.keySet())
       {
           HashMap<String, Double> queryInfo=tfObj.queryTFscore.get(qid);
           ArrayList<String> list=top10TRbased.get(qid);
           int count=0;
           HashMap<String, Double> queryInfoUpdated=new HashMap<>();
           for(String qword: queryInfo.keySet())
           {
               if(list.contains(qword)){
                   double qtf=queryInfo.get(qword);
                   queryInfoUpdated.put(qword, qtf);
               }
           }
           queryTFscoreTop10.put(qid, queryInfoUpdated);
       }
       System.out.println("I'm from findqueryTFScore");
       MiscUtility.showResult(10, queryTFscoreTop10);
       return queryTFscoreTop10;
   }
    
    public HashMap<String, HashMap<String, Double>> convertTRtoTFforCorpus()
    {
        HashMap<String, HashMap<String, Double>> corpusTFscore= new HashMap<>();
        for(String cid: this.corpusTextRankScore.keySet())
        {
            HashMap<String, QueryToken> corpusInfo=this.corpusTextRankScore.get(cid);
            HashMap<String, Double> corpusTFinfo=new HashMap<>();
            for(String cword: corpusInfo.keySet()){
                QueryToken tokendb=corpusInfo.get(cword);
                
                double dTF=tokendb.tokenRankScore;
                corpusTFinfo.put(cword, dTF);
            }
            //System.out.println(tokenRankMap);
            //showMap(tokenRankMap);
            corpusTFscore.put(cid, corpusTFinfo);
        }
        return corpusTFscore;
    }   
    
    
    public void bothTRbased(String base, String corpusname, String cospusFolderPath, String queryFolderPath, String type)
    {
        TextRankBased obj=new TextRankBased(corpusname, cospusFolderPath, queryFolderPath);
        obj.getTextRankForAll();
        obj.testWithQuery(queryFolderPath);
        MiscUtility.showFullMap(obj.queryTextRankScore);
        MiscUtility.showFullMap(obj.corpusTextRankScore);
        new rVSMscoreProvider(base, corpusname, obj.queryTextRankScore, obj.corpusTextRankScore).rVSMcalculatorManager(type);
    }
    
    public void testWithQuery(String queryFolderPath)
    {
        //Load queries in a hashMap
        HashMap<String, ArrayList<String>> queryall=new HashMap<>();
        ArrayList<String> queryContent=ContentLoader.readContent(queryFolderPath);
        int count=0;
        String bugId="";
        int noOfLine=0;
        ArrayList<String> lineList = new ArrayList<>();
        for(String line:queryContent)
        {
            String [] spilter;
            //For 1st line
            if(count==0||count>(noOfLine)) 
            {
                    if(count>(noOfLine)){
                        queryall.put(bugId, lineList);
                        count=0;
                    }
                    spilter=line.split(":");
                    bugId=spilter[0];
                    noOfLine=Integer.valueOf(spilter[1]);
                    lineList = new ArrayList<>();
                    count++;
            }
            else
            {
                lineList.add(line);
                count++;
            }
            
            
        }
        System.out.println(queryall);
        
        //Get text rank score for each query
        getTextRankScoreQueryAll(queryall);
    }
       
    public void getTextRankScoreQueryAll(HashMap<String, ArrayList<String>> queryall)
    {
        for(String query: queryall.keySet())
        {
            ArrayList<String> querycontent=queryall.get(query);
            HashMap<String, QueryToken> tokenRankMap = new HashMap<>();
            WordNetworkMaker networkMaker = new WordNetworkMaker(querycontent);
            tokenRankMap = getQueryTokenRankScores(networkMaker, true);
            //System.out.println(tokenRankMap);
            //showMap(tokenRankMap);
            this.queryTextRankScore.put(query, tokenRankMap);
        }
        //MiscUtility.showFullMap(this.queryTextRankScore);
    }
    public void getTextRankForAll()
    {
        String path=this.corpusFolderPath+this.corpusname+"\\";
        File[] files = new File(path).listFiles();
        for (File file : files) {
             //System.out.println("File: " + file.getAbsolutePath());
             ArrayList<String> content=ContentLoader.getAllLinesList(file.getAbsolutePath());
             //System.out.println(content);
             HashMap<String, QueryToken> tokenRankMap = new HashMap<>();
             WordNetworkMaker networkMaker = new WordNetworkMaker(content);
             tokenRankMap = getQueryTokenRankScores(networkMaker, true);
             //System.out.println(tokenRankMap);
             //showMap(tokenRankMap);
             this.corpusTextRankScore.put(file.getName(), tokenRankMap);
        }
    }


    protected static HashMap<String, QueryToken> getQueryTokenRankScores(
            WordNetworkMaker networkMaker, boolean weighted) {
        // collect query token scores
        HashMap<String, QueryToken> tokendb = new HashMap<>();
        if (weighted) {
            SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> wgraph = networkMaker
                    .createWeightedWordNetwork();
            tokendb = networkMaker.getTokenDictionary(true);
            TokenRankProvider rankProvider = new TokenRankProvider(wgraph,
                    tokendb, false);
            tokendb = rankProvider.calculateTokenRankWeighted();
        } else {
            DirectedGraph<String, DefaultEdge> graph = networkMaker
                    .createWordNetwork();
            tokendb = networkMaker.getTokenDictionary(false);
            TokenRankProvider rankProvider = null;
            //if (customInitialize) {
               // HashMap<String, Double> initializerMap = getTFIDFScores(new HashSet<String>(
                  //      tokendb.keySet()));
                //rankProvider = new TokenRankProvider(graph, tokendb, false,
                    //    initializerMap);
           // } else {
                rankProvider = new TokenRankProvider(graph, tokendb, false);
            //}
            tokendb = rankProvider.calculateTokenRank();
        }
        // token DB containing scores now.
        return tokendb;
    }
}
