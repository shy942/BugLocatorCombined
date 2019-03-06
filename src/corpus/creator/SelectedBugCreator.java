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



public class SelectedBugCreator {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        //Read the xml file content
      new SelectedBugCreator().createSelectedBug("Zxing", "ZXINGBugRepository.xml");   
    }

    public void createSelectedBug(String repo, String xmlFileName)
    {
        String baseForXMLfile="E:\\PhD\\Repo\\"+repo+"\\bugXML\\";
        String xmlFilePath=baseForXMLfile+xmlFileName;
        String baseForOutput="E:\\PhD\\BugLocatorP2\\SelectedBug\\";
       
        String outfile=baseForOutput+"\\"+repo+"\\"+"selectedbugs"+repo+".txt";
        ArrayList<String> selectedBugs=extractSelectedBug(xmlFilePath, outfile);
        ContentWriter.writeContent(outfile, selectedBugs);
    }
    protected ArrayList<String> extractSelectedBug(String xmlFilePath, String outfile) {
        ArrayList<String> extractedBugInfo=new ArrayList<>();
        try {
            
            HashMap<String, String> bugInfoHM=new HashMap<>();
            HashMap<String, ArrayList<String>> bugFixInfoHM=new HashMap<>();
            
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
                       //System.out.println(eElement.getAttribute("id"));
                       bugID=eElement.getAttribute("id");
                       extractedBugInfo.add(bugID);
                       bugFixInfoHM.put(bugID, listOfFiles);
                    }
            }
           } catch (Exception exc) {
            exc.printStackTrace();
        }

        return extractedBugInfo;
    }
   
}
