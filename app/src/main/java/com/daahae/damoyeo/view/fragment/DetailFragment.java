package com.daahae.damoyeo.view.fragment;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.daahae.damoyeo.R;
import com.daahae.damoyeo.communication.RetrofitCommunication;
import com.daahae.damoyeo.model.Building;
import com.daahae.damoyeo.model.BuildingArr;
import com.daahae.damoyeo.model.BuildingDetail;
import com.daahae.damoyeo.model.Person;
import com.daahae.damoyeo.presenter.DetailFragmentPresenter;
import com.daahae.damoyeo.presenter.MapsActivityPresenter;
import com.daahae.damoyeo.view.adapter.TransportAdapter;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class DetailFragment extends Fragment implements View.OnClickListener{

    private ListView listTransport;
    private DetailFragmentPresenter presenter;
    private MapsActivityPresenter parentPresenter;

    private TransportAdapter transportAdapter;

    private Button btnBuilding, btnTransport;

    private Building building;

    private TextView txtBuildingName, txtBuildingAddress, txtBuildingTel, txtDescription, txtBuildingDistance;
    private LinearLayout linearBuildingDetail;

    private ImageButton btnBack;
    private View view;

    public DetailFragment(MapsActivityPresenter parentPresenter){
        this.parentPresenter = parentPresenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new DetailFragmentPresenter(this);
        transportAdapter = new TransportAdapter(parentPresenter.getTransportData(), Person.getInstance(),getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (View) inflater.inflate(R.layout.fragment_detail, container, false);

        RetrofitCommunication.BuildingDetailCallBack buildingDetailCallBack = new RetrofitCommunication.BuildingDetailCallBack() {
            @Override
            public void buildingDetailDataPath(BuildingDetail buildingDetail) {

                initView(view);
                initListener();

                getBuildingInfo();
                setBuildingInfo();

                setBuildingDetail(buildingDetail);
                Log.v("상세 데이터",buildingDetail.getBuildingTel());
                Log.v("상세 데이터",buildingDetail.getBuildingDescription());

                presenter.initData(Person.getInstance(), parentPresenter.getTransportData());
                listTransport.setAdapter(transportAdapter);

            }
        };
        RetrofitCommunication.getInstance().setBuildingDetailData(buildingDetailCallBack);


        return view;
    }
    private void setBuildingDetail(BuildingDetail buildingDetail){
        if(buildingDetail.getBuildingTel()!=null) txtBuildingTel.setText("tel. "+buildingDetail.getBuildingTel());
        else txtBuildingTel.setVisibility(View.GONE);
        txtDescription.setText(buildingDetail.getBuildingDescription());

    }

    private void initView(View view){
        btnBuilding = view.findViewById(R.id.btn_building_detail);
        btnTransport = view.findViewById(R.id.btn_transport_detail);

        txtBuildingName = view.findViewById(R.id.txt_building_name_detail);
        txtBuildingAddress = view.findViewById(R.id.txt_building_address_datail);
        txtBuildingTel = view.findViewById(R.id.txt_building_tel_detail);
        txtDescription = view.findViewById(R.id.txt_building_description_detail);
        txtBuildingDistance = view.findViewById(R.id.txt_building_distance_detail);

        linearBuildingDetail = view.findViewById(R.id.linear_building_detail);
        listTransport = view.findViewById(R.id.list_transport_detail);
        btnBack = view.findViewById(R.id.btn_back_building_transport);
    }

    private void initListener(){
        btnBuilding.setOnClickListener(this);
        btnTransport.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    private void getBuildingInfo(){
        building = parentPresenter.getTargetBuilding();
    }

    private void setBuildingInfo(){
        txtBuildingName.setText(building.getName());
        txtBuildingAddress.setText(building.getBuildingAddress());
        txtBuildingDistance.setText(String.format("%.2f",building.getDistance()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back_building_transport:
                parentPresenter.backView(this);
                break;
            case R.id.btn_building_detail:
                btnBuilding.setBackgroundResource(R.drawable.bottom_edge_orange_box);
                btnTransport.setBackgroundResource(R.color.colorWhite);
                linearBuildingDetail.setVisibility(View.VISIBLE);
                listTransport.setVisibility(View.GONE);
                break;
            case R.id.btn_transport_detail:
                btnTransport.setBackgroundResource(R.drawable.bottom_edge_orange_box);
                btnBuilding.setBackgroundResource(R.color.colorWhite);
                linearBuildingDetail.setVisibility(View.GONE);
                listTransport.setVisibility(View.VISIBLE);
                break;
        }
    }

}