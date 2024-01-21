package com.imgyh;

import org.lionsoul.ip2region.xdb.Searcher;

import java.io.IOException;

/**
 * @Description : ip转地址工具类
 * @Author : imgyh
 * @Date : 2024/1/17 19:31
 * @Version : v1.0
 **/
public class Ip2regionUtils {
    private static final String dbPath = "src/main/resources/ip2region/ip2region.xdb";
    private static final byte[] cBuff;
    private static final Searcher searcher;

    static {
        try {
            // 1、从 dbPath 加载整个 xdb 到内存。
            cBuff = Searcher.loadContentFromFile(dbPath);
            // 2、使用上述的 cBuff 创建一个完全基于内存的查询对象。
            searcher = Searcher.newWithBuffer(cBuff);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据ip返回地址, 国内返回格式：四川 成都 或者 香港; 国外返回格式：美国
     *
     * @param ip ip地址
     * @return 地址
     */
    public static String searchRegion(String ip) {
        StringBuilder result = new StringBuilder();
        try {
            // 国家|区域|省份|城市|ISP
            String region = searcher.search(ip);
            String[] split = region.split("\\|");

            if ("中国".equals(split[0])) {
                if (!"0".equals(split[2])) {
                    // 省
                    result.append(split[2]);
                    if (!"0".equals(split[3])) {
                        // 市
                        result.append(" ");
                        if (split[3].endsWith("市")) {
                            result.append(split[3], 0, split[3].length() - 1);
                        } else {
                            result.append(split[3]);
                        }
                    }
                }

            } else if (!"0".equals(split[0])) {
                // 外国
                result.append(split[0]);
            }
            if (result.length() == 0) {
                return "未知";
            }
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 根据ip返回ISP服务商
     *
     * @param ip ip地址
     * @return 地址
     */
    public static String searchISP(String ip) {
        try {
            // 国家|区域|省份|城市|ISP
            String region = searcher.search(ip);
            String[] split = region.split("\\|");
            if ("0".equals(split[4])) {
                return "未知";
            }
            return split[4];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 关闭资源
     */
    public static void close() {
        try {
            searcher.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println(searchRegion("202.98.105.135"));
        System.out.println(searchISP("202.98.105.135"));
        System.out.println(searchRegion("47.243.90.230"));
        System.out.println(searchISP("47.243.90.230"));

    }
}
