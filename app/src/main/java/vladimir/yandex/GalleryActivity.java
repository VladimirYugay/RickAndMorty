package vladimir.yandex;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        int[] images = {R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,R.drawable.ic_launcher_background};
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(new GalleryAdapter(images));
    }
}
