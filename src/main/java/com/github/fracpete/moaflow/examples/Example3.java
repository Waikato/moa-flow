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
 * Example1.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.moaflow.examples;

import com.github.fracpete.moaflow.core.Operator;
import com.github.fracpete.moaflow.core.Utils;
import com.github.fracpete.moaflow.io.DefaultFlowReader;
import com.github.fracpete.moaflow.io.DefaultFlowWriter;
import com.github.fracpete.moaflow.sink.Console;
import com.github.fracpete.moaflow.sink.MeasurementsToCSV;
import com.github.fracpete.moaflow.source.AbstractSource;
import com.github.fracpete.moaflow.source.InstanceSource;
import com.github.fracpete.moaflow.transformer.EvaluateRegressor;
import com.github.fracpete.moaflow.transformer.InstanceFilter;
import moa.classifiers.functions.SGD;
import moa.streams.filters.ReplacingMissingValuesFilter;

/**
 * Example flow for regression.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Example3 {

  public static void main(String[] args) throws Exception {
    String regressor = SGD.class.getName();

    InstanceSource source;
    source = new InstanceSource();
    source.setGenerator("moa.streams.generators.RandomRBFGenerator -a 20");
    source.numInstances.setValue(100000);

    ReplacingMissingValuesFilter replace = new ReplacingMissingValuesFilter();
    InstanceFilter filter = new InstanceFilter();
    filter.filter.setCurrentObject(replace);
    source.subscribe(filter);

    EvaluateRegressor eval = new EvaluateRegressor();
    eval.everyNth.setValue(10000);
    eval.setRegressor(regressor);
    filter.subscribe(eval);

    Console console = new Console();
    eval.subscribe(console);

    MeasurementsToCSV measurements = new MeasurementsToCSV();
    measurements.outputFile.setValue(System.getProperty("java.io.tmpdir") + "/moa.csv");
    eval.subscribe(measurements);

    String flowfile = System.getProperty("java.io.tmpdir") + "/moa-regression.flow";
    DefaultFlowWriter writer = new DefaultFlowWriter();
    String msg = writer.write(flowfile, source);
    if (msg != null)
      System.err.println("Failed to store flow:\n" + msg);
    else
      System.out.println("Flow written to: " + flowfile);

    DefaultFlowReader reader = new DefaultFlowReader();
    Operator flow = reader.read(flowfile);
    System.out.println("Flow read from: " + flowfile);
    System.out.println(Utils.toTree(flow));

    System.out.println("Executing flow");
    if (flow instanceof AbstractSource)
      ((AbstractSource) flow).start();
  }
}
