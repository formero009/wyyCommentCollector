package com.wyyabout.collectingcomments.util;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.wyyabout.collectingcomments.commons.Constant;
import com.wyyabout.collectingcomments.commons.HttpClientFactory;
import com.wyyabout.collectingcomments.model.MoguModel;
import com.wyyabout.collectingcomments.model.Proxy;
import com.wyyabout.collectingcomments.vo.CheckVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.util.StringUtil;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component(value = "HTTPUtils")
@Slf4j
public final class HTTPUtils {

    //2.0 Get

    /**
     * get请求获取内容
     * @param ul
     * @param headers
     * @param proxy
     * @return
     */
    public static String getHtmlWithGet(String ul, Map<String,String> headers, Proxy proxy) {
        String html = "";
        log.info("正在请求地址:{}",ul);
        CookieStore cookieStore = new BasicCookieStore();
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);

        //代理不为空
        String urlHeader = "";
        if(ul.length()!=0){
            urlHeader = ul.substring(0,ul.indexOf(":"));
        }
        CloseableHttpClient httpClient = setProxy(urlHeader, proxy, cookieStore);
        //访问目标地址
        HttpGet httpGet = new HttpGet(ul);

        //设置请求头
        log.info("正在设置请求头");
        if(headers != null && headers.size()!=0){
            for(String key:headers.keySet()){
                httpGet.setHeader(key,headers.get(key));
            }
        }else{
            httpGet.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
        }

