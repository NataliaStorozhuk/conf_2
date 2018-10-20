package sample;

import java.util.ArrayList;

public class SimCalc {


    private Double getCos(ArrayList<Double> wQ, ArrayList<Double> wDocs, Double getSkalar) {
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

    private Double getManhetten(ArrayList<Double> wQ, ArrayList<Double> wDocs) {
        Double getManhetten = 0.0;

        for (int i = 0; i < wQ.size(); i++) {
            getManhetten += Math.abs((wQ.get(i) - wDocs.get(i)));
        }
        System.out.println("getManhetten=" + getManhetten);
        return getManhetten;
    }

    private Double getEvklid(ArrayList<Double> wQ, ArrayList<Double> wDocs) {
        Double getEvklid = 0.0;
        Double getSum = 0.0;

        for (int i = 0; i < wQ.size(); i++) {
            getSum += Math.pow((wQ.get(i) - wDocs.get(i)), 2);
        }
        getEvklid = Math.sqrt(getSum);

        System.out.println("getSum=" + getSum);
        System.out.println("getEvklid=" + getEvklid);
        return getEvklid;
    }

    private Double getSkalar(ArrayList<Double> wQ, ArrayList<Double> wDocs) {
        Double getSkalar = 0.0;
        Integer сountPairs = 0;

        for (int i = 0; i < wQ.size(); i++) {
            getSkalar += wQ.get(i) * wDocs.get(i);
            if (wQ.get(i) != 0 && wDocs.get(i) != 0)
                сountPairs++;
        }

        System.out.println("skalar=" + getSkalar);
        System.out.println("pair=" + сountPairs);
        return getSkalar;
    }

}
