package com.example.mobilfinal.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilfinal.R;
import com.example.mobilfinal.models.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    // burası message adapterı recylerviewin yani mesaj listesinin bağlı olduğu adaptör
    List<Message> messageModelList;

    public MessageAdapter(List<Message> messageModelList) {
        this.messageModelList = messageModelList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageViewHolder messageViewHolder = new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_message_layout, parent, false));
        return messageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message messageModel = messageModelList.get(position);
        holder.setData(messageModel);
        //message listesinin o pozisyondaki elemanını bir mesaj modeline atıyoruz.
    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageName, message;
        public MessageViewHolder( View itemView) {
            super(itemView);
            messageName = itemView.findViewById(R.id.groupName);
            message = itemView.findViewById(R.id.groupDescription);

        }
        public void setData(Message messageModel) {
            //mesaj modelimizdeki verileri textviewlarımıza işliyoruz
            messageName.setText(messageModel.getMessageName());
            message.setText(messageModel.getMessage());
        }
    }
}