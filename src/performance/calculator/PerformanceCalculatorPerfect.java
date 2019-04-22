package performance.calculator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import utility.ContentLoader;

import utility.ContentWriter;
import utility.MiscUtility;

public class PerformanceCalculatorPerfect {

	public HashMap<String, ArrayList<String>> goldResultsMap;
	public HashMap<String, ArrayList<String>> resultsMap;
	public String base;
	public String corpus;

	
	
	public PerformanceCalculatorPerfect(String base, String corpus, String type)
	{
		this.base=base;
		this.corpus=corpus;
		this.goldResultsMap=this.getContent(this.base+"\\GoldsetMe\\"+this.corpus+".txt");
		this.resultsMap=this.getResultContent(this.base+"\\Result\\"+this.corpus+"_result"+type+".txt");
		
		
	}
	
	



	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String base="E:\\PhD\\TextRankBased\\";
		String corpus="ecf";
		String type="";
		PerformanceCalculatorPerfect obj=new PerformanceCalculatorPerfect(base, corpus, type);
		System.out.println(obj.resultsMap);
		System.out.println(obj.goldResultsMap);
		//Top #1
		HashMap<String, ArrayList<String>> resultTop1=ComputePerformancePercent(1, obj);
		double percentageT1=Double.valueOf(resultTop1.size())/Double.valueOf(obj.goldResultsMap.size())*100;
		System.out.println(resultTop1.size()+"/"+obj.resultsMap.size());
        System.out.println("Top #1% = "+percentageT1);
        System.out.println("MAP = "+ComputeMAP(resultTop1,obj));
        System.out.println("MRR@10 = "+ComputeMRR(resultTop1, obj, 1));
		
