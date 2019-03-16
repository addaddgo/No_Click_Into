package com.kyle.healthcare.fragment_package;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kyle.healthcare.MainActivity;
import com.kyle.healthcare.MainActivityInterface;
import com.kyle.healthcare.R;
import com.kyle.healthcare.view.FatigueRateView;
import com.kyle.healthcare.view.HeartRateView;

public class HealthFragment extends Fragment {

    private HeartRateView heartRateView;
    private FatigueRateView fatigueRateView;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.health_frag,container,false);
        heartRateView = view.findViewById(R.id.health_fra_heart_rate_view);
        this.fatigueRateView = view.findViewById(R.id.health_fra_fatigue_rate_view);
        addListener();
        return view;
    }

    private void addListener(){
        this.fatigueRateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivityInterface mainActivityInterface = (MainActivityInterface) getActivity();
                mainActivityInterface.replaceFragmentInFragment(new FatigueRateFragment());
            }
        });
        this.heartRateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivityInterface mainActivityInterface = (MainActivityInterface)getActivity();
                mainActivityInterface.replaceFragmentInFragment(new HeartRateFragment());
            }
        });
    }
    @Override
    public void onStop() {
        super.onStop();
        heartRateView.stopDrawThread();
        fatigueRateView.stopDrawThread();
    }

}
