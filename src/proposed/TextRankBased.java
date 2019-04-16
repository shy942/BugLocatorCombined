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
import score.provider.rVSMscoreProvider;
import utility.ContentLoader;
import utility.MiscUtility;

public class TextRankBased {
    
       String corpusname;
       String corpusFolderPath;
       HashMap<String, HashMap<String, QueryToken>> queryTextRankScore;
       HashMap<String, HashMap<String, QueryToken>> corpusTextRankScore;
       public TextRankBased(String corpusname, String corpusFolderPath)
       {
           this.corpusname=corpusname;
           this.corpusFolderPath=corpusFolderPath;
           this.queryTextRankScore= new HashMap<>();
           this.corpusTextRankScore=new HashMap<>();
       }

       public static void main(String[] args) {
        // TODO Auto-generated method stub
     
        String base="E:\\PhD\\TextRankBased\\";
        String corpusname="eclipse.jdt.core";
        String cospusFolderPath="E:\\PhD\\TextRankBased\\processsedFolderBase\\";
        String queryFolderPath="E:\\PhD\\TextRankBased\\BR-Query\\"+corpusname+"-query.txt";
        TextRankBased obj=new TextRankBased(corpusname, cospusFolderPath);
        obj.getTextRankForAll();
        obj.testWithQuery(queryFolderPath);
        MiscUtility.showFullMap(obj.queryTextRankScore);
        MiscUtility.showFullMap(obj.corpusTextRankScore);
        new rVSMscoreProvider(base, corpusname, obj.queryTextRankScore, obj.corpusTextRankScore).rVSMcalculatorManager();
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
