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
 * Console.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.moaflow.sink;

import com.github.javacliparser.IntOption;
import com.github.javacliparser.StringOption;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Just outputs the object on stdout.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Console
  extends AbstractSink<Object> {

  public IntOption everyNth = new IntOption("everyNth", 'n', "Every n-th object will get output on the console", 1, 1, Integer.MAX_VALUE);

  public StringOption outputSeparator = new StringOption("outputSeparator", 's', "The separator to use between outputs, see https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html", "");

  /** the counter for the objects. */
  protected int counter;

  /** for formatting the timestamp. */
  protected transient SimpleDateFormat format;

  /** whether to use a separator. */
  protected Boolean useSeparator;

  /**
   * For initializing members.
   */
  @Override
  protected void init() {
    super.init();
    counter = 0;
    useSeparator = null;
  }

  /**
   * Gets the purpose of this object
   *
   * @return the string with the purpose of this object
   */
  @Override
  public String getPurposeString() {
    return "Outputs the objects it receives on stdout";
  }

  /**
   * For processing the received input.
   *
   * @param input the data to process
   */
  protected void doProcess(Object input) {
    counter++;
    if (counter == everyNth.getValue()) {
      if (useSeparator == null) {
        useSeparator = !outputSeparator.getValue().isEmpty();
	if (useSeparator) {
	  try {
	    format = new SimpleDateFormat(outputSeparator.getValue());
	  }
	  catch (Exception e) {
	    onError(new Exception("Failed to parse separator format: " + outputSeparator.getValue(), e));
	    format = null;
	    useSeparator = false;
	  }
	}
      }

      if (useSeparator)
        System.out.println(format.format(new Date()));

      System.out.println(input);
      counter = 0;
    }
  }
}
