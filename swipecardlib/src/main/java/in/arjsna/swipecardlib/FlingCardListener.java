package in.arjsna.swipecardlib;

import ohos.agp.animation.Animator;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.utils.Rect;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.multimodalinput.event.TouchEvent;


public class FlingCardListener implements Component.TouchEventListener {

    private static final String TAG = "entry_log " + FlingCardListener.class.getSimpleName();

    //#region parameters
    private static final int INVALID_POINTER_ID = -1;

    private final SwipeCardView parentView;

    private Rect RECT_TOP;

    private Rect RECT_BOTTOM;

    private Rect RECT_LEFT;

    private Rect RECT_RIGHT;

    private final float objectX;

    private final float objectY;

    private final int objectH;

    private final int objectW;

    private final int parentWidth;

    private final FlingListener mFlingListener;

    private final Object dataObject;

    private final float halfWidth;

    private final float halfHeight;

    private final int parentHeight;

    private float BASE_ROTATION_DEGREES;

    private float aPosX;

    private float aPosY;

    private float aDownTouchX;

    private float aDownTouchY;

    // The active pointer is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;

    private Component frame = null;

    private final int TOUCH_ABOVE = 0;

    private final int TOUCH_BELOW = 1;

    private int touchPosition;

    private boolean isAnimationRunning = false;

    private final float MAX_COS = (float) Math.cos(Math.toRadians(45));
    //#endregion parameters
    //
    public FlingCardListener(SwipeCardView parent, Component frame, Object itemAtPosition, FlingListener flingListener) {
        this(parent, frame, itemAtPosition, 15f, flingListener);
        Utils.entry_log();
    }

    public FlingCardListener(SwipeCardView parent, Component frame, Object itemAtPosition, float rotation_degrees, FlingListener flingListener) {
        super();
        Utils.entry_log();
        this.parentView = parent;
        this.frame = frame;
        this.objectX = frame.getContentPositionX();
        this.objectY = frame.getContentPositionY();
        this.objectH = frame.getHeight();
        this.objectW = frame.getWidth();
        this.halfWidth = objectW / 2f;
        this.halfHeight = objectH / 2f;
        this.dataObject = itemAtPosition;
        this.parentWidth = ((Component) frame.getComponentParent()).getWidth();
        this.parentHeight = ((Component) frame.getComponentParent()).getHeight();
        this.BASE_ROTATION_DEGREES = rotation_degrees;
        this.mFlingListener = flingListener;
        HiLog.debug(LABEL_LOG, "FlingCardListener: frame.getComponentParent() " + frame.getComponentParent());
        HiLog.debug(LABEL_LOG, "FlingCardListener: parentWidth " + parentWidth);
        HiLog.debug(LABEL_LOG, "FlingCardListener: parentHeight " + parentHeight);
        HiLog.debug(LABEL_LOG, "FlingCardListener: parent.getHeight " + parent.getHeight());
        HiLog.debug(LABEL_LOG, "FlingCardListener: parent.getWidth " + parent.getWidth());
        HiLog.debug(LABEL_LOG, "FlingCardListener: objectX " + objectX);
        HiLog.debug(LABEL_LOG, "FlingCardListener: objectY " + objectY);
        HiLog.debug(LABEL_LOG, "FlingCardListener: objectH " + objectH);
        HiLog.debug(LABEL_LOG, "FlingCardListener: objectW " + objectW);
        HiLog.debug(LABEL_LOG, "FlingCardListener: halfWidth " + halfWidth);
        HiLog.debug(LABEL_LOG, "FlingCardListener: halfHeight " + halfHeight);
        HiLog.debug(LABEL_LOG, "FlingCardListener: dataObject " + dataObject);
        HiLog.debug(LABEL_LOG, "FlingCardListener: BASE_ROTATION_DEGREES " + BASE_ROTATION_DEGREES);
        HiLog.debug(LABEL_LOG, "FlingCardListener: frame.getLeft() -> " + frame.getLeft());
        HiLog.debug(LABEL_LOG, "FlingCardListener: frame.getRight() -> " + frame.getRight());
        HiLog.debug(LABEL_LOG, "FlingCardListener: leftBorder() -> " + leftBorder());
        HiLog.debug(LABEL_LOG, "FlingCardListener: rightBorder() -> " + rightBorder());
        HiLog.debug(LABEL_LOG, "FlingCardListener: topBorder() -> " + topBorder());
        this.RECT_TOP = new Rect(
                0 + (int) Math.max(frame.getLeft(), leftBorder()),
                0,
                0 + (int) Math.min(frame.getRight(), rightBorder()),
                0 + (int) topBorder()
        );
        HiLog.debug(LABEL_LOG, "FlingCardListener: left " + RECT_TOP.left);
        HiLog.debug(LABEL_LOG, "FlingCardListener: top " + RECT_TOP.top);
        HiLog.debug(LABEL_LOG, "FlingCardListener: right " + RECT_TOP.right);
        HiLog.debug(LABEL_LOG, "FlingCardListener: bottom " + RECT_TOP.bottom);
        this.RECT_BOTTOM = new Rect(
                0+(int) Math.max(frame.getLeft(), leftBorder()),
                0+(int) bottomBorder(),
                0+(int) Math.min(frame.getRight(), rightBorder()),
                0+parentHeight);
        this.RECT_LEFT = new Rect(0, (int) Math.max(frame.getTop(), topBorder()), (int) leftBorder(), (int) Math.min(frame.getBottom(), bottomBorder()));
        this.RECT_RIGHT = new Rect((int) rightBorder(), (int) Math.max(frame.getTop(), topBorder()), parentWidth, (int) Math.min(frame.getBottom(), bottomBorder()));
        HiLog.debug(LABEL_LOG, "FlingCardListener: RECT_TOP " + RECT_TOP);
        HiLog.debug(LABEL_LOG, "FlingCardListener: RECT_BOTTOM " + RECT_BOTTOM);
        HiLog.debug(LABEL_LOG, "FlingCardListener: RECT_LEFT " + RECT_LEFT);
        HiLog.debug(LABEL_LOG, "FlingCardListener: RECT_RIGHT " + RECT_RIGHT);
    }


