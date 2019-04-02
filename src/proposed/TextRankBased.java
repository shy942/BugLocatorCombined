package proposed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import Graph.TokenRankProvider;
import Graph.WordNetworkMaker;
import proposed.QueryToken;
import utility.MiscUtility;

public class TextRankBased {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        ArrayList<String> sentences=new ArrayList<String>();
        sentences.add("w1 w2 w3 w1 w5");
        sentences.add("w3 w4 w6 w9 w2");
        HashMap<String, QueryToken> tokenRankMap = new HashMap<>();
        WordNetworkMaker networkMaker = new WordNetworkMaker(sentences);
        tokenRankMap = getQueryTokenRankScores(networkMaker, true);
        System.out.println(tokenRankMap);
        
        Iterator it = tokenRankMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String keyword = pair.getKey().toString();
            QueryToken tokendb=(QueryToken)pair.getValue();
            System.out.println(pair.getKey() + " = " + tokendb.tokenRankScore);
            
        }
        // tokenRankMap = getQueryTokenRankScores(networkMaker, true);
        //tokenRankMap = filterStopWords(tokenRankMap);
        //tokenRankMap=filterLowScores(tokenRankMap, "TR");
        //tokenRankMap = MiscUtility.normalizeScore(tokenRankMap, "TR");
        //return tokenRankMap;
    }
    protected static HashMap<String, QueryToken> getQueryTokenRankScores(
            WordNetworkMaker networkMaker, boolean weighted) {
        // collect query token scores
        HashMap<String, QueryToken> tokendb = new HashMap<>();
        if (weighted) {
            SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> wgraph = networkMaker
                    .createWeightedWordNetwork();
            tokendb = networkMaker.getTokenDictionary(true);
            TokenRankProvider rankProvider = new TokenRankProvider(wgraph,
                    tokendb, false);
            tokendb = rankProvider.calculateTokenRankWeighted();
        } else {
            DirectedGraph<String, DefaultEdge> graph = networkMaker
                    .createWordNetwork();
            tokendb = networkMaker.getTokenDictionary(false);
            TokenRankProvider rankProvider = null;
            //if (customInitialize) {
               // HashMap<String, Double> initializerMap = getTFIDFScores(new HashSet<String>(
                  //      tokendb.keySet()));
                //rankProvider = new TokenRankProvider(graph, tokendb, false,
                    //    initializerMap);
           // } else {
                rankProvider = new TokenRankProvider(graph, tokendb, false);
            //}
            tokendb = rankProvider.calculateTokenRank();
        }
        // token DB containing scores now.
        return tokendb;
    }
}
