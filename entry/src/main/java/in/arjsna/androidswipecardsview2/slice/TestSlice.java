package in.arjsna.androidswipecardsview2.slice;

import in.arjsna.androidswipecardsview2.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.multimodalinput.event.MmiPoint;
import ohos.multimodalinput.event.MouseEvent;
import ohos.multimodalinput.event.TouchEvent;

public class TestSlice extends AbilitySlice {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0x00201, "-MainAbility-");

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        setUIContent(ResourceTable.Layout_test_layout);

        (findComponentById(ResourceTable.Id_container)).setTouchEventListener((component, touchEvent) -> {
            HiLog.debug(LABEL_LOG, "onStart:touchEvent " + touchEvent);
            int action = touchEvent.getAction();
            switch (action) {
                case TouchEvent.POINT_MOVE: {
                    MmiPoint m = touchEvent.getPointerPosition(touchEvent.getPointerCount() - 1);
                    HiLog.debug(LABEL_LOG, "onStart: MOVE " + m);
                    ((Text)findComponentById(ResourceTable.Id_text)).setText(
                            String.format("x:%.2f, y:%.2f", m.getX(),m.getY())
                    );
                    break;
                }
                default: {
                    HiLog.debug(LABEL_LOG, "onStart: " + action);
                    break;
                }
            }
            return true;
        });
    }

    static class TestView extends DirectionalLayout implements Component.TouchEventListener, Component.EstimateSizeListener {
        public TestView(Context context) {
            super(context);
            init();
        }

        public TestView(Context context, AttrSet attrSet) {
            super(context, attrSet);
            init();
        }

        public TestView(Context context, AttrSet attrSet, String styleName) {
            super(context, attrSet, styleName);
            init();
        }

        void init() {
            setTouchEventListener(this);
        }

        private static final HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0x00201, "-MainAbility-");

        @Override
        public boolean onTouchEvent(Component component, TouchEvent touchEvent) {
            HiLog.debug(LABEL_LOG, "onTouchEvent");
            return false;
        }

        @Override
        public boolean onEstimateSize(int widthEstimateConfig,
                                      int heightEstimateConfig) {
            setEstimatedSize(
                    EstimateSpec.getChildSizeWithMode(400, widthEstimateConfig, widthEstimateConfig),
                    EstimateSpec.getChildSizeWithMode(400, heightEstimateConfig, heightEstimateConfig)
            );
            return true;
        }
    }
}
