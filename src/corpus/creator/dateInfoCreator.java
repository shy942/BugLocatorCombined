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

public class dateInfoCreator {
    String repoName;
    ArrayList<Integer> selectedBugs;
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String repoName = "Eclipse";
        new dateInfoCreator(repoName).createDateList();
    }

    
   public dateInfoCreator(String repoName) {
        this.repoName = repoName;
        this.selectedBugs = SelectedBugs.getSelectedBugs(repoName);
    }
   
   public void createDateList()
   {
       ArrayList<String> dateList=collectBugReportDates();
       String outFile="E:\\PhD\\BugLocatorP2\\versionhistory\\date\\"+this.repoName+"dateInfo.txt";
       ContentWriter.writeContent(outFile, dateList);
   }
   

    protected ArrayList<String>  collectBugReportDates() {
        ArrayList<String> dateList=new ArrayList<>();
        int i=0;
        for (int bugID : this.selectedBugs) {
            System.out.println(++i);
            //String xmlURL="https://bugs.eclipse.org/bugs/show_bug.cgi?ctype=xml&id="+bugID;
            String xmlURL="https://bugs.eclipse.org/bugs/show_bug.cgi?ctype=xml&id="+bugID;
            try {
                Document doc=Jsoup.parse(new URL(xmlURL),5000);
                Element created= doc.getElementsByTag("creation_ts").first();
                //System.out.println(bugID+"\t"+created.text());
                dateList.add(bugID+"\t"+created.text());
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        return dateList;
    }
    
    
}
