package vladimir.yandex.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vladimir.yandex.Constants;
import vladimir.yandex.adapters.GalleryAdapter;
import vladimir.yandex.R;
import vladimir.yandex.api.CharactersApi;
import vladimir.yandex.api.CharactersService;
import vladimir.yandex.entity.Reponse;
import vladimir.yandex.entity.Result;

public class GalleryActivity extends AppCompatActivity{

    private GalleryAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private RecyclerView mRecycler;
    private boolean isLoading = false;
    private Call<Reponse> mCall;
    private CharactersService mService;
    private String PAGE = "1";
    private Parcelable mRecyclerState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mRecycler = (RecyclerView)findViewById(R.id.recycler);
        mAdapter = new GalleryAdapter(this);

        if(savedInstanceState != null){
            PAGE = savedInstanceState.getString(Constants.PAGE);
            mAdapter.addAll(savedInstanceState.<Result>getParcelableArrayList(Constants.DATA));
            mRecyclerState = savedInstanceState.getParcelable(Constants.RECYCLER_STATE);
        }

        mLayoutManager = new GridLayoutManager(this, 2);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(mLayoutManager);

        //пагинация
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = mLayoutManager.getChildCount();
                int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                if(dy > 0){
                    if((visibleItemCount + pastVisibleItems) >= mLayoutManager.getItemCount() && !isLoading){
                        loadData();
                    }
                }
            }
        });

        //чтобы вью с прогресс бар была во всю ширину экрана
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

        //Только при первой странице т.к. при смене экрана снова вызывется данный метод из-за пересоздания активити
        if(PAGE.equals("1")){
            loadData();
        }
    }

    //Кладем следующую страницу, данные, состояние ресайлера.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.PAGE, PAGE);
        outState.putParcelableArrayList(Constants.DATA, (ArrayList<? extends Parcelable>) mAdapter.getGalleryItems());
        mRecyclerState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(Constants.RECYCLER_STATE, mRecyclerState);
        super.onSaveInstanceState(outState);
    }

    //отменяем вызов, чтобы не текло
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCall != null){
            mCall.cancel();
        }
    }

    //После поворота экрана, нужно восстановить recycler. Знаю, что так делать надо, но не могу исправить, что он все равно вверх прыгает
    @Override
    protected void onResume() {
        super.onResume();
        if(mRecyclerState != null){
            mLayoutManager.onRestoreInstanceState(mRecyclerState);
        }
    }

    /*
     Методы для работы с данными
   _________________________________________________________________________________________________
    */

    public void loadData(){
        if(getErrorCode() > 0){
            mCall = mService.getCharactersJSON(PAGE);
            isLoading = true;
            mCall.enqueue(new Callback<Reponse>() {
                @Override
                public void onResponse(Call<Reponse> call, Response<Reponse> response) {
                    isLoading = false;
                    if(response.isSuccessful()){
                        PAGE = fetchPageNumber(response);
                        mAdapter.addAll(fetchResults(response));
                    }else {
                        handleError(Constants.SERVER_ERROR);
                    }
                }

                @Override
                public void onFailure(Call<Reponse> call, Throwable t) {
                    handleError(Constants.SERVER_ERROR);
                    t.printStackTrace();
                }
            });
        }else{
            handleError(getErrorCode());
        }
    }


    private List<Result> fetchResults(Response<Reponse> response){
        return response.body().getResults();
    }

    //Из-за особенностей данного API разумнее брать номер следующей страницы из объекта INFO, тогда не нужно будет проверять общее число страниц и увеличивать текущую вручную
    //ИЗ-за особенностей Retrofit2 (нельзя менять baseURL) я достаю номер страницы regexp и отправляю в качестве параметра в запрос
    private String fetchPageNumber(Response<Reponse> response){
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
    int getErrorCode(){
        if(!isNetworkConnected()){
            return Constants.INTERNET_ERROR;
        }else if(!isNextPageExists()){
            return Constants.PAGE_LIMIT_ERROR;
        }else
        return Constants.OK;
    }

    void handleError(int errorCode){
        switch (errorCode){
            case Constants.INTERNET_ERROR:
                mAdapter.ERROR_MESSAGE = getString(R.string.INTERNET_ERROR_MESSAGE);
                mAdapter.INTERNET_ERROR = true;
                mAdapter.DATA_ERROR = false;
                break;
            case Constants.PAGE_LIMIT_ERROR:
                mAdapter.ERROR_MESSAGE = getString(R.string.EOF_ERROR_MESSAGE);
                mAdapter.DATA_ERROR = true;
                mAdapter.INTERNET_ERROR = false;
                break;
            case Constants.SERVER_ERROR:
                mAdapter.ERROR_MESSAGE = getString(R.string.SERVER_ERROR_MESSAGE);
                mAdapter.DATA_ERROR = true;
                mAdapter.INTERNET_ERROR = false;
                break;
            default:
                mAdapter.DATA_ERROR = false;
                mAdapter.INTERNET_ERROR = false;
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private boolean isNextPageExists(){
        return PAGE != null && !PAGE.equals("");
    }

}
