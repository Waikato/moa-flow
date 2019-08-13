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
 * MeasurementsToCSV.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.moaflow.sink;

import moa.evaluation.ClassificationPerformanceEvaluator;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * Writes measurements to a CSV file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MeasurementsToCSV
  extends AbstractSink<ClassificationPerformanceEvaluator> {

  protected File outputFile;

  protected String separator;

  public MeasurementsToCSV() {
    setOutputFile(new File("."));
    setSeparator(",");
  }

  public void setOutputFile(File value) {
    outputFile = value;
  }

  public File getOutputFile() {
    return outputFile;
  }

  public void setSeparator(String value) {
    separator = value;
  }

  public String getSeparator() {
    return separator;
  }

  @Override
  protected void doProcess(ClassificationPerformanceEvaluator input) {
    if (outputFile.isDirectory())
      throw new IllegalStateException("Output file is a directory: " + outputFile);

    StringBuilder content = new StringBuilder();

    // header?
    if (!outputFile.exists()) {
      for (int i = 0; i < input.getPerformanceMeasurements().length; i++) {
	if (i > 0)
	  content.append(separator);
	content.append(input.getPerformanceMeasurements()[i].getName());
      }
      content.append("\n");
    }

    // values
    for (int i = 0; i < input.getPerformanceMeasurements().length; i++) {
      if (i > 0)
	content.append(separator);
      content.append(input.getPerformanceMeasurements()[i].getValue());
    }
    content.append("\n");

    try {
      Files.write(outputFile.toPath(), content.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
    }
    catch (Exception e) {
      onError(new Exception("Failed to write measurements to: " + outputFile, e));
    }
  }
}
