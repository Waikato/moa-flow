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

import com.github.fracpete.moaflow.core.Utils;
import com.github.fracpete.moaflow.sink.Console;
import com.github.fracpete.moaflow.sink.MeasurementsToCSV;
import com.github.fracpete.moaflow.sink.WriteModel;
import com.github.fracpete.moaflow.source.InstanceSource;
import com.github.fracpete.moaflow.transformer.EvaluateClassifier;
import com.github.fracpete.moaflow.transformer.TrainClassifier;
import moa.classifiers.trees.HoeffdingTree;

import java.io.File;

/**
 * Example flow.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Example1 {

  public static void main(String[] args) throws Exception {
    String classifier = HoeffdingTree.class.getName() + " -b";

    InstanceSource source;
    /*
    RandomRBFGenerator generator = new RandomRBFGenerator();
    generator.numAttsOption.setValue(20);
    source = new InstanceSource();
    source.setGenerator(generator);
    source.setNumInstances(10000);
    */
    source = new InstanceSource();
    source.setGenerator("moa.streams.generators.RandomRBFGenerator -a 20");
    source.setNumInstances(100000);

    EvaluateClassifier eval = new EvaluateClassifier();
    eval.setEveryNth(10000);
    eval.setClassifier(classifier);
    source.subscribe(eval);

    Console console = new Console();
    eval.subscribe(console);

    MeasurementsToCSV measurements = new MeasurementsToCSV();
    measurements.setOutputFile(new File(System.getProperty("java.io.tmpdir") + "/moa.csv"));
    eval.subscribe(measurements);

    TrainClassifier train = new TrainClassifier();
    train.setClassifier(classifier);
    train.setEveryNth(10000);
    source.subscribe(train);

    WriteModel model = new WriteModel();
    model.setModelFile(new File(System.getProperty("java.io.tmpdir") + "/moa.model"));
    train.subscribe(model);

    System.out.println(Utils.toTree(source));

    source.start();
  }
}
