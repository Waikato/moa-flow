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

package com.github.fracpete.moaflow.source;

import com.github.fracpete.moaflow.core.Utils;
import com.yahoo.labs.samoa.instances.Instance;
import moa.core.Example;
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

  protected InstanceStream generator;

  protected int numInstances;

  protected int checkInterval;

  protected int numGenerated;

  public InstanceSource() {
    setGenerator(new RandomRBFGenerator());
    setNumInstances(10000);
    setCheckInterval(1000);
  }

  public void setGenerator(InstanceStream value) {
    generator = value;
  }

  public void setGenerator(String value) {
    setGenerator(Utils.fromCommandLine(InstanceStream.class, value));
  }

  public InstanceStream getGenerator() {
    return generator;
  }

  public void setNumInstances(int value) {
    numInstances = value;
  }

  public int getNumInstances() {
    return numInstances;
  }

  public void setCheckInterval(int value) {
    checkInterval = value;
  }

  public int getCheckInterval() {
    return checkInterval;
  }

  protected void run() {
    numGenerated = 0;
    if (generator instanceof OptionHandler)
      ((OptionHandler) generator).prepareForUse();
    else
      generator.restart();
    while (!isStopped() && generator.hasMoreInstances() && (numGenerated < numInstances)) {
      // check for backpressure
      if (numGenerated % checkInterval == 0) {
	while (!isStopped() && !canProduceNext() && generator.hasMoreInstances()) {
	  Utils.wait(this, 10, 10);
	}
      }
      if (generator.hasMoreInstances()) {
	submit(generator.nextInstance());
	numGenerated++;
      }
    }
  }
}
