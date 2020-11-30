    package com.beebeom.a19_retrofit;

    import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface WeatherServer {
    //api 키는 쿼리로 요청하면 키값이 변환되는 에러가 있었음.
    //메인액티비티에서 Response.toString 으로 주소값 로그출력해서 확인했음.
    //api, pageNo, numOfRows,dataType 은 어차피 고정값이라 그냥 정적으로 요청.
    @GET("/1360000/VilageFcstInfoService/getVilageFcst?serviceKey=70Isc0ZRaL3YQcP4qR2VwEbMQVlQIHayotCjEHOYSTe9Wf9RH0OxU28pZZgzGNAxEpNl5muvcqLS%2ByzF2H8YfQ%3D%3D&pageNo=1&numOfRows=12&dataType=JSON&")
    Call<Weather> getWeather(
            @Query("base_date") String base_date,
            @Query("base_time") String base_time,
            @Query("nx") int nx,
            @Query("ny") int ny
    );
}
