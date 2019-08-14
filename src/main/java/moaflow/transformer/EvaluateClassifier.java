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

package moaflow.transformer;

import moaflow.core.Utils;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import moa.classifiers.Classifier;
import moa.classifiers.trees.HoeffdingTree;
import moa.core.Example;
import moa.evaluation.BasicClassificationPerformanceEvaluator;
import moa.evaluation.ClassificationPerformanceEvaluator;
import moa.options.ClassOption;

/**
 * Evaluates a classifier and forwards the evaluation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class EvaluateClassifier
  extends AbstractTransformer<Example<Instance>, ClassificationPerformanceEvaluator> {

  public ClassOption classifier = new ClassOption("classifier", 'c', "The classifier to use", Classifier.class, HoeffdingTree.class.getName());

  public ClassOption evaluator = new ClassOption("evaluator", 'e', "The evaluator to use", ClassificationPerformanceEvaluator.class, BasicClassificationPerformanceEvaluator.class.getName());

  public IntOption everyNth = new IntOption("everyNth", 'n', "Every n-th evalutation the classifier will get forwarded", 1000, 1, Integer.MAX_VALUE);

  /** whether this is the first evaluation. */
  protected boolean first;

  /** for counting the evaluations. */
  protected int counter;

  /** the actual classifier. */
  protected transient Classifier actualClassifier;

  /** the actual evaluator. */
  protected transient ClassificationPerformanceEvaluator actualEvaluator;

  /**
   * Gets the purpose of this object
   *
   * @return the string with the purpose of this object
   */
  @Override
  public String getPurposeString() {
    return "Evaluates the specified classifier and forwards the evaluation every n-th evaluation.";
  }

  /**
   * For initializing members.
   */
  @Override
  protected void init() {
    super.init();
    first = true;
    counter = 0;
    actualClassifier = null;
    actualEvaluator = null;
  }

  /**
   * Sets the classifier via a commandline string.
   *
   * @param value the commandline
   */
  public void setClassifier(String value) {
    classifier.setCurrentObject(Utils.fromCommandLine(Classifier.class, value));
  }

  /**
   * Sets the evaluator via a commandline string.
   *
   * @param value the commandline
   */
  public void setEvaluator(String value) {
    evaluator.setCurrentObject(Utils.fromCommandLine(ClassificationPerformanceEvaluator.class, value));
  }

  protected ClassificationPerformanceEvaluator doProcess(Example<Instance> input) {
    if (first) {
      actualClassifier = (Classifier) classifier.getPreMaterializedObject();
      actualClassifier.setModelContext(new InstancesHeader(input.getData().dataset()));
      actualClassifier.prepareForUse();
      actualEvaluator = (ClassificationPerformanceEvaluator) evaluator.getPreMaterializedObject();
      first = false;
      counter = 0;
    }
    counter++;
    double[] votes = actualClassifier.getVotesForInstance(input.getData());
    actualEvaluator.addResult(input, votes);
    actualClassifier.trainOnInstance(input.getData());
    if (counter == everyNth.getValue()) {
      counter = 0;
      return actualEvaluator;
    }
    else {
      return null;
    }
  }
}
