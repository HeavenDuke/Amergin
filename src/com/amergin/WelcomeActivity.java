package com.amergin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
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

public class WelcomeActivity extends Activity {
	  private MyApplication app;//全局的Application类，存放全局变量
	  private ImageView background; //图片显示控件
	  private TextView mottoToday; //图片显示控件
	  private String todayString=""; //每个图片配的一句话
	  private Bitmap bitmap=null;
	  
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
	private LocationManager mLocationManager;
	private LocationListener mLocationListener;
	private Location lastKnownLocation;
	private String mProviderName;
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
    	mottoToday.setText(_str);
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
				  return todayPicUrl;
			  }

			  void getTodayPicAndTextAndWeather()
			  {
				    Log.i("KKK","进入函数");
				  	if(checkLocalPic())//若本地已有图片缓存，将认为若图片已缓存那么文本必定缓存
	              	{
				  		//提示
				  		Log.i("KKK","已下载，");
	              		Toast.makeText(WelcomeActivity.this, "本日图片已下载，无需重复下载",Toast.LENGTH_SHORT).show();
	              		
	              		//图片
	              		bitmap = FileNetManager.getLoacalBitmap(app.getLocalPicPath());
	              		setBackground(bitmap);
	              		
	              		//文本
	              		todayString=FileNetManager.ReadMottoFromLocalPath(app.getLocalTextPath());
	              		setMottoText(todayString);
	              		return;
	              	}
				  	if(!(app.isNetworkConnected()))//网络不通，读取上次的缓存
				  	{
				  		//通知
				  		Toast.makeText(WelcomeActivity.this, "网络状况很差，Amergin无法为您服务",Toast.LENGTH_SHORT).show();
				  		Log.i("KKK","未下载1，");
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
				  	//Toast.makeText(WelcomeActivity.this, "本日图片未下载，下载ing",Toast.LENGTH_SHORT).show();
	              	 new Thread(new Runnable(){  
	                     @Override  
	                     public void run() {  
	                    	 
	                    	 //下载图片并缓存到本地
	                         bitmap = FileNetManager.getImage(getWelcomePic());  
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
	                         todayString=FileNetManager.postRequestToHttp("http://amergin.sinaapp.com/WelcomeContent.php?date=2014-11-28",null);
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
                        	 //获得天气，解析---------------
                        	 //获得位置经纬度
                        	 //TT();
                        	 
	                     }
	                 }).start();
			  }
				public Location GetLocation(){
					LocationManager lManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
					Location location=lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					return location;
				}
				public int GetWeather(Location location){
					int weathercode=0;
					Log.i("Weather","??");
					try{
						if(location != null){   
			                double latitude = location.getLatitude(); //经度   
			                double longitude = location.getLongitude(); //纬度
			            }   
					}catch(Exception e)
					{
						Log.i("Weather","??"+e.getMessage());
						return 0;
					}
					double latitude=location.getLatitude();//纬度
					double longitude=location.getLongitude();//经度
					Log.i("Weather","?!?");
					String url="http://api.map.baidu.com/telematics/v3/weather?";
					url+="location="+longitude+","+latitude;
					url+="&output=json&ak="+app.getLbs_app_id();
					HttpGet httpRequest = new HttpGet(url);
					try  
			        {  
			          /*发送请求并等待响应*/  
			          HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);  
			          /*若状态码为200 ok*/  
			          if(httpResponse.getStatusLine().getStatusCode() == 200)   
			          {  
			            /*读*/  
			            String strResult = EntityUtils.toString(httpResponse.getEntity());
			            
			            
			            
			          }  
			          else  
			          {  
			            
			          }  
			        }catch(Exception e){
			        	e.printStackTrace();
			        }
					return weathercode;
				}

	@Override
      protected void onCreate(Bundle savedInstanceState) {
              super.onCreate(savedInstanceState);
              View view =  View.inflate(this, R.layout.welcome, null);
           //   getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
              	setContentView(view);
              	app = (MyApplication)getApplication();
              	getTodayPicAndTextAndWeather(); //设置欢迎页面的背景图片和文本
              	Log. i( "KKK", "897" );
              //设置动画，渐显渐隐用AlphaAnimation
              //第一个参数值0.3f为开始的透明度为50%，第二个参数值1.0f为结束的透明度为100%，即不透明。
              AlphaAnimation alphaAnimation = new AlphaAnimation(0.6f, 1.0f);
              //给动画设置持续时间，如果不设置，则时间为0，动画就看不到效果
              alphaAnimation.setDuration(8000);
              //给我们的背景运行动画
              alphaAnimation.setAnimationListener(new AnimationListener(){
            	  				@Override  //动画一开始就执行以下方法
                                  public void onAnimationStart(Animation animation) {
            	  					
            	  					//Toast.makeText(WelcomeActivity.this, getWelcomePic(), Toast.LENGTH_LONG).show();
            	  					//Toast.makeText(WelcomeActivity.this,"感谢给你逆境的众生", Toast.LENGTH_LONG).show();
                                  //    Toast.makeText(WelcomeActivity.this,JsonTool.getPerson("", ""), Toast.LENGTH_LONG).show();
                                          
            	  				
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
              PlayBGM();//播放应用启动的背景音乐
              
             
	  }
	
	
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); 
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, locationListener); 
		if (location != null) { 
			double lat = location.getLatitude(); 
			double lng = location.getLongitude(); 
			Log.i("Weather","第一次获取成功");
			
		} 
		else
		{
			Log.i("Weather","又是空的");
		}

		
		
	}
	private final LocationListener locationListener = new LocationListener() { 
		public void onLocationChanged(Location location) { 
		if (location != null) { 
		double lat = location.getLatitude(); 
		double lng = location.getLongitude(); 
		Log.i("Weather","第一次获取成功蔷薇蔷薇");
		                       
		}

 } 
		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		} 
		}; 
	private void PlayBGM() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					MediaPlayer mp1 = MediaPlayer.create(WelcomeActivity.this,  R.raw.hellosound); 
					mp1.start();
				}
				catch(Exception e)
				{
					Log.i("KLL",",错误"+e.getMessage());
				}
			}
			
			
		}).start();
	}
	private double latitude=0.0;
	private double longitude =0.0;
void TT()
{
	if(isOPen(this))
	{
		Log.i("Weather","打开了");
	}
	else
	{
		// openGPS(this);
		Log.i("Weather","没打开了");
	}
	

	LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);   
	
	            if(location != null){   
	                latitude = location.getLatitude(); //经度   
	                longitude = location.getLongitude(); //纬度
	                Log.i("Weather","out"+longitude);
	            }   
	        	Log.i("Weather","out");
}

/**
 * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
 * @param context
 * @return true 表示开启
 */
public static final boolean isOPen(final Context context) {
	LocationManager locationManager 
	                         = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
	boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
	boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	if (gps ||network) {
		return true;
	}
	return false;
	
}

/**
 * 强制帮用户打开GPS
 * @param context
 */
public static final void openGPS(Context context) {
	Intent GPSIntent = new Intent();
	GPSIntent.setClassName("com.android.settings",
			"com.android.settings.widget.SettingsAppWidgetProvider");
	GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
	GPSIntent.setData(Uri.parse("custom:3"));
	try {
		PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
	} catch (Exception e) {
		e.printStackTrace();
	}
}

}
