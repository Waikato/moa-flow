package moaflow.examples;

import moaflow.transformer.EvaluateClassifier;
import moaflow.core.Utils;
import moaflow.sink.OutputLearningCurve;
import moaflow.sink.MeasurementTableSawPlot;
import moaflow.source.InstanceSource;

/**
 * Example flow for classification with various evaluation schemas,
 * print out the learning curve in table format and plots in a realtime chart.
 * The chart can be plotted just on web-browser with Jupyter Notebook and IJava
 * @author Truong To (todinhtruong at gmail dot com)
 */
public class ClassificationVariousSchemaTablePlot {
    public static void main(String[] args) throws Exception {
        String learnerString = "meta.AdaptiveRandomForest -l (ARFHoeffdingTree -e 2000000 -g 75 -s GiniSplitCriterion -c 0.01 -l MC) -o (Percentage (M * (m / 100))) -m 80";
        String streamString = "generators.RandomTreeGenerator";
        String evaluatorString = "BasicClassificationPerformanceEvaluator";

        InstanceSource source;
        source = new InstanceSource();
        source.setGenerator(streamString);
        source.numInstances.setValue(100000);

        EvaluateClassifier eval = new EvaluateClassifier();
        eval.everyNth.setValue(10000);
        eval.setClassifier(learnerString);
        eval.setEvaluator(evaluatorString);
        eval.setEvaluationScheme("Prequential");
        //eval.setEvaluationScheme("PrequentialCV");
        //eval.setEvaluationScheme("PrequentialDelayed");
        //eval.setEvaluationScheme("PrequentialDelayedCV");
        source.subscribe(eval);

        MeasurementTableSawPlot plot = new MeasurementTableSawPlot();
        plot.measurement.setValue("[avg] classifications correct (percent)");
        plot.maxPoints.setValue(-1);
        eval.subscribe(plot);

        OutputLearningCurve curve = new OutputLearningCurve();
        eval.subscribe(curve);

        System.out.println(Utils.toTree(source));

        source.start();
    }
}
