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
 * AbstractAveragedEvaluationScheme.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package moaflow.transformer.evaluate;

import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Instance;
import moa.core.Example;
import moa.core.Measurement;
import moa.evaluation.LearningEvaluation;
import moa.evaluation.LearningPerformanceEvaluator;
import moa.learners.Learner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Base class for evaluation schemes which evaluate a number of
 * cross-validated copies of a learner.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public abstract class AbstractAveragedEvaluationScheme
  extends AbstractEvaluationScheme {

  /** Option to specify the number of CV folds to evaluate. */
  public IntOption numFoldsOption = new IntOption("numFolds", 'w',
    "The number of folds (e.g. distributed models) to be used.", 10, 1, Integer.MAX_VALUE);

  /** The learners being evaluated for each fold. */
  protected Learner[] m_Learners;

  /** The evaluator for each fold. */
  protected LearningPerformanceEvaluator[] m_Evaluators;

  /**
   * Buffer of instances to use for training with delayed algorithm
   */
  protected LinkedList<LinkedList<Example>> trainInstances;
  /**
   * Gets the number of folds to evaluate.
   */
  public int numFolds() {
    return numFoldsOption.getValue();
  }

  /**
   * Initialises the evaluation scheme with the base learner to
   * evaluate and the evaluator to use.
   *
   * @param baseLearner		The learner to evaluate.
   * @param baseEvaluator	The evaluator to use.
   */
  @Override
  public void initialise(Learner<Example<Instance>> baseLearner, LearningPerformanceEvaluator<Example<Instance>> baseEvaluator) {
    super.initialise(baseLearner, baseEvaluator);

    // Create the fold learners and evaluators
    m_Learners = new Learner[numFolds()];
    m_Evaluators = new LearningPerformanceEvaluator[numFolds()];
    trainInstances = new LinkedList<LinkedList<Example>>();
    for (int i = 0; i < numFolds(); i++) {
      trainInstances.add(new LinkedList<Example>());
      m_Learners[i] = (Learner) m_BaseLearner.copy();
      m_Learners[i].setModelContext(m_BaseLearner.getModelContext());
      m_Evaluators[i] = (LearningPerformanceEvaluator) m_BaseEvaluator.copy();
    }
  }

  /**
   * Evaluates the given instance using this scheme.
   *
   * @param instance	The instance to evaluate.
   */
  @Override
  public void performEvaluation(Example<Instance> instance) {
    // Perform the single evaluation scheme on each learner/evaluator pair
    for (int i = 0; i < numFolds(); i++){
      performSingleEvaluation(i, instance, m_Learners[i], m_Evaluators[i]);
    }
  }

  /**
   * Evaluates a single repetition of this averaged evaluation scheme.
   *
   * @param instance	The instance to evaluate.
   * @param learner	The repetition learner.
   * @param evaluator	The repetition evaluator.
   */
  public abstract void performSingleEvaluation(int repetition,
					       Example<Instance> instance,
					       Learner<Example<Instance>> learner,
					       LearningPerformanceEvaluator<Example<Instance>> evaluator);

  /**
   * Gets the current status of the evaluation.
   *
   * @return	The learning evaluation for the current state.
   */
  @Override
  public LearningEvaluation getEvaluation() {
    return new LearningEvaluation(
      getEvaluationMeasurements(new Measurement[]{instancesProcessedMeasurement()})
    );
  }

  /**
   * Get the evaluation measurements for this scheme.
   *
   * @param nonEvaluatorMeasurements	Any measurements no based on the fold evaluators.
   * @return			The measurements.
   */
  public Measurement[] getEvaluationMeasurements(Measurement[] nonEvaluatorMeasurements) {
    // Create a master list of measurements
    List<Measurement> measurementList = new LinkedList<>();

    // Add the given measurements
    if (nonEvaluatorMeasurements != null) {
      measurementList.addAll(Arrays.asList(nonEvaluatorMeasurements));
    }

    // Add the averaged measurements of the individual evaluators
    measurementList.addAll(Arrays.asList(getAverageMeasurements()));

    return measurementList.toArray(new Measurement[0]);
  }

  /**
   * Get the average of the measurements across all of the folds.
   *
   * @return	The averaged measurements.
   */
  public Measurement[] getAverageMeasurements() {
    // Get the measurements for each evaluator
    List<Measurement[]> evaluatorMeasurements = new LinkedList<>();
    for (LearningPerformanceEvaluator evaluator : m_Evaluators)
      evaluatorMeasurements.add(evaluator.getPerformanceMeasurements());

    // Return the average
    return Measurement.averageMeasurements(
      evaluatorMeasurements.toArray(
        new Measurement[0][]
      )
    );
  }
}
