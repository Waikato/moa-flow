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

import com.github.fracpete.moaflow.core.AbstractOperator;
import com.github.fracpete.moaflow.core.SubscriberManager;
import com.github.fracpete.moaflow.core.Utils;

import java.util.List;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.SubmissionPublisher;

/**
 * Ancestor for sources.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSource<O>
  extends AbstractOperator
  implements SubscriberManager<O> {

  /** for managing subscribers and publishing data. */
  protected transient SubmissionPublisher<O> publisher;

  /** flag whether the execution was stopped. */
  protected boolean stopped;

  /**
   * For initializing members.
   */
  @Override
  protected void init() {
    super.init();

    publisher = new SubmissionPublisher<>();
    stopped   = false;
  }

  /**
   * For executing the actual data generating loop.
   */
  protected abstract void run();

  /**
   * Whether a new data item can be generated.
   *
   * @return true if a new item can be generated
   */
  protected boolean canProduceNext() {
    return (publisher.estimateMaximumLag() == 0);
  }

  /**
   * Starts the data generation.
   */
  public void start() {
    stopped = false;
    run();
    if (!isStopped()) {
      while (!canProduceNext())
	Utils.wait(this, 1000, 100);
    }
    publisher.close();
  }

  /**
   * For stopping the data generation.
   */
  public void stop() {
    stopped = true;
  }

  /**
   * Whether the data generation has been stopped.
   *
   * @return true if stopped
   */
  public boolean isStopped() {
    return stopped;
  }

  /**
   * Subscribes the supplied object to listen for data.
   *
   * @param subscriber the object to subscribe
   */
  public void subscribe(Subscriber<? super O> subscriber) {
    publisher.subscribe(subscriber);
  }

  /**
   * Returns a list of current subscribers for monitoring and
   * tracking purposes, not for invoking {@link Flow.Subscriber}
   * methods on the subscribers.
   *
   * @return list of current subscribers
   */
  public List<Subscriber<? super O>> getSubscribers() {
    return publisher.getSubscribers();
  }
}
