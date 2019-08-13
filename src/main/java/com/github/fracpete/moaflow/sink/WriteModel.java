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

import moa.classifiers.Classifier;
import weka.core.SerializationHelper;

import java.io.File;

/**
 * Stores the model on disk.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WriteModel
  extends AbstractSink<Classifier> {

  protected File modelFile;

  protected Classifier model;

  public WriteModel() {
    setModelFile(new File("."));
  }

  public void setModelFile(File value) {
    modelFile = value;
    model = null;
  }

  public File getModelFile() {
    return modelFile;
  }

  @Override
  protected void doProcess(Classifier input) {
    model = input;
    if (!modelFile.isDirectory()) {
      synchronized (model) {
	try {
	  SerializationHelper.write(modelFile.getAbsolutePath(), model);
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
