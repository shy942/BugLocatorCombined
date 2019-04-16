package corpus.creator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utility.ContentWriter;
import utility.MiscUtility;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import config.StaticData;

public class MethodCorpusDeveloper {

	//String repoName;
	String repoFolder;
	String methodFolder;
	HashMap<String, String> methodmap;
	ArrayList<String> fileList=new ArrayList<>();
	String base;
	String packageName;
	ArrayList<String> listNoOfMethod;
	public static int count=0;
	public MethodCorpusDeveloper(String repoFolder, String methodFolder, String base) {
		
		this.repoFolder = repoFolder;
		this.methodFolder=methodFolder;
		
		this.methodmap = new HashMap<>();
		this.base=base;
		this.packageName="";
		this.listNoOfMethod=new ArrayList<>();
	}

	//public MethodCorpusDeveloper(String methodFolder){
		
	//}
	

	protected void extractMethods(String javaFileURL) {
		// extracting methods from the class
		try {
		    
			CompilationUnit cu = JavaParser.parse(new File(javaFileURL));
			
			
			if (cu != null) {
			    
			    //System.out.println(cu;
			    /*
			    for (TypeDeclaration typeDec : cu.getTypes()) {
			        List<BodyDeclaration> members = typeDec.getMembers();
			        if(members != null) {
			            for (BodyDeclaration member : members) {
			            //Check just members that are FieldDeclarations
			               
			            FieldDeclaration field = (FieldDeclaration) member;
			            
			            System.out.println(field.getType());
			         
			            //Print the field's name 
			            System.out.println(field.getVariables().get(0).getId().getName());
			            //Print the field's init value, if not null
			            Object initValue = field.getVariables().get(0).getInit();
			            if(initValue != null) {
			                 System.out.println(field.getVariables().get(0).getInit().toString());
			          
			            }
			        }
			    }*/
			    
			   
				PackageDeclaration packageDec=cu.getPackage();
				if(packageDec!=null){
				//System.out.println(packageDec.toString());
				String packageName=packageDec.toString();
				String[] spilter=packageName.split(" ");
				String fileName=spilter[1];
				//System.out.print(fileName);
				spilter=fileName.split(";");
				fileName=spilter[0];
				//System.out.print(fileName);
				this.packageName=fileName;
				MethodVisitor visitor = new MethodVisitor();
				cu.accept(visitor, null);
				ArrayList<String> methods = visitor.methods;
				// now add them to method map
				int index = 0;
				for (String method : methods) {
					index++;
					String key = javaFileURL + "#" + index;
					//System.out.println(key);
					this.methodmap.put(key, method);
				}
				
			}
				
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	protected void saveCorpusKeys() {
		// save the corpus keys
		String keysFile = this.base+".\\data\\"
				+ ".keys";
		ArrayList<String> keylist = new ArrayList<>();
		int index = 0;
		for (String key : this.methodmap.keySet()) {
			index++;
			keylist.add(index + ":" + key);
		}
		// now save the keys
		ContentWriter.writeContent(keysFile, keylist);
		//System.out.println("Keys saved successfully!");
	}

	protected void saveMethods(String javaFileURL) {
		// save the corpus keys
		saveCorpusKeys();
		// now save the extracted methods
		int index = 0;
		int no_of_method=0;
		String [] spilter=javaFileURL.split("\\\\");
        String filePart="";
    
        
        String lastPart=spilter[spilter.length-1];
		for (String key : this.methodmap.keySet()) {
			index++;
			String methodContent = this.methodmap.get(key);
			System.out.println(javaFileURL);
			//javaFileURL.replaceAll("\\\\", "/");
			
			
			//filePart=this.packageName+"."+index+"."+lastPart;
			filePart=lastPart.substring(0,lastPart.length()-5)+"."+index+".txt";
			String outFile = this.methodFolder + "\\"+filePart;
			//System.out.println("filePart:        "+filePart);
			SourceCodePreprocessor scbpp=new SourceCodePreprocessor(methodContent);
            
            String preprocessed=scbpp.performNLP();
            //System.out.println(preprocessed);
			ContentWriter.writeContent(outFile, preprocessed);
			fileList.add(outFile);
		}
		no_of_method=index;
        this.listNoOfMethod.add(this.packageName+"."+lastPart+","+(no_of_method));
		//System.out.println("Methods extracted successfully!");
	}


	public ArrayList<String> returnFiles()
	{
		return fileList;
	}
	public String getPackageName()
	{
		return this.packageName;
	}
	
	public void writeContent()
	{
	    ContentWriter.writeContent(this.base+".\\data\\"+"listNoOfMethod.txt", this.listNoOfMethod);
	}
	
	protected void createMethodCorpus(String srcDir) {
		// creating normalized corpus
		File dir = new File(srcDir);
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File f : files) {
				if (f.isDirectory()) {
					createMethodCorpus(f.getAbsolutePath());
				} else {
					if (f.getName().endsWith(".java")) {
					    count++;
					   // if(count>3)break;
						extractMethods(f.getAbsolutePath());
						saveMethods(f.getAbsolutePath());
						methodmap.clear();
					}
				}
			}
		} else {
			if (dir.getName().endsWith(".java")) {
			    count++;
                
				extractMethods(dir.getAbsolutePath());
				saveMethods(dir.getAbsolutePath());
				methodmap.clear();
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String base="E:\\PhD\\Repo\\SWT";
		String repoName=base+"\\Source\\swt-3.1\\";
		String methodFolder=base+"\\methodMar2019\\";
		MethodCorpusDeveloper developer=new MethodCorpusDeveloper(repoName,methodFolder,base);
		developer.createMethodCorpus(developer.repoFolder);
		developer.saveMethods(methodFolder);
		developer.writeContent();
	}
}
