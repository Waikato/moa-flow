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
 * AbstractFlowWriter.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.moaflow.io;

import com.github.fracpete.moaflow.core.Operator;

/**
 * Interface for flow writers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface FlowWriter {

  /**
   * Writes the specified flow to the file.
   *
   * @param filename	the file to write the flow to
   * @param flow	the flow to output
   * @return		null if successfully written, otherwise error message
   */
  public abstract String write(String filename, Operator flow);
}
