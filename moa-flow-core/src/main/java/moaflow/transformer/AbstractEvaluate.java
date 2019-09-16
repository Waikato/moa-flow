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
 * AbstractEvaluate.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package moaflow.transformer;

import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import moa.core.Example;
import moa.evaluation.LearningEvaluation;
import moa.evaluation.LearningPerformanceEvaluator;
import moa.learners.Learner;
import moa.options.ClassOption;
import moaflow.core.Utils;
import moaflow.transformer.evaluate.EvaluationScheme;
import moaflow.transformer.evaluate.Prequential;

/**
 * Implements base functionality for evaluating learners.
 * Used by {@link EvaluateClassifier} and {@link EvaluateRegressor}.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public abstract class AbstractEvaluate
  extends AbstractTransformer<Example<Instance>, LearningEvaluation> {

  /** Option to specify the evaluation scheme to use. */
  public ClassOption evaluationScheme = new ClassOption("scheme", 's', "The evaluation scheme to use", EvaluationScheme.class, Prequential.class.getName());

  /** Option to specify how often to output evaluations. */
  public IntOption everyNth = new IntOption("everyNth", 'n', "Every n-th evalutation the classifier will get forwarded", 1000, 1, Integer.MAX_VALUE);

  /** The evaluation scheme to use. **/
  protected transient EvaluationScheme actualEvaluationScheme;

  /**
   * For initializing members.
   */
  @Override
  protected void init() {
    super.init();
    actualEvaluationScheme = null;
  }

  /**
   * Sets the evaluation scheme via a commandline string.
   *
   * @param value the commandline
   */
  public void setEvaluationScheme(String value) {
    evaluationScheme.setCurrentObject(Utils.fromCommandLine(EvaluationScheme.class, value));
  }

  /**
   * Transforms the input data.
   *
   * @param input the input data
   * @return the generated output data
   */
  protected LearningEvaluation doProcess(Example<Instance> input) {
    // Initialise on the first instance
    if (actualEvaluationScheme == null) {
      actualEvaluationScheme = (EvaluationScheme) evaluationScheme.getPreMaterializedObject();
      actualEvaluationScheme.initialise(
        getLearner(new InstancesHeader(input.getData().dataset())),
	getEvaluator()
      );
    }

    // Evaluate using the scheme
    actualEvaluationScheme.evaluate(input);

    // If we've reached the n-th instance, output an evaluation
    return actualEvaluationScheme.numInstancesProcessed() % everyNth.getValue() == 0 ?
      actualEvaluationScheme.getEvaluation() :
      null;
  }

  /**
   * Gets the learner to evaluate.
   *
   * @return	The initialised learner.
   */
  protected abstract Learner<Example<Instance>> getLearner(InstancesHeader header);

  /**
   * Get the evaluator to use.
   *
   * @return	The initialised evaluator.
   */
  protected abstract LearningPerformanceEvaluator<Example<Instance>> getEvaluator();

}
