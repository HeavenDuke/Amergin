package com.amergin.lrc;


import android.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;
/*
 * StrokeTextView的目标是给文字描边
 * 实现方法是两个TextView叠加,只有描边的TextView为底,实体TextView叠加在上面
 * 看上去文字就有个不同颜色的边框了
 */
public class StrokeTextView extends TextView {
 
	public boolean isSelected=false;
	public boolean isLrcLine=true;
	@SuppressLint("NewApi")
	private void copyAttr()
	{
		borderText.setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
		borderText.setGravity(getGravity());
		borderText.setHeight(getHeight());
		borderText.setWidth(getWidth());

		if(isLrcLine){
			borderText.setTextAppearance(getContext(),R.attr.textAppearanceMedium);
		}else
		{
			this.setFocusable(true);
			borderText.setFocusable(true);
			this.setEllipsize(TruncateAt.MARQUEE);
			borderText.setEllipsize(TruncateAt.MARQUEE);
			this.setMarqueeRepeatLimit(6);
			
			borderText.setMarqueeRepeatLimit(6);
			
			
		}
			
	}
	

	private TextView borderText = null;///用于描边的TextView
 
    public StrokeTextView(Context context) {
        super(context);
        borderText = new TextView(context);
 
    }
 
    
    public void setBorderColor(Color color)
    {
    	
    	
    }
    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        borderText = new TextView(context,attrs);
      
    }
 
    public StrokeTextView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        borderText = new TextView(context,attrs,defStyle);
       
    }
 
    public void init(){
        TextPaint tp1 = borderText.getPaint(); 
        tp1.setStrokeWidth(2);                        //设置描边宽度
        tp1.setStyle(Style.STROKE);                             //对文字只描边
        borderText.setTextColor(Color.WHITE);  //设置描边颜色
    }
    public void noinit(){
	    TextPaint tp1 = borderText.getPaint(); 
        tp1.setStrokeWidth(1);                               //设置描边宽度
        tp1.setStyle(Style.STROKE);                             //对文字只描边
        borderText.setTextColor(Color.WHITE);  //设置描边颜色
    }
 
    
    @Override
	public void setTextSize(float size) {
		// TODO Auto-generated method stub
    	borderText.setTextSize(size);
		super.setTextSize(size);
	}

	@Override
    public void setLayoutParams (ViewGroup.LayoutParams params){
        super.setLayoutParams(params);
        borderText.setLayoutParams(params);
    }
 
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CharSequence tt = borderText.getText();
         
        //两个TextView上的文字必须一致
        if(tt== null || !tt.equals(this.getText())){
            borderText.setText(getText());
            this.postInvalidate();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        borderText.measure(widthMeasureSpec, heightMeasureSpec);
    }
 
    protected void onLayout (boolean changed, int left, int top, int right, int bottom){
        super.onLayout(changed, left, top, right, bottom);
        borderText.layout(left, top, right, bottom);
    }
 
    @Override
    protected void onDraw(Canvas canvas) {
    	  CharSequence tt = borderText.getText();
          //两个TextView上的文字必须一致
          if(tt== null || !tt.equals(this.getText())){
              borderText.setText(getText());
              this.postInvalidate();
          }
    	copyAttr();
    	if(isSelected)
    	{
    		this.init();
    	}else
    	{
    		this.noinit();
    	}
        borderText.draw(canvas);
        super.onDraw(canvas);
    }
 
}