        //Top #10
		HashMap<String, ArrayList<String>> resultTop10=ComputePerformancePercent(10, obj);
		//MiscUtility.showResult(obj.resultsMap.size()-1, obj.resultsMap);
        //for(String key:resultTop10.keySet())System.out.println(++count+" "+key+" "+resultTop10.get(key));
        double percentageT10=Double.valueOf(resultTop10.size())/Double.valueOf(obj.goldResultsMap.size())*100;
        System.out.println(resultTop10.size()+"/"+obj.resultsMap.size());
        System.out.println("Top #10% = "+percentageT10);
        System.out.println("MAP = "+ ComputeMAP(resultTop10,obj));
        System.out.println("MRR@10 = "+ComputeMRR(resultTop10, obj, 10));
	}

  
	
	
	public static double ComputeMAP(HashMap<String, ArrayList<String>> finalRankedResult, PerformanceCalculatorPerfect obj)
	{
		double averagePrecision=0.0;
		for(String queryID: finalRankedResult.keySet())
		{
			ArrayList<String> rankList=finalRankedResult.get(queryID);
			averagePrecision+=getAvgPrecisionEachQuery(rankList, queryID, obj);
			//System.out.println(rankList);
			//System.out.println(getAvgPrecisionEachQuery(rankList));
		}
		int totalQuery=obj.resultsMap.size();
		//System.out.println("averagePrecision: "+averagePrecision);
		double MAP=averagePrecision/Double.valueOf(totalQuery);
		//System.out.println("Total Query: "+totalQuery+" MAP: "+MAP);
		return MAP;
	}
	
	public static double getAvgPrecisionEachQuery(ArrayList<String> rankList, String queryID, PerformanceCalculatorPerfect obj)
	{
		double Precision=0.0;
		int count =0;
		for(String rankStr:rankList)
		{
			count++;
			int rank=Integer.valueOf(rankStr);
			Precision+=Double.valueOf(count)/Double.valueOf(rank);
		}
		int length=obj.goldResultsMap.get(queryID).size();
		//double AvgPrecision=Precision/Double.valueOf(count);
		double AvgPrecision=Precision/Double.valueOf(length);
		return AvgPrecision;
		
	}
	
	public static double ComputeMRR(HashMap<String, ArrayList<String>> finalRankedResult, PerformanceCalculatorPerfect obj, int TOP_K)
	{
		double averageRecall=0.0;
		for(String queryID: finalRankedResult.keySet())
		{
			ArrayList<String> rankList=finalRankedResult.get(queryID);
			averageRecall+=get1stRecall(rankList,TOP_K);
			//System.out.println(rankList);
			//System.out.println(get1stRecall(rankList,TOP_K));
		}
		int totalQuery=obj.resultsMap.size();
		int foundQuery=finalRankedResult.size();
		double MRR=averageRecall/Double.valueOf(totalQuery);
		//System.out.println("averageRecall: "+averageRecall);
		return MRR;
	}
	
	public static double get1stRecall(ArrayList<String> rankList, int TopK)
	{
		double recall1st=0.0;
		int count =0;
		int length=rankList.size();
		
		recall1st=1/Double.valueOf(rankList.get(0));
		
		return recall1st;
		
	}
	
	public static double getAvgRecallEachQuery(ArrayList<String> rankList, int TopK)
	{
		double Recall=0.0;
		int count =0;
		int length=rankList.size();
		String curRankStr=rankList.get(0);
		int rankCur=Integer.valueOf(curRankStr);
		//System.out.println(rankList);
	
		for(int r=1;r<rankList.size();r++)
		{ 
			String nextRankStr=rankList.get(r);
			count++;
			int rankNext=Integer.valueOf(nextRankStr);
			Recall+=getRecall(rankCur, rankNext, length, count);
			//System.out.println(rankCur+" "+rankNext+" "+length+" "+count);
			//System.out.println(getRecall(rankCur, rankNext, length, count));
			rankCur=rankNext;
		}
		Recall+=getRecall(rankCur, TopK+1, length, ++count);
		//System.out.println(getRecall(rankCur, TopK, length, count));
		double AvgPrecision=Recall/TopK;
		//System.out.println("Avg: "+AvgPrecision);
		return AvgPrecision;
		
	}
	
	public static double getRecall(int currentRank, int nextRank, int length, int count)
	{
		//System.out.println(currentRank+" "+nextRank+" "+length+" "+count);
		double recall=0.0;
		for(int i=1;i<=nextRank-currentRank;i++)
		{
			recall+=Double.valueOf(count)/Double.valueOf(length);
			//System.out.println(i+" Recall: "+Double.valueOf(count)/Double.valueOf(length));
		}
		return recall;
	}
	
	private static boolean IsMatched(String file, ArrayList <String> gitList, String bugID, int TOP_K)
	{
			int found=0;
			for(String GoldFile:gitList){
				if(GoldFile.equalsIgnoreCase(file.trim())){
					found=1;
					//System.out.println(file);
				}	
			}
			
		if(found==1) return true;
		else return false;
		
	}
	
	public static ArrayList<String> getRankedResult(ArrayList <String> resultList, ArrayList <String> gitList, String bugID, int TOP_K)
	{
		int count=0;
	    ArrayList<String> list=new ArrayList<>();
		for(String file:resultList){
			count++;
			if(count>TOP_K)break;
			if(IsMatched(file, gitList,bugID,TOP_K))
			{
				list.add(String.valueOf(count));
				//System.out.println(bugID+" "+file);
			}
			//count++;
		}
		return list;
	}
	
	private static HashMap<String, ArrayList<String>> ComputePerformancePercent(int TOP_K, PerformanceCalculatorPerfect obj) {
		// TODO Auto-generated method stub
		
		HashMap<String, ArrayList<String>> finalRankedResultlocal=new HashMap<>();
		int no_of_bug_matched=0;
		
		int total_found=0;
	
		for(String bugID:obj.resultsMap.keySet())
		{
		    
			ArrayList <String> resultList= obj.resultsMap.get(bugID); //Get the experimented results
	        if(obj.goldResultsMap.containsKey(bugID))// Truth set contains the bug
	        {
	            //System.out.println(bugID);
	        	ArrayList <String> gitList=obj.goldResultsMap.get(bugID);
	        	no_of_bug_matched++;
	        	ArrayList<String> list=getRankedResult(resultList,gitList, bugID, TOP_K);
	        
	        
	        	if(list.size()>0){
	        		
	        		total_found++;
	        		
	        		finalRankedResultlocal.put(bugID, list);
	        	}
	        }
	       
	       
	    }
	    
	   // System.out.println("Total found: "+finalRankedResultlocal);
	    //System.out.println("Total bug: "+obj.resultsMap.size());
	    //System.out.println("Top "+TOP_K+" %: "+(Double.valueOf(total_found)/Double.valueOf(no_of_bug_matched))*100);
	    //System.out.print((Double.valueOf(total_found)/Double.valueOf(no_of_bug_matched))*100+" ");
	    return finalRankedResultlocal;
	  
	}

	

	private HashMap<String, ArrayList<String>> getContent (String gitPath) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				HashMap<String, ArrayList<String>>hm=new HashMap<>();
				ArrayList<String> lines = ContentLoader
						.readContent(gitPath);
				for(String line: lines)
			    {
				    String [] spilter=line.split(",");
			        String bugID=spilter[0];
			        String address=spilter[1];
			        if(hm.containsKey(bugID))
			        {
			            ArrayList <String> tempList=hm.get(bugID);
			            tempList.add(address);
			            hm.put(bugID, tempList);
			        }
			        else
			        {
			            ArrayList <String> tempList=new ArrayList<>();
			            tempList.add(address);
			            hm.put(bugID, tempList);
			        }
			    }
				System.out.println("Goldset reloaded successfully for :"
						+ hm.size());
				return hm;
	
	}

	private HashMap<String, ArrayList<String>> getResultContent (String gitPath) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
                HashMap<String, ArrayList<String>>hm=new HashMap<>();
                ArrayList<String> lines = ContentLoader
                        .readContent(gitPath);
                for(String line: lines)
                {
                    String [] spilter=line.split(",");
                    String bugID=spilter[0];
                    if(this.goldResultsMap.containsKey(bugID)){
                    String address=spilter[1];
                    if(hm.containsKey(bugID))
                    {
                        ArrayList <String> tempList=hm.get(bugID);
                        tempList.add(address);
                        hm.put(bugID, tempList);
                    }
                    else
                    {
                        ArrayList <String> tempList=new ArrayList<>();
                        tempList.add(address);
                        hm.put(bugID, tempList);
                    }
                    }
                }
                System.out.println("Result sets reloaded successfully for :"
                        + hm.size());
                return hm;
                
    }
	
}
