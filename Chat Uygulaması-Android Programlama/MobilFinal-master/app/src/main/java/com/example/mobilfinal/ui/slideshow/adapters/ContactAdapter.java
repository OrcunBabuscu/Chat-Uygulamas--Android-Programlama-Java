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

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private Context context;
    List<Contact> contactModelList;
    OnClickItem onClickItem;

    public ContactAdapter(Context context,List<Contact> contactModelList, OnClickItem onClickItem){
        this.contactModelList = contactModelList;
        this.onClickItem = onClickItem;
        this.context=context;
    }
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContactViewHolder contactViewHolder = new ContactViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_contacts_layout, parent, false), onClickItem);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contactModel = contactModelList.get(position);
        holder.setData(contactModel);
    }

    @Override
    public int getItemCount() {
        return contactModelList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView contactImage;
        OnClickItem onClickItem;
        TextView contactName, contactNumber;
        public ContactViewHolder(View itemView , OnClickItem onClickItem) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.item_addMember_contactImage);
            contactName = itemView.findViewById(R.id.item_addMember_contactName);
            contactNumber = itemView.findViewById(R.id.item_addMember_contactNumber);

            this.onClickItem = onClickItem;
            itemView.setOnClickListener(this);
        }
        public void setData(Contact contactModel){
            contactName.setText(contactModel.getName());
            contactNumber.setText(contactModel.getNumber());

            if (contactModel.getPhoto() != null){
                Glide.with(context).load(contactModel.getPhoto()).into(contactImage);

            }
        }

        @Override
        public void onClick(View view) {
            onClickItem.onClickItem(getAdapterPosition());
        }
    }
}