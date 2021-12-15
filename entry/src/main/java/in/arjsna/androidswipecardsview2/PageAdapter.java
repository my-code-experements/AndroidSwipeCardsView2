package in.arjsna.androidswipecardsview2;

import ohos.agp.components.*;
import ohos.app.Context;

import java.util.ArrayList;

/**
 * Created by arjun on 4/26/16.
 */
public class PageAdapter extends BaseItemProvider {
    private final ArrayList<Card> cards;
    private final LayoutScatter layoutInflater;


    public PageAdapter(Context context, ArrayList<Card> cards) {
        super();
        this.cards = cards;
        this.layoutInflater = LayoutScatter.getInstance(context);
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Card getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public Component getComponent(int position, Component component, ComponentContainer componentContainer) {
        Card card = cards.get(position);
        Component view = layoutInflater.parse(ResourceTable.Layout_page_item, null, false);
        ((Image) view.findComponentById(ResourceTable.Id_card_image)).setPixelMap(card.imageId);
        //        ((TextView)view.findViewById(R.id.helloText_1)).setText(card.name);
        //        ((TextView)view.findViewById(R.id.helloText)).setText(card.name);
        return view;
    }

//  @Override public View getView(int position, View convertView, ViewGroup parent) {
//    Card card = cards.get(position);
//    View view = layoutInflater.inflate(R.layout.page_item, parent, false);
//    ((ImageView) view.findViewById(R.id.card_image)).setImageResource(card.imageId);
//    //        ((TextView)view.findViewById(R.id.helloText_1)).setText(card.name);
//    //        ((TextView)view.findViewById(R.id.helloText)).setText(card.name);
//    return view;
//  }
//
//  @Override public Card getItem(int position) {
//    return cards.get(position);
//  }
//
//  @Override public int getCount() {
//    return cards.size();
//  }
}
