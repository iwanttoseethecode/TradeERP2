package gttrade.guantang.com.tradeerp.TE12.bean;

import java.util.List;

/**
 * Created by luoling on 2017/3/9.
 */

public class FilterOrdersInitialDataBean {


    /**
     * Status : 1
     * Data : {"Platform":["淘宝","eBay","AliExpress","Amazon","其他","阿里巴巴"],"eShop":[{"ID":4,"Name":"速卖通"},{"ID":6,"Name":"super_speed_motors"},{"ID":7,"Name":"xkd_2013"},{"ID":8,"Name":"shkamuer0609  "},{"ID":9,"Name":" shsnas_888"},{"ID":10,"Name":"shsnas_999"},{"ID":11,"Name":"shkamuer0608"},{"ID":12,"Name":"ap_tech2014"},{"ID":13,"Name":"brand_franchise998"},{"ID":14,"Name":"vw-parts-88"},{"ID":15,"Name":"wwfautoparts"},{"ID":16,"Name":"sxh-autoparts"},{"ID":17,"Name":"ebay沙箱"},{"ID":18,"Name":"淘宝"}],"Shipment":["E邮宝  线下","E邮宝","Fedex IP/IE","中速TNT","E特快","顺丰","国际e包裹","美国 USPS","美国 UPS 标准","UPS 3日达","UPS 1 日达","DHL 德国","邮政小包","E邮宝线上","E邮宝线下","燕文专线","顺丰(电子面单)","EMS(电子面单)","圆通(电子面单)","中通(电子面单)","申通(电子面单)","百世快递(电子面单)","韵达(电子面单)","德邦(电子面单)","宅急送(电子面单)","优速(电子面单)","跨越速运(电子面单)","EMS国际","信丰快递(电子面单)","京东(电子面单)","全峰快递(电子面单)","22","DHL"],"Country":["安道尔","阿富汗","安提瓜和巴布达","安圭拉","阿尔巴尼亚","亚美尼亚","荷属安的列斯","安哥拉","南极洲","阿根廷","美属萨摩亚","奥地利","澳大利亚","阿鲁巴","奥兰群岛","阿塞拜疆","波黑","巴巴多斯","孟加拉","比利时","保加利亚","巴林","布隆迪","贝宁","圣巴泰勒米岛","百慕大","文莱","玻利维亚","巴西","不丹","布韦岛","博茨瓦纳","白俄罗斯","伯利兹","加拿大","科科斯群岛","刚果（金）","中非","刚果（布）","瑞士","库克群岛","智利","喀麦隆","中国","哥伦比亚","哥斯达黎加","古巴","佛得角","圣诞岛","塞浦路斯","捷克","德国","吉布提","丹麦","多米尼克","多米尼加","阿尔及利亚","厄瓜多尔","爱沙尼亚","埃及","西撒哈拉","厄立特里亚","西班牙","埃塞俄比亚","芬兰","斐济群岛","马尔维纳斯群岛（福克兰）","密克罗尼西亚联邦","法罗群岛","法国","加蓬","格林纳达","格鲁吉亚","法属圭亚那","根西岛","加纳","直布罗陀","格陵兰","冈比亚","几内亚","瓜德罗普","赤道几内亚","希腊","南乔治亚岛和南桑威奇群岛","危地马拉","关岛","几内亚比绍","圭亚那","香港","赫德岛和麦克唐纳群岛","洪都拉斯","克罗地亚","海地","匈牙利","印尼","爱尔兰","以色列","马恩岛","印度","英属印度洋领地","伊拉克","伊朗","冰岛","意大利","泽西岛","牙买加","约旦","日本","肯尼亚","吉尔吉斯斯坦","基里巴斯","科摩罗","圣基茨和尼维斯","朝鲜","韩国","科威特","开曼群岛","哈萨克斯坦","老挝","黎巴嫩","圣卢西亚","列支敦士登","斯里兰卡","利比里亚","莱索托","立陶宛","卢森堡","拉脱维亚","利比亚","摩洛哥","摩纳哥","摩尔多瓦","黑山","法属圣马丁","马达加斯加","马绍尔群岛","马其顿","马里","蒙古","北马里亚纳群岛","马提尼克","毛里塔尼亚","蒙塞拉特岛","马耳他","毛里求斯","马尔代夫","马拉维","墨西哥","马来西亚","莫桑比克","纳米比亚","新喀里多尼亚","尼日尔","诺福克岛","尼日利亚","尼加拉瓜","荷兰","挪威","尼泊尔","瑙鲁","纽埃","新西兰","阿曼","巴拿马","秘鲁","法属波利尼西亚","巴布亚新几内亚","菲律宾","巴基斯坦","波兰","皮特凯恩群岛","波多黎各","巴勒斯坦","葡萄牙","帕劳","巴拉圭","卡塔尔","罗马尼亚","塞尔维亚","俄罗斯","卢旺达","沙特阿拉伯","所罗门群岛","塞舌尔","苏丹","瑞典","新加坡","斯洛文尼亚","斯洛伐克","塞拉利昂","圣马力诺","塞内加尔","索马里","苏里南","圣多美和普林西比","萨尔瓦多","叙利亚","斯威士兰","特克斯和凯科斯群岛","乍得","法属南部领地","多哥","泰国","塔吉克斯坦","托克劳","东帝汶","土库曼斯坦","突尼斯","汤加","土耳其","特立尼达和多巴哥","台湾","图瓦卢","坦桑尼亚","乌克兰","乌干达","美国本土外小岛屿","美国","乌拉圭","乌兹别克斯坦","梵蒂冈","圣文森特和格林纳丁斯","委内瑞拉","英属维尔京群岛","美属维尔京群岛","越南","瓦努阿图","瓦利斯和富图纳","萨摩亚","也门","马约特","南非","赞比亚","津巴布韦","英国","阿联酋","布基纳法索","巴哈马","柬埔寨","缅甸","澳门","圣皮埃尔与密克隆","留尼汪岛","圣赫勒拿岛","斯瓦尔巴特和扬马延岛","英国"],"Attribute":["已支付","已检查","配货中","已发货","待出库","已退货","未支付","已作废","已退货","邮局退包","SKU异常"],"Storage":[{"ID":1,"Name":"成都库"},{"ID":2,"Name":"美国库"},{"ID":3,"Name":"德国库"}]}
     * Message : 获取数据成功
     */

