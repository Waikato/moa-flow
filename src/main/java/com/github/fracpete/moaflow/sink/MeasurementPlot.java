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
 * MeasurementPlot.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.moaflow.sink;

import com.github.javacliparser.IntOption;
import com.github.javacliparser.StringOption;
import com.yahoo.labs.samoa.instances.Instance;
import moa.core.Example;
import moa.core.Measurement;
import moa.evaluation.LearningPerformanceEvaluator;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import javax.swing.JFrame;
import java.util.ArrayList;
import java.util.List;

/**
 * Plots a measurement over time.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MeasurementPlot
  extends AbstractSink<LearningPerformanceEvaluator<Example<Instance>>> {

  public StringOption measurement = new StringOption("measurement", 'm', "The measurement to plot", "classifications correct (percent)");

  public StringOption title = new StringOption("title", 't', "The plot title", "Plot");

  public StringOption xAxis = new StringOption("xAxis", 'x', "The x-axis title", "Step");

  public StringOption yAxis = new StringOption("yAxis", 'y', "The y-axis title", "Value");

  public IntOption width = new IntOption("width", 'w', "The width of the plot", 800, 1, Integer.MAX_VALUE);

  public IntOption height = new IntOption("height", 'h', "The height of the plot", 400, 1, Integer.MAX_VALUE);

  public IntOption maxPoints = new IntOption("maxPoints", 'p', "The maximum number of data points (< 1 unlimited)", -1, -1, Integer.MAX_VALUE);

  /** the x data. */
  protected List<Double> xData;

  /** the y data. */
  protected List<Double> yData;

  /** the chart. */
  protected transient XYChart chart;

  /** the generated frame. */
  protected transient JFrame frame;

  /** the counter for x. */
  protected int xCounter;

  /**
   * Gets the purpose of this object
   *
   * @return the string with the purpose of this object
   */
  @Override
  public String getPurposeString() {
    return "Plots a measurement over time.";
  }

  /**
   * For initializing members.
   */
  @Override
  protected void init() {
    super.init();
    xData = new ArrayList<>();
    yData = new ArrayList<>();
  }

  /**
   * For processing the received input.
   *
   * @param input the data to process
   */
  @Override
  protected void doProcess(LearningPerformanceEvaluator<Example<Instance>> input) {
    Double value = null;
    for (Measurement m: input.getPerformanceMeasurements()) {
      if (m.getName().equals(measurement.getValue())) {
        value = m.getValue();
        break;
      }
    }
    if (value != null) {
      xCounter++;
      xData.add((double) xCounter);
      yData.add(value);
    }
    if (maxPoints.getValue() > 0) {
      while (xData.size() > maxPoints.getValue()) {
        xData.remove(0);
        yData.remove(0);
      }
    }

    if (chart == null) {
      chart = new XYChart(width.getValue(), height.getValue());
      chart.setTitle(title.getValue());
      chart.setXAxisTitle(xAxis.getValue());
      chart.setYAxisTitle(yAxis.getValue());
      chart.addSeries(measurement.getValue(), xData, yData);
      frame = new SwingWrapper(chart).displayChart();
      frame.setTitle("Measurement plot");
    }
    else {
      chart.updateXYSeries(measurement.getValue(), xData, yData, null);
      frame.repaint();
    }
  }
}
