package vladimir.yandex.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import vladimir.yandex.entity.Reponse;

public interface CharactersService {

    @GET("character/")
    Call<Reponse> getCharactersJSON(@Query("page") String pageNumber );
}