    private int Status;
    private DataBean Data;
    private String Message;

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public DataBean getData() {
        return Data;
    }

    public void setData(DataBean Data) {
        this.Data = Data;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public static class DataBean {
        private List<String> Platform;
        private List<EShopBean> eShop;
        private List<String> Shipment;
        private List<String> Country;
        private List<String> Attribute;
        private List<StorageBean> Storage;

        public List<String> getPlatform() {
            return Platform;
        }

        public void setPlatform(List<String> Platform) {
            this.Platform = Platform;
        }

        public List<EShopBean> getEShop() {
            return eShop;
        }

        public void setEShop(List<EShopBean> eShop) {
            this.eShop = eShop;
        }

        public List<String> getShipment() {
            return Shipment;
        }

        public void setShipment(List<String> Shipment) {
            this.Shipment = Shipment;
        }

        public List<String> getCountry() {
            return Country;
        }

        public void setCountry(List<String> Country) {
            this.Country = Country;
        }

        public List<String> getAttribute() {
            return Attribute;
        }

        public void setAttribute(List<String> Attribute) {
            this.Attribute = Attribute;
        }

        public List<StorageBean> getStorage() {
            return Storage;
        }

        public void setStorage(List<StorageBean> Storage) {
            this.Storage = Storage;
        }

        public static class EShopBean {
            /**
             * ID : 4
             * Name : 速卖通
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

        public static class StorageBean {
            /**
             * ID : 1
             * Name : 成都库
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
}
