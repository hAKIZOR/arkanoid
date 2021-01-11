package com.caserteam.arkanoid.editor.ui_plus_check;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.caserteam.arkanoid.R;

public class BottomSheetDialog extends BottomSheetDialogFragment {


    private static final int TAB_BRICK = 0;
    private static final int TAB_OBSTACLES = 1;

    private TabLayout tabLayout;
    FragmentManager fragmentManager;
    Fragment FragmentDefault = new FragmentVoid();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.modal_bottom_layout,container,false);

        initalizeBottomSheetFragment();



        tabLayout = (TabLayout) v.findViewById(R.id.tabLayoutBottomSheet);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragmentOutput = FragmentDefault;
                switch (position){
                    case TAB_BRICK:
                        //clicco il tab di scelta mattoni
                        fragmentOutput = new FragmentDetailBricks();
                        break;
                    case TAB_OBSTACLES:
                        //clicco il tab di scelta ostacoli
                        fragmentOutput = new FragmentDetailsObstacles();
                        break;

                }
                Log.d("BottomSheetDialog","passo1");
                fragmentTransaction.replace(R.id.outputFragment,fragmentOutput);
                fragmentTransaction.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        return v;
    }

    private void initalizeBottomSheetFragment() {
        Fragment fragment = new FragmentDetailBricks();
        FragmentManager mfragment = getChildFragmentManager();
        mfragment
                .beginTransaction()
                .replace(R.id.outputFragment,fragment)
                .commit();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // qui è da modificare lo stile se necessario
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                Dialog dialog =  getDialog();
                FrameLayout bottomSheet = (FrameLayout) dialog.findViewById(R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(0); // Remove this line to hide a dark background if you manually hide the dialog.
            }
        });
    }
}
