package vladimir.yandex.api;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CharactersApi {

    private static final String ROOT_URL = "https://rickandmortyapi.com/api/";

    private static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static CharactersService getApiService() {
        return getRetrofitInstance().create(CharactersService.class);
    }
}