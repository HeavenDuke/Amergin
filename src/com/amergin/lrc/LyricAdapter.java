/**
 * Copyright (c) www.longdw.com
 */
package com.amergin.lrc;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.amergin.R;
public class LyricAdapter extends BaseAdapter {

	/** 歌词句子集合 */
	List<LyricSentence> mLyricSentences = null;

	Context mContext = null;

	/** 当前的句子索引号 */
	int mIndexOfCurrentSentence = 0;

	float mCurrentSize = 20;
	float mNotCurrentSize = 17;

	public LyricAdapter(Context context) {
		mContext = context;
		mLyricSentences = new ArrayList<LyricSentence>();
		mIndexOfCurrentSentence = 0;
		
	}

	/** 设置歌词，由外部调用， */
	public void setLyric(List<LyricSentence> lyric) {
		mLyricSentences.clear();
		if (lyric != null) {
			mLyricSentences.addAll(lyric);
			Log.i("KKK", "歌词句子数目=" + mLyricSentences.size());
		}
		mIndexOfCurrentSentence = 0;
	}

	@Override
	public boolean isEmpty() {
		// 歌词为空时，让ListView显示EmptyView
		if (mLyricSentences == null) {
			Log.i("KKK", "isEmpty:null");
			return true;
		} else if (mLyricSentences.size() == 0) {
			Log.i("KKK", "isEmpty:size=0");
			return true;
		} else {
			Log.i("KKK", "isEmpty:not empty");
			return false;
		}
	}

	@Override
	public boolean isEnabled(int position) {
		// 禁止在列表条目上点击
		return false;
	}

	@Override
	public int getCount() {
		return mLyricSentences.size();
	}

	@Override
	public Object getItem(int position) {
		return mLyricSentences.get(position).getContentText();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.lyric_line, null);
			holder.lyric_line = (StrokeTextView) convertView
					.findViewById(R.id.lyric_line_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position >= 0 && position < mLyricSentences.size()) {
			holder.lyric_line.setText(mLyricSentences.get(position)
					.getContentText());
		}
		if (mIndexOfCurrentSentence == position) {
			// 当前播放到的句子设置为白色，字体大小更
			holder.lyric_line.setTextColor(Color.parseColor("#a12dfa"));
			holder.lyric_line.setTextSize(mCurrentSize);
			holder.lyric_line.isSelected=true;
		} else {
			// 其他的句子设置为暗色，字体大小较小
			holder.lyric_line.setTextColor(mContext.getResources().getColor(
					R.color.black));
			holder.lyric_line.setTextSize(mNotCurrentSize);
			holder.lyric_line.isSelected=false;
		}
		return convertView;
	}

	public void setCurrentSentenceIndex(int index) {
		mIndexOfCurrentSentence = index;
	}

	static class ViewHolder {
		StrokeTextView lyric_line;
	}
}
