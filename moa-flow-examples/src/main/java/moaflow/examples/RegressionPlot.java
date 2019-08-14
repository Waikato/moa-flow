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

package moaflow.examples;

import moa.classifiers.functions.SGD;
import moaflow.core.Utils;
import moaflow.sink.Console;
import moaflow.sink.MeasurementPlot;
import moaflow.source.InstanceSource;
import moaflow.transformer.EvaluateRegressor;

/**
 * Example flow for plotting measurement from regression evaluation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RegressionPlot {

  public static void main(String[] args) throws Exception {
    String regressor = SGD.class.getName();

    InstanceSource source;
    source = new InstanceSource();
    source.setGenerator("moa.streams.generators.RandomRBFGenerator -a 20");
    source.numInstances.setValue(1000000);

    EvaluateRegressor eval = new EvaluateRegressor();
    eval.everyNth.setValue(1000);
    eval.setRegressor(regressor);
    source.subscribe(eval);

    MeasurementPlot plot = new MeasurementPlot();
    plot.measurement.setValue("root mean squared error");
    plot.maxPoints.setValue(-1);
    eval.subscribe(plot);

    Console console = new Console();
    console.outputSeparator.setValue("------");
    eval.subscribe(console);

    System.out.println(Utils.toTree(source));

    source.start();
  }
}
