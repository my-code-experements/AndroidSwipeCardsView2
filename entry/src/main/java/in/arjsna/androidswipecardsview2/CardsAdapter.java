package in.arjsna.androidswipecardsview2;


import ohos.agp.components.*;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.util.ArrayList;

/**
 * Created by arjun on 4/25/16.
 */
public class CardsAdapter extends BaseItemProvider {
    private final ArrayList<Card> cards;
    private final LayoutScatter layoutScatter;

    public CardsAdapter(Context context, ArrayList<Card> cards) {

        this.cards = cards;
        this.layoutScatter = LayoutScatter.getInstance(context);
    }

    private static final HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0x00201, "-MainAbility-");

    @Override
    public Component getComponent(int position, Component component, ComponentContainer componentContainer) {
        Card card = cards.get(position);
        Component view = layoutScatter.parse(ResourceTable.Layout_item, null, false);
        ((Image) view.findComponentById(ResourceTable.Id_card_image)).setImageAndDecodeBounds(card.imageId);
        ((Text) view.findComponentById(ResourceTable.Id_helloText)).setText(card.name);
        return view;
    }

//  @Override public View getView(int position, View convertView, ViewGroup parent) {
//    Card card = cards.get(position);
//    View view = layoutInflater.inflate(R.layout.item, parent, false);
//    ((ImageView) view.findViewById(R.id.card_image)).setImageResource(card.imageId);
//    ((TextView) view.findViewById(R.id.helloText)).setText(card.name);
//    return view;
//  }

    @Override
    public Card getItem(int position) {
        return cards.get(position);
    }

    @Override
    public int getCount() {
        return cards.size();
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }

}
