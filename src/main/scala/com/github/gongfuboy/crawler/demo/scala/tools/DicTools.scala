package com.github.gongfuboy.crawler.demo.scala.tools

import scala.io.Source

/**
  * Created by ZhouLiMing on 2019/2/12.
  */
object DicTools {

  /**
    * 字典数据加载
    */
  private lazy val DIC_MAP: Map[String, String] = {
    val dicFilePath = "/default.dic"
    Source.fromInputStream(getClass.getResourceAsStream(dicFilePath)).getLines().map(x => {
      val strings = x.split("\t")
      (strings(0), strings(1))
    }).toMap
  }

  def main(args: Array[String]): Unit = {
    println(DIC_MAP.size)
  }

  /**
    * 获取字典数据
    *
    * @return
    */
  def getDicMap = DIC_MAP

}