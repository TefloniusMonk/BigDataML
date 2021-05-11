package ru.data.big.course.linear

import breeze.linalg.{DenseMatrix, DenseVector}

import scala.collection.mutable.ListBuffer

trait Estimator[A] {
  def fit(): Estimator[A]

  def predict(x: DenseMatrix[A]): DenseVector[A]

  def learnHistory(): ListBuffer[A]

  def weights(): DenseVector[A]
}
