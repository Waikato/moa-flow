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

package moaflow.transformer;

import moaflow.core.Utils;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import moa.classifiers.Classifier;
import moa.classifiers.Regressor;
import moa.classifiers.functions.SGD;
import moa.core.Example;
import moa.options.ClassOption;

/**
 * Trains a regressor and forwards it.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TrainRegressor
  extends AbstractTransformer<Example<Instance>, Regressor> {

  public ClassOption regressor = new ClassOption("regressor", 'c', "The regressor to use", Regressor.class, SGD.class.getName());

  public IntOption everyNth = new IntOption("everyNth", 'n', "Every n-th training step the regressor will get forwarded", 1000, 1, Integer.MAX_VALUE);

  /** the counter for the training steps. */
  protected int counter;

  /** the actual regressor. */
  protected transient Regressor actualRegressor;

  /**
   * Gets the purpose of this object
   *
   * @return the string with the purpose of this object
   */
  @Override
  public String getPurposeString() {
    return "Trains the regressor with every incoming instance and forwards the model every n-th training step.";
  }

  /**
   * For initializing members.
   */
  @Override
  protected void init() {
    super.init();
    counter = 0;
    actualRegressor = null;
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
   * Transforms the input data.
   *
   * @param input the input data
   * @return the generated output data
   */
  protected Regressor doProcess(Example<Instance> input) {
    if (actualRegressor == null) {
      actualRegressor = (Regressor) regressor.getPreMaterializedObject();
      ((Classifier) actualRegressor).setModelContext(new InstancesHeader(input.getData().dataset()));
      ((Classifier) actualRegressor).prepareForUse();
    }

    counter++;
    synchronized (actualRegressor) {
      ((Classifier) actualRegressor).trainOnInstance(input.getData());
    }

    if (counter == everyNth.getValue()) {
      counter = 0;
      return actualRegressor;
    }
    else {
      return null;
    }
  }
}
