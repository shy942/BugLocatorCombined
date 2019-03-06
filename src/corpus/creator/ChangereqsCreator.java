package corpus.creator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utility.ContentWriter;

public class ChangereqsCreator {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new ChangereqsCreator().createChangereqs("Zxing", "ZxingBugRepository.xml");
    }
    public void createChangereqs(String repo, String xmlFileName)
    {
        String baseForXMLfile="E:\\PhD\\Repo\\"+repo+"\\bugXML\\";
        String xmlFilePath=baseForXMLfile+xmlFileName;
        String baseForOutput="E:\\PhD\\BugLocatorP2\\changereqs\\";
       
        String changereqsOutputFolder=baseForOutput+"\\"+repo+"\\";
        extractAndWriteBugContent(xmlFilePath, changereqsOutputFolder);
        
    }
    protected void extractAndWriteBugContent(String xmlFilePath, String changereqsOutputFolder) {
        ArrayList<String> extractedBugInfo=new ArrayList<>();
        try {
            
            HashMap<String, String> bugInfoHM=new HashMap<>();
          
            
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(xmlFilePath));

            // normalize text representation
            doc.getDocumentElement().normalize();

            NodeList listOfBugs = doc.getElementsByTagName("bug");
            int totalBug = listOfBugs.getLength();
            System.out.println("Total Bug: " + totalBug);

            for (int i = 0; i < listOfBugs.getLength(); i++) {

                 Node nNode = listOfBugs.item(i);
                // System.out.println(nNode.getNodeName());
                 String bugID="";
                 String bugContent="";
                 ArrayList<String> listOfFiles=new ArrayList<>();
                 if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                       Element eElement = (Element) nNode;
                       System.out.println(eElement.getAttribute("id"));
                       bugID=eElement.getAttribute("id");
                       
                       int bugInfo = eElement.getElementsByTagName("buginformation").getLength();
                       
                       if (bugInfo > 0) {
                           String summary = eElement.getElementsByTagName("summary").item(0).getTextContent();
                           //System.out.println(summary);
                           bugContent=bugContent+summary;
                           String description = eElement.getElementsByTagName("description").item(0).getTextContent();
                           //System.out.println(description);
                           bugContent=bugContent+" "+description;
                        
                           //System.out.println(bugID+"\n"+bugContent);
                           bugInfoHM.put(bugID,bugContent);
                        }
                      
                    }
            }
            WriteContent(bugInfoHM, changereqsOutputFolder);
           } catch (Exception exc) {
            exc.printStackTrace();
        }

    }
    
    
    public void WriteContent(HashMap<String, String> bugInfoHM, String changereqsOutputFolder )
    {
        ArrayList<String> list=new ArrayList<>();
        for(String bugID:bugInfoHM.keySet())
        {
            String outFile=changereqsOutputFolder+"\\"+bugID+".txt";
            ContentWriter.writeContent(outFile, bugInfoHM.get(bugID));
        }
   }
}
