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
 * EvaluateClassifier.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.moaflow.transformer;

import com.github.fracpete.moaflow.core.Utils;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import moa.classifiers.Classifier;
import moa.classifiers.trees.HoeffdingTree;
import moa.core.Example;
import moa.evaluation.BasicClassificationPerformanceEvaluator;
import moa.evaluation.ClassificationPerformanceEvaluator;

/**
 * Evaluates a classifier and forwards the evaluation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class EvaluateClassifier
  extends AbstractTransformer<Example<Instance>, ClassificationPerformanceEvaluator> {

  protected Classifier classifier;

  protected ClassificationPerformanceEvaluator evaluator;

  protected boolean first;

  protected int everyNth;

  protected int counter;

  public EvaluateClassifier() {
    setClassifier(new HoeffdingTree());
    setEvaluator(new BasicClassificationPerformanceEvaluator());
    setEveryNth(1);
  }

  public void setClassifier(Classifier value) {
    classifier = value;
    first = true;
  }

  public void setClassifier(String value) {
    setClassifier(Utils.fromCommandLine(Classifier.class, value));
  }

  public Classifier getClassifier() {
    return classifier;
  }

  public void setEvaluator(ClassificationPerformanceEvaluator value) {
    evaluator = value;
    first = true;
  }

  public void setEvaluator(String value) {
    setEvaluator(Utils.fromCommandLine(ClassificationPerformanceEvaluator.class, value));
  }

  public ClassificationPerformanceEvaluator getEvaluator() {
    return evaluator;
  }

  public void setEveryNth(int value) {
    everyNth = value;
    first = true;
  }

  public int getEveryNth() {
    return everyNth;
  }

  protected ClassificationPerformanceEvaluator doProcess(Example<Instance> input) {
    if (first) {
      classifier.setModelContext(new InstancesHeader(input.getData().dataset()));
      classifier.prepareForUse();
      first = false;
      counter = 0;
    }
    counter++;
    double[] votes = classifier.getVotesForInstance(input.getData());
    evaluator.addResult(input, votes);
    if (counter == everyNth) {
      counter = 0;
      return evaluator;
    }
    else {
      return null;
    }
  }
}
