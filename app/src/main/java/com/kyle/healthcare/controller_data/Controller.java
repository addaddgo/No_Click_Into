package com.kyle.healthcare.controller_data;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.kyle.healthcare.MainActivity;
import com.kyle.healthcare.UIInterface;
import com.kyle.healthcare.risk_tip.RiskTipService;

public class Controller implements RiskTipService.Callback{

    //data from
    private final  static int FROM_BLUETOOTH = 1;
    private final static int FROM_MAP = 2;
    private DataDealInterface dataDealInterface;
    private UIInterface UIInterface;
    private String currentString;


    public Controller(UIInterface UIInterface){
        this.UIInterface = UIInterface;
        this.dataDealInterface = DataManger.dataManger;
        startRiskTipService();
    }

    //RiskTipService start and connect to
    private RiskTipService.TipBinder iBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iBinder = (RiskTipService.TipBinder) service;
            iBinder.setCallback(getMyCallback());
            Log.d("Service","connected");
            MainActivity mainActivity = (MainActivity)(UIInterface);
            mainActivity.startTest();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void startRiskTipService(){
        Log.d("Service","start");
        Activity activity = (Activity) UIInterface;
        Intent intent = new Intent(activity.getApplicationContext(),RiskTipService.class);
        activity.startService(intent);
        activity.bindService(intent,connection,Context.BIND_AUTO_CREATE);

    }


    //get blueTooth's data
    public void postBlueToothData(String string){
        Log.i("BlueToothThread",string);
        this.currentString = string;
        this.dataDealInterface.addBlueToothData(string);
        updateFragment(FROM_BLUETOOTH);
    }

    /*
       Driving record display
     */

    //get longitude and latitude
    public  void postXY(float X,float Y){
        this.dataDealInterface.addDrivingData(X,Y);
        updateFragment(FROM_MAP);
    }

    // update the fragment which is visible
    private void updateFragment(int from){
        informService();
        switch (this.UIInterface.getVisibleFragmentAddress()){
            case FragmentAddressBook.frag_id_driving:
                if(from == FROM_MAP){
                    this.UIInterface.updateDrivingFragment(this.dataDealInterface.getLatestDrivingInformation());
                    Log.i("Controller","update Driving");
                }
                break;
            case FragmentAddressBook.frag_id_health:
                if(from == FROM_BLUETOOTH){
                    Log.i("Controller","update Health");
                    this.UIInterface.updateHealthFragment(dataDealInterface.getHeartRate(),dataDealInterface.getFatigueRate());
                }
                break;
            case FragmentAddressBook.frag_id_homepage:
                    int currentGif = this.dataDealInterface.getCurrentGifId();
                    if(currentGif != 0){
                        this.UIInterface.updateHomePageFragment(currentGif);
                    }
                    break;
        }
    }


    //inform RiskTipService
    private void informService(){
        int si = this.dataDealInterface.analyzeSituation();
        Log.d("Service","inform");
        if(si != -1){
            this.iBinder.postDangerInformation(si);
        }
    }

    @Override
    public void TipBefore() {
        this.UIInterface.stopHealthFragmentUpdate();
    }

    private RiskTipService.Callback getMyCallback(){
        return this;
    }
}
