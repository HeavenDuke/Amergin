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
    	if(_str==""||_str==null||_str=="error!")_str="��ʹû�����磬ҲҪ��װ�����������ϵ����";
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

			  
			  
			  void getTodayPicAndText()
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
					Log.i("KKK","δ���أ�");
				  	if(!(app.isNetworkConnected()))//���粻ͨ����ȡ�ϴεĻ���
				  	{
				  		//֪ͨ
				  		Toast.makeText(WelcomeActivity.this, "����״���ܲAmergin�޷�Ϊ������",Toast.LENGTH_SHORT).show();
				  		
				  		//ͼƬ
				  		bitmap=FileNetManager.getLastBitmap(app.getLocalPicPath());
				  		setBackground(bitmap);

	              		//�ı�
	              		todayString=FileNetManager.ReadMottoFromLocalPath(app.getLocalTextPath());
	              		setMottoText(todayString);
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
	            				 
	                        	 Message msg=new Message();
	                        	 msg.what=1;
	                        	 handler.sendMessage(msg);
	                         }
	                         
	                         //����ı�...д���ػ���
	                         todayString=FileNetManager.postRequestToHttp("http://amergin.sinaapp.com/WelcomeContent.php?date=2014-11-28",null);
	                         FileNetManager.WriteMottoToLocalPath(app.getLocalTextPath(), todayString);
	                         
	                         // Toast.makeText(WelcomeActivity.this, todayString,Toast.LENGTH_SHORT).show();
	                         //����Ϣ...
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
              	getTodayPicAndText(); //���û�ӭҳ��ı���ͼƬ���ı�
              	Log. i( "KKK", "897" );
              //���ö��������Խ�����AlphaAnimation
              //��һ������ֵ0.3fΪ��ʼ��͸����Ϊ50%���ڶ�������ֵ1.0fΪ������͸����Ϊ100%������͸����
              AlphaAnimation alphaAnimation = new AlphaAnimation(0.6f, 1.0f);
              //���������ó���ʱ�䣬��������ã���ʱ��Ϊ0�������Ϳ�����Ч��
              alphaAnimation.setDuration(5000);
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
							Log.i("TTT","����"+tempStr);
							FileInputStream fis = new FileInputStream(file);
							mmp.setDataSource(fis.getFD());
							mmp.prepare();
							fis.close();
						}
						catch(Exception e)
						{
							Log.i("KKK","����׼��ʧ��");
						}
							mmp.start();
					}	
			});
		musicThread.start();
		}


}
