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
	private MyApplication app;  //ȫ�ֲ���
	//-----һ�ѶѵĲ���
	private long mExitTime;   //��������������ΰ����˳�ʱ���˳�
	public static Handler handlerForBackground; //����UI���µ�handelr
	private MediaPlayer mmp=null;  //�������ֵĹ���
	private Thread musicThread=null;  //�������ֵ��߳�
	private boolean isLoop=false;   //�Ƿ���
	private boolean SeekBarChagnging=false;   //Ϊ�����϶���������ʱ�������ζ�ΪMediaPlayer�Ľ���
	private boolean isDownloading=false;     //Ϊ����������һ����ʱ�򲻽�������һ��������
	private boolean BackgroundLock=false;     //Ϊ��ʱֹͣ������ͼƬ
	private Bitmap lastBitmap=null;   //ʵ��ͼƬ�ֻ���Ҫ�ı���
	private Bitmap currentBitmap=null;//ʵ��ͼƬ�ֻ���Ҫ�ı���
	//---------------------------------
	private SlidingMenu mMenu;  //����ҳ��
	private  Song currentSong=null; //��ǰ���ŵĸ���
	
	//--�м�ҳ��----
	private ImageView background1;	//���ű���ʵ�ֵ��뵭��
	private ImageView background2;    //���ű���ʵ�ֵ��뵭��
	public static ImageView centent_filter_bg;    //���ű���ʵ�ֵ��뵭��
	
	private ImageButton playOrPause_btn;
	private ImageButton next_btn;
	private ImageButton isLoop_rbtn;
	private ImageButton like_rbtn;
	private ImageButton dislike_rbtn;
	private SeekBar seekBar;  //������
	private LinearLayout music_info;  //������Ϣ
	private StrokeTextView musicInfoText;
	private SlidingDrawerManager LrcSDM;//���������
	private ListView LrcView;  //������
	private StrokeTextView title;  //��������
	private TextView artist;  //����
	
	private TextView username;
	
	private LinearLayout InfoPanel;
	private  BootstrapCircleThumbnail Icon;
	private WheelView mood;   //���ѡȡ�ؼ�
	private WheelView behavior;  //��Ϊѡȡ�ؼ�
	private ImageButton backgroundLocker;  //��������ͼƬ
	private LinearLayout personinfo;  //������Ϣ
	private LinearLayout systemadjust;  //ϵͳ����
	private LinearLayout logout;  //�ǳ�
	
	private LinearLayout LoginPanel;
	
	//----�Ҳ����
	private BootstrapButton like_list;
	private BootstrapButton dislike_list;
	private ListView dislike_listview;
	private ListView like_listview;
	private boolean cur_is_like=true;  //�����б����ʾ
	
	
	
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
		Log.i("��������","onResume");
		
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
    			Log.i("�����л�","����TImer");
    			
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
		Log.i("�����л�",usrdata.autochangeBkgTime+"�л�");
		//����ͷ��
		if(usrdata.Icon!=null&&!usrdata.Icon.equals(""))
		{
			Bitmap	iconBitmap= BitmapFactory.decodeFile(usrdata.Icon);
			if(iconBitmap==null) //��ȡ����ͷ��ʧ��
			{
				usrdata.Icon=null;
				UserDateManager.setLocalUserData(usrdata);
			}
			else
			{
				this.Icon.setImage(iconBitmap);
				iconBitmap.recycle();  //���գ���
			}
		}
		else
		{
			this.Icon.setImage(R.drawable.headicon);
		}
		
		//�����û���
		this.username.setText(usrdata.username);
	}


	void LoginCheck()
	{
		 if(MyApplication.UDM.id==-1)//δ��¼״̬
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //��Ҫ������
		setContentView(R.layout.main);
		app = (MyApplication) getApplication();
		
		//����ҳ��
		mMenu = (SlidingMenu) findViewById(R.id.id_menu);
		mMenu.setmMenuRightPadding(160);
		mMenu.hdl=MainActivity.handlerForBackground;

		
		
		 findViews();  //�ؼ���
		 ListenerBind(); //�¼���
		 BackgroundIniti(); //������ʼ��
		 SeekBarIniti(); //�������ĳ�ʼ��
		 
		 //updateSongLists();
		 
		 if(MyApplication.UDM.PlayOnStart==1)
			 PlayNextMusic();
		 
	}

	private void checkLoginStatus()
	{
		if(MyApplication.UDM.id==-1)
		{
			//��ʾ��½����
			Message msg=new Message();
			msg.what=8;
			msg.arg1=1;
			msg.obj="����û��½,�޷��鿴ϲ���б�";
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
		Log.i("UUU","��ȡ");
		
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
		Log.i("UUU","��ȡ����");
	}

	void getListSong(boolean b)
	{
		Log.i("UUU","��ʼ");
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
		
		Log.i("UUU","�����ַ�����Get��ʽ"+urlString);
		try {
			Log.i("UUU","��÷��ؽ��"+resultString);
			JSONObject jo=new JSONObject(resultString);
			Message msg=new Message();
			msg.what=8;
			if(jo.getInt("errcode")==0)
			{
				msg.obj=("��ȡ�ɹ�"+resultString);
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
				//����
				Log.i("ListItem","����"+jo.getString("errmsg"));
				msg.obj=("��ȡʧ�ܣ�"+jo.getString("errmsg"));
			}
			handlerForBackground.sendMessage(msg);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		finally{
			isUpdating--;
		}
		Log.i("Song","����");
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
				
				Log.i("UUU","�ύ"+urlString);
				String resultString=FileNetManager.postRequestToHttp(urlString, null);
				String pre_str="";//ǰ׺
				try {
					Log.i("UUU","��÷��ؽ��"+resultString);
			
					JSONObject jo=new JSONObject(resultString);
					int errorcode=jo.getInt("errcode");
					if(errorcode==0)
					{
						Log.i("UUU","������");
						
						if(!addtaste_islike)pre_str="��";
						//��ϲ...
						Message msg2=new Message();
						msg2.what=8;
						msg2.arg1=1;
						msg2.obj="����"+currentSong.getName()+"����ӵ� "+pre_str+"ϲ���б�";
						handlerForBackground.sendMessage(msg2);
					}
					else
					{
						Log.i("UUU","������");
						//����
						Log.i("ListItem","����+"+jo.getString("errmsg"));
						Message msg3=new Message();
						msg3.what=8;
						msg3.arg1=1;
						if(!addtaste_islike)pre_str="��";
						if(errorcode==3)//�ظ�....�����д��= =
						{
							msg3.obj="����"+currentSong.getName()+"����"+pre_str+"ϲ���б���";
							
						}else if(errorcode==4)//����һ���б���
						{
							msg3.obj="����"+currentSong.getName()+"����"+pre_str+"ϲ���б���";
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
	
	//��ʼ��������
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
				case 0: //��������ͼƬ����Ϣ
					    currentBitmap=(Bitmap)msg.obj;
						imageFadeChange();
					break;
				case 1:  //������˻�ȡ����ͼƬ
					getBackgroungImage();
					break;
				case 2:
					int p=msg.arg1;
					if(p>0)p+=200;  //��ǰ200������ʾ���ʱ�����
					LrcSDM.refreshUI(p);
					break;
				case 3:  			//�����ʾ���������¸�ʣ�������
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
						Log.i("TTT","����"+e.getMessage());
					}
					 music_info.setVisibility(View.INVISIBLE);
					 LrcView.setVisibility(View.VISIBLE);
					break;
				case 4://���ø����ʾģ�飬ò��û����,�走����������
					//�����ң�������
					//Song s1=(Song)msg.obj;
					Log.i("reset","1");
					title.setText(currentSong.getName());
					artist.setText(currentSong.getArtist());
					String infoText="";
					infoText+="������:"+currentSong.getName()+"\n";
					infoText+="������:"+currentSong.getArtist()+"\n";
					infoText+="ר��:"+currentSong.getAlbum();
					
					
					Log.i("reset","2");
					LrcSDM.reset();
					LrcView.setVisibility(View.INVISIBLE);
					music_info.setVisibility(View.VISIBLE);
					musicInfoText.setText(infoText);
					
					break;	
				case 5:
					//�õ���ѭ���İ�ť��Ϊ����״̬
					isLoop_rbtn.setBackgroundResource(R.drawable.loop);
					break;
					
				case 6://���Ÿ���,�����б�
					Song ps=(Song) msg.obj;
					if(ps!=null)
					{
						currentSong=ps;
						MainActivity.this.title.setText(ps.getName());
						String infoT="";
						infoT+="������:"+currentSong.getName()+"\n";
						infoT+="������:"+currentSong.getArtist()+"\n";
						infoT+="ר��:"+currentSong.getAlbum();
						LrcView.setVisibility(View.INVISIBLE);
						music_info.setVisibility(View.VISIBLE);
						musicInfoText.setText(infoT);
						
						
						Stop();
						PlayMusic(false);
						mMenu.ComeToCenter();
					}
					break;
				case 7://ɾ������
					try{
						Song ds=(Song) msg.obj;
						deleteCloudSong(ds,msg.arg1==0);
					}
					catch(Exception e)
					{
						ppp_toast("ɾ��bug"+e.getMessage());
					}

					break;
				case 8://������toast֪ͨ
					if(msg.arg1==1)
					{
						ppp_toast((String)msg.obj);
					}
					
					
					if(MyApplication.DEBUG){
						ppp_toast((String)msg.obj);
					}
					break;
				case 9: //����ϲ���б�
					
					if(MainActivity.this.mMenu.current_view_index==2)
					{
						if(msg.arg1==0)//ϲ����
						{
							//like_listview.setVisibility(View.GONE);
							like_listview.setAdapter(new SongListItemAdapter(MainActivity.this,getListFromArray((JSONArray) msg.obj), handlerForBackground,0));
							//like_listview.setVisibility(View.VISIBLE);
							//mMenu.closeMenu();
						}else//��ϲ����
						{
							//	dislike_listview.setVisibility(View.GONE);
							dislike_listview.setAdapter(new SongListItemAdapter(MainActivity.this,getListFromArray((JSONArray) msg.obj), handlerForBackground,1));
						}
					}

					
					break;
				case 10: //�����Ǵ�mMenu��߷�����Ҫ�����ϲ���б�
					updateSongLists();
					//like_listview.setVisibility(View.VISIBLE);
					if(cur_is_like)
						Log.i("BOOT","����ϲ��");
					else
						Log.i("BOOT","������ϲ��");
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
		Log.i("��������","onPause");
		//	bkg_timer.cancel();
		
	}
	@Override
	protected void onStop() {
		super.onStop();
		Log.i("��������","onStop timerȡ��");
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
					
					Log.i("ListItem","�ύ"+urlString);
					String resultString=FileNetManager.postRequestToHttp(urlString, null);
					
					try {
						Log.i("ListItem","��÷��ؽ��"+resultString);
				
						JSONObject jo=new JSONObject(resultString);
						
						
						
						
						if(jo.getInt("errcode")==0)
						{
							String str="";
							if(!deleteCloudSong_islike)str="��";
							//��ϲ...
							Message msg2=new Message();
							msg2.what=8;
							msg2.arg1=1;
							msg2.obj="����"+ds.getName()+"�Ѵ�"+str+"ϲ���б�ɾ��";
							handlerForBackground.sendMessage(msg2);
						}
						else
						{
							//����
							Log.i("ListItem","����"+jo.getString("errmsg"));
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
	

	//������˻�ȡ����ͼƬ������Ϣ���Ի�ͼ��
	private void getBackgroungImage() {
	   		new Thread(new Runnable(){
				@Override
				public void run() {
               	 //����·��
               	 String requestString=MyApplication.domain+"/picture/play.php?user="+MyApplication.UDM.id+"&class="+currentSong.getMclass();
               	 //��ȡ��ӭͼƬ��·���Լ�ÿ��һ�仰
               	 String pictureString="";
               	 String resultString=FileNetManager.postRequestToHttp(requestString,null);
               	 Log.i("�����л�",resultString);
               	 
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
					Bitmap bitmap=null;
					
					try{

						bitmap=FileNetManager.getImage(pictureString);//����ͼƬ
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
				}
	   	}).start();
			
}
	//��handler����ϢҪ���Ӻ�ͼ
	void TimeToChangeBackground()
	{	
		if(mmp==null||!mmp.isPlaying())return;
		if(BackgroundLock)return;
        Message message = new Message();
        message.what = 1; 
        handlerForBackground.sendMessageDelayed(message, MyApplication.UDM.autochangeBkgTime*1000);
	}
	//��handler����Ϣs���ͼ
	void TimeToChangeBackground(int s)
	{	
		if(mmp==null||!mmp.isPlaying())return;
		if(BackgroundLock)return;
        Message message = new Message();
        message.what = 1; 
        handlerForBackground.sendMessageDelayed(message, s*1000);
	}
	
    //����ͼƬ���л�
    @SuppressLint("NewApi")
	void imageFadeChange()
    {
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
				background2.setVisibility(View.INVISIBLE);
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {}
			@Override
			public void onAnimationStart(Animation arg0) {}
         });
         alphaAnimation.setDuration(2000);//����ʱ��
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
    //�󶨿ؼ�
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
                //  ppp_toast("����"+Mood_List.moodMap[MyApplication.mood-1]+"��"+Behavior_List.behaviorMap[MyApplication.behavior-1]);
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
            	//  ppp_toast("����"+Mood_List.moodMap[MyApplication.mood-1]+"��"+Behavior_List.behaviorMap[MyApplication.behavior-1]);
                  }  
          });
          
          ((LinearLayout)findViewById(R.id.moodselector)).addView(mood);
          ((LinearLayout)findViewById(R.id.behaviorselector)).addView(behavior);
          
          
          //�Ҳ�ҳ��
          like_list=(BootstrapButton) findViewById(R.id.like_list_btn);
          dislike_list=(BootstrapButton) findViewById(R.id.dislike_list_btn);
          like_listview=(ListView) findViewById(R.id.like_panel);
          dislike_listview=(ListView) findViewById(R.id.dislike_panel);
          
        //  like_list.setBootstrapType("info");
        //  dislike_list.setBootstrapType("default");
          
   }
	
    //���¼�
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
		if(b&&like_listview.getVisibility()==View.VISIBLE){Log.i("ANI","ֹ1");return;}
		if(!b&&dislike_listview.getVisibility()==View.VISIBLE){Log.i("ANI","ֹ2");return;}
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
		Log.i("ANI","��ʼ����");
	    visToInvis.addListener(new AnimatorListenerAdapter() { 
	        @Override 
	        public void onAnimationEnd(Animator anim) { 
	             
	            /* 
	             * �оټ��������ļ����� 
	             * һ��anim.isRunning(){//TODO} 
	             * ����anim.isStarted(){//TODO} 
	             * ����anim.end(){//TODO} 
	             */ 
	             
	            visiable.setVisibility(View.GONE); 
	            invisToVis.start(); 
	            invisiable.setVisibility(View.VISIBLE); 
	            
	            //���б��д�ѡ���ȥ��
	            SongListItemAdapter sia=(SongListItemAdapter) invisiable.getAdapter();
	            if(sia!=null)sia.hideMgr();
	            sia=(SongListItemAdapter) visiable.getAdapter();
	            if(sia!=null)sia.hideMgr();         
	        } 
	    }); 
	    visToInvis.start(); 
	}

	//��ť�¼�������(����/��ͣ/��һ��/�Ƿ���ѭ��)
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
	                		ppp_toast("��ͣ");
	                		PauseMusic();
	                	}
	                	else
	                	{
	                		ppp_toast("����");
	                		PlayMusic(true);
	                	}
	                	mMenu.ComeToCenter();
	                      break ;
	                case R.id.next :
	                	 ppp_toast("��һ��");
	                	 mMenu.ComeToCenter();
	                	 seekBar.setProgress(0);
	                	 PlayNextMusic();
	                     break ;
	                case R.id.isLoop :
	                	mMenu.ComeToCenter();
	                     if(isLoop)
	                     {
	                    	 ppp_toast("ȡ������ѭ��");
	                    	 isLoop_rbtn.setBackgroundResource(R.drawable.noloop);
	                    	 isLoop=false;
	                     }
	                     else
	                     {
	                    	 ppp_toast("����ѭ��");
	                    	 isLoop_rbtn.setBackgroundResource(R.drawable.loop);
	                    	 isLoop=true;
	                     }
	                     break ;
	                case R.id.musicInfo:  //��ʾ��������Ϣҳ
	   //           case R.id.musicLrc:
	                	//changeMusicInfo();
	                	break;
	                case R.id.like:
	                	mMenu.ComeToCenter();
	                	if(MyApplication.UDM.id==-1||currentSong==null){
	                		return;
	                	}
	                	//Toast.makeText(MainActivity.this,"ϲ�����׸�",Toast.LENGTH_SHORT).show();
	                	//ppp_toast("��ӵ�ϲ���б�");
	                	addtaste(true);
	                	break;
	                case R.id.dislike:
	                	mMenu.ComeToCenter();
	                	if(MyApplication.UDM.id==-1||currentSong==null)return;
	                	//Toast.makeText(MainActivity.this,"��ϲ�����׸�",Toast.LENGTH_SHORT).show();
	                	//ppp_toast("��ӵ���ϲ���б�");
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
	
	
		//�������ļ�����
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
	    
	    //��ͣ����
	    void  PauseMusic()
	    {
			if(mmp!=null)mmp.pause();
			playOrPause_btn.setBackgroundResource(R.drawable.play);
			
	    }
	    void CloudPlay(boolean needDownSong)
	    {
	    	 isDownloading=true;
	    	//����ģʽ...
			 mmp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			 try {
				 
				 if(needDownSong)
				 {
						 Log.i("LrcDown","��ʼ��ȡ����������Ϣ....");
						 //please
		            	 //����·��
		            	 String requestString=MyApplication.domain+"/music/fetch.php?userid="+MyApplication.UDM.id+
		            			 "&weather="+MyApplication.weather+"&mood="+MyApplication.mood+"&behaviour="+MyApplication.behavior;
		            	 Log.i("LrcDown","�����ִ�"+requestString);
		            	 
		            	 String resultString=FileNetManager.postRequestToHttp(requestString,null);
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
								 currentSong=new Song(ja.getString("mid"),ja.getString("name"),ja.getString("album"),ja.getString("artist"),ja.getString("lyric"),ja.getString("music"),ja.getString("class"));
								 Message msg=new Message();
								  msg.obj=currentSong;
								  msg.what=4;
								  handlerForBackground.sendMessage(msg);
								  Log.i("LrcDown","����֪ͨ����Ҫ���");
								 break;
							 case 1: //��Ϣ����������ȡʧ��
								 throw new Exception("��Ϣ����������ȡʧ��");
							 case 2://��Ϣ�Ƿ�����ȡʧ��
								 throw new Exception("��Ϣ�Ƿ�����ȡʧ��");
							 }
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							
							Log.i("UserData","��ȡ���������"+e.getMessage());return;
						}
				 }
				 //String nextPath=currentSong.getMusic();
            	 String nextPath="";
            	 boolean lrcsafe=true;
  
//				  nextPath=MyApplication.QiNiuBaseMusicPath+currentSong.getName()+".mp3";
				  nextPath=currentSong.getMusicUrl();
				  //nextPath="http://7rflrh.com1.z0.glb.clouddn.com/fancyblade/"+URLEncoder.encode("������ĸ�", "UTF-8").replace("+","%20")+".mp3";
				   //nextPath="http://7sbqfo.com1.z0.glb.clouddn.com/music/"+URLEncoder.encode("����Ϸ", "UTF-8").replace("+","%20")+".mp3";
				  
				  Log.i("SongName","��������"+currentSong.getName());
				  
				 LyricDownloadManager test=new LyricDownloadManager();
				 Log.i("LrcDown","��ʼ���ظ��");
				 String lrcPath=test.downLyricContent(currentSong.getLyric(), currentSong.getArtist(), currentSong.getName());
				 Log.i("LrcDown","����Ѵ浽����"+lrcPath);
				 if(lrcPath==null)lrcsafe=false;
				 currentSong.setLrcLocalPath(lrcPath);

				 //֪ͨlistView����
				 sendMessagetoToast("�Ѿ����θ�MediaPlayer"+currentSong.getName());
				 mmp.setDataSource(nextPath);
				 
				 try{
				 
				 mmp.prepare();
				 mmp.start();
				 }catch(Exception e)
				 {
					 //����ʧ����
					 
					 sendMessagetoToast("��������ʧЧ���Զ�����һ��",1);
					 
				 }
				 sendMessagetoToast("MediaPlayer��ʼ����");
				 if(lrcsafe)
				 {
					  Message msg=new Message();
					  msg.obj=currentSong;
					  msg.what=3;
					  handlerForBackground.sendMessage(msg);
					  Log.i("LrcDown","����֪ͨ������");
				 }
				 else
				 {
					 //��Ǵ˴���UI�߼�����
//					 Message msg=new Message();
//					  msg.obj=currentSong;
//					  msg.what=4;
//					  handlerForBackground.sendMessage(msg);
//					  Log.i("LrcDown","����֪ͨ����Ҫ���");
				 }

			} catch (Exception e) {
				Log.i("test","����"+e.getMessage());
				e.printStackTrace();
			}
			 isDownloading=false;
			
	    }
	    
	    //��ȡ��������ʼ����
	    void  PlayMusic(final boolean cneedDownSong)
	    {
			playOrPause_btn.setBackgroundResource(R.drawable.pause);  //��ť��ͼ
	    	if(mmp!=null)
			{
				mmp.start();
				return;
			}
	    	 Log.i("LrcDown","��ʼ������һ�׸�����������ȡ....");
			musicThread=new Thread(new Runnable(){
				@Override
				public void run() {
					if(!(app.isNetworkConnected())){
						//����Ϣ��ʾ˵û���Ų��˸���....
						return;
					}
					if(isDownloading)return;
						
						if(mmp!=null){mmp.release();mmp.reset();}
						mmp=null;
						mmp=new MediaPlayer();
						CloudPlay(cneedDownSong);
						//if()�ɵ��ñ��ز��ŵĺ����������ű����ļ�....
						mmp.setOnCompletionListener(new OnCompletionListener(){
						@Override
						public void onCompletion(MediaPlayer arg0) {
							// TODO Auto-generated method stub
							 Log.i("LrcDown","������ϻ���һ��");
								if(!(app.isNetworkConnected()))
								{
									//������粻ͨ��Ĭ���Զ����ɵ���ѭ��
									isLoop=true;
									//����ͼ����ʽ������������У�ֻ�÷���Ϣ�ˣ���д�غò�����
									Message msg=new Message();
									msg.what=5;
									msg.arg1=1;
									handlerForBackground.sendMessage(msg);
									return;
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
						
						
					seekBar.setMax(mmp.getDuration());
					mmp.start();
					isDownloading=false;
					
					
					//����趨Ϊ�и�ͻ���
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
		
		//���������˳�
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
	                       Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
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

//----------������-------------
class Mood_List{
	public static String[] moodMap=new String[]{
		"  ����  ",
		"  ����  ",
		"  ����  ",
		"  ��ŭ  ",
		"  ƣ��  ",
		"  ʹ��  ",
		"  һ��  "
	};
}

class Behavior_List{
	public static String[] behaviorMap=new String[]{
		"   �칫   ",
		"   ����   ",
		"   �Ķ�   ",		
		"   ����   ",
		"   ����   ",
		"   ��Ϣ   ",
		"   ����   "
	};
}

class Weather_List{
	/*
	1 ��,2 ����,3 ����,4 С��,5 ����,6 ����,7 ����,8 Сѩ,9 ��ѩ,10 ��ѩ,11 ����,12 ɳ��,0 ���� 
	 * */
	 public static HashMap<String,Integer> weatherMap;
	static{
		weatherMap= new HashMap<String,Integer>();
		weatherMap.put("��",1);
		weatherMap.put("����",2);
		weatherMap.put("��",3);
		weatherMap.put("����",4);
		weatherMap.put("������",7);
		weatherMap.put("�����겢���б���",7);
		weatherMap.put("���ѩ",8);
		weatherMap.put("С��",4);
		weatherMap.put("����",5);
		weatherMap.put("����",6);
		weatherMap.put("����",6);
		weatherMap.put("����",6);
		weatherMap.put("�ش���",6);
		weatherMap.put("��ѩ",8);
		weatherMap.put("Сѩ",8);
		weatherMap.put("��ѩ",9);
		weatherMap.put("��ѩ",10);
		weatherMap.put("��ѩ",10);
		weatherMap.put("��",11);
		weatherMap.put("����",4);
		weatherMap.put("ɳ����",12);
		weatherMap.put("С��-����",4);
		weatherMap.put("����-����",9);
		weatherMap.put("����-����",6);
		weatherMap.put("����-����",6);
		weatherMap.put("����-�ش���",6);
		weatherMap.put("Сѩ-��ѩ",8);
		weatherMap.put("��ѩ-��ѩ",9);
		weatherMap.put("��ѩ-��ѩ",10);
		weatherMap.put("����",12);
		weatherMap.put("��ɳ",12);
		weatherMap.put("ǿɳ����",12);
		weatherMap.put("�",0);
		weatherMap.put("�����",0);
		weatherMap.put("���ߴ�ѩ",8);
		weatherMap.put("����",11);
		weatherMap.put("��",11);
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




