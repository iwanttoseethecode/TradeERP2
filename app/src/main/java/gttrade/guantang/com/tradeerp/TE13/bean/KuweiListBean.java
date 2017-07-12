package gttrade.guantang.com.tradeerp.TE13.bean;

import java.util.List;

/**
 * Created by luoling on 2017/3/10.
 */

public class KuweiListBean {

    /**
     * Status : 1
     * data : [{"ID":1,"storageID":"1         ","RackName":"C001"}]
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
         * storageID : 1
         * RackName : C001
         */

        private int ID;
        private String storageID;
        private String RackName;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getStorageID() {
            return storageID;
        }

        public void setStorageID(String storageID) {
            this.storageID = storageID;
        }

        public String getRackName() {
            return RackName;
        }

        public void setRackName(String RackName) {
            this.RackName = RackName;
        }
    }
}
