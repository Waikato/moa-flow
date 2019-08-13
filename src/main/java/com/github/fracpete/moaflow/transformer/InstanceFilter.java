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
import moa.options.ClassOption;
import moa.streams.filters.AddNoiseFilter;
import moa.streams.filters.StreamFilter;

/**
 * Filters instances.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InstanceFilter
  extends AbstractTransformer<Example<Instance>, Example<Instance>> {

  public ClassOption filter = new ClassOption("filter", 'f', "The filter to apply to the data stream", StreamFilter.class, AddNoiseFilter.class.getName());

  /** the actual filter. */
  protected transient StreamFilter actualFilter;

  /**
   * Gets the purpose of this object
   *
   * @return the string with the purpose of this object
   */
  @Override
  public String getPurposeString() {
    return "Applies the specified filter to the data stream.";
  }

  /**
   * Sets the filter via a commandline string.
   *
   * @param value the commandline
   */
  public void setFilter(String value) {
    filter.setCurrentObject(Utils.fromCommandLine(StreamFilter.class, value));
  }

  /**
   * Transforms the input data.
   *
   * @param input the input data
   * @return the generated output data
   */
  protected Example<Instance> doProcess(Example<Instance> input) {
    if (actualFilter == null)
      actualFilter = (StreamFilter) filter.getPreMaterializedObject();

    return new InstanceExample(actualFilter.filterInstance(input.getData()));
  }
}
