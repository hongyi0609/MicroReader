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
import name.caiyao.microreader.bean.guokr.GuokrHotItem;
import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.ui.activity.ZhihuStoryActivity;
import name.caiyao.microreader.utils.DBUtils;
import name.caiyao.microreader.utils.ImageLoader;

/**
 * Created by 蔡小木 on 2016/4/27 0027.
 */
public class GuokrAdapter extends RecyclerView.Adapter<GuokrAdapter.GuokrViewHolder> {

    //解决item状态混乱问题
    private SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();

    private ArrayList<GuokrHotItem> guokrHotItems;
    private Context mContext;

    public GuokrAdapter(ArrayList<GuokrHotItem> guokrHotItems, Context context) {
        this.guokrHotItems = guokrHotItems;
        this.mContext = context;
    }

    @Override
    public GuokrViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GuokrViewHolder(LayoutInflater.from(mContext).inflate(R.layout.ithome_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final GuokrViewHolder holder, int position) {
        final GuokrHotItem guokrHotItem = guokrHotItems.get(holder.getAdapterPosition());
        if (DBUtils.getDB(mContext).isRead(Config.GUOKR, guokrHotItem.getId(), 1))
            holder.mTvTitle.setTextColor(Color.GRAY);
        else
            holder.mTvTitle.setTextColor(Color.BLACK);
        holder.mTvTitle.setText(guokrHotItem.getTitle());
        holder.mTvDescription.setText(guokrHotItem.getSummary());
        holder.mTvTime.setText(guokrHotItem.getTime());
        ImageLoader.loadImage(mContext, guokrHotItem.getSmallImage(), holder.mIvIthome);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBUtils.getDB(mContext).insertHasRead(Config.GUOKR, guokrHotItem.getId(), 1);
                holder.mTvTitle.setTextColor(Color.GRAY);
                Intent intent = new Intent(mContext, ZhihuStoryActivity.class);
                intent.putExtra("type", ZhihuStoryActivity.TYPE_GUOKR);
                intent.putExtra("id", guokrHotItem.getId());
                intent.putExtra("title", guokrHotItem.getTitle());
                mContext.startActivity(intent);
            }
        });
        if (mSparseBooleanArray.get(Integer.parseInt(guokrHotItem.getId()))){
            holder.btnDetail.setBackgroundResource(R.drawable.ic_expand_less_black_24px);
            holder.mTvDescription.setVisibility(View.VISIBLE);
        }else{
            holder.btnDetail.setBackgroundResource(R.drawable.ic_expand_more_black_24px);
            holder.mTvDescription.setVisibility(View.GONE);
        }
        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mTvDescription.getVisibility() == View.GONE) {
                    holder.btnDetail.setBackgroundResource(R.drawable.ic_expand_less_black_24px);
                    holder.mTvDescription.setVisibility(View.VISIBLE);
                    mSparseBooleanArray.put(Integer.parseInt(guokrHotItem.getId()), true);
                } else {
                    holder.btnDetail.setBackgroundResource(R.drawable.ic_expand_more_black_24px);
                    holder.mTvDescription.setVisibility(View.GONE);
                    mSparseBooleanArray.put(Integer.parseInt(guokrHotItem.getId()), false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return guokrHotItems.size();
    }

    class GuokrViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title)
        TextView mTvTitle;
        @BindView(R.id.iv_ithome)
        ImageView mIvIthome;
        @BindView(R.id.tv_description)
        TextView mTvDescription;
        @BindView(R.id.tv_time)
        TextView mTvTime;
        @BindView(R.id.btn_detail)
        Button btnDetail;

        GuokrViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
