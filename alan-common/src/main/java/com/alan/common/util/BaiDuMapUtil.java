package com.alan.common.util;

import com.alan.common.pojo.MapDistance;
import com.alan.common.pojo.MapGeoCode;
import com.alan.common.pojo.MapQuery;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 百度地图API
 *
 * @author: alan
 * @Date: 2018/5/2 16:54
 * @Description:
 */
public class BaiDuMapUtil {
    private final static String URL_SEARCH = "http://api.map.baidu.com/place/v2/search?";
    private final static String URL_DISTANCE = "http://api.map.baidu.com/routematrix/v2/";
    private final static String URL_GEO_CODE = "http://api.map.baidu.com/geocoder/v2/?";
    private final static String AK = "OHTDAltG7RuMrVC4thY7gxGA65YS9YKG";
    public final static String OUTPUT_JSON = "json";
    public final static String OUTPUT_XML = "xml";

    /**
     * 驾车
     */
    public final static String DRIVING = "driving?";
    /**
     * 骑行
     */
    public final static String RIDING = "riding?";
    /**
     * 步行
     */
    public final static String WALKING = "walking?";

    /**
     * 行政区划区域检索
     *
     * @param search   检索关键字 不同关键字间以$符号分隔
     * @param tag      检索分类，与q组合进行检索，多个分类以","分隔
     * @param region   行政区划名或对应cityCode 北京/131
     * @param output   输出格式为json或者xml
     * @param pageNo   分页页码，默认为0
     * @param pageSize 单次召回POI数量，默认为10条记录，最大返回20条
     * @return
     */
    public static List<MapQuery> search(String search, String tag, String region, String output, Integer pageNo, Integer pageSize) {
        if (StringUtils.isBlank(search) || StringUtils.isBlank(region)) {
            return null;
        }
        StringBuffer sb = new StringBuffer(URL_SEARCH);
        sb.append("query=").append(search);
        if (!StringUtils.isBlank(tag)) {
            sb.append("&tag=").append(tag);
        }
        if (null != pageNo) {
            sb.append("&page_num=").append(pageNo);
        }
        if (null != pageSize) {
            sb.append("&page_size=").append(pageSize);
        }
        sb.append("&region=").append(region);
        if (StringUtils.isBlank(output)) {
            output = OUTPUT_JSON;
        }
        sb.append("&output=").append(output);
        sb.append("&ak=").append(AK);
        List<MapQuery> list = getMapQueries(sb, output);
        if (list != null) {
            return list;
        }
        return null;
    }


    /**
     * 周边检索
     *
     * @param search   检索关键字 不同关键字间以$符号分隔
     * @param lat      纬度值
     * @param lng      经度值
     * @param radius   周边检索半径，单位为米 (超出区域范围则查找当前区域内) 默认1000
     * @param filter   筛选条件  sort_name:distance|sort_rule:1 (需要设置scope=2)
     * @param output   输出格式为json或者xml 默认json
     * @param pageNo   分页页码，默认为0
     * @param pageSize 单次召回POI数量，默认为10条记录，最大返回20条
     * @return
     */
    public static List<MapQuery> search(String search, String lat, String lng, String radius, String filter, String output, Integer pageNo, Integer pageSize) {
        if (StringUtils.isBlank(search) || StringUtils.isBlank(lat) || StringUtils.isBlank(lng)) {
            return null;
        }
        StringBuffer sb = new StringBuffer(URL_SEARCH);
        sb.append("query=").append(search);
        if (!StringUtils.isBlank(filter)) {
            sb.append("&scope=2&filter=").append(filter);
        }
        if (null != pageNo) {
            sb.append("&page_num=").append(pageNo);
        }
        if (null != pageSize) {
            sb.append("&page_size=").append(pageSize);
        }
        sb.append("&location=").append(lat + "," + lng);
        if (StringUtils.isBlank(radius)) {
            radius = "1000";
        }
        sb.append("&radius=").append(radius);
        if (StringUtils.isBlank(output)) {
            output = OUTPUT_JSON;
        }
        sb.append("&output=").append(output);
        sb.append("&ak=").append(AK);
        List<MapQuery> list = getMapQueries(sb, output);
        if (list.size() > 0) {
            return list;
        }
        return null;
    }

