package corpus.creator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import source.visitor.CommentFilterer;

import config.StaticData;
import utility.ContentLoader;
import utility.MiscUtility;

public class SourceCodePreprocessor {

	String content;
	ArrayList<String> stopwords;
	ArrayList<String> javaKeywords;

	public SourceCodePreprocessor(String content) {
		this.content = content;
		this.stopwords = new ArrayList<String>();
		this.loadStopWords();
		this.javaKeywords = new ArrayList<String>();
		this.loadJavaKeywords();
	}

	public void loadStopWords() {
		this.stopwords = ContentLoader.readContent(".\\NLP\\stop-words-english-total.txt");
	}

	protected ArrayList<String> removeStopWords(ArrayList<String> words) {
		ArrayList<String> refined = new ArrayList<String>(words);
		for (String word : words) {
			if (this.stopwords.contains(word)) {
				refined.remove(word);
			}
		}
		return refined;
	}

	public void loadJavaKeywords() {
	    this.javaKeywords=ContentLoader.readContent(".\\NLP\\java-keywords.txt");
		
		
	}

	protected ArrayList<String> removeJaveKeywords(ArrayList<String> words) {
		ArrayList<String> refinedKeywords = new ArrayList<String>(words);
		for (String word : words) {
			if (this.javaKeywords.contains(word)) {
				refinedKeywords.remove(word);
			}
		}
		return refinedKeywords;
	}

	public String performNLP() {
		// performing NLP operations
		ArrayList<String> words = splitContent(this.content);
		ArrayList<String> refined = removeStopWords(words);
		ArrayList<String> refinedKeywords = removeJaveKeywords(refined);
		ArrayList<String> list = new ArrayList<String>();
		for (String word : refinedKeywords) {
			if (!word.trim().isEmpty()) {

				if (word.length() >= 3) {
					// System.out.println("word: "+word+"\n");
					word=word.toLowerCase();
					word=word.trim();
					list.add(word);
				}
			}
		}

		return MiscUtility.list2Str(list);

	}

	protected ArrayList<String> splitContent(String content) {
		String[] words = content.split("(?=[A-Z])");
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(words));
		String content2 = MiscUtility.list2Str(list);
		String[] words2 = content2.split("\\s+|\\p{Punct}+|\\d+");

		return new ArrayList<String>(Arrays.asList(words2));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
