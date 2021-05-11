name := "BigDataML"

version := "0.1"

scalaVersion := "2.13.5"

lazy val global = project
  .in(file("."))
  .aggregate(
    linear,
    spark
  )

lazy val linear = project
  .settings(
    name := "linear",
    libraryDependencies ++= commonDependencies
  )

lazy val spark = project
  .settings(
    name := "spark",
    libraryDependencies ++= commonDependencies
  )

lazy val dependencies = new {
  val breezeV = "1.1"
  val breezeNativesV = "1.1"
  val breezeVizV = "1.1"
  val scalaTestV = "3.0.8"
  val sparkV = "3.1.1"

  val breeze = "org.scalanlp" %% "breeze" % breezeV
  val breezeNatives = "org.scalanlp" %% "breeze-natives" % breezeNativesV
  val breezeViz = "org.scalanlp" %% "breeze-viz" % breezeVizV
  val scalaTest = "org.scalatest" %% "scalatest" % scalaTestV % Test
  val sparkCore = "org.apache.spark" %% "spark-core" % sparkV
  val sparkMllib = "org.apache.spark" %% "spark-mllib" % sparkV
  val sparkSql = "org.apache.spark" %% "spark-sql" % sparkV
}

lazy val commonDependencies = Seq(
  dependencies.breeze,
  dependencies.breezeNatives,
  dependencies.breezeViz,
  dependencies.scalaTest,
  dependencies.sparkCore,
  dependencies.sparkMllib,
  dependencies.sparkSql,
)