package sample;

import java.util.ArrayList;
import java.util.HashMap;

public class FileJanr {

    //number - номер файла
    public Integer number;

    //вектор, в котором список появления слов из общего вектора
    private ArrayList<Integer> tf;

    //вектор, в котором список появления слов из общего вектора
    public ArrayList<String> words;



    public FileJanr(ArrayList<String> afterDeletingStopWords) {
        this.words = afterDeletingStopWords;

    }

    private void getTf(HashMap indexesMap)
   {
       for (int i=0; i<indexesMap.size(); i++)
       {}
       //   if indexesMap.get()

   }
}
