package gttrade.guantang.com.tradeerp.TE11Activty.TE1101;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by luoling on 2017/2/15.
 */

public class MyIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MyIntentService(String name) {
        super(name);
    }

    public MyIntentService(){
        this("intentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getDataString();
        String data = downLoad(url);
        Intent intent1 = new Intent("wuxingzhaungbizuiweizhiming");
        intent1.putExtra("baidu",data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
    }

    private String downLoad(String localUrlString){
        try {
            URL url = new URL(localUrlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream in = conn.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while((len = in.read(bytes))!=-1){
                bos.write(bytes,0,len);
            }
            in.close();
            return new String(bos.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
