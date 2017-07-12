package gttrade.guantang.com.tradeerp.webservice;

import android.os.Handler;
import android.os.Looper;
import android.util.Xml;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import gttrade.guantang.com.tradeerp.base.MyApplication;
import gttrade.guantang.com.tradeerp.shareprefence.shareprefenceBean;
import gttrade.guantang.com.tradeerp.util.MD5Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by luoling on 2016/10/8.
 */
public class MyWebservice {


    public static String CheckHttp(String str) {
        if (str != null && str.length() > 7) {
            if (!str.substring(0, 6).equals("http://")) {
                return "http://";
            } else {
                return "";
            }
        } else {
            return "http://";
        }
    }

    public static Element[] getSoapHeader() {
        Element[] header = new Element[1];
        header[0] = new Element().createElement("http://gtTadeApp.org/",
                "SecurityHeader");
        Element username = new Element().createElement("http://gtTadeApp.org/",
                "UserName");
        Element pass = new Element().createElement("http://gtTadeApp.org/","UserPass");
        if (MyApplication.getInstance().getSharedPreferences().getInt(shareprefenceBean.LOGINFLAG,1)==1){
            username.addChild(Node.TEXT, MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.USERNAME,""));
            pass.addChild(Node.TEXT, MD5Util.changeToMD5(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.PASSWORD,"")));
        }else if (MyApplication.getInstance().getSharedPreferences().getInt(shareprefenceBean.LOGINFLAG,1)==2){//体验账户登录
            username.addChild(Node.TEXT, MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.TIYANUSERNAME,""));
            pass.addChild(Node.TEXT, MD5Util.changeToMD5(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.TIYANPASSWORD,"")));
        }
        header[0].addChild(Node.ELEMENT, username);
        header[0].addChild(Node.ELEMENT, pass);
        return header;
    }

    public static HttpTransportSE serverurlconnection(){
        HttpTransportSE httpTransportation = null;
        if (MyApplication.getInstance().getSharedPreferences().getInt(shareprefenceBean.LOGINFLAG,1)==1){
            httpTransportation = new HttpTransportSE(CheckHttp(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.SERVERURL,""))+MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.SERVERURL,"")+"/gtTradeApp.asmx", 8000);
        }else if (MyApplication.getInstance().getSharedPreferences().getInt(shareprefenceBean.LOGINFLAG,1)==2){
            String  sss = CheckHttp(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.TIYANSERVERURL,""))+MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.TIYANSERVERURL,"")+"/gtTradeApp.asmx";
            httpTransportation = new HttpTransportSE(CheckHttp(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.TIYANSERVERURL,""))+MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.TIYANSERVERURL,"")+"/gtTradeApp.asmx", 8000);
        }
        return httpTransportation;
    }

    private static String checkUsername(){
        if (MyApplication.getInstance().getSharedPreferences().getInt(shareprefenceBean.LOGINFLAG,1)==1){
            return URLEncoder.encode( MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.USERNAME,""));
        }else if (MyApplication.getInstance().getSharedPreferences().getInt(shareprefenceBean.LOGINFLAG,1)==2){//体验账户登录
            return URLEncoder.encode(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.TIYANUSERNAME,""));
        }
        return null;
    }

    private static String checkPassWord(){
        if (MyApplication.getInstance().getSharedPreferences().getInt(shareprefenceBean.LOGINFLAG,1)==1){
            return MD5Util.changeToMD5(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.PASSWORD,""));
        }else if (MyApplication.getInstance().getSharedPreferences().getInt(shareprefenceBean.LOGINFLAG,1)==2){//体验账户登录
            return MD5Util.changeToMD5(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.TIYANPASSWORD,""));
        }
        return null;
    }

    private static String checkCompanyName(){
        if (MyApplication.getInstance().getSharedPreferences().getInt(shareprefenceBean.LOGINFLAG,1)==1){
            return URLEncoder.encode(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.COMPANY,""));
        }else if (MyApplication.getInstance().getSharedPreferences().getInt(shareprefenceBean.LOGINFLAG,1)==2){//体验账户登录
            return URLEncoder.encode(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.TIYANCOMPANY,""));
        }
        return null;
    }

    private static Map<String,String> getHeaderMap(){
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("UserName", checkUsername());
        headerMap.put("UserPass",checkPassWord());
        headerMap.put("CompanyName",checkCompanyName());
        return headerMap;
    }

    private static String getserverUrl(){
        String  url = null;
        if (MyApplication.getInstance().getSharedPreferences().getInt(shareprefenceBean.LOGINFLAG,1)==1){
            url=CheckHttp(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.SERVERURL,""))+MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.SERVERURL,"")+"/gtTradeApp.asmx";
        }else if (MyApplication.getInstance().getSharedPreferences().getInt(shareprefenceBean.LOGINFLAG,1)==2){
            url = CheckHttp(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.TIYANSERVERURL,""))+MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.TIYANSERVERURL,"")+"/gtTradeApp.asmx";
        }
        return url;
    }

    public static MyPostOkHttp doRequestService(String MethodName,Map<String,String> requestBodyMap, Looper looper, AsyncReturnValue asyncReturnValue){
        MyPostOkHttp.MyBuilder myBuilder = new MyPostOkHttp.MyBuilder();
        MyPostOkHttp myPostOkHttp=myBuilder.setConnectTimeout(10,TimeUnit.SECONDS)
                .setReadTimeout(20,TimeUnit.SECONDS)
                .setWriteTimeout(20,TimeUnit.SECONDS)
                .setHeaderMap(getHeaderMap())
                .setRequestBodyMap(requestBodyMap)
                .setUrl(getserverUrl()+"/"+MethodName)
                .bulid();
        myPostOkHttp.startRequest(looper,asyncReturnValue);

        return myPostOkHttp;
    }

