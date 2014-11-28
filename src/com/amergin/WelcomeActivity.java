package com.amergin;

import java.io.File;
import java.io.FileInputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
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
   void setBackground(Bitmap _bitmap){
	   if(background==null)background=(ImageView) findViewById(R.id.background);
 		if(_bitmap!=null)
 			background.setImageBitmap(_bitmap);
 		else
 			background.setBackgroundResource(R.drawable.background);
   }
    void setMottoText(String _str)
    {
    	if(mottoToday==null)mottoToday=(TextView)findViewById(R.id.mottoToday);
    	if(_str==""||_str==null||_str=="error!")_str="即使没有网络，也要假装和这个世界联系密切";
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

			  
			  
			  void getTodayPicAndText()
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
					Log.i("KKK","未下载，");
				  	if(!(app.isNetworkConnected()))//网络不通，读取上次的缓存
				  	{
				  		//通知
				  		Toast.makeText(WelcomeActivity.this, "网络状况很差，Amergin无法为您服务",Toast.LENGTH_SHORT).show();
				  		
				  		//图片
				  		bitmap=FileNetManager.getLastBitmap(app.getLocalPicPath());
				  		setBackground(bitmap);

	              		//文本
	              		todayString=FileNetManager.ReadMottoFromLocalPath(app.getLocalTextPath());
	              		setMottoText(todayString);
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
	            				 
	                        	 Message msg=new Message();
	                        	 msg.what=1;
	                        	 handler.sendMessage(msg);
	                         }
	                         
	                         //获得文本...写本地缓存
	                         todayString=FileNetManager.postRequestToHttp("http://amergin.sinaapp.com/WelcomeContent.php?date=2014-11-28",null);
	                         FileNetManager.WriteMottoToLocalPath(app.getLocalTextPath(), todayString);
	                         
	                         // Toast.makeText(WelcomeActivity.this, todayString,Toast.LENGTH_SHORT).show();
	                         //发消息...
	                         if(todayString!="")
	                         {
	                        	 Message msg=new Message();
	                        	 msg.what=2;
	                        	 handler.sendMessage(msg);
	                         }
	                         
	                         
	                         
	                     }
	                 }).start();
			  }

			  
	@Override
      protected void onCreate(Bundle savedInstanceState) {
              super.onCreate(savedInstanceState);
              View view =  View.inflate(this, R.layout.welcome, null);
           //   getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
              	setContentView(view);
              	app = (MyApplication)getApplication();
              	getTodayPicAndText(); //设置欢迎页面的背景图片和文本
              	Log. i( "KKK", "897" );
              //设置动画，渐显渐隐用AlphaAnimation
              //第一个参数值0.3f为开始的透明度为50%，第二个参数值1.0f为结束的透明度为100%，即不透明。
              AlphaAnimation alphaAnimation = new AlphaAnimation(0.6f, 1.0f);
              //给动画设置持续时间，如果不设置，则时间为0，动画就看不到效果
              alphaAnimation.setDuration(5000);
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
              PlayBGM();       
	  }

	private void PlayBGM() {
		// TODO Auto-generated method stub
		Thread musicThread=new Thread(new Runnable(){
			@Override
			public void run() {
						MediaPlayer mmp=new MediaPlayer();
						try{
							mmp.reset();
							String tempStr="";
							File file = new File(tempStr);
							Log.i("TTT","加载"+tempStr);
							FileInputStream fis = new FileInputStream(file);
							mmp.setDataSource(fis.getFD());
							mmp.prepare();
							fis.close();
						}
						catch(Exception e)
						{
							Log.i("KKK","音乐准备失败");
						}
							mmp.start();
					}	
			});
		musicThread.start();
		}


}
