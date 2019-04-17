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

	
	
	public PerformanceCalculatorPerfect(String base, String corpus)
	{
		this.base=base;
		this.corpus=corpus;
		this.resultsMap=this.getContent(this.base+"\\Result\\"+this.corpus+"_result.txt");
		this.goldResultsMap=this.getContent(this.base+"\\GoldsetMe\\"+this.corpus+".txt");
		
	}
	
	



	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String base="E:\\PhD\\TextRankBased\\";
		String corpus="ecf";
		
		PerformanceCalculatorPerfect obj=new PerformanceCalculatorPerfect(base, corpus);
		HashMap<String, ArrayList<String>> resultTop10=ComputePerformancePercent(10, obj);
		//MiscUtility.showResult(obj.resultsMap.size()-1, obj.resultsMap);
        MiscUtility.showResult(resultTop10.size(), resultTop10);
        int count=0;
        for(String key:resultTop10.keySet())System.out.println(++count+" "+key+" "+resultTop10.get(key));
        double percentageT10=Double.valueOf(resultTop10.size())/Double.valueOf(obj.resultsMap.size())*100;
        System.out.println(percentageT10);
        System.out.println( ComputeMAP(resultTop10,obj));
        System.out.println(ComputeMRR(resultTop10, obj, 10));
	}

  
	
	
	

	
	public static void getSingleResult()
	{
   	
			
		

		
        
       
		//MiscUtility.showResult(10, finalRankedResult);
	}
	
	public static HashMap<String, Double> getResultForTopK(PerformanceCalculatorPerfect obj)
	{
		
		boolean emptybug=false;
		HashMap<String, Double> resultHM=new HashMap<>();
		int TOP_K=1;
		int count=0;
		//System.out.println("Result for Top-"+TOP_K);
		HashMap<String, ArrayList<String>> resultTop1=ComputePerformancePercent(TOP_K, obj);
		if(resultTop1.size()>0){
            for(String key:resultTop1.keySet()) {
                //System.out.println(++count+" "+key+" "+resultTop1.get(key));
                resultHM.put("bugid", Double.valueOf(key));
            }
        }
        else {
            Set<String> hashset=obj.resultsMap.keySet();
            String id=hashset.toString();
            id=id.substring(1,id.length()-1);
            //System.out.println(id);
            if(id.isEmpty()==false)
            {
                resultHM.put("bugid", Double.valueOf(id));
               
            }
            else 
            {
                resultHM.put("bugid", 1.00);
                emptybug=true;
            }
        }
		//MiscUtility.showResult(resultTop1.size(), resultTop1);
		
		for(String key:resultTop1.keySet())
		    {
		       // System.out.println(++count+" "+key+" "+resultTop1.get(key));
		        //resultHM.put("bugid", Double.valueOf(key));
		    }
		
		double percentageT1=Double.valueOf(resultTop1.size())/Double.valueOf(obj.resultsMap.size())*100;
		if(emptybug==false)resultHM.put("T1", percentageT1); else resultHM.put("T1", 0.0);
		if(emptybug==false)resultHM.put("MAP@1", ComputeMAP(resultTop1,obj));else resultHM.put("MAP@1", 0.0);
		if(emptybug==false)resultHM.put("MRR@1", ComputeMRR(resultTop1,obj, TOP_K));else resultHM.put("MRR@1", 0.0);
		//System.out.println("MRR at "+TOP_K+" "+ComputeMRR(resultTop1,obj, TOP_K));
		//System.out.println("MAP at "+TOP_K+" "+ComputeMAP(resultTop1,obj));
		
		//finalRankedResult.clear();
		//System.out.println("=============================================================================");
		TOP_K=5;
		//System.out.println("Result for Top-"+TOP_K);
		HashMap<String, ArrayList<String>> resultTop5=ComputePerformancePercent(TOP_K, obj);
		count=0;
		//for(String key:resultTop5.keySet())System.out.println(++count+" "+key+" "+resultTop5.get(key));
		double percentageT5=Double.valueOf(resultTop5.size())/Double.valueOf(obj.resultsMap.size())*100;
		if(emptybug==false)resultHM.put("T5", percentageT5);else resultHM.put("T5", 0.0); 
		if(emptybug==false)resultHM.put("MAP@5", ComputeMAP(resultTop5,obj)); else resultHM.put("MAP@5", 0.0);
		if(emptybug==false)resultHM.put("MRR@5", ComputeMRR(resultTop5,obj, TOP_K)); else resultHM.put("MRR@5", 0.0);
		
		
		//System.out.println("=============================================================================");
		TOP_K=10;
		//System.out.println("Result for Top-"+TOP_K);
		HashMap<String, ArrayList<String>> resultTop10=ComputePerformancePercent(TOP_K, obj);
		
		double percentageT10=Double.valueOf(resultTop10.size())/Double.valueOf(obj.resultsMap.size())*100;
		if(emptybug==false)resultHM.put("T10", percentageT10); else resultHM.put("T10", 0.0);
		//resultHM.put("T10", ComputePerformancePercent(TOP_K, obj));
		//MiscUtility.showResult(resultTop10.size(), resultTop10);
		//System.out.println("=================="+finalRankedResult.size());
		//System.out.println("MRR at "+TOP_K+" "+ComputeMRR(resultTop10,obj, TOP_K));
		//System.out.println("MAP at "+TOP_K+" "+ComputeMAP(resultTop10,obj));
		
		if(emptybug==false)resultHM.put("MAP@10", ComputeMAP(resultTop10,obj));else resultHM.put("MAP@10", 0.0);
		if(emptybug==false)resultHM.put("MRR@10", ComputeMRR(resultTop10,obj, TOP_K)); else resultHM.put("MRR@10", 0.0);
		//MiscUtility.showResult(10, resultHM);
		//FindBestRank(1000, obj);
		return resultHM;
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
					System.out.println(file);
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
	            System.out.println(bugID);
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
				System.out.println("Changeset reloaded successfully for :"
						+ hm.size());
				return hm;
	
	}


	
}
