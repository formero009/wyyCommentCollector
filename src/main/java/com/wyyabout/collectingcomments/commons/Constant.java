package com.wyyabout.collectingcomments.commons;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
public final class Constant {

    //代理url
    //推荐蘑菇代理 很好用  可以包量
    public static String PROXY_URL = "http://piping.mogumiao.com/proxy/api/get_ip_al";

    //豆瓣电影api
    public static String DOUBAN_SEARCH = "https://api.douban.com/v2/movie/search";

    //豆瓣电影详情页
    public static String DOUBAN_DETAIL = "http://api.douban.com/v2/movie/subject/";

    /**
     * 生成豆瓣按类搜索api连接
     * @param tag
     * @param startPos
     * @param count
     * @return
     */
    public static String GenerateDoubanUrl(String tag,int startPos,int count){
        String newurl = "";
        try {
            newurl = DOUBAN_SEARCH + "?tag=" + URLEncoder.encode(tag,"UTF-8") + "&start=" + startPos + "&count=" + count;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("generate douban links is failed,PLEASE CHECK!");
        }

        return newurl;
    }
}
