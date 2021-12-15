
package in.arjsna.swipecardlib;

import ohos.agp.animation.Animator;
import ohos.agp.components.Component;
import ohos.agp.utils.Rect;
import ohos.multimodalinput.event.TouchEvent;

/**
 * Created by arjun on 4/25/16.
 */
public class FlingPageListener implements Component.TouchEventListener {
    private static final String TAG = "FlingPageListener";
    private static final int INVALID_POINTER_ID = -1;
    private final Rect RECT_BOTTOM;
    private final Rect RECT_TOP;
    private final Rect RECT_RIGHT;
    private final Rect RECT_LEFT;
    private final Component frame;
    private final float objectX;
    private final float objectY;
    private final int objectH;
    private final int objectW;
    private final float halfWidth;
    private final float halfHeight;
    private final Object dataObject;
    private final int parentWidth;
    private final int parentHeight;
    private final FlingListener mFlingListener;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float aDownTouchX;
    private float aDownTouchY;
    private float aPosX;
    private float aPosY;
    private boolean isAnimationRunning = false;

//    public FlingPageListener(View frame, Object itemAtPosition, FlingListener flingListener) {
//        this(frame, itemAtPosition, 15f, flingListener);
//    }

    public FlingPageListener(Component frame, Object itemAtPosition, FlingListener flingListener) {
        super();
        this.frame = frame;
        this.objectX = frame.getTranslationX();
        this.objectY = frame.getTranslationY();
        this.objectH = frame.getHeight();
        this.objectW = frame.getWidth();
        this.halfWidth = objectW / 2f;
        this.halfHeight = objectH / 2f;
        this.dataObject = itemAtPosition;
        this.parentWidth = ((Component) frame.getComponentParent()).getWidth();
        this.parentHeight = ((Component) frame.getComponentParent()).getHeight();
        this.mFlingListener = flingListener;
        this.RECT_TOP = new Rect((int) Math.max(frame.getLeft(), leftBorder()), 0, (int) Math.min(frame.getRight(), rightBorder()), (int) topBorder());
        this.RECT_BOTTOM = new Rect((int) Math.max(frame.getLeft(), leftBorder()), (int) bottomBorder(), (int) Math.min(frame.getRight(), rightBorder()), parentHeight);
        this.RECT_LEFT = new Rect(0, (int) Math.max(frame.getTop(), topBorder()), (int) leftBorder(), (int) Math.min(frame.getBottom(), bottomBorder()));
        this.RECT_RIGHT = new Rect((int) rightBorder(), (int) Math.max(frame.getTop(), topBorder()), parentWidth, (int) Math.min(frame.getBottom(), bottomBorder()));
    }

    public float leftBorder() {
        return parentWidth / 4.f;
    }

    public float rightBorder() {
        return 3 * parentWidth / 4.f;
    }

    public float bottomBorder() {
        return 3 * parentHeight / 4.f;
    }

    public float topBorder() {
        return parentHeight / 4.f;
    }


    @Override
    public boolean onTouchEvent(Component view, TouchEvent event) {

        switch (event.getAction()) {
            case TouchEvent.PRIMARY_POINT_DOWN:
                mActivePointerId = event.getPointerId(0);
                float x = 0;
                float y = 0;
                boolean success = false;
                try {
                    x = event.getPointerPosition(mActivePointerId).getX();
                    y = event.getPointerPosition(mActivePointerId).getY();
                    success = true;
                } catch (IllegalArgumentException e) {
//                    Log.w(TAG, "Exception in onTouch(view, event) : " + mActivePointerId, e);
                }
                if (success) {
                    // Remember where we started
                    aDownTouchX = x;
                    aDownTouchY = y;
                    //to prevent an initial jump of the magnifier, aposX and aPosY must
                    //have the values from the magnifier frame
                    if (aPosX == 0) {
                        aPosX = frame.getTranslationX();
                    }
                    if (aPosY == 0) {
                        aPosY = frame.getTranslationY();
                    }

//                    if (y < objectH / 2) {
//                        touchPosition = TOUCH_ABOVE;
//                    } else {
//                        touchPosition = TOUCH_BELOW;
//                    }
                }

//                view.getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case TouchEvent.PRIMARY_POINT_UP: //other
                mActivePointerId = INVALID_POINTER_ID;
                resetCardViewOnStack();
//                view.getParent().requestDisallowInterceptTouchEvent(false);
                break;

            case TouchEvent.OTHER_POINT_DOWN:
                break;
            /**
             * Constant for getActionMasked: A pressed gesture has finished, the motion contains the
             * final release location as well as any intermediate points since the last down or move event.
             */
            /**
             * Constant for {@link #getActionMasked}: A non-primary pointer has gone up.
             * <p>
             * Use {@link #getActionIndex} to retrieve the index of the pointer that changed.
             * </p><p>
             * The index is encoded in the {@link #ACTION_POINTER_INDEX_MASK} bits of the
             * unmasked action returned by {@link #getAction}.
             * </p>
             */
            case TouchEvent.OTHER_POINT_UP:
                final int pointerIndex = event.getAction();
//                        & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    aDownTouchX = event.getPointerPosition(newPointerIndex).getX();
                    aDownTouchY = event.getPointerPosition(newPointerIndex).getY();
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            case TouchEvent.POINT_MOVE:
//                final int pointerIndexMove = event.findPointerIndex(mActivePointerId);
                final float xMove = event.getPointerPosition(mActivePointerId).getX();
                final float yMove = event.getPointerPosition(mActivePointerId).getY();
                final float dx = xMove - aDownTouchX;
                final float dy = yMove - aDownTouchY;

                aPosX += dx;
                aPosY += dy;
                frame.setTranslationY(aPosY);
                mFlingListener.onScroll(getScrollProgressPercent());
                break;

            case TouchEvent.CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
//                view.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            }
        }

        return true;
    }

