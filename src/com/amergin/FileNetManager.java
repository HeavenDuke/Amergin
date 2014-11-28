package com.amergin;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;


public class FileNetManager {
	
	  public static  Bitmap getLoacalBitmap(String url) {
	         try {
	              FileInputStream fis = new FileInputStream(url);
	              return BitmapFactory.decodeStream(fis);  ///����ת��ΪBitmapͼƬ        
	           } catch (FileNotFoundException e) {
	              e.printStackTrace();
	              return null;
	         }
	    }
	  
	  public static Bitmap getLastBitmap(String path)
	  {
          int end = path.lastIndexOf(File.separator);
          String _filePath = path.substring(0, end);
          File filePath = new File(_filePath);
          File[] c_files=filePath.listFiles();
          if(c_files.length==0)return null;
          return getLoacalBitmap(c_files[0].getAbsolutePath());
        	
	  }
	  /**
     * Save Bitmap to a file.����ͼƬ��SD��������ֻ����һ���ļ�
     * 
     * @param bitmap
     * @param file
     * @return error message if the saving is failed. null if the saving is
     *         successful.
     * @throws IOException
     */
    public static void saveBitmapToFile(Bitmap bitmap, String _file){
        BufferedOutputStream os = null;
        try {
            File file = new File(_file);
            int end = _file.lastIndexOf(File.separator);
            String _filePath = _file.substring(0, end);
            File filePath = new File(_filePath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            FileNetManager.DeleteAllFilesOfADirectory(file.getParentFile());
            file.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        } 
        catch(Exception e)
        {
        	
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                   // Log.e(TAG_ERROR, e.getMessage(), e);
                }
            }
        }
    }
    
    public static void DeleteAllFilesOfADirectory(File file)
    {
        if(file.isDirectory()){
        	 File c_files[] = file.listFiles(); // ����Ŀ¼�����е��ļ� files[];
        	 for (int i = 0; i < c_files.length; i++) { // ����Ŀ¼�����е��ļ�
        	     c_files[i].delete();
        	 	}
        }
 
    }
    
    
   public static Bitmap getImage(String url) //����
	  {
	   Log.i("KKK","��ʼ����ͼƬ"+url);
		  URL imageUrl=null;
		  Bitmap bitmap=null;
		  try{
			  imageUrl=new URL(url);
			  HttpURLConnection conn=(HttpURLConnection)imageUrl.openConnection();
			  conn.setDoInput(true);
			  conn.connect();
			  InputStream is=conn.getInputStream();
			  BufferedInputStream bis=new BufferedInputStream(is);
			  bitmap=BitmapFactory.decodeStream(bis);
			  bis.close();
			  is.close();
		  }
		  catch(Exception e)
		  {
			  Log.i("KKK","���س���������");
			  //���������ҵ��������󲻵���ӭͼƬ....
			  //����returnһ������ͼƬ
		  }
		  return bitmap;
		  
	  }
   	//��get���͵����󻻳�post��Ϣ����,Լ����������ֻ�����ı�,String����"id=234&u=34"
 	public static String postRequestToHttp(String getModeUrlString,String Encoding)
 	{
 		if(Encoding==null||Encoding=="")Encoding="utf-8";
 		int pos=getModeUrlString.indexOf("?");
 		if(pos==-1)
 			return "";
 		String urlString=getModeUrlString.substring(0, pos);
 		String valueString =getModeUrlString.substring(pos+1, getModeUrlString.length());
 		
 		try {
 			URL url=new URL(urlString);
 			URLConnection urlConn= url.openConnection();
 			urlConn.setDoOutput(true);
 			urlConn.getOutputStream().write(valueString.getBytes(Encoding));
 			return InputStream2Text(urlConn.getInputStream(),null);
 			
 		} catch (Exception e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 		return "";
 	}
 	
 	
   	//��get���͵����󻻳�post��Ϣ����,Լ����������ֻ�����ı�,String����"id=234&u=34"
 	public static String getRequestToHttp(String getModeUrlString,String Encoding)
 	{
 		if(Encoding==null||Encoding=="")Encoding="utf-8";
 	//	int pos=getModeUrlString.indexOf("?");
 		//if(pos==-1)
 			//return "";
 		//String urlString=getModeUrlString.substring(0, pos);
 		//String valueString =getModeUrlString.substring(pos+1, getModeUrlString.length());
 		
 		try {
 			URL url=new URL(getModeUrlString);
 			URLConnection urlConn= url.openConnection();
 			//urlConn.setDoOutput(true);
 			//urlConn.getOutputStream().write(valueString.getBytes(Encoding));
 			Log.i("KKK","1456d");
 			return InputStream2Text(urlConn.getInputStream(),null);
 			
 		} catch (Exception e) {
 			// TODO Auto-generated catch block
 			Log.i("KKK","145635d");
 			e.printStackTrace();
 		}
 		return "";
 	}
 	
 	
 	
 	//����ҳ��ַ,����ҳ��html����
 	public static String getContentFromUrl(String urlString,String Encoding)
 	{
 		return InputStream2Text(getInputStreamFromUrl(urlString),Encoding);
 	}
	//��һ��URL����,����һ��������
	public static InputStream getInputStreamFromUrl(String urlString)
	{
		URL url=null;
		HttpURLConnection urlConn=null;
		try {
			url=new URL(urlString);
			urlConn=(HttpURLConnection) url.openConnection();
			return urlConn.getInputStream();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	//��������ת���ı�,Ĭ�ϱ�����utf8,����InputStream2Text( input, null);
	public static String InputStream2Text(InputStream input,String Encoding)
	{
		if(Encoding==null||Encoding=="")Encoding="utf-8";
		String str="";
		try {
			InputStreamReader is=new InputStreamReader(input,Encoding);
			BufferedReader br=new BufferedReader(is);
			String tempStr="";
				while((tempStr=br.readLine()) != null)
					str+=tempStr+"\r\n";
				br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Ҫ��ı��벻֧��,����Ƿ�д�������";
		}
		return str;
	}
	
	public static String ReadMottoFromLocalPath(String path)
	  {
		  File file= new File(path);
		  try {  
	            FileInputStream inputStream = new FileInputStream(file);
	            byte[] bytes = new byte[1];  
	            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();  
	            while (inputStream.read(bytes) != -1) {  
	                arrayOutputStream.write(bytes, 0, bytes.length);  
	            }  
	            inputStream.close();  
	            arrayOutputStream.close();  
	            String content = new String(arrayOutputStream.toByteArray());  
	            return content;
	  
	        } catch (FileNotFoundException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }    
		  return "";
	  }

	  
	 public static void WriteMottoToLocalPath(String path,String str)
	  	  {
	  		  File file=new File(path);
	  		System.out.println(path);
	            int end = path.lastIndexOf(File.separator);
	        	System.out.println(File.separator);
	            String _filePath = path.substring(0, end);
	            File filePath = new File(_filePath);

	            if (!filePath.exists()) {
	                filePath.mkdirs();
	            }
	  		  try{
	            FileOutputStream outputStream = new FileOutputStream(file);  
	            outputStream.write(str.getBytes());  
	            outputStream.flush();  
	            outputStream.close();  
	        } catch (FileNotFoundException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	  }
	
    
}
