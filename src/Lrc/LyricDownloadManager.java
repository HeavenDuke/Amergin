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
 * �������
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
	 * ���ݸ������͸�����ȡ�øø��XML��Ϣ�ļ� ���ظ�ʱ���·��
	 */
	public String searchLyricFromWeb(String musicName, String singerName, String oldMusicName) {
		
		
		// ������������Ǻ��֣���ô��Ҫ���б���ת��
		try {
			musicName = URLEncoder.encode(musicName, UTF_8);
			singerName = URLEncoder.encode(singerName, UTF_8);
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
			
		}

		// �ٶ����ֺе�API
		String strUrl = "http://box.zhangmen.baidu.com/x?op=12&count=1&title="
				+ musicName + "$$" + singerName + "$$$$";
		System.out.println("URL��"+strUrl);
		// ����URL
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
			// ���ٶ����ֺеķ��ص����������ݸ��Զ����XML����������������ʵ�����ID
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

	/** ���ݸ������ID����ȡ�����ϵĸ���ı����� 
	 * @param musicInfo */
	private String fetchLyricContent(String musicName, String singerName, String oldMusicName) {
		if (mDownloadLyricId == -1) {
			Log.i("LRC","ʧ�ܣ�");
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

		// ��ȡ����ı��������ַ�������
		try {
			// ������������
			br = new BufferedReader(new InputStreamReader(mUrl.openStream(),
					UTF_8));
			if (br != null) {
				content = new StringBuilder();
				// ���л�ȡ����ı�
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
			// ��鱣���Ŀ¼�Ƿ��Ѿ�����

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

	/** ����ʱ��浽���أ�д������� */
	private void saveLyric(String content, String filePath) {
		// ���浽����
		File file = new File(filePath);
		try {
			OutputStream outstream = new FileOutputStream(file);
			OutputStreamWriter out = new OutputStreamWriter(outstream);
			out.write(content);
			out.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
			Log.i("Lrc","����ʧ��"+e.getMessage());
			
		}
	}
	
	//-------------------

	//���������ظ�ʲ��浽����
	public String downLyricContent(String LrcUrl,String artistName,String musicName) {
		BufferedReader br = null;
		StringBuilder content = null;
		String temp = null;
		Log.i("Lrc","ȡ�õĸ��·��"+LrcUrl);
		try {
			mUrl = new URL(LrcUrl);
		} catch (MalformedURLException e2) {
			e2.printStackTrace();
			Log.i("Lrc","URL����ʧ��");
		}
		// ��ȡ����ı��������ַ�������
		try {
			// ������������
			Log.i("Lrc","��������");
			br = new BufferedReader(new InputStreamReader(mUrl.openStream(),
					UTF_8));
			Log.i("Lrc","�����1");
			if (br != null) {
				
				
				content = new StringBuilder();
				// ���л�ȡ����ı�
				while ((temp = br.readLine()) != null) {
					content.append(temp+"\r\n");
					Log.i("Lrc",temp);
				}
				br.close();
			}
		} catch (IOException e1) {
			Log.i("Lrc","����ʧ��");
			e1.printStackTrace();
		}
		if (content != null) {
			String folderPath = MyApplication.getLrcLocalDirectoryPath();
			File savefolder = new File(folderPath);
			if (!savefolder.exists()) {
				savefolder.mkdirs();
			}
			String savePath = folderPath+"/"+artistName+"-"+musicName+".lrc";
			Log.i("Lrc","������"+savePath);
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
