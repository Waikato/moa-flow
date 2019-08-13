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
 * InstanceFilter.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.moaflow.transformer;

import com.github.fracpete.moaflow.core.Utils;
import com.yahoo.labs.samoa.instances.Instance;
import moa.core.Example;
import moa.core.InstanceExample;
import moa.options.OptionHandler;
import moa.streams.filters.AddNoiseFilter;
import moa.streams.filters.StreamFilter;

/**
 * Filters instances.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InstanceFilter
  extends AbstractTransformer<Example<Instance>, Example<Instance>> {

  protected StreamFilter filter;

  public InstanceFilter() {
    setFilter(new AddNoiseFilter());
  }

  public void setFilter(StreamFilter value) {
    filter = value;
    ((OptionHandler) filter).prepareForUse();
  }

  public void setFilter(String value) {
    setFilter(Utils.fromCommandLine(StreamFilter.class, value));
  }

  public StreamFilter getFilter() {
    return filter;
  }

  protected Example<Instance> doProcess(Example<Instance> input) {
    return new InstanceExample(filter.filterInstance(input.getData()));
  }
}
