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
 * AbstractTransformer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package moaflow.transformer;

import moaflow.core.AbstractOperator;
import moaflow.core.SubscriberManager;

import java.util.List;
import java.util.concurrent.Flow.Processor;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.SubmissionPublisher;

/**
 * Ancestor for transformers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractTransformer<I, O>
  extends AbstractOperator
  implements SubscriberManager<O>, Processor<I, O> {

  /** for managing subscribers and publishing data. */
  protected transient SubmissionPublisher<O> publisher;

  /** the current subscription. */
  protected transient Subscription subscription;

  /**
   * For initializing members.
   */
  @Override
  protected void init() {
    super.init();
    publisher = new SubmissionPublisher<>();
  }

  /**
   * Method invoked prior to invoking any other Subscriber
   * methods for the given Subscription. If this method throws
   * an exception, resulting behavior is not guaranteed, but may
   * cause the Subscription not to be established or to be cancelled.
   *
   * <p>Typically, implementations of this method invoke {@code
   * subscription.request} to enable receiving items.
   *
   * @param value a new subscription
   */
  @Override
  public void onSubscribe(Subscription value) {
    subscription = value;
    subscription.request(Long.MAX_VALUE);
  }

  /**
   * Transforms the input data.
   *
   * @param input the input data
   * @return the generated output data
   */
  protected abstract O doProcess(I input);

  /**
   * Method invoked with a Subscription's next item.  If this
   * method throws an exception, resulting behavior is not
   * guaranteed, but may cause the Subscription to be cancelled.
   *
   * @param input the item
   */
  @Override
  public synchronized void onNext(I input) {
    O output = doProcess(input);
    if (output != null)
      publisher.submit(output);
    subscription.request(Long.MAX_VALUE);
  }

  /**
   * Method invoked upon an unrecoverable error encountered by a
   * Publisher or Subscription, after which no other Subscriber
   * methods are invoked by the Subscription.  If this method
   * itself throws an exception, resulting behavior is
   * undefined.
   *
   * @param throwable the exception
   */
  @Override
  public void onError(Throwable throwable) {
    throwable.printStackTrace();
  }

  /**
   * Method invoked when it is known that no additional
   * Subscriber method invocations will occur for a Subscription
   * that is not already terminated by error, after which no
   * other Subscriber methods are invoked by the Subscription.
   * If this method throws an exception, resulting behavior is
   * undefined.
   */
  @Override
  public void onComplete() {
    publisher.close();
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
