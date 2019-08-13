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

package com.github.fracpete.moaflow.transformer;

import com.github.fracpete.moaflow.core.Utils;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import com.yahoo.labs.samoa.instances.Prediction;
import moa.classifiers.Classifier;
import moa.classifiers.Regressor;
import moa.classifiers.functions.SGD;
import moa.core.Example;
import moa.evaluation.BasicRegressionPerformanceEvaluator;
import moa.evaluation.RegressionPerformanceEvaluator;

/**
 * Evaluates a regressor and forwards the evaluation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class EvaluateRegressor
  extends AbstractTransformer<Example<Instance>, RegressionPerformanceEvaluator> {

  protected Regressor regressor;

  protected RegressionPerformanceEvaluator evaluator;

  protected boolean first;

  protected int everyNth;

  protected int counter;

  public EvaluateRegressor() {
    setRegressor(new SGD());
    setEvaluator(new BasicRegressionPerformanceEvaluator());
    setEveryNth(1);
  }

  public void setRegressor(Regressor value) {
    regressor = value;
    first = true;
  }

  public void setRegressor(String value) {
    setRegressor(Utils.fromCommandLine(Regressor.class, value));
  }

  public Regressor getRegressor() {
    return regressor;
  }

  public void setEvaluator(RegressionPerformanceEvaluator value) {
    evaluator = value;
    first = true;
  }

  public void setEvaluator(String value) {
    setEvaluator(Utils.fromCommandLine(RegressionPerformanceEvaluator.class, value));
  }

  public RegressionPerformanceEvaluator getEvaluator() {
    return evaluator;
  }

  public void setEveryNth(int value) {
    everyNth = value;
    first = true;
  }

  public int getEveryNth() {
    return everyNth;
  }

  protected RegressionPerformanceEvaluator doProcess(Example<Instance> input) {
    if (first) {
      ((Classifier) regressor).setModelContext(new InstancesHeader(input.getData().dataset()));
      ((Classifier) regressor).prepareForUse();
      first = false;
      counter = 0;
    }
    counter++;
    Prediction pred = ((Classifier) regressor).getPredictionForInstance(input.getData());
    evaluator.addResult(input, pred);
    if (counter == everyNth) {
      counter = 0;
      return evaluator;
    }
    else {
      return null;
    }
  }
}
