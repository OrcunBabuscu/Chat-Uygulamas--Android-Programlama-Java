package com.example.mobilfinal.ui.messageSend;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilfinal.models.Group;
import com.example.mobilfinal.models.Message;
import com.example.mobilfinal.ui.messageSend.adapters.MessageAdapter;
import com.example.mobilfinal.ui.slideshow.adapters.GroupAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import com.example.mobilfinal.R;


public class MessageSendFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseFirestore mStore;

    RecyclerView recyclerView_sendMessage_groups, recyclerView_sendMessage_messages;
    TextView sendMessage_selectedGroup, sendMessage_selectedMessage;
    Button sendMessage_btn_sendMessage;

    ArrayList<Group> groupModelList;
    ArrayList<Message> messageModelList;

    Group selectedGroup;
    Message selectedMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_send, container, false);


        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        recyclerView_sendMessage_groups = view.findViewById(R.id.recyclerView_sendMessage_groups);
        recyclerView_sendMessage_messages = view.findViewById(R.id.recyclerView_sendMessage_messages);
        sendMessage_selectedGroup = view.findViewById(R.id.sendMessage_selectedGroup);
        sendMessage_selectedMessage = view.findViewById(R.id.sendMessage_selectedMessage);
        sendMessage_btn_sendMessage = view.findViewById(R.id.sendMessage_btn_sendMessage);

        groupModelList = new ArrayList<>();
        messageModelList = new ArrayList<>();

        ActivityResultLauncher launcher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGrant -> {
            if (isGrant) {
                sendSMS();
            } else {
                Toast.makeText(getContext(), "Sms gönderme izni gerekli", Toast.LENGTH_SHORT).show();
            }
        });

        sendMessage_btn_sendMessage.setOnClickListener(v->{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getContext().checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                launcher.launch(Manifest.permission.SEND_SMS);
            }
            else {
                sendSMS();
            }
        });


        fetchGroups();
        fetchMessages();
        return view;
    }

    private void fetchGroups(){
        String uid = mAuth.getCurrentUser().getUid();

        mStore.collection("users").document(uid).collection("groups").get().addOnSuccessListener(queryDocumentSnapshots -> {
            groupModelList.clear();
            for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                Group groupModel =  new Group(documentSnapshot.getString("grupAdı"), documentSnapshot.getString("grupAciklamasi"),
                        documentSnapshot.getString("grupResmi"), (List<String>)documentSnapshot.get("numaralar"),documentSnapshot.getId());
                groupModelList.add(groupModel);
            }
            recyclerView_sendMessage_groups.setAdapter(new GroupAdapter(getActivity(),groupModelList, position -> {
                selectedGroup = groupModelList.get(position);
                sendMessage_selectedGroup.setText("Seçili Grup : "+selectedGroup.getGroupName());
            }));
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
            recyclerView_sendMessage_groups.setLayoutManager(linearLayoutManager);
        });
    }

    private void fetchMessages(){
        String uid = mAuth.getCurrentUser().getUid();

        mStore.collection("users").document(uid).collection("messages").get().addOnSuccessListener(queryDocumentSnapshots -> {
            messageModelList.clear();
            for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                Message messageModel =  new Message(documentSnapshot.getString("messageName"), documentSnapshot.getString("message"),documentSnapshot.getId());
                messageModelList.add(messageModel);
            }
            recyclerView_sendMessage_messages.setAdapter(new MessageAdapter(messageModelList, position -> {
                selectedMessage = messageModelList.get(position);
                sendMessage_selectedMessage.setText("Seçili Mesaj : "+selectedMessage.getMessageName());
            }));
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
            recyclerView_sendMessage_messages.setLayoutManager(linearLayoutManager);
        });
    }

    private void sendSMS(){
        if (selectedGroup == null && selectedMessage == null){
            Toast.makeText(getContext(), "Lütfen grup ve mesaj seçiniz", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedGroup.getNumbers() != null && selectedGroup.getNumbers().size() > 0) {
            SmsManager smsManager = SmsManager.getDefault();
            for (String number : selectedGroup.getNumbers()) {
                smsManager.sendTextMessage(number, null, selectedMessage.getMessage(), null, null);
            }
            Toast.makeText(getContext(), "Mesajlar gönderildi", Toast.LENGTH_SHORT).show();
        }
    }
}