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
 * TrainRegressor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.moaflow.transformer;

import com.github.fracpete.moaflow.core.Utils;
import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.Classifier;
import moa.classifiers.Regressor;
import moa.classifiers.functions.SGD;
import moa.core.Example;

/**
 * Trains a regressor and forwards it.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TrainRegressor
  extends AbstractTransformer<Example<Instance>, Regressor> {

  protected Regressor regressor;

  protected int everyNth;

  protected int counter;

  public TrainRegressor() {
    setRegressor(new SGD());
    setEveryNth(1);
  }

  public void setRegressor(Regressor value) {
    regressor = value;
    ((Classifier) regressor).resetLearning();
    ((Classifier) regressor).prepareForUse();
  }

  public void setRegressor(String value) {
    setRegressor(Utils.fromCommandLine(Regressor.class, value));
  }

  public Regressor getRegressor() {
    return regressor;
  }

  public void setEveryNth(int value) {
    everyNth = value;
    counter = 0;
  }

  public int getEveryNth() {
    return everyNth;
  }

  protected Regressor doProcess(Example<Instance> input) {
    counter++;
    synchronized (regressor) {
      ((Classifier) regressor).trainOnInstance(input.getData());
    }
    if (counter == everyNth) {
      counter = 0;
      return regressor;
    }
    else {
      return null;
    }
  }
}
