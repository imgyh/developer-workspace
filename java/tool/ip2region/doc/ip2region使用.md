# 一、前言

本文将使用`ip2region`实现ip到实际地址的转换。

`ip2region`项目地址：https://github.com/lionsoul2014/ip2region

ip2region - 是一个离线IP地址定位库和IP定位数据管理框架，10微秒级别的查询效率，提供了众多主流编程语言的 `xdb` 数据生成和查询客户端实现。

`xdb` 支持亿级别的 IP 数据段行数，默认的 region 信息都固定了格式：`国家|区域|省份|城市|ISP`，缺省的地域信息默认是0。

# 二、xdb文件制作

`ip2region`程序使用到的数据格式是`xdb` 我们需要从`ip.merge.txt` 转成`xdb`。很方便的是`ip2region`项目已经提供了转化的工具并且支持多语言。

这里以python的转换工具为例来测试使用。

python转换工具的地址：https://github.com/lionsoul2014/ip2region/tree/master/maker/python

同时我也下载了一份放在当前`python-maker`文件夹中，里面也有纯真网络发布的免费的ip文件，只需要转换一下就可以使用。

```python
python main.py gen --src=./ip.merge.txt --dst=./ip2region.xdb
```

# 三、ip2region在java中的使用

java使用教程地址：https://github.com/lionsoul2014/ip2region/tree/master/binding/java

首先添加maven依赖

```xml
<dependency>
    <groupId>org.lionsoul</groupId>
    <artifactId>ip2region</artifactId>
    <version>2.7.0</version>
</dependency>
```



由于文件只有10MB直接预先加载整个 ip2region.xdb 的数据到内存，然后基于这个数据创建查询对象来实现完全基于文件的查询。

```java
import org.lionsoul.ip2region.xdb.Searcher;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class SearcherTest {
    public static void main(String[] args) {
        String dbPath = "ip2region.xdb file path";

        // 1、从 dbPath 加载整个 xdb 到内存。
        byte[] cBuff;
        try {
            cBuff = Searcher.loadContentFromFile(dbPath);
        } catch (Exception e) {
            System.out.printf("failed to load content from `%s`: %s\n", dbPath, e);
            return;
        }

        // 2、使用上述的 cBuff 创建一个完全基于内存的查询对象。
        Searcher searcher;
        try {
            searcher = Searcher.newWithBuffer(cBuff);
        } catch (Exception e) {
            System.out.printf("failed to create content cached searcher: %s\n", e);
            return;
        }

        // 3、查询
        try {
            String ip = "1.2.3.4";
            long sTime = System.nanoTime();
            String region = searcher.search(ip);
            long cost = TimeUnit.NANOSECONDS.toMicros((long) (System.nanoTime() - sTime));
            System.out.printf("{region: %s, ioCount: %d, took: %d μs}\n", region, searcher.getIOCount(), cost);
        } catch (Exception e) {
            System.out.printf("failed to search(%s): %s\n", ip, e);
        }
        
        // 4、关闭资源 - 该 searcher 对象可以安全用于并发，等整个服务关闭的时候再关闭 searcher
        // searcher.close();

        // 备注：并发使用，用整个 xdb 数据缓存创建的查询对象可以安全的用于并发，也就是你可以把这个 searcher 对象做成全局对象去跨线程访问。
    }
}
```



# 四、封装成工具类

```java
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
```

