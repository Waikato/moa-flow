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
 * SubscriberManager.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package moaflow.core;

import java.util.List;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;

/**
 * Interface for classes that manage subscribers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface SubscriberManager<T>
  extends Publisher<T> {

  /**
   * Returns a list of current subscribers for monitoring and
   * tracking purposes, not for invoking {@link Flow.Subscriber}
   * methods on the subscribers.
   *
   * @return list of current subscribers
   */
  public List<Subscriber<? super T>> getSubscribers();
}
