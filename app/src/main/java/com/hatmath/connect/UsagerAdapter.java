package com.hatmath.connect;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class UsagerAdapter extends ArrayAdapter<UsagerListItem> {

    public UsagerAdapter(Context context, List<UsagerListItem> items) {
        super(context, 0, items);
    }

    private int selectedItemPosition = -1;  // -1 signifie qu'aucun élément n'est sélectionné
    public void setSelectedItemPosition(int position) {
        this.selectedItemPosition = position;
    }

    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.liste_usagers, parent, false);
        }

        UsagerListItem item = getItem(position);

        TextView textView = convertView.findViewById(R.id.text);
        ImageView imageView = convertView.findViewById(R.id.icon);

        textView.setText(item.getText());
        imageView.setImageResource(item.getIconResId());

        // Mise à jour de l'arrière-plan en fonction de l'état de sélection
        if (position == getSelectedItemPosition()) {
            convertView.setBackgroundResource(R.drawable.list_item_selector);
        } else {
            convertView.setBackgroundColor(Color.BLACK);
        }

        return convertView;
    }
}
