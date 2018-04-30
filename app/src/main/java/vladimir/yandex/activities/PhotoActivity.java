package vladimir.yandex.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileNotFoundException;

import vladimir.yandex.Constants;
import vladimir.yandex.R;

public class PhotoActivity extends AppCompatActivity {

    String mUrl;
    String mName;
    Toolbar mToolbar;
    ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);


        mImage = findViewById(R.id.image);
        mUrl = getIntent().getStringExtra(Constants.URL);
        mName = getIntent().getStringExtra(Constants.NAME);

        if(savedInstanceState != null){
            mUrl = savedInstanceState.getString(Constants.STATE_URL);
            mName = savedInstanceState.getString(Constants.STATE_NAME);
        }

        setUpToolbar();

        Glide.with(this)
                .load(mUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mImage);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.STATE_URL, mUrl);
        outState.putString(Constants.STATE_NAME, mName);
    }

    private void setUpToolbar(){
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(mName);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        mToolbar.inflateMenu(R.menu.photo_menu);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.share){
                    shareImage();
                }
                return false;
            }
        });
    }

    private void shareImage(){
        if(mImage.getDrawable() != null){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mUrl);
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.SHARE_WITH_IMAGELINK)));
        }
    }
}
