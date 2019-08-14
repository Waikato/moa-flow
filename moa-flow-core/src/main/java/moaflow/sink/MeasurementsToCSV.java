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

package moaflow.sink;

import com.github.javacliparser.FileOption;
import com.github.javacliparser.StringOption;
import com.yahoo.labs.samoa.instances.Instance;
import moa.core.Example;
import moa.evaluation.LearningPerformanceEvaluator;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * Writes measurements to a CSV file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MeasurementsToCSV
  extends AbstractSink<LearningPerformanceEvaluator<Example<Instance>>> {

  public FileOption outputFile = new FileOption("outputFile", 'f', "The file to output the measurements to", ".", ".csv", true);

  public StringOption separator = new StringOption("separator", 's', "The separator to use for the cells", ",");

  /** whether this is the first output. */
  protected boolean first;

  /** the actual output file. */
  protected transient File actualOutputFile;

  /**
   * Gets the purpose of this object
   *
   * @return the string with the purpose of this object
   */
  @Override
  public String getPurposeString() {
    return "Stores the measures in the specified CSV file.";
  }

  /**
   * For initializing members.
   */
  @Override
  protected void init() {
    super.init();
    first = true;
    actualOutputFile = null;
  }

  /**
   * For processing the received input.
   *
   * @param input the data to process
   */
  @Override
  protected void doProcess(LearningPerformanceEvaluator<Example<Instance>> input) {
    if (actualOutputFile == null)
      actualOutputFile = outputFile.getFile();
    if (actualOutputFile.isDirectory())
      throw new IllegalStateException("Output file is a directory: " + outputFile);

    StringBuilder content = new StringBuilder();

    if (first) {
      if (actualOutputFile.exists()) {
        if (!actualOutputFile.delete())
          System.out.println("Failed to delete existing output file: " + outputFile);
      }
      first = false;
    }

    // header?
    if (!actualOutputFile.exists()) {
      for (int i = 0; i < input.getPerformanceMeasurements().length; i++) {
	if (i > 0)
	  content.append(separator.getValue());
	content.append(input.getPerformanceMeasurements()[i].getName());
      }
      content.append("\n");
    }

    // values
    for (int i = 0; i < input.getPerformanceMeasurements().length; i++) {
      if (i > 0)
	content.append(separator.getValue());
      content.append(input.getPerformanceMeasurements()[i].getValue());
    }
    content.append("\n");

    try {
      Files.write(actualOutputFile.toPath(), content.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
    }
    catch (Exception e) {
      onError(new Exception("Failed to write measurements to: " + outputFile, e));
    }
  }
}
