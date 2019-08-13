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

import java.util.concurrent.SubmissionPublisher;

/**
 * Ancestor for sources.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSource<O>
  extends SubmissionPublisher<O> {

  protected boolean stopped;

  protected abstract void run();

  protected boolean canProduceNext() {
    return (estimateMaximumLag() == 0);
  }

  public void start() {
    stopped = false;
    run();
    if (!isStopped()) {
      while (estimateMaximumLag() > 0)
	Utils.wait(this, 1000, 100);
    }
    close();
  }

  public void stop() {
    stopped = true;
  }

  public boolean isStopped() {
    return stopped;
  }
}
