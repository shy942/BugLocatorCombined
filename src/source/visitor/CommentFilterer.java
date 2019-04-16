package source.visitor;

import java.io.ByteArrayInputStream;
import utility.ContentLoader;
import utility.ContentWriter;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class CommentFilterer {

	String fileURL;
	String fileName;
	String outputURL;

	public CommentFilterer(String fileName) {
		this.fileName = fileName;
		this.fileURL = "./data/" + fileName;
	

	}
	
	public CommentFilterer(String fileName, String outPutURL) {
		this.fileName = fileName;
		this.fileURL = fileName;
		this.outputURL=outPutURL;
	}

	public void discardClassHeaderComment() {
		// discarding class header comment
		try {
			String orgContent = ContentLoader.readContentSimple(this.fileURL);
			CompilationUnit cu = JavaParser.parse(new ByteArrayInputStream(
					orgContent.getBytes()));
			//System.out.println(cu.getComment().toString());
			if(cu.getComment()!=null)cu.getComment().setContent(""); // class header comment is deleted
			//System.out.println(cu);
			//String refinedContent = cu.toString();
			
			//saveUpdatedVersion("./data/changed.java", refinedContent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void saveUpdatedVersion(String fileName, String updatedContent) {
		String outFileURL =  fileName;
		ContentWriter.writeContent(outFileURL, updatedContent);
	}

	public static void main(String[] args) {
		String srcFile = "AbstractHandler.java";
		new CommentFilterer(srcFile).discardClassHeaderComment();
	}
}
