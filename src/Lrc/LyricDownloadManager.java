package Lrc;
/**
 * Copyright (c) www.longdw.com
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import android.util.Log;

import com.amergin.MyApplication;


/**
 * 歌词下载
 * @author longdw(longdawei1988@gmail.com)
 *
 */
public class LyricDownloadManager {
	public static final String GB2312 = "GB2312";
	public static final String UTF_8 = "utf-8";
	private final int mTimeOut = 10 * 1000;
	private URL mUrl = null;
	private int mDownloadLyricId = -1;
	private LyricXMLParser mLyricXMLParser=new LyricXMLParser();



	/*
	 * 根据歌曲名和歌手名取得该歌的XML信息文件 返回歌词保存路径
	 */
	public String searchLyricFromWeb(String musicName, String singerName, String oldMusicName) {
		
		
		// 传进来的如果是汉字，那么就要进行编码转化
		try {
			musicName = URLEncoder.encode(musicName, UTF_8);
			singerName = URLEncoder.encode(singerName, UTF_8);
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
			
		}

		// 百度音乐盒的API
		String strUrl = "http://box.zhangmen.baidu.com/x?op=12&count=1&title="
				+ musicName + "$$" + singerName + "$$$$";
		System.out.println("URL："+strUrl);
		// 生成URL
		try {
			mUrl = new URL(strUrl);
		} catch (Exception e1) {
			e1.printStackTrace();
			
		}

		try {
			HttpURLConnection httpConn = (HttpURLConnection) mUrl
					.openConnection();
			httpConn.setReadTimeout(mTimeOut);
			if (httpConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return null;
			}
			httpConn.connect();
			// 将百度音乐盒的返回的输入流传递给自定义的XML解析器，解析出歌词的下载ID
			mDownloadLyricId = mLyricXMLParser.parseLyricId(httpConn
					.getInputStream());
			System.out.println(mDownloadLyricId+"iid");
			httpConn.disconnect();
			
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return fetchLyricContent(musicName, singerName, oldMusicName);
	}

	/** 根据歌词下载ID，获取网络上的歌词文本内容 
	 * @param musicInfo */
	private String fetchLyricContent(String musicName, String singerName, String oldMusicName) {
		if (mDownloadLyricId == -1) {
			Log.i("LRC","失败？");
			return null;
		}
		BufferedReader br = null;
		StringBuilder content = null;
		String temp = null;
		String lyricURL = "http://box.zhangmen.baidu.com/bdlrc/"
				+ mDownloadLyricId / 100 + "/" + mDownloadLyricId + ".lrc";

		try {
			mUrl = new URL(lyricURL);
		} catch (MalformedURLException e2) {
			e2.printStackTrace();
		}

		// 获取歌词文本，存在字符串类中
		try {
			// 建立网络连接
			br = new BufferedReader(new InputStreamReader(mUrl.openStream(),
					UTF_8));
			if (br != null) {
				content = new StringBuilder();
				// 逐行获取歌词文本
				while ((temp = br.readLine()) != null) {
					content.append(temp+"\r\n");
					Log.i("Lrc",temp);
				}
				br.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			musicName = URLDecoder.decode(musicName, UTF_8);
			singerName = URLDecoder.decode(singerName, UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (content != null) {
			// 检查保存的目录是否已经创建

//			String folderPath = PreferenceManager.getDefaultSharedPreferences(
//					mContext).getString(SettingFragment.KEY_LYRIC_SAVE_PATH,
//					Constant.LYRIC_SAVE_FOLDER_PATH);
			String folderPath = MyApplication.getLrcLocalDirectoryPath();
			File savefolder = new File(folderPath);
			if (!savefolder.exists()) {
				savefolder.mkdirs();
			}
			String savePath = folderPath + File.separator + oldMusicName
					+ ".lrc";
//			String savePath = folderPath + File.separator + musicName + ".lrc";

			saveLyric(content.toString(), savePath);

			return savePath;
		} else {
			return null;
		}

	}

	/** 将歌词保存到本地，写入外存中 */
	private void saveLyric(String content, String filePath) {
		// 保存到本地
		File file = new File(filePath);
		try {
			OutputStream outstream = new FileOutputStream(file);
			OutputStreamWriter out = new OutputStreamWriter(outstream);
			out.write(content);
			out.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
			Log.i("Lrc","保存失败"+e.getMessage());
			
		}
	}
	
	//-------------------

	//从网上下载歌词并存到本地
	public String downLyricContent(String LrcUrl,String artistName,String musicName) {
		BufferedReader br = null;
		StringBuilder content = null;
		String temp = null;
		Log.i("Lrc","取得的歌词路径"+LrcUrl);
		try {
			mUrl = new URL(LrcUrl);
		} catch (MalformedURLException e2) {
			e2.printStackTrace();
			Log.i("Lrc","URL构建失败");
		}
		// 获取歌词文本，存在字符串类中
		try {
			// 建立网络连接
			Log.i("Lrc","网络连接");
			br = new BufferedReader(new InputStreamReader(mUrl.openStream(),
					UTF_8));
			Log.i("Lrc","读歌词1");
			if (br != null) {
				
				
				content = new StringBuilder();
				// 逐行获取歌词文本
				while ((temp = br.readLine()) != null) {
					content.append(temp+"\r\n");
					Log.i("Lrc",temp);
				}
				br.close();
			}
		} catch (IOException e1) {
			Log.i("Lrc","连接失败");
			e1.printStackTrace();
		}
		if (content != null) {
			String folderPath = MyApplication.getLrcLocalDirectoryPath();
			File savefolder = new File(folderPath);
			if (!savefolder.exists()) {
				savefolder.mkdirs();
			}
			String savePath = folderPath+"/"+artistName+"-"+musicName+".lrc";
			Log.i("Lrc","保存歌词"+savePath);
			saveLyric(content.toString(), savePath);
			return savePath;
		} else {
			return null;
		}

	}

	public boolean checkLocalLrc(String artistName,String musicName)
	{
		String savePath = MyApplication.getLrcLocalDirectoryPath()+"/"+artistName+"-"+musicName+".lrc";
		File f=new File(savePath);
		return f.exists();
	}
	
}