//    public static String GetPageIndexOrder(){
//        SoapObject soapObject = new SoapObject("http://gtTadeApp.org/", "GetPageIndexOrder");
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        envelope.bodyOut = soapObject;
//        envelope.dotNet = true;
//        envelope.encodingStyle = "UTF-8";
//        HttpTransportSE httpTransportation = serverurlconnection();
//        httpTransportation.debug = true;
//        Element[] header = getSoapHeader();
//        envelope.headerOut = header;
//
//        try {
//            httpTransportation.call("http://gtTadeApp.org/GetPageIndexOrder", envelope);
//            Object result = envelope.getResponse();
//            if(result!=null && !result.toString().equals("anyType{}")){
//                return result.toString();
//            }else{
//                return "";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }



//    public static String GetStatistics(int statisticstype){
//        SoapObject soapObject = new SoapObject("http://gtTadeApp.org/", "GetStatistics");
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        soapObject.addProperty("statisticstype", statisticstype);
//        envelope.bodyOut = soapObject;
//        envelope.dotNet = true;
//        envelope.encodingStyle = "UTF-8";
//        HttpTransportSE httpTransportation = serverurlconnection();
//        httpTransportation.debug = true;
//        Element[] header = getSoapHeader();
//        envelope.headerOut = header;
//        try {
//            httpTransportation.call("http://gtTadeApp.org/GetStatistics", envelope);
//            Object result = envelope.getResponse();
//            if(result!=null && !result.toString().equals("anyType{}")){
//                return result.toString();
//            }else{
//                return "";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }



//    public static String GetTypeOrders(String topnum, String orderid, String searchno,String ordertype ){
//        SoapObject soapObject = new SoapObject("http://gtTadeApp.org/", "GetTypeOrders");
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        soapObject.addProperty("topnum", topnum);
//        soapObject.addProperty("orderid", orderid);
//        soapObject.addProperty("searchno", searchno);
//        soapObject.addProperty("ordertype",ordertype);
//        envelope.bodyOut = soapObject;
//        envelope.dotNet = true;
//        envelope.encodingStyle = "UTF-8";
//        HttpTransportSE httpTransportation = serverurlconnection();
//        httpTransportation.debug = true;
//        Element[] header = getSoapHeader();
//        envelope.headerOut = header;
//        try {
//            httpTransportation.call("http://gtTadeApp.org/GetTypeOrders", envelope);
//            Object result = envelope.getResponse();
//            if(result!=null && !result.toString().equals("anyType{}")){
//                return result.toString();
//            }else{
//                return "";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }

