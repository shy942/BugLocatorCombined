package corpus.creator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import utility.ContentWriter;
import utility.SelectedBugs;

public class authorInfoCreator {

    String repoName;
    ArrayList<Integer> selectedBugs;
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String repoName = "Eclipse";
        new authorInfoCreator(repoName).createAuthorList();
    }
    

    public authorInfoCreator(String repoName) {
        this.repoName = repoName;
        this.selectedBugs = SelectedBugs.getSelectedBugs(repoName);
    }

    public void createAuthorList()
    {
        ArrayList<String> authorList=collectBugReporterName();
        String outFile="E:\\PhD\\BugLocatorP2\\versionhistory\\author\\"+this.repoName+"AuthorInfo.txt";
        ContentWriter.writeContent(outFile, authorList);
    }
    
    
    protected ArrayList<String> collectBugReporterName() {
        System.out.println(this.selectedBugs.size());
        int i=0;
        ArrayList<String> authorList=new ArrayList<>();
        for (int bugID : this.selectedBugs) {
            System.out.println(++i);
            //String xmlURL = "https://bugs.eclipse.org/bugs/show_bug.cgi?ctype=xml&id="
            //      + bugID;
            String xmlURL="https://bugs.eclipse.org/bugs/show_bug.cgi?ctype=xml&id="+bugID;
            try {
                Document doc = Jsoup.parse(new URL(xmlURL), 5000);
                Element created = doc.getElementsByTag("reporter").first();
                //System.out.println(bugID + "\t" + created.text());
                authorList.add(bugID + "\t" + created.text());
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
          
        }
        return authorList;
    }
    
    
    
}
