package com.gtafe.assetsmanage.beans;

import java.io.Serializable;

/**
 * Created by ZhouJF on 2018/1/8.
 */

public class GetInstrumentInfo implements Serializable {

    private static final long serialVersionUID=2;
    /**
     * Data : {"InstrumentNum":"TY20171114001","InstrumentName":"示波器","Instrumenttype":"MK009","Specification":"3.2 GSa/s 12-bit ","Price":10000,"Content":"<p><a href=\"http://www.keysight.com/zh-CN/pd-2670205-pn-M9203A/pxie-12-bit-high-speed-digitizer-wideband-digital-receiver?nid=-35502.1171760&cc=CN&lc=chi\"><strong>M9203A PXIe 高速数字化仪/宽带数据接收机，12 位，3.2 GS/s，FPGA 信号处理\t\t\t<\/strong><\/a><\/p>","CountryCodeName":null,"PurposeName":"教学为主","InstrumentCategoryName":"混合结构","ClassEncoding":"01010100","SourceName":"购置","StatusCodeName":"在用","PurchaseDate":"2017-06-08T00:00:00","PurchasePrice":2000,"ManagerName":"陈金 实验室管理"}
     * IsSucess : true
     * Msg :
     */

    private DataBean Data;
    private boolean IsSucess;
    private String Msg;

    public DataBean getData() {
        return Data;
    }

    public void setData(DataBean Data) {
        this.Data = Data;
    }

    public boolean isIsSucess() {
        return IsSucess;
    }

    public void setIsSucess(boolean IsSucess) {
        this.IsSucess = IsSucess;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }

    public static class DataBean implements Serializable {
        /**
         * InstrumentNum : TY20171114001
         * InstrumentName : 示波器
         * Instrumenttype : MK009
         * Specification : 3.2 GSa/s 12-bit
         * Price : 10000
         * Content : <p><a href="http://www.keysight.com/zh-CN/pd-2670205-pn-M9203A/pxie-12-bit-high-speed-digitizer-wideband-digital-receiver?nid=-35502.1171760&cc=CN&lc=chi"><strong>M9203A PXIe 高速数字化仪/宽带数据接收机，12 位，3.2 GS/s，FPGA 信号处理			</strong></a></p>
         * CountryCodeName : null
         * PurposeName : 教学为主
         * InstrumentCategoryName : 混合结构
         * ClassEncoding : 01010100
         * SourceName : 购置
         * StatusCodeName : 在用
         * PurchaseDate : 2017-06-08T00:00:00
         * PurchasePrice : 2000
         * ManagerName : 陈金 实验室管理
         */

        private String InstrumentNum;
        private String InstrumentName;
        private String Instrumenttype;
        private String Specification;
        private int Price;
        private String Content;
        private Object CountryCodeName;
        private String PurposeName;
        private String InstrumentCategoryName;
        private String ClassEncoding;
        private String SourceName;
        private String StatusCodeName;
        private String PurchaseDate;
        private int PurchasePrice;
        private String ManagerName;
        private String RFID;

        public String getInstrumentNum() {
            return InstrumentNum;
        }

        public void setInstrumentNum(String InstrumentNum) {
            this.InstrumentNum = InstrumentNum;
        }

        public String getInstrumentName() {
            return InstrumentName;
        }

        public void setInstrumentName(String InstrumentName) {
            this.InstrumentName = InstrumentName;
        }

        public String getInstrumenttype() {
            return Instrumenttype;
        }

        public void setInstrumenttype(String Instrumenttype) {
            this.Instrumenttype = Instrumenttype;
        }

        public String getSpecification() {
            return Specification;
        }

        public void setSpecification(String Specification) {
            this.Specification = Specification;
        }

        public int getPrice() {
            return Price;
        }

        public void setPrice(int Price) {
            this.Price = Price;
        }

        public String getContent() {
            return Content;
        }

        public void setContent(String Content) {
            this.Content = Content;
        }

        public Object getCountryCodeName() {
            return CountryCodeName;
        }

        public void setCountryCodeName(Object CountryCodeName) {
            this.CountryCodeName = CountryCodeName;
        }

        public String getPurposeName() {
            return PurposeName;
        }

        public void setPurposeName(String PurposeName) {
            this.PurposeName = PurposeName;
        }

        public String getInstrumentCategoryName() {
            return InstrumentCategoryName;
        }

        public void setInstrumentCategoryName(String InstrumentCategoryName) {
            this.InstrumentCategoryName = InstrumentCategoryName;
        }

        public String getClassEncoding() {
            return ClassEncoding;
        }

        public void setClassEncoding(String ClassEncoding) {
            this.ClassEncoding = ClassEncoding;
        }

        public String getSourceName() {
            return SourceName;
        }

        public void setSourceName(String SourceName) {
            this.SourceName = SourceName;
        }

        public String getStatusCodeName() {
            return StatusCodeName;
        }

        public void setStatusCodeName(String StatusCodeName) {
            this.StatusCodeName = StatusCodeName;
        }

        public String getPurchaseDate() {
            return PurchaseDate;
        }

        public void setPurchaseDate(String PurchaseDate) {
            this.PurchaseDate = PurchaseDate;
        }

        public int getPurchasePrice() {
            return PurchasePrice;
        }

        public void setPurchasePrice(int PurchasePrice) {
            this.PurchasePrice = PurchasePrice;
        }

        public String getManagerName() {
            return ManagerName;
        }

        public void setManagerName(String ManagerName) {
            this.ManagerName = ManagerName;
        }

        public String getRFID() {

            return RFID;
        }

        public void setRFID(String RFID) {
            this.RFID = RFID;
        }
    }
}
