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
 * DataSource.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package moaflow.source;

import moaflow.core.Utils;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Instance;
import moa.core.Example;
import moa.options.ClassOption;
import moa.options.OptionHandler;
import moa.streams.InstanceStream;
import moa.streams.generators.RandomRBFGenerator;

/**
 * Generates instances using the specified generator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InstanceSource
  extends AbstractSource<Example<Instance>> {

  public ClassOption generator = new ClassOption("generator", 'g', "The data stream generator to use", InstanceStream.class, RandomRBFGenerator.class.getName());

  public IntOption numInstances = new IntOption("numInstances", 'i', "The number of instances to generate", 10000, 1, Integer.MAX_VALUE);

  public IntOption checkInterval = new IntOption("checkInterval", 'c', "The interval of generated instances to check for backpressure", 1000, 1, Integer.MAX_VALUE);

  /** the number of instances generated so far. */
  protected int numGenerated;

  /**
   * Gets the purpose of this object
   *
   * @return the string with the purpose of this object
   */
  @Override
  public String getPurposeString() {
    return "Outputs a maximum number of Instance objects with the specified stream generator.";
  }

  /**
   * Sets the generator from a commandline.
   *
   * @param value the commandline
   */
  public void setGenerator(String value) {
    generator.setCurrentObject(Utils.fromCommandLine(InstanceStream.class, value));
  }

  protected void run() {
    numGenerated = 0;
    InstanceStream actualGenerator = (InstanceStream) generator.getPreMaterializedObject();
    if (actualGenerator instanceof OptionHandler)
      ((OptionHandler) actualGenerator).prepareForUse();
    else
      actualGenerator.restart();
    while (!isStopped() && actualGenerator.hasMoreInstances() && (numGenerated < numInstances.getValue())) {
      // check for backpressure
      if (numGenerated % checkInterval.getValue() == 0) {
	while (!isStopped() && !canProduceNext() && actualGenerator.hasMoreInstances()) {
	  Utils.wait(this, 10, 10);
	}
      }
      if (actualGenerator.hasMoreInstances()) {
	publisher.submit(actualGenerator.nextInstance());
	numGenerated++;
      }
    }
  }
}
