package com.amergin;

import android.media.MediaPlayer;
import android.widget.SeekBar;

public class MyPlayer {
	private MediaPlayer mmp=null;
	private SeekBar seekBar=null;
	public MyPlayer()
	{
		MidiaPlayerIniti();
	}
	public MyPlayer(SeekBar _seekBar)
	{
		this();
		seekBar=_seekBar;
	}
	
	void MidiaPlayerIniti()
	{
		mmp=new MediaPlayer();
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
    }  
    
    
    
    
}
