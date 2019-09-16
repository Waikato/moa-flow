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
 * EvaluateRegressor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package moaflow.transformer;

import moa.evaluation.LearningPerformanceEvaluator;
import moa.learners.Learner;
import moaflow.core.Utils;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import moa.classifiers.Classifier;
import moa.classifiers.Regressor;
import moa.classifiers.functions.SGD;
import moa.core.Example;
import moa.evaluation.BasicRegressionPerformanceEvaluator;
import moa.evaluation.RegressionPerformanceEvaluator;
import moa.options.ClassOption;

/**
 * Evaluates a regressor and forwards the evaluation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class EvaluateRegressor
  extends AbstractEvaluate {

  public ClassOption regressor = new ClassOption("regressor", 'c', "The regressor to use", Regressor.class, SGD.class.getName());

  public ClassOption evaluator = new ClassOption("evaluator", 'e', "The evaluator to use", RegressionPerformanceEvaluator.class, BasicRegressionPerformanceEvaluator.class.getName());

  /** the actual regressor. */
  protected transient Regressor actualRegressor;

  /** the actual evaluator. */
  protected transient RegressionPerformanceEvaluator actualEvaluator;

  /**
   * Gets the purpose of this object
   *
   * @return the string with the purpose of this object
   */
  @Override
  public String getPurposeString() {
    return "Evaluates the specified regressor and forwards the evaluation every n-th evaluation.";
  }

  /**
   * For initializing members.
   */
  @Override
  protected void init() {
    super.init();
    actualRegressor = null;
    actualEvaluator= null;
  }

  /**
   * Sets the regressor via a commandline string.
   *
   * @param value the commandline
   */
  public void setRegressor(String value) {
    regressor.setCurrentObject(Utils.fromCommandLine(Regressor.class, value));
  }

  /**
   * Sets the evaluator via a commandline string.
   *
   * @param value the commandline
   */
  public void setEvaluator(String value) {
    evaluator.setCurrentObject(Utils.fromCommandLine(RegressionPerformanceEvaluator.class, value));
  }

  @Override
  protected Learner<Example<Instance>> getLearner(InstancesHeader header) {
    if (actualRegressor == null) {
      actualRegressor = (Regressor) regressor.getPreMaterializedObject();
      ((Classifier) actualRegressor).setModelContext(header);
      ((Classifier) actualRegressor).prepareForUse();
    }

    return (Classifier) actualRegressor;
  }

  /**
   * Get the evaluator to use.
   *
   * @return	The initialised evaluator.
   */
  @Override
  protected LearningPerformanceEvaluator<Example<Instance>> getEvaluator() {
    if (actualEvaluator == null) {
      actualEvaluator = (RegressionPerformanceEvaluator) evaluator.getPreMaterializedObject();
    }

    return actualEvaluator;
  }
}
