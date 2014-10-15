import commons._

import org.apache.spark._
import org.apache.spark.mllib.stat._
import org.apache.spark.mllib.stat.correlation._
import org.apache.spark.SparkContext._

import org.specs2._

class SparkMLlibCorrelationSpec extends Specification with Stoppable { def is = s2"""

  Spark SQL From File

  Correlation
    correlation1                               $correlation1
  """

  // 気温とビールの出荷数
  val xData = Array(12.1, 15.3, 18.6, 21.7, 26.1, 32.1)
  val yData = Array(45.0, 520.0, 2864.0, 6874.0, 25487.0, 102870.0)

  var retCorrelation1: Double = _
  using(new SparkContext("local[1]", "SparkMLlibCorrelationSpec", System.getenv("SPARK_HOME"))) { sc =>
    val x = sc.parallelize(xData)
    val y = sc.parallelize(yData)
    retCorrelation1 = Statistics.corr(x, y, "pearson")
  }
  // この相関係数を算出しているページ:http://dekiru.net/article/4576/
  def correlation1 = retCorrelation1 must_== 0.8633475827899126
}

