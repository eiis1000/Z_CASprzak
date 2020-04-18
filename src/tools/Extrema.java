package tools;

import functions.Function;

import java.util.ArrayList;
import java.util.List;

public class Extrema {
    public static double findLocalMinima(Function function, double lowerBound, double upperBound) {
        double[] criticalPoints = SingleVariableSolver.getSolutionsRange(function.getDerivative(0), lowerBound, upperBound);
        if (criticalPoints.length == 0) return Double.NaN;

        List<Double> secondDerivativeIsPositive = new ArrayList<>();
        for (double criticalPoint : criticalPoints) {
            if (function.getNthDerivative(0, 2).evaluate(criticalPoint) > 0) {
                secondDerivativeIsPositive.add(criticalPoint);
            }
        }
        if (secondDerivativeIsPositive.size() == 1) {
            return secondDerivativeIsPositive.get(0);
        }
        double[] functionAtPoints = new double[secondDerivativeIsPositive.size()];
        for (int i = 0; i < functionAtPoints.length; i++) {
            functionAtPoints[i] = function.evaluate(secondDerivativeIsPositive.get(i));
        }
        int smallest = 0;
        for (int i = 1; i < functionAtPoints.length; i++) {
            if (functionAtPoints[i] == functionAtPoints[smallest]) {
                return Double.NaN;
            } else if (functionAtPoints[i] < functionAtPoints[smallest]) {
                smallest = i;
            }
        }
        return secondDerivativeIsPositive.get(smallest);
    }

    public static double findLocalMaxima(Function function, double lowerBound, double upperBound) {
        double[] criticalPoints = SingleVariableSolver.getSolutionsRange(function.getDerivative(0), lowerBound, upperBound);
        if (criticalPoints.length == 0) return Double.NaN;

        List<Double> secondDerivativeIsNegative = new ArrayList<>();
        for (double criticalPoint : criticalPoints) {
            if (function.getNthDerivative(0, 2).evaluate(criticalPoint) < 0) {
                secondDerivativeIsNegative.add(criticalPoint);
            }
        }
        if (secondDerivativeIsNegative.size() == 1) {
            return secondDerivativeIsNegative.get(0);
        }
        double[] functionAtPoints = new double[secondDerivativeIsNegative.size()];
        for (int i = 0; i < functionAtPoints.length; i++) {
            functionAtPoints[i] = function.evaluate(secondDerivativeIsNegative.get(i));
        }
        int largest = 0;
        for (int i = 1; i < functionAtPoints.length; i++) {
            if (functionAtPoints[i] == functionAtPoints[largest]) {
                return Double.NaN;
            } else if (functionAtPoints[i] > functionAtPoints[largest]) {
                largest = i;
            }
        }
        return secondDerivativeIsNegative.get(largest);
    }

    public static double[] findAnyMinima(Function function, double lowerBound, double upperBound) {
        double[] criticalPoints = SingleVariableSolver.getSolutionsRange(function.getDerivative(0), lowerBound, upperBound);
        if (criticalPoints.length == 0) return null;

        List<Double> secondDerivativeIsPositive = new ArrayList<>();
        for (double criticalPoint : criticalPoints) {
            if (function.getNthDerivative(0, 2).evaluate(criticalPoint) > 0) {
                secondDerivativeIsPositive.add(criticalPoint);
            }
        }
        double[] secondDerivativeIsPositivePoints = new double[secondDerivativeIsPositive.size()];
        for (int i = 0; i < secondDerivativeIsPositive.size(); i++) {
            secondDerivativeIsPositivePoints[i] = secondDerivativeIsPositive.get(i);
        }
        return secondDerivativeIsPositivePoints;

    }

    public static double[] findAnyMaxima(Function function, double lowerBound, double upperBound) {
        double[] criticalPoints = SingleVariableSolver.getSolutionsRange(function.getDerivative(0), lowerBound, upperBound);
        if (criticalPoints.length == 0) return null;

        List<Double> secondDerivativeIsNegative = new ArrayList<>();
        for (double criticalPoint : criticalPoints) {
            if (function.getNthDerivative(0, 2).evaluate(criticalPoint) < 0) {
                secondDerivativeIsNegative.add(criticalPoint);
            }
        }
        double[] secondDerivativeIsPositivePoints = new double[secondDerivativeIsNegative.size()];
        for (int i = 0; i < secondDerivativeIsNegative.size(); i++) {
            secondDerivativeIsPositivePoints[i] = secondDerivativeIsNegative.get(i);
        }
        return secondDerivativeIsPositivePoints;

    }

    public static double[] findAnyInflectionPoints(Function function, double lowerBound, double upperBound) {
        double[] criticalPoints = SingleVariableSolver.getSolutionsRange(function.getDerivative(0), lowerBound, upperBound);
        if (criticalPoints.length == 0) return null;

        List<Double> secondDerivativeIsZero = new ArrayList<>();
        for (double criticalPoint : criticalPoints) {
            if (Math.abs(function.getNthDerivative(0, 2).evaluate(criticalPoint)) < 1E-5) {
                secondDerivativeIsZero.add(criticalPoint);
            }
        }
        double[] secondDerivativeIsPositivePoints = new double[secondDerivativeIsZero.size()];
        for (int i = 0; i < secondDerivativeIsZero.size(); i++) {
            secondDerivativeIsPositivePoints[i] = secondDerivativeIsZero.get(i);
        }
        return secondDerivativeIsPositivePoints;

    }
}
