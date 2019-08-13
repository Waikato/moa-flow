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
 * TrainClassifier.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.moaflow.transformer;

import com.github.fracpete.moaflow.core.Utils;
import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.Classifier;
import moa.classifiers.trees.HoeffdingTree;
import moa.core.Example;

/**
 * Trains a classifier and forwards it.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TrainClassifier
  extends AbstractTransformer<Example<Instance>, Classifier> {

  protected Classifier classifier;

  protected int everyNth;

  protected int counter;

  public TrainClassifier() {
    setClassifier(new HoeffdingTree());
    setEveryNth(1);
  }

  public void setClassifier(Classifier value) {
    classifier = value;
    classifier.resetLearning();
    classifier.prepareForUse();
  }

  public void setClassifier(String value) {
    setClassifier(Utils.fromCommandLine(Classifier.class, value));
  }

  public Classifier getClassifier() {
    return classifier;
  }

  public void setEveryNth(int value) {
    everyNth = value;
    counter = 0;
  }

  public int getEveryNth() {
    return everyNth;
  }

  protected Classifier doProcess(Example<Instance> input) {
    counter++;
    synchronized (classifier) {
      classifier.trainOnInstance(input.getData());
    }
    if (counter == everyNth) {
      counter = 0;
      return classifier;
    }
    else {
      return null;
    }
  }
}
