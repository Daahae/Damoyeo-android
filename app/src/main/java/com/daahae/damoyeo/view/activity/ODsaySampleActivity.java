package com.daahae.damoyeo.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.daahae.damoyeo.R;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.daahae.damoyeo.model.Transport;
import com.daahae.damoyeo.model.TransportInfoList;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public class ODsaySampleActivity extends AppCompatActivity {

    private Spinner sp_api;
    private RadioGroup rg_object_type;
    private RadioButton rb_json, rb_map;
    private Button bt_api_call;
    private TextView tv_data;
    private Context context;
    private String spinnerSelectedName;
    private ODsayService odsayService;
    private JSONObject jsonObject;
    private Map mapObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odsay_sample);
        init();
    }
    private void init() {
        context = this;
        sp_api = (Spinner) findViewById(R.id.sp_api);
        rg_object_type = (RadioGroup) findViewById(R.id.rg_object_type);
        bt_api_call = (Button) findViewById(R.id.bt_api_call);
        rb_json = (RadioButton) findViewById(R.id.rb_json);
        rb_map = (RadioButton) findViewById(R.id.rb_map);
        tv_data = (TextView) findViewById(R.id.tv_data);
        sp_api.setSelection(0);

        odsayService = ODsayService.init(ODsaySampleActivity.this, getString(R.string.odsay_key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);

        bt_api_call.setOnClickListener(onClickListener);
        sp_api.setOnItemSelectedListener(onItemSelectedListener);
        rg_object_type.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            if (rg_object_type.getCheckedRadioButtonId() == rb_json.getId()) {
                tv_data.setText(jsonObject.toString());
            } else if (rg_object_type.getCheckedRadioButtonId() == rb_map.getId()) {
                tv_data.setText(mapObject.toString());
            }
        }
    };
    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            spinnerSelectedName = (String) parent.getItemAtPosition(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
            @Override
            public void onSuccess(ODsayData oDsayData, API api) {
                ArrayList<Transport> transportInfoList = new ArrayList<>();
                jsonObject = oDsayData.getJson();
                mapObject = oDsayData.getMap();// 임시변수는 다 수정

                try {

                    jsonObject = jsonObject.getJSONObject("result");
                    JSONArray pathJa = jsonObject.getJSONArray("path");
                    StringBuffer sb = new StringBuffer();// 샘플 확인을 위함
                    if (rg_object_type.getCheckedRadioButtonId() == rb_json.getId()) {
                        for (int i = 0; i < pathJa.length(); i++) {
                            JSONObject jo = pathJa.getJSONObject(i);
                            JSONArray subPathJa = jo.getJSONArray("subPath");
                            sb.append(i+"번째 경로 --------\n+");
                            int totalTime = 0;// 총 걸린시간 계산

                            for(int j=0;j<subPathJa.length();j++) {
                                JSONObject tmpJo = subPathJa.getJSONObject(j);

                                int trafficType = tmpJo.getInt("trafficType");
                                int sectionTime = tmpJo.getInt("sectionTime");
                                String startName;
                                String endName;
                                if(trafficType ==3){
                                    startName = null;
                                    endName = null;
                                }else {
                                    startName = tmpJo.getString("startName");
                                    endName = tmpJo.getString("endName");
                                }

                                sb.append(

                                                      "trafficType : " + trafficType + "\n" +
                                                      "sectionTime : " + sectionTime + "\n" +
                                                      "startStation : " + startName + "\n" +
                                                      "endStation : " + endName + "\n" +
                                        "\n"
                                );
                                totalTime += sectionTime;
                            }
                            sb.append("totalTime : "+ totalTime+"분"+"\n\n");
                        }
                       tv_data.setText(sb);
                    } else if (rg_object_type.getCheckedRadioButtonId() == rb_map.getId()) {
                        tv_data.setText(mapObject.toString());
                    }

                } catch (Exception E) {
                    E.printStackTrace();
                }
            }
        @Override
        public void onError(int i, String errorMessage, API api) {
            tv_data.setText("API : " + api.name() + "\n" + errorMessage);
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (spinnerSelectedName) {
                case "버스 노선 조회":
                    odsayService.requestSearchBusLane("150", "1000", "no", "10", "1", onResultCallbackListener);
                    break;
                case "버스노선 상세정보 조회":
                    odsayService.requestBusLaneDetail("12018", onResultCallbackListener);
                    break;
                case "버스정류장 세부정보 조회":
                    odsayService.requestBusStationInfo("107475", onResultCallbackListener);
                    break;
                case "열차•KTX 운행정보 검색":
                    odsayService.requestTrainServiceTime("3300128", "3300108", onResultCallbackListener);
                    break;
                case "고속버스 운행정보 검색":
                    odsayService.requestExpressServiceTime("4000057", "4000030", onResultCallbackListener);
                    break;
                case "시외버스 운행정보 검색":
                    odsayService.requestIntercityServiceTime("4000022", "4000255", onResultCallbackListener);
                    break;
                case "항공 운행정보 검색":
                    odsayService.requestAirServiceTime("3500001", "3500003", "6", onResultCallbackListener);
                    break;
                case "운수회사별 버스노선 조회":
                    odsayService.requestSearchByCompany("792", "100", onResultCallbackListener);
                    break;
                case "지하철역 세부 정보 조회":
                    odsayService.requestSubwayStationInfo("130", onResultCallbackListener);
                    break;
                case "지하철역 전체 시간표 조회":
                    odsayService.requestSubwayTimeTable("130", "1", onResultCallbackListener);
                    break;
                case "노선 그래픽 데이터 검색":
                    odsayService.requestLoadLane("0:0@12018:1:-1:-1", onResultCallbackListener);
                    break;
                case "대중교통 정류장 검색":
                    odsayService.requestSearchStation("11", "1000", "1:2", "10", "1", "127.0363583:37.5113295", onResultCallbackListener);
                    break;
                case "반경내 대중교통 POI 검색":
                    odsayService.requestPointSearch("126.933361407195", "37.3643392278118", "250", "1:2", onResultCallbackListener);
                    break;
                case "지도 위 대중교통 POI 검색":
                    odsayService.requestBoundarySearch("127.045478316811:37.68882830829:127.055063420699:37.6370465749586", "127.045478316811:37.68882830829:127.055063420699:37.6370465749586", "1:2", onResultCallbackListener);
                    break;
                case "지하철 경로검색 조회(지하철 노선도)":
                    odsayService.requestSubwayPath("1000", "201", "222", "1", onResultCallbackListener);
                    break;
                case "대중교통 길찾기":// 주로 사용
                    odsayService.requestSearchPubTransPath("127.073139", "37.5502596", "127.0771595", "37.5407625", "1", "0", "0", onResultCallbackListener);
                    break;
                case "지하철역 환승 정보 조회":
                    odsayService.requestSubwayTransitInfo("133", onResultCallbackListener);
                    break;
                case "고속버스 터미널 조회":
                    odsayService.requestExpressBusTerminals("1000", "서울", onResultCallbackListener);
                    break;
                case "시외버스 터미널 조회":
                    odsayService.requestIntercityBusTerminals("1000", "서울", onResultCallbackListener);
                    break;
                case "도시코드 조회":
                    odsayService.requestSearchCID("서울", onResultCallbackListener);
                    break;
            }
        }
    };
}
