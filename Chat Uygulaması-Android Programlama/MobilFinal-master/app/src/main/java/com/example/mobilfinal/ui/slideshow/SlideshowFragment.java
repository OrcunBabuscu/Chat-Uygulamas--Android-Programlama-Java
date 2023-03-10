package com.example.mobilfinal.ui.slideshow;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilfinal.R;
import com.example.mobilfinal.databinding.FragmentSlideshowBinding;
import com.example.mobilfinal.models.Contact;
import com.example.mobilfinal.models.Group;
import com.example.mobilfinal.ui.slideshow.adapters.ContactAdapter;
import com.example.mobilfinal.ui.slideshow.adapters.GroupAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SlideshowFragment extends Fragment {
    //Mesaj Oluştur sayfası toplu mesaj gönderme
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;

    RecyclerView recyclerView_addMember_groups, recyclerView_addMember_contacts;
    TextView addMember_groupName;
    //Gruplarımızı ve kişilerimizi tutacağımız listeler
    Group selectedGroup;
    ArrayList<Group> groupModelList;
    ArrayList<Contact> contactModelList;

    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        recyclerView_addMember_groups = root.findViewById(R.id.recyclerView_addMember_groups);
        recyclerView_addMember_contacts = root.findViewById(R.id.recyclerView_addMember_contacts);
        addMember_groupName = root.findViewById(R.id.addMember_groupName);

        groupModelList = new ArrayList<>();
        contactModelList = new ArrayList<>();

        ActivityResultLauncher launcher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGrant -> {
            // kişiler için izi istiyoruz
            if (isGrant) {
                // izin verildi
                fetchContacts();
            } else {
                // izin verilmedi
                Toast.makeText(getContext(), "Rehber izni gerekli", Toast.LENGTH_SHORT).show();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getContext().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // izin yok
            launcher.launch(Manifest.permission.READ_CONTACTS);
            // izin iste
        }
        else {
            // izin var
            fetchContacts();
        }
        //Gruplarımızı getiriyoruz
        fetchGroups();
        return root;
    }

    private void fetchGroups(){
        String userId = mAuth.getCurrentUser().getUid();

        mStore.collection("/users/" + userId + "/groups").get().addOnSuccessListener(queryDocumentSnapshots -> {
            groupModelList.clear();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                Group groupModel = new Group(documentSnapshot.getString("grupAdı"), documentSnapshot.getString("grupAciklamasi"),
                        documentSnapshot.getString("grupResmi"), (List<String>)documentSnapshot.get("numaralar"),documentSnapshot.getId());
                groupModelList.add(groupModel);
            }
            recyclerView_addMember_groups.setAdapter(new GroupAdapter(getContext(),groupModelList, position -> {
                selectedGroup = groupModelList.get(position);
                addMember_groupName.setText("Seçili Grup : "+ selectedGroup.getGroupName());
            }));
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_addMember_groups.setLayoutManager(linearLayoutManager);

        });
    }
    private void fetchContacts(){
        Cursor cursor = getContext().getContentResolver().query(android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        contactModelList.clear();
        while (cursor.moveToNext()) {
            //Kişileri alıyoruz
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER));
            @SuppressLint("Range") String photo = cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            //Kişileri listeye ekliyoruz
            Contact contactModel = new Contact(name, phoneNumber, photo);
            contactModelList.add(contactModel);
            //Kişileri recyclerviewe ekliyoruz
        }

        recyclerView_addMember_contacts.setAdapter(new ContactAdapter(getContext(),contactModelList, position -> {
            Contact contactModel = contactModelList.get(position);
            if (selectedGroup != null){
                //Kişiyi seçili gruba ekliyoruz
                new AlertDialog.Builder(getContext())
                        .setTitle("Kişiyi Gruba Ekle")
                        .setMessage(contactModel.getName() + " adlı kişiyi " + selectedGroup.getGroupName() + " grubuna eklemek istiyor musunuz?")
                        .setPositiveButton("Evet", (dialog, which) -> {
                            mStore.collection("/users/" + mAuth.getCurrentUser().getUid() + "/groups").document(selectedGroup.getGroupId()).update(new HashMap<String, Object>() {{
                                put("numaralar", FieldValue.arrayUnion(contactModel.getNumber()));
                            }}).addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Kişi Gruba Eklendi", Toast.LENGTH_SHORT).show();
                            });
                        })
                        .setNegativeButton("Hayır", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
                Toast.makeText(getContext(), contactModel.getName() + " " + contactModel.getNumber(), Toast.LENGTH_SHORT).show();
            }
        }));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView_addMember_contacts.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}