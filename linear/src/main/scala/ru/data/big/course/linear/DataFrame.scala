package ru.data.big.course.linear

import breeze.linalg.{Axis, DenseMatrix, DenseVector}

import scala.io.Source
import scala.util.Random


class DataFrame(var columns: DenseVector[String],
                var values: DenseMatrix[Double],
                var columnMap: Map[String, Int] = null) {

  def drop(column: String, inplace: Boolean = false): DenseMatrix[Double] = {
    val toDropCol = columnMap.getOrElse(column, -1)
    if (toDropCol == -1) {
      return values
    }
    val filtered = values.delete(toDropCol, Axis._1)
    if (inplace) {
      values = filtered
    }

    columnMap =  columnMap.filter(_._1 != column)
    columns = DenseVector(columns.data.filter(_ != column))
    filtered
  }

  def randomSplit(testSize: Double = 0.25): (DataFrame, DataFrame) = {
    val trainRows = Random.shuffle(List.range(0, values.rows)).take((values.rows * (1 - testSize)).toInt)
    val testRows = Range(0, values.rows) diff trainRows
    val trainDF = new DataFrame(columns, values.delete(testRows, Axis._0))
    val testDf = new DataFrame(columns, values.delete(trainRows, Axis._0))
    (trainDF, testDf)
  }

  def this(columns: DenseVector[String], values: DenseMatrix[Double]) {
    this(columns, values, (List(columns.data: _*) zip List(Range(0, columns.length): _*)).toMap)
  }

  def apply(column: String): DenseVector[Double] = {
    if (!columnMap.contains(column)) {
      DenseVector()
    }
    values(::, columnMap(column))
  }

  def dropNa(inplace: Boolean = false): DenseMatrix[Double] = {
    val naRows = (0 until values.rows) intersect values.findAll(_ == Double.MaxValue).map(_._1)
    val filtered = values.delete(naRows, Axis._0)
    if (inplace) {
      values = filtered
    }
    filtered
  }
}

object DataFrame {

  def fromCsv(file: String, spliterator: String = ","): DataFrame = {
    val source = Source.fromFile(file)
    val lineIterator = source.getLines()
    val titles = lineIterator.next().split(spliterator)
    val columns: DenseVector[String] = DenseVector(titles)
    val values: DenseMatrix[Double] = DenseMatrix(lineIterator.toArray
      .map(_.split(spliterator).map(it => if (it.isEmpty) Double.MaxValue else it.toDouble)): _*)
    source.close()
    new DataFrame(columns, values)
  }
}
