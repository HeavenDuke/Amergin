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
	  private MyApplication app;//ȫ�ֵ�Application�࣬���ȫ�ֱ���
	  private ImageView background; //ͼƬ��ʾ�ؼ�
	  private TextView mottoToday; //ͼƬ��ʾ�ؼ�
	  private String todayString=""; //ÿ��ͼƬ���һ�仰
	  private Bitmap bitmap=null;
	  
		  	  //����ͼƬ��ȡ���ַ�����ȡ
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
	   Log.i("KKK","δ����3433��");
	   if(background==null)background=(ImageView) findViewById(R.id.background);
 		if(_bitmap!=null)
 			background.setImageBitmap(_bitmap);
 		else
 			background.setBackgroundResource(R.drawable.background);
 		Log.i("KKK","δ����2323��");
   }
    void setMottoText(String _str)
    {
    	if(mottoToday==null)mottoToday=(TextView)findViewById(R.id.mottoToday);
    	
    	if(_str==null||_str.equals("")||"error!".equals(_str.substring(0, 6)))_str="��ʹû�����磬ҲҪ��װ�����������ϵ����";
    	mottoToday.setText(_str);
    }
    
			//��鱾���Ƿ��Ѿ�����ͼƬ
			private boolean checkLocalPic() {
				// TODO Auto-generated method stub
				File f=new File(app.getLocalPicPath());
				Log.i("KKK","TTT");
				return f.exists();
			}
		  	  
			  //ƴ�ӵ�ǰ���ڵĻ�ӭͼƬ����(checked)
			  String getWelcomePic()
			  {
				  String todayPicUrl = "http://amergin-picture.stor.sinaapp.com/welcome/"+app.getDate_today()+".jpg";
				  return todayPicUrl;
			  }

			  void getTodayPicAndTextAndWeather()
			  {
				    Log.i("KKK","���뺯��");
				  	if(checkLocalPic())//����������ͼƬ���棬����Ϊ��ͼƬ�ѻ�����ô�ı��ض�����
	              	{
				  		//��ʾ
				  		Log.i("KKK","�����أ�");
	              		Toast.makeText(WelcomeActivity.this, "����ͼƬ�����أ������ظ�����",Toast.LENGTH_SHORT).show();
	              		
	              		//ͼƬ
	              		bitmap = FileNetManager.getLoacalBitmap(app.getLocalPicPath());
	              		setBackground(bitmap);
	              		
	              		//�ı�
	              		todayString=FileNetManager.ReadMottoFromLocalPath(app.getLocalTextPath());
	              		setMottoText(todayString);
	              		return;
	              	}
				  	if(!(app.isNetworkConnected()))//���粻ͨ����ȡ�ϴεĻ���
				  	{
				  		//֪ͨ
				  		Toast.makeText(WelcomeActivity.this, "����״���ܲAmergin�޷�Ϊ������",Toast.LENGTH_SHORT).show();
				  		Log.i("KKK","δ����1��");
				  		//ͼƬ
				  		bitmap=FileNetManager.getLastBitmap(app.getLocalPicPath());
				  		setBackground(bitmap);
				  		Log.i("KKK","δ����2��");
	              		//�ı�
	              		todayString=FileNetManager.ReadMottoFromLocalPath(app.getLocalTextPath());
	              		setMottoText(todayString);
	              		Log.i("KKK","δ����3��");
	              		return;
				  	}
				  	
				  	//������ͼ���ı�������
				  	//Toast.makeText(WelcomeActivity.this, "����ͼƬδ���أ�����ing",Toast.LENGTH_SHORT).show();
	              	 new Thread(new Runnable(){  
	                     @Override  
	                     public void run() {  
	                    	 
	                    	 //����ͼƬ�����浽����
	                         bitmap = FileNetManager.getImage(getWelcomePic());  
	                         //����Ϣ�����̣߳�������
	                         if(bitmap!=null)
	                         {
	            				 FileNetManager.saveBitmapToFile(bitmap,app.getLocalPicPath());//��ͼƬ
	            				 Log.i("UUU","��ͼƬ");
	                        	 Message msg=new Message();
	                        	 msg.what=1;
	                        	 handler.sendMessage(msg);
	                         }else
	                         {
	                        	 Log.i("UUU","������"+getWelcomePic());
	                         }
	                         
	                         //����ı�...д���ػ���
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
                        	 //�������������---------------
                        	 //���λ�þ�γ��
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
			                double latitude = location.getLatitude(); //����   
			                double longitude = location.getLongitude(); //γ��
			            }   
					}catch(Exception e)
					{
						Log.i("Weather","??"+e.getMessage());
						return 0;
					}
					double latitude=location.getLatitude();//γ��
					double longitude=location.getLongitude();//����
					Log.i("Weather","?!?");
					String url="http://api.map.baidu.com/telematics/v3/weather?";
					url+="location="+longitude+","+latitude;
					url+="&output=json&ak="+app.getLbs_app_id();
					HttpGet httpRequest = new HttpGet(url);
					try  
			        {  
			          /*�������󲢵ȴ���Ӧ*/  
			          HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);  
			          /*��״̬��Ϊ200 ok*/  
			          if(httpResponse.getStatusLine().getStatusCode() == 200)   
			          {  
			            /*��*/  
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
              	getTodayPicAndTextAndWeather(); //���û�ӭҳ��ı���ͼƬ���ı�
              	Log. i( "KKK", "897" );
              //���ö��������Խ�����AlphaAnimation
              //��һ������ֵ0.3fΪ��ʼ��͸����Ϊ50%���ڶ�������ֵ1.0fΪ������͸����Ϊ100%������͸����
              AlphaAnimation alphaAnimation = new AlphaAnimation(0.6f, 1.0f);
              //���������ó���ʱ�䣬��������ã���ʱ��Ϊ0�������Ϳ�����Ч��
              alphaAnimation.setDuration(8000);
              //�����ǵı������ж���
              alphaAnimation.setAnimationListener(new AnimationListener(){
            	  				@Override  //����һ��ʼ��ִ�����·���
                                  public void onAnimationStart(Animation animation) {
            	  					
            	  					//Toast.makeText(WelcomeActivity.this, getWelcomePic(), Toast.LENGTH_LONG).show();
            	  					//Toast.makeText(WelcomeActivity.this,"��л�����澳������", Toast.LENGTH_LONG).show();
                                  //    Toast.makeText(WelcomeActivity.this,JsonTool.getPerson("", ""), Toast.LENGTH_LONG).show();
                                          
            	  				
            	  				}
            	  					
                                  @Override //�����ظ�ʱִ�����·���
                                  public void onAnimationRepeat(Animation animation) {
                                	  
                                  		}
                                  @Override //��������ʱִ�����·���
                                  public void onAnimationEnd(Animation animation) {
                                	  //��ת����ҳ��
                                	  	Intent i=new Intent();
                                	  	i.setClass(WelcomeActivity.this, MainActivity.class);
                                	  	startActivity(i);
                                	  	finish();
                                  }
              	});
              view.startAnimation(alphaAnimation);//��ʼ����
              PlayBGM();//����Ӧ�������ı�������
              
             
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
			Log.i("Weather","��һ�λ�ȡ�ɹ�");
			
		} 
		else
		{
			Log.i("Weather","���ǿյ�");
		}

		
		
	}
	private final LocationListener locationListener = new LocationListener() { 
		public void onLocationChanged(Location location) { 
		if (location != null) { 
		double lat = location.getLatitude(); 
		double lng = location.getLongitude(); 
		Log.i("Weather","��һ�λ�ȡ�ɹ�ǾޱǾޱ");
		                       
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
					Log.i("KLL",",����"+e.getMessage());
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
		Log.i("Weather","����");
	}
	else
	{
		// openGPS(this);
		Log.i("Weather","û����");
	}
	

	LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);   
	
	            if(location != null){   
	                latitude = location.getLatitude(); //����   
	                longitude = location.getLongitude(); //γ��
	                Log.i("Weather","out"+longitude);
	            }   
	        	Log.i("Weather","out");
}

/**
 * �ж�GPS�Ƿ�����GPS����AGPS����һ������Ϊ�ǿ�����
 * @param context
 * @return true ��ʾ����
 */
public static final boolean isOPen(final Context context) {
	LocationManager locationManager 
	                         = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	// ͨ��GPS���Ƕ�λ����λ������Ծ�ȷ���֣�ͨ��24�����Ƕ�λ��������Ϳտ��ĵط���λ׼ȷ���ٶȿ죩
	boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	// ͨ��WLAN���ƶ�����(3G/2G)ȷ����λ�ã�Ҳ����AGPS������GPS��λ����Ҫ���������ڻ��ڸ������Ⱥ��ï�ܵ����ֵȣ��ܼ��ĵط���λ��
	boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	if (gps ||network) {
		return true;
	}
	return false;
	
}

/**
 * ǿ�ư��û���GPS
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
