package com.amergin;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SystemAdjustActivity extends Activity  implements OnClickListener,OnCheckedChangeListener,OnTouchListener {
	ToggleButton PlayOnStart;
	TextView PlayOnStartText;
	ToggleButton changewhennextbtn;
	TextView changewhennexttext;
	
	ToggleButton autoChangeBkgbtn;
	EditText autoChangeBkgTime;
	LinearLayout time_selector;
	Button  time_save_btn;
	boolean init=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_adjust);
	    InitViews();
	    InitValues();
	    init=false;
	}

	private void InitValues() {
		// TODO Auto-generated method stub
		
		String text="应用启动时自动播放音乐\n";
		if(MyApplication.UDM.PlayOnStart==1)
		{
			PlayOnStartText.setText(text+"(已开启)");
			PlayOnStart.setChecked(true);
		}
		else
		{
			PlayOnStartText.setText(text+"(已关闭)");
			PlayOnStart.setChecked(false);
		}
		text="切歌时切图\n";
		if(MyApplication.UDM.changeBkgWhenNext==1)
		{
			changewhennexttext.setText(text+"(已开启)");
			changewhennextbtn.setChecked(true);
		}
		else
		{
			changewhennexttext.setText(text+"(已关闭)");
			changewhennextbtn.setChecked(false);
		}
		
		
		Log.i("SYS",MyApplication.UDM.autochangeBkgTime+"");
		
		
		text="自动切换背景\n";
		if(MyApplication.UDM.autochangeBkgTime>0)
		{
			
			autoChangeBkgTime.setText(MyApplication.UDM.autochangeBkgTime+""); 
			time_selector.setVisibility(View.VISIBLE);
			autoChangeBkgbtn.setChecked(true);
			time_save_btn.setEnabled(false);
		}
		else
		{
			time_selector.setVisibility(View.GONE);
			time_save_btn.setEnabled(false);
		}
		
		
	}

	private void InitViews() {
		// TODO Auto-generated method stub
		PlayOnStart=(ToggleButton) findViewById(R.id.PlayOnStratBtn);
		PlayOnStartText=(TextView) findViewById(R.id.playonstarttext);
		changewhennextbtn=(ToggleButton) findViewById(R.id.changewhennextbtn);
		changewhennexttext=(TextView) findViewById(R.id.changewhennexttext);
		
		
		
		autoChangeBkgbtn=(ToggleButton) findViewById(R.id.autoChangeBkgbtn);
		autoChangeBkgTime=(EditText) findViewById(R.id.autoChangeBkgTime);
		time_selector= (LinearLayout) findViewById(R.id.time_selector);
		time_save_btn= (Button) findViewById(R.id.time_save_btn);

		
		
		PlayOnStart.setOnCheckedChangeListener(this);
		autoChangeBkgbtn.setOnCheckedChangeListener(this);
		changewhennextbtn.setOnCheckedChangeListener(this);
		time_save_btn.setOnClickListener(this);
		
		
		autoChangeBkgTime.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
			}

			@Override
			public void afterTextChanged(Editable s) {
					time_save_btn.setEnabled(!s.toString().equals(""));
			}
			
		});
		
		
		
		
		((LinearLayout) PlayOnStartText.getParent()).setOnTouchListener(this);
		((LinearLayout) autoChangeBkgbtn.getParent()).setOnTouchListener(this);
		((LinearLayout) autoChangeBkgTime.getParent()).setOnTouchListener(this);
		((LinearLayout) changewhennextbtn.getParent()).setOnTouchListener(this);
		
		
		
	}

	/**
	 * 处理各类点击事件
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id=v.getId();
		if(id==R.id.time_save_btn)
		{
			
			MyApplication.UDM.autochangeBkgTime=Integer.parseInt(autoChangeBkgTime.getText().toString());
			UserDateManager.setLocalUserData(MyApplication.UDM);
			InitValues();
			Toast.makeText(SystemAdjustActivity.this, "将每"+autoChangeBkgTime.getText()+"秒自动切换背景", Toast.LENGTH_SHORT).show();
			time_save_btn.setEnabled(false);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton v, boolean b) {
		// TODO Auto-generated method stub
		int id=v.getId();
		if(id==R.id.PlayOnStratBtn)
		{
			if(b)
			{
				MyApplication.UDM.PlayOnStart=1;
				
			}
			else
			{
				MyApplication.UDM.PlayOnStart=0;
				
			}
			UserDateManager.setLocalUserData(MyApplication.UDM);
			InitValues();
		}
		else if(id==R.id.autoChangeBkgbtn)
		{
			if(b)
			{
				//MyApplication.UDM.autochangeBkgTime=1;
				time_selector.setVisibility(View.VISIBLE);
				if(!init)Toast.makeText(SystemAdjustActivity.this, "请输入时间", Toast.LENGTH_SHORT).show();
				
			}
			else
			{
				if(!init)Toast.makeText(SystemAdjustActivity.this, "已取消自动切换背景", Toast.LENGTH_SHORT).show();
				MyApplication.UDM.autochangeBkgTime=-1;
				changewhennexttext.setText("");
				time_selector.setVisibility(View.GONE);
				autoChangeBkgTime.setText("");
				UserDateManager.setLocalUserData(MyApplication.UDM);
				InitValues();
			}

		}
		else if(id==R.id.changewhennextbtn)
		{
			if(b)
			{
				MyApplication.UDM.changeBkgWhenNext=1;
			}
			else
			{
				MyApplication.UDM.changeBkgWhenNext=0;
			}
			UserDateManager.setLocalUserData(MyApplication.UDM);
			InitValues();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int act=event.getAction();
		if(act==MotionEvent.ACTION_UP)
		{
			v.setBackgroundColor(Color.rgb(255, 255, 255));
			v.performClick();
		}
		else
		{
			v.setBackgroundColor(Color.rgb(225,225,225));
		}
		return true;
	}
	
	
}
