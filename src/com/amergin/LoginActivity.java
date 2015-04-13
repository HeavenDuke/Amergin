package com.amergin;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;

public class LoginActivity extends Activity {
	
	private LinearLayout reg_Panel;
	private BootstrapButton to_reg_btn;
	private BootstrapButton reg_btn;
	private BootstrapEditText reg_username;
	private BootstrapEditText reg_paswd;
	private BootstrapEditText reg_paswd2;
	private BootstrapEditText reg_email;
	private BootstrapEditText reg_age;
	private RadioGroup reg_sex;
	private String reg_sex_value="0";
	private LinearLayout log_Panel;
	private BootstrapButton to_log_btn;
	private BootstrapButton log_btn;
	private BootstrapEditText log_username;
	private BootstrapEditText log_paswd;
	
	private static  Handler hd;
	private  String u_username="Amergin1";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		findviews();
		HandlerInit();
	}



	private void HandlerInit() {

		 hd=new Handler(){
				@Override
				public void handleMessage(Message msg) {
					switch(msg.what)
					{
					case 1://登陆或者注册成功
						MyApplication.UDM.username=u_username;
						MyApplication.UDM.password=log_paswd.getText().toString();
						
						Log.i("UserData","登陆完成"+MyApplication.UDM.username);
						UserDateManager.setLocalUserData(MyApplication.UDM);
						LoginActivity.this.finish();
						break;
					case 2:
						Toast.makeText(LoginActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
						log_btn.setEnabled(true);
						break;
					}
					super.handleMessage(msg);
				}
			};
	}



	void ToReg(boolean b)
	{
		if(b&&reg_Panel.getVisibility()==View.VISIBLE)return;
		if(!b&&log_Panel.getVisibility()==View.VISIBLE)return;
		if(log_Panel.getVisibility()==View.VISIBLE)
		{
			log_Panel.setVisibility(View.GONE);
			reg_Panel.setVisibility(View.VISIBLE);
			to_log_btn.setLeftIcon("fa-angle-left");
			to_reg_btn.setRightIcon(null);
		}
		else
		{
		//	LayoutParams lp=new LayoutParams();
			log_Panel.setVisibility(View.VISIBLE);
			reg_Panel.setVisibility(View.GONE);
			to_log_btn.setLeftIcon(null);
			to_reg_btn.setRightIcon("fa-angle-right");
		}
		
		android.view.ViewGroup.LayoutParams llp=to_log_btn.getLayoutParams();
		android.view.ViewGroup.LayoutParams rlp=to_reg_btn.getLayoutParams();
		Log.i("BOOT",""+llp.width+","+rlp.width);
		
		
		llp.width=to_reg_btn.getWidth();
		rlp.width=to_log_btn.getWidth();
		to_log_btn.setLayoutParams(llp);
		to_reg_btn.setLayoutParams(rlp);
		
		
	}
	private void findviews() {
		// TODO Auto-generated method stub
		reg_Panel=(LinearLayout) findViewById(R.id.reg_Panel);
		log_Panel=(LinearLayout) findViewById(R.id.log_Panel);
		to_reg_btn=(BootstrapButton) findViewById(R.id.to_reg_btn);
		to_log_btn=(BootstrapButton) findViewById(R.id.to_log_btn);
		reg_btn=(BootstrapButton) findViewById(R.id.reg_btn);
		log_btn=(BootstrapButton) findViewById(R.id.log_btn);
		reg_username=(BootstrapEditText) findViewById(R.id.reg_username);
		reg_paswd=(BootstrapEditText) findViewById(R.id.reg_paswd);
		reg_paswd2=(BootstrapEditText) findViewById(R.id.reg_paswd_confirm);

		log_username=(BootstrapEditText) findViewById(R.id.log_username);
		log_paswd=(BootstrapEditText) findViewById(R.id.log_paswd);
		
		reg_email=(BootstrapEditText) findViewById(R.id.reg_email);
		reg_age=(BootstrapEditText) findViewById(R.id.reg_age);
		reg_sex=(RadioGroup) findViewById(R.id.reg_sex);
		OCL ocl=new OCL();
		to_reg_btn.setOnClickListener(ocl);
		to_log_btn.setOnClickListener(ocl);
		reg_btn.setOnClickListener(ocl);
		log_btn.setOnClickListener(ocl);
		
		
		reg_sex.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				// TODO Auto-generated method stub
				RadioButton rb=	(RadioButton)findViewById(checkedId);
				reg_sex_value=rb.getText().toString();
				if(reg_sex_value.equals("男"))
				{
					reg_sex_value="0";
				}
				else
				{
					reg_sex_value="1";
				}
				
				//m_toast(reg_sex_value);
			}});
		
		LayoutParams llp=log_Panel.getLayoutParams();
		LayoutParams rlp=reg_Panel.getLayoutParams();
		llp.width=ScreenUtils.getScreenWidth(this)*3/4;
		rlp.width=llp.width;
		log_Panel.setLayoutParams(llp);
		reg_Panel.setLayoutParams(rlp);
		
		
		
	}
	class OCL implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId())
			{

			case R.id.to_log_btn:
				ToReg(false);
				break;
			case R.id.to_reg_btn:
				ToReg(true);
				break;
			case R.id.log_btn:
				Log();
				break;
			case R.id.reg_btn:
				Reg();
				break;
			}
		}


		
	}

	public void m_toast(String info)
	{
		Toast.makeText(LoginActivity.this, info, Toast.LENGTH_SHORT).show();
	}
	
	
	//检测用户名是否合法
	private boolean testusername(String u)
	{
		if(u.length()<3)
		{
			m_toast("用户名长度应不小于3");
			return false;
		}
		return true;
	}
	
	//检测密码是否合法
	private boolean testpaswd(String u)
	{
		if(u.length()<6)
		{
			m_toast("密码长度应不小于6");
			return false;
		}
		 String regex = "\\w+";
		 boolean r= u.matches(regex);
		 if(!r)m_toast("密码中只能有数字字母下划线");
		return r;
	}
		
	
	//检测邮箱是否合法
	private boolean testemail(String u)
	{
		 String regex = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		 return u.matches(regex);
	}
	
	private boolean testage(String u)
	{
		 String telReg = "\\d*";//11位纯数字
		 if(u.matches(telReg))
		 {
			 int s=Integer.parseInt(u, 10);
			 if(s>200)
			 {
				 m_toast("= =,您老真是活到老学到老....");
			 }
			 return true;
		 }
		 m_toast("年龄填写有问题呢");
		 return false;
	}
	//注册
	private void Reg() {
		// TODO Auto-generated method stub
		//先检查空不空
		String username=reg_username.getText().toString();
		String paswd=reg_paswd.getText().toString();
		String paswd_confirm=reg_paswd2.getText().toString();
		String email=reg_email.getText().toString();
		String age=reg_age.getText().toString();
		if(username.equals(""))
		{
			//提示空
			m_toast("用户名怎么能为空");
			reg_username.requestFocus();
			return;
		}
		if(!testusername(username))
		{
			//提示不合法
			reg_username.requestFocus();
			return;
		}
		
		if(paswd.equals(""))
		{
			//提示空
			m_toast("密码也不能空啊");
			reg_paswd.requestFocus();
			return;
		}
		if(!testpaswd(paswd))
		{
			//提示不合法
			reg_paswd.requestFocus();
			return;
		}
		
		if(!paswd.equals(paswd_confirm))
		{
			//提示密码不一致
			m_toast("密码不一致");
			reg_paswd2.requestFocus();
			return;
		}
		
		if(email.equals(""))
		{
			//提示空
			m_toast("为帮助您将来找回密码,请输入邮箱");
			reg_email.requestFocus();
			return;
		}
		if(!testemail(email))
		{
			//提示空
			m_toast("邮箱格式错误！");
			reg_email.requestFocus();
			return;
		}
		if(age.equals(""))age="18";
		if(!testage(age))return;
		
		
		//向服务端发消息，注册
		String urlString=MyApplication.domain+"/user/register.php?";
		urlString+=("&username="+username);
		urlString+=("&password="+paswd);
		urlString+=("&email="+email);
		urlString+=("&sex="+reg_sex_value);
		urlString+=("&age="+age);
		u_username=username;
		postRequest(urlString,true);
		//提交请求了该
		reg_btn.setEnabled(false);
	}
	
	//登陆
	private void Log() {
		// TODO Auto-generated method stub
		String username=log_username.getText().toString();
		String paswd=log_paswd.getText().toString();
		if(username.equals(""))
		{
			//提示空
			m_toast("用户名不可为空");
			log_username.requestFocus();
			return;
		}
		if(!testusername(username))
		{
			//提示不合法
			log_username.requestFocus();
			return;
		}
		
		if(paswd.equals(""))
		{
			//提示空
			m_toast("忘记输入密码了么");
			log_paswd.requestFocus();
			return;
		}
		if(!testpaswd(paswd))
		{
			//提示不合法
			log_paswd.requestFocus();
			return;
		}
		
		
		
		String urlString=MyApplication.domain+"/user/login.php?";
		urlString+=("&username="+username);
		urlString+=("&password="+paswd);
		u_username=username;
		postRequest(urlString,false);
		//提交请求了该
		log_btn.setEnabled(false);
		
	}

	private void postRequest(final  String urlString,final boolean isReg) {
		// TODO Auto-generated method stub
		if(MyApplication.DEBUG)
		{
			if(isReg)
			{
				m_toast("注册"+urlString);
			}else
			{
				m_toast("登陆"+urlString);
			}
		}
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result=FileNetManager.postRequestToHttp(urlString, null);
				Log.i("Login","返回值"+result);
				try {
					JSONObject jo=new JSONObject(result);
					if(jo.getInt("errcode")==0)
					{
						MyApplication.UDM.id=jo.getInt("userid");
						MyApplication.UDM.email=jo.getString("user_email");
						MyApplication.UDM.isMale=(jo.getInt("user_sex")==0);
						MyApplication.UDM.age=jo.getInt("user_age");
						MyApplication.UDM.username=jo.getString("user_username");
						
						
						
						UserDateManager.setLocalUserData(MyApplication.UDM);
					}
					else
					{
						Message msg=new Message();
						msg.obj=jo.getString("errmsg");
						msg.what=2;
						hd.sendMessage(msg);
						return;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.i("Login","错了？"+e.getMessage());
				}
				
				Message msg=new Message();
				msg.what=1;
				hd.sendMessage(msg);
				
			}}).start();
	}
	
	
	
	
	
}
