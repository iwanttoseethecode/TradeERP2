package gttrade.guantang.com.tradeerp.webservice;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by luoling on 2017/3/1.
 * 对Okhttp的post网络请求
 */

public class MyPostOkHttp {

    private static final String TAG = "luoling";
    private OkHttpClient mOkHttpClient;

    private Request request;

    private Call call;

    private Handler mHandler ;

    private AsyncReturnValue mAsyncReturnValue;


    private MyPostOkHttp(){
        super();
    }

    private MyPostOkHttp(Request request,OkHttpClient okHttpClient) {
        this.request = request;
        mOkHttpClient = okHttpClient;
        call = mOkHttpClient.newCall(request);
    }


    private void initHandler(Looper looper){
        mHandler = new Handler(looper){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case -1:
                        mAsyncReturnValue.faild(msg.obj.toString());
                        break;
                    case 1:
                        mAsyncReturnValue.getNetValue(msg.obj.toString());
                        break;
                }
            }
        };
    }

    /**
     * 开始网络请求
     * @param looper 对应线程的Looper，如果非ui线程，请初始化一个Looper传入到该方法中
     */
    public void startRequest(Looper looper,AsyncReturnValue asyncReturnValue){
        if (call == null){
            throw new RuntimeException("call is null,please builder the MyBuilder");
        }

        mAsyncReturnValue = asyncReturnValue;

        initHandler(looper);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = Message.obtain();
                msg.what = -1;
                msg.obj = e.getMessage();
                mHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = xmlPrase(response);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     *
     * 根据自己项目放回结果解析xml获取里面的Json数据
     * @param response
     */
    private String xmlPrase(Response response){

        String responseString = "";
        try {
            if(response.isSuccessful()){
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(response.body().byteStream(), "utf-8");
                int eventType = parser.getEventType();//返回当前项类型,如:START_TAG(开始标签), END_TAG(结束标签), TEXT(文本内容), etc.)
                while(eventType != XmlPullParser.END_DOCUMENT){
                    switch(eventType){
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG:
                            if(parser.getName().equals("string")){
                                eventType = parser.next();
                                if(eventType == parser.TEXT) {
                                    responseString=parser.getText();
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                    }
                    eventType = parser.next();
                }

            }else{
                throw new IOException("Unexpected code "+request);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responseString;
    }


    /**
     * 请求停止网络请求
     */
    public void cancelRequest() {
        if (call == null){
            return;
        }
        call.cancel();
    }



    public static final class MyBuilder{

        private OkHttpClient okHttpClient;

        private Map<String,String> requestBodyMap = new HashMap<String,String>();
        private Map<String,String> HeaderMap = new HashMap<String,String>();
        private int connectTimeout;
        private TimeUnit connectTimeoutType;
        private int readTimeout;
        private TimeUnit readTimeoutType;
        private int writeTimeout;
        private TimeUnit writeTimeoutType;
        private String url;

        public MyBuilder(){
            okHttpClient = new OkHttpClient();
        }

        public Map<String, String> getHeaderMap() {
            return HeaderMap;
        }

        public MyBuilder setHeaderMap(Map<String, String> headerMap) {
            HeaderMap = headerMap;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public MyBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Map<String, String> getRequestBodyMap() {
            return requestBodyMap;
        }

        public MyBuilder setRequestBodyMap(Map<String, String> requestBodyMap) {
            this.requestBodyMap = requestBodyMap;
            return this;
        }


        public int getConnectTimeout() {
            return connectTimeout;
        }

        public MyBuilder setConnectTimeout(int connectTimeout,TimeUnit time) {
            this.connectTimeout = connectTimeout;
            this.connectTimeoutType = time;
            return this;
        }

        public int getReadTimeout() {
            return readTimeout;
        }

        public MyBuilder setReadTimeout(int readTimeout,TimeUnit time) {
            this.readTimeout = readTimeout;
            this.readTimeoutType = time;
            return this;
        }

        public int getWriteTimeout() {
            return writeTimeout;
        }

        public MyBuilder setWriteTimeout(int writeTimeout,TimeUnit time) {
            this.writeTimeout = writeTimeout;
            this.writeTimeoutType = time;
            return this;
        }


        public MyPostOkHttp bulid(){


            OkHttpClient.Builder OkHttpClientBuilder = okHttpClient.newBuilder();
            OkHttpClientBuilder.connectTimeout(this.connectTimeout,this.connectTimeoutType)
                    .readTimeout(this.readTimeout,this.readTimeoutType)
                    .writeTimeout(this.writeTimeout,this.writeTimeoutType)
                    .build();

            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            if (requestBodyMap != null){
                Set<String> requestBodyKey=requestBodyMap.keySet();
                for (Iterator<String> it = requestBodyKey.iterator(); it.hasNext() ; ) {
                    String key = it.next();
                    formBodyBuilder.add(key,requestBodyMap.get(key));
                }
            }
            RequestBody requestBody = formBodyBuilder.build();

            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.post(requestBody);
            requestBuilder.url(url);

            Set<String> headerKey=HeaderMap.keySet();
            for (Iterator<String> it = headerKey.iterator(); it.hasNext() ; ) {
                String key = it.next();
                requestBuilder.addHeader(key,HeaderMap.get(key));
            }

            Request request = requestBuilder.build();

            return new MyPostOkHttp(request,okHttpClient);
        }
    }
}
