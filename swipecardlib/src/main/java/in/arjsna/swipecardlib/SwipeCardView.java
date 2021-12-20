package in.arjsna.swipecardlib;


import ohos.agp.components.*;
import ohos.agp.database.DataSetSubscriber;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class SwipeCardView extends BaseFlingAdapterView implements ComponentContainer.ArrangeListener {

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

    private BaseItemProvider mAdapter;

    private int LAST_OBJECT_IN_STACK = 0;

    private OnCardFlingListener mFlingListener;

    private AdapterDataSetObserver mDataSetObserver;

    private boolean mInLayout = false;

    private Component mActiveCard = null;

    private OnItemClickListener mOnItemClickListener;

    private FlingCardListener flingCardListener;

    private PointF mLastTouchPoint;

    private int START_STACK_FROM = 0;

    private int adapterCount = 0;
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0x00201, "-MainAbility-");

//#endregion properties

    public final static class SwipeCardViewAttrs {
        final static String ROTATION_DEGREES = "rotation_degrees";
        final static String MIN_ADAPTER_STACK = "min_adapter_stack";
        final static String MAX_VISIBLE = "max_visible";
        final static String LEFT_SWIPE_DETECT = "left_swipe_detect";
        final static String RIGHT_SWIPE_DETECT = "right_swipe_detect";
        final static String TOP_SWIPE_DETECT = "top_swipe_detect";
        final static String BOTTOM_SWIPE_DETECT = "bottom_swipe_detect";
    }

    public SwipeCardView(Context context) {
        super(context);
        initConst(null);
    }

    public SwipeCardView(Context context, AttrSet attrSet) {
        super(context, attrSet);
        initConst(attrSet);
        Utils.entry_log();
    }

    public SwipeCardView(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
        initConst(attrSet);
        Utils.entry_log();
    }

    public void initConst(AttrSet attrSet) {

        AttrUtils attrUtils = new AttrUtils(attrSet);
        MAX_VISIBLE = attrUtils.getIntFromAttr(SwipeCardViewAttrs.MAX_VISIBLE, MAX_VISIBLE);
        MIN_ADAPTER_STACK = attrUtils.getIntFromAttr(SwipeCardViewAttrs.MIN_ADAPTER_STACK, MIN_ADAPTER_STACK);
        ROTATION_DEGREES = attrUtils.getFloatFromAttr(SwipeCardViewAttrs.ROTATION_DEGREES, ROTATION_DEGREES);
        DETECT_LEFT_SWIPE = attrUtils.getBooleanFromAttr(SwipeCardViewAttrs.LEFT_SWIPE_DETECT, true);
        DETECT_RIGHT_SWIPE = attrUtils.getBooleanFromAttr(SwipeCardViewAttrs.RIGHT_SWIPE_DETECT, true);
        DETECT_BOTTOM_SWIPE = attrUtils.getBooleanFromAttr(SwipeCardViewAttrs.BOTTOM_SWIPE_DETECT, true);
        DETECT_TOP_SWIPE = attrUtils.getBooleanFromAttr(SwipeCardViewAttrs.TOP_SWIPE_DETECT, true);
        INITIAL_MAX_VISIBLE = MAX_VISIBLE;
        setLayoutRefreshedListener(this);
        setArrangeListener(this);
        Utils.entry_log();
    }


    /**
     * A shortcut method to set both the listeners and the adapter.
     *
     * @param context  The activity context which extends OnCardFlingListener, OnItemClickListener or both
     * @param mAdapter The adapter you have to set.
     */
    public void init(final Context context, BaseItemProvider mAdapter) {
        Utils.entry_log();
        Utils.entry_log();
        if (context instanceof OnCardFlingListener) {
            Utils.entry_log();
            mFlingListener = (OnCardFlingListener) context;
        } else {
            throw new RuntimeException("Activity does not implement SwipeFlingAdapterView.OnCardFlingListener");
        }
        if (context instanceof OnItemClickListener) {
            Utils.entry_log();
            mOnItemClickListener = (OnItemClickListener) context;
        }
        setAdapter(mAdapter);
    }


    public Component getSelectedView() {
        Utils.entry_log();
        return mActiveCard;
    }

    public int getCurrentPosition() {
        Utils.entry_log();
        return START_STACK_FROM;
    }

    public Object getCurrentItem() {
        Utils.entry_log();
        return mAdapter.getItem(START_STACK_FROM);
    }

    public void requestLayout() {
        Utils.entry_log("mInLayout " + mInLayout);
        if (!mInLayout) {
            Utils.entry_log();
            postLayout();
        }
    }

    @Override
    public boolean onArrange(int left,
                             int top,
                             int width,
                             int height) {
        return false;
    }


    @Override
    public void onRefreshed(Component component) {
        try {
            Utils.entry_log();
            // if we don't have an adapter, we don't need to do anything
            if (mAdapter == null) {
                Utils.entry_log();
                return;
            }
            mInLayout = true;

            if (adapterCount == 0) {
                Utils.entry_log();

                removeAllComponents();
            } else {
                Component topCard = getComponentAt(LAST_OBJECT_IN_STACK);
                if (mActiveCard != null && topCard != null && topCard == mActiveCard) {
                    Utils.entry_log();
                    if (this.flingCardListener.isTouching()) {
                        Utils.entry_log();
                        PointF lastPoint = this.flingCardListener.getLastPoint();
                        if (this.mLastTouchPoint == null || !this.mLastTouchPoint.equals(lastPoint)) {
                            Utils.entry_log();
                            this.mLastTouchPoint = lastPoint;
                            for (int i = 0; i < LAST_OBJECT_IN_STACK; i++) {
                                removeComponentAt(i);
                            }// TODO: is he implementaion right ?
                            Utils.entry_log("here");
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

            if (currentAdapterCount <= MIN_ADAPTER_STACK) {
                mFlingListener.onAdapterAboutToEmpty(currentAdapterCount);
            }
        } catch (Exception ex) {
            HiLog.debug(LABEL_LOG, "Exception" + ex);
            for (StackTraceElement st : ex.getStackTrace()) {
                HiLog.debug(LABEL_LOG, "" + st);

            }
        }

    }


    private void layoutChildren(int startingIndex, int adapterCount) {
        Utils.entry_log();
        resetOffsets();
        if (adapterCount - startingIndex < MAX_VISIBLE) {
            Utils.entry_log();
            MAX_VISIBLE = adapterCount - startingIndex;
        }
        int viewStack = 0;
        while (startingIndex < START_STACK_FROM + MAX_VISIBLE && startingIndex < adapterCount) {
            Utils.entry_log();
            Component newUnderChild = mAdapter.getComponent(startingIndex, null, this);
            if (newUnderChild.getVisibility() != HIDE) {
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
        if (startingIndex >= adapterCount) {
            Utils.entry_log();
            LAST_OBJECT_IN_STACK = --viewStack;
            return;
        }
        Component newUnderChild = mAdapter.getComponent(startingIndex, null, this);
        if (newUnderChild != null && newUnderChild.getVisibility() != HIDE) {
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

    int maxWidth = -1;
    int maxHeight = -1;

    private void makeAndAddView(Component child, boolean isBase) {
        try {
            LayoutConfig lp = child.getLayoutConfig();

            Utils.entry_log();
            if (isBase) {
                Utils.entry_log();
                child.setScaleX((float) (child.getScaleX() - (CURRENT_SCALE_VAL - SCALE_OFFSET)));
                child.setScaleY((float) (child.getScaleY() - (CURRENT_SCALE_VAL - SCALE_OFFSET)));
                child.setTranslationY(child.getTranslationY() + CURRENT_TRANSY_VAL - TRANS_OFFSET);
            } else {
                child.setScaleX(child.getScaleX() - CURRENT_SCALE_VAL);
                child.setScaleY(child.getScaleY() - CURRENT_SCALE_VAL);
                child.setTranslationY(child.getTranslationY() + CURRENT_TRANSY_VAL);
            }

            CURRENT_SCALE_VAL += SCALE_OFFSET;
            CURRENT_TRANSY_VAL += TRANS_OFFSET;
            addComponent(child, 0);

            //TODO:gravity ??

            final boolean needToMeasure = true;// child.isLayoutRequested();
            if (needToMeasure) {
                Utils.entry_log();
                int childWidthSpec = EstimateSpec.getChildSizeWithMode(lp.width, getWidthMeasureSpec(), EstimateSpec.PRECISE);


                //            getChildMeasureSpec(getWidthMeasureSpec(),
                //                    getPaddingLeft() + getPaddingRight() + lp.getMarginLeft() + lp.getMarginRight(),
                //                    lp.width);
                //            int childHeightSpec = getChildMeasureSpec(getHeightMeasureSpec(),
                //                    getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin,
                //                    lp.height);
                int childHeightSpec = EstimateSpec.getChildSizeWithMode(lp.height, getEstimatedHeight(), EstimateSpec.PRECISE);


                child.estimateSize(childWidthSpec, childHeightSpec);
//                estimateSize(childWidthSpec, childHeightSpec);
            } else {
//            cleanupLayoutState(child);
            }
            //
//
            int w = child.getEstimatedWidth();
            int h = child.getEstimatedHeight();
//
//        int gravity = lp.gravity;
//        if (gravity == -1) {
//            Utils.entry_log();
//            gravity = Gravity.TOP | Gravity.START;
//        }
//
//
//        int layoutDirection = getLayoutDirection();
//        final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
//        final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
//
            int childLeft = getPaddingLeft() + lp.getMarginLeft();//(getWidth() + getPaddingLeft() - getPaddingRight() - w) / 2 + lp.getMarginLeft() - lp.getMarginRight();
            int childTop = getPaddingTop() + lp.getMarginTop();//(getHeight() + getPaddingTop() - getPaddingBottom() - h) / 2 + lp.getMarginTop() - lp.getMarginBottom();
//        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
//
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


            // child.layout(childLeft, childTop, childLeft + w, childTop + h);
            child.arrange(childLeft, childTop, childLeft + w, childTop + h);
            HiLog.debug(LABEL_LOG, String.format("makeAndAddView: child.arrange(%d, %d, %d, %d) ", childLeft, childTop, childLeft + w, childTop + h));

            maxWidth = Math.max(maxWidth, child.getWidth());
            maxHeight = Math.max(maxHeight, child.getHeight());
        } catch (Exception ex) {
            HiLog.debug(LABEL_LOG, "Exception" + ex);
            for (StackTraceElement st : ex.getStackTrace()) {
                HiLog.debug(LABEL_LOG, "" + st);

            }
        }
    }

    public void relayoutChild(Component child, float scrollDis, int childcount) {
        Utils.entry_log();
        float absScrollDis = scrollDis > 1 ? 1 : scrollDis;
        float newScale = (float) (1 - SCALE_OFFSET * (MAX_VISIBLE - childcount) + absScrollDis * SCALE_OFFSET);
        child.setScaleX(newScale);
        child.setScaleY(newScale);
        child.setTranslationY(TRANS_OFFSET * (MAX_VISIBLE - childcount) - absScrollDis * TRANS_OFFSET);
    }

    /**
     * Set the top view and add the fling listener
     */
    private void setTopView() {
        Utils.entry_log();
        if (getChildCount() > 0) {
            Utils.entry_log();
            mActiveCard = getComponentAt(LAST_OBJECT_IN_STACK);
            if (mActiveCard != null) {
                Utils.entry_log();
                flingCardListener = new FlingCardListener(
                        this,
                        mActiveCard,
                        mAdapter.getItem(START_STACK_FROM),
                        ROTATION_DEGREES,
                        new FlingCardListener.FlingListener() {
                            @Override
                            public void onCardExited() {
                                Utils.entry_log();
                                 mActiveCard = null;
                                START_STACK_FROM++;
                                currentAdapterCount--;
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
                                if (mOnItemClickListener != null)
                                    mOnItemClickListener.onItemClicked(0, dataObject);

                            }

                            @Override
                            public void onScroll(float scrollProgressPercent) {
                                Utils.entry_log();
                                mFlingListener.onScroll(scrollProgressPercent);
                                int childCount = getChildCount() - 1;
                                if (childCount < MAX_VISIBLE) {
                                    Utils.entry_log();
                                    while (childCount > 0) {
                                        Utils.entry_log();
                                        relayoutChild(getComponentAt(childCount - 1), Math.abs(scrollProgressPercent), childCount);
                                        childCount--;
                                    }
                                } else {
                                    while (childCount > 1) {
                                        Utils.entry_log();
                                        relayoutChild(getComponentAt(childCount - 1), Math.abs(scrollProgressPercent), childCount - 1);
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

                mActiveCard.setTouchEventListener(flingCardListener);
            }
        }
    }

    public void restart() {
        Utils.entry_log();
        currentAdapterCount = mAdapter.getCount();
        adapterCount = currentAdapterCount;
        START_STACK_FROM = 0;
        LAST_OBJECT_IN_STACK = 0;
        MAX_VISIBLE = INITIAL_MAX_VISIBLE;
        layoutChildren(0, currentAdapterCount);
        requestLayout();
    }

    public FlingCardListener getTopCardListener() throws NullPointerException {
        if (flingCardListener == null) {
            Utils.entry_log();
            throw new NullPointerException();
        }
        return flingCardListener;
    }

    public void setMaxVisible(int MAX_VISIBLE) {
        Utils.entry_log();
        this.MAX_VISIBLE = MAX_VISIBLE;
    }

    public void setMinStackInAdapter(int MIN_ADAPTER_STACK) {
        Utils.entry_log();
        this.MIN_ADAPTER_STACK = MIN_ADAPTER_STACK;
    }


    public BaseItemProvider getAdapter() {
        Utils.entry_log();
        return mAdapter;
    }


    public void setAdapter(BaseItemProvider adapter) {
        Utils.entry_log();
        if (mAdapter != null && mDataSetObserver != null) {
            Utils.entry_log();
            mAdapter.removeDataSubscriber(mDataSetObserver);
            mDataSetObserver = null;
        }

        mAdapter = adapter;
        currentAdapterCount = adapter.getCount();
        adapterCount = mAdapter.getCount();


        if (mAdapter != null && mDataSetObserver == null) {
            Utils.entry_log();
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.addDataSubscriber(mDataSetObserver);
        }
    }

    public void setFlingListener(OnCardFlingListener OnCardFlingListener) {
        Utils.entry_log();
        this.mFlingListener = OnCardFlingListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        Utils.entry_log();
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public StackLayout.LayoutConfig createLayoutConfig(Context context, AttrSet attrSet) {
        return new StackLayout.LayoutConfig(getContext(), attrSet);
    }

    @Override
    public void onComponentBoundToWindow(Component component) {

    }

    @Override
    public void onComponentUnboundFromWindow(Component component) {

    }


    private class AdapterDataSetObserver extends DataSetSubscriber {
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
        Utils.entry_log(flingCardListener);
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
