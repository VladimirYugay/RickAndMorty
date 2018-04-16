package vladimir.yandex;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vladimir.yandex.entity.Characters;
import vladimir.yandex.entity.Result;
import vladimir.yandex.interfaces.ApiService;

public class GalleryActivity extends AppCompatActivity {

    private List<Result> mCharacters;
    private ProgressDialog mDialog;
    private RecyclerView mRecyclerView;
    private GalleryAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mDialog = new ProgressDialog(GalleryActivity.this);
        mDialog.setMessage("Loading Data.. Please wait...");
        mDialog.setIndeterminate(false);
        mDialog.setCancelable(false);
        mDialog.show();

        ApiService api = RetroClient.getApiService();

        Call<Characters> call = api.getMyJSON();

        call.enqueue(new Callback<Characters>() {
            @Override
            public void onResponse(Call<Characters> call, Response<Characters> response) {
                mDialog.dismiss();
                Response<Characters> k = response;
                if (response.isSuccessful()) {
                    mCharacters = response.body().getResults();
                    mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
                    mAdapter = new GalleryAdapter(mCharacters);
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onFailure(Call<Characters> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "AFSFASFASDF", Toast.LENGTH_LONG).show();
                mDialog.dismiss();
            }
        });
    }
}
