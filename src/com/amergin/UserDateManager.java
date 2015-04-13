package com.amergin;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UserDateManager {
	
	public  int id=-1; //�û�id
	public String username;
	public String password;
	public int PlayOnStart;
	public String email;
	public boolean isMale; //���е�
	public int age; //����
	
	
	
	
	//��������
	public String Icon;  //�������ͷ���·�������Ϊnull���ʾĬ��ͷ��
	public int changeBkgWhenNext;
	public int autochangeBkgTime;
	
	
	
	
	public static String getUserDataPath()
	{
		return MyApplication.getUserDataLocalDirectoryPath()+"/data.txt";
	}
	public UserDateManager(int _id,String _username,String _password)
	{
		id=_id;
		username=_username;
		password=_password;
	}
	public UserDateManager()
	{
		 reset();
	}
	public void reset()
	{
		id=-1;
		username="Amergin";
		password="";
		PlayOnStart=1;
		email="";
		isMale=true;
		age=18;
		Icon=null;
		changeBkgWhenNext=1;
		autochangeBkgTime=20;
	}
	
	public static UserDateManager getLocalUserData()
	{
		String DataString=FileNetManager.ReadMottoFromLocalPath(getUserDataPath());
		Log.i("UserDateManager","��ȡ�û����ã�  "+DataString);
		if(DataString==null||DataString.equals(""))return new UserDateManager();
		Log.i("UserDateManager","���ض�ȡ"+DataString);
		return j_decode(DataString);
	}
	
	public static void setLocalUserData(UserDateManager udm)
	{
		String DataString=j_encode(udm);
		Log.i("UserDateManager","����"+getUserDataPath()+"����"+DataString);
		FileNetManager.WriteMottoToLocalPath(getUserDataPath(),DataString);
		Log.i("UserDateManager","���汾�����"+DataString);
		
	}
	
	//����
	public static UserDateManager j_decode(String str)
	{
		JSONObject jsonObj=null;
		UserDateManager udm=null;
		
		try {
	
			jsonObj = new JSONObject(str);
			
			udm=new UserDateManager(jsonObj.getInt("id"),jsonObj.getString("username"),jsonObj.getString("password"));
			
			udm.PlayOnStart=jsonObj.getInt("PlayOnStart");
			udm.email=jsonObj.getString("email");
			udm.age=jsonObj.getInt("age");
			udm.isMale=jsonObj.getBoolean("isMale");
			udm.Icon=jsonObj.getString("Icon");
			udm.changeBkgWhenNext=jsonObj.getInt("changeBkgWhenNext");
			udm.autochangeBkgTime=jsonObj.getInt("autochangeBkgTime");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.i("UserDateManager","json��������");
			return new  UserDateManager();
		}
		return udm;
	}
	
	//����
	public static String j_encode(UserDateManager data)
	{

		
		String str="";
		str+="{";
		str+="id:"+data.id+",";
		str+="username:'"+data.username+"',";
		str+="password:'"+data.password+"',";
		str+="email:'"+data.email+"',";
		str+="isMale:'"+data.isMale+"',";
		str+="age:'"+data.age+"',";
		str+="Icon:'"+data.Icon+"',";
		str+="changeBkgWhenNext:'"+data.changeBkgWhenNext+"',";
		str+="autochangeBkgTime:'"+data.autochangeBkgTime+"',";
		
		
		str+="PlayOnStart:'"+data.PlayOnStart+"'";
		str+="}";
		return str;
	}
	
	
	
	public void updateUserDataAtServer(final Handler h,final int UPDATE_O)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				Log.i("�û�","����");
				String sex="1";
				if(isMale)sex="0";
				
				String urlString=MyApplication.domain+"/user/update.php?";
				urlString+=("userid="+MyApplication.UDM.id);
				urlString+=("&sex="+sex);
				urlString+=("&age="+age);

				//urlString+=("&mood="+MyApplication.mood);
				
				Log.i("�û�","�ύ"+urlString);
				String resultString=FileNetManager.postRequestToHttp(urlString, null);
				try {
					Log.i("�û�","��÷��ؽ��"+resultString);
					JSONObject jo=new JSONObject(resultString);
					int errorcode=jo.getInt("errcode");
					if(errorcode==0)
					{
						//��ϲ...
						h.sendEmptyMessage(UPDATE_O);
					}
					else
					{
						Log.i("�û�","������");
						h.sendEmptyMessage(UPDATE_O+1);	
						return;
					}	
				} catch (JSONException e) {
					e.printStackTrace();
					
					h.sendEmptyMessage(UPDATE_O+1);
					
				}

			}}).start();
		
	}
	
	
	
	
	
}
