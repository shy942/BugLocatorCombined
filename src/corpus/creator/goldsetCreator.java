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

import utility.ContentLoader;
import utility.ContentWriter;

public class goldsetCreator {

    String base;
    String corpus;
    ArrayList <String> goldsetList=new ArrayList<>();
    ArrayList<String> validSoureList=new ArrayList<>();
    public goldsetCreator(String base, String corpus)
    {
        this.base=base;
        this.corpus=corpus;
    }
    
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String base="E:\\PhD\\TextRankBased\\";
        String corpus="eclipse.jdt.core";
        goldsetCreator obj= new goldsetCreator(base, corpus);
        obj.getValidCorpusInfo();
        obj.createGoldSetMe();
        System.out.println(obj.goldsetList);
    }
    
    public void getValidCorpusInfo()
    {
        ArrayList<String> fileListId=LoadFileLengthList();
        HashMap<String, String> mappedInfo=getCorpusMapping();
        for(String fileID: fileListId)
        {
            this.validSoureList.add(mappedInfo.get(fileID));
        }
        System.out.println(this.validSoureList);
    }
    
    
    
    public HashMap<String, String> getCorpusMapping(){
        HashMap<String, String> mappedInfo=new HashMap<>();
        
        ArrayList<String> content=ContentLoader.readContent(this.base+"\\CorpusMappingMe\\"+this.corpus+".txt");
        for(String line:content)
        {
            String[] spilter = line.split(":");
            String fileID=spilter[0];
            String fileName=spilter[1];
            mappedInfo.put(fileID, fileName);
        }
        return mappedInfo;
    }
    
    public ArrayList<String> LoadFileLengthList()
    {
        ArrayList<String> fileListId=new ArrayList<>();
        ArrayList<String> content=ContentLoader.readContent(this.base+"\\FileInfo\\"+this.corpus+"-lengthList.txt");
        for(String line:content)
        {
            String[] spilter = line.split(":");
            String fileID=spilter[0];
            fileListId.add(fileID);
        }
        return fileListId;
    }
    public void createGoldSetMe()
    {
        //Load Goldset from Masud's creation
        File goldsetFolder=new File(this.base+"\\Goldset\\"+this.corpus+"\\");
        for (final File fileEntry : goldsetFolder.listFiles()) {
            if (fileEntry.isDirectory()) {
                //Do nothing
            } else {
                
                if (fileEntry.getName().endsWith(".txt")) {
                    //Save to goldsetArrayList
                    ArrayList<String> content=ContentLoader.readContent(fileEntry.getAbsolutePath());
                    ArrayList<String> tempList=new ArrayList<>();
                    int no_of_address_exist=0;
                    for(String line: content)
                    {
                        String[] spilter=line.split("/");
                        String address=spilter[spilter.length-1];
                        if(this.validSoureList.contains(address)) 
                            {
                                 no_of_address_exist++;
                                 tempList.add(address);
                            }
                        //if(this.validSoureList.contains(address))this.goldsetList.add(fileEntry.getName().substring(0, fileEntry.getName().length()-4)+","+address);
                    }
                    if(no_of_address_exist==content.size())
                        {
                            for(String line:tempList)
                            {
                                this.goldsetList.add(fileEntry.getName().substring(0, fileEntry.getName().length()-4)+","+line);
                            }
                        }
                }
            }
        }
        ContentWriter.writeContent(this.base+"\\GoldsetMe\\"+this.corpus+".txt", this.goldsetList);
    }
    
    
    
   /* public void createGoldsets(String repo, String xmlFileName)
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
       
   }*/
}
