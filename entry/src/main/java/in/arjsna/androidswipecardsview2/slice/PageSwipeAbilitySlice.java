package in.arjsna.androidswipecardsview2.slice;

import in.arjsna.androidswipecardsview2.Card;
import in.arjsna.androidswipecardsview2.PageAdapter;
import in.arjsna.androidswipecardsview2.ResourceTable;
import in.arjsna.swipecardlib.SwipePageView;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.window.dialog.ToastDialog;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.util.ArrayList;

public class PageSwipeAbilitySlice extends AbilitySlice {
    private ArrayList<Card> al;
    private PageAdapter arrayAdapter;
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0x00201, "-MainAbility-");

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        try {
            setUIContent(ResourceTable.Layout_activity_page_swipe);
            SwipePageView flingContainer = (SwipePageView) findComponentById(ResourceTable.Id_page_swipe_view);
            al = new ArrayList<>();
            getDummyData(al);

            arrayAdapter = new PageAdapter(this, al);
            flingContainer.setAdapter(arrayAdapter);
            flingContainer.setFlingListener(new SwipePageView.OnPageFlingListener() {
                @Override
                public void onAdapterAboutToEmpty(int itemsInAdapter) {
                    makeToast("onAdapterAboutToEmpty");

                }

                @Override
                public void onScroll(float scrollProgressPercent) {
                    makeToast("onScroll");
                }

                @Override
                public void onTopCardExit(Object dataObject) {
                    makeToast("onTopCardExit");
                }

                @Override
                public void onBottomCardExit(Object dataObject) {
                    makeToast("onBottomCardExit");
                }
            });

            flingContainer.setOnItemClickListener((itemPosition, dataObject) -> {
                makeToast("Pos: " + itemPosition + " Card: " + dataObject);
            });
        } catch (Exception ex) {
            HiLog.debug(LABEL_LOG, "Exception" + ex);
            for (StackTraceElement st : ex.getStackTrace()) {
                HiLog.debug(LABEL_LOG, "" + st);

            }
        }
    }

    void makeToast(String msg) {
        new ToastDialog(getContext()).setText(msg).setDuration(1000).show();
    }

    private void getDummyData(ArrayList<Card> al) {
        Card card = new Card();
        card.name = "John";
        card.imageId = ResourceTable.Media_faces1;
        al.add(card);

        Card card2 = new Card();
        card2.name = "Mike";
        card2.imageId = ResourceTable.Media_faces2;
        al.add(card2);
        Card card3 = new Card();
        card3.name = "Ronoldo";
        card3.imageId = ResourceTable.Media_faces3;
        al.add(card3);
        Card card4 = new Card();
        card4.name = "Messi";
        card4.imageId = ResourceTable.Media_faces4;
        al.add(card4);
        Card card5 = new Card();
        card5.name = "Sachin";
        card5.imageId = ResourceTable.Media_faces5;
        al.add(card5);
        Card card56 = new Card();
        card56.name = "Dhoni";
        card56.imageId = ResourceTable.Media_faces6;
        al.add(card56);
        Card card7 = new Card();
        card7.name = "Kohli";
        card7.imageId = ResourceTable.Media_faces7;
        al.add(card7);
        Card card8 = new Card();
        card8.name = "Pandya";
        card8.imageId = ResourceTable.Media_faces8;
        al.add(card8);
        Card card9 = new Card();
        card9.name = "Nehra";
        card9.imageId = ResourceTable.Media_faces9;
        al.add(card9);
        Card card10 = new Card();
        card10.name = "Bumra";
        card10.imageId = ResourceTable.Media_faces10;
        al.add(card10);
        Card card11 = new Card();
        card11.name = "Rohit";
        card11.imageId = ResourceTable.Media_faces11;
        al.add(card11);
    }
}
