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

	/** ��ʾ��Ӽ��� */
	List<LyricSentence> mLyricSentences = null;

	Context mContext = null;

	/** ��ǰ�ľ��������� */
	int mIndexOfCurrentSentence = 0;

	float mCurrentSize = 20;
	float mNotCurrentSize = 17;

	public LyricAdapter(Context context) {
		mContext = context;
		mLyricSentences = new ArrayList<LyricSentence>();
		mIndexOfCurrentSentence = 0;
		
	}

	/** ���ø�ʣ����ⲿ���ã� */
	public void setLyric(List<LyricSentence> lyric) {
		mLyricSentences.clear();
		if (lyric != null) {
			mLyricSentences.addAll(lyric);
			Log.i("KKK", "��ʾ�����Ŀ=" + mLyricSentences.size());
		}
		mIndexOfCurrentSentence = 0;
	}

	@Override
	public boolean isEmpty() {
		// ���Ϊ��ʱ����ListView��ʾEmptyView
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
		// ��ֹ���б���Ŀ�ϵ��
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
			// ��ǰ���ŵ��ľ�������Ϊ��ɫ�������С��
			holder.lyric_line.setTextColor(Color.parseColor("#a12dfa"));
			holder.lyric_line.setTextSize(mCurrentSize);
			holder.lyric_line.isSelected=true;
		} else {
			// �����ľ�������Ϊ��ɫ�������С��С
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