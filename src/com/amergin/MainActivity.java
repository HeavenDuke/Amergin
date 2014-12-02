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
			"ttpod/song/百年轮回.mp3",
			"ttpod/song/1.mp3",
			"ttpod/song/自挂东南枝.mp3"
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
	private boolean nextBtnReady=true;  //泪奔....为了应对瞬间按下n次下一曲...
	//------这么多变量---- = =
//	LinearLayout music_lrc;
	LinearLayout music_info;
	SlidingDrawerManager LrcSDM;
	ListView LrcView;
	TextView title;
	TextView artist;
	//---测试用类
	int current_view_id=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        app = (MyApplication) getApplication();
        setContentView(R.layout.main);
        //---------------各种初始化----------
	    PushSliderIniti();  //三个页面
	    findViews();  //控件绑定
	    ListenerBind(); //事件绑定
	    BackgroundIniti(); //背景初始化
	    SeekBarIniti(); //进度条的初始化
	    MusicInfoIniti(); //音乐信息的初始化
    }
    
    void test()
    {
	    Log.i("test","错误???");
	    //---测试代码----
	    new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				MediaPlayer mmp1 = new MediaPlayer();  
				Log.i("test","开始实例化");
				 String url="http://pro04d2f76e.pic18.websiteonline.cn/upload/8g4q.mp3";  
				 mmp1.setAudioStreamType(AudioManager.STREAM_MUSIC);
				 try {
					 mmp1.setDataSource(url);
					 mmp1.prepare();
				} catch (Exception e){
					// TODO Auto-generated catch block
					Log.i("test","错误"+e.getMessage());
					e.printStackTrace();
				}

				 mmp1.setOnPreparedListener(new OnPreparedListener(){
					@Override
					public void onPrepared(MediaPlayer arg0) {
						// TODO Auto-generated method stub
						//Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
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
				//	Log.i("KKK","换图片");
					Toast.makeText(MainActivity.this,"该换了", Toast.LENGTH_SHORT).show();
					if(current_view_id!=1)return;
					    currentBitmap=(Bitmap)msg.obj;
						imageFadeChange();
					break;
				case 1:
				//	Log.i("KKK","该换图片了");
					
			   		new Thread(new Runnable(){
						@Override
						public void run() {
							//---------此处应该为从网络获取，但是由于流量的问题，先从其他地方获取图片
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
								//表示还是心疼流量啊，最后再改回来好了
								if(MyApplication.DEBUG)
								{
									bitmap=FileNetManager.getImage(str[n2]);//下载图片
								}
								else
								{
									bitmap=FileNetManager.getImage(FileNetManager.getContentFromUrl(MyApplication.backgroundUrlStirng, null));//下载图片
								}
								if(bitmap==null&&lastBitmap!=null)bitmap=currentBitmap;
								
							}catch(Exception e)
							{
								Log.i("KKK","背景图片下载失败并且读取本地默认图片失败");
								return;
							}
							
							if(bitmap==null)return;
							Message msg=new Message(); //通知handler换图片
							msg.what=0;
							msg.obj=bitmap;
							handlerForBackground.sendMessage(msg);
							
							//这里有必要睡眠么....如果发消息的是多出比如手动和自动，睡眠应该会有必要了吧
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
					if(p>100)p+=300;  //提前300毫秒提示歌词时间进度
				//	if(p>)
					Log.i("LLRC","改更新歌词了:"+p);
					LrcSDM.refreshUI(p);
					break;
				case 3:
					 Log.i("TTT","收到消息换词");
					 Song s=(Song)msg.obj;
					LrcSDM.reloadLrc(s.getLrcLocalPath());
					 try{
						 Log.i("TTT","换歌名"+s.getMusicName());
						  title.setText(s.getMusicName());
						  Log.i("TTT","换歌手"+s.getArtist());
						  artist.setText(s.getArtist());
						  
					 }
					catch(Exception e)
					{
						Log.i("TTT","换了"+e.getMessage());
					}
					break;
				case 4:
					//重置
				//	LrcSDM.toTop();
					//貌似没用上
					//LrcSDM.toTop();
					break;	
				case 5:
					//让单曲循环的按钮变为激活状态
					
					//貌似没用上
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
    
	//向handler发消息要求换图，有些细节需要调整
	void TimeToChangeBackground()
	{
		if(NoChangeBackground)return;
		NoChangeBackground=true;
        Message message = new Message();
        message.what = 1; 
        handlerForBackground.sendMessage(message); 
	}
    //背景图片的切换
    @SuppressLint("NewApi")
	void imageFadeChange()
    {
    	//background1是在background2的底部，思路：先将底边的图片换掉，然后上层的图片渐渐透明，完事儿之后上层设为同样的图片并且回归不透明
    	//实践表明在这里不可行，泪奔啊啊啊
    	if(lastBitmap!=null)
    	{
    		background2.setImageBitmap(lastBitmap); //前后一致
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
         alphaAnimation.setDuration(2000);//设置时间
         background2.startAnimation(alphaAnimation);
    }
    //绑定控件
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
	
    //绑定事件
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
   //按钮事件监听类（ ）
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
                     Log. i( "KKK", "单曲循环" );
                     if(isLoop)
                     {
                    	 //取消单曲
                    	 isLoop_rbtn.setBackgroundResource(R.drawable.noloop);
                    	 isLoop=false;
                     }
                     else
                     {
                    	 //设置为单曲
                    	 isLoop_rbtn.setBackgroundResource(R.drawable.loop);
                    	 isLoop=true;
                     }
                     break ;
                case R.id.musicInfo:
   //             case R.id.musicLrc:
                	changeMusicInfo();
                	break;
                case R.id.like:
                	Toast.makeText(MainActivity.this,"喜欢这首歌",Toast.LENGTH_SHORT).show();
                	break;
                case R.id.dislike:
                	Toast.makeText(MainActivity.this,"不喜欢这首歌",Toast.LENGTH_SHORT).show();
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
		Log.i("TTT","音乐暂停");
		if(mmp!=null)mmp.pause();
		Log.i("TTT","音乐暂停完");
    }
    void CloudPlay()
    {
    	 Log.i("TTT","进入函数CloudPlay");
    	//设置模式...
		 mmp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		 try {
			 Song song=JsonTool.getTodaySong();
			 String nextPath=getNextCloudMusicpath();
			 nextPath=song.getMusicUrl();
			 Log.i("TTT","歌词链接："+song.getLyricUrl()+";歌手："+song.getArtist()+";歌名："+song.getMusicName());
			 Log.i("TTT","mp3链接："+song.getMusicUrl());
			 
			 LyricDownloadManager test=new LyricDownloadManager();
			 Log.i("LrcDown","start");
			 String lrcPath=test.downLyricContent(song.getLyricUrl(), song.getArtist(), song.getMusicName());
			 Log.i("LrcDown","done");
			 song.setLrcLocalPath(lrcPath);
			 //通知listView换词
			 Log.i("TTT","开始发消息通知换词");
			 Message msg=new Message();
			 msg.obj=song;
			 msg.what=3;
			 handlerForBackground.sendMessage(msg);
			 mmp.setDataSource(nextPath);
			 Log.i("TTT","链接："+nextPath);
			 mmp.prepare();
			 Log.i("JKL","歌曲信息1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.i("test","错误"+e.getMessage());
			e.printStackTrace();
		}

		 mmp.setOnPreparedListener(new OnPreparedListener(){
			@Override
			public void onPrepared(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				//Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				 Log.i("TTT","准备完毕");
				arg0.start();
				isPlay=true;
			}
		 });
    }
    
    void LocalPlay()
    {
		Log.i("TTT","初始化完毕");
		try{
			mmp.reset();
			String tempStr=getNextMusicpath();
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
					//发消息提示说没网放不了歌曲....
					return;
				}
					if(mmp!=null){mmp.release();mmp.reset();}
					mmp=null;
					mmp=new MediaPlayer();
					CloudPlay();
					//if()可调用本地播放的函数，即播放本地文件....
					mmp.setOnCompletionListener(new OnCompletionListener(){
					@Override
					public void onCompletion(MediaPlayer arg0) {
						// TODO Auto-generated method stub
						 Log.i("TTT","播放完毕换下一个");
							if(!(app.isNetworkConnected()))
							{
								//如果网络不通，默认自动换成单曲循环
								isLoop=true;
								//更改图标样式不能在这里进行，只好发消息了，唉写地好不优雅
								Message msg=new Message();
								msg.what=5;
								msg.arg1=1;
								handlerForBackground.sendMessage(msg);
								
							}
						if(isLoop)
						{
							mmp.start();
							//歌词拉回去
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
			//	Log.i("JKL",""+mmp.getDuration()+"长度");
			}
		});
		musicThread.start();
		Log.i("TTT","音乐开始完毕");
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
		Log.i("TTT","已换成"+tempMusicPath);
		Log.i("TTT","当前索引"+lindex);
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
				NoChangeBackground=(whichHandle!=1); //无奈之举
				current_view_id=whichHandle;
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		   if (keyCode == KeyEvent.KEYCODE_BACK) {
               if ((System.currentTimeMillis() - mExitTime) > 2000) {
                       Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
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
    //------------工具函数-----------
	public Location GetLocation(){
		LocationManager lManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Location location=lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		return location;
	}
	public int GetWeather(Location location){
		int weathercode=0;
		double latitude=location.getLatitude();//纬度
		double longitude=location.getLongitude();//经度
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
}


//----------其他类-------------
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


