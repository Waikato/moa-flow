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
 * DefaultFlowReader.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package moaflow.io;

import moaflow.core.Operator;
import moaflow.core.SubscriberManager;
import moaflow.core.Utils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;

/**
 * Default reader for flows.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DefaultFlowReader
  implements FlowReader {

  /**
   * Determines the indentation for each line.
   *
   * @param lines	the lines to process
   * @return		the indentations
   */
  protected List<Integer> getIndentations(List<String> lines) {
    List<Integer>	result;
    int			count;
    int			i;

    result = new ArrayList<>();
    for (String line: lines) {
      count = 0;
      for (i = 0; i < line.length(); i++) {
        if (line.charAt(i) == ' ')
          count++;
        else
          break;
      }
      result.add(count);
    }

    return result;
  }

  /**
   * Instantiates the operators from the lines.
   *
   * @param lines	the lines to process
   * @return		the instantiated operators
   * @throws Exception	if instantiation fails
   */
  protected List<Operator> getOperators(List<String> lines) throws Exception {
    List<Operator>	result;

    result = new ArrayList<>();
    for (String line: lines) {
      line = line.trim();
      if (line.length() > 0)
	result.add(Utils.fromCommandLine(Operator.class, line));
    }

    return result;
  }

  /**
   * Writes the specified flow to the file.
   *
   * @param filename	the file to write the flow to
   * @return		the flow, null if failed to read
   * @throws Exception  if reading fails
   */
  @Override
  public Operator read(String filename) throws Exception {
    Operator		result;
    List<String> 	lines;
    List<Integer> 	indents;
    List<Operator>	ops;
    int			i;
    int			n;
    int			indent;
    Operator		op;
    boolean		found;

    lines  = Files.readAllLines(new File(filename).toPath());
    if (lines.size() == 0)
      throw new Exception("No operators defined in file: " + filename);
    indents = getIndentations(lines);
    if (indents.get(0) > 0)
      throw new Exception("First indentation is not 0: " + indents.get(0) + "\n" + lines.get(0));
    ops = getOperators(lines);
    result = ops.get(0);
    for (i = 1; i < ops.size(); i++) {
      indent = indents.get(i);
      op     = ops.get(i);
      // find parent operator (with indentation - 1)
      found = false;
      for (n = i - 1; n >= 0; n--) {
        if (indents.get(n) == indent - 1) {
	  if (!(ops.get(n) instanceof SubscriberManager))
	    throw new IllegalStateException("Operator in line #" + (n+1) + " does not allow subscription of other operators!\n" + lines.get(n));
	  if (!(op instanceof Subscriber))
	    throw new IllegalStateException("Operator in line #" + (i+1) + " cannot subscribe to other operators!\n" + lines.get(i));
	  ((SubscriberManager) ops.get(n)).subscribe((Subscriber) op);
	  found = true;
          break;
	}
      }
      if (!found)
	throw new IllegalStateException("Failed to find parent operator for operator in line #" + (i+1) + ":\n" + lines.get(i));
    }

    return result;
  }
}
