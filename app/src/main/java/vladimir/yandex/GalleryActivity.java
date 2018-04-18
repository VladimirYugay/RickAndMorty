package vladimir.yandex;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import vladimir.yandex.entity.Characters;
import vladimir.yandex.entity.Result;
import vladimir.yandex.interfaces.ApiService;
import vladimir.yandex.interfaces.OnDataSendToActivity;

public class GalleryActivity extends AppCompatActivity implements OnDataSendToActivity {

    private GalleryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mAdapter = new GalleryAdapter();
        GridLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        new LoadData(GalleryActivity.this).execute(1);
    }

    @Override
    public void sendData(List<Result> results) {
        mAdapter.addAll(results);
    }

    private static class LoadData extends AsyncTask<Integer, Void, List<Result>>{

        int mPage = 0;
        OnDataSendToActivity dataSendToActivity;

        public LoadData(Activity activity){
            mPage = 1;
            dataSendToActivity = (OnDataSendToActivity) activity;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Result> doInBackground(Integer... integers) {
            try{
                ApiService api = RetroClient.getApiService();
                List<Result> results = new ArrayList<>();
                Call<Characters> call = api.getCharactersJSON(integers[0]);
                Response<Characters> response = call.execute();
                return response.body().getResults();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Result> results) {
            dataSendToActivity.sendData(results);
        }
    }

}
