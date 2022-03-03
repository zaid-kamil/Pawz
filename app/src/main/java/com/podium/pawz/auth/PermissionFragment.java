package com.podium.pawz.auth;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.VIBRATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.podium.pawz.R;
import com.podium.pawz.databinding.FragmentPermissionBinding;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class PermissionFragment extends Fragment implements EasyPermissions.PermissionCallbacks {


    String[] perms = new String[]{
            WRITE_EXTERNAL_STORAGE,
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
            INTERNET,
            SEND_SMS,
            RECEIVE_SMS,
            READ_SMS,
            VIBRATE,
            ACCESS_NETWORK_STATE,
            CALL_PHONE
    };
    private FragmentPermissionBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPermissionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!EasyPermissions.hasPermissions(requireActivity(), perms)) {
            binding.btnContinue.setOnClickListener(v -> {
                EasyPermissions.requestPermissions(this, "requested permission", 23, perms);
            });
        } else {
            moveToLogin();
        }
    }

    private void moveToLogin() {
        NavHostFragment.findNavController(this).navigate(R.id.action_PermissionFragment_to_LoginFragment);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        moveToLogin();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}