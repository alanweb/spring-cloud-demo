package com.alan.common.pojo;

/**
 * @author alan
 * @descript:地图地址转码
 * @date 2018/5/16
 */
public class MapGeoCode {
    /**
     * 纬度值
     */
    private String lat;
    /**
     * 经度值
     */
    private String lng;
    /**
     * 地址
     */
    private String address;

    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city ;
    /**
     * 区
     */
    private String district;

    public MapGeoCode(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public MapGeoCode(String address) {
        this.address = address;
    }

    public MapGeoCode() {
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