    @Override
    public boolean onTouchEvent(Component view, TouchEvent event) {
        try {


            Utils.entry_log();
            switch (event.getAction()) {
                case TouchEvent.UNSUPPORTED_DEVICE:
                    HiLog.debug(LABEL_LOG, "onTouchEvent:UNSUPPORTED_DEVICE");
                    break;
                case TouchEvent.CANCEL:
                    HiLog.debug(LABEL_LOG, "onTouchEvent: CANCEL");
                    break;
                case TouchEvent.HOVER_POINTER_ENTER:
                    HiLog.debug(LABEL_LOG, "onTouchEvent: HOVER_POINTER_ENTER");
                    break;
                case TouchEvent.HOVER_POINTER_EXIT:
                    HiLog.debug(LABEL_LOG, "onTouchEvent: HOVER_POINTER_EXIT");
                    break;
                case TouchEvent.HOVER_POINTER_MOVE:
                    HiLog.debug(LABEL_LOG, "onTouchEvent: HOVER_POINTER_MOVE");
                    break;
                case TouchEvent.NONE:
                    HiLog.debug(LABEL_LOG, "onTouchEvent: NONE");
                    break;
                case TouchEvent.OTHER_POINT_DOWN:
                    HiLog.debug(LABEL_LOG, "onTouchEvent: OTHER_POINT_DOWN");
                    break;
                case TouchEvent.OTHER_POINT_UP:
                    HiLog.debug(LABEL_LOG, "onTouchEvent: OTHER_POINT_UP");
                    break;
                case TouchEvent.POINT_MOVE:
                    HiLog.debug(LABEL_LOG, "onTouchEvent: POINT_MOVE");
                    break;
                case TouchEvent.PRIMARY_POINT_DOWN:
                    HiLog.debug(LABEL_LOG, "onTouchEvent: PRIMARY_POINT_DOWN");
                    break;
                case TouchEvent.PRIMARY_POINT_UP:
                    HiLog.debug(LABEL_LOG, "onTouchEvent: PRIMARY_POINT_UP");
                    break;
            }
            switch (event.getAction()) {
                case TouchEvent.PRIMARY_POINT_DOWN: {
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
                            aPosX = 0;
                        }
                        if (aPosY == 0) {
                            aPosY = 0;
                        }

                        if (y < objectH / 2) {
                            touchPosition = TOUCH_ABOVE;
                        } else {
                            touchPosition = TOUCH_BELOW;
                        }
                    }
//                view.getComponentParent().requestDisallowInterceptTouchEvent(true);
                    break;
                }

                case TouchEvent.PRIMARY_POINT_UP: {
                    mActivePointerId = INVALID_POINTER_ID;
                    resetCardViewOnStack();
//                view.getComponentParent().requestDisallowInterceptTouchEvent(false);
                    break;
                }

                case TouchEvent.OTHER_POINT_DOWN:
                    break;

                case TouchEvent.OTHER_POINT_UP: {
                    final int pointerIndex = (event.getAction());
                    final int pointerId = event.getPointerId(pointerIndex);
                    if (pointerId == mActivePointerId) {
                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        aDownTouchX = event.getPointerPosition(newPointerIndex).getX();
                        aDownTouchY = event.getPointerPosition(newPointerIndex).getY();
                        mActivePointerId = event.getPointerId(newPointerIndex);
                    }
                    break;
                }

                case TouchEvent.POINT_MOVE: {
//                final int pointerIndexMove = event.findPointerIndex(mActivePointerId);
                    final float xMove = event.getPointerPosition(mActivePointerId).getX();
                    final float yMove = event.getPointerPosition(mActivePointerId).getY();

                    final float dx = xMove - aDownTouchX;
                    final float dy = yMove - aDownTouchY;
                    // BUG: it is only incrementing so it is getting out of bound
                    aPosX += dx;
                    aPosY += dy;

                    // calculate the rotation degrees
                    float distobjectX = aPosX - objectX;
                    float rotation = BASE_ROTATION_DEGREES * 2.f * distobjectX / parentWidth;
                    if (touchPosition == TOUCH_BELOW) {
                        Utils.entry_log();
                        rotation = -rotation;
                    }
                    frame.setContentPositionX(aPosX);
                    frame.setContentPositionY(aPosY);
                    frame.setRotation(rotation);

                    HiLog.debug(LABEL_LOG, "onTouchEvent:xMove "+xMove);
                    HiLog.debug(LABEL_LOG, "onTouchEvent:yMove "+yMove);
                    HiLog.debug(LABEL_LOG, "onTouchEvent:aDownTouchX "+aDownTouchX);
                    HiLog.debug(LABEL_LOG, "onTouchEvent:aDownTouchY "+aDownTouchY);
                    HiLog.debug(LABEL_LOG, "onTouchEvent:dx "+dx);
                    HiLog.debug(LABEL_LOG, "onTouchEvent:dy "+dy);
                    HiLog.debug(LABEL_LOG, "onTouchEvent:aPosX "+aPosX);
                    HiLog.debug(LABEL_LOG, "onTouchEvent:aPosY "+aPosY);
                    HiLog.debug(LABEL_LOG, "onTouchEvent:rotation:BASE_ROTATION_DEGREES "+BASE_ROTATION_DEGREES);
                    HiLog.debug(LABEL_LOG, "onTouchEvent:rotation:distobjectX "+distobjectX);
                    HiLog.debug(LABEL_LOG, "onTouchEvent:rotation:parentWidth "+parentWidth);
                    HiLog.debug(LABEL_LOG, "onTouchEvent:rotation "+rotation);
                    mFlingListener.onScroll(getScrollProgressPercent());
                    break;
                }

                case TouchEvent.CANCEL: {
                    mActivePointerId = INVALID_POINTER_ID;

                    break;
                }
            }
        } catch (Exception ex) {
            HiLog.debug(LABEL_LOG, "Exception" + ex);
            for (StackTraceElement st : ex.getStackTrace()) {
                HiLog.debug(LABEL_LOG, "" + st);

            }
        }
        return true;
    }

    private float getScrollProgressPercent() {
        Utils.entry_log();
        if (movedBeyondLeftBorder()) {
            return -1f;
        } else if (movedBeyondRightBorder()) {
            return 1f;
        } else {
            float zeroToOneValue = (aPosX + halfWidth - leftBorder()) / (rightBorder() - leftBorder());
            return zeroToOneValue * 2f - 1f;
        }
    }

    private void resetCardViewOnStack() {
        Utils.entry_log();
        if (movedBeyondLeftBorder() && parentView.DETECT_LEFT_SWIPE) {
            HiLog.debug(LABEL_LOG, "resetCardViewOnStack: movedBeyondLeftBorder");
            // Left Swipe
            onSelectedX(true, getExitPoint(-objectW), 100);
            mFlingListener.onScroll(-1.0f);
        } else if (movedBeyondRightBorder() && parentView.DETECT_RIGHT_SWIPE) {
            HiLog.debug(LABEL_LOG, "resetCardViewOnStack: movedBeyondRightBorder");
            // Right Swipe
            onSelectedX(false, getExitPoint(parentWidth), 100);
            mFlingListener.onScroll(1.0f);
        } else if (movedBeyondTopBorder() && parentView.DETECT_TOP_SWIPE) {
            HiLog.debug(LABEL_LOG, "resetCardViewOnStack: movedBeyondTopBorder");
            onSelectedY(true, getExitPointX(-objectH), 100);
            mFlingListener.onScroll(-1.0f);
        } else if (movedBeyondBottomBorder() && parentView.DETECT_BOTTOM_SWIPE) {
            HiLog.debug(LABEL_LOG, "resetCardViewOnStack: movedBeyondBottomBorder");
            onSelectedY(false, getExitPointX(parentHeight), 100);
            mFlingListener.onScroll(1.0f);
        } else {
            HiLog.debug(LABEL_LOG, "resetCardViewOnStack: else");
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
                Utils.entry_log();
                mFlingListener.onClick(dataObject);
            }
        }
    }


    private boolean movedBeyondLeftBorder() {

        Utils.entry_log();
        int centerX = (int) (frame.getContentPositionX() + halfWidth);
        int centerY = (int) (frame.getContentPositionY() + halfHeight);
        HiLog.debug(LABEL_LOG, "movedBeyondLeftBorder:halfWidth " + halfWidth);
        HiLog.debug(LABEL_LOG, "movedBeyondLeftBorder:halfHeight " + halfHeight);
        HiLog.debug(LABEL_LOG, "movedBeyondLeftBorder:centerX " + centerX);
        HiLog.debug(LABEL_LOG, "movedBeyondLeftBorder:centerY " + centerY);
        HiLog.debug(LABEL_LOG, "movedBeyondLeftBorder:RECT_LEFT " + RECT_LEFT);
        HiLog.debug(LABEL_LOG, "movedBeyondLeftBorder:RectContains(RECT_LEFT, 0 + centerX, 0 + centerY) " + RectContains(RECT_LEFT, 0 + centerX, 0 + centerY));
        HiLog.debug(LABEL_LOG, "movedBeyondLeftBorder:centerX < RECT_LEFT.left " + (centerX < RECT_LEFT.left));
        HiLog.debug(LABEL_LOG, "movedBeyondLeftBorder:RectContains(RECT_LEFT, 0, centerY) " + (RectContains(RECT_LEFT, 0, centerY)));
        HiLog.debug(LABEL_LOG, "movedBeyondLeftBorder:RectContains(new Rect(0,459,270,1151), 0, 587)) " + (RectContains(new Rect(0, 459, 270, 1151), 0, 587)));
        return (RectContains(RECT_LEFT, 0 + centerX, 0 + centerY)
                || (centerX < RECT_LEFT.left
                && RectContains(RECT_LEFT, 0, centerY)));
    }

    private boolean movedBeyondRightBorder() {
        Utils.entry_log();
        int centerX = (int) (frame.getContentPositionX() + halfWidth);
        int centerY = (int) (frame.getContentPositionY() + halfHeight);
        return (RectContains(RECT_RIGHT, centerX, centerY)
                || (centerX > RECT_RIGHT.right
                && RectContains(RECT_RIGHT, RECT_RIGHT.left, centerY)));
    }

    private boolean movedBeyondBottomBorder() {
        Utils.entry_log();
        int centerX = (int) (frame.getContentPositionX() + halfWidth);
        int centerY = (int) (frame.getContentPositionY() + halfHeight);
        return (RectContains(RECT_BOTTOM, centerX, centerY)
                || (centerY > RECT_BOTTOM.bottom
                && RectContains(RECT_BOTTOM, centerX, RECT_BOTTOM.top)));
    }

    /**
     * Returns true if (x,y) is inside the rectangle. The left and top are
     * considered to be inside, while the right and bottom are not. This means
     * that for a x,y to be contained: left <= x < right and top <= y < bottom.
     * An empty rectangle never contains any point.
     *
     * @param x The X coordinate of the point being tested for containment
     * @param y The Y coordinate of the point being tested for containment
     * @return true iff (x,y) are contained by the rectangle, where containment
     * means left <= x < right and top <= y < bottom
     */
    public boolean RectContains(Rect r, int x, int y) {
        return r.left < r.right && r.top < r.bottom  // check for empty first
                && x >= r.left && x < r.right && y >= r.top && y < r.bottom;
    }

    private boolean movedBeyondTopBorder() {
        Utils.entry_log();
        // FIXME: this it a bug ??
        // movedBeyondTopBorder:RECT_TOP (400,0,0,480)
        // why                                  | is zero ??

        int centerX = (int) (frame.getContentPositionX() + halfWidth);
        int centerY = (int) (frame.getContentPositionY() + halfHeight);

        HiLog.debug(LABEL_LOG, "movedBeyondTopBorder:getContentPositionX " + frame.getContentPositionX());
        HiLog.debug(LABEL_LOG, "movedBeyondTopBorder:getContentPositionY " + frame.getContentPositionY());
        HiLog.debug(LABEL_LOG, "movedBeyondTopBorder:centerX " + centerX);
        HiLog.debug(LABEL_LOG, "movedBeyondTopBorder:centerY " + centerY);
        HiLog.debug(LABEL_LOG, "movedBeyondTopBorder:RECT_TOP " + RECT_TOP);
        return (RectContains(RECT_TOP, centerX, centerY)
                || (centerY < RECT_TOP.top && RectContains(RECT_TOP, centerX, 0)));
    }

    private float leftBorder() {
        Utils.entry_log();
        return parentWidth / 4.f;
    }

    private float rightBorder() {
        Utils.entry_log();
        return 3 * parentWidth / 4.f;
    }

    private float bottomBorder() {
        Utils.entry_log();
        return 3 * parentHeight / 4.f;
    }

    private float topBorder() {
        Utils.entry_log();
        return parentHeight / 4.f;
    }

    private static final HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0x00201, "-MainAbility-");

    private void onSelectedY(final boolean isTop, float exitX, int duration) {
        Utils.entry_log();
        isAnimationRunning = true;
        float exitY;
        if (isTop) {
            Utils.entry_log();
            exitY = -objectH - getRotationWidthOffset();
        } else {
            exitY = parentHeight + getRotationWidthOffset();
        }

        HiLog.debug(LABEL_LOG, "onSelectedY: exitX " + exitX);
        HiLog.debug(LABEL_LOG, "onSelectedY: exitY " + exitY);
        HiLog.debug(LABEL_LOG, "onSelectedY: duration " + duration);
        HiLog.debug(LABEL_LOG, "onSelectedY: getVerticalExitRotation(isTop) " + getVerticalExitRotation(isTop));
        this.frame.createAnimatorProperty().setDuration(duration)
                .setCurveType(Animator.CurveType.ACCELERATE)
                .moveToX(exitX)
                .moveToY(exitY)
                .setStateChangedListener(new Animator.StateChangedListener() {
                    //#region not used

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
                    //#endregion not used

                    @Override
                    public void onEnd(Animator animator) {
                        if (isTop) {
                            Utils.entry_log();
                            mFlingListener.onCardExited();
                            mFlingListener.topExit(dataObject);
                        } else {
                            mFlingListener.onCardExited();
                            mFlingListener.bottomExit(dataObject);
                        }
                        isAnimationRunning = false;
                    }

                }).rotate(getVerticalExitRotation(isTop)).start();

    }

    private void onSelectedX(final boolean isLeft,
                             float exitY, long duration) {
        Utils.entry_log();
        isAnimationRunning = true;
        float exitX;
        if (isLeft) {
            Utils.entry_log();
            exitX = -objectW - getRotationWidthOffset();
        } else {
            exitX = parentWidth + getRotationWidthOffset();
        }
//         Log.d(TAG, "onSelectedX: duration: "+duration);
//         Log.d(TAG, "onSelectedX: exitX: "+exitX);
//         Log.d(TAG, "onSelectedX: exitY: "+exitY);
//        Log.d(TAG, "onSelectedX: frame: " + frame);
//        Log.d(TAG, "onSelectedX: parentView: " + parentView);

        HiLog.debug(LABEL_LOG, "onSelectedY: objectW " + objectW);
        HiLog.debug(LABEL_LOG, "onSelectedY: objectH " + objectH);
        HiLog.debug(LABEL_LOG, "onSelectedY: exitX " + exitX);
        HiLog.debug(LABEL_LOG, "onSelectedY: exitY " + exitY);
        HiLog.debug(LABEL_LOG, "onSelectedY: duration " + duration);
        HiLog.debug(LABEL_LOG, "onSelectedY: getVerticalExitRotation(isTop) " +
                getHorizontalExitRotation(isLeft));

        this.frame.createAnimatorProperty().setDuration(duration)
                .setCurveType(Animator.CurveType.ACCELERATE)
                .moveFromY(0)
                .moveFromY(0)
                .moveToX(exitX)
                .moveToY(exitY)
                .setStateChangedListener(new Animator.StateChangedListener() {
                    //#region not used
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
                    //#endregion not used

                    @Override
                    public void onEnd(Animator animator) {
                        Utils.entry_log();
                        if (isLeft) {
                            Utils.entry_log();
                            mFlingListener.onCardExited();
                            mFlingListener.leftExit(dataObject);
                        } else {
                            mFlingListener.onCardExited();
                            mFlingListener.rightExit(dataObject);
                        }
                        isAnimationRunning = false;
                    }

                }).rotate(getHorizontalExitRotation(isLeft)).start();
    }

    void selectLeft() {
        Utils.entry_log();
        if (!isAnimationRunning)
            onSelectedX(true, objectY, 200);
    }

    void selectRight() {
        Utils.entry_log();
        if (!isAnimationRunning)
            onSelectedX(false, objectY, 200);
    }

    void selectTop() {
        Utils.entry_log();
        if (!isAnimationRunning)
            onSelectedY(true, objectX, 200);
    }

    void selectBottom() {
        Utils.entry_log();
        if (!isAnimationRunning)
            onSelectedY(false, objectX, 200);
    }


    private float getExitPoint(int exitXPoint) {
        Utils.entry_log();
        float[] x = new float[2];
        x[0] = objectX;
        x[1] = aPosX;

        float[] y = new float[2];
        y[0] = objectY;
        y[1] = aPosY;

        LinearRegression regression = new LinearRegression(x, y);

        //Your typical y = ax+b linear regression
        return (float) regression.slope() * exitXPoint + (float) regression.intercept();
    }

    private float getExitPointX(int exitYPoint) {
        Utils.entry_log();
        float[] x = new float[2];
        x[0] = objectX;
        x[1] = aPosX;

        float[] y = new float[2];
        y[0] = objectY;
        y[1] = aPosY;

        LinearRegression regression = new LinearRegression(x, y);

        //Your typical x = (y - b) / a linear regression
        return (float) ((exitYPoint - (float) regression.intercept()) / regression.slope());
    }

    private float getHorizontalExitRotation(boolean isLeft) {
        Utils.entry_log();
        float rotation = BASE_ROTATION_DEGREES * 2.f * (parentWidth - objectX) / parentWidth;
        if (touchPosition == TOUCH_BELOW) {
            rotation = -rotation;
        }
        if (isLeft) {
            rotation = -rotation;
        }
        return rotation;
    }

    private float getVerticalExitRotation(boolean isTop) {
        Utils.entry_log();
        float rotation = BASE_ROTATION_DEGREES * 2.f * (parentHeight - objectY) / parentHeight;
        if (touchPosition == TOUCH_BELOW) {
            rotation = -rotation;
        }
        if (isTop) {
            rotation = -rotation;
        }
        return rotation;
    }

    /**
     * When the object rotates it's width becomes bigger.
     * The maximum width is at 45 degrees.
     * <p/>
     * The below method calculates the width offset of the rotation.
     */
    private float getRotationWidthOffset() {
        Utils.entry_log();
        return objectW / MAX_COS - objectW;
    }


    public void setRotationDegrees(float degrees) {
        Utils.entry_log();
        this.BASE_ROTATION_DEGREES = degrees;
    }

    boolean isTouching() {
        Utils.entry_log();
        return this.mActivePointerId != INVALID_POINTER_ID;
    }

    PointF getLastPoint() {
        Utils.entry_log();
        return new PointF(this.aPosX, this.aPosY);
    }

    interface FlingListener {
        void onCardExited();

        void leftExit(Object dataObject);

        void rightExit(Object dataObject);

        void onClick(Object dataObject);

        void onScroll(float scrollProgressPercent);

        void topExit(Object dataObject);

        void bottomExit(Object dataObject);
    }

}





