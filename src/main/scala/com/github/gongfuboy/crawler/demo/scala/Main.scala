package com.github.gongfuboy.crawler.demo.scala

import java.io.{File, FileOutputStream}

import com.github.gongfuboy.crawler.demo.scala.tools.{CrawlerTools, DicTools}
import com.github.gongfuboy.utils.excel.ExcelFileDownloadUtils

import scala.collection.JavaConversions._

/**
  * Created by ZhouLiMing on 2019/2/12.
  */
object Main {

  def main(args: Array[String]): Unit = {
    val dicMap = DicTools.getDicMap
    val stringToStrings: Iterator[Map[String, String]] = dicMap.sliding(400, 400)
    stringToStrings.zipWithIndex.foreach({
      case (source, index) => {
        new Thread() {
          override def run(): Unit = {
            val driver = CrawlerTools.chromeDriver
            val companyInfoBeans = CrawlerTools.analyze(source, driver)
            ExcelFileDownloadUtils.createHSSFWorkbook(companyInfoBeans, 1000,
              new FileOutputStream(new File(s"D:\\crawler-demo-report${index}.xls")))
          }
        }.start()
      }
    })
  }

}