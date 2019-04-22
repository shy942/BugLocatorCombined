package corpus.creator;

import java.io.File;
import java.util.ArrayList;


import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;
import config.StaticData;

public class BugReportCorpusBuilder {

	int year;
	String bugFolder;
	String bugPPFolder;
	int noOfBugReports;
	ArrayList <String> frequentKeywordList=new ArrayList<String>();

	public BugReportCorpusBuilder(String base, String corpus)
	{
	    //For testing
	    //this.bugFolder=base+"Query//"+corpus+"//";
	    //this.bugPPFolder=base+"Query//"+corpus+"-query.txt";
		this.bugFolder=base+"BR-Raw//"+corpus+"//";
		this.bugPPFolder=base+"BR-Query//"+corpus+"-query.txt";
		this.noOfBugReports=noOfBugReports;
	}
	protected void createPreprocessedRepo()
	{
		File[] files=new File(bugFolder).listFiles();
		//String allInOne="";
		ArrayList <String> list=new ArrayList<String>();
		noOfBugReports=files.length;
		ArrayList<String> processedQuery=new ArrayList<>();
		for(File f:files){
			if(!f.getName().equalsIgnoreCase(".DS_Store"))
			{
			String fileName=f.getName();
			ArrayList<String> content=ContentLoader.readContent(f.getAbsolutePath());
			
			BugReportPreprocessor bpp=new BugReportPreprocessor(content);
			String preprocessed=bpp.performNLPforAllContent();
			String bugID=fileName.substring(0, fileName.length()-4);
			int noOfLine=bpp.getNoOfLine();
			preprocessed=bugID+":"+noOfLine+"\n"+preprocessed.trim();
			processedQuery.add(preprocessed);
			String outFile=this.bugPPFolder+"/"+fileName;
			
			
			System.out.println("Preprocessed:"+fileName);
			
			}
		}
		ContentWriter.writeContent(this.bugPPFolder, processedQuery);
		
		
		
	    
		
	}
	

	protected int getNoOFSourceCodes()
	{
		return noOfBugReports;
	}
	
	

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//IndividualYearProcessing();
		//PutAll2gether();
	    String base="E:\\PhD\\TextRankBased\\";
	    String corpus="ecf";
		new BugReportCorpusBuilder(base, corpus).createPreprocessedRepo();
	}

}
