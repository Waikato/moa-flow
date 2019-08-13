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
 * WriteModel.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.moaflow.sink;

import com.github.javacliparser.FileOption;
import com.yahoo.labs.samoa.instances.Instance;
import moa.core.Example;
import moa.learners.Learner;
import weka.core.SerializationHelper;

import java.io.File;

/**
 * Stores the model on disk.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WriteModel
  extends AbstractSink<Learner<Example<Instance>>> {

  public FileOption modelFile = new FileOption("modelFile", 'f', "The file to write the model to", ".", ".model", true);

  /** the learner that was last received. */
  protected transient Learner<Example<Instance>> model;

  /** the actual model file. */
  protected transient File actualModelFile;

  /**
   * Gets the purpose of this object
   *
   * @return the string with the purpose of this object
   */
  @Override
  public String getPurposeString() {
    return "Serializes the incoming model to disk.";
  }

  /**
   * For initializing members.
   */
  @Override
  protected void init() {
    super.init();
    model = null;
  }

  /**
   * For processing the received input.
   *
   * @param input the data to process
   */
  @Override
  protected void doProcess(Learner<Example<Instance>> input) {
    model = input;
    if (actualModelFile == null)
      actualModelFile = modelFile.getFile();

    if (!actualModelFile.isDirectory()) {
      synchronized (model) {
	try {
	  SerializationHelper.write(actualModelFile.getAbsolutePath(), model);
	  System.out.println("Model written to: " + modelFile);
	}
	catch (Exception e) {
	  onError(new Exception("Failed to write model to: " + modelFile, e));
	}
      }
    }
    else {
      onError(new IllegalStateException("Model file points to directory: " + modelFile));
    }
  }
}
