package ru.nsk.decentury.bonuses;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ru.nsk.decentury.bonuses.hyperledger.Buyer;

public class BonusesListAdapter extends RecyclerView.Adapter<BonusesListAdapter.BonusesListViewHolder> {
    private List<Buyer.BonusBalance> bonuses;

    public static class BonusesListViewHolder extends RecyclerView.ViewHolder {
        public TextView bonusesText;
        public TextView bonusesNumber;

        public BonusesListViewHolder(View v) {
            super(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Element  " + getAdapterPosition() + " clicked.");
                }
            });

            bonusesText = (TextView) v.findViewById(R.id.bonuses_text);
            bonusesNumber = (TextView) v.findViewById(R.id.bonuses_number);
        }
    }

    public BonusesListAdapter(List<Buyer.BonusBalance> bonusesList) {
        bonuses = bonusesList;
    }

    @Override
    public BonusesListAdapter.BonusesListViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bonuses_list_layout, parent, false);

        BonusesListViewHolder vh = new BonusesListViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(BonusesListViewHolder holder, int position) {
        Buyer.BonusBalance bonusBalance = bonuses.get(position);
        holder.bonusesText.setText(bonusBalance.store);
        holder.bonusesNumber.setText(String.valueOf(bonusBalance.tokenBalance));
    }

    @Override
    public int getItemCount() {
        return bonuses.size();
    }
}
