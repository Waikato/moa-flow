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
 * AbstractSingleEvaluationScheme.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package moaflow.transformer.evaluate;

import moa.core.Measurement;
import moa.evaluation.LearningEvaluation;

/**
 * Base class for evaluation schemes which evaluate a single learner.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public abstract class AbstractSingleEvaluationScheme
  extends AbstractEvaluationScheme {

  /**
   * Gets the current status of the evaluation.
   *
   * @return	The learning evaluation for the current state.
   */
  @Override
  public LearningEvaluation getEvaluation() {
    return new LearningEvaluation(
      new Measurement[]{instancesProcessedMeasurement()},
      m_BaseEvaluator,
      m_BaseLearner);
  }
}
