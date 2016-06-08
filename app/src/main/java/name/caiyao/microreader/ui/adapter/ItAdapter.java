package name.caiyao.microreader.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.bean.itHome.ItHomeItem;
import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.ui.activity.ItHomeActivity;
import name.caiyao.microreader.utils.DBUtils;
import name.caiyao.microreader.utils.ImageLoader;

/**
 * Created by 蔡小木 on 2016/4/29 0029.
 */
public class ItAdapter extends RecyclerView.Adapter<ItAdapter.ItViewHolder> {

    //解决item状态混乱问题
    private SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();

    private ArrayList<ItHomeItem> itHomeItems;
    private Context mContext;

    public ItAdapter(Context context, ArrayList<ItHomeItem> itHomeItems) {
        this.itHomeItems = itHomeItems;
        this.mContext = context;
    }

    @Override
    public ItViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItViewHolder(LayoutInflater.from(mContext).inflate(R.layout.ithome_item, parent, false));
    }


    @Override
    public void onBindViewHolder(final ItViewHolder holder,int position) {
        final ItHomeItem itHomeItem = itHomeItems.get(holder.getAdapterPosition());
        if (DBUtils.getDB(mContext).isRead(Config.IT, itHomeItem.getNewsid(), 1))
            holder.tvTitle.setTextColor(Color.GRAY);
        else
            holder.tvTitle.setTextColor(Color.BLACK);
        holder.tvTitle.setText(itHomeItem.getTitle());
        holder.tvTime.setText(itHomeItem.getPostdate());
        holder.tvDescription.setText(itHomeItem.getDescription());
        ImageLoader.loadImage(mContext, itHomeItem.getImage(), holder.ivIthome);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBUtils.getDB(mContext).insertHasRead(Config.IT, itHomeItem.getNewsid(), 1);
                holder.tvTitle.setTextColor(Color.GRAY);
                mContext.startActivity(new Intent(mContext, ItHomeActivity.class)
                        .putExtra("item", itHomeItem));
            }
        });
        if (mSparseBooleanArray.get(Integer.parseInt(itHomeItem.getNewsid()))){
            holder.btnDetail.setBackgroundResource(R.drawable.ic_expand_less_black_24px);
            holder.tvDescription.setVisibility(View.VISIBLE);
        }else{
            holder.btnDetail.setBackgroundResource(R.drawable.ic_expand_more_black_24px);
            holder.tvDescription.setVisibility(View.GONE);
        }
        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.tvDescription.getVisibility() == View.GONE) {
                    holder.btnDetail.setBackgroundResource(R.drawable.ic_expand_less_black_24px);
                    holder.tvDescription.setVisibility(View.VISIBLE);
                    mSparseBooleanArray.put(Integer.parseInt(itHomeItem.getNewsid()),true);
                } else {
                    holder.btnDetail.setBackgroundResource(R.drawable.ic_expand_more_black_24px);
                    holder.tvDescription.setVisibility(View.GONE);
                    mSparseBooleanArray.put(Integer.parseInt(itHomeItem.getNewsid()),false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itHomeItems.size();
    }

    class ItViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_ithome)
        ImageView ivIthome;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_description)
        TextView tvDescription;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.btn_detail)
        Button btnDetail;

        ItViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
