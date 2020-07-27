package asm.uabierta.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import asm.uabierta.R;
import asm.uabierta.activities.ItemLostActivity;
import asm.uabierta.models.Lost;
import asm.uabierta.utils.Constants;
import asm.uabierta.utils.UserPreferences;
import asm.uabierta.utils.UtilsFunctions;

/**
 * Created by Alex on 27/07/2016.
 */
public class AdapterLosts extends RecyclerView.Adapter<AdapterLosts.LostViewHolder>{

    private ArrayList<Lost> data;
    private Context context;
    private UserPreferences uP;

    public class LostViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvUser, tvDate;
        FloatingActionButton btnEmail, btnPhone;
        ImageView imgItem;
        CardView cardV;
        LinearLayout clickLay, layButtons;

        LostViewHolder(View itemView) {
            super(itemView);

            layButtons = (LinearLayout) itemView.findViewById(R.id.layoutButtons);
            clickLay = (LinearLayout) itemView.findViewById(R.id.itemClickLay);
            cardV = (CardView) itemView.findViewById(R.id.card_view);
            tvTitle = (TextView)itemView.findViewById(R.id.title);
            tvUser = (TextView)itemView.findViewById(R.id.user);
            tvDate = (TextView)itemView.findViewById(R.id.date);
            btnEmail = (FloatingActionButton)itemView.findViewById(R.id.email);
            btnPhone = (FloatingActionButton)itemView.findViewById(R.id.phone);
            imgItem = (ImageView)itemView.findViewById(R.id.imageItem);
        }
    }

    public AdapterLosts(Context c, ArrayList<Lost> d){
        this.context = c;
        this.data = d;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public LostViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_items, viewGroup, false);
        LostViewHolder vh = new LostViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(LostViewHolder holder, int position) {
        final int pos = position;

        uP = new UserPreferences(context);

        if(data.get(pos).getUser().getId()==uP.getIntId()){
            holder.tvUser.setVisibility(View.INVISIBLE);
            holder.layButtons.setVisibility(View.GONE);
        }

        holder.clickLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ItemLostActivity.class);
                intent.putExtra(Constants.id, Integer.toString(data.get(pos).getId()));
                intent.putExtra(Constants.title, data.get(pos).getTitle());
                intent.putExtra(Constants.userId, Integer.toString(data.get(pos).getUser().getId()));
                v.getContext().startActivity(intent);
            }
        });

        holder.tvTitle.setText(data.get(pos).getTitle());
        holder.tvUser.setText(data.get(pos).getUser().getName());
        try {
            holder.tvDate.setText(data.get(pos).getStartDate() + " - " + data.get(pos).getEndDate());
        }catch (NullPointerException e){
            holder.tvDate.setText("");
        }

        holder.btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("PHONE ", data.get(pos).getUser().getPrefix().getPrefix()+" "+data.get(pos).getUser().getPhone());
                try {
                    UtilsFunctions.callPhone(v.getContext(), data.get(pos).getUser().getPrefix().getPrefix(), data.get(pos).getUser().getPhone());
                }catch (NullPointerException e){
                    UtilsFunctions.callPhone(v.getContext(), null, data.get(pos).getUser().getPhone());
                }
            }
        });

        holder.btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UtilsFunctions.callPhone(v.getContext(), data.get(pos).getUser().getPrefix().getPrefix(), data.get(pos).getUser().getPhone());
                }catch (NullPointerException e){
                    UtilsFunctions.callPhone(v.getContext(), null, data.get(pos).getUser().getPhone());
                }
            }
        });

        if(data.get(pos).getHasPhoto()==1) {
            Picasso.with(context)
                .load(data.get(pos).getImage())
                .resize(150, 150)
                //.fit()
                .centerCrop()
                .placeholder(R.drawable.no_image)
                .into(holder.imgItem);
        }
        else{
            Picasso.with(context)
                .load(R.drawable.no_image)
                .resize(150, 150)
                //.fit()
                .centerCrop()
                .into(holder.imgItem);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}