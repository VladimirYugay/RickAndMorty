package vladimir.yandex.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vladimir.yandex.entity.Characters;

public interface CharactersService {

    @GET("character/")
    Call<Characters> getCharactersJSON(@Query("page") String pageNumber );
}