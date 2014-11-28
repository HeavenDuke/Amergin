package com.amergin;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Scroller;

public class PushSlider extends ViewGroup implements OnGestureListener{
	private static final String TAG = "PushSlider";
	private final boolean DEBUG = false;
	public static final int SLIDER_PAGE_LEFT = 0;
	public static final int SLIDER_PAGE_MIDDLE = 1;
	public static final int SLIDER_PAGE_RIGHT = 2;
	public static final int SLIDER_PAGE_FIX = SLIDER_PAGE_MIDDLE;
	private final int MOVE_FLAG_ALLOW_LEFT = 0x00000001;
	private final int MOVE_FLAG_ALLOW_RIGHT = 0x00000002;
	private final int FLING_DISTANCE = 80;
	private final int FLING_VELOCITY = 300;
	private final int SCROLL_DISTANCE = 4;
	private boolean mForbidden = false;
	private Context mContext;
	private View mChild[] = null;
	private int mChildWidth[] = null;
	private int focusedIndex = SLIDER_PAGE_MIDDLE;
	private GestureDetector mGDetector;
	private	int mMoveFlag = 0;
	private float mDensity = 1;
	private Scroller mScroller;
	private int mLeftPosX = 0;
	private int mRightPosX = 0;
	//private boolean mDragging = false;
//	private int mToBeMove = 0;
	private OnPageChangedListener mPageChangedListener = null;
	
	public interface OnPageChangedListener{
		//void onPageToBeChange(int oldHandle);
		void onPageChanged(View v, int whichHandle);
	}
	
	public void setOnPageChangedListener(OnPageChangedListener listener){
		mPageChangedListener = listener;
	}
	
	private void log(String info){
		if(DEBUG)
			Log.d(TAG, info);
	}
	
	
	//三个子页面的宽度
	public void setPageWidth(int index, int width){
		if(index == SLIDER_PAGE_FIX)
			return;
		mChildWidth[index]=width;
	}
	
	//public void setPageFocused(int index){
	//	focusedIndex = index;
	//}
	
	
	//得到当前获得焦点的子页面索引
	public int getFocusedPageId(){
		return focusedIndex;
	}
	
	public void pushForbid(boolean forbid){
		mForbidden = forbid;
	}
	
	public PushSlider(Context context){
		this(context, null);
	}
	
	public PushSlider(Context context, AttributeSet attrs) {
		super(context, attrs);
		setHapticFeedbackEnabled(false);
		mContext = context;
		mForbidden = false;
		mGDetector = new GestureDetector(mContext,this);
		focusedIndex = SLIDER_PAGE_MIDDLE;
		mMoveFlag = MOVE_FLAG_ALLOW_LEFT|MOVE_FLAG_ALLOW_RIGHT;
		mDensity = getResources().getDisplayMetrics().density;
		mScroller = new Scroller(mContext, new AccelerateInterpolator());
		mChildWidth = new int[3];
		//mToBeMove = 0;
		
	}
	
	private boolean isMoveAllow(int flag){
		boolean ret = ((mMoveFlag & flag) == flag);
		return ret;
	}
	
