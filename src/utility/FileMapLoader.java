package utility;

import java.util.ArrayList;
import java.util.HashMap;
import config.StaticData;

public class FileMapLoader {

	

	public static HashMap<Integer, String> loadKeyMap(String repoName) {
		String keysFile = StaticData.BLP2_EXP + "/FileKeyMap/" + repoName
				+ "FileKeyMap.txt";
		ArrayList<String> lines = ContentLoader.getAllLinesOptList(keysFile);
		HashMap<Integer, String> keyMap = new HashMap<>();
		for (String line : lines) {
		    System.out.println(line);
			String[] parts = line.split(":");
			String key = parts[1].trim();
			//String canonical = key.replace('\\', '.');
			int index = Integer.parseInt(parts[0].trim());
			keyMap.put(index, key);
		}
		return keyMap;
	}

	
}
