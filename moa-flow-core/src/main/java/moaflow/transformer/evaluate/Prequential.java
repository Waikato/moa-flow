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

import com.yahoo.labs.samoa.instances.Instance;
import moa.core.Example;

/**
 * Performs single-learner prequential evaluation.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class Prequential
  extends AbstractSingleEvaluationScheme {

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
   * @param instance	The instance to evaluate.
   */
  @Override
  public void performEvaluation(Example<Instance> instance) {
    // Evaluate first
    if (instance.getData().classAttribute().isNominal())
      m_BaseEvaluator.addResult(instance, m_BaseLearner.getVotesForInstance(instance));
    else
      m_BaseEvaluator.addResult(instance, m_BaseLearner.getPredictionForInstance(instance));

    // Train second
    m_BaseLearner.trainOnInstance(instance);
  }
}
