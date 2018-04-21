package vladimir.yandex;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.nio.file.Path;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vladimir.yandex.api.CharactersApi;
import vladimir.yandex.api.CharactersService;
import vladimir.yandex.entity.Characters;
import vladimir.yandex.entity.Result;
import vladimir.yandex.utils.PaginationScrollListener;

public class GalleryActivity extends AppCompatActivity{

    private GalleryAdapter mAdapter;
    GridLayoutManager mLayoutManager;
    RecyclerView mRecycler;
    private CharactersService mService;

    private boolean isLoading = false;
    private String PAGE = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mRecycler = (RecyclerView)findViewById(R.id.recycler);
        mAdapter = new GalleryAdapter();
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.addOnScrollListener(new PaginationScrollListener(mLayoutManager){

            @Override
            protected void loadMoreItems() {
                isLoading = true;
                loadData();
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
            @Override
            public int getSpanSize(int position) {
                if(mAdapter.getItemViewType(position) == mAdapter.REGULAR_ITEM){
                    return 1;
                }else if(mAdapter.getItemViewType(position) == mAdapter.LOADING_ITEM){
                    return 2;
                }else{
                    return -1;
                }
            }
        });

        mService = CharactersApi.getApiService();
    }


    /*
     Методы для работы с данными
   _________________________________________________________________________________________________
    */

    private void loadData(){
        if(!isNetworkConnected()){
            mAdapter.INTERNET_ERROR = true;
        }else if(!isNextPageExists()){
            mAdapter.DATA_ERROR = true;
        } else {
            mAdapter.INTERNET_ERROR = false;
            mAdapter.DATA_ERROR = false;
            callCharacters().enqueue(new Callback<Characters>() {
                @Override
                public void onResponse(Call<Characters> call, Response<Characters> response) {
                    isLoading = false;
                    PAGE = fetchPageNumber(response);
                    mAdapter.addAll(fetchResults(response));
                }

                @Override
                public void onFailure(Call<Characters> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private Call<Characters> callCharacters(){
        return mService.getCharactersJSON(PAGE);
    }

    private List<Result> fetchResults(Response<Characters> response){
        return response.body().getResults();
    }

    //Из-за особенностей данного API разумнее брать номер следующей страницы из объекта INFO, тогда не нужно будет проверять общее число страниц
    //ИЗ-за особенностей Retrofit2 (нельзя менять baseURL) я достаю номер страницы regexp и отправляю в качестве параметра в запрос
    private String fetchPageNumber(Response<Characters> response){
        String url = response.body().getInfo().getNext();
        if(url != null && !url.isEmpty()){
             return url.replaceAll("\\D+","");
        }
        return null;
    }

    /*
     Методы для работы с возможными ошибками
   _________________________________________________________________________________________________
    */


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private boolean isNextPageExists(){
        return PAGE != null;
    }


}
