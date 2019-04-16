package score.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import proposed.QueryToken;
import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;

public class rVSMscoreProvider {

    
    HashMap<String, HashMap<String, QueryToken>> queryTextRankScore;
    HashMap<String, HashMap<String, QueryToken>> corpusTextRankScore;
    HashMap<String, HashMap<String, Double>> TermDocumentMap;
    HashMap<String, Double> LengthInfoMap;
    HashMap<String, String> corpusMap;
    int totalDocumnet;
    double minLength;
    double maxLength;
    String base;
    String corpus;
    public rVSMscoreProvider(String base, String corpus, HashMap<String, HashMap<String, QueryToken>> queryTextRankScore,  HashMap<String, HashMap<String, QueryToken>> corpusTextRankScore)
    {
        this.base=base;
        this.corpus=corpus;
        this.queryTextRankScore=queryTextRankScore;
        this.corpusTextRankScore=corpusTextRankScore;
        this.TermDocumentMap=new HashMap<>();
        this.totalDocumnet=this.corpusTextRankScore.size();
        this.LengthInfoMap=new HashMap<>();
        this.corpusMap=new HashMap<>();
        this.MinMaxLengthCalculator();
        this.LoadCorpusMap();
    }
    
    public void LoadCorpusMap()
    {
        ArrayList<String> content=ContentLoader.readContent(this.base+"\\CorpusMappingMe\\"+this.corpus+".txt");
        for(String line: content)
        {
            String [] spilter=line.split(":");
            this.corpusMap.put(spilter[0]+".txt",spilter[1]);
        }
    }
    
    public void MinMaxLengthCalculator()
    {
        ArrayList<String> loadedLength=ContentLoader.getAllKeywords(this.base+"\\FileInfo\\"+corpus+"-lengthList.txt");
        for(String line: loadedLength)
        {
            String [] spilter=line.split(":");
            this.LengthInfoMap.put(spilter[0]+".txt", Double.valueOf(spilter[1]));
        }
        this.minLength=getMinLenth(this.LengthInfoMap);
        this.maxLength=getMaxLength(this.LengthInfoMap);
    }
    public double getMaxLength(HashMap <String, Double> Map)
    {
        double maxLength=0;
        for(String key:Map.keySet())
        {
            double len=(Map.get(key));
            if(len>maxLength) maxLength=len;
        }
        return maxLength;
    }
    public double getMinLenth(HashMap <String, Double> Map)
    {
        double minLength=1000;
        
        for(String key:Map.keySet())
        {
            double len=(Map.get(key));
            if(len<minLength) minLength=len;
        }
        return minLength;
    }
    public void rVSMcalculatorManager()
    {
        MiscUtility.showFullMap(this.queryTextRankScore);
        MiscUtility.showFullMap(this.corpusTextRankScore);
        System.out.println("I am from rVSM calculator");
        TermDocumentCalculator();
        calculaterVSMforAllQuery();
    }
    
    
    public void TermDocumentCalculator()
    {
        for(String docId: this.corpusTextRankScore.keySet())
        {
            HashMap<String, Double> tempDocumentMap=new HashMap<>();
            HashMap<String, QueryToken> textRankMap=this.corpusTextRankScore.get(docId);
            for(String word: textRankMap.keySet())
            {
                if(this.TermDocumentMap.containsKey(word))
                {
                   tempDocumentMap=this.TermDocumentMap.get(word);
                   if(tempDocumentMap.containsKey(docId))
                   {
                       double nt=tempDocumentMap.get(docId);
                       nt =nt + 1.0;
                       tempDocumentMap.put(docId, nt);
                   }
                   else
                   {
                       //tempDocumentMap=new HashMap<>();
                       tempDocumentMap.put(docId, 1.0);
                   }
                   this.TermDocumentMap.put(word, tempDocumentMap);
                }
                else
                {
                    tempDocumentMap=new HashMap<>();
                    tempDocumentMap.put(docId, 1.0);
                    this.TermDocumentMap.put(word, tempDocumentMap);
                }
            }
        }
        //System.out.println(this.TermDocumentMap);
    }
    
