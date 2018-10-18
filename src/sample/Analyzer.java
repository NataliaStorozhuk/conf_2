package sample;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

class Analyzer {

   /* List<String> getAllTokensArray(ArrayList<ArrayList<String>> array) {
        ArrayList<String> allArray = new ArrayList<>();
        for (ArrayList<String> anArray : array) allArray.addAll(anArray);

        return allArray.stream().distinct().sorted().collect(Collectors.toList());
    }*/

    static String usingBufferedReader(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }


    ArrayList<String> getWordsAfterPorter(ArrayList<String> res) {
        ArrayList<String> afterPorter = new ArrayList<>();
        AlgorithmPorter algorithmPorter = new AlgorithmPorter();
        for (int i = 0; i < res.size(); i++) {
            String temp = algorithmPorter.stem(res.get(i));
            afterPorter.add(i, temp);
        }
        return afterPorter;
    }

    ArrayList<String> getWordsFromString(String q) {
        String s = q.replaceAll("[^а-яёА-Я a-zA-Z]", " ");
        s = s.toLowerCase();
        ArrayList<String> res = new ArrayList<>(Arrays.asList(s.split("\\s+")));
        return res;
    }

    public ArrayList<String> getWithoutStopWords(ArrayList<String> newS) {
        String stopWordsString = usingBufferedReader("src/resources/stop_words.txt");
        ArrayList<String> stopWords = getWordsFromString(stopWordsString);
        newS.removeAll(stopWords);
        return newS;
    }
}
