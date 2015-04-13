package com.amergin;

import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SongListItemAdapter extends BaseAdapter {
	private  List<Song> songs;
	private int islike; //0代表喜欢，1代表不喜欢，好无奈啊
	private LayoutInflater m_Inflater;
	private int pre_pos=-1;
	private Handler Mainhd;
	private View  actived_item=null;
	public SongListItemAdapter(Context context, List<Song> SongList,Handler hd,int _islike) {
		m_Inflater = (LayoutInflater)context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		songs= SongList;
		Mainhd=hd;
		islike=_islike;
	}
	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return songs.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int pos, View convertView,final ViewGroup parent) {
		// TODO Auto-generated method stub
		
            convertView = m_Inflater.inflate(R.layout.song_list_item, null);  
            TextView item_index=(TextView)convertView.findViewById(R.id.item_Index);
            TextView item_title=(TextView)convertView.findViewById(R.id.item_title);
            TextView item_artist=(TextView)convertView.findViewById(R.id.item_artist);
            final LinearLayout item_managePanel=(LinearLayout)convertView.findViewById(R.id.item_managePanel);
            Button item_play=(Button) convertView.findViewById(R.id.item_play);
            Button item_delete=(Button) convertView.findViewById(R.id.item_delete);
            
            item_index.setText(""+(pos+1));
            item_title.setText(songs.get(pos).getName());
            item_artist.setText(songs.get(pos).getArtist());
            
            
            
            //按钮事件  ,播放
            item_play.setOnClickListener(new View.OnClickListener() {  
                @Override  
                public void onClick(View v) {  
                	Message msg=new Message();
                	msg.what=6;
                	msg.obj=songs.get(pos);
                	Mainhd.sendMessage(msg);
                 }  
            });   
            item_delete.setOnClickListener(new View.OnClickListener() {  
                @Override  
                public void onClick(View v) {  
                	
                	
                   	Message msg=new Message();
                   	msg.what=7;
                   	msg.arg1=islike;
                	msg.obj=songs.get(pos);
                	Mainhd.sendMessage(msg);
                	songs.remove(pos);
                	SongListItemAdapter.this.notifyDataSetChanged();  
                	actived_item=null;

                 }  
            }); 
            convertView.setOnClickListener(new View.OnClickListener() {  
                @Override  
                public void onClick(View v) {
                	//Log.i("UUU","pos:"+pos+";pre_pos:"+pre_pos);
                	
                	
                	if(pre_pos!=-1&&pre_pos!=pos)
                	{
                		if(pre_pos<parent.getChildCount())
                		{
                			View vv=parent.getChildAt(pre_pos);
                			setGone(vv);
                		}
                	}
                	
                	
                	//他喵的加上这个log就不出bug是要闹哪样
                	Log.i("UUU","pos:"+pos+";pre_pos:"+pre_pos);
                	pre_pos=pos;
                	
                	
                	if(item_managePanel.getVisibility()==View.GONE)
                	{
                		//((View)(item_managePanel.getParent())).setBackgroundColor(Color.parseColor("#cc6493C9"));
                		((View)(item_managePanel.getParent())).setBackgroundColor(Color.parseColor("#bbffffff"));
                		
                		//item_managePanel.setVisibility(View.VISIBLE);
                		AnimationShow(item_managePanel);
                		actived_item=item_managePanel;
                	}
                	else
                	{
                		((View)(item_managePanel.getParent())).setBackgroundColor(Color.parseColor("#88ffffff"));
                		//item_managePanel.setVisibility(View.GONE);
                		AnimationGone(item_managePanel);
                	}
                	
                 }  
            });
            
            

        return convertView;  
	}
	
	void setGone(View contentView)
	{
		LinearLayout ly=(LinearLayout)contentView.findViewById(R.id.item_managePanel);
		
		//ly.setVisibility(View.GONE); 
		AnimationGone(ly);
		
		((View)(ly.getParent())).setBackgroundColor(Color.parseColor("#88ffffff"));
	}
	
	
	
	//private int pre_height=0;
	@SuppressLint("NewApi")
	void AnimationGone(final View view)
	{
		   // pre_height=view.getHeight();
			ObjectAnimator visToInvis = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.0f); 
			visToInvis.setDuration(500); 
			    
			    visToInvis.addListener(new AnimatorListenerAdapter() { 
			        @Override 
			        public void onAnimationEnd(Animator anim) { 
			            view.setVisibility(View.GONE);
			            
			        } 
			    }); 
			    visToInvis.start(); 
	}
	
	@SuppressLint("NewApi")
	void AnimationShow(final View view)
	{
		    view.setVisibility(View.VISIBLE);
			ObjectAnimator visToInvis = ObjectAnimator.ofFloat(view, "scaleY",0.0f,1.0f); 
			visToInvis.setDuration(500); 
			    visToInvis.start(); 
	}
	
		
	
	public void clearItems()
	{
		songs.clear();
	}
	
	public void hideMgr()
	{
		if(actived_item!=null)
			setGone(actived_item);
	}
	
}