	public void moveToPageById(int index){
		if(focusedIndex == index)
			return;
		
		if(focusedIndex == SLIDER_PAGE_LEFT){
			if((index == SLIDER_PAGE_MIDDLE) && isMoveAllow(MOVE_FLAG_ALLOW_LEFT)){
				flingTo(MOVE_FLAG_ALLOW_LEFT,true);
			}else if((index == SLIDER_PAGE_RIGHT) && isMoveAllow(MOVE_FLAG_ALLOW_LEFT)){
				flingTo(MOVE_FLAG_ALLOW_LEFT,true);
				if(isMoveAllow(MOVE_FLAG_ALLOW_LEFT)){
					flingTo(MOVE_FLAG_ALLOW_LEFT,true);
				}
			}
		}else if(focusedIndex == SLIDER_PAGE_MIDDLE){
			if((index == SLIDER_PAGE_LEFT) && isMoveAllow(MOVE_FLAG_ALLOW_RIGHT)){
				flingTo(MOVE_FLAG_ALLOW_RIGHT,true);
			}else if((index == SLIDER_PAGE_RIGHT) && isMoveAllow(MOVE_FLAG_ALLOW_LEFT)){
				flingTo(MOVE_FLAG_ALLOW_LEFT,true);
			}
		}else if(focusedIndex == SLIDER_PAGE_RIGHT){
			if((index == SLIDER_PAGE_MIDDLE) && isMoveAllow(MOVE_FLAG_ALLOW_RIGHT)){
				flingTo(MOVE_FLAG_ALLOW_RIGHT,true);
			}else if((index == SLIDER_PAGE_LEFT) && isMoveAllow(MOVE_FLAG_ALLOW_RIGHT)){
				flingTo(MOVE_FLAG_ALLOW_RIGHT,true);
				if(isMoveAllow(MOVE_FLAG_ALLOW_RIGHT)){
					flingTo(MOVE_FLAG_ALLOW_RIGHT,true);
				}
			}
		}
	}
	
	private void changeFocus(){
		mMoveFlag = 0;
		if((focusedIndex == SLIDER_PAGE_LEFT) && (mChild[SLIDER_PAGE_MIDDLE]!=null)){
			mMoveFlag = MOVE_FLAG_ALLOW_LEFT;	
		}else if(focusedIndex == SLIDER_PAGE_MIDDLE){
			if(mChild[SLIDER_PAGE_LEFT]!=null){
				mMoveFlag = MOVE_FLAG_ALLOW_RIGHT;
			}
			if(mChild[SLIDER_PAGE_RIGHT]!=null){
				mMoveFlag |= MOVE_FLAG_ALLOW_LEFT;
			}
		}else if((focusedIndex == SLIDER_PAGE_RIGHT) && (mChild[SLIDER_PAGE_MIDDLE] != null)){
			mMoveFlag = MOVE_FLAG_ALLOW_RIGHT;
		}
		if(mPageChangedListener != null){
			mPageChangedListener.onPageChanged(mChild[focusedIndex],focusedIndex);
		}
	}
	
	private void flingTo(int direct,boolean fast){
		
		if(mForbidden)
			return;
		
		int startX = getScrollX();		
		int endX=0;
		int dx;
		
		if(direct == MOVE_FLAG_ALLOW_LEFT){
			//if(mPageChangedListener != null){
			//	mPageChangedListener.onPageToBeChange(focusedIndex);
			//}
			focusedIndex ++;
			
			if(focusedIndex == SLIDER_PAGE_MIDDLE)
				endX = 0;
			else if(focusedIndex == SLIDER_PAGE_RIGHT)
				endX = mRightPosX;
			
			dx = endX - startX;
			mScroller.startScroll(startX, 0, dx, 0,(fast?100:250));
			
		}else{
			//if(mPageChangedListener != null){
			//	mPageChangedListener.onPageToBeChange(focusedIndex);
			//}
			focusedIndex --;
			if(focusedIndex == SLIDER_PAGE_MIDDLE)
				endX = 0;
			else if(focusedIndex == SLIDER_PAGE_LEFT)
				endX = mLeftPosX;
			
			dx = endX - startX;
			mScroller.startScroll(startX, 0, dx, 0,(fast?100:250));
		}
		log("flingTo " + startX);
		invalidate();
		changeFocus();
	}
	
	@Override
	public void computeScroll(){
		if(mScroller.computeScrollOffset()){
			int curX = mScroller.getCurrX();
			scrollTo(curX,0);
			postInvalidate();
		}
	}
	
	public boolean isPageMoving(){
		boolean b = mScroller.isFinished();
		return (b==false);
	}
	
