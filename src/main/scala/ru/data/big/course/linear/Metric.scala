package ru.data.big.course.linear

import breeze.linalg.{DenseMatrix, DenseVector, sum}
import breeze.numerics.{abs, pow, sqrt}

object Metric {
  def mse(yTrue: DenseVector[Double], yPred: DenseVector[Double]): Double = {
    if (yTrue.length != yPred.length) {
      throw new Exception("Vectors should have same size")
    }
    sum(pow((yTrue - yPred), 2)) / yTrue.length
  }

  def rmse(yTrue: DenseVector[Double], yPred: DenseVector[Double]): Double = {
    if (yTrue.length != yPred.length) {
      throw new Exception("Vectors should have same size")
    }
    sqrt(mse(yTrue, yPred))
  }

  def mae(yTrue: DenseVector[Double], yPred: DenseVector[Double]): Double = {
    if (yTrue.length != yPred.length) {
      throw new Exception("Vectors should have same size")
    }
    sum(abs(yTrue - yPred)) / yTrue.length
  }
}
