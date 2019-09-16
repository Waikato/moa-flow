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
 * AbstractEvaluationScheme.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package moaflow.transformer.evaluate;

import com.yahoo.labs.samoa.instances.Instance;
import moa.core.Example;
import moa.core.Measurement;
import moa.core.ObjectRepository;
import moa.evaluation.LearningPerformanceEvaluator;
import moa.learners.Learner;
import moa.options.AbstractOptionHandler;
import moa.tasks.TaskMonitor;
import moaflow.core.Utils;

/**
 * Provides base functionality for evaluation schemes.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public abstract class AbstractEvaluationScheme
  extends AbstractOptionHandler
  implements EvaluationScheme {

  /** Number of instances processed so far by the scheme. */
  protected int m_NumInstancesProcessed = 0;

  /** The base learner being evaluated. */
  protected Learner<Example<Instance>> m_BaseLearner;

  /** The base evaluator doing the evaluating. */
  protected LearningPerformanceEvaluator<Example<Instance>> m_BaseEvaluator;

  /**
   * Initialises the evaluation scheme with the base learner to
   * evaluate and the evaluator to use.
   *
   * @param baseLearner		The learner to evaluate.
   * @param baseEvaluator	The evaluator to use.
   */
  @Override
  public void initialise(Learner<Example<Instance>> baseLearner,
			 LearningPerformanceEvaluator<Example<Instance>> baseEvaluator) {
    m_NumInstancesProcessed = 0;
    m_BaseLearner = baseLearner;
    m_BaseEvaluator = baseEvaluator;
  }

  /**
   * Evaluates the given instance using this scheme.
   *
   * @param instance	The instance to evaluate.
   */
  @Override
  public void evaluate(Example<Instance> instance) {
    performEvaluation(instance);
    m_NumInstancesProcessed++;
  }

  /**
   * Evaluates the given instance using this scheme.
   *
   * @param instance	The instance to evaluate.
   */
  public abstract void performEvaluation(Example<Instance> instance);

  /**
   * Gets the number of instances processed by this scheme.
   *
   * @return  The number of instances.
   */
  @Override
  public int numInstancesProcessed() {
    return m_NumInstancesProcessed;
  }

  /**
   * Gets a measurement specifying the number of instances processed
   * so far.
   *
   * @return	The measurement.
   */
  public Measurement instancesProcessedMeasurement() {
    return new Measurement(
      "learning evaluation instances",
      m_NumInstancesProcessed);
  }

  /**
   * Does nothing.
   */
  @Override
  protected void prepareForUseImpl(TaskMonitor monitor, ObjectRepository repository) {
  }

  /**
   * Appends the commandline of this operator.
   *
   * @param sb the buffer to append it to
   * @param indent the indentation
   */
  @Override
  public void getDescription(StringBuilder sb, int indent) {
    for (int i = 0; i < indent; i++)
      sb.append(" ");
    sb.append(Utils.toCommandLine(this));
  }

  /**
   * Gets the purpose of this object
   *
   * @return the string with the purpose of this object
   */
  @Override
  public abstract String getPurposeString();
}
