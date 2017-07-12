package gttrade.guantang.com.tradeerp.TE02;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.base.MyApplication;
import gttrade.guantang.com.tradeerp.custom.DispatchWebView;
import gttrade.guantang.com.tradeerp.shareprefence.shareprefenceBean;
import gttrade.guantang.com.tradeerp.util.MD5Util;
import gttrade.guantang.com.tradeerp.util.NetWorkTool;


public class TE0204Fragment extends Fragment implements TE02Activity.HtmlbackInterface {

    @BindView(R.id.myWebView)
    DispatchWebView myWebView;
    @BindView(R.id.reloadBtn)
    TextView reloadBtn;

    private WebSettings webSettings;
    private Context context;

    /**
     * errorFlag表示是不是AppError.html页面启动，0表示是，1表示不是。AppError.html打开新页面之后自己应该被清除。
     */
//    private int errorFlag = 1;

    public TE0204Fragment() {
        // Required empty public constructor
    }

    public static TE0204Fragment newInstance() {
        TE0204Fragment fragment = new TE0204Fragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_te0204, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onStop() {
        super.onStop();
        myWebView.stopLoading();
        myWebView.clearCache(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        clearWebViewCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public void clearWebViewCache() {
        // 清除cookie即可彻底清除缓存
        CookieSyncManager.createInstance(context);
        CookieManager.getInstance().removeAllCookie();
    }

    public void init() {

        ((TE02Activity) context).setHtmlbackInterface(this);

        webSettings = myWebView.getSettings();
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);

//		如果设置了此属性，那么webView.getSettings().setSupportZoom(true);也默认设置为true
        webSettings.setBuiltInZoomControls(true);
//		myWebView.setInitialScale(100);
//		myWebView.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。
        myWebView.requestFocus();
        myWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        myWebView.setIntercept(true);
//        myWebView.setScrollContainer(true);
//        myWebView.setVerticalScrollBarEnabled(true);
//        myWebView.setHorizontalScrollBarEnabled(true);
        if (NetWorkTool.checkNetworkState(context)) {
            myWebView.setWebViewClient(webViewClient);
            myWebView.loadUrl(getUrlString());
            //		设置此属性，仅支持双击缩放，不支持触摸缩放（在android4.0是这样，其他平台没试过）
        } else {
//            errorFlag = 0;
            myWebView.setVisibility(View.GONE);
            reloadBtn.setVisibility(View.VISIBLE);
//            myWebView.setWebViewClient(webViewClient);
//            myWebView.loadUrl("file:///android_asset/AppError.html?str=" + getUrlString());
//            myWebView.clearHistory();
//            myWebView.addJavascriptInterface(new JavaScriptToAndroid(),"errorAddLoadInterface");
        }

    }

//    final class JavaScriptToAndroid{
//        @JavascriptInterface
//        public void returnUrl(String str){
//            if (str.equals("reload")){
//                String sssss = getUrlString();
//                myWebView.clearHistory();
//                myWebView.loadUrl(getUrlString());
//                myWebView.reload();
//                myWebView.setWebViewClient(webViewClient);
//            }
//        }
//    }

    WebViewClient webViewClient = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            ((TE02Activity) context).showProgressDialog("正在加载数据");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            ((TE02Activity) context).dismissDialog();

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            }
            return true;
        }

/*        @TargetApi(value=21)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }*/

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                ((TE02Activity) context).dismissDialog();
            }
        }

        @TargetApi(value = 21)
        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((TE02Activity) context).dismissDialog();

            }
        }
    };

    public String getUrlString() {
        StringBuilder sb = new StringBuilder();
        if (MyApplication.getInstance().getSharedPreferences().getInt(shareprefenceBean.LOGINFLAG, 1) == 1) {
            sb.append(CheckHttp(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.SERVERURL, "")) + "/webapp/views/index.aspx?");
            sb.append("Code=" + MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.CONNECTSTR, ""));
            sb.append("&UID=" + MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.USERNAME, ""));
            sb.append("&PWD=" + MD5Util.changeToMD5(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.PASSWORD, "")));
        } else if (MyApplication.getInstance().getSharedPreferences().getInt(shareprefenceBean.LOGINFLAG, 1) == 2) {
            sb.append(CheckHttp(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.TIYANSERVERURL, "")) + "/webapp/views/index.aspx?");
            sb.append("Code=" + MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.CONNECTSTR, ""));
            sb.append("&UID=" + MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.TIYANUSERNAME, ""));
            sb.append("&PWD=" + MD5Util.changeToMD5(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.TIYANPASSWORD, "")));
        }
        return sb.toString();
    }

    public String CheckHttp(String str) {
        if (str != null && str.length() > 7) {
            if (!str.substring(0, 6).equals("http://")) {
                return "http://" + str;
            } else {
                return "";
            }
        } else {
            return "http://" + str;
        }
    }

    @Override
    public boolean htmlbackExecute() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();//返回上一页面
            return false;
        } else {
            return true;
        }
    }


}
