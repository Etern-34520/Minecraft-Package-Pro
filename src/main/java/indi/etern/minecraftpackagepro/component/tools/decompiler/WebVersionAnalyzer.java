package indi.etern.minecraftpackagepro.component.tools.decompiler;

import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class WebVersionAnalyzer {
    
    //EXAMPLE:  https://bmclapi2.bangbang93.com/version/1.*.*/json
    public static String getIndexJsonVersion(String version) throws Exception {
        String uri = "https://bmclapi2.bangbang93.com/version/" + version + "/json";
        Map<String,Object> map = (Map<String,Object>)new Gson().fromJson(new InputStreamReader(returnBitMap(uri)), Map.class);
        Map<String,Object> assetIndex = (Map<String, Object>) map.get("assetIndex");
        String url = (String) assetIndex.get("url");
        return url.substring(url.lastIndexOf('/')+1,url.lastIndexOf('.'));
    }
    
    private static InputStream returnBitMap(String path) throws Exception {
        URL url = null;
        InputStream is = null;
        url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();//利用HttpURLConnection对象,我们可以从网络中获取网页数据.
        conn.setDoInput(true);
        conn.connect();
        is = conn.getInputStream();    //得到网络返回的输入流
        return is;
    }
}
