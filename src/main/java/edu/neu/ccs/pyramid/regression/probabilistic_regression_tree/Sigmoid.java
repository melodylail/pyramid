package edu.neu.ccs.pyramid.regression.probabilistic_regression_tree;

import edu.neu.ccs.pyramid.util.MathUtil;
import org.apache.mahout.math.Vector;

/**
 * Created by chengli on 5/21/15.
 */
public class Sigmoid implements GatingFunction {
    private Vector weights;
    private double bias;

    public Sigmoid(Vector weights, double bias) {
        this.weights = weights;
        this.bias = bias;
    }

    public Vector getWeights() {
        return weights;
    }

    public double getBias() {
        return bias;
    }

    @Override
    public double leftProbability(Vector row) {
        double logProb = 0;
        double[] scores = new double[2];
        scores[0] = 0;
        scores[1] = row.dot(weights) + bias;
        double logNumerator = scores[1];
        double logDenominator = MathUtil.logSumExp(scores);
        logProb += logNumerator;
        logProb -= logDenominator;
        return Math.exp(logProb);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Sigmoid{");
        sb.append("weights=").append(weights);
        sb.append(", bias=").append(bias);
        sb.append('}');
        return sb.toString();
    }
}