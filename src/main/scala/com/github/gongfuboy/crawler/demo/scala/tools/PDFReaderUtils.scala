package com.github.gongfuboy.crawler.demo.scala.tools

import java.net.URL

import org.apache.commons.io.IOUtils
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper

/**
  * Created by ZhouLiMing on 2019/2/12.
  */
object PDFReaderUtils {

  def main(args: Array[String]): Unit = {
    val fileUrl = "http://static.cninfo.com.cn/finalpage/2018-11-23/1205627159.PDF"
    val uri = new URL(fileUrl)
    println(isUsefulPDF(uri))
  }

  private lazy val KEY_WORD = List("党")

  /**
    * 是否是一个有用的
    * @param url
    * @return
    */
  def isUsefulPDF(url: URL) = {
    val stream = url.openStream()
    val document = PDDocument.load(stream)
    val textStripper = new PDFTextStripper()
    val pdfString = textStripper.getText(document)
    KEY_WORD.exists(x => {
      pdfString.contains(x)
    })
  }

}