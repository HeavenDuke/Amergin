package com.amergin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;

public class PersonalInfoActivity extends Activity implements OnClickListener,OnTouchListener {

	public boolean needEmailChange=false; //直接更改邮箱不知合不合适,涉及到邮箱验证的部分
	
	final public static  int UPDATE_SEX=0;
	final public static int UPDATE_AGE=5;
	final public static int UPDATE_EMAIL=10;
	final public static int UPDATE_USERNAME=15;
	//public static int UPDATE_SEX=1;
	
//	private WheelView mood;

	/*
	 * 用户信息应该包括......
	 * 头像，用户名，年龄，性别，email
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	private LinearLayout person_parent_layout; 
	
	
	private BootstrapCircleThumbnail info_Icon;
	private TextView info_username;
	private TextView info_email;
	private TextView info_age;
	private TextView info_sex;
	
	
	private LinearLayout info_sex_parent;
	private LinearLayout info_age_parent;
	private LinearLayout info_email_parent;
	
	
	private TextView info_sex_btn;
	private TextView info_age_btn;
	private EditText info_age_btn_edit;
	private Button info_age_btn_edit_ok;
	private TextView info_email_btn;
	private EditText info_email_btn_edit;
	private Button info_email_btn_edit_ok;
	
	
	
	
	private PopupWindow icon_selector_prompt;
	private BootstrapButton pop_album_selector_btn;
	private BootstrapButton pop_camera_selector_btn;
	private BootstrapButton pop_reset_selector_btn;
	
	
	private  Handler ah;
	
	public static int getImageFromAlbumsCode=1;
	public static int getImageFromCameraCode=2;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personalinfo);
		
	
		
		
		
		initViews();
		initValues();

				
		
		
		
//		mood=new WheelView(this);
//        mood.setVisibleItems(5);  
//        mood.setCyclic(true);
//        mood.setAdapter(new ArrayWheelAdapter<String>(Mood_List.moodMap));
//        mood.addChangingListener(new OnWheelChangedListener() {  
//            public void onChanged(WheelView wheel, int oldValue,  
//                    int newValue) {  
//            	Toast.makeText(PersonalInfoActivity.this, Mood_List.moodMap[newValue], Toast.LENGTH_SHORT).show();
//            }  
//        });
//        ((LinearLayout)findViewById(R.id.playout)).addView(mood);
//		
		
		
		this.ah=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				int what=msg.what;
				
				
				switch(what)
				{
					case PersonalInfoActivity.UPDATE_SEX: 
						info_sex_btn.setText("[更新成功]");
						
						UserDateManager.setLocalUserData(MyApplication.UDM);
						initValues();
						this.sendEmptyMessageDelayed(PersonalInfoActivity.UPDATE_SEX+2, 2000);
						break;
					case (PersonalInfoActivity.UPDATE_SEX+1):
						//Toast.makeText(PersonalInfoActivity.this, "数据更新失败", Toast.LENGTH_SHORT).show();
						MyApplication.UDM.isMale=!MyApplication.UDM.isMale;
						info_sex_btn.setText("[更新失败]");
						this.sendEmptyMessageDelayed(PersonalInfoActivity.UPDATE_SEX+2, 2000);
						break;
					case (PersonalInfoActivity.UPDATE_SEX+2):
						info_sex_btn.setVisibility(View.INVISIBLE);
						break;
					case PersonalInfoActivity.UPDATE_AGE:
						info_age_btn.setText("[更新成功]");
						info_age.setVisibility(View.VISIBLE);
						UserDateManager.setLocalUserData(MyApplication.UDM);
						initValues();
						this.sendEmptyMessageDelayed(PersonalInfoActivity.UPDATE_AGE+2, 2000);
						break;
					case PersonalInfoActivity.UPDATE_AGE+1:
						MyApplication.UDM=UserDateManager.getLocalUserData();
						info_age_btn.setText("[更新失败]");
						this.sendEmptyMessageDelayed(PersonalInfoActivity.UPDATE_AGE+2, 2000);
						break;
					case PersonalInfoActivity.UPDATE_AGE+2:
						info_age_btn.setVisibility(View.INVISIBLE);
						info_age_btn_edit.setVisibility(View.INVISIBLE);
						info_age_btn_edit_ok.setVisibility(View.INVISIBLE);
						info_age.setVisibility(View.VISIBLE);
						break;
						
					case PersonalInfoActivity.UPDATE_EMAIL:
						info_email_btn.setText("[更新成功]");
						info_email.setVisibility(View.VISIBLE);
						UserDateManager.setLocalUserData(MyApplication.UDM);
						initValues();
						this.sendEmptyMessageDelayed(PersonalInfoActivity.UPDATE_EMAIL+2,2000);
						break;
					case PersonalInfoActivity.UPDATE_EMAIL+1:
						MyApplication.UDM=UserDateManager.getLocalUserData();
						info_email_btn.setText("[更新失败]");
						this.sendEmptyMessageDelayed(PersonalInfoActivity.UPDATE_EMAIL+2, 2000);
						break;
					case PersonalInfoActivity.UPDATE_EMAIL+2:
						if(needEmailChange){
							info_email_btn.setVisibility(View.INVISIBLE);
							info_email_btn_edit.setVisibility(View.INVISIBLE);
							info_email_btn_edit_ok.setVisibility(View.INVISIBLE);
						}
						info_email.setVisibility(View.VISIBLE);
						break;
						
					
						
					default:
						
						break;
				}
				
				
				super.handleMessage(msg);
			}
			
		};

		
	//	ah.sendEmptyMessage(1);
		
	}


	//检测邮箱是否合法
	private boolean testemail(String u)
	{
		 String regex = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		 return u.matches(regex);
	}
	
	private void initPopWindow() {
		// TODO Auto-generated method stub
		
		

         //获取LayoutInflater实例 
		LayoutInflater inflater  = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE); 
         //获取弹出菜单的布局
		RelativeLayout pop_content=new RelativeLayout(this);
        View layout = inflater.inflate(R.layout.icon_selector_prompt,pop_content);   //待修改.....
         //设置popupWindow的布局 
         icon_selector_prompt =new PopupWindow(pop_content, WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT); 
         
         pop_album_selector_btn=(BootstrapButton) layout.findViewById(R.id.Albums_select);
         pop_camera_selector_btn=(BootstrapButton) layout.findViewById(R.id.Camera_select);
         pop_reset_selector_btn=(BootstrapButton) layout.findViewById(R.id.Reset_select);
         
         pop_album_selector_btn.setOnClickListener(this);
         pop_camera_selector_btn.setOnClickListener(this);
         pop_reset_selector_btn.setOnClickListener(this);
         
         
         
	}


	private void initValues() {
		// TODO Auto-generated method stub
		UserDateManager  usrdata=MyApplication.UDM;
		
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
				this.info_Icon.setImage(iconBitmap);
				iconBitmap.recycle();  //回收？？
			}
		}
		
		//更新用户名
		this.info_username.setText(usrdata.username);
		
		//更新性别
		String sex="女";
		if(usrdata.isMale)sex="男";
		this.info_sex.setText(sex);
		
		
		//更新email
		if(usrdata.email!=null&&!usrdata.email.equals("")){
			this.info_email.setText(usrdata.email);
		}
		else{
			this.info_email.setText("未设");	
		}
		
		
		
		//更新age
		if(usrdata.age==-1){
			this.info_age.setText("未设");	
		}
		else{
			this.info_age.setText(usrdata.age+"");
		}
		
	}


	private void initViews() {
		// TODO Auto-generated method stub
		info_Icon=(BootstrapCircleThumbnail) findViewById(R.id.info_Icon);
		info_username=(TextView) findViewById(R.id.info_username);
		info_email=(TextView) findViewById(R.id.info_email);
		info_age=(TextView) findViewById(R.id.info_age);
		info_sex=(TextView) findViewById(R.id.info_sex);
		
		info_sex_btn=(TextView) findViewById(R.id.info_sex_btn);
		
		info_age_btn=(TextView) findViewById(R.id.info_age_btn);
		info_age_btn_edit=(EditText) findViewById(R.id.info_age_btn_edit);
		info_age_btn_edit_ok=(Button) findViewById(R.id.info_age_btn_edit_ok);
		
		
		info_email_btn=(TextView) findViewById(R.id.info_email_btn);
		info_email_btn_edit=(EditText) findViewById(R.id.info_email_btn_edit);
		info_email_btn_edit_ok=(Button) findViewById(R.id.info_email_btn_edit_ok);
		
		
		
		info_sex_parent=(LinearLayout) findViewById(R.id.info_sex_parent);
		info_age_parent=(LinearLayout) findViewById(R.id.info_age_parent);
		info_email_parent=(LinearLayout) findViewById(R.id.info_email_parent);
		
		
		
		person_parent_layout=(LinearLayout)findViewById(R.id.person_parent_layout);
		
		
		
		
		
		initPopWindow();
		
		((LinearLayout) info_Icon.getParent()).setOnTouchListener(this);
		info_email_parent.setOnTouchListener(this);
		info_sex_parent.setOnTouchListener(this);
		info_age_parent.setOnTouchListener(this);
		
	
		
		
		//绑定事件
		
		
		info_Icon.setOnClickListener(this);
		person_parent_layout.setOnClickListener(this);
		info_sex_parent.setOnClickListener(this);
		info_age_parent.setOnClickListener(this);
		info_email_parent.setOnClickListener(this);
		
		info_age_btn_edit_ok.setOnClickListener(this);
		info_email_btn_edit_ok.setOnClickListener(this);
		
		
		
		
		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.info_Icon:
				//提示选择使用相机还是相册
				if(icon_selector_prompt.isShowing())
				{
					icon_selector_prompt.dismiss();
				}
				else
				{
					icon_selector_prompt.showAsDropDown(v);
				}
				break;
			case R.id.Albums_select:
				//唤起图片选择
				Intent picture = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(picture, PersonalInfoActivity.getImageFromAlbumsCode);
				icon_selector_prompt.dismiss();
				break;
			case R.id.Camera_select:
				//唤起相机
				Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(camera, PersonalInfoActivity.getImageFromCameraCode);
				icon_selector_prompt.dismiss();
				break;
			case R.id.Reset_select:
				//设为默认
				   this.info_Icon.setImage(R.drawable.headicon); //显示
				   MyApplication.UDM.Icon="";
				   UserDateManager.setLocalUserData(MyApplication.UDM); //保存用户信息到本地
				   icon_selector_prompt.dismiss();
				break;
			case R.id.info_sex_parent:
				if(this.info_sex_btn.getVisibility()==View.INVISIBLE)
				{
					info_sex_btn.setText("[再点击修改]");
					info_sex_btn.setVisibility(View.VISIBLE);
				}
				else
				{
					MyApplication.UDM.isMale=!MyApplication.UDM.isMale;
					MyApplication.UDM.updateUserDataAtServer(this.ah,PersonalInfoActivity.UPDATE_SEX);
					info_sex_btn.setText("[更新中...]");
				}
				break;

			case R.id.info_age_parent:
				if(info_age_btn.getVisibility()==View.INVISIBLE)
				{
					info_age_btn.setText("[再点击修改]");
					info_age_btn.setVisibility(View.VISIBLE);
				}
				else if(info_age_btn.getVisibility()==View.VISIBLE)
				{
					info_age_btn.setVisibility(View.GONE);
					info_age.setVisibility(View.GONE);
					info_age_btn_edit.setText(MyApplication.UDM.age+"");
					this.info_age_btn_edit.setVisibility(View.VISIBLE);
					this.info_age_btn_edit_ok.setVisibility(View.VISIBLE);
					//强制显示输入法
					((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(info_age_btn_edit,InputMethodManager.SHOW_FORCED);
					
					info_age_btn_edit.selectAll();
				}
				break;
			case R.id.info_age_btn_edit_ok:
				String age=info_age_btn_edit.getText().toString();
				if(age.equals(""))return;
				//强制隐藏输入法
				((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(info_age_btn_edit.getWindowToken(), 0);
				MyApplication.UDM.age=Integer.parseInt(age);
				MyApplication.UDM.updateUserDataAtServer(this.ah,PersonalInfoActivity.UPDATE_AGE);
				this.info_age_btn_edit.setVisibility(View.INVISIBLE);
				this.info_age_btn_edit_ok.setVisibility(View.INVISIBLE);
				info_age_btn.setText("[更新中...]");
				info_age_btn.setVisibility(View.VISIBLE);
				break;
				
			case R.id.info_email_parent:
				if(!this.needEmailChange)return;
				if(info_email_btn.getVisibility()==View.INVISIBLE)
				{
					info_email_btn.setText("[再点修改]");
					info_email_btn.setVisibility(View.VISIBLE);
				}
				else if(info_email_btn.getVisibility()==View.VISIBLE)
				{
					this.info_email.setVisibility(View.GONE);
					info_email_btn.setVisibility(View.GONE);
					info_email_btn_edit.setText(MyApplication.UDM.email);
					this.info_email_btn_edit.setVisibility(View.VISIBLE);
					this.info_email_btn_edit_ok.setVisibility(View.VISIBLE);
					//强制显示输入法
					((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(info_email_btn_edit,InputMethodManager.SHOW_FORCED);
					
					info_email_btn_edit.selectAll();
				}
				break;
			case R.id.info_email_btn_edit_ok:
				
				String email=info_email_btn_edit.getText().toString();
				if(!testemail(email)){
					Toast.makeText(PersonalInfoActivity.this, "邮箱不合法诶", Toast.LENGTH_SHORT).show();
					return;
				}
				//强制隐藏输入法
				((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(info_age_btn_edit.getWindowToken(), 0);
				
				MyApplication.UDM.email=email;
				MyApplication.UDM.updateUserDataAtServer(this.ah,PersonalInfoActivity.UPDATE_EMAIL);
				this.info_email_btn_edit.setVisibility(View.INVISIBLE);
				this.info_email_btn_edit_ok.setVisibility(View.INVISIBLE);
				info_email_btn.setText("[更新中...]");
				info_email_btn.setVisibility(View.VISIBLE);
				break;
				
				
			default:
				if(icon_selector_prompt.isShowing())
				{
					icon_selector_prompt.dismiss();
				}
				initValues();
				break;
		}
		
//		
//		if(id==R.id.info_Icon)//换头像
//		{
//
//			
//		}
//		else if(id==R.id.Albums_select)
//		{
//			//唤起图片选择
//			Intent picture = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//			startActivityForResult(picture, PersonalInfoActivity.getImageFromAlbumsCode);
//			icon_selector_prompt.dismiss();
//		}
//		else if(id==R.id.Camera_select)
//		{
//			//唤起相机
//			Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//			startActivityForResult(camera, PersonalInfoActivity.getImageFromCameraCode);
//			icon_selector_prompt.dismiss();
//		}
//		else if(id==R.id.Reset_select)
//		{
//			//设为默认
//			   this.info_Icon.setImage(R.drawable.headicon); //显示
//			   MyApplication.UDM.Icon="";
//			   UserDateManager.setLocalUserData(MyApplication.UDM); //保存用户信息到本地
//			   icon_selector_prompt.dismiss();
//		}
//		else if(id==R.id.info_sex_parent)
//		{
//			if(this.info_sex_btn.getVisibility()==View.INVISIBLE)
//			{
//				info_sex_btn.setText("[再点击修改]");
//				info_sex_btn.setVisibility(View.VISIBLE);
//			}
//			else
//			{
//				MyApplication.UDM.isMale=!MyApplication.UDM.isMale;
//				MyApplication.UDM.updateUserDataAtServer(this.ah);
//				info_sex_btn.setText("[更新中...]");
//			}
//		}
//		else if(id==R.id.info_age_parent)
//		{
//			if(info_age_btn.getVisibility()==View.INVISIBLE)
//			{
//				info_sex_btn.setText("[再点击修改]");
//				info_sex_btn.setVisibility(View.VISIBLE);
//			}
//			else if(info_age_btn.getVisibility()==View.VISIBLE)
//			{
//				info_sex_btn.setVisibility(View.GONE);
//				
//				
//				
//				
//			}
//			else
//			{
//				
//			}
//		}
//		else 
//		{
//			Log.i("PER",id+"j进来了");
//			if(icon_selector_prompt.isShowing())
//			{
//				icon_selector_prompt.dismiss();
//			}
//			
//			initValues();
//		}
//		
//	Log.i("PER",id+"");
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if( resultCode != Activity.RESULT_OK|| null == data)return;
		Bitmap bitmap=null;
		if(requestCode==PersonalInfoActivity.getImageFromAlbumsCode) //从相册返回照片
		{
			   Uri selectedImage = data.getData();
			   String[] filePathColumns={MediaStore.Images.Media.DATA};
			   Cursor c = this.getContentResolver().query(selectedImage, filePathColumns, null,null, null);
			   c.moveToFirst();
			   int columnIndex = c.getColumnIndex(filePathColumns[0]);
			   String picturePath= c.getString(columnIndex);
			   c.close();
			   bitmap= BitmapFactory.decodeFile(picturePath);	
		}
		else if(requestCode==PersonalInfoActivity.getImageFromCameraCode)//从相机返回....
		{
			   Bundle bundle = data.getExtras();
			   //获取相机返回的数据，并转换为图片格式
			   bitmap = (Bitmap)bundle.get("data");
		}
		
		if(null!=bitmap)
		{
			/*
			 * 必要时进行裁剪........................
			 * 
			 * 
			 * 
			 */
			
			   this.info_Icon.setImage(bitmap); //显示
			   String iconPath=MyApplication.getUserDataLocalDirectoryPath()+"/icon.jpg";
			   FileNetManager.saveBitmapToFile(bitmap,iconPath); //保存
			   MyApplication.UDM.Icon=iconPath;
			   UserDateManager.setLocalUserData(MyApplication.UDM); //保存用户信息到本地
			   bitmap.recycle(); //回收....
		}
		else
		{
			//木有图片
		}
		
		
		
		
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		int act=event.getAction();
		//Log.i("Touch","code "+act);
		
		if(act==MotionEvent.ACTION_UP)
		{
			//Log.i("Touch","up");
			v.setBackgroundColor(Color.rgb(255, 255, 255));
			
			v.performClick();
		}
		else
		{
			//Log.i("Touch","down");
			v.setBackgroundColor(Color.rgb(225,225,225));
		}
		return true;
	}
	
	
	
	
	
	
}
