/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Prequential.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package moaflow.transformer.evaluate;

import com.github.javacliparser.FlagOption;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Instance;
import moa.core.Example;
import moa.core.InstanceExample;
import moa.evaluation.LearningPerformanceEvaluator;
import moa.learners.Learner;

import java.util.LinkedList;
import java.util.Random;

/**
 * Performs single-learner delayed prequential evaluation.
 *
 * @author Truong To (todinhtruong at gmail dot com)
 */
public class PrequentialDelayed
        extends AbstractSingleEvaluationScheme {

    public IntOption delayLengthOption = new IntOption("delay", 'k',
            "Number of instances before test instance is used for training",
            1000, 1, Integer.MAX_VALUE);

    public IntOption initialWindowSizeOption = new IntOption("initialTrainingWindow", 'p',
            "Number of instances used for training in the beginning of the stream.",
            1000, 0, Integer.MAX_VALUE);

    public FlagOption trainOnInitialWindowOption = new FlagOption("trainOnInitialWindow", 'm',
            "Whether to train or not using instances in the initial window.");

    public FlagOption trainInBatches = new FlagOption("trainInBatches", 'b',
            "If set training will not be interleaved with testing. ");

    /**
     * for counting the evaluations.
     */
    protected int counter;

    /**
     * Buffer of instances to use for training.
     */
    protected LinkedList<Example> trainInstances;

    /**
     * Gets the purpose of this object
     *
     * @return the string with the purpose of this object
     */
    @Override
    public String getPurposeString() {
        return "Evaluation scheme performing prequential evaluation.";
    }

    /**
     * Evaluates the given instance using this scheme.
     *
     * @param instance The instance to evaluate.
     */
    @Override
    public void initialise(Learner<Example<Instance>> baseLearner,
                           LearningPerformanceEvaluator<Example<Instance>> baseEvaluator) {
        super.initialise(baseLearner, baseEvaluator);

        counter = 0;
        trainInstances = new LinkedList<Example>();
    }

    @Override
    public void performEvaluation(Example<Instance> instance) {
        counter++;
        if (counter <= initialWindowSizeOption.getValue()) {
            if (trainOnInitialWindowOption.isSet()) {
                m_BaseLearner.trainOnInstance(instance);
            } else if ((initialWindowSizeOption.getValue() - counter) < delayLengthOption.getValue()) {
                trainInstances.addLast(instance);
            }
        } else {
            trainInstances.addLast(instance);

            if (delayLengthOption.getValue() < trainInstances.size()) {
                if (trainInBatches.isSet()) {
                    // Do not train on the latest instance, otherwise
                    // it would train on k+1 instances
                    while (trainInstances.size() > 1) {
                        Example trainInst = trainInstances.removeFirst();
                        m_BaseLearner.trainOnInstance(trainInst);
                    }
                } else {
                    Example trainInst = trainInstances.removeFirst();
                    m_BaseLearner.trainOnInstance(trainInst);
                }
            }

            // Remove class label from test instances.
            Instance testInstance = ((Instance) instance.getData()).copy();
            Example testInst = new InstanceExample(testInstance);
            testInstance.setMissing(testInstance.classAttribute());
            testInstance.setClassValue(0.0);

            if (instance.getData().classAttribute().isNominal())
                m_BaseEvaluator.addResult(instance, m_BaseLearner.getVotesForInstance(instance));
            else
                m_BaseEvaluator.addResult(instance, m_BaseLearner.getPredictionForInstance(instance));
        }
    }
}
