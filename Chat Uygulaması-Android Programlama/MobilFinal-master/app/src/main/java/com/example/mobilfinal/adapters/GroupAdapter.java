package com.example.mobilfinal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobilfinal.R;
import com.example.mobilfinal.models.Group;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private Context context;
    List<Group> groupModelList;

    public GroupAdapter(Context context,List<Group> groupModelList){
        this.groupModelList = groupModelList;
        this.context= context;
    }
    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GroupViewHolder groupViewHolder = new GroupViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_groups_layout, parent, false));
        // Bu adapter row_groups_layout dosyasının bağlı oldu recyclerviewı doldurur ve onun işlemlerini yapar
        return groupViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group groupModel = groupModelList.get(position);
        holder.setData(groupModel);
    }

    @Override
    public int getItemCount() {
        return groupModelList.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        ImageView groupImage;
        TextView groupName, groupExplanation;
        public GroupViewHolder(View itemView) {
            super(itemView);
            groupImage = itemView.findViewById(R.id.groupImage);
            groupName = itemView.findViewById(R.id.groupName);
            groupExplanation = itemView.findViewById(R.id.groupDescription);
        }
        public void setData(Group groupModel){
            //adaptörümüze gönderidğimiz verileri burada set ediyoruz
            groupName.setText(groupModel.getGroupName());
            groupExplanation.setText(groupModel.getGroupExplanation());

            if (groupModel.getGroupImage() != null){
                Glide.with(context).load(groupModel.getGroupImage()).into(groupImage);
                //image i imageview a bu şekilde yerleştiriyoruz
            }
        }
    }
}
