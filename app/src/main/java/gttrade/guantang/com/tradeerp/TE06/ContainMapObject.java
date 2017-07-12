package gttrade.guantang.com.tradeerp.TE06;

import java.util.Map;

/**
 * Created by luoling on 2016/10/21.
 */
public class ContainMapObject {
    private Map<String,Object> map;
    /**
     * 让上一级容器判断是否要删除map，false 不删除，true 删除
     * */
    private boolean deleteFlag;

    public ContainMapObject(Map<String, Object> map, boolean deleteFlag) {
        this.map = map;
        this.deleteFlag = deleteFlag;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public boolean isDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}
