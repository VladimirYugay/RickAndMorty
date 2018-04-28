package vladimir.yandex.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vladimir.yandex.Constants;
import vladimir.yandex.adapters.GalleryAdapter;
import vladimir.yandex.R;
import vladimir.yandex.api.CharactersApi;
import vladimir.yandex.api.CharactersService;
import vladimir.yandex.database.DatabaseHandler;
import vladimir.yandex.entity.Reponse;
import vladimir.yandex.entity.Result;

public class GalleryActivity extends AppCompatActivity{

    //Извините
    private GalleryAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private RecyclerView mRecycler;
    private boolean isLoading = false;
    private Call<Reponse> mCall;
    private CharactersService mService;
    private String PAGE = "1";
    private DatabaseHandler mDatabaseHandler;
    private SaveToDatabase mSaveToDabatase;
    private RetrieveFromDatabase mRetrieveFromDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mRecycler = (RecyclerView)findViewById(R.id.recycler);
        mAdapter = new GalleryAdapter(this);
        mDatabaseHandler = new DatabaseHandler(this);

        if(savedInstanceState != null){
            PAGE = savedInstanceState.getString(Constants.PAGE);
            mAdapter.addAll(savedInstanceState.<Result>getParcelableArrayList(Constants.DATA));
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

        //Идем смотреть кэш
        //Если там ничего нету, то он сам запустит метод, который грузит из сети
        if(savedInstanceState == null){
            mRetrieveFromDatabase = new RetrieveFromDatabase(GalleryActivity.this);
            mRetrieveFromDatabase.execute();
        }

    }

    //Кладем следующую страницу.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.PAGE, PAGE);
        outState.putParcelableArrayList(Constants.DATA, (ArrayList<? extends Parcelable>) mAdapter.getGalleryItems());
        super.onSaveInstanceState(outState);
    }

    //отменяем вызовы, чтобы не текло
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCall != null){
            mCall.cancel();
        }
        if(mSaveToDabatase != null){
            mSaveToDabatase.cancel(true);
        }
        if(mRetrieveFromDatabase != null){
            mRetrieveFromDatabase.cancel(true);
        }
    }


    /*
     Методы для работы с данными
   _________________________________________________________________________________________________
    */

    public void loadData(){
        //смотрим код ошибки, если он больше 0 (все коды ошибок <  0), то выполняем, иначе что-то делаем с ошибкой
        if(getErrorCode() > 0){
            mCall = mService.getCharactersJSON(PAGE);
            isLoading = true;
            mCall.enqueue(new Callback<Reponse>() {
                @Override
                public void onResponse(Call<Reponse> call, Response<Reponse> response) {
                    isLoading = false;
                    if(response.isSuccessful()){
                        //берем следующую страницу из апи
                        PAGE = fetchPageNumber(response);
                        //кэшируем в случае успешного запроса
                        mSaveToDabatase = new SaveToDatabase(GalleryActivity.this, fetchResults(response));
                        mSaveToDabatase.execute();
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

    //кэшируем в бд
    private static class SaveToDatabase extends AsyncTask<Void, Void, Void>{

        private final WeakReference<GalleryActivity> mReference;
        private final List<Result> mCharacters;

        private SaveToDatabase(GalleryActivity activity, List<Result> responses) {
            mReference = new WeakReference<>(activity);
            mCharacters = responses;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for(Result character : mCharacters){
                if(isCancelled()){
                    break;
                }
                if(mReference.get() != null){
                    mReference.get().mDatabaseHandler.addCharacter(character);
                }
            }
            return null;
        }
    }

    //берем из бд
    private static class RetrieveFromDatabase extends AsyncTask<Void, Void, List<Result>>{

        private final WeakReference<GalleryActivity> mReference;

        private RetrieveFromDatabase(GalleryActivity activity) {
            mReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            if(mReference.get() != null){
                mReference.get().isLoading = true;
            }
        }

        @Override
        protected List<Result> doInBackground(Void... voids) {
            if(mReference.get() != null && !isCancelled()){
                return new ArrayList<>(mReference.get().mDatabaseHandler.getAllCharacters());
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Result> results) {
            if(mReference.get() != null){
                mReference.get().isLoading = false;
                if(results != null && results.size() != 0){
                    mReference.get().PAGE = String.valueOf(results.size() / 20 + 1);
                    mReference.get().mAdapter.addAll(results);
                }else{
                    mReference.get().loadData();
                }
            }
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
