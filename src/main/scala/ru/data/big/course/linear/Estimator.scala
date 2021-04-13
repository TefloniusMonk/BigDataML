package ru.data.big.course.linear

import breeze.linalg.{DenseMatrix, DenseVector}

trait Estimator[A] {
  def fit(): Estimator[A]

  def predict(x: DenseMatrix[A]): DenseVector[A]
}
