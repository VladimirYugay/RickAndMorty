package vladimir.yandex.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import vladimir.yandex.Constants;
import vladimir.yandex.R;

public class PhotoActivity extends AppCompatActivity {

    String mUrl;
    String mName;
    Toolbar mToolbar;
    ImageView mImage;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);


        mImage = findViewById(R.id.photoImage);
        mUrl = getIntent().getStringExtra(Constants.URL);
        mName = getIntent().getStringExtra(Constants.NAME);

        if(savedInstanceState != null){
            mUrl = savedInstanceState.getString(Constants.STATE_URL);
            mName = savedInstanceState.getString(Constants.STATE_NAME);
        }

        setUpToolbar();
        setUpStatusBar();

        Glide.with(this)
                .load(mUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mImage);


        mImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(mToolbar.isShown()){
                        mToolbar.setVisibility(View.INVISIBLE);
                    }else{
                        mToolbar.setVisibility(View.VISIBLE);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.STATE_URL, mUrl);
        outState.putString(Constants.STATE_NAME, mName);
    }

    private void setUpToolbar(){
        mToolbar = findViewById(R.id.photoToolbar);
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

    private void setUpStatusBar(){
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }

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