    /**
     * 矩形区域检索
     *
     * @param search   检索关键字 不同关键字间以$符号分隔
     * @param tag      检索分类， 多个分类以","分隔
     * @param bounds   检索矩形区域，多组坐标间以","分隔 左上,右下
     * @param output   输出格式为json或者xml 默认 json
     * @param pageNo   分页页码，默认为0
     * @param pageSize 单次召回POI数量，默认为10条记录，最大返回20条
     * @return
     */
    public static List<MapQuery> search(String search, String tag, String[] bounds, String output, Integer pageNo, Integer pageSize) {
        if (StringUtils.isBlank(search) || bounds.length < 2) {
            return null;
        }
        StringBuffer sb = new StringBuffer(URL_SEARCH);
        sb.append("query=").append(search);
        if (null != pageNo) {
            sb.append("&page_num=").append(pageNo);
        }
        if (null != pageSize) {
            sb.append("&page_size=").append(pageSize);
        }
        if (!StringUtils.isBlank(tag)) {
            sb.append("&tag=").append(tag);
        }
        sb.append("&bounds=").append(bounds[0] + "," + bounds[1]);
        if (StringUtils.isBlank(output)) {
            output = OUTPUT_JSON;
        }
        sb.append("&output=").append(output);
        sb.append("&ak=").append(AK);
        List<MapQuery> list = getMapQueries(sb, output);
        if (list.size() > 0) {
            return list;
        }
        return null;
    }

