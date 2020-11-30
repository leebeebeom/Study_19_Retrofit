package com.beebeom.a19_retrofit;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
POP - 강수 확률 %
PTY - 없음(0), 비(1), 진눈개비(2), 눈(3), 소나기(4), 빗방울(5), 빗방울/눈날림(6), 눈날림(7)
R06 - 6시간 강수량 1mm
REH - 습도 %
S06 - 6시간 신적설 1mm
SKY - 하늘 상태 - 맑음(1), 구름많음(3), 흐림(4)
T3H - 3시간 기온 ℃
TMN - 아침 최저기온
TMX - 낮 최고기온

 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "테스트";
    private static final String RUN_NUMBER = "run";
    private List<LocalNxNy> mNxNyList;
    private MutableLiveData<List<Weather>> mLiveWeatherList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //오늘 날짜
        String mBase_date = getTodayDate();
        //현재 베이스 타임
        String mBase_time = getCurrentTime();
        //지역 경도 위도 리스트
        getNxNy();
        //비동기라서 바로 다음 코드 작성할 경우 처리 안됨.
        //새로고침으로 나타나게해줌
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK);
        ListView listView = findViewById(R.id.list_view);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (listView == null || listView.getChildCount() == 0) ? 0 : listView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);

            }
        });
        mLiveWeatherList = new MutableLiveData<>();
        mLiveWeatherList.setValue(getWeather(mBase_date, mBase_time));
        TextView please = findViewById(R.id.please_refresh);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Handler().postDelayed(() -> {
                please.setVisibility(View.GONE);
                mLiveWeatherList.observe(this, weathers -> {
                    WeatherAdapter adapter = new WeatherAdapter();
                    adapter.setItems(weathers);
                    listView.setAdapter(adapter);
                });
                swipeRefreshLayout.setRefreshing(false);
            }, 2500);

        });

    }

    private void getNxNy() {
        mNxNyList = new ArrayList<>();
        mNxNyList.add(new LocalNxNy("서울", 60, 127));
        mNxNyList.add(new LocalNxNy("부산", 98, 76));
        mNxNyList.add(new LocalNxNy("대구", 89, 90));
        mNxNyList.add(new LocalNxNy("인천", 55, 124));
        mNxNyList.add(new LocalNxNy("대전", 67, 100));
        mNxNyList.add(new LocalNxNy("울산", 102, 84));
        mNxNyList.add(new LocalNxNy("세종", 66, 103));
        mNxNyList.add(new LocalNxNy("경기", 60, 120));
        mNxNyList.add(new LocalNxNy("강원", 73, 134));
        mNxNyList.add(new LocalNxNy("충북", 69, 107));
        mNxNyList.add(new LocalNxNy("충남", 68, 100));
        mNxNyList.add(new LocalNxNy("전북", 63, 89));
        mNxNyList.add(new LocalNxNy("전남", 51, 67));
        mNxNyList.add(new LocalNxNy("경북", 89, 91));
        mNxNyList.add(new LocalNxNy("경남", 91, 77));
        mNxNyList.add(new LocalNxNy("제주", 52, 38));
        mNxNyList.add(new LocalNxNy("이어도", 28, 8));
    }

    private List<Weather> getWeather(String mBase_date, String mBase_time) {
        List<Weather> mWeatherList = new ArrayList<>();
        RetrofitInit retrofitInit = new RetrofitInit();
        for (int i = 0; i < mNxNyList.size(); i++) {
            //리스트에서 아이템 뽑기
            LocalNxNy localNxNy = mNxNyList.get(i);
            Log.d(TAG, "getWeather: localNxNy" + localNxNy.toString());
            //데이터 요청
            retrofitInit.server.getWeather(mBase_date, mBase_time, localNxNy.getNx(), localNxNy.getNy()).enqueue(new Callback<Weather>() {
                @Override
                public void onResponse(@NonNull Call<Weather> call, @NonNull Response<Weather> response) {
                    if (response.isSuccessful()) {
                        Weather weather = response.body();
                        mWeatherList.add(weather);
                        Log.d(TAG, "onResponse: 성공" + response.toString());
                    } else {
                        Log.d(TAG, "onResponse: 실패" + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Weather> call, @NonNull Throwable t) {
                    Log.d(TAG, "onFailure: 에러" + t.getMessage());
                }
            });
        }
        return mWeatherList;
    }

    private String getTodayDate() {
        //오늘 날짜 구하기 = base_date
        long todayDate = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return format.format(todayDate);
    }

    private String getCurrentTime() {
        //현재 시간 구하기 = base_time
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("hhmm", Locale.getDefault());
        long time = Long.parseLong(format.format(currentTime));
        String base_time = "";
        if (259 < time && time < 559) {
            base_time = "0200";
        } else if (559 < time && time < 859) {
            base_time = "0500";
        } else if (859 < time && time < 1159) {
            base_time = "0800";
        } else if (1159 < time && time < 1459) {
            base_time = "1100";
        } else if (1459 < time && time < 1759) {
            base_time = "1400";
        } else if (1759 < time && time < 2059) {
            base_time = "1700";
        } else if (2059 < time && time < 2359) {
            base_time = "2000";
        } else if (2359 < time) {
            base_time = "2300";
        }

        return base_time;
    }

}