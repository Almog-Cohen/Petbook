package com.thegalos.petbook.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thegalos.petbook.objects.Pet;
import com.thegalos.petbook.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class AddFeed extends Fragment {


    // --Commented out by Inspection (20/06/2020 1:26):EditText etBreed, etName;
    private static List<Pet> petArrayList = new ArrayList<>();
    private static List<String> petNameList = new ArrayList<>();
    SharedPreferences preferences;
    private static final int PICK_IMAGE_REQUEST = 999;
    ImageView ivPhoto;
    Uri uri;
    ProgressBar progressBar;
    String imageLink;
    FirebaseUser user;
    DatabaseReference db;
    TextView etDetails;
    String selectedPet, postText;
    Spinner spnPet;
    Button btnPostFeed;
    ConstraintLayout postingLayout;
    boolean PhotoSelected = false;
    RadioGroup rgIsFree, rgWhoPays;
    EditText etAmount;
    boolean isFree = true;


    public AddFeed() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.add_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view,  Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        user = FirebaseAuth.getInstance().getCurrentUser();
        spnPet = view.findViewById(R.id.spnPet);
//        etBreed = view.findViewById(R.id.etBreed);
//        etName = view.findViewById(R.id.etName);
        progressBar = view.findViewById(R.id.progressBar);
        etDetails = view.findViewById(R.id.etDetails);
        btnPostFeed = view.findViewById(R.id.btnPostFeed);
        postingLayout = view.findViewById(R.id.postingLayout);
        rgIsFree = view.findViewById(R.id.rgIsFree);
        rgWhoPays = view.findViewById(R.id.rgWhoPays);
        etAmount = view.findViewById(R.id.etAmount);

        rgIsFree.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbFree) {
                    rgWhoPays.setVisibility(View.INVISIBLE);
                    rgWhoPays.clearCheck();
                    etAmount.setVisibility(View.INVISIBLE);
                    etAmount.setText("");
                    isFree = true;
                } else if (checkedId == R.id.rbPay) {
                    rgWhoPays.setVisibility(View.VISIBLE);
                    isFree = false;
                }
            }
        });
        rgWhoPays.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbToGet) {
                    etAmount.setVisibility(View.VISIBLE);
                    etAmount.setText("");
                } else if (checkedId == R.id.rbToPay) {
                    etAmount.setVisibility(View.VISIBLE);
                    etAmount.setText("");
                    rgWhoPays.setVisibility(View.VISIBLE);
                }
            }
        });


        loadData();
        ivPhoto = view.findViewById(R.id.tvAddPhoto);
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, petNameList);
        nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPet.setAdapter(nameAdapter);

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

                //method 1
//                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(i, PICK_IMAGE_REQUEST);




