package vladimir.yandex.interfaces;



import retrofit2.Call;
import retrofit2.http.GET;
import vladimir.yandex.entity.Characters;

public interface ApiService {

    /*
    Retrofit get annotation with our URL
    And our method that will return us the List of EmployeeList
    */
    @GET("?page=1")
    Call<Characters> getMyJSON();
}