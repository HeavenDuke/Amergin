package com.amergin;

import java.io.File;
import java.io.FileInputStream;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;
import android.widget.SeekBar;

public class MyPlayer {
	public final static int  PLAY_INTERNET=0;
	public final static int  PLAY_LOCAL=1;
	private int cur_mode=0;
	private String cur_SourceString=null;
	private static Context cur_context=null;
	private static int cur_ResId=0;
	
	private MediaPlayer mmp=null;
	private SeekBar seekBar=null;
	private Thread musicThread=null;
	public MyPlayer(SeekBar _seekBar)
	{
		this();
		seekBar=_seekBar;
	}
	public MyPlayer()
	{
		//setPlayMode(PLAY_LOCAL);
	}

		
	    void SetMusicProcess(int p)
	    {
	    	if(mmp!=null)
	    		mmp.seekTo(p);
	    }
	    void PlayNextMusic(String localPath)
	    {
	    	stop();
	    	
	    }
	    
	    public void stop()  
	    {  
	        if (mmp != null) {   
	        	mmp.stop();
	        	mmp.release();   
	        	mmp = null;
	        }   
	    }
	    
	    public void pause()  
	    {  
	    	if(mmp!=null)
	    		mmp.pause();  
	    	//mmp.setDataSource()
	    }  
	    
	    
	    public void setSourceAndMode(String URLString,int mode)
	    {
	    	
	    	cur_mode=mode;
	    }
	    //开始新的播放音乐，但是必须设定模式
		public void StartNewMusic(String URLString,int mode)
		{
			musicThread=new Thread(new Runnable(){
				@Override
				public void run() {
					
					if(mmp!=null)
					{
						mmp.release();
						mmp=null;
					}
					mmp=new MediaPlayer();
					if(cur_mode==PLAY_INTERNET)
					{
						mmp.setAudioStreamType(AudioManager.STREAM_MUSIC);
						 try {
							 mmp.setDataSource(cur_SourceString);
							 mmp.prepare();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.i("test","云错误"+e.getMessage());
							e.printStackTrace();
							return;
						}	
					}else if(cur_mode==PLAY_LOCAL)
					{
						try{
							File file = new File(cur_SourceString);
							Log.i("TTT","加载"+cur_SourceString);
							FileInputStream fis = new FileInputStream(file);
							mmp.setDataSource(fis.getFD());
							mmp.prepare();
							fis.close();
						}
						catch(Exception e)
						{
							Log.i("test","本地错误"+e.getMessage());
							e.printStackTrace();
							return;
						}
					}
					 mmp.setOnPreparedListener(new OnPreparedListener(){
						@Override
						public void onPrepared(MediaPlayer arg0) {
							// TODO Auto-generated method stub
							//Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
							arg0.start();
						}
					 });

					// TODO Auto-generated method stub
					mmp.setOnCompletionListener(new OnCompletionListener(){
						@Override
						public void onCompletion(MediaPlayer arg0) {
							// TODO Auto-generated method stub
							
							}
						});
				}
				
			});
			
			
			
		}
		

		
		public static void PlayFromRescouce(Context context,int resId)
		{
			cur_context=context;
			cur_ResId=resId;
			new Thread(new Runnable(){
				@Override
				public void run() {
					try{
						MediaPlayer mp=MediaPlayer.create(cur_context, cur_ResId);
						Log.i("KLL",""+cur_context+","+cur_ResId);
						mp.setOnPreparedListener(new OnPreparedListener(){
							@Override
							public void onPrepared(MediaPlayer arg0) {
								// TODO Auto-generated method stub
								arg0.start();
							}});
						mp.prepare();
					}
					catch(Exception e)
					{
						Log.i("KLL",e.getMessage());
					}
		}}).start();
		}
			
}
