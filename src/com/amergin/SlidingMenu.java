package com.amergin;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class SlidingMenu extends HorizontalScrollView
{
	/**
	 * 屏幕宽度
	 */
	private Drawable dr=null;
	
	private int mScreenWidth;
	public int current_view_index=1;
	public Handler hdl=null;
	/**
	 * dp
	 */
	private int mMenuRightPadding;
	public int getmMenuRightPadding() {
		return mMenuRightPadding;
	}

	public void setmMenuRightPadding(int mMenuRightPadding) {
		this.mMenuRightPadding = mMenuRightPadding;
	}
	public void setmMenuRightPadding2(int i) {
	}
	/**
	 * 菜单的宽度
	 */
	private int mMenuWidth;
	private int mHalfMenuWidth;

	private boolean isOpen;
	
	private boolean once;

	public SlidingMenu(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}
	public SlidingMenu(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		mScreenWidth = ScreenUtils.getScreenWidth(context);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.SlidingMenu, defStyle, 0);
		int n = a.getIndexCount();
		for (int i = 0; i < n; i++)
		{
			int attr = a.getIndex(i);
			switch (attr)
			{
			case R.styleable.SlidingMenu_rightPadding:
				// 默认50
				mMenuRightPadding = a.getDimensionPixelSize(attr,
						(int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 50f,
								getResources().getDisplayMetrics()));// 默认为10DP
				break;
			}
		}
		a.recycle();
	}

	public SlidingMenu(Context context)
	{
		this(context, null, 0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		/**
		 * 显示的设置一个宽度
		 */
		if (!once)
		{
			LinearLayout wrapper = (LinearLayout) getChildAt(0);
			ViewGroup menu = (ViewGroup) wrapper.getChildAt(0);
			ViewGroup content = (ViewGroup) wrapper.getChildAt(1);
			ViewGroup rightContent=(ViewGroup) wrapper.getChildAt(2);
			mMenuWidth = mScreenWidth - mMenuRightPadding;
			mHalfMenuWidth = mMenuWidth / 2;
			menu.getLayoutParams().width = mMenuWidth;
			content.getLayoutParams().width = mScreenWidth;
			rightContent.getLayoutParams().width=mScreenWidth;
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);
		
		Log.i("DEMO","参数:"+l+","+t+","+r+","+b);
		
		
		if (changed)
		{
			// 将菜单隐藏
			this.scrollTo(mMenuWidth, 0);
			once = true;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		int action = ev.getAction();
		int scrollX = getScrollX();
		switch (action)
		{
		// Up时，进行判断，如果显示区域大于菜单宽度一半则完全显示，否则隐藏
		case MotionEvent.ACTION_UP:
			if(scrollX<mHalfMenuWidth)//左边界面
			{
				ComeToLeft();
				this.performClick();
			}
			else if ( mHalfMenuWidth<=scrollX&& scrollX<=mScreenWidth/2+mMenuWidth)//中间
			{
				
				ComeToCenter();
			} 
			else
			{
				ComeToRight();
			}
			return true;
		case MotionEvent.ACTION_MOVE:
			//if(scrollX<=0)return true;
			Log.i("DMEO","scrollX:"+scrollX);
			
			
			if(dr==null)
			{
				dr=this.getResources().getDrawable(R.drawable.centent_filter_bg);
			}
			if(scrollX<mMenuWidth)//左
			{
				float prams=0.7f;
				prams=prams*(mMenuWidth-scrollX)/mMenuWidth;
				dr.setColorFilter(Color.BLACK,android.graphics.PorterDuff.Mode.MULTIPLY);
				MainActivity.centent_filter_bg.setImageDrawable(dr);
				MainActivity.centent_filter_bg.setAlpha(prams);
				MainActivity.centent_filter_bg.setVisibility(View.VISIBLE);
				
			}
			
			if(scrollX>mMenuWidth)//右
			{
				float prams=1.0f;
				prams=prams*(scrollX-mMenuWidth)/mScreenWidth;
				dr.setColorFilter(Color.BLACK,android.graphics.PorterDuff.Mode.MULTIPLY);
				MainActivity.centent_filter_bg.setImageDrawable(dr);
				MainActivity.centent_filter_bg.setAlpha(prams);
				MainActivity.centent_filter_bg.setVisibility(View.VISIBLE);
				
			}			
			break;
		}
		return super.onTouchEvent(ev);
	}


	public void ComeToCenter()
	{
		this.smoothScrollTo(mMenuWidth, 0);
		current_view_index=1;
		isOpen = false;
		Message msg=new Message();
		msg.what=11;
		MainActivity.handlerForBackground.sendMessage(msg);
	}
	
	public void ComeToLeft()
	{
		this.smoothScrollTo(0, 0);
		current_view_index=0;
		isOpen = false;
		Message msg=new Message();
		msg.what=12;
		MainActivity.handlerForBackground.sendMessage(msg);
	}
	
	

	public void ComeToRight()
	{
		if (isOpen)
			return;
		this.smoothScrollTo(mMenuWidth+mScreenWidth, 0);
		current_view_index=2;
		isOpen = true;
		sendChangeInfo();
	}

	

	
	//略有问题
	void sendChangeInfo()
	{
		Message msg=new Message();
		msg.what=10;
		MainActivity.handlerForBackground.sendMessage(msg);
		//Toast.makeText(this.getContext(), "切换了", Toast.LENGTH_SHORT).show();
		
	}


}
