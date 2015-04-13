package com.amergin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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

import com.amergin.WheelView.ArrayWheelAdapter;
import com.amergin.WheelView.OnWheelChangedListener;
import com.amergin.WheelView.WheelView;
import com.amergin.lrc.LyricDownloadManager;
import com.amergin.lrc.SlidingDrawerManager;
import com.amergin.lrc.StrokeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity {
	private MyApplication app;  //全局参数
	//-----一堆堆的参数
	private long mExitTime;   //完成连续按下两次按下退出时候退出
	public static Handler handlerForBackground; //处理UI更新的handelr
	private MediaPlayer mmp=null;  //播放音乐的工具
	private Thread musicThread=null;  //播放音乐的线程
	private boolean isLoop=false;   //是否单曲
	private boolean SeekBarChagnging=false;   //为了在拖动进度条的时候不无数次定为MediaPlayer的进度
	private boolean isDownloading=false;     //为了在下载下一曲的时候不接收下下一曲的请求
	private boolean BackgroundLock=false;     //为真时停止换背景图片
	private Bitmap lastBitmap=null;   //实现图片轮换需要的变量
	private Bitmap currentBitmap=null;//实现图片轮换需要的变量
	//---------------------------------
	private SlidingMenu mMenu;  //滑动页面
	private  Song currentSong=null; //当前播放的歌曲
	
	//--中间页面----
	private ImageView background1;	//两张背景实现淡入淡出
	private ImageView background2;    //两张背景实现淡入淡出
	public static ImageView centent_filter_bg;    //两张背景实现淡入淡出
	
	private ImageButton playOrPause_btn;
	private ImageButton next_btn;
	private ImageButton isLoop_rbtn;
	private ImageButton like_rbtn;
	private ImageButton dislike_rbtn;
	private SeekBar seekBar;  //进度条
	private LinearLayout music_info;  //歌曲信息
	private StrokeTextView musicInfoText;
	private SlidingDrawerManager LrcSDM;//管理歌词面板
	private ListView LrcView;  //歌词面板
	private StrokeTextView title;  //歌曲名字
	private TextView artist;  //歌手
	
	private TextView username;
	
	private LinearLayout InfoPanel;
	private  BootstrapCircleThumbnail Icon;
	private WheelView mood;   //情感选取控件
	private WheelView behavior;  //行为选取控件
	private ImageButton backgroundLocker;  //锁定背景图片
	private LinearLayout personinfo;  //个人信息
	private LinearLayout systemadjust;  //系统设置
	private LinearLayout logout;  //登出
	
	private LinearLayout LoginPanel;
	
	//----右侧面板
	private BootstrapButton like_list;
	private BootstrapButton dislike_list;
	private ListView dislike_listview;
	private ListView like_listview;
	private boolean cur_is_like=true;  //保留列表的显示
	
	
	
	public static final OnTouchListener TouchDark = new OnTouchListener() {
        
        public final float[] BT_SELECTED = new float[] {1,0,0,0,-50,0,1,0,0,-50,0,0,1,0,-50,0,0,0,1,0};
        public final float[] BT_NOT_SELECTED = new float[] {1,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0};
         
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.getBackground().setColorFilter(
                        new ColorMatrixColorFilter(BT_SELECTED));
                v.setBackgroundDrawable(v.getBackground());
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.getBackground().setColorFilter(
                        new ColorMatrixColorFilter(BT_NOT_SELECTED));
                v.setBackgroundDrawable(v.getBackground());
            }
            return false;
        }

    };
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.i("生命周期","onResume");
		
		if(isStoped)
		{
			LoginCheck();
			//mMenu.ComeToCenter();
			
			reInitValues();
			
			
			if(this.mMenu.current_view_index==1)
			{
				centent_filter_bg.setBackground(MainActivity.this.getResources().getDrawable(R.drawable.centent_filter_bg));
				centent_filter_bg.setVisibility(View.INVISIBLE);
			}
			
			
    		if(MyApplication.UDM.autochangeBkgTime>=0)
    		{
    			Log.i("背景切换","重启TImer");
    			
    			bkg_timer=new Timer();
    			bkg_timer.schedule(new TimerTask() { 
    			    @Override 
    			    public void run() { 
    			    	if(MyApplication.UDM.autochangeBkgTime>=0){
    			    			TimeToChangeBackground(0);
    			    	}
    			    } 
    			},MyApplication.UDM.autochangeBkgTime*1000, MyApplication.UDM.autochangeBkgTime*1000);
    		}
    		isStoped=false;
		}
		super.onResume();
	}


	private void reInitValues() {
		// TODO Auto-generated method stub
		UserDateManager  usrdata=MyApplication.UDM;
		Log.i("背景切换",usrdata.autochangeBkgTime+"切换");
		//加载头像
		if(usrdata.Icon!=null&&!usrdata.Icon.equals(""))
		{
			Bitmap	iconBitmap= BitmapFactory.decodeFile(usrdata.Icon);
			if(iconBitmap==null) //读取本地头像失败
			{
				usrdata.Icon=null;
				UserDateManager.setLocalUserData(usrdata);
			}
			else
			{
				this.Icon.setImage(iconBitmap);
				iconBitmap.recycle();  //回收？？
			}
		}
		else
		{
			this.Icon.setImage(R.drawable.headicon);
		}
		
		//更新用户名
		this.username.setText(usrdata.username);
	}


	void LoginCheck()
	{
		 if(MyApplication.UDM.id==-1)//未登录状态
		 {
			 LoginPanel.setVisibility(View.VISIBLE);
			 InfoPanel.setVisibility(View.GONE);
			 like_rbtn.setVisibility(View.INVISIBLE);
			 dislike_rbtn.setVisibility(View.INVISIBLE);
			 
		 }else
		 {
			 
			 InfoPanel.setVisibility(View.VISIBLE);
			 LoginPanel.setVisibility(View.GONE);
			 like_rbtn.setVisibility(View.VISIBLE);
			 dislike_rbtn.setVisibility(View.VISIBLE);
		 }
		 username.setText(MyApplication.UDM.username);
		// updateSongLists();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //不要标题栏
		setContentView(R.layout.main);
		app = (MyApplication) getApplication();
		
		//滑动页面
		mMenu = (SlidingMenu) findViewById(R.id.id_menu);
		mMenu.setmMenuRightPadding(160);
		mMenu.hdl=MainActivity.handlerForBackground;

		
		
		 findViews();  //控件绑定
		 ListenerBind(); //事件绑定
		 BackgroundIniti(); //背景初始化
		 SeekBarIniti(); //进度条的初始化
		 
		 //updateSongLists();
		 
		 if(MyApplication.UDM.PlayOnStart==1)
			 PlayNextMusic();
		 
	}

	private void checkLoginStatus()
	{
		if(MyApplication.UDM.id==-1)
		{
			//提示登陆好了
			Message msg=new Message();
			msg.what=8;
			msg.arg1=1;
			msg.obj="您还没登陆,无法查看喜好列表";
			handlerForBackground.sendMessage(msg);
			mMenu.ComeToLeft();
			return;
		}

	}
	
	

	private int isUpdating=0;
	private void updateSongLists() {
		// TODO Auto-generated method stub
		checkLoginStatus();
		if(isUpdating!=0)return;
		isUpdating=2;
		Log.i("UUU","拉取");
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				getListSong(false);
			}
		}).start();
		new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				getListSong(true);
			}
		}).start();
		Log.i("UUU","拉取结束");
	}

	void getListSong(boolean b)
	{
		Log.i("UUU","开始");
		String urlString=MyApplication.domain+"/user/listtaste.php?";
		urlString+=("&user="+MyApplication.UDM.id);
		if(b)
		{
			urlString+=("&type=0");
		}
		else
		{
			urlString+=("&type=1");
		}
		String resultString=FileNetManager.postRequestToHttp(urlString, null);
		
		Log.i("UUU","请求字符串的Get形式"+urlString);
		try {
			Log.i("UUU","获得返回结果"+resultString);
			JSONObject jo=new JSONObject(resultString);
			Message msg=new Message();
			msg.what=8;
			if(jo.getInt("errcode")==0)
			{
				msg.obj=("拉取成功"+resultString);
				JSONArray ja=jo.getJSONArray("list");
				Message msg2=new Message();
				msg2.what=9;
				msg2.arg1=1;
				if(b)msg2.arg1=0;
				msg2.obj=ja;
				handlerForBackground.sendMessage(msg2);
			}
			else
			{
				//报错
				Log.i("ListItem","报错"+jo.getString("errmsg"));
				msg.obj=("拉取失败："+jo.getString("errmsg"));
			}
			handlerForBackground.sendMessage(msg);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		finally{
			isUpdating--;
		}
		Log.i("Song","结束");
	}
	
	
	
	
	private boolean isAdding=false;
	private boolean addtaste_islike=false;
	private void addtaste(boolean islike)
	{
		checkLoginStatus();
		if(isAdding)return;isAdding=true;
		addtaste_islike=islike;
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub

				String urlString=MyApplication.domain+"/user/addtaste.php?";
				urlString+=("user="+MyApplication.UDM.id);
				urlString+=("&music="+currentSong.getMid());
				urlString+=("&behavior="+MyApplication.behavior);
				urlString+=("&weather="+MyApplication.weather);
				urlString+=("&mood="+MyApplication.mood);
				
				
				
				if(addtaste_islike)
				{
					urlString+=("&type=0");
				}
				else
				{
					urlString+=("&type=1");
				}
				
				Log.i("UUU","提交"+urlString);
				String resultString=FileNetManager.postRequestToHttp(urlString, null);
				String pre_str="";//前缀
				try {
					Log.i("UUU","获得返回结果"+resultString);
			
					JSONObject jo=new JSONObject(resultString);
					int errorcode=jo.getInt("errcode");
					if(errorcode==0)
					{
						Log.i("UUU","进来了");
						
						if(!addtaste_islike)pre_str="不";
						//报喜...
						Message msg2=new Message();
						msg2.what=8;
						msg2.arg1=1;
						msg2.obj="歌曲"+currentSong.getName()+"已添加到 "+pre_str+"喜欢列表";
						handlerForBackground.sendMessage(msg2);
					}
					else
					{
						Log.i("UUU","出错了");
						//报错
						Log.i("ListItem","报错+"+jo.getString("errmsg"));
						Message msg3=new Message();
						msg3.what=8;
						msg3.arg1=1;
						if(!addtaste_islike)pre_str="不";
						if(errorcode==3)//重复....这代码写的= =
						{
							msg3.obj="歌曲"+currentSong.getName()+"已在"+pre_str+"喜欢列表中";
							
						}else if(errorcode==4)//在另一个列表中
						{
							msg3.obj="歌曲"+currentSong.getName()+"已在"+pre_str+"喜欢列表中";
						}
						else{
							return;
						}
						handlerForBackground.sendMessage(msg3);						
						return;
					}
					
					
					
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
				finally{
					isAdding=false;	
				}
			}
		}).start();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//初始化进度条
    private void SeekBarIniti() {
		// TODO Auto-generated method stub
    	Timer SeekBarTimer = new Timer();    
    	TimerTask SeekBarmTimerTask = new TimerTask() {    
             @Override    
             public void run() {     
            	 try{
	                 if(SeekBarChagnging||mmp==null){
	                     return;
	                 }
	                 int temp_p;
	                 if(mmp!=null&&mmp.isPlaying())
	                 {
	                	  temp_p=mmp.getCurrentPosition();
	                	  if(temp_p<seekBar.getMax())
	                	  {
	                	 	 seekBar.setProgress(temp_p);
							 Message msg=new Message();
	                	     msg.what=2;
	                	     msg.arg1=temp_p;
	                	     handlerForBackground.sendMessage(msg); 
	                	  }
	                 }
            	 }
            	 catch(Exception e) {
            		 Log.i("ENE","what the hell");
            		 
            	 }
             }    
         };   
         SeekBarTimer.schedule(SeekBarmTimerTask, 0, 100);
	}
	
	private void BackgroundIniti() {
		Resources res = getResources();
		lastBitmap= BitmapFactory.decodeResource(res, R.drawable.welcome);
		currentBitmap= BitmapFactory.decodeResource(res, R.drawable.welcome2);
    	handlerForBackground=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what)
				{
				case 0: //处理换背景图片的消息
					    currentBitmap=(Bitmap)msg.obj;
						imageFadeChange();
					break;
				case 1:  //从网络端获取背景图片
					getBackgroungImage();
					break;
				case 2:
					int p=msg.arg1;
					if(p>0)p+=200;  //提前200毫秒提示歌词时间进度
					LrcSDM.refreshUI(p);
					break;
				case 3:  			//歌词显示部分载入新歌词，并更新
					Song s=(Song)msg.obj;
					Log.i("LrcDown","1"+s.getLrcLocalPath());
					LrcSDM.reloadLrc(s.getLrcLocalPath());
					Log.i("LrcDown","2");
					 try{
						  title.setText(s.getName());
						  artist.setText(s.getArtist());
					 }
					catch(Exception e)
					{
						Log.i("TTT","换了"+e.getMessage());
					}
					 music_info.setVisibility(View.INVISIBLE);
					 LrcView.setVisibility(View.VISIBLE);
					break;
				case 4://重置歌词显示模块，貌似没用上,妈蛋现在用上了
					//艺术家，歌曲名
					//Song s1=(Song)msg.obj;
					Log.i("reset","1");
					title.setText(currentSong.getName());
					artist.setText(currentSong.getArtist());
					String infoText="";
					infoText+="歌曲名:"+currentSong.getName()+"\n";
					infoText+="艺术家:"+currentSong.getArtist()+"\n";
					infoText+="专辑:"+currentSong.getAlbum();
					
					
					Log.i("reset","2");
					LrcSDM.reset();
					LrcView.setVisibility(View.INVISIBLE);
					music_info.setVisibility(View.VISIBLE);
					musicInfoText.setText(infoText);
					
					break;	
				case 5:
					//让单曲循环的按钮变为激活状态
					isLoop_rbtn.setBackgroundResource(R.drawable.loop);
					break;
					
				case 6://播放歌曲,来自列表
					Song ps=(Song) msg.obj;
					if(ps!=null)
					{
						currentSong=ps;
						MainActivity.this.title.setText(ps.getName());
						String infoT="";
						infoT+="歌曲名:"+currentSong.getName()+"\n";
						infoT+="艺术家:"+currentSong.getArtist()+"\n";
						infoT+="专辑:"+currentSong.getAlbum();
						LrcView.setVisibility(View.INVISIBLE);
						music_info.setVisibility(View.VISIBLE);
						musicInfoText.setText(infoT);
						
						
						Stop();
						PlayMusic(false);
						mMenu.ComeToCenter();
					}
					break;
				case 7://删除歌曲
					try{
						Song ds=(Song) msg.obj;
						deleteCloudSong(ds,msg.arg1==0);
					}
					catch(Exception e)
					{
						ppp_toast("删除bug"+e.getMessage());
					}

					break;
				case 8://发各种toast通知
					if(msg.arg1==1)
					{
						ppp_toast((String)msg.obj);
					}
					
					
					if(MyApplication.DEBUG){
						ppp_toast((String)msg.obj);
					}
					break;
				case 9: //更新喜好列表
					
					if(MainActivity.this.mMenu.current_view_index==2)
					{
						if(msg.arg1==0)//喜欢的
						{
							//like_listview.setVisibility(View.GONE);
							like_listview.setAdapter(new SongListItemAdapter(MainActivity.this,getListFromArray((JSONArray) msg.obj), handlerForBackground,0));
							//like_listview.setVisibility(View.VISIBLE);
							//mMenu.closeMenu();
						}else//不喜欢的
						{
							//	dislike_listview.setVisibility(View.GONE);
							dislike_listview.setAdapter(new SongListItemAdapter(MainActivity.this,getListFromArray((JSONArray) msg.obj), handlerForBackground,1));
						}
					}

					
					break;
				case 10: //这里是从mMenu里边发出的要求更新喜好列表
					updateSongLists();
					//like_listview.setVisibility(View.VISIBLE);
					if(cur_is_like)
						Log.i("BOOT","翻到喜欢");
					else
						Log.i("BOOT","翻到不喜欢");
					changeToLikePanel(cur_is_like);
					break;
				case 12:
					Drawable dr=MainActivity.this.getResources().getDrawable(R.drawable.centent_filter_bg);
					dr.setColorFilter(Color.BLACK,android.graphics.PorterDuff.Mode.MULTIPLY);
					centent_filter_bg.setImageDrawable(dr);
					centent_filter_bg.setAlpha(0.7f);
					centent_filter_bg.setVisibility(View.VISIBLE);
					break;
				case 11:
					centent_filter_bg.setBackground(MainActivity.this.getResources().getDrawable(R.drawable.centent_filter_bg));
					centent_filter_bg.setVisibility(View.INVISIBLE);
					
					break;
				}
				super.handleMessage(msg);
			}
    	};
    		

    			
		
	}
	
	private boolean isStoped=true;
	private Timer bkg_timer=new Timer();	
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.i("生命周期","onPause");
		//	bkg_timer.cancel();
		
	}
	@Override
	protected void onStop() {
		super.onStop();
		Log.i("生命周期","onStop timer取消");
		if(bkg_timer!=null)
		{
			bkg_timer.cancel();
			bkg_timer=null;
			System.gc();
		}

		isStoped=true;
	}

	List<Song> getListFromArray(JSONArray ja)
	{
		List<Song> ss=new ArrayList<Song>();
		for(int i=0;i<ja.length();++i)
		{
			JSONObject jo;
			try {
				jo = ja.getJSONObject(i);
				ss.add(new Song(jo.getString("id"), jo.getString("name"), jo.getString("artist")));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ss;
	}
	
	
	void ppp_toast(String text)
	{
			Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
	}
	
	private boolean isDeleting=false;
	private boolean deleteCloudSong_islike=false;
	 protected void deleteCloudSong(final Song ds,boolean islike) {
		// TODO Auto-generated method stub
		 
			checkLoginStatus();
			if(isDeleting)return;isDeleting=true;
			deleteCloudSong_islike=islike;
			new Thread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub

					String urlString=MyApplication.domain+"/user/deletetaste.php?";
					urlString+=("&user="+MyApplication.UDM.id);
					urlString+=("&music="+ds.getMid());
					if(deleteCloudSong_islike)
					{
						urlString+=("&type=0");
					}
					else
					{
						urlString+=("&type=1");
					}
					
					Log.i("ListItem","提交"+urlString);
					String resultString=FileNetManager.postRequestToHttp(urlString, null);
					
					try {
						Log.i("ListItem","获得返回结果"+resultString);
				
						JSONObject jo=new JSONObject(resultString);
						
						
						
						
						if(jo.getInt("errcode")==0)
						{
							String str="";
							if(!deleteCloudSong_islike)str="不";
							//报喜...
							Message msg2=new Message();
							msg2.what=8;
							msg2.arg1=1;
							msg2.obj="歌曲"+ds.getName()+"已从"+str+"喜欢列表删除";
							handlerForBackground.sendMessage(msg2);
						}
						else
						{
							//报错
							Log.i("ListItem","报错"+jo.getString("errmsg"));
							Message msg3=new Message();
							msg3.what=8;
							msg3.arg1=1;
							msg3.obj=jo.getString("errmsg");
							handlerForBackground.sendMessage(msg3);
							return;
						}
						
						
						
						
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.i("ListItem","bugbug");
						
					}
				
					isDeleting=false;	
				}
			}).start();
		 
		 
	}
	

	//从网络端获取背景图片并发消息可以换图了
	private void getBackgroungImage() {
	   		new Thread(new Runnable(){
				@Override
				public void run() {
               	 //请求路径
               	 String requestString=MyApplication.domain+"/picture/play.php?user="+MyApplication.UDM.id+"&class="+currentSong.getMclass();
               	 //获取欢迎图片的路径以及每日一句话
               	 String pictureString="";
               	 String resultString=FileNetManager.postRequestToHttp(requestString,null);
               	 Log.i("背景切换",resultString);
               	 
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
					Bitmap bitmap=null;
					
					try{

						bitmap=FileNetManager.getImage(pictureString);//下载图片
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
				}
	   	}).start();
			
}
	//向handler发消息要求延后换图
	void TimeToChangeBackground()
	{	
		if(mmp==null||!mmp.isPlaying())return;
		if(BackgroundLock)return;
        Message message = new Message();
        message.what = 1; 
        handlerForBackground.sendMessageDelayed(message, MyApplication.UDM.autochangeBkgTime*1000);
	}
	//向handler发消息s秒后换图
	void TimeToChangeBackground(int s)
	{	
		if(mmp==null||!mmp.isPlaying())return;
		if(BackgroundLock)return;
        Message message = new Message();
        message.what = 1; 
        handlerForBackground.sendMessageDelayed(message, s*1000);
	}
	
    //背景图片的切换
    @SuppressLint("NewApi")
	void imageFadeChange()
    {
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
				background2.setVisibility(View.INVISIBLE);
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {}
			@Override
			public void onAnimationStart(Animation arg0) {}
         });
         alphaAnimation.setDuration(2000);//设置时间
         background2.startAnimation(alphaAnimation);
    }

    
    public static void setGradients(SeekBar m,int color)
    {
    	int[] Colors=new int[]{R.color.white,color};
    	GradientDrawable mg=new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, Colors);
    	mg.setGradientType(GradientDrawable.LINEAR_GRADIENT);
    	mg.setCornerRadius(15);
    	mg.setStroke(10, -1);
    	m.setProgressDrawable(mg);
    }
    //绑定控件
	void findViews()
	{
		
          playOrPause_btn =(ImageButton)findViewById(R.id.playOrPuse);
          title =(StrokeTextView)findViewById(R.id.title);
          title.isLrcLine=false;
          artist =(TextView)findViewById(R.id.artist);
          next_btn =(ImageButton)findViewById(R.id.next);
          isLoop_rbtn =(ImageButton)findViewById(R.id.isLoop);
          dislike_rbtn =(ImageButton)findViewById(R.id.dislike);
          like_rbtn =(ImageButton)findViewById(R.id.like);
          background1=(ImageView) findViewById(R.id.background1);
          background2=(ImageView) findViewById(R.id.background2);
          centent_filter_bg=(ImageView) findViewById(R.id.centent_filter_bg);
          seekBar=(SeekBar) findViewById(R.id.seekBar1);
          
        //  setGradients(seekBar,Color.parseColor("#FF981CF7"));
        
          
          music_info=(LinearLayout) findViewById(R.id.musicInfo);
          musicInfoText=(StrokeTextView) findViewById(R.id.musicInfoText);
          
          
          playOrPause_btn.setOnTouchListener(TouchDark);
          next_btn.setOnTouchListener(TouchDark);
          isLoop_rbtn.setOnTouchListener(TouchDark);
          dislike_rbtn.setOnTouchListener(TouchDark);
          like_rbtn.setOnTouchListener(TouchDark);
          
          
          
          
          LrcView=(ListView) findViewById(R.id.lyricshow);
          backgroundLocker=(ImageButton) findViewById(R.id.lock_background);
          personinfo=(LinearLayout) findViewById(R.id.PersonalInfo);
          systemadjust=(LinearLayout) findViewById(R.id.SystemAdjust);
          logout=(LinearLayout) findViewById(R.id.LogOut);
          LrcView.setEnabled(false);
          LrcSDM=new SlidingDrawerManager(this,LrcView);
          
          
          Icon= (BootstrapCircleThumbnail) findViewById(R.id.Icon);
          InfoPanel= (LinearLayout) findViewById(R.id.InfoPanel);
          LoginPanel= (LinearLayout) findViewById(R.id.LoginPanel);
          username= (TextView) findViewById(R.id.username);
          
          mood=new WheelView(this);
        
          mood.setVisibleItems(7);
          mood.setCyclic(true);
          mood.setAdapter(new ArrayWheelAdapter<String>(Mood_List.moodMap));
          mood.addChangingListener(new OnWheelChangedListener() {  
              public void onChanged(WheelView wheel, int oldValue,  
                      int newValue) {
            	  MyApplication.mood=newValue+1;
                //  ppp_toast("您在"+Mood_List.moodMap[MyApplication.mood-1]+"地"+Behavior_List.behaviorMap[MyApplication.behavior-1]);
              }  
          });
          
          
          
          
          behavior=new WheelView(this);
          behavior.setVisibleItems(7);  
          behavior.setCyclic(true);  
          behavior.setAdapter(new ArrayWheelAdapter<String>(Behavior_List.behaviorMap));
          behavior.addChangingListener(new OnWheelChangedListener() {  
              public void onChanged(WheelView wheel, int oldValue,  
                      int newValue) {
            	  MyApplication.behavior=newValue+1;
            	//  ppp_toast("您在"+Mood_List.moodMap[MyApplication.mood-1]+"地"+Behavior_List.behaviorMap[MyApplication.behavior-1]);
                  }  
          });
          
          ((LinearLayout)findViewById(R.id.moodselector)).addView(mood);
          ((LinearLayout)findViewById(R.id.behaviorselector)).addView(behavior);
          
          
          //右侧页面
          like_list=(BootstrapButton) findViewById(R.id.like_list_btn);
          dislike_list=(BootstrapButton) findViewById(R.id.dislike_list_btn);
          like_listview=(ListView) findViewById(R.id.like_panel);
          dislike_listview=(ListView) findViewById(R.id.dislike_panel);
          
        //  like_list.setBootstrapType("info");
        //  dislike_list.setBootstrapType("default");
          
   }
	
    //绑定事件
	void ListenerBind()
   {
          btnListener blst= new btnListener();
          playOrPause_btn .setOnClickListener(blst);
          next_btn .setOnClickListener(blst);
          isLoop_rbtn .setOnClickListener(blst);
          seekBar.setOnSeekBarChangeListener(new SeekBarListener());
          music_info .setOnClickListener(blst);
          like_rbtn.setOnClickListener(blst);
          dislike_rbtn.setOnClickListener(blst);
          backgroundLocker.setOnClickListener(blst);
          
          personinfo.setOnClickListener(blst);
          systemadjust.setOnClickListener(blst);          
          LoginPanel.setOnClickListener(blst);
          logout.setOnClickListener(blst);
          like_list.setOnClickListener(blst);
          dislike_list.setOnClickListener(blst);
          Icon.setOnClickListener(blst);
   
          
   }
	
	public void changeToLikePanel(final boolean b) {
		// TODO Auto-generated method stub
		if(b&&like_listview.getVisibility()==View.VISIBLE){Log.i("ANI","止1");return;}
		if(!b&&dislike_listview.getVisibility()==View.VISIBLE){Log.i("ANI","止2");return;}
		final ListView visiable;
	    final ListView invisiable;
	    if(like_listview.getVisibility()==View.VISIBLE)
	    {
	    	visiable=like_listview;
	    	invisiable=dislike_listview;
	    	cur_is_like=false;
	     	like_list.setBootstrapType("default");
	        dislike_list.setBootstrapType("info");
	    	
	    }
	    else
	    {			    	
	   
	        like_list.setBootstrapType("info");
	        dislike_list.setBootstrapType("default");
	    	visiable=dislike_listview;
	    	invisiable=like_listview;
	    	cur_is_like=true;
	    }
		final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(invisiable, "rotationY",-90f, 0f); 
	    ObjectAnimator visToInvis = ObjectAnimator.ofFloat(visiable, "rotationY", 0f, 90f); 
	    visToInvis.setDuration(500); 
	    invisToVis.setDuration(500); 
		Log.i("ANI","开始动画");
	    visToInvis.addListener(new AnimatorListenerAdapter() { 
	        @Override 
	        public void onAnimationEnd(Animator anim) { 
	             
	            /* 
	             * 列举几个动画的监听： 
	             * 一：anim.isRunning(){//TODO} 
	             * 二：anim.isStarted(){//TODO} 
	             * 三：anim.end(){//TODO} 
	             */ 
	             
	            visiable.setVisibility(View.GONE); 
	            invisToVis.start(); 
	            invisiable.setVisibility(View.VISIBLE); 
	            
	            //将列表中带选项的去掉
	            SongListItemAdapter sia=(SongListItemAdapter) invisiable.getAdapter();
	            if(sia!=null)sia.hideMgr();
	            sia=(SongListItemAdapter) visiable.getAdapter();
	            if(sia!=null)sia.hideMgr();         
	        } 
	    }); 
	    visToInvis.start(); 
	}

	//按钮事件监听类(播放/暂停/下一曲/是否单曲循环)
	class btnListener implements OnClickListener{
	         
			@Override
	          public void onClick(View arg0) {
	                // TODO Auto-generated method stub
	        	  	SeekBarChagnging=true;
	        	  	
	                switch (arg0.getId())
	               {
	                case R.id.playOrPuse :
	                	if(mmp!=null&&mmp.isPlaying())
	                	{
	                		ppp_toast("暂停");
	                		PauseMusic();
	                	}
	                	else
	                	{
	                		ppp_toast("播放");
	                		PlayMusic(true);
	                	}
	                	mMenu.ComeToCenter();
	                      break ;
	                case R.id.next :
	                	 ppp_toast("下一曲");
	                	 mMenu.ComeToCenter();
	                	 seekBar.setProgress(0);
	                	 PlayNextMusic();
	                     break ;
	                case R.id.isLoop :
	                	mMenu.ComeToCenter();
	                     if(isLoop)
	                     {
	                    	 ppp_toast("取消单曲循环");
	                    	 isLoop_rbtn.setBackgroundResource(R.drawable.noloop);
	                    	 isLoop=false;
	                     }
	                     else
	                     {
	                    	 ppp_toast("单曲循环");
	                    	 isLoop_rbtn.setBackgroundResource(R.drawable.loop);
	                    	 isLoop=true;
	                     }
	                     break ;
	                case R.id.musicInfo:  //显示歌曲的信息页
	   //           case R.id.musicLrc:
	                	//changeMusicInfo();
	                	break;
	                case R.id.like:
	                	mMenu.ComeToCenter();
	                	if(MyApplication.UDM.id==-1||currentSong==null){
	                		return;
	                	}
	                	//Toast.makeText(MainActivity.this,"喜欢这首歌",Toast.LENGTH_SHORT).show();
	                	//ppp_toast("添加到喜欢列表");
	                	addtaste(true);
	                	break;
	                case R.id.dislike:
	                	mMenu.ComeToCenter();
	                	if(MyApplication.UDM.id==-1||currentSong==null)return;
	                	//Toast.makeText(MainActivity.this,"不喜欢这首歌",Toast.LENGTH_SHORT).show();
	                	//ppp_toast("添加到不喜欢列表");
	                	addtaste(false);
	                	break;
	                case R.id.lock_background:
	                	mMenu.ComeToCenter();
	                	if(!BackgroundLock)
	                	{
	                		backgroundLocker.setBackgroundResource(R.drawable.lock);
	                		BackgroundLock=true;
	                	}else
	                	{
	                		backgroundLocker.setBackgroundResource(R.drawable.unlocked);
	                		BackgroundLock=false;
	                	}
	                	break;
	                case R.id.PersonalInfo:
	                case R.id.Icon:
	                	Intent pi=new Intent();
	                	pi.setClass(MainActivity.this, PersonalInfoActivity.class);
	                	startActivity(pi);
	                	break;
	                case R.id.SystemAdjust:
	                	Intent sa=new Intent();
	                	sa.setClass(MainActivity.this, SystemAdjustActivity.class);
	                	startActivity(sa);
	                	break;
	                case R.id.LogOut:
	                	 MyApplication.UDM.reset();
	                	 clearSongLists();
	                	 UserDateManager.setLocalUserData(MyApplication.UDM);
	                	 LoginCheck();
	                	break;
	                case R.id.LoginPanel:
	                	Intent la=new Intent();
	                	la.setClass(MainActivity.this, LoginActivity.class);
	                	startActivity(la);
	                	break;
	                case R.id.like_list_btn:
	                	changeToLikePanel(true);
	                	break;
	                case R.id.dislike_list_btn:
	                	changeToLikePanel(false);
	                	break;
	               }
	                SeekBarChagnging=false;
	         }
	         
	   }
	
	
		//进度条的监听器
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
	    
	    public void clearSongLists() {
			// TODO Auto-generated method stub
	    	JSONArray ja=new JSONArray();
	    	like_listview.setAdapter(new SongListItemAdapter(MainActivity.this,getListFromArray(ja), handlerForBackground,0));
	    	dislike_listview.setAdapter(new SongListItemAdapter(MainActivity.this,getListFromArray(ja), handlerForBackground,0));
		}


		void PlayNextMusic()
	    {
	    	if(isDownloading)return;
	        PauseMusic();
	        Stop();
	        PlayMusic(true);
	    }
	    
	    //暂停歌曲
	    void  PauseMusic()
	    {
			if(mmp!=null)mmp.pause();
			playOrPause_btn.setBackgroundResource(R.drawable.play);
			
	    }
	    void CloudPlay(boolean needDownSong)
	    {
	    	 isDownloading=true;
	    	//设置模式...
			 mmp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			 try {
				 
				 if(needDownSong)
				 {
						 Log.i("LrcDown","开始获取歌曲基本信息....");
						 //please
		            	 //请求路径
		            	 String requestString=MyApplication.domain+"/music/fetch.php?userid="+MyApplication.UDM.id+
		            			 "&weather="+MyApplication.weather+"&mood="+MyApplication.mood+"&behaviour="+MyApplication.behavior;
		            	 Log.i("LrcDown","请求字串"+requestString);
		            	 
		            	 String resultString=FileNetManager.postRequestToHttp(requestString,null);
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
								 currentSong=new Song(ja.getString("mid"),ja.getString("name"),ja.getString("album"),ja.getString("artist"),ja.getString("lyric"),ja.getString("music"),ja.getString("class"));
								 Message msg=new Message();
								  msg.obj=currentSong;
								  msg.what=4;
								  handlerForBackground.sendMessage(msg);
								  Log.i("LrcDown","发送通知不需要歌词");
								 break;
							 case 1: //信息不完整，获取失败
								 throw new Exception("信息不完整，获取失败");
							 case 2://信息非法，获取失败
								 throw new Exception("信息非法，获取失败");
							 }
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							
							Log.i("UserData","获取错误码出错"+e.getMessage());return;
						}
				 }
				 //String nextPath=currentSong.getMusic();
            	 String nextPath="";
            	 boolean lrcsafe=true;
  
