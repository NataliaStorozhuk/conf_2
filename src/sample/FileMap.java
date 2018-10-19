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


    FileMap(Integer number) {
        this.number = number;
        //добавляем в список массвов словопозиций
        this.wordPositions = new ArrayList();
    }

    void addWordPosition(int p) {
        wordPositions.add(p);
    }

}
