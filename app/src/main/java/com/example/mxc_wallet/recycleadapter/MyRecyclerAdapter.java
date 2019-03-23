package com.example.mxc_wallet.recycleadapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mxc_wallet.R;

import java.text.DateFormat;
import java.util.Date;

import static com.example.mxc_wallet.MainActivity.mETHList;
import static com.example.mxc_wallet.MainActivity.mMXCList;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    private String mToken;

    /**
     * 事件回调監聽
     */
    private MyRecyclerAdapter.OnItemClickListener onItemClickListener;

    public MyRecyclerAdapter(String result) {
        this.mToken = result;
    }

    public void updateData(String result) {
        this.mToken = result;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    /**
     * 設置回調監聽
     *
     * @param listener
     */
    public void setOnItemClickListener(MyRecyclerAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_rv_item, parent, false);

        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // Bind data
        if (mToken.equals("ETH")) {
            long longTimeStamp = Long.parseLong(mETHList.get((mETHList.size()-1) - position).timeStamp);
            String timeStamp = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT)
                    .format(new Date(longTimeStamp * 1000));
            holder.mIv.setText(timeStamp);
        } else {
            long longTimeStamp = Long.parseLong(mMXCList.get((mETHList.size()-1) - position).timeStamp);
            String timeStamp = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT)
                    .format(new Date(longTimeStamp * 1000));
            holder.mIv.setText(timeStamp);
        }

        // Click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });

        // Long click
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemLongClick(holder.itemView, pos);
                }
                //表示此事件已经消费，不会触发单击事件
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mToken.equals("ETH")) {
            return mETHList == null ? 0 : mETHList.size();
        } else {
            return mMXCList == null ? 0 : mMXCList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mIv;

        public ViewHolder(View itemView) {
            super(itemView);
            mIv = (TextView) itemView.findViewById(R.id.item_iv);
        }
    }
}
