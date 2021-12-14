package in.arjsna.swipecardlib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.FrameLayout;

public class SwipeCardView extends BaseFlingAdapterView {

//#region properties


    private static final double SCALE_OFFSET = 0.04;

    private static final float TRANS_OFFSET = 45;
    private static final String TAG = "SwipeCardView";

    protected boolean DETECT_BOTTOM_SWIPE;

    protected boolean DETECT_TOP_SWIPE;

    protected boolean DETECT_RIGHT_SWIPE;

    protected boolean DETECT_LEFT_SWIPE;

    private float CURRENT_TRANSY_VAL = 0;

    private float CURRENT_SCALE_VAL = 0;

    private int INITIAL_MAX_VISIBLE = 3;

    private int MAX_VISIBLE = 3;

    private int MIN_ADAPTER_STACK = 6;

    private float ROTATION_DEGREES = 15.f;

    private int currentAdapterCount = 0;

    private Adapter mAdapter;

    private int LAST_OBJECT_IN_STACK = 0;

    private OnCardFlingListener mFlingListener;

    private AdapterDataSetObserver mDataSetObserver;

    private boolean mInLayout = false;

    private View mActiveCard = null;

    private OnItemClickListener mOnItemClickListener;

    private FlingCardListener flingCardListener;

    private PointF mLastTouchPoint;

    private int START_STACK_FROM = 0;

    private int adapterCount = 0;
//#endregion properties

    public SwipeCardView(Context context) {
        this(context, null);
        Utils.entry_log();
    }

