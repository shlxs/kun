import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.hash.Hashing;

import java.util.*;
import java.util.function.Consumer;


/**
 * APP ID  8AB8E18ACD4A40D5B287BD97AB22E09B
 * Secret  080E9746C2A94C80BB28EB0CC88C73FD
 * marketing api对接文档：
 * https://docs.qq.com/doc/DRmZYdnl6bG5kTlVa?&u=163aee1dc0b74cf3b43418320e6f8a35
 *
 *
 * @author : ShaoHongLiang
 * @date : 2022/12/7
 */
public class BilibiliSample {

    public static void main(String[] args) {
        // 1135376: C21646F9545649AAA04C51B0E246576B
        // 1135369: 25CC49C7CD6B465FABA3C70929F97EA4

        // 获取账户列表，记得分页
//        Map<String, Object> map = new HashMap<>();
//        map.put("page", 1);
//        map.put("size", 1000);
//        signBySecret(map);
//        get("/open_api/v2/auth/account_ids", map);

        // 刷新Token
//        Map<String, Object> map = new HashMap<>();
//        map.put("account_id", 1135369);
//        signBySecret(map);
//        put("/open_api/v2/auth/refresh_token", map);

//        // 查看创意投放数据（新）
        Map<String, Object> map = new HashMap<>();
        map.put("account_id", 1135369);
        map.put("start_time", "20221205");
        map.put("end_time", "20221205");
        map.put("size", 20);
        signByToken(map);
        JSONObject result = get("/open_api/report/v3/creative", map);

//        double cost = 0;
//        double click_count = 0;
//        JSONArray jsonArray = result.getJSONObject("result").getJSONArray("data");
//        for (Object o : jsonArray){
//            JSONObject creativeReport = ((JSONObject)o);
//            cost += ((JSONObject)o).getDouble("cost");
//            click_count += ((JSONObject)o).getDouble("click_count");
//        }
//        System.out.println(cost);
//        System.out.println(click_count);

        // 查看完整创意投放数据，分页size有bug
//        Map<String, Object> map2 = new HashMap<>();
//        map2.put("account_id", 1135369);
//        map2.put("start_time", "20221205");
//        map2.put("end_time", "20221205");
//        map2.put("size", 1000);
//        signByToken(map2);
//        JSONObject result2 = get("/open_api/report/v2/complete/creative", map2);
//        double cost2 = 0;
//        double click_count2 = 0;
//        JSONArray jsonArray2 = result2.getJSONObject("result").getJSONArray("data");
//        for (Object o : jsonArray2){
//            cost2 += ((JSONObject)o).getDouble("cost");
//            click_count2 += ((JSONObject)o).getDouble("click_count");
//        }
//        System.out.println(cost2);
//        System.out.println(click_count2);

        // 批量查询账号消耗数据，签名校验无法通过
//        Map<String, Object> map2 = new HashMap<>();
//        map2.put("account_id", 1135369);
//        map2.put("account_ids", 1135369);
//        map2.put("start_time", "20221205");
//        map2.put("end_time", "20221205");
//        signByToken(map2);
//        JSONObject result2 = get("/open_api/report/v3/account/consume_data", map2);

        // 查看广告账户投放数据
//        Map<String, Object> map2 = new HashMap<>();
//        map2.put("time_type", 3);
//        map2.put("account_id", 1135369);
//        map2.put("start_time", "20221205");
//        map2.put("end_time", "20221207");
//        signByToken(map2);
//        JSONObject result2 = get("/open_api/report/v2/advertiser", map2);
    }

    private static JSONObject put(String s, Map<String, Object> map) {
        String result = HttpRequest.put("https://cm.bilibili.com/takumi/api" + s).body(JSONObject.toJSONString(map)).contentType("application/json").execute().body();
        System.out.println(result);
        return JSONObject.parseObject(result);
    }

    private static JSONObject get(String s, Map<String, Object> map) {
        String result = HttpUtil.get("https://cm.bilibili.com/takumi/api" + s, map);
        System.out.println(result);
        return JSONObject.parseObject(result);
    }

    private static void signBySecret(Map<String, Object> map) {
        map.put("appkey", "8AB8E18ACD4A40D5B287BD97AB22E09B");
        map.put("ts", System.currentTimeMillis());
        map.remove("sign");
        map.put("sign", getSign(map, "080E9746C2A94C80BB28EB0CC88C73FD"));
    }

    private static void signByToken(Map<String, Object> map) {
        map.put("appkey", "8AB8E18ACD4A40D5B287BD97AB22E09B");
        map.put("ts", System.currentTimeMillis());
        map.remove("sign");
        map.put("sign", getSign(map, "6C552269ADCE4E9CBBCC8D1BCB20A102"));
    }

    public static String getSign(Map<String, Object> map, String token) {
        List<String> params = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getKey().equals("sign")) {
                continue;
            }
            params.add(entry.getKey() + "=" +
                    JSONObject.toJSONString(entry.getValue()));
        }
        Collections.sort(params);
        StringBuilder sb = new StringBuilder();
        sb.append(Joiner.on("&").join(params));
        sb.append(token);
        String str = sb.toString().replaceAll("\"", "");
        return Hashing.md5().newHasher().putString(str,
                Charsets.UTF_8).hash().toString();
    }
}
