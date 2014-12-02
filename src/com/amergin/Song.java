package com.amergin;

import java.io.UnsupportedEncodingException;

import android.util.Log;

public class Song {
	private String id;
	private String name;
	private String album;
	private String artist;
	private String lyricUrl;
	private String MusicName;
	private String MusicExt;//扩展名
	private String charset="UTF-8";
	private String localPath="";
	public Song(String id, String name, String album, String artist,String lrc) {
		SetValue( id,  name,  album,  artist,lrc);
	}
	public void SetValue(String id, String name, String album, String artist,String lrc) {
		this.id = id;
		try {
			this.name = name;
			this.album = java.net.URLDecoder.decode(album, "UTF-8");
			this.artist = java.net.URLDecoder.decode(artist, "UTF-8");
			Log.i("TTT","艺术家"+this.artist);
			this.lyricUrl =lrc;
		splitName();
		//show();
		
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private void show()
	{
		Log.i("TTT","id"+id);
		Log.i("TTT","name:"+name);
		Log.i("TTT","album:"+album);
		Log.i("TTT","artist:"+artist);
		Log.i("TTT","lyricUrl:"+lyricUrl);
		Log.i("TTT",getEncoding(lyricUrl));
	}
	private void splitName()
	{
		try{
			Log.i("TTT","歌曲名字识别:"+name);
			MusicName = name.substring(name.lastIndexOf("/")+1,name.lastIndexOf("."));
			MusicExt = name.substring(name.lastIndexOf(".")+1,name.length());
			MusicName=java.net.URLDecoder.decode(MusicName,"UTF-8");
			Log.i("TTT","歌曲名字:"+MusicName);
		}
		catch(Exception e)
		{
			Log.i("TTT","歌曲名字识别出错");
		}

	}
	public String getMusicName()
	{
		return MusicName;
	}	
	public String getLrcLocalPath()
	{
		return localPath;
	}	
	public void setLrcLocalPath(String str)
	{
		 localPath=str;
	}
	
	
	
	public String getMusicFileName()
	{
		return MusicName+"."+MusicExt;
	}
	public String changeCharset(String str, String newCharset){
        if(str != null) {
            //用默认字符编码解码字符串。与系统相关，中文windows默认为GB2312
            byte[] bs = str.getBytes();
            try {
				return new String(bs, newCharset);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    //用新的字符编码生成字符串
        }
        return null;
    }
	
	public String getLyricUrl() {
		return lyricUrl;
	}
	public String getId() {
		return id;
	}
	public String getMusicUrl() {
		return name;
	}
	public String getAlbum() {
		return album;
	}
	public String getArtist() {
		return this.artist;
	}
	
	 public static String getEncoding(String str) {    
         String encode = "GB2312";    
        try {    
            if (str.equals(new String(str.getBytes(encode), encode))) {    
                 String s = encode;    
                return s;    
             }    
         } catch (Exception exception) {    
         }    
         encode = "ISO-8859-1";    
        try {    
            if (str.equals(new String(str.getBytes(encode), encode))) {    
                 String s1 = encode;    
                return s1;    
             }    
         } catch (Exception exception1) {    
         }    
         encode = "UTF-8";    
        try {    
            if (str.equals(new String(str.getBytes(encode), encode))) {    
                 String s2 = encode;    
                return s2;    
             }    
         } catch (Exception exception2) {    
         }    
         encode = "GBK";    
        try {    
            if (str.equals(new String(str.getBytes(encode), encode))) {    
                 String s3 = encode;    
                return s3;    
             }    
         } catch (Exception exception3) {    
         }    
        return "";    
     }    

	
	
}
