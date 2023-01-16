package com.example.mobilfinal.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilfinal.R;
import com.example.mobilfinal.adapters.MessageAdapter;
import com.example.mobilfinal.databinding.FragmentGalleryBinding;
import com.example.mobilfinal.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class GalleryFragment extends Fragment {

    //Mesaj Oluştur Sayfası

    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    // firebase ve firestore nesnelerimzi ioluşturudk
    EditText messageName, message;
    Button createMessageButton;
    RecyclerView messagesRecyclerView;

    ArrayList<Message> messageModelList;

    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        messageName = root.findViewById(R.id.editTextGroupName);
        message = root.findViewById(R.id.editTextDescription);
        createMessageButton = root.findViewById(R.id.buttonCreate);
        messagesRecyclerView = root.findViewById(R.id.rv_message);
        // oluşturduğumuz tüm nesneelri root yardımı ile layouta koyduğumuz widgetlarla bağladık

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        // firebase ve firestore nesnelerimizi oluşturduk

        messageModelList = new ArrayList<>();
        // mesaj model listemizi oluşturduk

        createMessageButton.setOnClickListener(v -> {
            String messageNameText = messageName.getText().toString();
            String messageText = message.getText().toString();
            // oluşturduğumuz mesaj ve mesaj ismini alıp string değişkenlerine atadık

            if (messageNameText.isEmpty()) {
                Toast.makeText(getContext(), "Mesaj Adı boş bırakılamaz", Toast.LENGTH_SHORT).show();
                return;
            }
            if (messageText.isEmpty()) {
                Toast.makeText(getContext(), "Mesaj boş bırakılamaz", Toast.LENGTH_SHORT).show();
                return;
            }
            createMessage(messageNameText, messageText);
            // oluşturduğumuz mesaj ve mesaj ismini createMessage fonksiyonuna gönderdik
        });
        fetchMessage();

        return root;
    }
        // oluşturduğumuz mesajı firebase firestore'a kaydettik
    private void createMessage(String messageNameText, String messageText) {

        String userId = mAuth.getCurrentUser().getUid();

        mStore.collection("/users/" + userId + "/messages").add(new HashMap<String, String>() {{
                    put("messageName", messageNameText);
                    put("message", messageText);
                }})
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Mesaj başarıyla oluşturuldu", Toast.LENGTH_SHORT).show();

                    documentReference.get().addOnSuccessListener(documentSnapshot -> {
                        Message messageModel =new Message( messageNameText, messageText,documentSnapshot.getId());
                        messageModelList.add(messageModel);
                        messagesRecyclerView.getAdapter().notifyItemInserted(messageModelList.size() - 1);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Mesaj oluşturulamadı", Toast.LENGTH_SHORT).show();
                });
    }
    // oluşturduğumuz mesajı firebase firestoredan geri çağırdık
    private void fetchMessage(){
        String userId = mAuth.getCurrentUser().getUid();
        mStore.collection("/users/" + userId + "/messages").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    messageModelList.clear();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Message messageModel = new Message(documentSnapshot.getString("messageName"), documentSnapshot.getString("message"), documentSnapshot.getId());
                        messageModelList.add(messageModel);
                    }
                    messagesRecyclerView.setAdapter(new MessageAdapter(messageModelList));
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    messagesRecyclerView.setLayoutManager(linearLayoutManager);

                });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}