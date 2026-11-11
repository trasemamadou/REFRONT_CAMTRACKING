package com.groupeisi.ms1_gestschool.examen.parametrage;

public class CalculationUtils {
    // moyenne simple
    public double avg(double... values) {
        if (values == null || values.length == 0) return 0;
        double s = 0;
        for (double v : values) s += v;
        return s / values.length;
    }

    // somme
    public double sum(double... values) {
        double s = 0;
        for (double v : values) s += v;
        return s;
    }

    // moyenne pondérée: valeurs[], weights[] must be same length
    public double weightedAverage(double[] values, double[] weights) {
        if (values == null || weights == null || values.length == 0) return 0;
        double num = 0, den = 0;
        int n = Math.min(values.length, weights.length);
        for (int i = 0; i < n; i++) {
            num += values[i] * weights[i];
            den += weights[i];
        }
        return den == 0 ? 0 : num / den;
    }
}
