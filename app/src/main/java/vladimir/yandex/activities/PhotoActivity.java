package vladimir.yandex.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import vladimir.yandex.R;

public class PhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        ImageView imageView = findViewById(R.id.image);
        String url = getIntent().getStringExtra("URL");

        Glide.with(this)
                .load(url)
                .into(imageView);
    }
}
