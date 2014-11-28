package com.amergin;

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
		String jsonString=FileNetManager.getContentFromUrl(url, null);
		String str1="";
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
	
	public static Song getTodaySong()
	{
		Song song=null;
		String jsonString=FileNetManager.getContentFromUrl(MyApplication.musicFetchUrlStirng, null);
		List<String> list=new ArrayList<String>();
		try {
			//获取Json对象
			JSONObject jsonObj=new JSONObject(jsonString);
			song=new Song(jsonObj.getString("id"),jsonObj.getString("name"),jsonObj.getString("album"),jsonObj.getString("artist"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return song;
	}
	
}
