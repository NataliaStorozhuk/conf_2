package sample;

import java.util.ArrayList;

public class SimCalc {


    private static Double getCos(ArrayList<Double> wQ, ArrayList<Double> wDocs) {
        Double getSkalar = getSkalar(wQ, wDocs);
        Double getCos = 0.0;
        Double getChisl = Math.sqrt(getSkalar);
        Double getZnam = 0.0;
        Double getZnam1 = 0.0;
        Double getZnam1Sum = 0.0;
        Double getZnam2 = 0.0;
        Double getZnam2Sum = 0.0;

        for (int i = 0; i < wQ.size(); i++) {
            getZnam1Sum += wQ.get(i);
            getZnam2Sum += wDocs.get(i);
        }
        getZnam1 = Math.sqrt(getZnam1Sum);
        getZnam2 = Math.sqrt(getZnam2Sum);

        getZnam = getZnam1 * getZnam2;
        getCos = getChisl / getZnam;

        System.out.println("getCos=" + getCos);
        return getCos;
    }

    private static Double getManhetten(ArrayList<Double> wQ, ArrayList<Double> wDocs) {
        Double getManhetten = 0.0;

        for (int i = 0; i < wQ.size(); i++) {
            getManhetten += Math.abs((wQ.get(i) - wDocs.get(i)));
        }
        System.out.println("getManhetten=" + getManhetten);
        return getManhetten;
    }

    private static Double getEvklid(ArrayList<Double> wQ, ArrayList<Double> wDocs) {
        Double getEvklid = 0.0;
        Double getSum = 0.0;

        for (int i = 0; i < wQ.size(); i++) {
            getSum += Math.pow((wQ.get(i) - wDocs.get(i)), 2);
        }
        getEvklid = Math.sqrt(getSum);

        System.out.println("getEvklid=" + getEvklid);
        return getEvklid;
    }

    private static Double getSkalar(ArrayList<Double> wQ, ArrayList<Double> wDocs) {
        Double getSkalar = 0.0;
        Integer сountPairs = 0;

        for (int i = 0; i < wQ.size(); i++) {
            getSkalar += wQ.get(i) * wDocs.get(i);
            if (wQ.get(i) != 0 && wDocs.get(i) != 0)
                сountPairs++;
        }

        System.out.println("getSkalar=" + getSkalar);
        return getSkalar;
    }

    public static CompareResults getCompareResults(ArrayList<Double> wQ, ArrayList<Double> wDocs) {
        //получаем скалярное произведение
        Double getSkalar = getSkalar(wQ, wDocs);

        //получаем эвклидово
        Double getEvklid = getEvklid(wQ, wDocs);

        //получаем манхеттенское

        Double getManhetten = getManhetten(wQ, wDocs);

        //получаем косинусное
        Double getCos = getCos(wQ, wDocs);

        CompareResults compareResults = new CompareResults();
        compareResults.skalar = getSkalar;
        compareResults.cos = getCos;
        compareResults.evkl = getEvklid;
        compareResults.manh = getManhetten;
        return compareResults;
    }
}
