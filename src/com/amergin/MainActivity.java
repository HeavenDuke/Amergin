package com.amergin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import Lrc.LyricDownloadManager;
import Lrc.SlidingDrawerManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.amergin.PushSlider.OnPageChangedListener;

public class MainActivity extends Activity {
	private PushSlider mPushView;
	private ImageButton playOrPause_btn;
	private ImageButton next_btn;
	private ImageButton isLoop_rbtn;
	private MyApplication app;
	private ImageButton like_rbtn;
	private ImageButton dislike_rbtn;
	private long mExitTime;
	private Handler handlerForBackground;
	private Timer timer = new Timer(); 
	private TimerTask task; 

	private String[] tempLocalMusicPathArry=new String[]{
			"ttpod/song/�����ֻ�.mp3",
			"ttpod/song/1.mp3",
			"ttpod/song/�ԹҶ���֦.mp3"
	};
	private String[] tempCloudMusicPathArry=new String[]{
			"http://pro04d2f76e.pic18.websiteonline.cn/upload/8g4q.mp3",
			"http://pro04d2f76e.pic18.websiteonline.cn/upload/3jal.mp3",
			"http://pro04d2f76e.pic18.websiteonline.cn/upload/ciq7.mp3",
			"http://pro04d2f76e.pic18.websiteonline.cn/upload/f7st.mp3",
			"http://pro04d2f76e.pic18.websiteonline.cn/upload/dcqy.mp3"
			
	};
	private int lindex=-1;
	private String tempMusicPath="http://pro04d2f76e.pic18.websiteonline.cn/upload/8g4q.mp3";
	
