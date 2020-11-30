    package com.beebeom.a19_retrofit;

    import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface WeatherServer {
    @GET("/1360000/VilageFcstInfoService/getVilageFcst?serviceKey=70Isc0ZRaL3YQcP4qR2VwEbMQVlQIHayotCjEHOYSTe9Wf9RH0OxU28pZZgzGNAxEpNl5muvcqLS%2ByzF2H8YfQ%3D%3D&pageNo=1&numOfRows=12&dataType=JSON&")
    Call<Weather> getWeather(
            @Query("base_date") String base_date,
            @Query("base_time") String base_time,
            @Query("nx") int nx,
            @Query("ny") int ny
    );
}
