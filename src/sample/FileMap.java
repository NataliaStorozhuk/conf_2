package sample;

import lombok.Data;

import java.util.ArrayList;

@Data
public class FileMap {
    //number - номер файла
    public Integer number;
    //cписок словопозиций: представлен как список массивов:
    // (гибридная схема согласно варианта предполагает список массивов фиксированной длины)
    ArrayList wordPositions;
    //индекс первого свободного элемента в последнем массиве

    //вектор, сколько слово встерчается в этом тексте
    private ArrayList<Integer> tf;


    FileMap(Integer number, int wordpositions) {
        this.number = number;
        //добавляем в список массвов словопозиций
        this.wordPositions = new ArrayList();
    }

    void addWordPosition(int p) {
        wordPositions.add(p);
    }

    void getTf(ArrayList<String> tf) {

    }
}
