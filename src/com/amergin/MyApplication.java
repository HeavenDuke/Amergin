package com.amergin;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

@SuppressLint("SimpleDateFormat") //ʲô��˼...
public class MyApplication extends Application {
	private int user_id;//�û�id��-1Ϊ�οͣ�
	private int songid; //��ǰ����id 
	private Date date_today=null;//��������
	private String current_time;//��ǰʱ��
	private int current_mood;//��ǰ����
	private int current_behaviour;//��ǰ��Ϊ
	private int current_weather;//��ǰ����
	private boolean indoor=false; 
	private double temperature=0; //�¶�
	public static  boolean DEBUG=false;
	public static String backgroundUrlStirng="http://amergin.sinaapp.com/GetPicture.php";
	public static String musicFetchUrlStirng="http://amergin.sinaapp.com/GetMusic.php";
	//public static String songFetchUrlString="http://amergin.sinaapp.com/GetMusic.php";
	
	private String lbs_app_id="F82d383bd0e4f7e97267f80265af60ec";//
	public  String SDPATH = Environment.getExternalStorageDirectory() + "/";
	public String getLbs_app_id() {
		return lbs_app_id;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
		
		//�������set�͵��˰�
		super.onCreate();
	}

	
	public String getLocalPicPath()
	{
		return SDPATH+"Amergin/Cache/Img/"+getDate_today()+".jpg";
	}

	public String getLocalMianBackPicPath()//���Ҫ��ô���趨
	{
		return SDPATH+"Amergin/Cache/Img/"+getDate_today()+".jpg";
	}
	
	
	public String getLocalTextPath()
	{
		return SDPATH+"Amergin/Cache/text/motto.txt";
	}
	
	
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getSongid() {
		return songid;
	}
	public void setSongid(int songid) {
		this.songid = songid;
	}
	public String getDate_today() {
		if(date_today==null)setDate_today();  //�������
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date_today);
	}
	
	private void setDate_today() {
		date_today    =   new  Date(System.currentTimeMillis());//��ȡ��ǰʱ��       
	}
	public String getCurrent_time() {
		return current_time;
	}
	public void setCurrent_time(String current_time) {
		this.current_time = current_time;
	}
	public int getCurrent_mood() {
		return current_mood;
	}
	public void setCurrent_mood(int current_mood) {
		this.current_mood = current_mood;
	}
	public int getCurrent_behaviour() {
		return current_behaviour;
	}
	public void setCurrent_behaviour(int current_behaviour) {
		this.current_behaviour = current_behaviour;
	}
	public int getCurrent_weather() {
		return current_weather;
	}
	public void setCurrent_weather(int current_weather) {
		this.current_weather = current_weather;
	}
	//�ж�ϵͳ�����Ƿ����
	 public  boolean isNetworkConnected(){
	        ConnectivityManager cm =    (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE); 
	        NetworkInfo info =cm.getActiveNetworkInfo(); 
	        if(info!=null&&info.isConnected()){ 
	            return true; 
	        }else { 
	            return false ; 
	        } 
	 	}
}
