package in.arjsna.swipecardlib;


import ohos.agp.components.AttrSet;
import ohos.agp.components.BaseItemProvider;
import ohos.agp.components.Component;
import ohos.agp.components.StackLayout;
import ohos.agp.database.DataSetSubscriber;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

/**
 * Created by arjun on 4/26/16.
 */
public class SwipePageView extends BaseFlingAdapterView {
    private int MIN_ADAPTER_STACK = 6;
    private int MAX_VISIBLE = 2;
    private BaseItemProvider mAdapter;
    private boolean mInLayout = false;
    private int LAST_OBJECT_IN_STACK = 0;
    private Component mActiveCard;
    private FlingPageListener flingPageListener;
    private PointF mLastTouchPoint;
    private float CURRENT_TRANSY_VAL;
    private float CURRENT_SCALE_VAL;
    private double SCALE_OFFSET = 0.2;
    private int TRANS_OFFSET = 45;
    private OnPageFlingListener mFlingListener;
    private OnItemClickListener mOnItemClickListener;
    private AdapterDataSetObserver mDataSetObserver;
    private int START_STACK_FROM = 0;
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0x00201, "-MainAbility-");

    public SwipePageView(Context context) {
        super(context);
        init(null);
    }

    public SwipePageView(Context context, AttrSet attrSet) {
        super(context, attrSet);
        init(attrSet);
    }

    public SwipePageView(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
        init(attrSet);
    }

    public static class SwipePageViewAttrs{
       public static final String MIN_ADAPTER_STACK_PAGE="min_adapter_stack_page";
    }

    void init(AttrSet attrSet) {
        setLayoutRefreshedListener(this);
        AttrUtils attrUtils = new AttrUtils(attrSet);
        MAX_VISIBLE = attrUtils.getDimensionFromAttr(SwipeCardView.SwipeCardViewAttrs.MAX_VISIBLE, MAX_VISIBLE);
        MIN_ADAPTER_STACK = attrUtils.getDimensionFromAttr(SwipePageViewAttrs.MIN_ADAPTER_STACK_PAGE, MIN_ADAPTER_STACK);
//
    }
    //
//    public SwipePageView(Context context) {
//        this(context, null);
//        Utils.entry_log();
//    }
//
//    public SwipePageView(Context context, AttributeSet attrs) {
//        this(context, attrs, -1);
//        Utils.entry_log();
//    }
//
//    public SwipePageView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        Utils.entry_log();
//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipePageView, defStyle, 0);
////        MAX_VISIBLE = a.getInt(R.styleable.SwipeFlingCardView_max_visible, MAX_VISIBLE);
//        MIN_ADAPTER_STACK = a.getInt(R.styleable.SwipePageView_min_adapter_stack_page, MIN_ADAPTER_STACK);
//        a.recycle();
//    }

