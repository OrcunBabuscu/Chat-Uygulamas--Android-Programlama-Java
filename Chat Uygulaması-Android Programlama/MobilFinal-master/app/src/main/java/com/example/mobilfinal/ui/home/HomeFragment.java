package com.example.mobilfinal.ui.home;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobilfinal.R;
import com.example.mobilfinal.adapters.GroupAdapter;
import com.example.mobilfinal.databinding.FragmentHomeBinding;
import com.example.mobilfinal.models.Group;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HomeFragment extends Fragment {

    //Grup Oluştur Sayfası
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    FirebaseStorage mStorage;
    // firebase ve firestore nesnelerimzi ioluşturudk
    ArrayList<Group> groupModelArrayList;
    EditText editTextGroupName,editTextDescription;
    ImageView iv_GroupIcon;
    Button buttonCreate;
    RecyclerView rv_group;

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // oluşturduğumuz tüm nesneelri root yardımı ile layouta koyduğumuz widgetlarla bağladık
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        editTextGroupName = root.findViewById(R.id.editTextGroupName);
        editTextDescription = root.findViewById(R.id.editTextDescription);
        iv_GroupIcon = root.findViewById(R.id.iv_GroupIcon);
        buttonCreate = root.findViewById(R.id.buttonCreate);
        rv_group = root.findViewById(R.id.rv_group);
        groupModelArrayList = new ArrayList<>();
        groupModelArrayList = new ArrayList<>();
        // grup oluştur sayfasındaki nesneleri tanımladık
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            // resultLauncher ile resim seçme işlemini gerçekleştirdik
            if (result.getResultCode() == RESULT_OK) {
                filePath = result.getData().getData();
                iv_GroupIcon.setImageURI(filePath);
            }
        });
        iv_GroupIcon.setOnClickListener(v -> {
            // resim seçme işlemini gerçekleştirdik
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResultLauncher.launch(intent);
        });

        buttonCreate.setOnClickListener(v -> {
            // grup oluşturma işlemini gerçekleştirdik
            String name = editTextGroupName.getText().toString();
            String explanation = editTextDescription.getText().toString();
            if (name.isEmpty() ) {
                Toast.makeText(getContext(), "Grup adı boş bırakılamaz", Toast.LENGTH_SHORT).show();
                return;
            }
            if ( explanation.isEmpty()) {
                Toast.makeText(getContext(), "Grup açıklaması boş bırakılamaz", Toast.LENGTH_SHORT).show();
                return;
            }

            if (filePath != null) {
                // resim seçilmişse
                StorageReference storageReference = mStorage.getReference().child("images" + UUID.randomUUID().toString());
                storageReference.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                    // resim yüklendiğinde urlini alıp firestore a kaydettik
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        Toast.makeText(getContext(), "Resim yüklendi", Toast.LENGTH_SHORT).show();
                        createGroup(name, explanation, imageUrl);
                    });
                });
            }
            else{
                createGroup(name, explanation, null);
            }
        });
        FetchGroup();

        return root;
    }

    private  void createGroup(String name, String explanation, String imageUrl) {
        // grup oluşturma işlemini gerçekleştirdik
        String userId = mAuth.getCurrentUser().getUid();

        mStore.collection("/users/" + userId + "/groups").add(new HashMap<String, Object>() {
            {
                // grup oluşturulduğunda firestore a kaydettik
                put("grupAdı", name);
                put("grupAciklamasi", explanation);
                put("grupResmi", imageUrl);
                put("numaralar", new ArrayList<String>());
            }
        }).addOnSuccessListener(documentReference -> {
            Toast.makeText(getContext(), "Grup oluşturuldu", Toast.LENGTH_SHORT).show();
            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                Group groupModel = new Group( name, explanation, imageUrl,(List<String>)documentSnapshot.get("numaralar") ,documentSnapshot.getId());
                groupModelArrayList.add(groupModel);
                rv_group.getAdapter().notifyItemInserted(groupModelArrayList.size() - 1);
            });

        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Grup oluşturulamadı", Toast.LENGTH_SHORT).show();
        });

    }

    private void FetchGroup(){
        // oluşturulan grupları firestore dan çekip recyclerview a ekledik
        String userId = mAuth.getCurrentUser().getUid();
        mStore.collection("/users/" + userId + "/groups").get().addOnSuccessListener(queryDocumentSnapshots -> {
            groupModelArrayList.clear();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                Group groupModel = new Group(documentSnapshot.getString("grupAdı"), documentSnapshot.getString("grupAciklamasi"),
                        documentSnapshot.getString("grupResmi"), (List<String>)documentSnapshot.get("numaralar"),documentSnapshot.getId());
                groupModelArrayList.add(groupModel);
            }

            rv_group.setAdapter(new GroupAdapter(getActivity(),groupModelArrayList));
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            rv_group.setLayoutManager(linearLayoutManager);

        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}