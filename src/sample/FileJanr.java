package sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class FileJanr {

    //number - номер файла
    public Integer number;

    //вектор, в котором список появления слов из общего вектора
    public ArrayList<Integer> frequency;

    //вектор, в котором список всех слов из этого файла
    public ArrayList<String> words;

    //нормальизованная частота которая freq/ max freq
    public ArrayList<Double> tf;

    //w
    public ArrayList<Double> w;

    public FileJanr(int i, ArrayList<String> afterDeletingStopWords) {
        this.number = i;
        this.words = afterDeletingStopWords;
    }


    public void setFrequency(TreeMap<String, ArrayList<FileMap>> indexesMap) {
        this.frequency = new ArrayList<Integer>();

        for (Map.Entry<String, ArrayList<FileMap>> e : indexesMap.entrySet()) {

            //    System.out.println(e.getKey() + " " + e.getValue());

            ArrayList<FileMap> fileMaps = e.getValue();
            Integer count = 0;
            for (int i = 0; i < fileMaps.size(); i++) {
                if (fileMaps.get(i).number == this.number) {
                    count = fileMaps.get(i).wordPositions.size();
                    break;
                }
            }
            frequency.add(count);
            //    System.out.println("ФАйл номер " + this.number + " слово " +e.getKey() + "число повторений " + count );

        }

    }

    public void setTf() {
        this.tf = new ArrayList<Double>();
        Integer max = Collections.max(this.frequency);
        for (int i = 0; i < frequency.size(); i++) {
            double d = ((double) frequency.get(i)) / (int) max;
            this.tf.add(d);
            //  System.out.println(this.tf.get(i));
        }
    }

    public void setW(ArrayList<Double> frequency) {
        w = new ArrayList<Double>();
        for (int i = 0; i < tf.size(); i++) {
            Double temp = getW(tf.get(i), frequency.get(i));
            w.add(temp);
        }
    }

    //рассчет по формуле
    private Double getW(Double tf, Double idf) {
        Double temp = (0.5 + 0.5 * tf) * idf;
        return temp;
    }
}
