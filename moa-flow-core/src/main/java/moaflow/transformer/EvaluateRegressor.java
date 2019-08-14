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

import moaflow.core.Utils;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import com.yahoo.labs.samoa.instances.Prediction;
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
  extends AbstractTransformer<Example<Instance>, RegressionPerformanceEvaluator> {

  public ClassOption regressor = new ClassOption("regressor", 'c', "The regressor to use", Regressor.class, SGD.class.getName());

  public ClassOption evaluator = new ClassOption("evaluator", 'e', "The evaluator to use", RegressionPerformanceEvaluator.class, BasicRegressionPerformanceEvaluator.class.getName());

  public IntOption everyNth = new IntOption("everyNth", 'n', "Every n-th evalutation the regressor will get forwarded", 1000, 1, Integer.MAX_VALUE);

  /** whether this is the first evaluation. */
  protected boolean first;

  /** for counting the evaluations. */
  protected int counter;

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
    first = true;
    counter = 0;
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

  protected RegressionPerformanceEvaluator doProcess(Example<Instance> input) {
    if (first) {
      actualRegressor = (Regressor) regressor.getPreMaterializedObject();
      ((Classifier) actualRegressor).setModelContext(new InstancesHeader(input.getData().dataset()));
      ((Classifier) actualRegressor).prepareForUse();
      actualEvaluator = (RegressionPerformanceEvaluator) evaluator.getPreMaterializedObject();
      first = false;
      counter = 0;
    }
    counter++;
    Prediction pred = ((Classifier) actualRegressor).getPredictionForInstance(input.getData());
    actualEvaluator.addResult(input, pred);
    ((Classifier) actualRegressor).trainOnInstance(input.getData());
    if (counter == everyNth.getValue()) {
      counter = 0;
      return actualEvaluator;
    }
    else {
      return null;
    }
  }
}
