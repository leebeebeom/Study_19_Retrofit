package com.beebeom.a19_retrofit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//어댑터 클래스
public class WeatherAdapter extends BaseAdapter {
    private List<Weather> mItems = new ArrayList<>();
    private final HashMap<String, Integer> mWeatherMap = new HashMap<>();
    private String mBase_time = "";

    public void setItems(List<Weather> items, String base_time) {
        //객체 초기화
        mBase_time = base_time;
        mItems = items;
        //날씨 아이콘 맵
        mWeatherMap.put("맑음", R.drawable.sunnypng);
        mWeatherMap.put("구름", R.drawable.cloud);
        mWeatherMap.put("흐림", R.drawable.foggy);
        mWeatherMap.put("비", R.drawable.rain);
        mWeatherMap.put("진눈개비", R.drawable.rainsnowpng);
        mWeatherMap.put("눈", R.drawable.snow);
        mWeatherMap.put("소나기", R.drawable.showerpng);
        mWeatherMap.put("빗방울", R.drawable.raindroppng);
        mWeatherMap.put("눈날림", R.drawable.snow);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //뷰홀더 패턴
        MYVH viewHolder = new MYVH();
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_weather, parent, false);
            viewHolder.weatherIcon = convertView.findViewById(R.id.item_image_weather);
            viewHolder.cityText = convertView.findViewById(R.id.item_text_city);
            viewHolder.weatherText = convertView.findViewById(R.id.item_text_weather);
            viewHolder.tempText = convertView.findViewById(R.id.item_text_temp);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MYVH) convertView.getTag();
        }
        //데이터 바인딩
        Weather weather = mItems.get(position);
        //도시명
        viewHolder.cityText.setText(getCity(weather));
        //날씨 텍스트
        String weatherText = getWeatherText(weather);
        viewHolder.weatherText.setText(weatherText);
        //날씨 아이콘
        Integer weatherIcon = mWeatherMap.get(weatherText);
        if (weatherIcon != null) {
            viewHolder.weatherIcon.setImageResource(weatherIcon);
        }
        //온도
        String temp = weather.getResponse().getBody().getItems().getItem().get(4).getFcstValue();
        viewHolder.tempText.setText(String.format("%s℃", temp));

        return convertView;
    }

    //날씨 텍스트 구하기
    private String getWeatherText(Weather weather) {
        String ptyfcstValue = "";
        String styfcstValue = "";
        //베이스 타임마다 가져오는 데이터 순서가 달라서 설정해줌
        if (mBase_time.equals("0200") || mBase_time.equals("0800") || mBase_time.equals("1400") || mBase_time.equals("2000")) {
            ptyfcstValue = weather.getResponse().getBody().getItems().getItem().get(1).getFcstValue();
            styfcstValue = weather.getResponse().getBody().getItems().getItem().get(5).getFcstValue();
        } else if (mBase_time.equals("0500") || mBase_time.equals("1100") || mBase_time.equals("1700") || mBase_time.equals("2300")) {
            ptyfcstValue = weather.getResponse().getBody().getItems().getItem().get(1).getFcstValue();
            styfcstValue = weather.getResponse().getBody().getItems().getItem().get(3).getFcstValue();
        }
        String weatherText = "";
        switch (ptyfcstValue) {
            case "0":
                switch (styfcstValue) {
                    case "1":
                        weatherText = "맑음";
                        break;
                    case "3":
                        weatherText = "구름";
                        break;
                    case "4":
                        weatherText = "흐림";
                        break;
                }
                break;
            case "1":
                weatherText = "비";
                break;
            case "2":
            case "6":
                weatherText = "진눈개비";
                break;
            case "3":
                weatherText = "눈";
                break;
            case "4":
                weatherText = "소나기";
                break;
            case "5":
                weatherText = "빗방울";
                break;
            case "7":
                weatherText = "눈날림";
                break;
        }
        return weatherText;
    }

    //도시명 구하기
    private String getCity(Weather weather) {
        String city = "";
        int nx = weather.getResponse().getBody().getItems().getItem().get(0).getNx();
        int ny = weather.getResponse().getBody().getItems().getItem().get(0).getNy();
        if (nx == 60 && ny == 127) {
            city = "서울";
        } else if (nx == 98 && ny == 76) {
            city = "부산";
        } else if (nx == 89 && ny == 90) {
            city = "대구";
        } else if (nx == 55 && ny == 124) {
            city = "인천";
        } else if (nx == 67 && ny == 100) {
            city = "대전";
        } else if (nx == 102 && ny == 84) {
            city = "울산";
        } else if (nx == 66 && ny == 103) {
            city = "세종";
        } else if (nx == 60 && ny == 120) {
            city = "경기";
        } else if (nx == 73 && ny == 134) {
            city = "강원";
        } else if (nx == 69 && ny == 107) {
            city = "충북";
        } else if (nx == 68 && ny == 100) {
            city = "충남";
        } else if (nx == 63 && ny == 89) {
            city = "전북";
        } else if (nx == 51 && ny == 67) {
            city = "전남";
        } else if (nx == 89 && ny == 91) {
            city = "경북";
        } else if (nx == 91 && ny == 77) {
            city = "경남";
        } else if (nx == 52 && ny == 38) {
            city = "제주";
        } else if (nx == 28 && ny == 8) {
            city = "이어도";
        }
        return city;
    }

    //뷰홀더
    private static class MYVH {
        ImageView weatherIcon;
        TextView cityText;
        TextView weatherText;
        TextView tempText;
    }
}

