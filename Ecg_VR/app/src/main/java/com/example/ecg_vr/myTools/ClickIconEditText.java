package com.example.ecg_vr.myTools;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Range;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatEditText;

import com.example.ecg_vr.R;

/**
 * Created by jingzz on 2020/3/04.
 */
public class ClickIconEditText extends AppCompatEditText {

    public static final int CLICK_LEFT_ICON = 0;
    public static final int CLICK_RIGHT_ICON = 2;
    public static final int CLICK_TOP_ICON = 1;
    public static final int CLICK_BOTTOM_ICON = 3;

    public ClickIconEditText(Context context) {
        this(context,null);
    }

    public ClickIconEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public ClickIconEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP)
            return super.onTouchEvent(event);

        if(onIconClickListener ==null)
            return super.onTouchEvent(event);

        Drawable[] drawables = getCompoundDrawables();
        //0左边图片 1 上边图片 2右边图片 3 下边图片
        Drawable dl = drawables[0];
        Drawable dt = drawables[1];
        Drawable dr = drawables[2];
        Drawable db = drawables[3];

        int pt = getPaddingTop()+(dt==null?0:dt.getIntrinsicHeight());
        int pb = getPaddingBottom()+(db== null?0:db.getIntrinsicHeight());
        int pl = getPaddingLeft()+(dl==null?0:dl.getIntrinsicWidth());
        int pr = getPaddingRight()+(dr==null?0:dr.getIntrinsicWidth());

        float x = event.getX();
        float y = event.getY();

        int mx = (getWidth()-pl-pr)/2+pl;
        int my = (getHeight()-pt-pb)/2+pt;

        if(dl != null){
            int dlw = dl.getIntrinsicWidth();
            int dlh = dl.getIntrinsicHeight();
            Range<Integer> rlx = new Range<>(getPaddingLeft()-getCompoundDrawablePadding(),getPaddingLeft()+dlw+getCompoundDrawablePadding());
            Range<Integer> rly = new Range<>(my-dlh/2-getCompoundDrawablePadding(),my+dlh/2+dlw+getCompoundDrawablePadding());
            if(rlx.contains((int) x)&&rly.contains((int) y)){
                Log.d(" ","点击了左边图片");
                onIconClickListener.onIconClickListener(this,CLICK_LEFT_ICON);
                return true;
            }
        }
        if(dr != null){
            int drw = dr.getIntrinsicWidth();
            int drh = dr.getIntrinsicHeight();
            Range<Integer> rrx = new Range<>(getWidth()-getPaddingRight()-drw/2-getCompoundDrawablePadding(),getWidth()+getPaddingRight()+drw/2-getCompoundDrawablePadding());
            Range<Integer> rry = new Range<>(my-drh/2-getCompoundDrawablePadding(),my+drh/2+drw+getCompoundDrawablePadding());
            if(rrx.contains((int) x)&&rry.contains((int) y)){
                Log.e("","点击了右边图片");
                onIconClickListener.onIconClickListener(this,CLICK_RIGHT_ICON);
                return true;
            }
        }
        if(dt != null){
            int dtw = dt.getIntrinsicWidth();
            int dth = dt.getIntrinsicHeight();
            Range<Integer> rtx = new Range<>(mx-dtw/2-getCompoundDrawablePadding(),mx+dtw/2+getCompoundDrawablePadding());
            Range<Integer> rty = new Range<>(getPaddingTop()-getCompoundDrawablePadding(),pt+getCompoundDrawablePadding());
            if(rtx.contains((int) x)&&rty.contains((int) y)){
                Log.e("","点击了上边图片");
                onIconClickListener.onIconClickListener(this,CLICK_TOP_ICON);
                return true;
            }
        }
        if(db != null){
            int dbw = db.getIntrinsicWidth();
            Range<Integer> rbx = new Range<>(mx-dbw/2-getCompoundDrawablePadding(),mx+dbw/2+getCompoundDrawablePadding());
            Range<Integer> rby = new Range<>(getHeight()-pb-getCompoundDrawablePadding(),getHeight()-getPaddingBottom()-getCompoundDrawablePadding());
            if(rbx.contains((int) x)&&rby.contains((int) y)){
                Log.e("","点击了下边图片");
                onIconClickListener.onIconClickListener(this,CLICK_BOTTOM_ICON);
                return true;
            }
        }

        return super.onTouchEvent(event);
    }

    private OnIconClickListener onIconClickListener;

    public void setOnIconClickListener(OnIconClickListener onIconClickListener) {
        this.onIconClickListener = onIconClickListener;
    }

    public interface OnIconClickListener{
        /**
         * @param view 被点击的view
         * @param iconType 点击的图标位置 0：左边 1:上边 2:右边 3:下边
         */
        void onIconClickListener(ClickIconEditText view,int iconType);
    }

}