//                Intent intent = new Intent();
//                intent.setType("Image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        view.findViewById(R.id.btnPostFeed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etDetails.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please add details to your post", Toast.LENGTH_SHORT).show();
                    return;

                }
                if ((rgIsFree.getCheckedRadioButtonId() == R.id.rbPay)
                        && (rgWhoPays.getCheckedRadioButtonId() == R.id.rbToPay || rgWhoPays.getCheckedRadioButtonId() == R.id.rbToGet)
                        && etAmount.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please set Amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                showProgress(true);
//                addToStorage();

            }

        });
    }

    private void showProgress(Boolean show) {
        if (show){
            //        progressBar.setVisibility(View.VISIBLE);
            btnPostFeed.setEnabled(false);
            etDetails.setEnabled(false);
            spnPet.setEnabled(false);
            ivPhoto.setEnabled(false);
            postingLayout.setVisibility(View.VISIBLE);
            if (PhotoSelected)
                uploadFiles();
            else
                justPost();
        } else {
            btnPostFeed.setEnabled(true);
            etDetails.setEnabled(true);
            spnPet.setEnabled(true);
            ivPhoto.setEnabled(true);
            postingLayout.setVisibility(View.GONE);
        }

    }

    private void justPost() {
        db = FirebaseDatabase.getInstance().getReference().child("Posts").push();
        db.child("Time").setValue(ServerValue.TIMESTAMP);
        db.child("PostText").setValue(etDetails.getText().toString());
        db.child("SelectedPet").setValue(spnPet.getSelectedItem().toString());
        db.child("Pet").setValue(petArrayList.get(spnPet.getSelectedItemPosition()));
        db.child("Owner").setValue(user.getDisplayName());
        db.child("OwnerUID").setValue(user.getUid());
        db.child("ImageURL").setValue("null");
        if (!isFree){
            db.child("IsFree").setValue("no");
            if (rgWhoPays.getCheckedRadioButtonId() == R.id.rbToGet)
                db.child("WhoPays").setValue("user");
            else
                db.child("WhoPays").setValue("owner");
            db.child("Amount").setValue(etAmount.getText().toString());
        } else {
            db.child("IsFree").setValue("yes");
            db.child("WhoPays").setValue("free");
            db.child("Amount").setValue("0");
        }


//        Feed feed = new Feed();
//        feed.setSelectedPet(spnPet.getSelectedItem().toString());
//        feed.setPostText(etDetails.getText().toString());
//        feed.setOwnerUID(user.getUid());
//        feed.setPet(petArrayList.get(spnPet.getSelectedItemPosition()));
//        feed.setPostOwner(user.getDisplayName());
//        db.setValue(feed);
//        db.child("Time").setValue(ServerValue.TIMESTAMP);
//        //TODO if we add option to update name after Sign up we need to use getUID and make sure to load correct name in fragments
        showProgress(false);
        changeFragment();
        Toast.makeText(getContext(), "Uploaded no ", Toast.LENGTH_SHORT).show();

    }

    private void uploadFiles() {
        FirebaseStorage storage;
        StorageReference storageReference;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        if(uri != null)
        {
//            final ProgressDialog progressDialog = new ProgressDialog(getContext());
//            progressDialog.setTitle("Uploading...");
//            progressDialog.show();
            final StorageReference ref = storageReference.child(user.getUid()).child("images/"+ UUID.randomUUID().toString());
            ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            db = FirebaseDatabase.getInstance().getReference().child("Posts").push();
                            db.child("Time").setValue(ServerValue.TIMESTAMP);
                            db.child("PostText").setValue(etDetails.getText().toString());
                            db.child("SelectedPet").setValue(spnPet.getSelectedItem().toString());
                            db.child("Pet").setValue(petArrayList.get(spnPet.getSelectedItemPosition()));
                            db.child("Owner").setValue(user.getDisplayName());
                            db.child("OwnerUID").setValue(user.getUid());
//                            db.child("ImageURL").setValue("null");
                            if (!isFree){
                                db.child("IsFree").setValue("no");
                                if (rgWhoPays.getCheckedRadioButtonId() == R.id.rbToGet)
                                    db.child("WhoPays").setValue("user");
                                else
                                    db.child("WhoPays").setValue("owner");
                                db.child("Amount").setValue(etAmount.getText().toString());
                            } else {
                                db.child("IsFree").setValue("yes");
                                db.child("WhoPays").setValue("free");
                                db.child("Amount").setValue("0");
                            }
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageLink = (uri.toString());
                                    db.child("ImageURL").setValue(imageLink);
                                    Toast.makeText(getContext(), "image link: " + imageLink, Toast.LENGTH_LONG).show();
                                }
                            });
//                            db = FirebaseDatabase.getInstance().getReference().child("Posts").push();
//                            db.child("Time").setValue(ServerValue.TIMESTAMP);
//                            db.child("PostText").setValue(etDetails.getText().toString());
//                            db.child("SelectedPet").setValue(spnPet.getSelectedItem().toString());
//                            db.child("Pet").setValue(petArrayList.get(spnPet.getSelectedItemPosition()));
//                            db.child("Owner").setValue(user.getDisplayName());
//                            db.child("OwnerUID").setValue(user.getUid());
//                            if (!isFree){
//                                db.child("IsFree").setValue("no");
//                                if (rgWhoPays.getCheckedRadioButtonId() == R.id.rbToGet)
//                                    db.child("WhoPays").setValue("user");
//                                else
//                                    db.child("WhoPays").setValue("owner");
//
//                                db.child("Amount").setValue(etAmount.getText().toString());
//                            } else {
//                                db.child("IsFree").setValue("yes");
//                                db.child("WhoPays").setValue("free");
//                                db.child("Amount").setValue("0");
//                            }
                            showProgress(false);
                            changeFragment();
                            Toast.makeText(getContext(), "Uploaded with photo", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
                            showProgress(false);
                            Toast.makeText(getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
//                                    .getTotalByteCount());
//
////                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
//                        }
//                    });
        }
    }

    private void changeFragment() {
        progressBar.setVisibility(View.GONE);
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.flFragment, new MainFeed()).commit();
    }


    public void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            PhotoSelected = true;
            uri = data.getData();
            Glide.with(this).load(uri).into(ivPhoto);
            Toast.makeText(getContext(), "uri is: " + uri, Toast.LENGTH_SHORT).show();
            Log.d("URI_Galos", "uri is: " + uri);

        }
    }

    // load data
    private void loadData() {
        petNameList.clear();
        Gson gson = new Gson();
        String json = preferences.getString("PetList", null);
        Type type = new TypeToken<ArrayList<Pet>>() {
        }.getType();
        petArrayList = gson.fromJson(json, type);
        if (petArrayList == null) {
            petArrayList = new ArrayList<>();
        }
        for (Pet pet : petArrayList){
            petNameList.add(pet.getName());
        }
    }

}