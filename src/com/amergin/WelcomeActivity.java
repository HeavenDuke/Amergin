package com.amergin;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.LocationManagerProxy;

public class WelcomeActivity extends Activity implements
AMapLocalWeatherListener {
	  private MyApplication app;//全局的Application类，存放全局变量
	  private ImageView background; //图片显示控件
	  private TextView mottoToday; //图片显示控件
	  private String todayString=""; //每个图片配的一句话
	  private Bitmap bitmap=null;
	  // private ImageView weather_icon;
		  	  //处理图片获取，字符串获取
  	  private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
            case 2:
            	setMottoText(todayString);
                break;
            case 1:
            	setBackground(bitmap);
                break;
            }
        }
    };
	private LocationManagerProxy mLocationManagerProxy;
   void setBackground(Bitmap _bitmap){
	   Log.i("KKK","未下载3433，");
	   if(background==null)background=(ImageView) findViewById(R.id.background);
 		if(_bitmap!=null)
 			background.setImageBitmap(_bitmap);
 		else
 			background.setBackgroundResource(R.drawable.background);
 		Log.i("KKK","未下载2323，");
   }
    void setMottoText(String _str)
    {
    	if(mottoToday==null)mottoToday=(TextView)findViewById(R.id.mottoToday);
    	
    	if(_str==null||_str.equals("")||"error!".equals(_str.substring(0, 6)))_str="即使没有网络，也要假装和这个世界联系密切";
    	mottoToday.setText("今日寄语:\n    "+_str);
    }
    
			//检查本地是否已经有了图片
			private boolean checkLocalPic() {
				// TODO Auto-generated method stub
				File f=new File(app.getLocalPicPath());
				Log.i("KKK","TTT");
				return f.exists();
			}
		  	  
			  //拼接当前日期的欢迎图片链接(checked)
			  String getWelcomePic()
			  {
				  String todayPicUrl = "http://amergin-picture.stor.sinaapp.com/welcome/"+app.getDate_today()+".jpg";
				  if(MyApplication.DEBUG) todayPicUrl ="http://pic.159.com/theme/pic/2009/3/24/20093241437132.jpg";
				  return todayPicUrl;
			  }

			  void getTodayPicAndTextAndWeather()
			  {
				    Log.i("KKK","进入函数");
				    
				  	if(checkLocalPic())//若本地已有图片缓存，将认为若图片已缓存那么文本必定缓存
	              	{
				  		//提示
				  		if(MyApplication.DEBUG)
				  		{
				  			Log.i("KKK","已下载，");
				  			Toast.makeText(WelcomeActivity.this, "本日图片已下载，无需重复下载",Toast.LENGTH_SHORT).show();
				  		}
				  		
	              		//图片
	              		bitmap = FileNetManager.getLoacalBitmap(app.getLocalPicPath());
	              		setBackground(bitmap);
	              		//bitmap.recycle();
	              		
	              		//文本
	              		todayString=FileNetManager.ReadMottoFromLocalPath(app.getLocalTextPath());
	              		setMottoText(todayString);
	              		return;
	              	}
				  	if(!(app.isNetworkConnected()))//网络不通，读取上次的缓存
				  	{
				  		
				  		//通知
				  		if(MyApplication.DEBUG)
				  		{
				  			Toast.makeText(WelcomeActivity.this, "网络状况很差，Amergin无法为您服务",Toast.LENGTH_SHORT).show();
				  			Log.i("KKK","未下载1，");
				  		}
				  		//图片
				  		bitmap=FileNetManager.getLastBitmap(app.getLocalPicPath());
				  		setBackground(bitmap);
				  		Log.i("KKK","未下载2，");
	              		//文本
	              		todayString=FileNetManager.ReadMottoFromLocalPath(app.getLocalTextPath());
	              		setMottoText(todayString);
	              		Log.i("KKK","未下载3，");
	              		return;
				  	}
				  	
				  	//下载新图新文本并缓存
				  //	Toast.makeText(WelcomeActivity.this, "本日图片未下载，下载ing",Toast.LENGTH_SHORT).show();
	              	 new Thread(new Runnable(){  
	                     @Override  
	                     public void run() {  
	                    	 Log.i("UserData","获取错误码出错1");
	                    	 //用户信息对象

	                    	 //请求路径
	                    	 String requestString=MyApplication.domain+"/picture/welcome.php?user="+MyApplication.UDM.id;
	                    	 Log.i("UserData","请求字符串"+requestString);
	                    	 //获取欢迎图片的路径以及每日一句话
	                    	 String motto="";
	                    	 String pictureString="";
	                    	 String resultString=FileNetManager.postRequestToHttp(requestString,null);
	                    	 Log.i("UserData","结果字符串"+resultString);
	                    	 JSONObject ja=null;
	                    	 try {
								ja=new JSONObject(resultString);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Log.i("UserData","json数组解析错误");
								return;
							}
	                    	 try {
								switch(ja.getInt("errcode"))
								 {
								 case -1://系统异常
									 throw new Exception("系统异常");
								 case 0://成功
									 
									 pictureString=ja.getString("background");
									 motto=ja.getString("content");
									 Log.i("UserData","成功"+pictureString+";"+motto);
									 
									 break;
								 case 1: //非法访问
									 throw new Exception("非法访问");
								 case 2://数据不存在
									 throw new Exception("数据不存在");
								 }
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								
								Log.i("UserData","获取错误码出错"+e.getMessage());return;
							}
	                    	 Log.i("UserData","获取错误码出错5");
	                    	 
	                    	 
	                    	 
	                    	 //下载图片并缓存到本地
	                         bitmap = FileNetManager.getImage(pictureString);  
	                         //发消息给主线程，换背景
	                         if(bitmap!=null)
	                         {
	            				 FileNetManager.saveBitmapToFile(bitmap,app.getLocalPicPath());//存图片
	            				 Log.i("UUU","存图片");
	                        	 Message msg=new Message();
	                        	 msg.what=1;
	                        	 handler.sendMessage(msg);
	                         }else
	                         {
	                        	 Log.i("UUU","错误了"+getWelcomePic());
	                         }
	                         
	                         //获得文本...写本地缓存
	                         //todayString=FileNetManager.postRequestToHttp("http://amergin.sinaapp.com/WelcomeContent.php?date=2014-11-28",null);
	                         todayString=motto;
	                         
	                         if(todayString!=null&&!"error!".equals(todayString.substring(0,6)))
	                         {
	                        	 FileNetManager.WriteMottoToLocalPath(app.getLocalTextPath(), todayString);
	                        	 Message msg=new Message();
	                        	 msg.what=2;
	                        	 handler.sendMessage(msg);
	                         }
	                         Message msg=new Message();
                        	 msg.what=2;
                        	 handler.sendMessage(msg);
	                     }
	                 }).start();
			  }

	@Override
      protected void onCreate(Bundle savedInstanceState) {
              super.onCreate(savedInstanceState);
              View view =  View.inflate(this, R.layout.welcome, null);
              	setContentView(view);
              	
//              	weather_icon=(ImageView) findViewById(R.id.weatherIcon);
//              	weather_icon.setImageDrawable(getResources().getDrawable(R.drawable.w_default));
//              	
            	init();//天气
            	PlayBGM();//播放应用启动的背景音乐
              	
              	
              	app = (MyApplication)getApplication();
              	MyApplication.UDM=UserDateManager.getLocalUserData();
              	
              	Log.i("UserDateManager",""+MyApplication.UDM.id+" "+MyApplication.UDM.username+"- "+MyApplication.UDM.PlayOnStart);
              	
              	
              	getTodayPicAndTextAndWeather(); //设置欢迎页面的背景图片和文本
              
              	
              	
              //设置动画，渐显渐隐用AlphaAnimation
              //第一个参数值0.3f为开始的透明度为50%，第二个参数值1.0f为结束的透明度为100%，即不透明。
              AlphaAnimation alphaAnimation = new AlphaAnimation(0.6f, 1.0f);
              //给动画设置持续时间，如果不设置，则时间为0，动画就看不到效果
              int time=6000;
              if(MyApplication.DEBUG)time=1000;
              alphaAnimation.setDuration(time);
              //给我们的背景运行动画
              alphaAnimation.setAnimationListener(new AnimationListener(){
            	  				@Override  //动画一开始就执行以下方法
                                  public void onAnimationStart(Animation animation) {
            	  					
            	  				}
            	  					
                                  @Override //动画重复时执行以下方法
                                  public void onAnimationRepeat(Animation animation) {
                                	  
                                  		}
                                  @Override //动画结束时执行以下方法
                                  public void onAnimationEnd(Animation animation) {
                                	  //跳转到主页面
                                	  	Intent i=new Intent();
                                	  	i.setClass(WelcomeActivity.this, MainActivity.class);
                                	  	startActivity(i);
                                	  	finish();
                                  }
              	});
              view.startAnimation(alphaAnimation);//开始动画
              
              
             
	  }
	
	private void PlayBGM() {
//		MediaPlayer mp1 = MediaPlayer.create(WelcomeActivity.this,  R.raw.hellosound); 
//		mp1.start();
		new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					MediaPlayer mp1 = MediaPlayer.create(WelcomeActivity.this,  R.raw.hellosound); 
					mp1.start();
					Log.i("KLL","发个");
				}
				catch(Exception e)
				{
					Log.i("KLL",",错误"+e.getMessage());
				}
			}
		}).start();
	}
	@Override
	public void onWeatherForecaseSearched(AMapLocalWeatherForecast arg0) {
		
	}
	@Override
	public void onWeatherLiveSearched(AMapLocalWeatherLive aMapLocalWeatherLive) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				if (aMapLocalWeatherLive!=null&&aMapLocalWeatherLive.getAMapException().getErrorCode() == 0) {
					// 天气预报成功回调 设置天气信息
								
					//处理天气情况，存入全局变量
					MyApplication.weather=Weather_List.GetIdFromName(aMapLocalWeatherLive.getWeather());
					//设置天气图片	
					  if(MyApplication.DEBUG)
					Toast.makeText(
							this,
							(aMapLocalWeatherLive).getWeather()+""+Weather_List.getResImageById(MyApplication.weather)
									, Toast.LENGTH_SHORT)
							.show();
//					weather_icon.setImageDrawable(getResources().getDrawable(Weather_List.getResImageById(MyApplication.weather)
//							));
	              	
					
					//weather_icon.setBackgroundResource(Weather_List.getResImageById(MyApplication.weather));
				// 获取天气预报
					
				} else {

					// 获取天气预报失败
					Toast.makeText(
							this,
							"获取天气预报失败:"
									+ aMapLocalWeatherLive.getAMapException()
											.getErrorMessage(), Toast.LENGTH_SHORT)
							.show();
				 
				}
				
				
	}
	
	private void init() {
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		//获取实时天气预报
		//如果需要同时请求实时、未来三天天气，请确保定位获取位置后使用,分开调用，可忽略本句。
		mLocationManagerProxy.requestWeatherUpdates(
				LocationManagerProxy.WEATHER_TYPE_LIVE, this);
		Log.i("WWW","end1");
	}

}
