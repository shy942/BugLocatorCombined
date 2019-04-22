package corpus.creator;

import java.io.File;
import java.util.ArrayList;

import source.visitor.CommentFilterer;
import utility.ContentLoader;
import utility.ContentWriter;
import config.StaticData;;

public class SourceCodeCorpusBuilder {

    String sourceCodeFolder;
	String sourceCodePPFolder;
	ArrayList<String> javaFilePaths;
	ArrayList<String> javaFilePathsLastName;
	int noOfFile=0;
	String base;
	String corpus;
	public SourceCodeCorpusBuilder(String corpus, String base)
	{
	    this.corpus=corpus;
		this.base=base;
		this.sourceCodeFolder=base+"\\Corpus\\"+corpus;
		this.sourceCodePPFolder=base+"\\processsedFolderBase\\"+corpus+"\\";
		this.javaFilePaths=new ArrayList<String>();
		this.javaFilePathsLastName=new ArrayList<String>();
		this.noOfFile=0;
		this.loadJavaFilesOnly(new File(this.sourceCodeFolder));
	}
	
	protected void createPreprocessedRepo()
	{
		int file_track=0;
		ArrayList<String> listofFiles=new ArrayList<>();
		ArrayList<String> lengthList=new ArrayList<>();
		int i=0;
		for (String s : javaFilePaths)
	    {
		    i++;
		    //if(i>3) break;
	        String fileName=javaFilePathsLastName.get(file_track++);
	    	//Remove initial copyright comment
			CommentFilterer cf=new CommentFilterer(s,fileName);
			cf.discardClassHeaderComment();
			
			String methodFolder=this.base+"\\ExtractedMethod\\"+this.corpus;
			MethodCorpusDeveloper developer=new MethodCorpusDeveloper(this.sourceCodeFolder, methodFolder,this.base);
			//developer.createMethodCorpus(developer.repoFolder);
			developer.extractMethods(s);
			developer.saveMethods(s);
			
			String packageName=developer.getPackageName();
			ArrayList<String> fileList=developer.returnFiles();
			//String content=ContentLoader.readContentSimple("./data/processed/"+fileName);
			String preprocessed="";
			int length=0;
			for(String file:fileList)
			{
				String content=ContentLoader.readContentSimple(file);
			    String[] spilter=content.split(" ");
			    length+=spilter.length;
				SourceCodePreprocessor scbpp=new SourceCodePreprocessor(content);
				
				preprocessed=preprocessed+scbpp.performNLP()+"\n";
				
			}
			
			String [] spilter=s.split("\\\\");
			String filePart="";
			
				
			String fileNameWithjava=spilter[spilter.length-1];
			String fineNameTosave=fileNameWithjava.substring(0, fileNameWithjava.length()-5);
			filePart=packageName+"."+spilter[spilter.length-1];
			if(!listofFiles.contains(filePart))listofFiles.add(filePart);
			
			System.out.println(filePart);
			//System.out.println(file_track+" Preprocessed:"+this.sourceCodePPFolder+filePart);
			if(preprocessed.length()>0){
			ContentWriter.writeContent(this.sourceCodePPFolder+fineNameTosave+".txt", preprocessed);
			lengthList.add(fineNameTosave+":"+length);
			}
		}
		System.out.println("Total no. of files: "+file_track);
		ContentWriter.writeContent(this.base+"\\FileInfo\\"+corpus+"-SourceFileNames.txt", listofFiles);
		ContentWriter.writeContent(this.base+"\\FileInfo\\"+corpus+"-lengthList.txt", lengthList);
	}
	
	public void loadJavaFilesOnly(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				loadJavaFilesOnly(fileEntry);
			} else {
				// System.out.println(fileEntry.getAbsolutePath());
				if (fileEntry.getName().endsWith(".java")) {
					this.javaFilePaths.add(fileEntry.getAbsolutePath());
					this.javaFilePathsLastName.add(noOfFile++,
							fileEntry.getName());
				}
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String corpus="ecf";
		String base="E:\\PhD\\TextRankBased\\";
		//String processsedFolderBase="E:\\PhD\\TextRankBased\\ProcessedSC\\";
		new SourceCodeCorpusBuilder(corpus,base).createPreprocessedRepo();
		//This is a simple change.
	}

}