    /**
     * 执行请求api获取数据
     *
     * @param sb
     * @param output
     * @return
     */
    private static List<MapQuery> getMapQueries(StringBuffer sb, String output) {
        InputStream in = null;
        try {
            URL u = new URL(sb.toString());
            in = u.openStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int read = 0;
            while ((read = in.read(buf)) > 0) {
                out.write(buf, 0, read);
            }
            byte b[] = out.toByteArray();
            String result = new String(b, "utf-8");
            List<MapQuery> list = new ArrayList<>();
            switch (output) {
                case OUTPUT_JSON:
                    readJson(result, list);
                    break;
                case OUTPUT_XML:
                    readXML(result, list);
                    break;
                default:
                    break;
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 解析json字符串
     *
     * @param result
     * @param list
     */
    private static void readJson(String result, List<MapQuery> list) {
        Map json = JsonUtils.jsonToObject(result, Map.class);
        List results = (List) json.get("results");
        for (Object temp : results) {
            Map map = (Map) temp;
            String name = (String) map.get("name");
            String address = (String) map.get("address");
            String province = (String) map.get("province");
            String city = (String) map.get("city");
            String area = (String) map.get("area");
            String uid = (String) map.get("uid");
            String phone = (String) map.get("phone");
            Map location = (Map) map.get("location");
            String lat = String.valueOf(location.get("lat"));
            String lng = String.valueOf(location.get("lng"));
            MapQuery mapQuery = new MapQuery();
            mapQuery.setName(name);
            mapQuery.setAddress(address);
            mapQuery.setCity(city);
            mapQuery.setProvince(province);
            mapQuery.setArea(area);
            mapQuery.setUid(uid);
            mapQuery.setPhone(phone);
            mapQuery.setLat(lat);
            mapQuery.setLng(lng);
            list.add(mapQuery);
        }
    }

    /**
     * 解析xml字符串
     *
     * @param result
     * @param list
     */
    private static void readXML(String result, List<MapQuery> list) {
        try {
            Document document = DocumentHelper.parseText(result);
            //获取根节点
            Element root = document.getRootElement();
            //获取子节点results
            Element results = root.element("results");
            //获取results的result子节点集合
            List<Element> resultList = results.elements("result");
            for (Element temp : resultList) {
                Element name = temp.element("name");
                Element address = temp.element("address");
                Element province = temp.element("province");
                Element city = temp.element("city");
                Element area = temp.element("area");
                Element uid = temp.element("uid");
                Element phone = temp.element("phone");
                Element location = temp.element("location");
                Element lat = null, lng = null;
                if (null != location) {
                    lat = location.element("lat");
                    lng = location.element("lng");
                }
                MapQuery mapQuery = new MapQuery();
                mapQuery.setName(null == name ? "" : name.getText());
                mapQuery.setAddress(null == address ? "" : address.getText());
                mapQuery.setCity(null == city ? "" : city.getText());
                mapQuery.setProvince(null == province ? "" : province.getText());
                mapQuery.setArea(null == area ? "" : area.getText());
                mapQuery.setUid(null == uid ? "" : uid.getText());
                mapQuery.setPhone(null == phone ? "" : phone.getText());
                mapQuery.setLat(null == lat ? "" : lat.getText());
                mapQuery.setLng(null == lng ? "" : lng.getText());
                list.add(mapQuery);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测距
     *
     * @param traffic      交通
     * @param origins      起点坐标 以"|"分割
     * @param destinations 终点坐标 以"|"分割 ps 起终点乘积不得大于50
     * @param tactics      路线规划 (只作用于driving) 10 不走高速 11 常规路线 12 最短路线计算路况 13 最短路线不计算路况
     */
    public static List<MapDistance> distance(String traffic, String origins, String destinations, String tactics) {
        if (StringUtils.isBlank(traffic) || StringUtils.isBlank(origins) || StringUtils.isBlank(destinations)) {
            return null;
        }
        if (50 < origins.split("\\|").length * destinations.split("\\|").length) {
            return null;
        }
        StringBuffer sb = new StringBuffer(URL_DISTANCE);
        sb.append(traffic);
        sb.append("output=").append(OUTPUT_JSON);
        sb.append("&origins=").append(origins);
        sb.append("&destinations=").append(destinations);
        if (DRIVING.equals(traffic) && !StringUtils.isBlank(tactics)) {
            sb.append("&tactics=").append(tactics);
        }
        sb.append("&ak=").append(AK);
        InputStream in = null;
        try {
            URL u = new URL(sb.toString());
            in = u.openStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int read = 0;
            while ((read = in.read(buf)) > 0) {
                out.write(buf, 0, read);
            }
            byte b[] = out.toByteArray();
            String result = new String(b, "utf-8");
            List<MapDistance> list = new ArrayList<>();
            Map json = JsonUtils.jsonToObject(result, Map.class);
            List results = (List) json.get("result");
            for (Object temp : results) {
                Map map = (Map) temp;
                Map distance = (Map) map.get("distance");
                Map duration = (Map) map.get("duration");
                MapDistance mapDistance = new MapDistance();
                mapDistance.setDistanceText((String) distance.get("text"));
                mapDistance.setDistanceValue((Integer) distance.get("value"));
                mapDistance.setDurationText((String) duration.get("text"));
                mapDistance.setDurationValue((Integer) duration.get("value"));
                list.add(mapDistance);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 根据地址获取坐标
     *
     * @param address
     * @return
     */
    public static MapGeoCode convertLocationByAddress(String address) {
        if (StringUtils.isBlank(address)) {
            return null;
        }
        MapGeoCode mapGeoCode = new MapGeoCode(address);
        StringBuffer sb = new StringBuffer(URL_GEO_CODE);
        sb.append("address=").append(address);
        sb.append("&output=").append(OUTPUT_JSON);
        sb.append("&ak=").append(AK);
        geoCode(mapGeoCode, sb, 0);
        return mapGeoCode;
    }

    /**
     * 根据坐标获取地址
     *
     * @param lat
     * @param lng
     * @return
     */
    public static MapGeoCode convertAddressByLocation(String lat, String lng) {
        if (StringUtils.isBlank(lat) || StringUtils.isBlank(lng)) {
            return null;
        }
        MapGeoCode mapGeoCode = new MapGeoCode(lat, lng);
        StringBuffer sb = new StringBuffer(URL_GEO_CODE);
        sb.append("location=").append(lat + "," + lng);
        sb.append("&output=").append(OUTPUT_JSON);
        sb.append("&ak=").append(AK);
        geoCode(mapGeoCode, sb, 1);
        return mapGeoCode;
    }

    /**
     * 执行api请求解析数据
     *
     * @param mapGeoCode
     * @param sb
     * @param type       0 根据地址解析 1 根据坐标解析
     */
    public static void geoCode(MapGeoCode mapGeoCode, StringBuffer sb, int type) {
        InputStream in = null;
        try {
            URL u = new URL(sb.toString());
            in = u.openStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int read = 0;
            while ((read = in.read(buf)) > 0) {
                out.write(buf, 0, read);
            }
            byte b[] = out.toByteArray();
            String result = new String(b, "utf-8");
            Map json = JsonUtils.jsonToObject(result, Map.class);
            Map map = (Map) json.get("result");
            if (type == 1) {
                mapGeoCode.setAddress((String) map.get("formatted_address"));
                //解析地区信息
                Map component = (Map) map.get("addressComponent");
                mapGeoCode.setProvince(String.valueOf(component.get("province")));
                mapGeoCode.setCity(String.valueOf(component.get("city")));
                mapGeoCode.setDistrict(String.valueOf(component.get("district")));
            } else {
                //解析经纬度
                Map location = (Map) map.get("location");
                mapGeoCode.setLat(String.valueOf(location.get("lat")));
                mapGeoCode.setLng(String.valueOf(location.get("lng")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void main(String[] args) throws Exception {
//        ProxyUtil.ocProxy();
        List<MapQuery> list = null;
        List<MapDistance> listArrr = null;
        //周边检索
        list = search("地铁", "22.542073", "114.036773", "400", "sort_name:distance|sort_rule:1", OUTPUT_JSON, null, null);
        String destinations = "";
        for (MapQuery temp : list) {
            destinations += temp.getLat() + "," + temp.getLng() + "|";
        }
        if (!StringUtils.isBlank(destinations)) {
            destinations = destinations.substring(0, destinations.length() - 1);
            listArrr = distance(WALKING, "22.542302,114.03612", destinations, null);

        }

    }
}
