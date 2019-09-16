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
 * EvaluationScheme.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package moaflow.transformer.evaluate;

import com.yahoo.labs.samoa.instances.Instance;
import moa.core.Example;
import moa.evaluation.LearningEvaluation;
import moa.evaluation.LearningPerformanceEvaluator;
import moa.learners.Learner;

/**
 * Evaluation schemes define how to evaluate a learner.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public interface EvaluationScheme {

  /**
   * Initialises the evaluation scheme with the base learner to
   * evaluate and the evaluator to use.
   *
   * @param baseLearner		The learner to evaluate.
   * @param baseEvaluator	The evaluator to use.
   */
  void initialise(Learner<Example<Instance>> baseLearner,
		  LearningPerformanceEvaluator<Example<Instance>> baseEvaluator);

  /**
   * Evaluates the given instance using this scheme.
   *
   * @param instance	The instance to evaluate.
   */
  void evaluate(Example<Instance> instance);

  /**
   * Gets the number of instances processed by this scheme.
   *
   * @return  The number of instances.
   */
  int numInstancesProcessed();

  /**
   * Gets the current status of the evaluation.
   *
   * @return	The learning evaluation for the current state.
   */
  LearningEvaluation getEvaluation();

}
