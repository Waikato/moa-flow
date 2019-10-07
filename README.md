# moa-flow
Simple workflow API for MOA.

Makes use of the reactive API that Java introduced with Java 9:

https://community.oracle.com/docs/DOC-1006738


## Operators

The following operators are currently available:

### Sources

Package: `com.github.fracpete.moaflow.source`

* `InstanceSource` - generates instances using a stream generator

### Transformers

Package: `com.github.fracpete.moaflow.transformer`

* `EvaluatorClassifier` - evaluates a classifier
* `EvaluatorRegressor` - evaluates a regressor
* `InstanceFilter` - for filtering a data stream
* `TrainClassifier` - for training a classifier
* `TrainRegressor` - for training a regressor

### Sinks

Package: `com.github.fracpete.moaflow.sink`

* `Console` - simply outputs any objects it receives via `toString()`
* `DrawTable` - draws a table
* `MeasurementPlot` - for plotting a single measurement
* `MeasurementsToCSV` - stores the measurements from an evaluation as CSV
* `MeasurementTableSawPlot` - plots realtime chart on web-browser using Jupyter Notebook and IJava
* `OutputLearningCurve` - outputs the whole learning curve of MOA in a table format
* `WriteModel` - stores a model on disk



## Examples

* [ClassificationCsvAndTrain](moa-flow-examples/src/main/java/moaflow/examples/ClassificationCsvAndTrain.java) -- evaluates 
  a classifier, outputs the statistics in a CSV, also trains the model and stores it on disk 
* [ClassificationPlot](moa-flow-examples/src/main/java/moaflow/examples/ClassificationPlot.java) -- evaluates 
  a classifier and plots a statistic. 
* [RegressionCsvAndTrain](moa-flow-examples/src/main/java/moaflow/examples/RegressionCsvAndTrain.java) -- filters the 
  data, evaluates a regressor, outputs the statistics in a CSV, also trains the model and stores it on disk 
* [RegressionPlot](moa-flow-examples/src/main/java/moaflow/examples/RegressionPlot.java) -- evaluates 
  a regressor and plots a statistic. 
* [RegressionConstructSaveLoadRun](moa-flow-examples/src/main/java/moaflow/examples/RegressionConstructSaveLoadRun.java) -- constructs, 
  saves, loads and runs a flow that evaluates a regressor, outputs the statistics in a CSV, 
  also trains the model and stores it on disk 
* [ClassificationVariousSchemaTablePlot](moa-flow-examples/src/main/java/moaflow/examples/ClassificationVariousSchemaTablePlot.java) -- uses different evaluation schemes, output a table of learning curves and plots a statistic with TableSaw (only works with IJava and Jupyter Notebook)


## Modules

* `moa-flow-core` - core functionality
* `moa-flow-examples` - example code
* `moa-flow-app` - meta-module for generating binary .zip and .deb package


## Build

* compile

  ```
  mvn clean install -DskipTests=true
  ```
  
* build .deb package

  ```
  mvn clean install deb:package -DskipTests=true
  ```
