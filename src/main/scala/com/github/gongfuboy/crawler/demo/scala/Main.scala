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
    val companyInfoBeans = CrawlerTools.analyze(dicMap)
    ExcelFileDownloadUtils.createHSSFWorkbook(companyInfoBeans, 1000,
      new FileOutputStream(new File("D:\\crawler-demo-report.xls")))
  }

}