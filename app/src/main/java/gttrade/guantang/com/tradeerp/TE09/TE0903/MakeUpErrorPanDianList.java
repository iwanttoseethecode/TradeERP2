package gttrade.guantang.com.tradeerp.TE09.TE0903;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gttrade.guantang.com.tradeerp.TE09.PanDianDatabaseOperation;

/**
 * Created by luoling on 2017/1/23.
 *
 * 这个类主要将账面数量发生改变的盘点货品和服务端账面数量进行拼凑，然后将拼凑的数据返回。
 */

public class MakeUpErrorPanDianList {

    private String jsonArraySstring;
    private PanDianDatabaseOperation panDianDatabaseOperation;

    public MakeUpErrorPanDianList(String jsonArraySstring,PanDianDatabaseOperation panDianDatabaseOperation) {
        this.jsonArraySstring = jsonArraySstring;
        this.panDianDatabaseOperation = panDianDatabaseOperation;
    }


    /*
    * 返回集合中的map的键是 ID,ItemSKU,ItemName,ItemID,Storage,Position,Stock,CheckNum,PicUrl_Small,netStock(服务端返回的正确账面数)
    * */
    public List<Map<String,Object>> makeUpList(){
        LinkedList<Map<String,Object>> loaclList =panDianDatabaseOperation.getPanDianErrorHPList(jsonArraySstring);
        return matchData(loaclList);
    }

    private List<Map<String,Object>> matchData(LinkedList<Map<String,Object>> loaclList){

        List<Map<String,Object>> makedUpList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonArraySstring);
            int length = jsonArray.length();

            while(loaclList.size()>0){

                Map<String,Object> map = loaclList.poll();

                for (int i = 0; i < length; i++) {
                    String ItemID = jsonArray.getJSONObject(i).getString("ItemID");
                    String netStock = jsonArray.getJSONObject(i).getString("Stock");
                    if (map.get("ItemID").toString().equals(ItemID)){
                        map.put("netStock",netStock);
                        makedUpList.add(map);
                        break;
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return makedUpList;
    }

}
