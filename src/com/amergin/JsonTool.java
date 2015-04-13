package com.amergin;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonTool {
	//ak值是动态的咩...
	static String  url="http://api.map.baidu.com/telematics/v3/weather?location=%E5%8C%97%E4%BA%AC&output=json&ak=F82d383bd0e4f7e97267f80265af60ec";
	static String weather="";
	static String temperature="";
	static boolean weatherGet=false;
	
	//返回值第一个是天气，第二个是温度
	public static List<String> getWeather(){
		String jsonString=FileNetManager.getContentFromUrl(url, "GB2312");
		
		
		List<String> list=new ArrayList<String>();
		try {
			//获取Json对象
			JSONObject jsonObj=new JSONObject(jsonString);
			JSONArray ja=jsonObj.getJSONArray("results");
			JSONArray jsonObjectq = (ja.getJSONObject(0)).getJSONArray("weather_data");
			JSONObject jsonObjw=jsonObjectq.getJSONObject(0);
			weather=jsonObjw.getString("weather");
			temperature=jsonObjw.getString("temperature");
			list.add(weather);
			list.add(temperature);
			weatherGet=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	public static String getWeather(String _url)
	{
		if(!weatherGet)getWeather(_url);
		return weather;
	}
	public static String getTemperature(String _url)
	{
		if(!weatherGet)getWeather(_url);
		return temperature;
	}

	

	
	/*
	 * 
	 * {"id":2,
	 * "name":"http:\/\/amergin-music.stor.sinaapp.com\/\u68e0\u68a8\u714e\u96eamp3",
	 * "album":"\u94f6\u4e34",
	 * "artist":"\u8150\u8349\u4e3a\u8424"}
	 * 
	 */
	public static String changeCharset(String str, String newCharset){
        if(str != null) {
            //用默认字符编码解码字符串。与系统相关，中文windows默认为GB2312
            byte[] bs = str.getBytes();
            try {
				return new String(bs, newCharset);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    //用新的字符编码生成字符串
        }
        return null;
    }
	public static Song getTodaySong()
	{
		Song song=null;
		String jsonString=FileNetManager.getContentFromUrl(MyApplication.musicFetchUrlStirng, "UTF-8");
		try {
			//获取Json对象
			JSONObject jsonObj=new JSONObject(jsonString);
			song=new Song(jsonObj.getString("mid"),jsonObj.getString("name"),jsonObj.getString("album"),jsonObj.getString("artist"),jsonObj.getString("lyric"),jsonObj.getString("music"),jsonObj.getString("class"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return song;
	}
	
	 public static String getEncoding(String str) {    
         String encode = "GB2312";    
        try {    
            if (str.equals(new String(str.getBytes(encode), encode))) {    
                 String s = encode;    
                return s;    
             }    
         } catch (Exception exception) {    
         }    
         encode = "ISO-8859-1";    
        try {    
            if (str.equals(new String(str.getBytes(encode), encode))) {    
                 String s1 = encode;    
                return s1;    
             }    
         } catch (Exception exception1) {    
         }    
         encode = "UTF-8";
        try {    
            if (str.equals(new String(str.getBytes(encode), encode))) {    
                 String s2 = encode;    
                return s2;    
             }    
         } catch (Exception exception2) {    
         }    
         encode = "GBK";    
        try {    
            if (str.equals(new String(str.getBytes(encode), encode))) {    
                 String s3 = encode;    
                return s3;    
             }    
         } catch (Exception exception3) {    
         }    
        return "";    
     }    
	
}
