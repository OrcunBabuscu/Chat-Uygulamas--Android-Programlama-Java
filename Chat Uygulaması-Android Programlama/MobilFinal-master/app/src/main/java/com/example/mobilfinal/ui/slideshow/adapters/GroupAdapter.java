package com.example.mobilfinal.ui.slideshow.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobilfinal.OnClickItem;
import com.example.mobilfinal.R;
import com.example.mobilfinal.models.Contact;
import com.example.mobilfinal.models.Group;

import java.util.List;
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder>{
    Context context;
    List<Group> groupModelList;
    OnClickItem onClickItem;

    public GroupAdapter(Context context,List<Group> groupModelList, OnClickItem onClickItem){
        this.groupModelList = groupModelList;
        this.onClickItem = onClickItem;
        this.context= context;
    }
    @NonNull
    @Override
    public GroupAdapter.GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GroupViewHolder groupViewHolder = new GroupViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_addgroupmember_layout, parent, false), onClickItem);
        return groupViewHolder ;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.GroupViewHolder holder, int position) {
        Group groupModel = groupModelList.get(position);
        holder.setData(groupModel);
    }

    @Override
    public int getItemCount() {
        return groupModelList.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView groupImage;
        TextView groupName, groupExplanation;
        OnClickItem onClickItem;
        public GroupViewHolder(View itemView , OnClickItem onClickItem) {
            super(itemView);
            groupImage = itemView.findViewById(R.id.item_addMember_groupImage);
            groupName = itemView.findViewById(R.id.item_addMember_groupName);
            groupExplanation = itemView.findViewById(R.id.item_addMember_groupExplain);

            this.onClickItem = onClickItem;
            itemView.setOnClickListener(this);
        }
        public void setData(Group groupModel){
            groupName.setText(groupModel.getGroupName());
            groupExplanation.setText(groupModel.getGroupExplanation());
            if (groupModel.getGroupImage() != null){
                Glide.with(context).load(groupModel.getGroupImage()).into(groupImage);
            }
        }

        @Override
        public void onClick(View view) {
            onClickItem.onClickItem(getAdapterPosition());
        }
    }
}