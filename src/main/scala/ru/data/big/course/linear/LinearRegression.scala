package ru.data.big.course.linear

import breeze.linalg._
import breeze.math.PowImplicits.DoublePow

trait Regularization

case class NONE() extends Regularization

case class LASSO() extends Regularization

case class RIDGE() extends Regularization

case class ELASTIC_NET() extends Regularization

class LinearRegression(val X: DenseMatrix[Double] = null,
                       val y: DenseVector[Double] = null,
                       val learningRate: Double = 10 pow (-5),
                       val precision: Double = 10 pow (-5),
                       val regularisation: Regularization = NONE(),
                       val maxIterations: Int = 1000,
                       var weights: DenseVector[Double] = null,
                       var bias: Double = 0,
                       var l1Penalty: Double = 0.01,
                       var l2Penalty: Double = 0.01,
                       var elasticNetRatio: Double = 0.5,
                       private var isInit: Boolean = false,
                       private var lastStepSize: Double = Double.MaxValue,
                      ) extends Estimator[Double] {
  override def fit(): Estimator[Double] = {
    validateParams()
    if (!isInit) {
      weights = DenseVector.zeros[Double](X.cols)
    }
    regularisation match {
      case NONE() => fitWithoutRegularization()
      case LASSO() => fitLasso()
      case RIDGE() => fitRidge()
      case ELASTIC_NET() => {
        null
      }
    }

    this
  }

  override def predict(x: DenseMatrix[Double]): DenseVector[Double] = {
    (x * weights).toDenseVector + bias
  }

  private def fitWithoutRegularization(): Unit = {
    var currIter = 0
    while (lastStepSize > precision && currIter < maxIterations) {
      val error = y - predict(X)
      val dW: DenseVector[Double] = -(2.0 * (X.t.toDenseMatrix * error)) / y.length.toDouble
      val db = getBiasDiff(error)
      step(dW, db)
      currIter += 1
    }
  }

  private def fitLasso(): Unit = {
    val dW = DenseVector.zeros[Double](X.cols)
    var col = 0
    for (col <- 0 until X.cols) {
      val error = y - predict(X)
      if (weights(col) > 0) {
        val grad = (-(2.0 * X(::, col).toDenseMatrix * error + l1Penalty) / y.length.toDouble)
        dW(col) = grad(0)
      } else {
        val grad = (-(2.0 * X(::, col).toDenseMatrix * error - l1Penalty) / y.length.toDouble)
        dW(col) = grad(0)
      }
      val db = -2 * sum(error) / y.length.toDouble
      step(dW, db)
    }
  }

  private def fitRidge(): Unit = {
    var currIter = 0
    while (lastStepSize > precision && currIter < maxIterations) {
      val error = y - predict(X)
      val dW = (-(2.0 * (X.t * error)) + (2 * l1Penalty * weights)) / y.length.toDouble
      val db = getBiasDiff(error)
      step(dW, db)
      currIter += 1
    }
  }

  private def getBiasDiff(error: DenseVector[Double]) = {
    -2.0 * sum(error) / y.length.toDouble
  }

  private def step(dW: DenseVector[Double], db: Double) = {
    weights = weights - learningRate * dW
    bias = bias - learningRate * db
  }

  private def validateParams(): Unit = {
    if (X == null) {
      throw new Exception("X is not specified")
    }
    if (y == null) {
      throw new Exception("y is not specified")
    }
    if (X.rows != y.length) {
      throw new Exception("X must have same number of rows as y.size")
    }
    if (maxIterations <= 0) {
      throw new Exception("Max iterations must be more than 0")
    }
    if (learningRate < 0) {
      throw new Exception("Learning rate must be more than 0")
    }
    if (precision < 0) {
      throw new Exception("Precision rate must be more than 0")
    }
    regularisation match {
      case ELASTIC_NET() =>
        if (elasticNetRatio > 1 || elasticNetRatio < 0) {
          throw new Exception("Elastic net ration must be between 0 and 1")
        }
      case _ =>
    }
  }
}

object LinearRegression {
  def apply(X: DenseMatrix[Double], y: DenseVector[Double]): LinearRegression = {
    new LinearRegression(X, y)
  }

  def apply(X: DenseMatrix[Double], y: DenseVector[Double], regularization: Regularization): LinearRegression = {
    new LinearRegression(X, y, regularisation = regularization)
  }
}
