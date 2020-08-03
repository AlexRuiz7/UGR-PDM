package ugr.pdm.granadatour.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import ugr.pdm.granadatour.R;

public class HomeFragment extends Fragment {

    private ViewFlipper mViewFlipper;

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        setupImageSlider(root);
        loadSliderImages();

        return root;
    }

    /**
     * Realiza las rutinas de inicialización del slider
     *
     * @param root Vista padre
     */
    private void setupImageSlider(View root) {
        mViewFlipper = root.findViewById(R.id.imageSlider);

        Animation in = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_out_right);
        mViewFlipper.setInAnimation(in);
        mViewFlipper.setOutAnimation(out);

        mViewFlipper.setAutoStart(true);
        mViewFlipper.setFlipInterval(4000);
        mViewFlipper.startFlipping();
    }


    /**
     * Rellena el slider con las imágenes del proyecto en Firebase
     */
    private void loadSliderImages() {
        StorageReference gsReference = FirebaseStorage.getInstance().getReference();

        gsReference.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            ImageView imageView = new ImageView(getContext());

                            Glide.with(getContext())
                                    .load(item)
                                    .centerCrop()
                                    .into(imageView);

                            mViewFlipper.addView(imageView);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("HOME", "Storage failure");
                    }
                });
    }
}