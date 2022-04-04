package com.podium.pawz.ui.create;

import static com.podium.pawz.Constants.REQUEST_IMAGE_GET;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.podium.pawz.Constants;
import com.podium.pawz.R;
import com.podium.pawz.Util;
import com.podium.pawz.databinding.FragmentCreateBinding;
import com.podium.pawz.models.Animal;

import org.jetbrains.annotations.NotNull;

public class CreateFragment extends Fragment {

    private FragmentCreateBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private Uri fullPhotoUri;
    private Location location;
    private FirebaseUser profile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCreateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        profile = auth.getCurrentUser();
        binding.uploadImg.setOnClickListener(view1 -> selectImage());
        binding.shareBtn.setEnabled(false);
        getCurrentLocation();
        binding.shareBtn.setOnClickListener(view1 -> {
            String title = binding.editTitle.getText().toString().toLowerCase().trim();
            String petType = binding.editPetType.getText().toString().toLowerCase().trim();
            String sender = binding.editUsername.getText().toString().toLowerCase().trim();
            String phone = binding.editPhone.getText().toString().toLowerCase().trim();
            String detail = binding.editDetail.getText().toString().toLowerCase().trim();
            binding.shareBtn.setEnabled(false);
            if (title.length() >= 4) {
                if (petType.length() >= 2) {
                    if (sender.length() >= 3) {
                        if (phone.length() >= 10 && phone.length() < 15) {
                            String imageStr = Util.encodeImgToString(getActivity(), fullPhotoUri);
                            Animal data = new Animal(imageStr, phone, petType, title, detail, sender, profile.getUid(), location.getLatitude(), location.getLongitude());
                            db.collection(Constants.ANIMAL).add(data).addOnSuccessListener(documentReference -> {
                                showDiaglog("Success", "Animal details are added to database. Rescue will reach soon. If you need to hurry, you can call NGO available to other tab ", "ok");
                                NavHostFragment.findNavController(this).navigate(R.id.action_createFragment_to_navigation_dashboard);
                            }).addOnFailureListener(e -> {
                                showDiaglog("Error occurred", e + ".", "close");
                                binding.shareBtn.setEnabled(true);
                            });
                        } else {
                            Toast.makeText(getActivity(), "phone number invalid", Toast.LENGTH_SHORT).show();
                            binding.shareBtn.setEnabled(true);
                        }
                    } else {
                        Toast.makeText(getActivity(), "sender name is invalid", Toast.LENGTH_SHORT).show();
                        binding.shareBtn.setEnabled(true);
                    }
                } else {
                    Toast.makeText(getActivity(), "pet type invalid", Toast.LENGTH_SHORT).show();
                    binding.shareBtn.setEnabled(true);
                }
            } else {
                Toast.makeText(getActivity(), "title should be bigger", Toast.LENGTH_SHORT).show();
                binding.shareBtn.setEnabled(true);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        FusedLocationProviderClient locationProviderClient = new FusedLocationProviderClient(getActivity());
        locationProviderClient.getLocationAvailability().addOnSuccessListener(locationAvailability -> {
            if (locationAvailability.isLocationAvailable()) {
                locationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                    this.location = location;
                    binding.shareBtn.setEnabled(true);
                    binding.editLocation.setText("" + location.getLatitude() + "," + location.getLongitude());
                }).addOnFailureListener(e -> {
                    showDiaglog("location not available", "please enable location service and wifi then reopen the app", "ok");
                });
            } else {
                showDiaglog("location not available", "please enable location service and restart the app", "ok");
            }
        });
    }

    private void showDiaglog(String title, String message, String btn_text) {
        new AlertDialog.Builder(getActivity()).setTitle(title).setMessage(message).setPositiveButton(btn_text, (dialogInterface, i) -> {

        }).create().show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        } else {
            Toast.makeText(getActivity(), "no app available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            fullPhotoUri = data.getData();
            Glide.with(this).load(fullPhotoUri).into(binding.uploadImg);
            binding.uploadImg.setTag(fullPhotoUri);
        }
    }
}