    public SwipeCardView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.SwipeFlingStyle);
        Utils.entry_log();
    }

    public SwipeCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Utils.entry_log();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeCardView, defStyle, 0);
        MAX_VISIBLE = a.getInt(R.styleable.SwipeCardView_max_visible, MAX_VISIBLE);
        MIN_ADAPTER_STACK = a.getInt(R.styleable.SwipeCardView_min_adapter_stack, MIN_ADAPTER_STACK);
        ROTATION_DEGREES = a.getFloat(R.styleable.SwipeCardView_rotation_degrees, ROTATION_DEGREES);
        DETECT_LEFT_SWIPE = a.getBoolean(R.styleable.SwipeCardView_left_swipe_detect, true);
        DETECT_RIGHT_SWIPE = a.getBoolean(R.styleable.SwipeCardView_right_swipe_detect, true);
        DETECT_BOTTOM_SWIPE = a.getBoolean(R.styleable.SwipeCardView_bottom_swipe_detect, true);
        DETECT_TOP_SWIPE = a.getBoolean(R.styleable.SwipeCardView_top_swipe_detect, true);
        INITIAL_MAX_VISIBLE = MAX_VISIBLE;
        a.recycle();
    }


    /**
     * A shortcut method to set both the listeners and the adapter.
     *
     * @param context The activity context which extends OnCardFlingListener, OnItemClickListener or both
     * @param mAdapter The adapter you have to set.
     */
    public void init(final Context context, Adapter mAdapter) {
        Utils.entry_log();
        if(context instanceof OnCardFlingListener) {
            Utils.entry_log();
            mFlingListener = (OnCardFlingListener) context;
        }else{
            throw new RuntimeException("Activity does not implement SwipeFlingAdapterView.OnCardFlingListener");
        }
        if(context instanceof OnItemClickListener){
            Utils.entry_log();
            mOnItemClickListener = (OnItemClickListener) context;
        }
        setAdapter(mAdapter);
    }

 	@Override
    public View getSelectedView() {
        Utils.entry_log();
        return mActiveCard;
    }

    public int getCurrentPosition(){
        Utils.entry_log();
        return START_STACK_FROM;
    }

    public Object getCurrentItem(){
        Utils.entry_log();
        return mAdapter.getItem(START_STACK_FROM);
    }

    @Override
    public void requestLayout() {
        Utils.entry_log();
        if (!mInLayout) {
            Utils.entry_log();
            super.requestLayout();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Utils.entry_log();
        super.onLayout(changed, left, top, right, bottom);
        // if we don't have an adapter, we don't need to do anything
        if (mAdapter == null) {
            Utils.entry_log();
            return;
        }


        mInLayout = true;

        if (adapterCount == 0) {
            Utils.entry_log();
            Log.d(TAG, "onLayout: removeAllViewsInLayout 0");

            removeAllViewsInLayout();
        } else {
            View topCard = getChildAt(LAST_OBJECT_IN_STACK);
            if (mActiveCard != null && topCard != null && topCard == mActiveCard) {
                Utils.entry_log();
                if (this.flingCardListener.isTouching()) {
                    Utils.entry_log();
                    PointF lastPoint = this.flingCardListener.getLastPoint();
                    if (this.mLastTouchPoint == null || !this.mLastTouchPoint.equals(lastPoint)) {
                        Utils.entry_log();
                        this.mLastTouchPoint = lastPoint;
                        Log.d(TAG, "onLayout: removeViewsInLayout");
                        removeViewsInLayout(0, LAST_OBJECT_IN_STACK);
                        layoutChildren(1, adapterCount);
                    }
                }
            } else {
                Log.d(TAG, "onLayout: removeAllViewsInLayout 3");
                // Reset the UI and set top view listener
                removeAllViewsInLayout();
                layoutChildren(START_STACK_FROM, adapterCount);
                setTopView();
            }
        }

        mInLayout = false;

        if(currentAdapterCount <= MIN_ADAPTER_STACK) mFlingListener.onAdapterAboutToEmpty(currentAdapterCount);
    }


    private void layoutChildren(int startingIndex, int adapterCount){
        Utils.entry_log();
        resetOffsets();
        if (adapterCount - startingIndex < MAX_VISIBLE) {
            Utils.entry_log();
            MAX_VISIBLE = adapterCount - startingIndex;
        }
        int viewStack = 0;
        while (startingIndex < START_STACK_FROM + MAX_VISIBLE && startingIndex < adapterCount) {
            Utils.entry_log();
            View newUnderChild = mAdapter.getView(startingIndex, null, this);
            if (newUnderChild.getVisibility() != GONE) {
                Utils.entry_log();
                makeAndAddView(newUnderChild, false);
            }
            startingIndex++;
            viewStack++;
        }

        /**
         * This is to add a base view at end. To make an illusion that views come out from
         * a base card. The scale and translation of this view is same as the one previous to
         * this.
         */
        if(startingIndex >= adapterCount){
            Utils.entry_log();
            LAST_OBJECT_IN_STACK = --viewStack;
            return;
        }
        View newUnderChild = mAdapter.getView(startingIndex, null, this);
        if (newUnderChild != null && newUnderChild.getVisibility() != GONE) {
            Utils.entry_log();
            makeAndAddView(newUnderChild, true);
            LAST_OBJECT_IN_STACK = viewStack;
        }
    }

    private void resetOffsets() {
        Utils.entry_log();
        CURRENT_TRANSY_VAL = 0;
        CURRENT_SCALE_VAL = 0;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void makeAndAddView(View child, boolean isBase) {
        Utils.entry_log();
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
        if (isBase) {
            Utils.entry_log();
            child.setScaleX((float) (child.getScaleX() - (CURRENT_SCALE_VAL - SCALE_OFFSET)));
            child.setScaleY((float) (child.getScaleY() - (CURRENT_SCALE_VAL - SCALE_OFFSET)));
            child.setY(child.getTranslationY() + CURRENT_TRANSY_VAL - TRANS_OFFSET);
        } else {
            child.setScaleX(child.getScaleX() - CURRENT_SCALE_VAL);
            child.setScaleY(child.getScaleY() - CURRENT_SCALE_VAL);
            child.setY(child.getTranslationY() + CURRENT_TRANSY_VAL);
        }

        CURRENT_SCALE_VAL += SCALE_OFFSET;
        CURRENT_TRANSY_VAL += TRANS_OFFSET;
        addViewInLayout(child, 0, lp, true);

        final boolean needToMeasure = child.isLayoutRequested();
        if (needToMeasure) {
            Utils.entry_log();
            int childWidthSpec = getChildMeasureSpec(getWidthMeasureSpec(),
                    getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin,
                    lp.width);
            int childHeightSpec = getChildMeasureSpec(getHeightMeasureSpec(),
                    getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin,
                    lp.height);
            child.measure(childWidthSpec, childHeightSpec);
        } else {
            cleanupLayoutState(child);
        }


        int w = child.getMeasuredWidth();
        int h = child.getMeasuredHeight();

        int gravity = lp.gravity;
        if (gravity == -1) {
            Utils.entry_log();
            gravity = Gravity.TOP | Gravity.START;
        }


        int layoutDirection = getLayoutDirection();
        final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
        final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

        int childLeft;
        int childTop;
        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {

            case Gravity.CENTER_HORIZONTAL:
                childLeft = (getWidth() + getPaddingLeft() - getPaddingRight()  - w) / 2 +
                        lp.leftMargin - lp.rightMargin;
                break;
            case Gravity.END:
                childLeft = getWidth() + getPaddingRight() - w - lp.rightMargin;
                break;
            case Gravity.START:
            default:
                childLeft = getPaddingLeft() + lp.leftMargin;
                break;
        }
        switch (verticalGravity) {
            case Gravity.CENTER_VERTICAL:
                childTop = (getHeight() + getPaddingTop() - getPaddingBottom()  - h) / 2 +
                        lp.topMargin - lp.bottomMargin;
                break;
            case Gravity.BOTTOM:
                childTop = getHeight() - getPaddingBottom() - h - lp.bottomMargin;
                break;
            case Gravity.TOP:
            default:
                childTop = getPaddingTop() + lp.topMargin;
                break;
        }

        child.layout(childLeft, childTop, childLeft + w, childTop + h);
    }

    public void relayoutChild(View child, float scrollDis, int childcount){
        Utils.entry_log();
        float absScrollDis = scrollDis > 1 ? 1 : scrollDis;
        float newScale = (float) (1 - SCALE_OFFSET * (MAX_VISIBLE - childcount) + absScrollDis * SCALE_OFFSET);
        child.setScaleX(newScale);
        child.setScaleY(newScale);
        child.setTranslationY(TRANS_OFFSET * (MAX_VISIBLE - childcount) - absScrollDis * TRANS_OFFSET);
    }

    /**
    *  Set the top view and add the fling listener
    */
    private void setTopView() {
        Utils.entry_log();
        if (getChildCount() > 0) {
            Utils.entry_log();
            mActiveCard = getChildAt(LAST_OBJECT_IN_STACK);
            if (mActiveCard != null) {
                Utils.entry_log();
                flingCardListener = new FlingCardListener(this, mActiveCard, mAdapter.getItem(START_STACK_FROM),
                ROTATION_DEGREES, new FlingCardListener.FlingListener() {
                    @Override
                    public void onCardExited() {
                        Utils.entry_log();
                        mActiveCard = null;
                        START_STACK_FROM ++;
                        currentAdapterCount --;
                        requestLayout();
                    }

                    @Override
                    public void leftExit(Object dataObject) {
                        Utils.entry_log();
                        mFlingListener.onCardExitLeft(dataObject);
                    }

                    @Override
                    public void rightExit(Object dataObject) {
                        Utils.entry_log();
                        mFlingListener.onCardExitRight(dataObject);
                    }

                    @Override
                    public void onClick(Object dataObject) {
                        Utils.entry_log();
                        if(mOnItemClickListener!=null)
                            mOnItemClickListener.onItemClicked(0, dataObject);

                    }

                    @Override
                    public void onScroll(float scrollProgressPercent) {
                        Utils.entry_log();
                        mFlingListener.onScroll(scrollProgressPercent);
                        int childCount = getChildCount() - 1;
                        if(childCount < MAX_VISIBLE){
                            Utils.entry_log();
                            while (childCount > 0) {
                                Utils.entry_log();
                                relayoutChild(getChildAt(childCount - 1), Math.abs(scrollProgressPercent), childCount);
                                childCount--;
                            }
                        } else {
                            while (childCount > 1) {
                                Utils.entry_log();
                                relayoutChild(getChildAt(childCount - 1), Math.abs(scrollProgressPercent), childCount - 1);
                                childCount--;
                            }
                        }
                    }

                    @Override
                    public void topExit(Object dataObject) {
                        Utils.entry_log();
                        mFlingListener.onCardExitTop(dataObject);
                    }

                    @Override
                    public void bottomExit(Object dataObject) {
                        Utils.entry_log();
                        mFlingListener.onCardExitBottom(dataObject);
                    }
                });

                mActiveCard.setOnTouchListener(flingCardListener);
            }
        }
    }

    public void restart(){
        Utils.entry_log();
        currentAdapterCount = mAdapter.getCount();
        adapterCount = currentAdapterCount;
        START_STACK_FROM = 0;
        LAST_OBJECT_IN_STACK = 0;
        MAX_VISIBLE = INITIAL_MAX_VISIBLE;
        layoutChildren(0,currentAdapterCount);
        requestLayout();
    }

    public FlingCardListener getTopCardListener() throws NullPointerException{
        if(flingCardListener==null){
            Utils.entry_log();
            throw new NullPointerException();
        }
        return flingCardListener;
    }

    public void setMaxVisible(int MAX_VISIBLE){
        Utils.entry_log();
        this.MAX_VISIBLE = MAX_VISIBLE;
    }

    public void setMinStackInAdapter(int MIN_ADAPTER_STACK){
        Utils.entry_log();
        this.MIN_ADAPTER_STACK = MIN_ADAPTER_STACK;
    }

    @Override
    public Adapter getAdapter() {
        Utils.entry_log();
        return mAdapter;
    }


    @Override
    public void setAdapter(Adapter adapter) {
        Utils.entry_log();
        if (mAdapter != null && mDataSetObserver != null) {
            Utils.entry_log();
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
            mDataSetObserver = null;
        }

        mAdapter = adapter;
        currentAdapterCount = adapter.getCount();
        adapterCount = mAdapter.getCount();


        if (mAdapter != null  && mDataSetObserver == null) {
            Utils.entry_log();
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }
    }

    public void setFlingListener(OnCardFlingListener OnCardFlingListener) {
        Utils.entry_log();
        this.mFlingListener = OnCardFlingListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        Utils.entry_log();
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        Utils.entry_log();
        return new FrameLayout.LayoutParams(getContext(), attrs);
    }

    private class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            Utils.entry_log();
            int newAdapterCount = mAdapter.getCount();
            currentAdapterCount += newAdapterCount - adapterCount;
            adapterCount = newAdapterCount;
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            Utils.entry_log();
            requestLayout();
        }

    }

    public interface OnItemClickListener {
        void onItemClicked(int itemPosition, Object dataObject);
    }

    public interface OnCardFlingListener {
        void onCardExitLeft(Object dataObject);
        void onCardExitRight(Object dataObject);
        void onAdapterAboutToEmpty(int itemsInAdapter);
        void onScroll(float scrollProgressPercent);
        void onCardExitTop(Object dataObject);
        void onCardExitBottom(Object dataObject);
    }

    public void throwLeft() {
        Utils.entry_log();
        flingCardListener.selectLeft();
    }

    public void throwRight() {
        Utils.entry_log();
        flingCardListener.selectRight();
    }

    public void throwTop() {
        Utils.entry_log();
        flingCardListener.selectTop();
    }

    public void throwBottom() {
        Utils.entry_log();
        flingCardListener.selectBottom();
    }

}
