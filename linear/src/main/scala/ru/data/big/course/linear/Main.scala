package ru.data.big.course.linear

import breeze.linalg.DenseVector

object Main {
  def main(args: Array[String]): Unit = {
    if (args.length != 0) {
      println("The path to the dataset file must be passed as an argument")
    }
    val file = args(0)
    val df = DataFrame.fromCsv(file)
    df.dropNa(inplace = true)
    val carriedSplit = df.randomSplit()
    val xTrain = carriedSplit._1
    val xTest = carriedSplit._2
    val yTrain = xTrain("output")
    val yTest = xTest("output")
    xTrain.drop("output", inplace = true)
    xTest.drop("output", inplace = true)


    val scaler = new StandardScaler()
    val lr = new LinearRegression(scaler.fitTransform(xTrain.values), yTrain, regularisation = NONE(), learningRate = 0.1, maxIterations = 1000).fit()
    println("****Without regularization")
    report(xTrain, xTest, yTrain, yTest, scaler, lr)
    //    On train:
    //      MAE: 8.037022508801302
    //      MSE: 904.6267047540136
    //      RMSE: 30.07701289613072
    //    On test:
    //      MAE: 8.02966048190332
    //      MSE: 799.7871556734862
    //      RMSE: 28.28050840549876
    //    Max 5 features: [comm24 : 9,00] [diff2448 : 6,58] [comm24_1 : 2,50] [commBase : 2,32] [Returns : 1,70]

    println("****Ridge")
    val ridge = new LinearRegression(scaler.fitTransform(xTrain.values), yTrain, regularisation = RIDGE(), learningRate = 0.0001, maxIterations = 1000, precision = 0.0001).fit()
    report(xTrain, xTest, yTrain, yTest, scaler, ridge)
    //    On train:
    //      MAE: 7.084448665177471
    //      MSE: 1173.7533406271748
    //      RMSE: 34.260083780212426
    //    On test:
    //      MAE: 6.572841622463386
    //      MSE: 697.0541958266135
    //      RMSE: 26.401783951593377
    //    Max 5 features: [comm24 : 2,96] [diff2448 : 2,22] [comm24_1 : 1,74] [commBase : 1,65] [Returns : 0,84]

    println("****Lasso")
    val lasso = new LinearRegression(scaler.fitTransform(xTrain.values), yTrain, regularisation = LASSO(), learningRate = 0.01, l1Penalty = 0.1).fit()
    report(xTrain, xTest, yTrain, yTest, scaler, lasso)
    //    On train:
    //      MAE: 9.51628966856073
    //      MSE: 905.5680827997252
    //      RMSE: 30.092658287358482
    //    On test:
    //      MAE: 9.407398259737633
    //      MSE: 1079.5857512807759
    //      RMSE: 32.857050252278825
    //    Max 5 features: [comm24 : 8,17] [commBase : 5,25] [diff2448 : 4,84] [comm24_1 : 4,24] [Returns : 2,96]
  }

  private def report(xTrain: DataFrame,
                     xTest: DataFrame,
                     yTrain: DenseVector[Double],
                     yTest: DenseVector[Double],
                     scaler: StandardScaler,
                     estimator: Estimator[Double]): Unit = {
    println("On train:")
    regressionReport(yTrain, estimator.predict(scaler.transform(xTrain.values)))
    println("On test:")
    regressionReport(yTest, estimator.predict(scaler.transform(xTest.values)))
    printMaxFeatures(xTest, estimator)
  }

  private def printMaxFeatures(xTest: DataFrame, estimator: Estimator[Double]): Unit = {
    print("Max 5 features: ")
    val featureMap = xTest.columnMap.map(t => (t._2, t._1))
    val sortedWeights = estimator.weights().data.zipWithIndex.sortWith((t1, t2) => t1._1 > t2._1).take(5)
    sortedWeights.foreach(t => print(s"[${featureMap(t._2)} : " + f"${t._1}%1.2f] "))
    println(" ")
  }

  private def regressionReport(yTest: DenseVector[Double], yPred: DenseVector[Double]): Unit = {
    val mae = Metric.mae(yPred = yPred, yTrue = yTest)
    val mse = Metric.mse(yPred = yPred, yTrue = yTest)
    val rmse = Metric.rmse(yPred = yPred, yTrue = yTest)
    println(s"MAE: $mae\nMSE: $mse\nRMSE: $rmse")
  }
}
