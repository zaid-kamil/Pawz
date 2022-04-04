package com.podium.pawz.ui.dashboard;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.podium.pawz.Constants;
import com.podium.pawz.R;
import com.podium.pawz.Util;
import com.podium.pawz.databinding.FragmentDashboardBinding;
import com.podium.pawz.databinding.PostCardViewBinding;
import com.podium.pawz.models.Animal;
import com.podium.pawz.ui.home.HomeFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {


    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        binding.recyclerViewRescue.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<Animal> animalList = new ArrayList<>();


        RescueAdapter adapter = new RescueAdapter(this, R.layout.post_card_view, animalList);
        binding.recyclerViewRescue.setAdapter(adapter);
        db.collection(Constants.ANIMAL).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.size() > 0) {
                for (QueryDocumentSnapshot item : queryDocumentSnapshots) {
                    animalList.add(item.toObject(Animal.class));
                    binding.recyclerViewRescue.getAdapter().notifyDataSetChanged();
                }
            }
        });
        binding.fab.setOnClickListener(v->{
            NavHostFragment.findNavController(this).navigate(R.id.action_navigation_dashboard_to_createFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class RescueAdapter extends RecyclerView.Adapter<RescueAdapter.Holder> {

        private final LayoutInflater inflater;
        private final int layout;
        public List<Animal> animalList;

        public RescueAdapter(Fragment fragment, int layout, List<Animal> list) {
            inflater = LayoutInflater.from(fragment.getActivity());
            this.layout = layout;
            animalList = list;
        }

        @NonNull
        @NotNull
        @Override
        public RescueAdapter.Holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            return new RescueAdapter.Holder(inflater.inflate(layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull RescueAdapter.Holder holder, int position) {
            Animal animal = animalList.get(position);
            holder.bind(animal);
        }

        @Override
        public int getItemCount() {
            return animalList.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            PostCardViewBinding binding;

            public Holder(@NonNull @NotNull View itemView) {
                super(itemView);
                binding = PostCardViewBinding.bind(itemView);
                binding.callBtn.setOnClickListener(view -> {
                    Animal animal = animalList.get(getAdapterPosition());
                    Util.dialPhoneNumber(getActivity(), animal.userPhone);
                });
                binding.navigateButton.setOnClickListener(view -> {
                    Animal animal = animalList.get(getAdapterPosition());
                    Uri locationUri = Uri.parse("geo:0,0?q=" + animal.latitude + "," + animal.longitude + "(Rescue Location)");
                    Util.showMap(getActivity(), locationUri);
                });
                binding.detailView.setOnClickListener(view -> {
                    Animal animal = animalList.get(getAdapterPosition());
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setMessage("details of " + animal.petType + "\n" + animal.message)
                            .setTitle(animal.title)
                            .setPositiveButton("ok", (dialogInterface, i) -> {
                            }).create();
                    dialog.show();
                });
            }

            public void bind(Animal animal) {
                binding.textTitle.setText(animal.title);
                binding.textUser.setText(animal.username);
                Bitmap bitmap = Util.stringToBitMap(animal.imageStr);
                Glide.with(binding.viewImage).load(bitmap).into(binding.viewImage);
            }
        }

    }
}