package sample;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.codehaus.plexus.util.FileUtils;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Controller {

    private Analyzer analyzer = new Analyzer();
    private int countFiles = 5;
    private int countWords = 100;

    private Stage stage;
    @FXML
    private Button getDoc, getIndex, deleteFolder, cleanLabel, getQ, anotherGenre;


    @FXML
    private Label name1, name2, name3, name4, countLex1, countLex2, countLex3, countLex4,
            countStop1, countStop2, countStop3, countStop4, durabilityStem1, durabilityStem2,
            durabilityStem3, durabilityStem4;
    @FXML
    private Label skalar1, cos1, evkl1, manh1, durability1,
            skalar2, cos2, evkl2, manh2, durability2;

    @FXML
    private Label setOut;

    private String pathDetFile = "C:\\Users\\Natalia\\Desktop\\tempDet\\";
    private String pathAnotherFile = "C:\\Users\\Natalia\\Desktop\\tempAnother\\";

    public Controller() {
    }

    @FXML
    public void initialize() {
        setOut.setAlignment(Pos.TOP_LEFT);
        addTextToLabel("Язык: Русский \n");
        addTextToLabel("Число файлов: " + countFiles + "\n");
        addTextToLabel("Число слов в файле: " + countWords + "\n");


        getDoc.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            setOut.setAlignment(Pos.TOP_LEFT);
            final FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    getFiles(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        getIndex.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> processingNormal());
        anotherGenre.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> processingAnother());

        getQ.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            setOut.setAlignment(Pos.TOP_LEFT);
            final FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                getRequest(file);
            }
        });
        //    getQ.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> getRequest());
        deleteFolder.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            try {
                deleteFolder();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Удаление папки");
            alert.setHeaderText(null);
            alert.setContentText("Папка удалена успешно!");
            alert.showAndWait();
        });
        cleanLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> setOut.setText(null));
    }

    private void getRequest(File file) {

        File f = new File(file.getPath());
        FileJanr fileQ = getFileJanr(-1, file.getName(), f);

    }

    private void deleteFolder() throws IOException {
        FileUtils.deleteDirectory(pathDetFile);
    }

    private void addTextToLabel(String s) {
     /*   System.out.println(s);
        setOut.setText(setOut.getText() + s);*/
    }

    private void getFiles(File file) throws IOException {

        addTextToLabel("Текст из исходного файла:\n");
        String s = Analyzer.usingBufferedReader(file.getPath());
        addTextToLabel(s);

        addTextToLabel("\nТекст после лексической обработки (список исходных лексем):\n");
        ArrayList<String> newS = analyzer.getWordsFromString(s);
        addTextToLabel(newS.toString());

        addTextToLabel("\n" + countFiles + " файлов по " + countWords + " cлов:\n");

        Files.createDirectories(Paths.get(pathDetFile));
        //пишем 100 файлов
        for (int i = 0; i < countFiles; i++) {

            StringBuilder buf = new StringBuilder();

            for (int j = i * countWords; j < i * countWords + countWords; j++) {

                buf.append(newS.get(j)).append(" ");
            }

            String filename = pathDetFile + "file_ (" + i + ").txt";

            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filename), "utf-8"))) {
                writer.write(buf.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            addTextToLabel("\n" + filename + ":\n");
            addTextToLabel(buf.toString() + "\n");
        }
    }

    private void processingNormal() {

        // Это список исходных нормализованных с помощью алгоритма Портера лексем по каждому файлу
        //'это можно будет убрать и заменить на файлы
        ArrayList<FileJanr> listFiles = getLexemesFromFiles(pathDetFile);
        CompareResults compareResults = processing(listFiles);

        evkl1.setText(compareResults.evkl.toString());
        manh1.setText(compareResults.manh.toString());
        skalar1.setText(compareResults.skalar.toString());
        cos1.setText(compareResults.cos.toString());
        durability1.setText(compareResults.durability.toString());

    }

    private void processingAnother() {

        // Это список исходных нормализованных с помощью алгоритма Портера лексем по каждому файлу
        //'это можно будет убрать и заменить на файлы
        ArrayList<FileJanr> listFiles = getLexemesFromFiles(pathAnotherFile);
        CompareResults compareResults = processing(listFiles);

        evkl2.setText(compareResults.evkl.toString());
        manh2.setText(compareResults.manh.toString());
        skalar2.setText(compareResults.skalar.toString());
        cos2.setText(compareResults.cos.toString());
        durability2.setText(compareResults.durability.toString());

    }

    private CompareResults processing(ArrayList<FileJanr> listFiles) {
        long start = System.currentTimeMillis();
        //формируем общий массив лексем, сортируем, выкидываем повторы
        List<String> lexemesList = getLexemesList(listFiles);
        System.out.println(lexemesList.toString());

        //тут ключ - это лексема, значение - объект класса FileMap
        //     TreeMap<String, ArrayList<FileMap>> indexesMap = getInvertIndexes(listFiles, lexemesList);
        TreeMap<String, ArrayList<FileMap>> indexesMap = getInvertIndexes(listFiles, lexemesList);

        for (FileJanr listFile : listFiles) {
            listFile.setFrequency(indexesMap);
            System.out.println(listFile.frequency.toString());
            listFile.setTf();
            System.out.println(listFile.tf.toString());
        }

        ArrayList<Double> getIdf = getIdf(countFiles, indexesMap);
        //    System.out.println("Idf");
        //   System.out.println(getIdf.toString());

        for (FileJanr listFile : listFiles) {
            listFile.setW(getIdf);
            System.out.println("W");
            System.out.println(listFile.w.toString());

        }

        ArrayList<Double> averageW = getAverageW(listFiles, indexesMap);

        CompareResults compareResults = SimCalc.getCompareResults(listFiles.get(0).w, averageW);

        long finish = System.currentTimeMillis();
        long timeConsumedMillis = finish - start;

        compareResults.durability = timeConsumedMillis;
        return compareResults;
    }

    private ArrayList<Double> getAverageW(ArrayList<FileJanr> listFiles, TreeMap<String, ArrayList<FileMap>> indexesMap) {

        ArrayList<Double> getAverageW = new ArrayList<>();
        for (int i = 0; i < indexesMap.size(); i++) {
            Double wAverage;
            Double temp = 0.0;
            //берем не все, а только от 1, чтоб первый документ - как бы запрос
            for (int j = 1; j < listFiles.size(); j++) {
                temp += listFiles.get(j).w.get(i);
            }
            wAverage = temp / (countFiles - 1);
            getAverageW.add(wAverage);
        }
        System.out.println("Средние частоты");
        System.out.println(getAverageW.toString());
        return getAverageW;
    }

    private ArrayList<Double> getIdf(Integer N, TreeMap<String, ArrayList<FileMap>> treeMap) {
        ArrayList<Double> idf = new ArrayList<>();

        for (Map.Entry<String, ArrayList<FileMap>> item : treeMap.entrySet()) {

            Integer countDocs = item.getValue().size();

            BigDecimal bdCountDocs = new BigDecimal(countDocs);
            BigDecimal bdN = new BigDecimal(N);
            BigDecimal arg = bdN.divide(bdCountDocs, 8, BigDecimal.ROUND_HALF_EVEN);
            double log = Math.log(arg.doubleValue());

            idf.add(log);

        }
        //     System.out.println(idf);
        return idf;
    }

    private TreeMap<String, ArrayList<FileMap>> getInvertIndexes(ArrayList<FileJanr> listFiles, List<String> lexemesList) {

        TreeMap<String, ArrayList<FileMap>> indexesMap = new TreeMap<>();

        // проходим по списку лексем (словарю)
        for (String dictionaryWord : lexemesList) {
            indexesMap.put(dictionaryWord, new ArrayList<FileMap>());
            //просматриваем все файлы по очереди
            for (int numberFile = 0; numberFile < listFiles.size(); numberFile++) {
                //проходим по каждой лексеме файла
                for (int wordPosition = 0; wordPosition < listFiles.get(numberFile).words.size(); wordPosition++) {
                    //текущее слово в файле
                    String currentWord = listFiles.get(numberFile).words.get(wordPosition);
                    //если слово совпадает со словом в словаре
                    if (currentWord.equals(dictionaryWord)) {
                        addWordPosition(indexesMap, dictionaryWord, numberFile, wordPosition);
                    }
                }
            }
        }

        return indexesMap;
    }

    private void addWordPosition(TreeMap indexesMap, String dictionaryWord, int numberFile, int wordPosition) {
        //получаем список файлов этого слова
        ArrayList<FileMap> listDocs;
        listDocs = (ArrayList<FileMap>) indexesMap.get(dictionaryWord);
        if (listDocs.size() != 0) {
            int test = -1;
            //если этого документа ещё нет в списке - добавляем и пишем номер лексемы
            for (int numberDoc = 0; numberDoc < listDocs.size(); numberDoc++) {

                FileMap aTemp = listDocs.get(numberDoc);
                //если такого документа ещё нет в списке у этого слова -
                // добавляем документ и словопозицию
                if (aTemp.number.equals(numberFile)) {
                    test = numberDoc;
                    break;
                }
            }
            if (test == -1)
                addDocAndWordPosition(numberFile, wordPosition, listDocs);
            else updateDocInfo(wordPosition, listDocs, test, listDocs.get(test));
        } else addDocAndWordPosition(numberFile, wordPosition, listDocs);
        indexesMap.replace(dictionaryWord, listDocs);
    }

    private void updateDocInfo(int p, ArrayList<FileMap> temp, int i, FileMap aTemp) {
        FileMap fm;
        fm = aTemp;
        fm.addWordPosition(p);
        temp.set(i, fm);
    }

    private void addDocAndWordPosition(int j, int p, ArrayList<FileMap> temp) {
        FileMap fm;
        fm = new FileMap(j);
        fm.addWordPosition(p);
        temp.add(fm);
    }

    private List<String> getLexemesList(ArrayList<FileJanr> listFiles) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (FileJanr listFile : listFiles) {
            arrayList.addAll(listFile.words);

        }
        List<String> arrayAfterSort = arrayList.stream().distinct().sorted().collect(Collectors.toList());
        addTextToLabel("\n" + "Вектор лексем из файлов после фильтрации, сортировки, без повторов:\n");
        addTextToLabel(arrayAfterSort + ":\n");
        return arrayAfterSort;
    }

    private ArrayList<FileJanr> getLexemesFromFiles(String path) {
        addTextToLabel("\nОбработка файлов\n");
        ArrayList<FileJanr> listFiles = new ArrayList<>(countFiles);
        Integer countWords = 0;
        Integer countAfterPorter = 0;
        long duration = 0;
        for (int i = 0; i < countFiles; i++) {

            String filename = path + "file_ (" + i + ").txt";
            File f = new File(filename);
            if (f.exists()) {
                FileJanr fileJanr = getFileJanr(i, filename, f);
                countWords += fileJanr.countWords;
                countAfterPorter += fileJanr.countAfterPorter;
                duration += fileJanr.duration;
                listFiles.add(fileJanr);

            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Файл отсутствует");
                alert.setHeaderText(null);
                alert.setContentText("Файл с именем " + filename + " отсутствует." +
                        " Выберите другой исходный файл для генерации или уменьшите размер или число файлов.");
                alert.showAndWait();
                break;
            }
        }
        countLex1.setText(listFiles.get(0).countWords.toString());
        countStop1.setText(listFiles.get(0).countAfterPorter.toString());
        durabilityStem1.setText(String.valueOf(listFiles.get(0).duration));

        if (path == pathAnotherFile) {
            countLex3.setText(countWords.toString());
            countStop3.setText(countAfterPorter.toString());
            durabilityStem3.setText(String.valueOf(duration));
        } else {
            countLex2.setText(countWords.toString());
            countStop2.setText(countAfterPorter.toString());
            durabilityStem2.setText(String.valueOf(duration));
        }


        return listFiles;
    }

    private FileJanr getFileJanr(int i, String filename, File f) {
        // addTextToLabel("\nСодержание файла " + filename + ":\n");
        long start = System.currentTimeMillis();
        String s = Analyzer.usingBufferedReader(f.getPath());
        //      addTextToLabel(s);

        //    addTextToLabel("После обработки:\n");
        ArrayList<String> newS = analyzer.getWordsFromString(s);
        //  addTextToLabel(newS + "\n");

        //addTextToLabel("После стемминга:\n");
        ArrayList<String> afterPorter = analyzer.getWordsAfterPorter(newS);
        //  addTextToLabel(afterPorter + "\n");

        //addTextToLabel("После удаления стоп слов:\n");
        ArrayList<String> afterDeletingStopWords = analyzer.getWithoutStopWords(afterPorter);
        //  addTextToLabel(afterDeletingStopWords + "\n");
        long finish = System.currentTimeMillis();
        long timeConsumedMillis = finish - start;
        return new FileJanr(i, afterDeletingStopWords, newS.size(), afterPorter.size(), timeConsumedMillis);
    }

    private String getLexemasIndex(TreeMap<String, ArrayList<FileMap>> hm, String anLexemesList) {
        StringBuilder s = new StringBuilder();
        s.append("\n\n").append(anLexemesList).append(": ");
        ArrayList<FileMap> fm = hm.get(anLexemesList);
        for (FileMap aFm : fm) {
            s.append("\nНомер файла ").append((aFm.number)).append(", словопозиции: ");
            for (int q = 0; q < aFm.wordPositions.size(); q++) {
                s.append((aFm.wordPositions.get(q))).append(", ");
            }
        }
        return s.toString();
    }

    void setStageAndSetupListeners(Stage primaryStage) {

        stage = primaryStage;
    }

}
