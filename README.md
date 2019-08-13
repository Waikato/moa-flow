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
* `MeasurementPlot` - for plotting a single measurement
* `MeasurementsToCSV` - stores the measurements from an evaluation as CSV
* `WriteModel` - stores a model on disk

## Examples

* [Example1](src/main/java/com/github/fracpete/moaflow/examples/Example1.java) -- evaluates 
  a classifier, outputs the statistics in a CSV, also trains the model and stores it on disk 
* [Example2](src/main/java/com/github/fracpete/moaflow/examples/Example2.java) -- filters the 
  data, evaluates a regressor, outputs the statistics in a CSV, also trains the model and stores it on disk 
* [Example3](src/main/java/com/github/fracpete/moaflow/examples/Example3.java) -- constructs, 
  saves, loads and runs a flow that evaluates a regressor, outputs the statistics in a CSV, 
  also trains the model and stores it on disk 
* [Example4](src/main/java/com/github/fracpete/moaflow/examples/Example4.java) -- evaluates 
  a classifier and plots a statistic. 
