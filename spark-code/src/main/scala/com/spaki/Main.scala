package com.spaki

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.slf4j.LoggerFactory


object Main {
  val LOGGER = LoggerFactory.getLogger(this.getClass)
  lazy val spark: SparkSession = {
    SparkSession.builder()
      .appName("app")
      // nice for debugging locally
      // and should be disable once about to submit to dataproc
      // .master("local") 
      .getOrCreate()
  }

  def readTextFile(path: String, spark: SparkSession): DataFrame = {
    spark.read.text(path)
  }

  def main(args: Array[String]): Unit = {
    import spark.implicits._
    val textFile = args(0)
    try {
      val df = readTextFile(textFile, spark)
      df.cache()
      df.createOrReplaceGlobalTempView("TextFile")

      val lines = spark.sql("select count(*) as totalLine from global_temp.TextFile")
      lines.show()

      val shorts = spark.sql("select substr(value, 0, 100) as short from global_temp.TextFile")
      shorts.show()

    } catch {
      case e: Throwable => {
        LOGGER.error("spark job failed")
        throw e
      }
    } finally {
      spark.stop()
    }
  }
}

