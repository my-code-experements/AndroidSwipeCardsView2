package in.arjsna.androidswipecardsview2.slice;

import in.arjsna.androidswipecardsview2.Card;
import in.arjsna.androidswipecardsview2.CardsAdapter;
import in.arjsna.androidswipecardsview2.ResourceTable;
import in.arjsna.swipecardlib.SwipeCardView;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.window.dialog.ToastDialog;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.util.ArrayList;

public class MainAbilitySlice extends AbilitySlice {
    private ArrayList<Card> al;
    private CardsAdapter arrayAdapter;
    private int i;

    public SwipeCardView swipeCardView;
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0x00201, "-MainAbility-");

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        setUIContent(ResourceTable.Layout_ability_main);
        al = new ArrayList<>();
        getDummyData(al);
        arrayAdapter = new CardsAdapter(this, al);
        try {
            swipeCardView = (SwipeCardView) findComponentById(ResourceTable.Id_card_stack_view);
            swipeCardView.setAdapter(arrayAdapter);

            swipeCardView.setFlingListener(new SwipeCardView.OnCardFlingListener() {
                @Override
                public void onCardExitLeft(Object dataObject) {
                    makeToast("Left !");
                }

                @Override
                public void onCardExitRight(Object dataObject) {
                    makeToast("Right !");
                }

                @Override
                public void onAdapterAboutToEmpty(int itemsInAdapter) {

                }

                @Override
                public void onScroll(float scrollProgressPercent) {

                }

                @Override
                public void onCardExitTop(Object dataObject) {
                    makeToast("Top !");
                }

                @Override
                public void onCardExitBottom(Object dataObject) {
                    makeToast("Bottom !");
                }
            });

            swipeCardView.setOnItemClickListener(new SwipeCardView.OnItemClickListener() {
                @Override
                public void onItemClicked(int itemPosition, Object dataObject) {
                    makeToast("itemPosition: " + itemPosition);
                }
            });

            findComponentById(ResourceTable.Id_left).setClickedListener(c -> {
                try {
                    HiLog.debug(LABEL_LOG, "swipeCardView " + swipeCardView);
                    swipeCardView.throwLeft();

                } catch (Exception ex) {
                    HiLog.debug(LABEL_LOG, "throwLeft  " + ex);
                    for (StackTraceElement st : ex.getStackTrace()) {
                        HiLog.debug(LABEL_LOG, "" + st);

                    }
                }
            });

            findComponentById(ResourceTable.Id_right).setClickedListener(c -> {
                swipeCardView.throwRight();
            });
            findComponentById(ResourceTable.Id_top).setClickedListener(c -> {
                swipeCardView.throwTop();
            });
            findComponentById(ResourceTable.Id_bottom).setClickedListener(c -> {
                swipeCardView.throwBottom();
            });
            findComponentById(ResourceTable.Id_restart).setClickedListener(c -> {
                swipeCardView.restart();
            });
            findComponentById(ResourceTable.Id_position).setClickedListener(c -> {
                int pos = swipeCardView.getCurrentPosition();
                makeToast("Pos: " + pos);
            });

        } catch (Exception ex) {
            HiLog.debug(LABEL_LOG, "Exception" + ex);
            for (StackTraceElement st : ex.getStackTrace()) {
                HiLog.debug(LABEL_LOG, "" + st);

            }
        }
    }


    private void getDummyData(ArrayList<Card> al) {
        Card card = new Card();
        card.name = "Card : 1";
        card.imageId = ResourceTable.Media_faces1;
        al.add(card);

        Card card2 = new Card();
        card2.name = "Card : 2";
        card2.imageId = ResourceTable.Media_faces2;
        al.add(card2);
        Card card3 = new Card();
        card3.name = "Card : 3";
        card3.imageId = ResourceTable.Media_faces3;
        al.add(card3);
        Card card4 = new Card();
        card4.name = "Card : 4";
        card4.imageId = ResourceTable.Media_faces4;
        al.add(card4);
    }

    void makeToast(String msg) {
        new ToastDialog(getContext()).setText(msg).setDuration(1000).show();
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
