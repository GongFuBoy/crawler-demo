package com.github.gongfuboy.crawler.demo.scala.tools

import java.net.URL

import com.github.gongfuboy.crawler.demo.bean.CompanyInfoBean
import org.openqa.selenium.By
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}

import scala.collection.JavaConversions._

/**
  * Created by ZhouLiMing on 2019/2/12.
  */
object CrawlerTools {

  private lazy val INDEX_URL = "http://www.cninfo.com.cn/new/fulltextSearch?keyWord="

  def main(args: Array[String]): Unit = {
    searchAndAnalyzeInfo("000001公司章程", "平安银行", "000001")
  }

  def analyze(sourceMap: Map[String, String]) = {
    sourceMap.map({
      case (companyCode, companyName) => {
        try {
          searchAndAnalyzeInfo(s"${companyCode}公司章程", companyName, companyCode)
        } catch {
          case _: Throwable => Seq.empty
        }
      }
    }).flatten.toList
  }

  /**
    * 搜索并且分析数据
    * @param keyword
    */
  private def searchAndAnalyzeInfo(keyword: String, companyName: String, companyCode: String) = {
    val driver = chromeDriver
    /**进行搜索, 获取*/
    driver.get(INDEX_URL + keyword)
    new WebDriverWait(driver, 5).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"result-title\"]/div[1]")))
    Thread.sleep(2 * 1000)

    val resultTitleElement = driver.findElement(By.id("result-title"))
    val countString = resultTitleElement.findElement(By.className("page-tabs")).
      findElement(By.className("page-tab-info")).findElements(By.tagName("span")).
      toList.headOption.getOrElse(throw new NullPointerException).getText
    val resultCount = parseNumberFromString(countString)

    val clickTime: Int = resultCount / 10
    val resultTableContent: Seq[(String, String)] = (0 to clickTime).map(count => {
      val result = resultTitleElement.findElement(By.className("page-list")).
        findElement(By.className("page-list-list")).findElement(By.tagName("tbody")).
        findElements(By.tagName("tr")).toList.map(x => {
          val a = x.findElements(By.tagName("td")).get(0).findElement(By.tagName("a"))
          val href = a.getAttribute("href")
          val tableContent = a.findElements(By.tagName("em")).toList.map(_.getText).mkString("")
          val time = x.findElements(By.tagName("td")).get(1).findElement(By.tagName("div")).getText
          (tableContent, href, time)
        }).filter(_._1.contains(companyName)).map(x => (x._2, x._3))
      if (count < resultCount) {
        resultTitleElement.findElement(By.className("page-tabs")).findElement(By.className("page-tabs-list")).
          findElements(By.tagName("li")).get(3).click()
        new WebDriverWait(driver, 5).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"result-title\"]/div[1]")))
        Thread.sleep(2 * 1000)
      }
      result
    }).flatten

    val pdfUrlAndTimeTuples: Seq[(Option[String], String)] = resultTableContent.map({
      case (href, time) => {
        driver.get(href)
        new WebDriverWait(driver, 5).until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div[2]/div/div[1]/div")))
        Thread.sleep(2 * 1000)
        try {
          val pdfDivElement = driver.findElements(By.tagName("div")).toList.filter(_.getAttribute("className") == "page-filedetail-view").head
          val pdfUrl = pdfDivElement.findElements(By.tagName("a")).get(0).getAttribute("href")
          (Some(pdfUrl), time)
        } catch {
          case _ : Throwable => (None, time)
        }
      }
    })

    val result = pdfUrlAndTimeTuples.filter(_._1.isDefined).map(x => (x._1.get, x._2)).map({
      case (pdfURL, time) => (PDFReaderUtils.isUsefulPDF(new URL(pdfURL)), pdfURL, time)
    }).filter(_._1)
    result.map({
      case (_, pdfUrl, time) => {
        val bean = new CompanyInfoBean
        bean.companyCode = companyCode
        bean.companyName = companyName
        bean.time = time
        bean.url = pdfUrl
        bean
      }
    })
  }

  /**
    * 解析获取总数量
    * @param sourceString
    * @return
    */
  private def parseNumberFromString(sourceString: String): Int = {
    val tempString = sourceString.replaceAll("[^0-9]", "")
    if (tempString.isEmpty) {
      0
    } else {
      tempString.toInt
    }
  }


  /**
    * 获取chromeDriver
    */
  private val chromeDriver = {
    System.setProperty("webdriver.chrome.driver", "C:/Program Files (x86)/Google/Chrome/Application/chromedriver.exe")
    val chromeOptions = new ChromeOptions
    // chromeOptions.addArguments("--headless")
    new ChromeDriver(chromeOptions)
  }

}