//    @Override
//    public void requestLayout() {
//        Utils.entry_log();
//        if (!mInLayout) {
//            Utils.entry_log();
//            super.requestLayout();
//        }
//    }

    @Override
    public void onRefreshed(Component component) {
//
//    }
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Utils.entry_log();
        // if we don't have an adapter, we don't need to do anything
        if (mAdapter == null) {
            Utils.entry_log();
            return;
        }

        mInLayout = true;
        final int adapterCount = mAdapter.getCount();

        if (adapterCount == 0) {
            Utils.entry_log();
            removeAllComponents();
        } else {
            Component topCard = getComponentAt(LAST_OBJECT_IN_STACK);
            if (mActiveCard != null && topCard != null && topCard == mActiveCard) {
                Utils.entry_log();
                if (this.flingPageListener.isTouching()) {
                    Utils.entry_log();
                    PointF lastPoint = this.flingPageListener.getLastPoint();
                    if (this.mLastTouchPoint == null || !this.mLastTouchPoint.equals(lastPoint)) {
                        Utils.entry_log();
                        this.mLastTouchPoint = lastPoint;
                        for (int i = 0; i < LAST_OBJECT_IN_STACK; i++) {
                            removeComponentAt(i);
                        }// TODO: is he implementaion right ?
//                        removeViewsInLayout(0, LAST_OBJECT_IN_STACK);
                        layoutChildren(1, adapterCount);
                    }
                }
            } else {
                // Reset the UI and set top view listener
                removeAllComponents();
                layoutChildren(START_STACK_FROM, adapterCount);
                setTopView();
            }
        }

        mInLayout = false;

        if (adapterCount <= MIN_ADAPTER_STACK) mFlingListener.onAdapterAboutToEmpty(adapterCount);
    }

    private void layoutChildren(int startingIndex, int adapterCount) {
        resetOffsets();
        if (adapterCount < MAX_VISIBLE) {
            MAX_VISIBLE = adapterCount;
        }
        int viewStack = 0;
        while (startingIndex < START_STACK_FROM + MAX_VISIBLE && startingIndex < adapterCount) {
            Utils.entry_log();
            Component newUnderChild = mAdapter.getComponent(startingIndex, null, this);
            if (newUnderChild.getVisibility() != HIDE) {
                Utils.entry_log();
                makeAndAddView(newUnderChild);
                LAST_OBJECT_IN_STACK = viewStack;
            }
            startingIndex++;
            viewStack++;
        }
    }

    private void resetOffsets() {
        Utils.entry_log();
        CURRENT_TRANSY_VAL = 0;
        CURRENT_SCALE_VAL = 0;
    }


    private void makeAndAddView(Component child) {
        Utils.entry_log();
        try {
            LayoutConfig lp = (LayoutConfig) child.getLayoutConfig();
            child.setScaleX(child.getScaleX() - CURRENT_SCALE_VAL);
            child.setScaleY(child.getScaleY() - CURRENT_SCALE_VAL);
//        child.setY(child.getTranslationY() + CURRENT_TRANSY_VAL);
            CURRENT_SCALE_VAL += SCALE_OFFSET;
//        CURRENT_TRANSY_VAL += TRANS_OFFSET;
//        addViewInLayout(child, 0, lp, true);
            addComponent(child, 0, lp);

//        final boolean needToMeasure = child.isLayoutRequested();
//        if (needToMeasure) {
//            int childWidthSpec = getChildMeasureSpec(getWidthMeasureSpec(),
//                    getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin,
//                    lp.width);
//            int childHeightSpec = getChildMeasureSpec(getHeightMeasureSpec(),
//                    getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin,
//                    lp.height);
//            child.measure(childWidthSpec, childHeightSpec);
//        } else {
//            cleanupLayoutState(child);
//        }


            int w = child.getEstimatedWidth();
            int h = child.getEstimatedHeight();
//
//        int gravity = lp.gravity;
//        if (gravity == -1) {
//            Utils.entry_log();
//            gravity = Gravity.TOP | Gravity.START;
//        }

            //TODO: no gravity ??

//        LayoutDirection layoutDirection = getLayoutDirection();
//        final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
//        final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
//
            int childLeft = getPaddingLeft() + lp.getMarginLeft();//(getWidth() + getPaddingLeft() - getPaddingRight() - w) / 2 + lp.getMarginLeft() - lp.getMarginRight();
            int childTop = getPaddingTop() + lp.getMarginTop();//(getHeight() + getPaddingTop() - getPaddingBottom() - h) / 2 + lp.getMarginTop() - lp.getMarginBottom();
//        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
//            case Gravity.CENTER_HORIZONTAL:
//                childLeft = (getWidth() + getPaddingLeft() - getPaddingRight()  - w) / 2 +
//                        lp.leftMargin - lp.rightMargin;
//                break;
//            case Gravity.END:
//                childLeft = getWidth() + getPaddingRight() - w - lp.rightMargin;
//                break;
//            case Gravity.START:
//            default:
//                childLeft = getPaddingLeft() + lp.leftMargin;
//                break;
//        }
//        switch (verticalGravity) {
//            case Gravity.CENTER_VERTICAL:
//                childTop = (getHeight() + getPaddingTop() - getPaddingBottom()  - h) / 2 +
//                        lp.topMargin - lp.bottomMargin;
//                break;
//            case Gravity.BOTTOM:
//                childTop = getHeight() - getPaddingBottom() - h - lp.bottomMargin;
//                break;
//            case Gravity.TOP:
//            default:
//                childTop = getPaddingTop() + lp.topMargin;
//                break;
//        }
//
//        child.layout(childLeft, childTop, childLeft + w, childTop + h);
            child.arrange(childLeft, childTop, childLeft + w, childTop + h);
        } catch (Exception ex) {
            HiLog.debug(LABEL_LOG, "Exception" + ex);
            for (StackTraceElement st : ex.getStackTrace()) {
                HiLog.debug(LABEL_LOG, "" + st);

            }
        }
    }

    private void setTopView() {
        Utils.entry_log();
        if (getChildCount() > 0) {

            mActiveCard = getComponentAt(LAST_OBJECT_IN_STACK);
            if (mActiveCard != null) {
                Utils.entry_log();

                flingPageListener = new FlingPageListener(mActiveCard, mAdapter.getItem(0),
                        new FlingPageListener.FlingListener() {
                            @Override
                            public void onCardExited() {
                                Utils.entry_log();
                                mActiveCard = null;
                                START_STACK_FROM++;
                                postLayout();
//                                mFlingListener.removeFirstObjectInAdapter();
                            }

                            @Override
                            public void onClick(Object dataObject) {
                                Utils.entry_log();
                                if (mOnItemClickListener != null)
                                    mOnItemClickListener.onItemClicked(0, dataObject);

                            }

                            @Override
                            public void onScroll(float scrollProgressPercent) {
                                Utils.entry_log();
                                mFlingListener.onScroll(scrollProgressPercent);
                                int childCount = getChildCount() - 1;
                                if (childCount < MAX_VISIBLE) {
                                    while (childCount > 0) {
                                        Utils.entry_log(childCount);
                                        relayoutChild(getComponentAt(childCount - 1), Math.abs(scrollProgressPercent), childCount);
                                        childCount--;
                                    }
                                } else {
                                    while (childCount > 1) {
                                        Utils.entry_log(childCount);
                                        relayoutChild(getComponentAt(childCount - 1), Math.abs(scrollProgressPercent), childCount - 1);
                                        childCount--;
                                    }
                                }
                            }

                            @Override
                            public void topExit(Object dataObject) {
                                Utils.entry_log();
                                mFlingListener.onTopCardExit(dataObject);
                            }

                            @Override
                            public void bottomExit(Object dataObject) {
                                Utils.entry_log();
                                mFlingListener.onBottomCardExit(dataObject);
                            }
                        });

                mActiveCard.setTouchEventListener(flingPageListener);
            }
        }
    }

    public void relayoutChild(Component child, float scrollDis, int childcount) {
        Utils.entry_log();
        float absScrollDis = scrollDis > 1 ? 1 : scrollDis;
        float newScale = (float) (1 - SCALE_OFFSET * (MAX_VISIBLE - childcount) + absScrollDis * SCALE_OFFSET);
        child.setScaleX(newScale);
        child.setScaleY(newScale);
//        child.setTranslationY(TRANS_OFFSET * (MAX_VISIBLE - childcount) - absScrollDis * TRANS_OFFSET);
    }

    @Override
    public void onComponentBoundToWindow(Component component) {

    }

    @Override
    public void onComponentUnboundFromWindow(Component component) {

    }


    public interface OnItemClickListener {
        void onItemClicked(int itemPosition, Object dataObject);
    }

    public BaseItemProvider getAdapter() {
        Utils.entry_log();
        return null;
    }

    public void setAdapter(BaseItemProvider adapter) {
        Utils.entry_log();
        if (mAdapter != null && mDataSetObserver != null) {
            Utils.entry_log();
            mAdapter.removeDataSubscriber(mDataSetObserver);
            mDataSetObserver = null;
        }

        mAdapter = adapter;

        if (mAdapter != null && mDataSetObserver == null) {
            Utils.entry_log();
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.addDataSubscriber(mDataSetObserver);
        }
    }

    public Component getSelectedView() {
        Utils.entry_log();
        return mActiveCard;
    }

    public void setFlingListener(OnPageFlingListener OnCardFlingListener) {
        Utils.entry_log();
        this.mFlingListener = OnCardFlingListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnPageFlingListener {
        void onAdapterAboutToEmpty(int itemsInAdapter);

        void onScroll(float scrollProgressPercent);

        void onTopCardExit(Object dataObject);

        void onBottomCardExit(Object dataObject);
    }

    private class AdapterDataSetObserver extends DataSetSubscriber {
        @Override
        public void onChanged() {
            Utils.entry_log();
            postLayout();
        }

        @Override
        public void onInvalidated() {
            Utils.entry_log();
            postLayout();
        }

    }

    @Override
    public LayoutConfig createLayoutConfig(Context context, AttrSet attrSet) {
        //TODO: ??
        return new StackLayout.LayoutConfig(getContext(), attrSet);
//        return super.createLayoutConfig(context, attrSet);
    }

    //    @Override
//    public LayoutParams generateLayoutParams(AttributeSet attrs) {
//        Utils.entry_log();
//        return new FrameLayout.LayoutParams(getContext(), attrs);
//    }
}
