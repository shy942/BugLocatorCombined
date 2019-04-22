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
import utility.ContentWriter;
import utility.MiscUtility;

public class TFiDFBased {
    
       String corpusname;
       String corpusFolderPath;
       HashMap<String, HashMap<String, Double>> queryTFscore;
       HashMap<String, HashMap<String, Double>> corpusTFscore;
       String queryFolderPath;
       public TFiDFBased(String corpusname, String corpusFolderPath, String queryFolderPath)
       {
           this.corpusname=corpusname;
           this.corpusFolderPath=corpusFolderPath;
           this.queryTFscore= new HashMap<>();
           this.corpusTFscore=new HashMap<>();
           this.queryFolderPath=queryFolderPath;
       }

       public static void main(String[] args) {
        // TODO Auto-generated method stub
     
        String base="E:\\PhD\\TextRankBased\\";
        String corpusname="ecf";
        String cospusFolderPath="E:\\PhD\\TextRankBased\\processsedFolderBase\\";
        String queryFolderPath="E:\\PhD\\TextRankBased\\BR-Query\\"+corpusname+"-query.txt";
        TFiDFBased obj=new TFiDFBased(corpusname, cospusFolderPath, queryFolderPath);
        obj.getTFscoreAllcorpus();
        obj.testWithQuery(queryFolderPath);
        MiscUtility.showResult(10,obj.queryTFscore);
        MiscUtility.showResult(10,obj.corpusTFscore);
        //MiscUtility.showResult( obj.corpusTFscore.size()-1, obj.corpusTFscore);
        //ContentWriter.writeContent(base+"//Test//"+"tfscoreCorpus.txt", obj.corpusTFscore);
        new rVSMScoreProviderForTF(base, corpusname, obj.queryTFscore, obj.corpusTFscore).rVSMcalculatorManager("");
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
            System.out.println(line);
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
        getTFscoreQueryAll(queryall);
    }
       
    public void getTFscoreQueryAll(HashMap<String, ArrayList<String>> queryall)
    {
        for(String queryId: queryall.keySet())
        {
            ArrayList<String> querycontent=queryall.get(queryId);
            HashMap<String, Double> queryTFmap = new HashMap<>();
           
            queryTFmap = getTFscores(querycontent);
         
            this.queryTFscore.put(queryId, queryTFmap);
        }
        //MiscUtility.showFullMap(this.queryTextRankScore);
    }
    public void getTFscoreAllcorpus()
    {
        String path=this.corpusFolderPath+this.corpusname+"\\";
        File[] files = new File(path).listFiles();
        for (File file : files) {
             //System.out.println("File: " + file.getAbsolutePath());
             ArrayList<String> content=ContentLoader.getAllLinesList(file.getAbsolutePath());
             HashMap<String, Double> corpusTFmap = new HashMap<>();
             
             corpusTFmap = getTFscores(content);
             this.corpusTFscore.put(file.getName(), corpusTFmap);
        }
    }


    protected static HashMap<String, Double> getTFscores(ArrayList<String> content) {
        // collect query token scores
        HashMap<String, Double> tfMap = new HashMap<>();
        for(String line:content)
        {
            String[] spilter=line.split(" ");
            for(String keyword:spilter)
            {
            
                if(tfMap.containsKey(keyword))
                {
                    double tf=tfMap.get(keyword);
                    tf=tf+1.0;
                    tfMap.put(keyword, tf);
                }
                else
                {
                
                tfMap.put(keyword, 1.0);
                }
            }
        }
        return tfMap;
    }
}
