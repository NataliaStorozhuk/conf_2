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
    private int countFiles = 4;
    private int countWords = 100;

    private Stage stage;
    @FXML
    private Button getDoc, getIndex, deleteFolder, cleanLabel, getQ;

    @FXML
    private Label setOut;

    private String pathFile = "C:\\Users\\Natalia\\Desktop\\temp\\";

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
        getIndex.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> getIndexes());

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
        FileUtils.deleteDirectory(pathFile);
    }

    private void addTextToLabel(String s) {
        System.out.println(s);
        setOut.setText(setOut.getText() + s);
    }

    private void getFiles(File file) throws IOException {

        addTextToLabel("Текст из исходного файла:\n");
        String s = Analyzer.usingBufferedReader(file.getPath());
        addTextToLabel(s);

        addTextToLabel("\nТекст после лексической обработки (список исходных лексем):\n");
        ArrayList<String> newS = analyzer.getWordsFromString(s);
        addTextToLabel(newS.toString());

        addTextToLabel("\n" + countFiles + " файлов по " + countWords + " cлов:\n");

        Files.createDirectories(Paths.get(pathFile));
        //пишем 100 файлов
        for (int i = 0; i < countFiles; i++) {

            StringBuilder buf = new StringBuilder();

            for (int j = i * countWords; j < i * countWords + countWords; j++) {

                buf.append(newS.get(j)).append(" ");
            }

            String filename = pathFile + "file_" + i + ".txt";

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

    private void getIndexes() {

        // Это список исходных нормализованных с помощью алгоритма Портера лексем по каждому файлу
        //'это можно будет убрать и заменить на файлы
        ArrayList<FileJanr> listFiles = getLexemesFromFiles();

        //  ArrayList<FileJanr> listFilesNew = getLexemesFromFilesNew();

        //формируем общий массив лексем, сортируем, выкидываем повторы
        List<String> lexemesList = getLexemesList(listFiles);
        System.out.println(lexemesList.toString());

        //тут ключ - это лексема, значение - объект класса FileMap
        //     TreeMap<String, ArrayList<FileMap>> indexesMap = getInvertIndexes(listFiles, lexemesList);
        TreeMap<String, ArrayList<FileMap>> indexesMap = getInvertIndexes(listFiles, lexemesList);

        //выводим инвертированный индекс
   /*     for (String anLexemesList : lexemesList) {
            String buf = getLexemasIndex(indexesMap, anLexemesList);
            addTextToLabel(buf);
        }
*/
        for (FileJanr listFile : listFiles) {
            listFile.setFrequency(indexesMap);
            System.out.println(listFile.frequency.toString());
            listFile.setTf();
            System.out.println(listFile.tf.toString());

        }

        ArrayList<Double> getIdf = getIdf(countFiles, indexesMap);
        System.out.println("Idf");
        System.out.println(getIdf.toString());

        for (FileJanr listFile : listFiles) {
            listFile.setW(getIdf);
            System.out.println("W");
            System.out.println(listFile.w.toString());

        }

        ArrayList<Double> averageW = getAverageW(listFiles, indexesMap);

        CompareResults compareResults = SimCalc.getCompareResults(listFiles.get(0).w, averageW);
        System.out.println(compareResults.toString());
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
            wAverage = temp / (countFiles-1);
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

    private ArrayList<FileJanr> getLexemesFromFiles() {
        addTextToLabel("\nОбработка файлов\n");
        ArrayList<FileJanr> listFiles = new ArrayList<>(countFiles);
        for (int i = 0; i < countFiles; i++) {

            String filename = pathFile + "file_" + i + ".txt";
            File f = new File(filename);
            if (f.exists()) {
                FileJanr fileJanr = getFileJanr(i, filename, f);
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

        return listFiles;
    }

    private FileJanr getFileJanr(int i, String filename, File f) {
        addTextToLabel("\nСодержание файла " + filename + ":\n");

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

        return new FileJanr(i, afterDeletingStopWords);
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