//    public static String GetOrderDetail(String orderid){
//        SoapObject soapObject = new SoapObject("http://gtTadeApp.org/", "GetOrderDetail");
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        soapObject.addProperty("orderid", orderid);
//        envelope.bodyOut = soapObject;
//        envelope.dotNet = true;
//        envelope.encodingStyle = "UTF-8";
//        HttpTransportSE httpTransportation = serverurlconnection();
//        httpTransportation.debug = true;
//        Element[] header = getSoapHeader();
//        envelope.headerOut = header;
//        try {
//            httpTransportation.call("http://gtTadeApp.org/GetOrderDetail", envelope);
//            Object result = envelope.getResponse();
//            if(result!=null && !result.toString().equals("anyType{}")){
//                return result.toString();
//            }else{
//                return "";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();//            return "";
//        } catch (XmlPullParserException e) {

//            e.printStackTrace();
//            return "";
//        }
//    }

//    public static String GetRecipients(String orderid){
//        SoapObject soapObject = new SoapObject("http://gtTadeApp.org/", "GetRecipients");
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        soapObject.addProperty("orderid", orderid);
//        envelope.bodyOut = soapObject;
//        envelope.dotNet = true;
//        envelope.encodingStyle = "UTF-8";
//        HttpTransportSE httpTransportation = serverurlconnection();
//        httpTransportation.debug = true;
//        Element[] header = getSoapHeader();
//        envelope.headerOut = header;
//        try {
//            httpTransportation.call("http://gtTadeApp.org/GetRecipients", envelope);
//            Object result = envelope.getResponse();
//            if(result!=null && !result.toString().equals("anyType{}")){
//                return result.toString();
//            }else{
//                return "";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }

//    public static String GetItems(String topnum,String sku,String itemid){
//        SoapObject soapObject = new SoapObject("http://gtTadeApp.org/", "GetItems");
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        soapObject.addProperty("topnum", topnum);
//        soapObject.addProperty("sku", sku);
//        soapObject.addProperty("itemid", itemid);
//        envelope.bodyOut = soapObject;
//        envelope.dotNet = true;
//        envelope.encodingStyle = "UTF-8";
//        HttpTransportSE httpTransportation = serverurlconnection();
//        httpTransportation.debug = true;
//        Element[] header = getSoapHeader();
//        envelope.headerOut = header;
//        try {
//            httpTransportation.call("http://gtTadeApp.org/GetItems", envelope);
//            Object result = envelope.getResponse();
//            if(result!=null && !result.toString().equals("anyType{}")){
//                return result.toString();
//            }else{
//                return "";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }

//    public static String GetItemsDetails(String itemid){
//        SoapObject soapObject = new SoapObject("http://gtTadeApp.org/", "GetItemsDetails");
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        soapObject.addProperty("itemid", itemid);
//        envelope.bodyOut = soapObject;
//        envelope.dotNet = true;
//        envelope.encodingStyle = "UTF-8";
//        HttpTransportSE httpTransportation = serverurlconnection();
//        httpTransportation.debug = true;
//        Element[] header = getSoapHeader();
//        envelope.headerOut = header;
//        try {
//            httpTransportation.call("http://gtTadeApp.org/GetItemsDetails", envelope);
//            Object result = envelope.getResponse();
//            if(result!=null && !result.toString().equals("anyType{}")){
//                return result.toString();
//            }else{
//                return "";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }

//    public static String GetPickItems(String topnum,String itemid){
//        SoapObject soapObject = new SoapObject("http://gtTadeApp.org/", "GetPickItems");
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        soapObject.addProperty("topnum", topnum);
//        soapObject.addProperty("itemid", itemid);
//        envelope.bodyOut = soapObject;
//        envelope.dotNet = true;
//        envelope.encodingStyle = "UTF-8";
//        HttpTransportSE httpTransportation = serverurlconnection();
//        httpTransportation.debug = true;
//        Element[] header = getSoapHeader();
//        envelope.headerOut = header;
//        try {
//            httpTransportation.call("http://gtTadeApp.org/GetPickItems", envelope);
//            Object result = envelope.getResponse();
//            if(result!=null && !result.toString().equals("anyType{}")){
//                return result.toString();
//            }else{
//                return "";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }

