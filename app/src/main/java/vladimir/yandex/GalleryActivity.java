package vladimir.yandex;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 5;
    private int currentPage = 1;
    private String NEXT_PAGE = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mRecycler = (RecyclerView)findViewById(R.id.recycler);
        mAdapter = new GalleryAdapter(this);
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.addOnScrollListener(new PaginationScrollListener(mLayoutManager){

            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
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

        loadFirstPage();
    }

    private void loadFirstPage(){
        callCharacters().enqueue(new Callback<Characters>() {
            @Override
            public void onResponse(Call<Characters> call, Response<Characters> response) {
                isLastPage = false;
                isLoading = false;
                mAdapter.addAll(fetchResults(response));
            }

            @Override
            public void onFailure(Call<Characters> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void loadNextPage(){
        callCharacters().enqueue(new Callback<Characters>() {
            @Override
            public void onResponse(Call<Characters> call, Response<Characters> response) {
                isLastPage = false;
                isLoading = false;
                mAdapter.addAll(fetchResults(response));
            }

            @Override
            public void onFailure(Call<Characters> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private Call<Characters> callCharacters(){
        return mService.getCharactersJSON(currentPage);
    }

    private List<Result> fetchResults(Response<Characters> response){
        return response.body().getResults();
    }

}
