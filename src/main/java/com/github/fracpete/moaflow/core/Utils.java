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
 * Utils.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.moaflow.core;

import com.github.fracpete.moaflow.source.AbstractSource;
import moa.MOAObject;
import moa.options.AbstractOptionHandler;
import moa.options.OptionHandler;

import java.util.concurrent.SubmissionPublisher;

/**
 * Helper class for waiting.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Utils {

  /**
   * A simple waiting method.
   *
   * @param obj      the object to use for logging and synchronizing
   * @param msec     the maximum number of milli-seconds to wait, no waiting if 0
   * @param interval the amount msecs to wait before checking state (interval < msec)
   */
  public static void wait(Object obj, int msec, int interval) {
    int count;
    int current;

    if (msec == 0)
      return;

    count = 0;
    while (count < msec) {
      try {
	current = msec - interval;
	if (current <= 0)
	  current = msec;
	if (current > interval)
	  current = interval;
	synchronized (obj) {
	  obj.wait(current);
	}
	count += current;
      }
      catch (Throwable t) {
	// ignored
      }
    }
  }

  public static <T> T fromCommandLine(Class<T> requiredType, String commandline) {
    T result;
    try {
      String[] tmpOptions = weka.core.Utils.splitOptions(commandline);
      String classname = tmpOptions[0];
      tmpOptions[0] = "";

      try {
	result = (T) Class.forName(classname).newInstance();
      }
      catch (Exception var6) {
	result = (T) Class.forName(requiredType.getPackage().getName() + "." + classname).newInstance();
      }

      if (result instanceof AbstractOptionHandler) {
	((AbstractOptionHandler) result).getOptions().setViaCLIString(weka.core.Utils.joinOptions(tmpOptions));
	((AbstractOptionHandler) result).prepareForUse();
      }
    }
    catch (Exception var7) {
      var7.printStackTrace();
      result = null;
    }

    return result;
  }

  public static String toCommandLine(MOAObject obj) {
    String result = obj.getClass().getName();
    if (obj instanceof OptionHandler) {
      result = result + " " + ((OptionHandler) obj).getOptions().getAsCLIString();
    }

    return result.trim();
  }

  protected static void toTree(Object obj, String indent, StringBuilder content) {
    content.append(indent);
    content.append(obj.getClass().getName());
    content.append("\n");
    if (obj instanceof SubmissionPublisher) {
      SubmissionPublisher pub = (SubmissionPublisher) obj;
      for (Object sub : pub.getSubscribers())
	toTree(sub, indent + "  ", content);
    }
  }

  public static String toTree(AbstractSource source) {
    StringBuilder result;

    result = new StringBuilder();
    toTree(source, "", result);

    return result.toString();
  }
}
