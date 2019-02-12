package com.github.gongfuboy.crawler.demo.bean;

import com.github.gongfuboy.utils.excel.Description;

/**
 * Created by ZhouLiMing on 2019/2/12.
 */
public class CompanyInfoBean {

    @Description("证券代码")
    public String companyCode;

    @Description("证券简称")
    public String companyName;

    @Description("章程日期")
    public String time;

    @Description("章程内容")
    public String url;

}
