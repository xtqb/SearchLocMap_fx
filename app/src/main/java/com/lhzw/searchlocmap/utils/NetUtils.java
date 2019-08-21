package com.lhzw.searchlocmap.utils;

import android.util.Log;

import com.lhzw.searchlocmap.constants.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Created by xtqb on 2019/3/29.
 */
public class NetUtils {

    public static String doLoginClient(String name, String password) {
        try {
            BasicHttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
            HttpConnectionParams.setSoTimeout(httpParams, 5000);
            //用HttpClient发送请求，分为五步
            //第一步：创建HttpClient对象
            HttpClient httpCient = new DefaultHttpClient(httpParams);
            //第二步：创建代表请求的对象,参数是访问的服务器地址
           // String url = BaseUtils.getBaseIP() + "/security/login?loginName=" + name + "&password=" + password + "&savePassword=true";
            String url = Constants.IP_ADD + "/security/login?loginName=" + name + "&password=" + password + "&savePassword=true";
            HttpGet httpGet = new HttpGet(url);
            //第三步：执行请求，获取服务器发还的相应对象
            HttpResponse httpResponse = httpCient.execute(httpGet);
            //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                Log.e("Tag", "login success");
                //第五步：从相应对象当中取出数据，放到entity当中
                HttpEntity entity = httpResponse.getEntity();
                String token = new JSONObject(EntityUtils.toString(entity, "utf-8")).getJSONObject("data").getString("token");//将entity当中的数据转换为字符串
                return token;
            } else {
                Log.e("Tag", "login fail code : " + httpResponse.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String reqAppClient(String token, String type, String subPath) {
        //开始请求接口数据
        try {
            HttpClient httpclient = new DefaultHttpClient();
            String uri = BaseUtils.getBaseIP() + subPath;
            HttpGet get = new HttpGet(uri);
            //添加http头信息
            get.addHeader(Constants.HTTP_TOOKEN, token);
            get.addHeader("type", type);
            get.addHeader("Content-Type", "application/json");
            get.addHeader("User-Agent", "your agent");
            HttpResponse response = httpclient.execute(get);
            int code = response.getStatusLine().getStatusCode();
            //检验状态码，如果成功接收数据
            if (code == 200) {
                String rev = EntityUtils.toString(response.getEntity(), "utf-8");
                return rev;
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.e("Tag", "doHttpGetClient fail, message : " + e.getMessage());
        }
        return null;
    }

    public static String doHttpGetClient(String token, String subPath) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);//连接时间
            httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);//数据传输时间
            String uri = Constants.IP_ADD + subPath;
            HttpGet get = new HttpGet(uri);
            //添加http头信息
            get.addHeader(Constants.HTTP_TOOKEN, token);
            get.addHeader("Content-Type", "application/json");
            get.addHeader("User-Agent", "your agent");
            HttpResponse response = httpclient.execute(get);
            int code = response.getStatusLine().getStatusCode();
            //检验状态码，如果成功接收数据
            if (code == 200) {
                String rev = EntityUtils.toString(response.getEntity(), "utf-8");
                return rev;
            }
        } catch (Exception e) {
            Log.e("Tag", "doHttpGetClient fail, message : " + e.getMessage());
        }
        return null;
    }
}
