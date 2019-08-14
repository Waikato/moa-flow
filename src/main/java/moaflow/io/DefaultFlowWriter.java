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
 * DefaultFlowWriter.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package moaflow.io;

import moaflow.core.Operator;
import moaflow.core.Utils;
import nz.ac.waikato.cms.core.FileUtils;

/**
 * Default writer for flows.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DefaultFlowWriter
  implements FlowWriter {

  /**
   * Writes the specified flow to the file.
   *
   * @param filename	the file to write the flow to
   * @param flow	the flow to output
   * @return		null if successfully written, otherwise error message
   */
  @Override
  public String write(String filename, Operator flow) {
    return FileUtils.writeToFileMsg(filename, Utils.toTree(flow), false, null);
  }
}
