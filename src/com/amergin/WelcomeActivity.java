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
	  private MyApplication app;//ȫ�ֵ�Application�࣬���ȫ�ֱ���
	  private ImageView background; //ͼƬ��ʾ�ؼ�
	  private TextView mottoToday; //ͼƬ��ʾ�ؼ�
	  private String todayString=""; //ÿ��ͼƬ���һ�仰
	  private Bitmap bitmap=null;
	  // private ImageView weather_icon;
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
	private LocationManagerProxy mLocationManagerProxy;
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
    	mottoToday.setText("���ռ���:\n    "+_str);
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
				  if(MyApplication.DEBUG) todayPicUrl ="http://pic.159.com/theme/pic/2009/3/24/20093241437132.jpg";
				  return todayPicUrl;
			  }

			  void getTodayPicAndTextAndWeather()
			  {
				    Log.i("KKK","���뺯��");
				    
				  	if(checkLocalPic())//����������ͼƬ���棬����Ϊ��ͼƬ�ѻ�����ô�ı��ض�����
	              	{
				  		//��ʾ
				  		if(MyApplication.DEBUG)
				  		{
				  			Log.i("KKK","�����أ�");
				  			Toast.makeText(WelcomeActivity.this, "����ͼƬ�����أ������ظ�����",Toast.LENGTH_SHORT).show();
				  		}
				  		
	              		//ͼƬ
	              		bitmap = FileNetManager.getLoacalBitmap(app.getLocalPicPath());
	              		setBackground(bitmap);
	              		//bitmap.recycle();
	              		
	              		//�ı�
	              		todayString=FileNetManager.ReadMottoFromLocalPath(app.getLocalTextPath());
	              		setMottoText(todayString);
	              		return;
	              	}
				  	if(!(app.isNetworkConnected()))//���粻ͨ����ȡ�ϴεĻ���
				  	{
				  		
				  		//֪ͨ
				  		if(MyApplication.DEBUG)
				  		{
				  			Toast.makeText(WelcomeActivity.this, "����״���ܲAmergin�޷�Ϊ������",Toast.LENGTH_SHORT).show();
				  			Log.i("KKK","δ����1��");
				  		}
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
				  //	Toast.makeText(WelcomeActivity.this, "����ͼƬδ���أ�����ing",Toast.LENGTH_SHORT).show();
	              	 new Thread(new Runnable(){  
	                     @Override  
	                     public void run() {  
	                    	 Log.i("UserData","��ȡ���������1");
	                    	 //�û���Ϣ����

	                    	 //����·��
	                    	 String requestString=MyApplication.domain+"/picture/welcome.php?user="+MyApplication.UDM.id;
	                    	 Log.i("UserData","�����ַ���"+requestString);
	                    	 //��ȡ��ӭͼƬ��·���Լ�ÿ��һ�仰
	                    	 String motto="";
	                    	 String pictureString="";
	                    	 String resultString=FileNetManager.postRequestToHttp(requestString,null);
	                    	 Log.i("UserData","����ַ���"+resultString);
	                    	 JSONObject ja=null;
	                    	 try {
								ja=new JSONObject(resultString);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Log.i("UserData","json�����������");
								return;
							}
	                    	 try {
								switch(ja.getInt("errcode"))
								 {
								 case -1://ϵͳ�쳣
									 throw new Exception("ϵͳ�쳣");
								 case 0://�ɹ�
									 
									 pictureString=ja.getString("background");
									 motto=ja.getString("content");
									 Log.i("UserData","�ɹ�"+pictureString+";"+motto);
									 
									 break;
								 case 1: //�Ƿ�����
									 throw new Exception("�Ƿ�����");
								 case 2://���ݲ�����
									 throw new Exception("���ݲ�����");
								 }
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								
								Log.i("UserData","��ȡ���������"+e.getMessage());return;
							}
	                    	 Log.i("UserData","��ȡ���������5");
	                    	 
	                    	 
	                    	 
	                    	 //����ͼƬ�����浽����
	                         bitmap = FileNetManager.getImage(pictureString);  
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
            	init();//����
            	PlayBGM();//����Ӧ�������ı�������
              	
              	
              	app = (MyApplication)getApplication();
              	MyApplication.UDM=UserDateManager.getLocalUserData();
              	
              	Log.i("UserDateManager",""+MyApplication.UDM.id+" "+MyApplication.UDM.username+"- "+MyApplication.UDM.PlayOnStart);
              	
              	
              	getTodayPicAndTextAndWeather(); //���û�ӭҳ��ı���ͼƬ���ı�
              
              	
              	
              //���ö��������Խ�����AlphaAnimation
              //��һ������ֵ0.3fΪ��ʼ��͸����Ϊ50%���ڶ�������ֵ1.0fΪ������͸����Ϊ100%������͸����
              AlphaAnimation alphaAnimation = new AlphaAnimation(0.6f, 1.0f);
              //���������ó���ʱ�䣬��������ã���ʱ��Ϊ0�������Ϳ�����Ч��
              int time=6000;
              if(MyApplication.DEBUG)time=1000;
              alphaAnimation.setDuration(time);
              //�����ǵı������ж���
              alphaAnimation.setAnimationListener(new AnimationListener(){
            	  				@Override  //����һ��ʼ��ִ�����·���
                                  public void onAnimationStart(Animation animation) {
            	  					
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
					Log.i("KLL","����");
				}
				catch(Exception e)
				{
					Log.i("KLL",",����"+e.getMessage());
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
					// ����Ԥ���ɹ��ص� ����������Ϣ
								
					//�����������������ȫ�ֱ���
					MyApplication.weather=Weather_List.GetIdFromName(aMapLocalWeatherLive.getWeather());
					//��������ͼƬ	
					  if(MyApplication.DEBUG)
					Toast.makeText(
							this,
							(aMapLocalWeatherLive).getWeather()+""+Weather_List.getResImageById(MyApplication.weather)
									, Toast.LENGTH_SHORT)
							.show();
//					weather_icon.setImageDrawable(getResources().getDrawable(Weather_List.getResImageById(MyApplication.weather)
//							));
	              	
					
					//weather_icon.setBackgroundResource(Weather_List.getResImageById(MyApplication.weather));
				// ��ȡ����Ԥ��
					
				} else {

					// ��ȡ����Ԥ��ʧ��
					Toast.makeText(
							this,
							"��ȡ����Ԥ��ʧ��:"
									+ aMapLocalWeatherLive.getAMapException()
											.getErrorMessage(), Toast.LENGTH_SHORT)
							.show();
				 
				}
				
				
	}
	
	private void init() {
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		//��ȡʵʱ����Ԥ��
		//�����Ҫͬʱ����ʵʱ��δ��������������ȷ����λ��ȡλ�ú�ʹ��,�ֿ����ã��ɺ��Ա��䡣
		mLocationManagerProxy.requestWeatherUpdates(
				LocationManagerProxy.WEATHER_TYPE_LIVE, this);
		Log.i("WWW","end1");
	}

}
