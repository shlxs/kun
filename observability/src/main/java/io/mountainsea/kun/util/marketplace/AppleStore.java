package io.mountainsea.kun.util.marketplace;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 应用市场API工具类
 *
 * @author : ShaoHongLiang
 * @date : 2022/11/3
 */
public class AppleStore {

    /**
     * 查询 itunes 应用详情，地区跟随系统默认设定
     * @param itunesId
     * @return
     */
    public static AppleSoftwareInfo lookup(String itunesId){
        return lookup(Locale.getDefault().getCountry(), itunesId);
    }

    /**
     * 查询指定地区 itunes 应用详情
     * @param country
     * @param itunesId
     * @return
     */
    public static AppleSoftwareInfo lookup(String country, String itunesId){
        List<AppleSoftwareInfo> list = lookup(country, Arrays.asList(itunesId));
        return list.size() == 1 ? list.get(0) : null;
    }

    /**
     * 查询指定地区一组 itunes 应用详情
     * @param country
     * @param itunesId
     * @return
     */
    public static List<AppleSoftwareInfo> lookup(String country, List<String> itunesId){
        String str = HttpUtil.get("https://itunes.apple.com/lookup?country=" + country + "&id=" + String.join(",", itunesId));

        return JSONObject.parseObject(str)
                .getJSONArray("results")
                .stream()
                .map(elem -> (JSONObject)elem)
                .filter(jsonElement -> Objects.equals("software", jsonElement.get("wrapperType")))
                .map(jsonElement -> jsonElement.toJavaObject(AppleSoftwareInfo.class))
                .map(iTunesSoftwareInfo -> {
                    // 下载地址处理成小写，美观统一
                    String lowerCountry = country.toLowerCase();
                    iTunesSoftwareInfo.setDownloadUrl("https://itunes.apple.com/" + lowerCountry + "/app/id" + iTunesSoftwareInfo.getTrackId());
                    return iTunesSoftwareInfo;
                })
                .collect(Collectors.toList());
    }

}