//    public static String SubmitPickItems(String commitData){
//        SoapObject soapObject = new SoapObject("http://gtTadeApp.org/", "SubmitPickItems");
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        soapObject.addProperty("returndata", commitData);
//        envelope.bodyOut = soapObject;
//        envelope.dotNet = true;
//        envelope.encodingStyle = "UTF-8";
//        HttpTransportSE httpTransportation = serverurlconnection();
//        httpTransportation.debug = true;
//        Element[] header = getSoapHeader();
//        envelope.headerOut = header;
//        try {
//            httpTransportation.call("http://gtTadeApp.org/SubmitPickItems", envelope);
//            Object result = envelope.getResponse();
//            if(result!=null && !result.toString().equals("anyType{}")){
//                return result.toString();
//            }else{
//                return "";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }

    public static String Login(String url,String companyname,String username,String password){
        SoapObject soapObject = new SoapObject("http://gtTadeApp.org/", "Login");
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapObject.addProperty("companyname", companyname);
        soapObject.addProperty("username", username);
        soapObject.addProperty("password", MD5Util.changeToMD5(password));
        envelope.bodyOut = soapObject;
        envelope.dotNet = true;
        envelope.encodingStyle = "UTF-8";
        HttpTransportSE httpTransportation = new HttpTransportSE( CheckHttp(url)+url+"/gtTradeApp.asmx", 8000);
        httpTransportation.debug = true;
        try {
            httpTransportation.call("http://gtTadeApp.org/Login", envelope);
            Object result = envelope.getResponse();
            if(result!=null && !result.toString().equals("anyType{}")){
                return result.toString();
            }else{
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String ApkUpdate(int version,String Apktype){
        SoapObject soapObject = new SoapObject("http://tempuri.org/", "ApkUpdate");
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapObject.addProperty("version", version);
        soapObject.addProperty("Apktype", Apktype);
        envelope.bodyOut = soapObject;
        envelope.dotNet = true;
        envelope.encodingStyle = "UTF-8";
        HttpTransportSE httpTransportation = new HttpTransportSE("http://www3.gtcangku.com/CheckUpdate.asmx", 8000);
        httpTransportation.debug = true;
        try {
            httpTransportation.call("http://tempuri.org/ApkUpdate", envelope);
            Object result = envelope.getResponse();
            if(result!=null && !result.toString().equals("anyType{}")){
                return result.toString();
            }else{
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return "";
        }
    }

//    public static String GetOrderItemInfo(String orderid){
//        SoapObject soapObject = new SoapObject("http://gtTadeApp.org/", "GetOrderItemInfo");
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        soapObject.addProperty("orderid", orderid);
//        envelope.bodyOut = soapObject;
//        envelope.dotNet = true;
//        envelope.encodingStyle = "UTF-8";
//        HttpTransportSE httpTransportation = serverurlconnection();
//        httpTransportation.debug = true;
//        Element[] header = getSoapHeader();
//        envelope.headerOut = header;
//        try {
//            httpTransportation.call("http://gtTadeApp.org/GetOrderItemInfo", envelope);
//            Object result = envelope.getResponse();
//            if(result!=null && !result.toString().equals("anyType{}")){
//                return result.toString();
//            }else{
//                return "";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }

//    public static String GetStorage(){
//        SoapObject soapObject = new SoapObject("http://gtTadeApp.org/", "GetStorage");
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        envelope.bodyOut = soapObject;
//        envelope.dotNet = true;
//        envelope.encodingStyle = "UTF-8";
//        HttpTransportSE httpTransportation = serverurlconnection();
//        httpTransportation.debug = true;
//        Element[] header = getSoapHeader();
//        envelope.headerOut = header;
//        try {
//            httpTransportation.call("http://gtTadeApp.org/GetStorage", envelope);
//            Object result = envelope.getResponse();
//            if(result!=null && !result.toString().equals("anyType{}")){
//                return result.toString();
//            }else{
//                return "";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }

//    public static String GetStorageRack(String storageid){
//        SoapObject soapObject = new SoapObject("http://gtTadeApp.org/", "GetStorageRack");
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        soapObject.addProperty("storageid",storageid);
//        envelope.bodyOut = soapObject;
//        envelope.dotNet = true;
//        envelope.encodingStyle = "UTF-8";
//        HttpTransportSE httpTransportation = serverurlconnection();
//        httpTransportation.debug = true;
//        Element[] header = getSoapHeader();
//        envelope.headerOut = header;
//        try {
//            httpTransportation.call("http://gtTadeApp.org/GetStorageRack", envelope);
//            Object result = envelope.getResponse();
//            if(result!=null && !result.toString().equals("anyType{}")){
//                return result.toString();
//            }else{
//                return "";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }

//    public static String GetCatalogue (){
//        SoapObject soapObject = new SoapObject("http://gtTadeApp.org/", "GetCatalogue");
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        envelope.bodyOut = soapObject;
//        envelope.dotNet = true;
//        envelope.encodingStyle = "UTF-8";
//        HttpTransportSE httpTransportation = serverurlconnection();
//        httpTransportation.debug = true;
//        Element[] header = getSoapHeader();
//        envelope.headerOut = header;
//        try {
//            httpTransportation.call("http://gtTadeApp.org/GetCatalogue", envelope);
//            Object result = envelope.getResponse();
//            if(result!=null && !result.toString().equals("anyType{}")){
//                return result.toString();
//            }else{
//                return "";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }

//    public static String GetWaitCheck(String jsonc){
//        SoapObject soapObject = new SoapObject("http://gtTadeApp.org/", "GetWaitCheck");
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        soapObject.addProperty("jsonc",jsonc);
//        envelope.bodyOut = soapObject;
//        envelope.dotNet = true;
//        envelope.encodingStyle = "UTF-8";
//        HttpTransportSE httpTransportation = serverurlconnection();
//        httpTransportation.debug = true;
//        Element[] header = getSoapHeader();
//        envelope.headerOut = header;
//        try {
//            httpTransportation.call("http://gtTadeApp.org/GetWaitCheck", envelope);
//            Object result = envelope.getResponse();
//            if(result!=null && !result.toString().equals("anyType{}")){
//                return result.toString();
//            }else{
//                return "";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }

//    public static String ConfirmCheck(String jonc){
//        SoapObject soapObject = new SoapObject("http://gtTadeApp.org/", "ConfirmCheck");
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        soapObject.addProperty("jonc",jonc);
//        envelope.bodyOut = soapObject;
//        envelope.dotNet = true;
//        envelope.encodingStyle = "UTF-8";
//        HttpTransportSE httpTransportation = serverurlconnection();
//        httpTransportation.debug = true;
//        Element[] header = getSoapHeader();
//        envelope.headerOut = header;
//        try {
//            httpTransportation.call("http://gtTadeApp.org/ConfirmCheck", envelope);
//            Object result = envelope.getResponse();
//            if(result!=null && !result.toString().equals("anyType{}")){
//                return result.toString();
//            }else{
//                return "";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }

//    public static String ScanSend(String scanjc){
//        SoapObject soapObject = new SoapObject("http://gtTadeApp.org/","ScanSend");
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        soapObject.addProperty("scanjc",scanjc);
//        envelope.bodyOut = soapObject;
//        envelope.dotNet = true;
//        envelope.encodingStyle = "UTF-8";
//        HttpTransportSE httpTransportation = serverurlconnection();
//        httpTransportation.debug = true;
//        Element[] header = getSoapHeader();
//        envelope.headerOut = getSoapHeader();
//        try {
//            httpTransportation.call("http://gtTadeApp.org/ScanSend", envelope);
//            Object result = envelope.getResponse();
//            if (result!=null && !result.toString().equals("anyType{}")){
//                return result.toString();
//            }else{
//                return "";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            return "";
//        }
//
//    }

//    public static String SendItem(String sendjson){
//        SoapObject soapObject = new SoapObject("http://gtTadeApp.org/","SendItem");
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        soapObject.addProperty("sendjson",sendjson);
//        envelope.bodyOut = soapObject;
//        envelope.dotNet = true;
//        envelope.encodingStyle = "UTF-8";
//        HttpTransportSE httpTransportation = serverurlconnection();
//        httpTransportation.debug = true;
//        Element[] header = getSoapHeader();
//        envelope.headerOut = getSoapHeader();
//        try {
//            httpTransportation.call("http://gtTadeApp.org/SendItem", envelope);
//            Object result = envelope.getResponse();
//            if (result!=null && !result.toString().equals("anyType{}")){
//                return result.toString();
//            }else{
//                return "";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }

}
