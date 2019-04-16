package corpus.creator;

import java.util.ArrayList;

import utility.ContentLoader;
import utility.ContentWriter;

public class MapperCreator {

   
    String base;
    String corpus;
    ArrayList<String> mappedInfo=new ArrayList<>();
    
    public MapperCreator(String base, String corpus)
    {
        this.base=base;
        this.corpus=corpus;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        String base="E:\\PhD\\TextRankBased\\";
        String corpus="eclipse.jdt.core";
        
        MapperCreator obj=new MapperCreator(base, corpus);
        obj.loadMap();
    }

    //load map created by Masud
    public void loadMap ()
    {
        ArrayList <String> content=new ArrayList<>();
        content = ContentLoader.readContent(this.base+"\\CorpusMappingMasud\\"+this.corpus+".ckeys");
        //System.out.println(content);
        processContent(content);
    }
    
    public void processContent(ArrayList <String> content){
        for(String line: content)
        {
            String[] spilter=line.split(":");
            String id=spilter[0];
            String address=spilter[2];
            
            String [] spilter2=address.split("/");
            String addressToSave=spilter2[spilter2.length-1];
            System.out.println(id+":"+addressToSave);
            this.mappedInfo.add(id+":"+addressToSave);
        }
        ContentWriter.writeContent(this.base+"\\CorpusMappingMe\\"+this.corpus+".txt", this.mappedInfo);
    }
}
