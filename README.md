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
* `MeasurementsToCSV` - stores the measurements from an evaluation as CSV
* `WriteModel` - stores a model on disk