    protected void calculaterVSMforAllQuery()
    {
        //Calculate Nx
       // double Nx=(Double.valueOf(this.totalDocumnet)-Double.valueOf(this.minLength))/(Double.valueOf(this.maxLength)-Double.valueOf(this.minLength));
        //Calculate gTerms
        //double gTerms=1/(1+Math.exp(-Nx));
        ArrayList<String> resultAll=new ArrayList<>();
        for(String qid: this.queryTextRankScore.keySet())
        {
            System.out.println(qid+"----------------------------------------------------------------------------------------------"+this.maxLength+" "+this.minLength);
            HashMap<String, QueryToken> queryInfo=this.queryTextRankScore.get(qid);
            HashMap <String, Double> finalResult=new HashMap<>();
            for(String docID:this.corpusTextRankScore.keySet())
            {
                HashMap<String, QueryToken> docInfo=this.corpusTextRankScore.get(docID);
                double upperscore=this.calculateUpperPart(queryInfo, docInfo, docID);
                double lowerscore=this.calculateLowerPart(queryInfo, docInfo, docID);
                double score=0.0;
                
                //Calculate gTerms
                //Calculate Nx
                double Nx=0.0;
                if(this.LengthInfoMap.containsKey(docID)){
                    Nx=(this.LengthInfoMap.get(docID)-this.minLength)/(this.maxLength-this.minLength);
                }
                
                //Calculate gTerms
                double gTerms=1/(1+Math.exp(-Nx));
                if(upperscore!=0&&lowerscore!=0) score=gTerms*(upperscore)/lowerscore;
                if(score>0) {
                    //System.out.println(docID+" = "+score);
                    finalResult.put(docID, score);
                }
                
            }
            HashMap<String, Double> normalizedAndSortedResult=doNormalization(finalResult);
            System.out.println(normalizedAndSortedResult);
            int count=0;
            for(String docID:normalizedAndSortedResult.keySet())
            {
                if(count>10) break;
                resultAll.add(qid+","+docID+","+normalizedAndSortedResult.get(docID));
                count++;
            }
        }
        ContentWriter.writeContent(this.base+"\\Result\\"+this.corpus+"_result.txt", resultAll);
    }
    
    
    public HashMap<String, Double> doNormalization(HashMap <String, Double> finalResultEachQuery)
    {
        HashMap<String, Double> normalizedResult=new HashMap<>();
        double minlength=getMinLenth(finalResultEachQuery);
        double maxLength=getMaxLength(finalResultEachQuery);
        for (String key:finalResultEachQuery.keySet())
        {
            double score=finalResultEachQuery.get(key);
            double normalizedScore=(score-minlength)/(maxLength-minlength);
            String fullPath=this.corpusMap.get(key);
            normalizedResult.put(fullPath, normalizedScore);
            //normalizedResult.put(key, normalizedScore);
        }
        HashMap<String, Double> sortedResult=MiscUtility.sortByValues(normalizedResult);
        return sortedResult;
    }
    
    
    
    protected double calculateLowerPart(HashMap<String, QueryToken> queryInfo, HashMap<String, QueryToken> docInfo, String docID)
    {
        double scoreLowerPart=0.0;
        //For Query q
        double qsqrt=0;
        double qsum=0;
       
        for(String qword: queryInfo.keySet()){
            QueryToken tokendb=queryInfo.get(qword);
            
            double qTF=tokendb.tokenRankScore;
            double first=Math.log(qTF)+1;
           
            double dTF=0.0;
            if(this.TermDocumentMap.containsKey(qword))
            {
                HashMap<String, Double> temp= this.TermDocumentMap.get(qword);
                if(temp.containsKey(docID))dTF=temp.get(docID);
            }
            double second=1;
            if(dTF>0.0) second+=Math.log(dTF);
            double score=first*second;
            double squaredScore=Math.pow(score, 2);
            qsum+=squaredScore;
        }
        qsqrt=Math.sqrt(qsum);
        
        //For document d
        double dsqrt=0;
        double dsum=0;
        
        for(String dword: docInfo.keySet())
        {
            QueryToken tokendbdoc=docInfo.get(dword);
            double dTF=tokendbdoc.tokenRankScore;
            double first=Math.log(dTF)+1;
            double nt=0.0;
            if(this.TermDocumentMap.containsKey(dword))
            {
                HashMap<String, Double> temp= this.TermDocumentMap.get(dword);
                if(temp.containsKey(docID))nt=temp.get(docID);
            }
            double second=1;
            if(nt>0.0) second+=Math.log(nt);
            double score=first*second;
            double squaredScore=Math.pow(score, 2);
            dsum+=squaredScore;
        }
        dsqrt=Math.sqrt(dsum);
        
        scoreLowerPart=qsqrt*dsqrt;
        return scoreLowerPart;
        
    }
    protected double calculateUpperPart(HashMap<String, QueryToken> queryInfo, HashMap<String, QueryToken> docInfo, String docID)
        {
            double scoreUpperPart=0.0;
            double sum=0;
            for(String qword: queryInfo.keySet()){
                QueryToken tokendb=queryInfo.get(qword);
                
                double qTF=tokendb.tokenRankScore;
                double first=Math.log(qTF)+1;
                double dTF=0.0;
                if(docInfo.containsKey(qword)) 
                {
                    QueryToken tokendbCorpus=docInfo.get(qword);
                    dTF=tokendbCorpus.tokenRankScore;
                }
                double second=1;
                if(dTF>0.0) second+=Math.log(dTF);
                double third=0;
                
                //if(docInfo.containsKey(qword))third=2*Math.log(this.totalDocumnet/ddfMap.get(t)); 
                if(this.TermDocumentMap.containsKey(qword))
                {
                    HashMap<String, Double> temp= this.TermDocumentMap.get(qword);
                    if(temp.containsKey(docID))third=2*Math.log(this.totalDocumnet/temp.get(docID));
                }
                double score=first*second*third;
                sum+=score;
            }
            scoreUpperPart=sum;
            return scoreUpperPart;
        }
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
