package vladimir.yandex;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vladimir.yandex.api.CharactersApi;
import vladimir.yandex.api.CharactersService;
import vladimir.yandex.entity.Reponse;
import vladimir.yandex.entity.Result;
import vladimir.yandex.utils.RetryCallback;

public class GalleryActivity extends AppCompatActivity implements RetryCallback{

    private GalleryAdapter mAdapter;
    GridLayoutManager mLayoutManager;
    RecyclerView mRecycler;
    Parcelable mRecyclerState = null;
    private CharactersService mService;
    private String PAGE = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mRecycler = (RecyclerView)findViewById(R.id.recycler);
        mAdapter = new GalleryAdapter(this);
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(mLayoutManager);


        if(savedInstanceState != null){
            PAGE = savedInstanceState.getString(Constants.PAGE);
            mAdapter.addAll(savedInstanceState.<Result>getParcelableArrayList(Constants.DATA));
        }



        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int itemCount = 20;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = mLayoutManager.getChildCount();
                int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                if(dy > 0){
                    if((visibleItemCount + pastVisibleItems) >= itemCount){
                        if(getErrorCode() > 0){
                            loadData();
                            itemCount += 20;
                        }else{
                            handleError(getErrorCode());
                        }
                    }
                }
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

        //Только при первом запуске сессии т.к. при смене экрана снрва вызывется данный метод из-за пересоздания активити
        if(getErrorCode() > 0 && PAGE.equals("1")){
            loadData();
        }else{
            handleError(getErrorCode());
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mRecyclerState = mRecycler.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(Constants.GALLERY_STATE, mRecyclerState);

        outState.putString(Constants.PAGE, PAGE);
        outState.putParcelableArrayList(Constants.DATA, (ArrayList<? extends Parcelable>) mAdapter.getGalleryItems());
        super.onSaveInstanceState(outState);
    }



    /*
     Методы для работы с данными
   _________________________________________________________________________________________________
    */

    private void loadData(){
        callCharacters().enqueue(new Callback<Reponse>() {
            @Override
            public void onResponse(Call<Reponse> call, Response<Reponse> response) {
                PAGE = fetchPageNumber(response);
                mAdapter.addAll(fetchResults(response));
            }

            @Override
            public void onFailure(Call<Reponse> call, Throwable t) {
                handleError(Constants.SERVER_ERROR);
                t.printStackTrace();
            }
        });

    }


    @Override
    public void retryLoad() {
        if(getErrorCode() > 0){
            loadData();
        }else{
            handleError(getErrorCode());
        }
    }

    private Call<Reponse> callCharacters(){
        return mService.getCharactersJSON(PAGE);
    }

    private List<Result> fetchResults(Response<Reponse> response){
        return response.body().getResults();
    }

    //Из-за особенностей данного API разумнее брать номер следующей страницы из объекта INFO, тогда не нужно будет проверять общее число страниц
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
