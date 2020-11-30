package com.beebeom.a19_retrofit;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

//구현 못한것
//데이터 순서를 어떻게 하면 항상 고정되게 할 수 있는가..
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "테스트";
    private List<LocalNxNy> mNxNyList;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = findViewById(R.id.list_view);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);
        //스와이프 리프레쉬 색 설정
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK);
        mProgressBar = findViewById(R.id.progressBar);
        //베이스 날짜 가져오기
        String mBase_date = getTodayDate();
        //베이스 타임 가져오기
        String mBase_time = getCurrentTime();
        //지역 경도 위도 리스트
        getNxNy();
        //새로고침
        //리스트뷰 스와이프 새로고침 설정
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);

            }
        });
        getWeather(mBase_date, mBase_time);
        //스와이프 후 갱신, 4초 후 표시
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mListView.setVisibility(View.GONE);
            getWeather(mBase_date, mBase_time);
            new Handler().postDelayed(() -> {
                mSwipeRefreshLayout.setRefreshing(false);
                mListView.setVisibility(View.VISIBLE);
            }, 5000);
        });

    }

    private void getWeather(String mBase_date, String mBase_time) {
        List<Weather> mWeatherList = new ArrayList<>();
        RetrofitInit retrofitInit = new RetrofitInit();

        for (int i = 0; i < mNxNyList.size(); i++) {
            //경도 위도 가져오기
            LocalNxNy localNxNy = mNxNyList.get(i);
            //데이터 요청
            retrofitInit.server.getWeather(mBase_date, mBase_time, localNxNy.getNx(), localNxNy.getNy()).enqueue(new Callback<Weather>() {
                @Override
                public void onResponse(@NonNull Call<Weather> call, @NonNull Response<Weather> response) {
                    if (response.isSuccessful()) {
                        //UI 갱신을 그냥 여기서 하면 되는거였는데 시발.
                        //Retrofit 은 여기서 그냥 UI 갱신해도 됨.
                        mWeatherList.add(response.body());
                        WeatherAdapter adapter = new WeatherAdapter();
                        adapter.setItems(mWeatherList, mBase_time);
                        mListView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
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
        //프로그레스 바가 5초 돌고 사라지고, 리스트뷰가 나타나게끔
        new Handler().postDelayed(() -> {
            mProgressBar.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);

        }, 5000);
    }

    //오늘 날짜 구하기 = base_date
    private String getTodayDate() {
        long todayDate = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return format.format(todayDate);
    }

    //베이스 타임 구하기 = base_time
    private String getCurrentTime() {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("HHmm", Locale.getDefault());
        long time = Long.parseLong(format.format(currentTime));
        Log.d("시간", "getCurrentTime: " + time);
        String base_time = "";
        if (210 < time && time < 510) {
            base_time = "0200";
        } else if (510 < time && time < 810) {
            base_time = "0500";
        } else if (810 < time && time < 1110) {
            base_time = "0800";
        } else if (1110 < time && time < 1410) {
            base_time = "1100";
        } else if (1410 < time && time < 1710) {
            base_time = "1400";
        } else if (1710 < time && time < 2010) {
            base_time = "1700";
        } else if (2010 < time && time < 2310) {
            base_time = "2000";
        } else if (2310 < time) {
            base_time = "2300";
        }

        return base_time;
    }

    //경도 위도 가져오기
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

}