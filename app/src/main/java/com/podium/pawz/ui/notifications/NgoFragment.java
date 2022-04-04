package com.podium.pawz.ui.notifications;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.podium.pawz.Constants;
import com.podium.pawz.R;
import com.podium.pawz.databinding.FragmentNgoBinding;
import com.podium.pawz.databinding.NotifCardViewBinding;
import com.podium.pawz.models.Ngo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NgoFragment extends Fragment {


    private FragmentNgoBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNgoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @org.jetbrains.annotations.NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        binding.recyclerViewNgo.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<Ngo> ngoList = new ArrayList<>();
        NgoAdapter adatper = new NgoAdapter(this, R.layout.notif_card_view, ngoList);
        binding.recyclerViewNgo.setAdapter(adatper);
        db.collection(Constants.NGO).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.size() > 0) {
                for (QueryDocumentSnapshot item : queryDocumentSnapshots) {
                    ngoList.add(item.toObject(Ngo.class));
                    binding.recyclerViewNgo.getAdapter().notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class NgoAdapter extends RecyclerView.Adapter<NgoAdapter.Holder> {

        private final LayoutInflater inflater;
        private final int layout;
        private final Fragment fragment;
        public List<Ngo> ngoList;

        public NgoAdapter(Fragment fragment, int layout, List<Ngo> list) {
            inflater = LayoutInflater.from(fragment.getActivity());
            this.layout = layout;
            ngoList = list;
            this.fragment = fragment;
        }

        @NonNull
        @NotNull
        @Override
        public Holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            return new Holder(inflater.inflate(layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull Holder holder, int position) {
            Ngo ngo = ngoList.get(position);
            holder.bind(ngo);
        }

        @Override
        public int getItemCount() {
            return ngoList.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            NotifCardViewBinding binding;

            public Holder(@NonNull @NotNull View itemView) {
                super(itemView);
                binding = NotifCardViewBinding.bind(itemView);
            }

            public void bind(Ngo ngo) {
                binding.textTitle.setText(ngo.name);
                binding.textPhone.setText(ngo.phone1);
                binding.textPhone2.setText(ngo.phone2);
                binding.textPhone3.setText(ngo.phone3);
                binding.textWebsite.setText(ngo.website);
                binding.textAddress.setOnClickListener(v -> {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    String loc = "geo:0,0?q" + ngo.address;
                    i.setData(Uri.parse(loc));
                    if (i.resolveActivity(fragment.getContext().getPackageManager()) != null) {
                        startActivity(i);
                    }
                });
            }
        }
    }
}