	private ImageView background1;
	private ImageView background2;
	private boolean NoChangeBackground=false;
	private Bitmap lastBitmap=null;
	private Bitmap currentBitmap=null;
	private boolean isPlay=false;
	private MediaPlayer mmp=null;
	Thread musicThread=null;
	private boolean isLoop=false;
	private SeekBar seekBar;
	private boolean SeekBarChagnging=false;
	private boolean nextBtnReady=true;  //�ᱼ....Ϊ��Ӧ��˲�䰴��n����һ��...
	//------��ô�����---- = =
//	LinearLayout music_lrc;
	LinearLayout music_info;
	SlidingDrawerManager LrcSDM;
	ListView LrcView;
	TextView title;
	TextView artist;
	//---��������
	int current_view_id=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        app = (MyApplication) getApplication();
        setContentView(R.layout.main);
        //---------------���ֳ�ʼ��----------
	    PushSliderIniti();  //����ҳ��
	    findViews();  //�ؼ���
	    ListenerBind(); //�¼���
	    BackgroundIniti(); //������ʼ��
	    SeekBarIniti(); //�������ĳ�ʼ��
	    MusicInfoIniti(); //������Ϣ�ĳ�ʼ��
    }
    
    void test()
    {
	    Log.i("test","����???");
	    //---���Դ���----
	    new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				MediaPlayer mmp1 = new MediaPlayer();  
				Log.i("test","��ʼʵ����");
				 String url="http://pro04d2f76e.pic18.websiteonline.cn/upload/8g4q.mp3";  
				 mmp1.setAudioStreamType(AudioManager.STREAM_MUSIC);
				 try {
					 mmp1.setDataSource(url);
					 mmp1.prepare();
				} catch (Exception e){
					// TODO Auto-generated catch block
					Log.i("test","����"+e.getMessage());
					e.printStackTrace();
				}

				 mmp1.setOnPreparedListener(new OnPreparedListener(){
					@Override
					public void onPrepared(MediaPlayer arg0) {
						// TODO Auto-generated method stub
						//Toast.makeText(MainActivity.this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
						arg0.start();
					}
				 });
			}}).start();
    }
    
    private void PushSliderIniti() {
	    mPushView = (PushSlider)findViewById(R.id.push_view);
	    int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
	    //mPushView.setPageWidth(PushSlider.SLIDER_PAGE_LEFT, screenWidth/2);
	    mPushView.setPageWidth(PushSlider.SLIDER_PAGE_LEFT, 3*screenWidth/4+1);
	    mPushView.setPageWidth(PushSlider.SLIDER_PAGE_RIGHT, screenWidth);
	    mPushView.setOnPageChangedListener(new myOnPageChangedListener());
	}
    
	private void MusicInfoIniti() {
		// TODO Auto-generated method stub
    	//changeMusicInfo();
	}
    
    void changeMusicInfo()
    {
    	if(music_info.getVisibility()==View.INVISIBLE)
    	{
 //   		music_lrc.setVisibility(View.INVISIBLE);
    		music_info.setVisibility(View.VISIBLE);
    	}
    	else
    	{
  //  		music_lrc.setVisibility(View.VISIBLE);
    		music_info.setVisibility(View.INVISIBLE);
    	}
    }
	

    private void SeekBarIniti() {
		// TODO Auto-generated method stub
    	final boolean d=true;
    	Timer SeekBarmTimer = new Timer();    
    	TimerTask SeekBarmTimerTask = new TimerTask() {    
             @Override    
             public void run() {     
                 if(SeekBarChagnging||mmp==null||!mmp.isPlaying()){
                     return;
                 }
                 int temp_p=mmp.getCurrentPosition();
                 if(temp_p<seekBar.getMax())
                 {
                	 seekBar.setProgress(temp_p);
                	 Message msg=new Message();
                	 msg.what=2;
                	 msg.arg1=temp_p;
                	 handlerForBackground.sendMessage(msg); 
                 }
                	 
             }    
         };   
         SeekBarmTimer.schedule(SeekBarmTimerTask, 0, 10);   
	}

	private void BackgroundIniti() {
		Resources res = getResources();
		lastBitmap= BitmapFactory.decodeResource(res, R.drawable.welcome2);
		currentBitmap= BitmapFactory.decodeResource(res, R.drawable.welcome);
    	handlerForBackground=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what)
				{
				case 0:
				//	Log.i("KKK","��ͼƬ");
					Toast.makeText(MainActivity.this,"�û���", Toast.LENGTH_SHORT).show();
					if(current_view_id!=1)return;
					    currentBitmap=(Bitmap)msg.obj;
						imageFadeChange();
					break;
				case 1:
				//	Log.i("KKK","�û�ͼƬ��");
					
			   		new Thread(new Runnable(){
						@Override
						public void run() {
							//---------�˴�Ӧ��Ϊ�������ȡ�������������������⣬�ȴ������ط���ȡͼƬ
							String[] str=new String[]{
									"http://e.hiphotos.baidu.com/image/pic/item/f3d3572c11dfa9ecc675b97f61d0f703918fc17b.jpg",
									"http://g.hiphotos.baidu.com/image/pic/item/b64543a98226cffc60dcf5deba014a90f703eaec.jpg",
									"http://c.hiphotos.baidu.com/image/pic/item/d000baa1cd11728b056c3843cbfcc3cec3fd2c09.jpg",
									"http://c.hiphotos.baidu.com/image/pic/item/50da81cb39dbb6fd3066a6570a24ab18972b3785.jpg"};
							Random r=new Random();
							int n2 = r.nextInt(4);
							//--------------------------
							Bitmap bitmap=null;
							try{
								//��ʾ��������������������ٸĻ�������
								if(MyApplication.DEBUG)
								{
									bitmap=FileNetManager.getImage(str[n2]);//����ͼƬ
								}
								else
								{
									bitmap=FileNetManager.getImage(FileNetManager.getContentFromUrl(MyApplication.backgroundUrlStirng, null));//����ͼƬ
								}
								if(bitmap==null&&lastBitmap!=null)bitmap=currentBitmap;
								
							}catch(Exception e)
							{
								Log.i("KKK","����ͼƬ����ʧ�ܲ��Ҷ�ȡ����Ĭ��ͼƬʧ��");
								return;
							}
							
							if(bitmap==null)return;
							Message msg=new Message(); //֪ͨhandler��ͼƬ
							msg.what=0;
							msg.obj=bitmap;
							handlerForBackground.sendMessage(msg);
							
							//�����б�Ҫ˯��ô....�������Ϣ���Ƕ�������ֶ����Զ���˯��Ӧ�û��б�Ҫ�˰�
							try {
								Thread.sleep(8000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
			   	}).start();
					break;
				case 2:
					if(current_view_id!=1)return;
					if(NoChangeBackground)return;
					int p=msg.arg1;
					if(p>100)p+=300;  //��ǰ300������ʾ���ʱ�����
				//	if(p>)
					Log.i("LLRC","�ĸ��¸����:"+p);
					LrcSDM.refreshUI(p);
					break;
				case 3:
					 Log.i("TTT","�յ���Ϣ����");
					 Song s=(Song)msg.obj;
					LrcSDM.reloadLrc(s.getLrcLocalPath());
					 try{
						 Log.i("TTT","������"+s.getMusicName());
						  title.setText(s.getMusicName());
						  Log.i("TTT","������"+s.getArtist());
						  artist.setText(s.getArtist());
						  
					 }
					catch(Exception e)
					{
						Log.i("TTT","����"+e.getMessage());
					}
					break;
				case 4:
					//����
				//	LrcSDM.toTop();
					//ò��û����
					//LrcSDM.toTop();
					break;	
				case 5:
					//�õ���ѭ���İ�ť��Ϊ����״̬
					
					//ò��û����
					isLoop_rbtn.setBackgroundResource(R.drawable.loop);
					break;
				}
				super.handleMessage(msg);
			}
    	};
    	
    	task = new TimerTask() { 
    	    @Override 
    	    public void run() { 
    	        // TODO Auto-generated method stub 
    	    	TimeToChangeBackground();
    	    } 
    	}; 
    	timer.schedule(task,0, 8000);
	}
    
	//��handler����ϢҪ��ͼ����Щϸ����Ҫ����
	void TimeToChangeBackground()
	{
		if(NoChangeBackground)return;
		NoChangeBackground=true;
        Message message = new Message();
        message.what = 1; 
        handlerForBackground.sendMessage(message); 
	}
    //����ͼƬ���л�
    @SuppressLint("NewApi")
	void imageFadeChange()
    {
    	//background1����background2�ĵײ���˼·���Ƚ��ױߵ�ͼƬ������Ȼ���ϲ��ͼƬ����͸�������¶�֮���ϲ���Ϊͬ����ͼƬ���һع鲻͸��
    	//ʵ�����������ﲻ���У��ᱼ������
    	if(lastBitmap!=null)
    	{
    		background2.setImageBitmap(lastBitmap); //ǰ��һ��
    		if(background2.getVisibility()==View.INVISIBLE)
    		{
    			background2.setVisibility(View.VISIBLE);
    		}
    			  
    	}
    	background1.setImageBitmap(currentBitmap);
    	Bitmap temp=lastBitmap.copy(Bitmap.Config.ARGB_8888, true);
    	lastBitmap=currentBitmap;
    	currentBitmap=temp;
    	 AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
         alphaAnimation.setAnimationListener(new AnimationListener(){
			@Override
			public void onAnimationEnd(Animation arg0) {
				//if(current_view_id==1)
					background2.setVisibility(View.INVISIBLE);
				NoChangeBackground=false;
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {}
			@Override
			public void onAnimationStart(Animation arg0) {}
         });
         alphaAnimation.setDuration(2000);//����ʱ��
         background2.startAnimation(alphaAnimation);
    }
    //�󶨿ؼ�
	void findViews()
	{
          playOrPause_btn =(ImageButton)findViewById(R.id.playOrPuse);
          title =(TextView)findViewById(R.id.title);
          artist =(TextView)findViewById(R.id.artist);
          next_btn =(ImageButton)findViewById(R.id.next);
          isLoop_rbtn =(ImageButton)findViewById(R.id.isLoop);
          dislike_rbtn =(ImageButton)findViewById(R.id.dislike);
          like_rbtn =(ImageButton)findViewById(R.id.like);
          background1=(ImageView) findViewById(R.id.background1);
          background2=(ImageView) findViewById(R.id.background2);
          seekBar=(SeekBar) findViewById(R.id.seekBar1);
  //        music_lrc=(LinearLayout) findViewById(R.id.musicLrc);
          music_info=(LinearLayout) findViewById(R.id.musicInfo);
          LrcView=(ListView) findViewById(R.id.lyricshow);
          LrcSDM=new SlidingDrawerManager(this,LrcView);
          //LrcView.set
          
   }
	
    //���¼�
	void ListenerBind()
   {
          btnListener blst= new btnListener();
          playOrPause_btn .setOnClickListener(blst);
          next_btn .setOnClickListener(blst);
          isLoop_rbtn .setOnClickListener(blst);
          seekBar.setOnSeekBarChangeListener(new SeekBarListener());
      //    music_lrc .setOnClickListener(blst);
          music_info .setOnClickListener(blst);
          like_rbtn.setOnClickListener(blst);
          dislike_rbtn.setOnClickListener(blst);
          //music_lrc.setOnDragListener(new dragLayoutListener());
   }
   //��ť�¼������ࣨ ��
    class btnListener implements OnClickListener{
          @Override
          public void onClick(View arg0) {
                // TODO Auto-generated method stub
        	  SeekBarChagnging=true;
                switch (arg0.getId())
               {
                case R.id.playOrPuse :
                	if(isPlay)
                	{
                		playOrPause_btn.setBackgroundResource(R.drawable.play);
                		isPlay=false;
                		PauseMusic();
                	}
                	else
                	{
                		playOrPause_btn.setBackgroundResource(R.drawable.pause);
                		isPlay=true;
                		PlayMusic();
                	}
                      break ;
                case R.id.next :
                	 seekBar.setProgress(0);
                	 PlayNextMusic();
                     break ;
                case R.id.isLoop :
                     Log. i( "KKK", "����ѭ��" );
                     if(isLoop)
                     {
                    	 //ȡ������
                    	 isLoop_rbtn.setBackgroundResource(R.drawable.noloop);
                    	 isLoop=false;
                     }
                     else
                     {
                    	 //����Ϊ����
                    	 isLoop_rbtn.setBackgroundResource(R.drawable.loop);
                    	 isLoop=true;
                     }
                     break ;
                case R.id.musicInfo:
   //             case R.id.musicLrc:
                	changeMusicInfo();
                	break;
                case R.id.like:
                	Toast.makeText(MainActivity.this,"ϲ�����׸�",Toast.LENGTH_SHORT).show();
                	break;
                case R.id.dislike:
                	Toast.makeText(MainActivity.this,"��ϲ�����׸�",Toast.LENGTH_SHORT).show();
                	break;
                	
               }
                SeekBarChagnging=false;
         }
         
   }
    class SeekBarListener implements OnSeekBarChangeListener {  
        public void onProgressChanged(SeekBar seekBar, int progress,  
                boolean fromUser) {
        }  
  
        public void onStartTrackingTouch(SeekBar seekBar) {  
        	SeekBarChagnging=true;    
        }  
  
        public void onStopTrackingTouch(SeekBar seekBar) {  
        	if(mmp!=null)
        	{
        		SetMusicProcess(seekBar.getProgress());
        		SeekBarChagnging=false;
        	}
        	else
        	{
        		seekBar.setProgress(0);
        	}
 
        }  
  
    }  
    
    
    void SetMusicProcess(int p)
    {
    	if(mmp!=null)
    	{
    		mmp.seekTo(p);
    		LrcSDM.refreshUI(p);
    	}
    	
    }
    
    void PlayNextMusic()
    {
    	if(!nextBtnReady)return;
    	nextBtnReady=false;
    //	NoChangeBackground=true;
    	TimeToChangeBackground();
        PauseMusic();
        NextMusicIniti();
        PlayMusic();
        playOrPause_btn.setBackgroundResource(R.drawable.pause);
		isPlay=true;
    }
    void  PauseMusic()
    {
		Log.i("TTT","������ͣ");
		if(mmp!=null)mmp.pause();
		Log.i("TTT","������ͣ��");
    }
    void CloudPlay()
    {
    	 Log.i("TTT","���뺯��CloudPlay");
    	//����ģʽ...
		 mmp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		 try {
			 Song song=JsonTool.getTodaySong();
			 String nextPath=getNextCloudMusicpath();
			 nextPath=song.getMusicUrl();
			 Log.i("TTT","������ӣ�"+song.getLyricUrl()+";���֣�"+song.getArtist()+";������"+song.getMusicName());
			 Log.i("TTT","mp3���ӣ�"+song.getMusicUrl());
			 
			 LyricDownloadManager test=new LyricDownloadManager();
			 Log.i("LrcDown","start");
			 String lrcPath=test.downLyricContent(song.getLyricUrl(), song.getArtist(), song.getMusicName());
			 Log.i("LrcDown","done");
			 song.setLrcLocalPath(lrcPath);
			 //֪ͨlistView����
			 Log.i("TTT","��ʼ����Ϣ֪ͨ����");
			 Message msg=new Message();
			 msg.obj=song;
			 msg.what=3;
			 handlerForBackground.sendMessage(msg);
			 mmp.setDataSource(nextPath);
			 Log.i("TTT","���ӣ�"+nextPath);
			 mmp.prepare();
			 Log.i("JKL","������Ϣ1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.i("test","����"+e.getMessage());
			e.printStackTrace();
		}

		 mmp.setOnPreparedListener(new OnPreparedListener(){
			@Override
			public void onPrepared(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				//Toast.makeText(MainActivity.this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
				 Log.i("TTT","׼�����");
				arg0.start();
				isPlay=true;
			}
		 });
    }
    
    void LocalPlay()
    {
		Log.i("TTT","��ʼ�����");
		try{
			mmp.reset();
			String tempStr=getNextMusicpath();
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
    }
    void  PlayMusic()
    {
    	if(mmp!=null)
		{
			mmp.start();
			return;
		}
		musicThread=new Thread(new Runnable(){
			@Override
			public void run() {
				if(!(app.isNetworkConnected())){
					//����Ϣ��ʾ˵û���Ų��˸���....
					return;
				}
					if(mmp!=null){mmp.release();mmp.reset();}
					mmp=null;
					mmp=new MediaPlayer();
					CloudPlay();
					//if()�ɵ��ñ��ز��ŵĺ����������ű����ļ�....
					mmp.setOnCompletionListener(new OnCompletionListener(){
					@Override
					public void onCompletion(MediaPlayer arg0) {
						// TODO Auto-generated method stub
						 Log.i("TTT","������ϻ���һ��");
							if(!(app.isNetworkConnected()))
							{
								//������粻ͨ��Ĭ���Զ����ɵ���ѭ��
								isLoop=true;
								//����ͼ����ʽ������������У�ֻ�÷���Ϣ�ˣ���д�غò�����
								Message msg=new Message();
								msg.what=5;
								msg.arg1=1;
								handlerForBackground.sendMessage(msg);
								
							}
						if(isLoop)
						{
							mmp.start();
							//�������ȥ
							Message msg=new Message();
							msg.what=4;
							handlerForBackground.sendMessage(msg);
						}
						else
						{
								PlayNextMusic();
						}
						
					}
				});
				mmp.start();
				nextBtnReady=true;
				seekBar.setMax(mmp.getDuration());  //
			//	Log.i("JKL",""+mmp.getDuration()+"����");
			}
		});
		musicThread.start();
		Log.i("TTT","���ֿ�ʼ���");
    }
	void  NextMusicIniti()
	{
		mmp=null;
		if(musicThread!=null)musicThread.interrupt();
		musicThread=null;
	}
	String getNextMusicpath(){
		if(!isLoop){
			lindex++;
			if(lindex>=3)lindex=0;
		}
		if(lindex<=0)lindex=0;
		tempMusicPath=app.SDPATH+tempLocalMusicPathArry[lindex];
		Log.i("TTT","�ѻ���"+tempMusicPath);
		Log.i("TTT","��ǰ����"+lindex);
		return tempMusicPath;
	}
    
	String getNextCloudMusicpath()
	{
		if(!isLoop){
			lindex++;
			if(lindex>=5)lindex=0;
		}
		if(lindex<=0)lindex=0;
		tempMusicPath=tempCloudMusicPathArry[lindex];
		return tempMusicPath;
	}
	
    private class myOnPageChangedListener implements OnPageChangedListener{

		@Override
		public void onPageChanged(View v, int whichHandle) {
			// TODO Auto-generated method stub
				NoChangeBackground=(whichHandle!=1); //����֮��
				current_view_id=whichHandle;
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		   if (keyCode == KeyEvent.KEYCODE_BACK) {
               if ((System.currentTimeMillis() - mExitTime) > 2000) {
                       Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
                       mExitTime = System.currentTimeMillis();      
               } else {
            	   System.exit(0);
               }
               return true;
       }
		  
		return super.onKeyUp(keyCode, event);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //------------���ߺ���-----------
	public Location GetLocation(){
		LocationManager lManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Location location=lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		return location;
	}
	public int GetWeather(Location location){
		int weathercode=0;
		double latitude=location.getLatitude();//γ��
		double longitude=location.getLongitude();//����
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
}


//----------������-------------
class Mood_List{
	public static final int MOOD_EXCITED=0;
	public static final int MOOD_HAPPY=1;
	public static final int MOOD_NORMAL=2;
	public static final int MOOD_DISAPPOINTED=3;
	public static final int MOOD_ANXIOUS=4;
	public static final int MOOD_ANNOYED=5;
	public static final int MOOD_ANGRY=6;
	public static final int MOOD_SAD=7;
}

class Behaviour_List{
	
}

class Weather_List{
	
}


