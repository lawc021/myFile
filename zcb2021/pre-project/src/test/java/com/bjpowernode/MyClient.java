package com.bjpowernode;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MyClient {

    @Test
    public void testGET(){
        //1.创建HttpClient对象 : 包含请求参数， 发起请求， 获取应答结果
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //2.创建请求
        String uri = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=13822222222";
        HttpGet get = new HttpGet(uri);


        try {
            //3.向uri发起请求 CloseableHttpResponse:访问了接口后的，应答结果
            CloseableHttpResponse response = httpClient.execute(get);

            //
            /*Header[] allHeaders = response.getAllHeaders();
            for (Header allHeader : allHeaders) {
                System.out.println("allHeader="+allHeader);
            }*/

            // 4.获取访问接口的结果， 使用response对象
            StatusLine  statusLine= response.getStatusLine(); //StatusLine: 包含HttpStatus状态码
            if( statusLine.getStatusCode() == HttpStatus.SC_OK){ // 200 请求成功
                //获取返回的数据 response.getEntity();
                HttpEntity entity = response.getEntity(); //返回的结果内容
                InputStream in  = entity.getContent();

                StringBuilder builder = new StringBuilder("");
                BufferedReader br = new BufferedReader( new InputStreamReader(in,"GBK"));
                String line = null;
                while(  (line = br.readLine() ) != null){
                    builder.append(line);
                }
                System.out.println("result="+builder.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Test
    public void testPOST(){
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String uri="https://tcc.taobao.com/cc/json/mobile_tel_segment.htm";
        HttpPost post = new HttpPost(uri);

        try {
            //给post添加参数,模拟form的
            List<NameValuePair> params = new ArrayList<>();
            //添加参数和值
            params.add( new BasicNameValuePair("tel","13999999999"));
            //其他参数
            HttpEntity entity = new UrlEncodedFormEntity(params);
            post.setEntity(entity);

            //执行请求
            CloseableHttpResponse response = httpClient.execute(post);
            //获取结果
            if( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK ){
                String str  = EntityUtils.toString(response.getEntity());
                System.out.println("请求的返回结果："+str);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
