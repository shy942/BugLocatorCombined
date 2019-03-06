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

public class goldsetCreator {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new goldsetCreator().createGoldsets("Eclipse", "EclipseBugRepository.xml");
    }
    public void createGoldsets(String repo, String xmlFileName)
    {
        String baseForXMLfile="E:\\PhD\\Repo\\"+repo+"\\bugXML\\";
        String xmlFilePath=baseForXMLfile+xmlFileName;
        String baseForOutput="E:\\PhD\\BugLocatorP2\\goldsets\\";
       
        String goldsetsOutputFolder=baseForOutput+"\\"+repo+"\\";
        extractAndWriteGoldsets(xmlFilePath, goldsetsOutputFolder);
        
    }
    protected void extractAndWriteGoldsets(String xmlFilePath, String goldsetsOutputFolder) {
        
        try {
            
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
                       System.out.println(eElement.getAttribute("id"));
                       bugID=eElement.getAttribute("id");
                       
                       int bugInfo = eElement.getElementsByTagName("buginformation").getLength();
                       
                       
                       NodeList fixfiles = eElement.getElementsByTagName("fixedFiles");
                       NodeList fixfilesChildren = fixfiles.item(0).getChildNodes();

                       for(int k = 0; k < fixfilesChildren.getLength(); k++) {
                           Node file = fixfilesChildren.item(k);
                           //Only want stuff from ELEMENT nodes
                           if(file.getNodeType() == Node.ELEMENT_NODE) {
                               //System.out.println(file.getNodeName()+": "+file.getTextContent());
                               listOfFiles.add(file.getTextContent());
                           }
                       }    
                       bugFixInfoHM.put(bugID, listOfFiles);
                    }
            }
            WriteContent(bugFixInfoHM, goldsetsOutputFolder);
           } catch (Exception exc) {
            exc.printStackTrace();
        }

    }
    
    
    public void WriteContent(HashMap<String, ArrayList<String>> bugFixInfoHM, String changereqsOutputFolder )
    {
        
        for(String bugID:bugFixInfoHM.keySet())
        {
            
            ArrayList<String> listofFixFiles=bugFixInfoHM.get(bugID);
            ContentWriter.writeContent(changereqsOutputFolder+"\\"+bugID+".txt", listofFixFiles);
        }
       
   }
}
