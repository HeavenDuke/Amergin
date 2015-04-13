package com.amergin;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Song {
	//mid         -音乐编码
		//music  	  -音乐地址
		//name   	  -音乐名
		//album 	  -专辑名
		//artist 	  -艺术家
		//class   	  -音乐类型
		//lyric  	  -歌词地址
	private String mid;
	private String name;
	private String album;
	private String artist;
	private String music;
	private String mclass;
	private String localPath="";
	
	
	
	
	public Song(String mid, String name, String artist) {
		this.mid = mid;
		this.name = name;
		this.artist = artist;
		if(this.artist==null)this.artist="未知";
	}
	public String getMid() {
		return mid;
	}
	public String getName() {
		return name;
	}
	public String getLyric() {
		try {
			return MyApplication.QiNiuBaseLrcPath+URLEncoder.encode(name, "UTF-8").replace("+","%20")+".lrc";
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	public String getMusic() {
		return music;
	}
	public String getMusicUrl() {
		try {
			return MyApplication.QiNiuBaseMusicPath+URLEncoder.encode(name, "UTF-8").replace("+","%20")+".mp3";
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	public String getMclass() {
		return mclass;
	}
	public String getLocalPath() {
		return localPath;
	}

	
	public Song(String id, String name, String album, String artist,String lrc,String _music,String _mclass) {
		SetValue( id,  name,  album,  artist,lrc,_music,_mclass);
	}
	public void SetValue(String id, String name, String album, String artist,String lrc,String _music,String _mclass) {
		this.mid = id;
		try {
			this.name = java.net.URLDecoder.decode(name, "UTF-8");
			this.album = java.net.URLDecoder.decode(album, "UTF-8");
			this.artist = java.net.URLDecoder.decode(artist, "UTF-8");
			this.music =java.net.URLDecoder.decode(_music, "UTF-8");
			this.mclass =java.net.URLDecoder.decode(_mclass, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
//
//	private void splitName()
//	{
//		try{
//			Log.i("TTT","歌曲名字识别:"+name);
//			MusicName = name.substring(name.lastIndexOf("/")+1,name.lastIndexOf("."));
//			MusicExt = name.substring(name.lastIndexOf(".")+1,name.length());
//			MusicName=java.net.URLDecoder.decode(MusicName,"UTF-8");
//			Log.i("TTT","歌曲名字:"+MusicName);
//		}
//		catch(Exception e)
//		{
//			Log.i("TTT","歌曲名字识别出错");
//		}
//
//	}

	public String getLrcLocalPath()
	{
		return localPath;
	}	
	public void setLrcLocalPath(String str)
	{
		 localPath=str;
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