	public void reIniti()
	{
		mChild[SLIDER_PAGE_LEFT] = getChildAt(SLIDER_PAGE_LEFT);
		mChild[SLIDER_PAGE_LEFT].setVisibility(View.VISIBLE);
	}
	private void initChildren(int width, int height){
		if(mChild == null){
			mChild = new View[3];
			mChildWidth[SLIDER_PAGE_FIX]=width;
		}
		
		for(int i=SLIDER_PAGE_LEFT; i<=SLIDER_PAGE_RIGHT; i++){
			mChild[i] = getChildAt(i);
			if(mChild[i] != null){
				mChild[i].setVisibility(View.VISIBLE);
				int childWidth = mChildWidth[i];
				if(childWidth > width){
					childWidth = width;
					mChildWidth[i] = width;
				}
				int widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
				int heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
				mChild[i].measure(widthMeasureSpec, heightMeasureSpec);
			}
		}
		
		changeFocus();
		
	}
	
	//@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		//int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		
		//int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		if(mChild == null){
			initChildren(widthSize,heightSize);
		}
		else{
			int newSpec = MeasureSpec.makeMeasureSpec(mChildWidth[focusedIndex], MeasureSpec.EXACTLY);
			
			mChild[focusedIndex].measure(newSpec, heightMeasureSpec);
		}
		
	}  
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		//if(mChild == null){
		//	initChildren(); 
		//}
		if(changed){

			if(mChild[focusedIndex] != null){
				int w = mChildWidth[focusedIndex];
				if(w == 0){
					w = r-l;
				}
				mChild[focusedIndex].layout(l,t,l+w,b);
			}
			
			for(int next=SLIDER_PAGE_LEFT; next<=SLIDER_PAGE_RIGHT; next++){
				if(next == focusedIndex){
					continue;
				}
				if(mChild[next] != null){
					int w1 = mChildWidth[next];
					int x = 0;
					if((next== SLIDER_PAGE_LEFT)&&(focusedIndex == SLIDER_PAGE_MIDDLE)){
						x = l- w1;
					}else if((next== SLIDER_PAGE_LEFT)&&(focusedIndex == SLIDER_PAGE_RIGHT)){
						x = l- mChildWidth[SLIDER_PAGE_MIDDLE] - w1;
					}else if((next== SLIDER_PAGE_MIDDLE)&&(focusedIndex == SLIDER_PAGE_LEFT)){
						x = l+ mChildWidth[SLIDER_PAGE_LEFT];
					}else if((next== SLIDER_PAGE_MIDDLE)&&(focusedIndex == SLIDER_PAGE_RIGHT)){
						x = l-w1;
					}else if((next== SLIDER_PAGE_RIGHT)&&(focusedIndex == SLIDER_PAGE_MIDDLE)){
						x = l+mChildWidth[SLIDER_PAGE_MIDDLE];
					}
					else if((next== SLIDER_PAGE_RIGHT)&&(focusedIndex == SLIDER_PAGE_LEFT)){
						x = l+mChildWidth[SLIDER_PAGE_LEFT]+mChildWidth[SLIDER_PAGE_MIDDLE];
					}
					mChild[next].layout(x,t,x+w1,b);
				}
			}
			
			mLeftPosX = -mChildWidth[SLIDER_PAGE_LEFT];
			if(mChild[SLIDER_PAGE_RIGHT] != null)
				mRightPosX = mChildWidth[SLIDER_PAGE_MIDDLE];
			else
				mRightPosX = 0;
		}else{
			/*onMeasure 锟?onLayout 锟?锟斤拷锟?鍋氫竴涓媍hild view锟?measure 锟?layout锛屽惁鍒欎細瀵艰嚧listview涓嶅埛*/
			if(mChild[focusedIndex] != null){
				int w = mChildWidth[focusedIndex];
				mChild[focusedIndex].layout(l,t,l+w,b);
			}
		}
		
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev){
		int action = ev.getAction();
		boolean gdHandled = mGDetector.onTouchEvent(ev);
		if((gdHandled) && (action == MotionEvent.ACTION_UP)){
			log("[dispatchTouchEvent] action = " + action + "return true");
			//mDragging = false;
			return true;
		}
		
		log("[dispatchTouchEvent] action = " + action + "return false");
		
		return super.dispatchTouchEvent(ev);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev){
		int action = ev.getAction();
		boolean gdHandled = mGDetector.onTouchEvent(ev);
		if((gdHandled) && (action == MotionEvent.ACTION_UP)){
			log("[onInterceptTouchEvent] action = " + action + "return true");
			return true;
		}
		log("[onInterceptTouchEvent] action = " + action + "return false");
		return super.onInterceptTouchEvent(ev);
	}
	
	//@Override
	public boolean onTouchEvent(MotionEvent ev){
		int action = ev.getAction();
		
		//boolean gdHandled = mGDetector.onTouchEvent(ev);
		//boolean ret = super.onTouchEvent(ev);
		//log("[onTouchEvent] action = " + action + ";ret = " + ret);
		return true;
	}
	
	@Override
	public boolean onDown(MotionEvent ev) {
		int pos = getScrollX();
		log("[onDown] true "+pos);
		
		return true;
	}
	
	@Override
	public boolean onFling(MotionEvent eDown, MotionEvent eMove, float velocityX,
			float velocityY) {
		log("onFling " + eDown.getX() + "," + eMove.getX() +"," + velocityX);
		//<<<
		if((eDown.getX() - eMove.getX() > FLING_DISTANCE*mDensity) && (Math.abs(velocityX)> FLING_VELOCITY)){
			if(isMoveAllow(MOVE_FLAG_ALLOW_LEFT)/* && (mToBeMove & MOVE_FLAG_ALLOW_LEFT)==MOVE_FLAG_ALLOW_LEFT*/){
				flingTo(MOVE_FLAG_ALLOW_LEFT,false);
				log("[onFling] true");
				return true;
			}
		}
		
		//>>>
		if((eMove.getX() - eDown.getX() > FLING_DISTANCE*mDensity) && (Math.abs(velocityX)> FLING_VELOCITY)){
			if(isMoveAllow(MOVE_FLAG_ALLOW_RIGHT)/* && (mToBeMove & MOVE_FLAG_ALLOW_RIGHT)==MOVE_FLAG_ALLOW_RIGHT*/){
				flingTo(MOVE_FLAG_ALLOW_RIGHT,false);
				log("[onFling] true");
				return true;
			}
		}
		log("[onFling] false");
		return false;
	}
	
	@Override
	public void onLongPress(MotionEvent arg0) {}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		
		
		/*if((Math.abs(distanceX)> SCROLL_DISTANCE*mDensity)){
			int pos = getScrollX();

			if((pos + distanceX > mLeftPosX ) && (pos + distanceX < mRightPosX)){
				scrollBy((int) distanceX, 0);
				log("[onScroll] distanceX = "+ distanceX+ " true");
				mDragging = true;
				return true;
			}
		}*/
		log("[onScroll] false");
		return false;
	}
	
	@Override
	public void onShowPress(MotionEvent arg0) {}
	
	@Override 
	public boolean onSingleTapUp(MotionEvent ev) {
		Rect focusRect = new Rect();
		mChild[focusedIndex].getHitRect(focusRect);
		float x = ev.getX();
		int left = focusRect.left-mScroller.getCurrX();
		int right = focusRect.right-mScroller.getCurrX();
		
		if((x < left) && isMoveAllow(MOVE_FLAG_ALLOW_RIGHT)){
			flingTo(MOVE_FLAG_ALLOW_RIGHT,true);
			log("[onSingleTapUp] true");
			return true;
		}else if((x > right) && isMoveAllow(MOVE_FLAG_ALLOW_LEFT)){
			flingTo(MOVE_FLAG_ALLOW_LEFT,true);
			log("[onSingleTapUp] true");
			return true;
		}
		log("[onSingleTapUp] false");
		return false;
	}

	
}
