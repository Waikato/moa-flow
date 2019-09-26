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

package moaflow.sink;

import com.github.javacliparser.IntOption;
import com.github.javacliparser.StringOption;
import com.yahoo.labs.samoa.instances.Instance;
import moa.core.Example;
import moa.core.Measurement;
import moa.evaluation.LearningEvaluation;
import moa.evaluation.LearningPerformanceEvaluator;

import java.util.Map;
import java.util.UUID;

import moa.evaluation.preview.LearningCurve;
import tech.tablesaw.plotly.components.*;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.traces.ScatterTrace;
import io.github.spencerpark.ijava.IJava;

import static io.github.spencerpark.ijava.runtime.Display.*;

/**
 * Plots a measurement over time. This class is used for plotting realtime chart
 * on web-browser using Jupyter Notebook and IJava
 *
 * @author Truong To (todinhtruong at gmail dot com)
 */
public class MeasurementTableSawPlot
        extends AbstractSink<Object> {

    public StringOption measurement = new StringOption("measurement", 'm', "The measurement to plot", "classifications correct (percent)");

    public StringOption title = new StringOption("title", 't', "The plot title", "Plot");

    public IntOption maxPoints = new IntOption("maxPoints", 'p', "The maximum number of data points (< 1 unlimited)", -1, -1, Integer.MAX_VALUE);

    /** By default, plots the data at the column with index 4 **/
    int index = 4;

    /** Stores the learning curve **/
    protected LearningCurve learningCurve;

    /** id of the chart**/
    protected String id = UUID.randomUUID().toString().replace("-", "");

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
        learningCurve = new LearningCurve("learning evaluation instances");
    }

    /**
     * For processing the received input.
     * The code for rendering a chart in Jupyter Notebook and IJava is provided by SpencerPark - the author of IJava
     * https://stackoverflow.com/questions/54654434/how-to-embed-tablesaw-graph-in-jupyter-notebook-with-ijava-kernel?answertab=oldest#tab-top
     * @param input the data to process
     */
    @Override
    protected void doProcess(Object input) {
        IJava.getKernelInstance().getRenderer()
                .createRegistration(tech.tablesaw.plotly.components.Figure.class)
                .preferring(io.github.spencerpark.jupyter.kernel.display.mime.MIMEType.TEXT_HTML)
                .register((figure, ctx) -> {
                    ctx.renderIfRequested(io.github.spencerpark.jupyter.kernel.display.mime.MIMEType.TEXT_HTML, () -> {

                        figure.asJavascript(this.id);
                        Map<String, Object> context = figure.getContext();

                        StringBuilder html = new StringBuilder();
                        html.append("<div id=\"").append(this.id).append("\"></div>\n");
                        html.append("<script>require(['https://cdn.plot.ly/plotly-1.44.4.min.js'], Plotly => {\n");
                        html.append("var target_").append(this.id).append(" = document.getElementById('").append(this.id).append("');\n");
                        html.append(context.get("figure")).append('\n');
                        html.append(context.get("plotFunction")).append('\n');
                        html.append("})</script>\n");
                        return html.toString();
                    });
                });

        learningCurve.insertEntry((LearningEvaluation) input);
        double[] xData = new double[learningCurve.numEntries()];
        double[] yData = new double[learningCurve.numEntries()];
        String[] hStr = learningCurve.headerToString().split(",");
        for (int i =0; i<hStr.length;i++) {
            if (hStr[i].equals(measurement.getValue())) {
                title.setValue(measurement.getValue());
                index = i;
                break;
            }
        }

        for (int i = 0; i < learningCurve.numEntries(); i++) {
            String[] eStr = learningCurve.entryToString(i).split(",");
            xData[i]= Double.valueOf(eStr[1]);
            yData[i]= Double.valueOf(eStr[index]);
        }

        Layout layout = Layout.builder().title(title.getValue()).build();
        ScatterTrace trace = ScatterTrace.builder(xData, yData).mode(ScatterTrace.Mode.LINE).build();
        display(new Figure(layout, trace), "text/html");
    }
}
