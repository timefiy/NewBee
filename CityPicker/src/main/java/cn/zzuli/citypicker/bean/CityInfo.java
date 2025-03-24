package cn.zzuli.citypicker.bean;

import java.util.List;

/**
 * Description: 类的描述
 */
public class CityInfo {

    /**
     * id : 110000
     * name : 北京市
     * cityList : [{"id":"110000","name":"省直辖县级行政单位","cityList":[{"id":"110101","name":"东城区"},{
     * "id":"110102","name":"西城区"},{"id":"110105","name":"朝阳区"},{"id":"110106","name":"丰台区"},{"id
     * ":"110107","name":"石景山区"},{"id":"110108","name":"海淀区"},{"id":"110109","name":"门头沟区"},{"id
     * ":"110111","name":"房山区"},{"id":"110112","name":"通州区"},{"id":"110113","name":"顺义区"},{"id
     * ":"110114","name":"昌平区"},{"id":"110115","name":"大兴区"},{"id":"110116","name":"怀柔区"},{"id
     * ":"110117","name":"平谷区"},{"id":"110118","name":"密云区"},{"id":"110119","name":"延庆区"}]}]
     * gisBd09Lat : 0
     * gisBd09Lng : 0
     * gisGcj02Lat : 0
     * gisGcj02Lng : 0
     * pinYin : HongKong
     */

    public String id;
    public String              name;
    public int                 gisBd09Lat;
    public int                 gisBd09Lng;
    public int                 gisGcj02Lat;
    public int                 gisGcj02Lng;
    public String              pinYin;
    public List<CityListBeanX> cityList;

    public static class CityListBeanX {
        /**
         * id : 110000
         * name : 省直辖县级行政单位
         * cityList : [{"id":"110101","name":"东城区"},{"id":"110102","name":"西城区"},{"id":"110105",
         * "name":"朝阳区"},{"id":"110106","name":"丰台区"},{"id":"110107","name":"石景山区"},{"id":"110108
         * ","name":"海淀区"},{"id":"110109","name":"门头沟区"},{"id":"110111","name":"房山区"},{"id
         * ":"110112","name":"通州区"},{"id":"110113","name":"顺义区"},{"id":"110114","name":"昌平区"},{
         * "id":"110115","name":"大兴区"},{"id":"110116","name":"怀柔区"},{"id":"110117","name":"平谷区"},
         * {"id":"110118","name":"密云区"},{"id":"110119","name":"延庆区"}]
         */

        public String id;
        public String             name;
        public List<CityListBean> cityList;

        public static class CityListBean {
            /**
             * id : 110101
             * name : 东城区
             */

            public String id;
            public String name;
        }
    }
}
