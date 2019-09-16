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

import moa.evaluation.LearningPerformanceEvaluator;
import moa.learners.Learner;
import moaflow.core.Utils;
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
  extends AbstractEvaluate {

  public ClassOption classifier = new ClassOption("classifier", 'c', "The classifier to use", Classifier.class, HoeffdingTree.class.getName());

  public ClassOption evaluator = new ClassOption("evaluator", 'e', "The evaluator to use", ClassificationPerformanceEvaluator.class, BasicClassificationPerformanceEvaluator.class.getName());

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

  /**
   * Gets the learner to evaluate.
   *
   * @return	The initialised learner.
   */
  protected Learner<Example<Instance>> getLearner(InstancesHeader header) {
    if (actualClassifier == null) {
      actualClassifier = (Classifier) classifier.getPreMaterializedObject();
      actualClassifier.setModelContext(header);
      actualClassifier.prepareForUse();
    }

    return actualClassifier;
  }

  /**
   * Get the evaluator to use.
   *
   * @return	The initialised evaluator.
   */
  protected LearningPerformanceEvaluator<Example<Instance>> getEvaluator() {
    if (actualEvaluator == null) {
      actualEvaluator = (ClassificationPerformanceEvaluator) evaluator.getPreMaterializedObject();
    }

    return actualEvaluator;
  }

}
