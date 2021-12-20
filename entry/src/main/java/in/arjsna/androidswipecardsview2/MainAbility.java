package in.arjsna.androidswipecardsview2;

import in.arjsna.androidswipecardsview2.slice.MainAbilitySlice;
import in.arjsna.androidswipecardsview2.slice.PageSwipeAbilitySlice;
import in.arjsna.androidswipecardsview2.slice.TestSlice;
import in.arjsna.swipecardlib.SwipeCardView;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.window.dialog.ToastDialog;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.util.ArrayList;

public class MainAbility extends Ability {
    private ArrayList<Card> al;
    private CardsAdapter arrayAdapter;
    private int i;

    public SwipeCardView swipeCardView;
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0x00201, "-MainAbility-");

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
//         super.setMainRoute(PageSwipeAbilitySlice.class.getName());
         super.setMainRoute(MainAbilitySlice.class.getName());
//        super.setMainRoute(TestSlice.class.getName());

    }
}
