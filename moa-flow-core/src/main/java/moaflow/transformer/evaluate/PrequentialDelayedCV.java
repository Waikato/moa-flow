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
 * PrequentialCV.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package moaflow.transformer.evaluate;

import com.github.javacliparser.IntOption;
import com.github.javacliparser.MultiChoiceOption;
import com.yahoo.labs.samoa.instances.Instance;
import moa.core.Example;
import moa.core.MiscUtils;
import moa.evaluation.LearningPerformanceEvaluator;
import moa.learners.Learner;

import java.util.LinkedList;
import java.util.Random;

/**
 * Performs delayed cross-validated prequential evaluation of a learner.
 *
 * @author Truong To (todinhtruong at gmail dot com)
 */
public class PrequentialDelayedCV
        extends AbstractAveragedEvaluationScheme {

    /**
     * Option to specify the validation methodology to use.
     */
    public MultiChoiceOption validationMethodologyOption = new MultiChoiceOption(
            "validationMethodology",
            'a',
            "Validation methodology to use.",
            new String[]{"Cross-Validation", "Bootstrap-Validation", "Split-Validation"},
            new String[]{
                    "k-fold distributed Cross Validation",
                    "k-fold distributed Bootstrap Validation",
                    "k-fold distributed Split Validation"
            },
            0);

    /**
     * Option to specify the random seed to use.
     */
    public IntOption randomSeedOption = new IntOption(
            "randomSeed",
            'r',
            "Seed for random behaviour of the task.",
            1);

    /**
     * Option to specify the delayed length.
     */
    public IntOption delayLengthOption = new IntOption("delay", 'k',
            "Number of instances before test instance is used for training",
            1000, 1, Integer.MAX_VALUE);

    /**
     * The RNG.
     */
    protected Random m_Random;

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
     * Initialises the evaluation scheme with the base learner to
     * evaluate and the evaluator to use.
     *
     * @param baseLearner   The learner to evaluate.
     * @param baseEvaluator The evaluator to use.
     */
    @Override
    public void initialise(Learner<Example<Instance>> baseLearner,
                           LearningPerformanceEvaluator<Example<Instance>> baseEvaluator) {
        super.initialise(baseLearner, baseEvaluator);

        m_Random = new Random(randomSeedOption.getValue());
    }

    /**
     * Evaluates a single repetition of this averaged evaluation scheme.
     *
     * @param instance  The instance to evaluate.
     * @param learner   The repetition learner.
     * @param evaluator The repetition evaluator.
     */
    @Override
    public void performSingleEvaluation(int repetition,
                                        Example<Instance> instance,
                                        Learner<Example<Instance>> learner,
                                        LearningPerformanceEvaluator<Example<Instance>> evaluator) {
        // Prequential evaluation
        if (instance.getData().classAttribute().isNominal())
            evaluator.addResult(instance, learner.getVotesForInstance(instance));
        else
            evaluator.addResult(instance, learner.getPredictionForInstance(instance));

        // Validation methodology weight selection
        int k = 1;
        switch (validationMethodologyOption.getChosenIndex()) {
            case 0: //Cross-Validation;
                k = (m_NumInstancesProcessed + 1) % numFolds() == repetition ? 0 : 1; //Test all except one
                break;
            case 1: //Bootstrap;
                k = MiscUtils.poisson(1, m_Random);
                break;
            case 2: //Split-Validation;
                k = (m_NumInstancesProcessed + 1) % numFolds() == repetition ? 1 : 0; //Test only one
                break;
        }

        if (k > 0) {
            trainInstances.get(repetition).addLast(instance);
        }
        if (delayLengthOption.getValue() < trainInstances.get(repetition).size()) {
            Example trainInstI = trainInstances.get(repetition).removeFirst();
            learner.trainOnInstance(trainInstI);
        }


    }
}
