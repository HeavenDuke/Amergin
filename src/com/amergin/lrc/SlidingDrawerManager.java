/**
 * Copyright (c) www.longdw.com
 */
package com.amergin.lrc;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ListView;

import com.amergin.lrc.LyricLoadHelper.LyricListener;
@SuppressLint("NewApi")
public class SlidingDrawerManager {
	private boolean isempty=true;
	private ListView mLrcListView;
	private LyricLoadHelper mLyricLoadHelper;//保留
	private LyricAdapter mLyricAdapter; //保留
	public SlidingDrawerManager(Context a, ListView view){
		mLyricLoadHelper = new LyricLoadHelper();
		mLrcListView = view;
		//不能拖动....如果想做更好，那就这里下功夫吧
		//mLrcListView.setEnabled(false);
		mLyricAdapter=new LyricAdapter(a);
		mLyricAdapter.setLyric(mLyricLoadHelper.getLyricSentences());
		mLyricLoadHelper.setLyricListener(mLyricListener);
		mLrcListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		mLrcListView.setAdapter(mLyricAdapter);
	}

	public void reloadLrc(String path)
	{
		reset();
		isempty=false;
		loadLyric(path);
		mLyricAdapter.setLyric(mLyricLoadHelper.getLyricSentences());
		mLrcListView.setAdapter(mLyricAdapter);
//		mLrcListView.smoothScrollToPositionFromTop(0,0, 300);
//		mLrcListView.setSelectionFromTop(0,0);
		
	}
	
	public void reset()
	{
		mLrcListView.setAdapter(null);
		isempty=true;
	}
	
	
	public void refreshUI(int curTime) {
		if(isempty)return;
		mLyricLoadHelper.notifyTime(curTime);

	}
	/**
	 * 读取本地歌词文件
	 */
	public void loadLyric(String path) {
		String lyricFilePath = path;
		File lyricfile = new File(lyricFilePath);
		if (lyricfile.exists()) {
			mLyricLoadHelper.loadLyric(lyricFilePath);
		}
	}
	private LyricListener mLyricListener = new LyricListener() {
		@Override
		public void onLyricLoaded(List<LyricSentence> lyricSentences, int index) {
			if (lyricSentences != null) {
				mLyricAdapter.setLyric(lyricSentences);
				mLyricAdapter.setCurrentSentenceIndex(index);
				mLyricAdapter.notifyDataSetChanged();
			}
		}
		@Override
		public void onLyricSentenceChanged(int indexOfCurSentence) {
			mLyricAdapter.setCurrentSentenceIndex(indexOfCurSentence);
			mLyricAdapter.notifyDataSetChanged();
			mLrcListView.smoothScrollToPositionFromTop(indexOfCurSentence,
					mLrcListView.getHeight()/2-10, 500);
	
		}
	};
//	public void toTop() {
//		// TODO Auto-generated method stub
//		mLrcListView.setSelectionFromTop(0,0);
//	}



}
