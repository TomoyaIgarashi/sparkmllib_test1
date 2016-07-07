import commons._

import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
import org.apache.spark.mllib.recommendation.Rating

import org.specs2._

import scala.util.control.Exception._

import com.typesafe.scalalogging.slf4j.{LazyLogging, StrictLogging}

class CollaborativeFilteringSpec extends Specification with Stoppable with LazyLogging {
  def is =
    s2"""

  Spark CollaborativeFiltering Spec

  CollaborativeFiltering
     meaned squared error                              $meanedSquaredError
  """

  var mse: Double = _
  val sparkConf = new SparkConf()
    .setAppName("CollaborativeFilteringSpec")
    .setMaster("local[1]")
    .setSparkHome(System.getenv("SPARK_HOME"))
  using(new SparkContext(sparkConf)) { sc =>
    val data = sc.textFile("src/test/resources/ratings.csv")
    val ratings = data.map {
      _.split(",")
    }.map { words =>
      val userId = allCatch opt words(0).toInt
      val itemId = allCatch opt words(1).toInt
      val rating = allCatch opt words(2).toDouble
      (userId, itemId, rating)
    }.filter {
      case (Some(_), Some(_), Some(_)) => true
      case _ => false
    }.map { t =>
      val (userId, itemId, rating) = t
      Rating(userId.get, itemId.get, rating.get)
    }
    val rank = 10
    val numIterations = 10
    val model = ALS.train(ratings, rank, numIterations)

    val usersProducts = ratings.map(r => (r.user, r.product))
    val predictions = model.predict(usersProducts).map(r => ((r.user, r.product), r.rating))
    val ratesAndPreds = ratings.map(r => ((r.user, r.product), r.rating)).join(predictions)
    val MSE = ratesAndPreds.map { case ((_, _), (r1, r2)) =>
      val err = r1 - r2
      err * err
    }.mean()
    mse = MSE

    //    model.save(sc, "target/tmp/myCollaborativeFilter")
    //    val sameModel = MatrixFactorizationModel.load(sc, "target/tmp/myCollaborativeFilter")
  }

  def meanedSquaredError = mse must beCloseTo(0.25, 0.01)
}

