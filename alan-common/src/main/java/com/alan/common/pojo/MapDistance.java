package com.alan.common.pojo;

/**
 * @author alan
 * @descript:地图测距
 * @date 2018/5/14
 */
public class MapDistance {
    /**
     * 线路距离的文本描述 文本描述的单位有米、公里两种
     */
    private String distanceText;

    /**
     * 线路距离的数值 数值的单位为米。若没有计算结果，值为0
     */
    private Integer distanceValue;

    /**
     * 路线耗时的文本描述 文本描述的单位有分钟、小时两种。
     */
    private String durationText;

    /**
     * 路线耗时的数值  数值的单位为秒。若没有计算结果，值为0
     */
    private Integer durationValue;


    public String getDistanceText() {
        return distanceText;
    }

    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }

    public Integer getDistanceValue() {
        return distanceValue;
    }

    public void setDistanceValue(Integer distanceValue) {
        this.distanceValue = distanceValue;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public Integer getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(Integer durationValue) {
        this.durationValue = durationValue;
    }
}