        try {
            HttpResponse httpResp = httpClient.execute(httpGet);
            int statusCode = httpResp.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                log.info("地址{}请求成功",ul);
                html = EntityUtils.toString(httpResp.getEntity(),"utf8");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            httpGet.releaseConnection();
            return html;
        }
    }

    //2.0 POST

    /**
     * post请求获取内容
     * @param url
     * @param headers
     * @param proxy
     * @return
     */
    public static String getThemWithPost(String url,Map<String,String> headers,Proxy proxy,Map<String,String> params){
        log.info("正在请求地址:{}",url);
        CookieStore cookieStore = new BasicCookieStore();
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);

        CloseableHttpClient httpClient = null;
        //设置代理
        String urlHeader = "";
        if(url.length()!=0){
            urlHeader = url.substring(0,url.indexOf(":"));
        }
        httpClient = setProxy(urlHeader, proxy, cookieStore);

        // 创建httpPost
        HttpPost httpPost = new HttpPost(url);

        //设置headers
        if(headers != null){
            log.info("设置请求头:{}",headers);
            for(String key:headers.keySet()){
                httpPost.setHeader(key,headers.get(key));
            }
        }else{
            httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
        }


        //设置post请求参数
        List<NameValuePair> postParameters = new ArrayList <NameValuePair>();
        if(params != null && params.size() != 0){
            log.info("设置请求参数:{}",params);
            postParameters = new ArrayList<NameValuePair>();
            for(String key:params.keySet()) {
                postParameters.add(new BasicNameValuePair(key, params.get(key)));
            }
        }


        CloseableHttpResponse response = null;
        try {
            StringEntity entity = new UrlEncodedFormEntity(postParameters, "UTF-8");
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            int stateCode = response.getStatusLine().getStatusCode();
            if (stateCode == HttpStatus.SC_OK) {
                log.info("地址{}请求成功",url);
                HttpEntity responseEntity = response.getEntity();
                String jsonString = EntityUtils.toString(responseEntity);
                return jsonString;
            }
            else{
                log.error("请求错误:"+stateCode+"("+url+")");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpPost.releaseConnection();
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 设置代理
     * @param proxy
     * @param cookieStore
     * @return
     */

    private static CloseableHttpClient setProxy(String protocalType,Proxy proxy, CookieStore cookieStore) {
        CloseableHttpClient httpClient = null;
        try {

            if(proxy != null && StringUtil.isNotEmpty(proxy.getIp()) && proxy.getPort() != null){
                log.info("正在设置代理{}...",proxy);
                HttpHost pro = null;
                if(protocalType.equalsIgnoreCase("http")){
                    httpClient = HttpClientBuilder.create().build();
                    pro = new HttpHost(proxy.getIp(), proxy.getPort(), "http");
                }
                if(protocalType.equalsIgnoreCase("https")) {
                    httpClient = HttpClientFactory.getHttpsClient();
                    pro = new HttpHost(proxy.getIp(), proxy.getPort(), "http");
                }
                //把代理设置到请求配置
                RequestConfig requestConfigWithProxy = RequestConfig.custom()
                        .setProxy(pro)
                        .build();
                //实例化CloseableHttpClient对象
                httpClient = HttpClients.custom()
                        .setDefaultRequestConfig(requestConfigWithProxy)
                        .setDefaultCookieStore(cookieStore)
                        .build();
            }else{
                log.info("代理为空,使用本地网络请求...");
                //无代理用默认实例化CloseableHttpClient对象
                httpClient = HttpClients.custom()
                        .setDefaultCookieStore(cookieStore).build();
            }
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return httpClient;
    }


    /**
     * 随机UA
     */
    public static Map<String,String> getRandomUA(){
        String[] ua = {
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:46.0) Gecko/20100101 Firefox/46.0",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36 OPR/37.0.2178.32",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.57.2 (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586",
                "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko",
                "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)",
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)",
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0)",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 BIDUBrowser/8.3 Safari/537.36",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36 Core/1.47.277.400 QQBrowser/9.4.7658.400",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 UBrowser/5.6.12150.8 Safari/537.36",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36 SE 2.X MetaSr 1.0",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36 TheWorld 7",
                "Mozilla/5.0 (Windows NT 6.1; W…) Gecko/20100101 Firefox/60.0"
        };
        int size = ua.length;
        int rand = (int) (Math.random() * size - 1);
        Map<String,String> result = new HashMap<>();
        result.put("User-Agent",ua[rand]);
        log.info("随机UA {}",result);
        return result;
    }

    /**
     * 包含错误码返回false
     * @param url
     * @param proxy
     * @param headers
     * @param errorContains
     * @return
     */
    public static CheckVo checkProxy(String url, Proxy proxy, Map<String,String> headers, String errorContains) {
        //当传入代理为null时，不使用代理
        log.info("校验代理：{} 对地址：{} 的可用性",JSONObject.toJSONString(proxy),url);
        String html = getHtmlWithGet(url,headers,proxy);
        CheckVo checkVo = new CheckVo();

        if(html.contains(errorContains) || StringUtil.isEmpty(html)){
            checkVo.setCheckValue("false");
            checkVo.setResult(null);
            log.info("代理：{} 对地址：{} 不可用",JSONObject.toJSONString(proxy),url);
            return checkVo;
        }else{
            checkVo.setResult(html);
            checkVo.setCheckValue("true");
            log.info("代理：{} 对地址：{} 可用",JSONObject.toJSONString(proxy),url);
            return checkVo;
        }
    }

    /**
     * 代理失效，更新最新代理
     * @return
     */
    public static Proxy changeProxy() {
        log.info("尝试切换可用代理....");
        Proxy proxy = new Proxy();
        //初始化代理来源url
        String proxyurl = Constant.PROXY_URL;
        String proxyJson = getHtmlWithGet(proxyurl,null,null);
        MoguModel moguModel = JSONObject.parseObject(proxyJson,new TypeReference<MoguModel>(){});
        String code = moguModel.getCode();

        //代理异常，错误码
        if(code.equals("3004") || code.equals("3005") || code.equals("3001") || code.equals("3006")){
            Proxy pro = new Proxy();
            pro.setIp("1");
            pro.setPort(1);
            log.warn("代理切换异常，请查看代理可用性!目前使用的代理地址为{}" +
                    "\n",Constant.PROXY_URL);
            return pro;
        }

        //代理消息内容
        List<Proxy> msg = moguModel.getMsg();

        if(msg.size()>0){
            log.info("代理已成功切换 ^_^，新的代理地址为{}",JSONObject.toJSONString(msg.get(0)));
            return msg.get(0);
        }else{
            Proxy pro = new Proxy();
            pro.setIp("1");
            pro.setPort(1);
            log.warn("代理切换异常，请查看代理可用性!目前使用的代理地址为{}" +
                    "\n",Constant.PROXY_URL);
            return pro;
        }
    }


    public static String posttest(String url,List<NameValuePair> nameValuePairList) throws IOException {
        JSONObject jsonObject = null;
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try{
            /**
             *  创建一个httpclient对象
             */
            client = HttpClients.createDefault();
            /**
             * 创建一个post对象
             */
            HttpPost post = new HttpPost(url);
            /**
             * 包装成一个Entity对象
             */
            StringEntity entity = new UrlEncodedFormEntity(nameValuePairList, "UTF-8");
            /**
             * 设置请求的内容
             */
            post.setEntity(entity);
            /**
             * 设置请求的报文头部的编码
             */
            post.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"));
            post.setHeader(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36"));
            post.setHeader(new BasicHeader("Content-Type","application/x-www-form-urlencoded"));
            post.setHeader(new BasicHeader("Origin", "http://music.163.com"));
            post.setHeader(new BasicHeader("Host", "music.163.com"));
            /**
             * 设置请求的报文头部的编码
             */
//            post.setHeader(new BasicHeader("Accept", "text/plain;charset=utf-8"));
            /**
             * 执行post请求
             */
            response = client.execute(post);
            /**
             * 获取响应码
             */
            int statusCode = response.getStatusLine().getStatusCode();
            if (200 == statusCode){
                /**
                 * 通过EntityUitls获取返回内容
                 */
                String result = EntityUtils.toString(response.getEntity(),"UTF-8");
                /**
                 * 转换成json,根据合法性返回json或者字符串
                 */
                try{
                    jsonObject = JSONObject.parseObject(result);
                    return jsonObject.toString();
                }catch (Exception e){
                    return result;
                }
            }else{
                log.error("HttpClientService-line: {}, errorMsg：{}", 146, "POST请求失败！");
            }
        }catch (Exception e){
            log.error("HttpClientService-line: {}, Exception：{}", 149, e);
        }finally {
            response.close();
            client.close();
        }
        return null;
    }
}