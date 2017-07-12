package gttrade.guantang.com.tradeerp.webservice;

import android.os.Looper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luoling on 2017/3/1.
 */

public abstract class MyOkHttpExecute implements AsyncReturnValue{

    protected Map<String,String> requestBodyMap = new HashMap<String,String>();
    protected MyPostOkHttp mMyPostOkHttp;


    public void startWork(Map<String,String> requestBodyMap){
        this.requestBodyMap = requestBodyMap;
        mMyPostOkHttp = executeNetWork();
    }

    protected MyPostOkHttp dealwith(String MethodName, Looper looper){
        return MyWebservice.doRequestService(MethodName,requestBodyMap,looper,this);
    }

    protected abstract MyPostOkHttp executeNetWork();

    public void cancel(){
        mMyPostOkHttp.cancelRequest();
    }

    @Override
    public abstract void getNetValue(String JsonString);

    @Override
    public abstract void faild(String error);
}