//				  nextPath=MyApplication.QiNiuBaseMusicPath+currentSong.getName()+".mp3";
				  nextPath=currentSong.getMusicUrl();
				  //nextPath="http://7rflrh.com1.z0.glb.clouddn.com/fancyblade/"+URLEncoder.encode("唱给你的歌", "UTF-8").replace("+","%20")+".mp3";
				   //nextPath="http://7sbqfo.com1.z0.glb.clouddn.com/music/"+URLEncoder.encode("折子戏", "UTF-8").replace("+","%20")+".mp3";
				  
				  Log.i("SongName","歌曲名称"+currentSong.getName());
				  
				 LyricDownloadManager test=new LyricDownloadManager();
				 Log.i("LrcDown","开始下载歌词");
				 String lrcPath=test.downLyricContent(currentSong.getLyric(), currentSong.getArtist(), currentSong.getName());
				 Log.i("LrcDown","歌词已存到本地"+lrcPath);
				 if(lrcPath==null)lrcsafe=false;
				 currentSong.setLrcLocalPath(lrcPath);

				 //通知listView换词
				 sendMessagetoToast("已经传参给MediaPlayer"+currentSong.getName());
				 mmp.setDataSource(nextPath);
				 
				 try{
				 
				 mmp.prepare();
				 mmp.start();
				 }catch(Exception e)
				 {
					 //播放失败了
					 
					 sendMessagetoToast("歌曲链接失效，自动换下一首",1);
					 
				 }
				 sendMessagetoToast("MediaPlayer开始播放");
				 if(lrcsafe)
				 {
					  Message msg=new Message();
					  msg.obj=currentSong;
					  msg.what=3;
					  handlerForBackground.sendMessage(msg);
					  Log.i("LrcDown","发送通知换个词");
				 }
				 else
				 {
					 //标记此处，UI逻辑问题
//					 Message msg=new Message();
//					  msg.obj=currentSong;
//					  msg.what=4;
//					  handlerForBackground.sendMessage(msg);
//					  Log.i("LrcDown","发送通知不需要歌词");
				 }

			} catch (Exception e) {
				Log.i("test","错误"+e.getMessage());
				e.printStackTrace();
			}
			 isDownloading=false;
			
	    }
	    
	    //获取歌曲并开始播放
	    void  PlayMusic(final boolean cneedDownSong)
	    {
			playOrPause_btn.setBackgroundResource(R.drawable.pause);  //按钮换图
	    	if(mmp!=null)
			{
				mmp.start();
				return;
			}
	    	 Log.i("LrcDown","开始进行下一首歌曲的联网获取....");
			musicThread=new Thread(new Runnable(){
				@Override
				public void run() {
					if(!(app.isNetworkConnected())){
						//发消息提示说没网放不了歌曲....
						return;
					}
					if(isDownloading)return;
						
						if(mmp!=null){mmp.release();mmp.reset();}
						mmp=null;
						mmp=new MediaPlayer();
						CloudPlay(cneedDownSong);
						//if()可调用本地播放的函数，即播放本地文件....
						mmp.setOnCompletionListener(new OnCompletionListener(){
						@Override
						public void onCompletion(MediaPlayer arg0) {
							// TODO Auto-generated method stub
							 Log.i("LrcDown","播放完毕换下一个");
								if(!(app.isNetworkConnected()))
								{
									//如果网络不通，默认自动换成单曲循环
									isLoop=true;
									//更改图标样式不能在这里进行，只好发消息了，唉写地好不优雅
									Message msg=new Message();
									msg.what=5;
									msg.arg1=1;
									handlerForBackground.sendMessage(msg);
									return;
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
						
						
					seekBar.setMax(mmp.getDuration());
					mmp.start();
					isDownloading=false;
					
					
					//如果设定为切歌就换，
					if(MyApplication.UDM.changeBkgWhenNext==1)
					{
						TimeToChangeBackground(0);
						//sendChange=true;
					}
				}
			});
			musicThread.start();
	    }
		void  Stop()
		{
			LrcView.setVisibility(View.INVISIBLE);
			music_info.setVisibility(View.VISIBLE);
			LrcSDM.reset();
			if(mmp!=null)
			{
				mmp.stop();
				mmp.release();
			}
			mmp=null;
			if(musicThread!=null)musicThread.interrupt();
			musicThread=null;
			isDownloading=false;
		}
		
		//连按两次退出
		@Override
		public boolean onKeyUp(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			   if (keyCode == KeyEvent.KEYCODE_BACK) {
				   if(mMenu.current_view_index!=1)
				   {
					   mMenu.ComeToCenter();
					   return true;
				   }
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
		public void sendMessagetoToast(String str)
		{
			Message msg=new Message();
			msg.what=8;
			msg.obj=str;
			
			MainActivity.handlerForBackground.sendMessage(msg);
		}
		public void sendMessagetoToast(String str ,int arg1)
		{
			Message msg=new Message();
			msg.what=8;
			msg.obj=str;
			msg.arg1=arg1;
			MainActivity.handlerForBackground.sendMessage(msg);
		}
		
		
		
}

//----------其他类-------------
class Mood_List{
	public static String[] moodMap=new String[]{
		"  开心  ",
		"  轻松  ",
		"  悲伤  ",
		"  愤怒  ",
		"  疲惫  ",
		"  痛苦  ",
		"  一般  "
	};
}

class Behavior_List{
	public static String[] behaviorMap=new String[]{
		"   办公   ",
		"   锻炼   ",
		"   阅读   ",		
		"   餐饮   ",
		"   娱乐   ",
		"   休息   ",
		"   出行   "
	};
}

class Weather_List{
	/*
	1 晴,2 多云,3 阴天,4 小雨,5 中雨,6 大雨,7 雷雨,8 小雪,9 中雪,10 大雪,11 雾霾,12 沙尘,0 其他 
	 * */
	 public static HashMap<String,Integer> weatherMap;
	static{
		weatherMap= new HashMap<String,Integer>();
		weatherMap.put("晴",1);
		weatherMap.put("多云",2);
		weatherMap.put("阴",3);
		weatherMap.put("阵雨",4);
		weatherMap.put("雷阵雨",7);
		weatherMap.put("雷阵雨并伴有冰雹",7);
		weatherMap.put("雨夹雪",8);
		weatherMap.put("小雨",4);
		weatherMap.put("中雨",5);
		weatherMap.put("大雨",6);
		weatherMap.put("暴雨",6);
		weatherMap.put("大暴雨",6);
		weatherMap.put("特大暴雨",6);
		weatherMap.put("阵雪",8);
		weatherMap.put("小雪",8);
		weatherMap.put("中雪",9);
		weatherMap.put("大雪",10);
		weatherMap.put("暴雪",10);
		weatherMap.put("雾",11);
		weatherMap.put("冻雨",4);
		weatherMap.put("沙尘暴",12);
		weatherMap.put("小雨-中雨",4);
		weatherMap.put("中雨-大雨",9);
		weatherMap.put("大雨-暴雨",6);
		weatherMap.put("暴雨-大暴雨",6);
		weatherMap.put("大暴雨-特大暴雨",6);
		weatherMap.put("小雪-中雪",8);
		weatherMap.put("中雪-大雪",9);
		weatherMap.put("大雪-暴雪",10);
		weatherMap.put("浮尘",12);
		weatherMap.put("扬沙",12);
		weatherMap.put("强沙尘暴",12);
		weatherMap.put("飑",0);
		weatherMap.put("龙卷风",0);
		weatherMap.put("弱高吹雪",8);
		weatherMap.put("轻霾",11);
		weatherMap.put("霾",11);
	 }

	public static int GetIdFromName(String name)
	{
		Integer temp=weatherMap.get(name);
		if(temp==null)temp=0;
		return temp;
	}
	
	public static int getResImageById(int id)
	{
		switch(id)
		{
		case 1:
			return R.drawable.w1;
		case 2:
			return R.drawable.w2;
		case 3:
			return R.drawable.w3;
		case 4:
			return R.drawable.w4;
		case 5:
			return R.drawable.w5;
		case 6:
			return R.drawable.w6;
		case 7:
			return R.drawable.w7;
		case 8:
			return R.drawable.w8;
		case 9:
			return R.drawable.w9;
		case 10:
			return R.drawable.w10;
		case 11:
			return R.drawable.w11;
		case 12:
			return R.drawable.w12;
		default:
			return R.drawable.w_default;
		}
	}
	
}




