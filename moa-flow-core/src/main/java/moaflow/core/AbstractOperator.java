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
 * AbstractOperator.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package moaflow.core;

import moa.core.ObjectRepository;
import moa.options.AbstractOptionHandler;
import moa.tasks.TaskMonitor;

/**
 * Ancestor for operators.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractOperator
  extends AbstractOptionHandler
  implements Operator {

  /**
   * Default constructor.
   */
  protected AbstractOperator() {
    init();
    finishInit();
  }

  /**
   * For initializing members.
   */
  protected void init() {
  }

  /**
   * For finalizing the initialization.
   */
  protected void finishInit() {
  }

  /**
   * Gets the purpose of this object
   *
   * @return the string with the purpose of this object
   */
  @Override
  public abstract String getPurposeString();

  /**
   * Does nothing.
   *
   * @param monitor
   * @param repository
   */
  @Override
  protected void prepareForUseImpl(TaskMonitor monitor, ObjectRepository repository) {
  }

  /**
   * Appends the commandline of this operator.
   *
   * @param sb the buffer to append it to
   * @param indent the indentation
   */
  @Override
  public void getDescription(StringBuilder sb, int indent) {
    for (int i = 0; i < indent; i++)
      sb.append(" ");
    sb.append(Utils.toCommandLine(this));
  }
}
