package edu.neu.ccs.pyramid.experiment;

import edu.neu.ccs.pyramid.configuration.Config;
import edu.neu.ccs.pyramid.dataset.DataSetType;
import edu.neu.ccs.pyramid.dataset.MultiLabelClfDataSet;
import edu.neu.ccs.pyramid.dataset.TRECFormat;
import edu.neu.ccs.pyramid.eval.Accuracy;
import edu.neu.ccs.pyramid.eval.Overlap;
import edu.neu.ccs.pyramid.multilabel_classification.bmm_variant.BMMClassifier;
import edu.neu.ccs.pyramid.multilabel_classification.bmm_variant.BMMOptimizer;

import java.io.File;


/**
 * Created by Rainicy on 10/24/15.
 */
public class Exp211 {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Please specify a properties file.");
        }

        Config config = new Config(args[0]);

        System.out.println(config);

        MultiLabelClfDataSet trainSet = TRECFormat.loadMultiLabelClfDataSet(config.getString("input.trainData"),
                DataSetType.ML_CLF_SPARSE, true);
        MultiLabelClfDataSet testSet = TRECFormat.loadMultiLabelClfDataSet(config.getString("input.testData"),
                DataSetType.ML_CLF_SPARSE, true);

        int numClusters = config.getInt("numClusters");
        double softmaxVariance = config.getDouble("softmaxVariance");
        double logitVariance = config.getDouble("logitVariance");
        int numIterations = config.getInt("numIterations");
        int numSamples = config.getInt("numSamples");

        String output = config.getString("output");
        String modelName = config.getString("modelName");

        BMMClassifier bmmClassifier;
        if (config.getBoolean("train.warmStart")) {
            bmmClassifier = BMMClassifier.deserialize(new File(output, modelName));
        } else {
            bmmClassifier = new BMMClassifier(trainSet.getNumClasses(),numClusters,trainSet.getNumFeatures());
            BMMOptimizer optimizer = new BMMOptimizer(bmmClassifier, trainSet,softmaxVariance,logitVariance);
            bmmClassifier.setNumSample(numSamples);

            System.out.print("random init" + "\t" );
            System.out.print("objective: "+optimizer.getObjective()+ "\t");
            System.out.print("trainAcc : "+ Accuracy.accuracy(bmmClassifier, trainSet) + "\t");
            System.out.print("trainOver: "+ Overlap.overlap(bmmClassifier, trainSet) + "\t");
            System.out.print("testACC  : "+ Accuracy.accuracy(bmmClassifier,testSet) + "\t");
            System.out.println("testOver : "+ Overlap.overlap(bmmClassifier, testSet) + "\t");

            for (int i=1;i<=numIterations;i++){
                optimizer.iterate();
                System.out.print("iter : "+i + "\t");
                System.out.print("objective: "+optimizer.getTerminator().getLastValue() + "\t");
                System.out.print("trainAcc : "+ Accuracy.accuracy(bmmClassifier,trainSet)+ "\t");
                System.out.print("trainOver: "+ Overlap.overlap(bmmClassifier, trainSet)+ "\t");
                System.out.print("testAcc  : "+ Accuracy.accuracy(bmmClassifier,testSet)+ "\t");
                System.out.println("testOver : "+ Overlap.overlap(bmmClassifier, testSet)+ "\t");
            }
            System.out.println("history = "+optimizer.getTerminator().getHistory());
        }

        System.out.println("--------------------------------Results-----------------------------\n");
        System.out.println();
        System.out.print("trainAcc : " + Accuracy.accuracy(bmmClassifier, trainSet) + "\t");
        System.out.print("trainOver: "+ Overlap.overlap(bmmClassifier, trainSet)+ "\t");
        System.out.print("testAcc  : "+ Accuracy.accuracy(bmmClassifier,testSet)+ "\t");
        System.out.println("testOver : "+ Overlap.overlap(bmmClassifier, testSet)+ "\t");
        System.out.println();
        System.out.println();
        System.out.println(bmmClassifier);

        if (config.getBoolean("saveModel")) {
            File serializeModel = new File(output, modelName);
            bmmClassifier.serialize(serializeModel);
        }
    }
}