    private float getScrollProgressPercent() {
        if (movedBeyondTopBorder()) {
            return -1f;
        } else if (movedBeyondBottomBorder()) {
            return 1f;
        } else {
            float zeroToOneValue = (aPosY + halfHeight - topBorder()) / (bottomBorder() - topBorder());
            return zeroToOneValue * 2f - 1f;
        }
    }

    private boolean resetCardViewOnStack() {
        if (movedBeyondTopBorder()) {
            onSelectedY(true, 100);
            mFlingListener.onScroll(-1.0f);
        } else if (movedBeyondBottomBorder()) {
            onSelectedY(false, 100);
            mFlingListener.onScroll(1.0f);
        } else {
            float abslMoveDistance = Math.abs(aPosX - objectX);
            aPosX = 0;
            aPosY = 0;
            aDownTouchX = 0;
            aDownTouchY = 0;
            frame.createAnimatorProperty()
                    .setDuration(200)
                    .setCurveType(Animator.CurveType.OVERSHOOT)
                    .moveToX(objectX)
                    .moveToY(objectY)
                    .rotate(0)
                    .start();
//            frame.animate()
//                    .setDuration(200)
//                    .setInterpolator(new OvershootInterpolator(1.5f))
//                    .x(objectX)
//                    .y(objectY)
//                    .rotation(0);
            mFlingListener.onScroll(0.0f);
            if (abslMoveDistance < 4.0) {
                mFlingListener.onClick(dataObject);
            }
        }
        return false;
    }

    private boolean movedBeyondBottomBorder() {
        int centerX = (int) (frame.getTranslationX() + halfWidth);
        int centerY = (int) (frame.getTranslationY() + halfHeight);
        return (RECT_BOTTOM.contains(centerX, centerY, centerX, centerY) || (centerY > RECT_BOTTOM.bottom && RECT_BOTTOM.contains(centerX, RECT_BOTTOM.top, centerX, RECT_BOTTOM.top)));
//        return aPosY + halfHeight > bottomBorder();
    }

    private boolean movedBeyondTopBorder() {
        int centerX = (int) (frame.getTranslationX() + halfWidth);
        int centerY = (int) (frame.getTranslationY() + halfHeight);
        return (RECT_TOP.contains(centerX, centerY, centerX, centerY) || (centerY < RECT_TOP.top && RECT_TOP.contains(centerX, 0, centerX, 0)));
//        return aPosY + halfHeight < topBorder();
    }

    private void onSelectedY(final boolean isTop, int duration) {
        isAnimationRunning = true;
        float exitY;
        if (isTop) {
            exitY = -objectH;
        } else {
            exitY = parentHeight;
        }

        this.frame.createAnimatorProperty()
                .setDuration(duration)
                .setCurveType(Animator.CurveType.ACCELERATE)
                .moveToY(exitY)
                .setStateChangedListener(new Animator.StateChangedListener() {
                    //#region unused
                    @Override
                    public void onStart(Animator animator) {

                    }

                    @Override
                    public void onStop(Animator animator) {

                    }

                    @Override
                    public void onCancel(Animator animator) {

                    }

                    @Override
                    public void onPause(Animator animator) {

                    }

                    @Override
                    public void onResume(Animator animator) {

                    }
                    //#endregion unused

                    @Override
                    public void onEnd(Animator animator) {
                        if (isTop) {
                            mFlingListener.onCardExited();
                            mFlingListener.topExit(dataObject);
                        } else {
                            mFlingListener.onCardExited();
                            mFlingListener.bottomExit(dataObject);
                        }
                        isAnimationRunning = false;
                    }

                }).start();
    }

    private float getExitPointX(int exitYPoint) {
        float[] x = new float[2];
        x[0] = objectX;
        x[1] = aPosX;

        float[] y = new float[2];
        y[0] = objectY;
        y[1] = aPosY;

        LinearRegression regression = new LinearRegression(y, x);

        //Your typical y = ax+b linear regression
        return (float) regression.slope() * exitYPoint + (float) regression.intercept();
    }

    public boolean isTouching() {
        return this.mActivePointerId != INVALID_POINTER_ID;
    }

    public PointF getLastPoint() {
        return new PointF(this.aPosX, this.aPosY);
    }

//    private float getRotationWidthOffset() {
//        return objectW / MAX_COS - objectW;
//    }

    protected interface FlingListener {
        void onCardExited();

        void onClick(Object dataObject);

        void onScroll(float scrollProgressPercent);

        void topExit(Object dataObject);

        void bottomExit(Object dataObject);
    }
}
