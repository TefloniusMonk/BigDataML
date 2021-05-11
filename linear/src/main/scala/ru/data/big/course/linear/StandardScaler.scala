package ru.data.big.course.linear

import breeze.linalg.{*, DenseMatrix}
import breeze.stats.{mean, stddev}

import scala.collection.immutable.HashMap

class StandardScaler(private var meanMap: Map[Int, Double] = new HashMap[Int, Double](),
                     private var stdMap: Map[Int, Double] = new HashMap[Int, Double](),
                     private var fitted: Boolean = false
                    ) {

  def fitTransform(data: DenseMatrix[Double]): DenseMatrix[Double] = {
    val copyTransposed = data.copy.t
    var i = 0
    val scaled = copyTransposed(*, ::).map(column => {
      val meanForColumn = mean(column)
      meanMap += (i -> meanForColumn)
      val stdForColumn = stddev(column)
      stdMap += (i -> stdForColumn)
      i += 1
      column.map(value => {
        (value - meanForColumn) / stdForColumn
      })
    })
    fitted = true
    scaled.t
  }

  def transform(data: DenseMatrix[Double]): DenseMatrix[Double] = {
    if (!fitted) {
      throw new Exception("Call fit before transform")
    }
    val copyTransposed = data.copy.t
    var i = 0
    val scaled = copyTransposed(*, ::).map(column => {
      val meanForColumn = meanMap(i)
      val stdForColumn = stdMap(i)
      i += 1
      column.map(value => {
        (value - meanForColumn) / stdForColumn
      })
    })
    scaled.t
  }
}
