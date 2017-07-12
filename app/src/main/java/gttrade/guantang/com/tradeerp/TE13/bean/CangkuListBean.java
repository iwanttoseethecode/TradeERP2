package gttrade.guantang.com.tradeerp.TE13.bean;

import java.util.List;

/**
 * Created by luoling on 2017/3/10.
 */

public class CangkuListBean {

    /**
     * Status : 1
     * data : [{"ID":1,"Name":"ebay亘邦仓"},{"ID":2,"Name":"淘宝亘邦仓"},{"ID":3,"Name":"万邑通美西仓"},{"ID":4,"Name":"中邮美西仓"},{"ID":5,"Name":"进口亘邦仓"},{"ID":6,"Name":"进口京东仓"},{"ID":7,"Name":"进口京东香港仓"},{"ID":8,"Name":"收藏亘邦仓"},{"ID":9,"Name":"收藏赵涌仓"},{"ID":10,"Name":"收藏易金仓"},{"ID":11,"Name":"收藏NGC评级"},{"ID":12,"Name":"收藏PCGS评级"},{"ID":13,"Name":"收藏PMG评级"},{"ID":14,"Name":"收藏源泰评级"},{"ID":15,"Name":"万邑通英国仓"},{"ID":16,"Name":"收藏广州V6竞投仓"},{"ID":17,"Name":"收藏天津广福泉钞"},{"ID":18,"Name":"北京阿诚哥微拍仓"},{"ID":19,"Name":"李闫纸币微拍仓"},{"ID":20,"Name":"中邮美东仓"},{"ID":21,"Name":"扬州苏吉仓"},{"ID":22,"Name":"天堂阳光仓"},{"ID":23,"Name":"海外仓中灏国际"},{"ID":24,"Name":"中邮在途仓"},{"ID":25,"Name":"万邑通在途仓"},{"ID":26,"Name":"收藏京东仓"},{"ID":27,"Name":"瑕疵品仓"}]
     * Message : 获取数据成功
     */

    private int Status;
    private String Message;
    private List<DataBean> data;

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * ID : 1
         * Name : ebay亘邦仓
         */

        private int ID;
        private String Name;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }
    }
}

