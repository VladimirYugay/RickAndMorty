package vladimir.yandex;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import vladimir.yandex.entity.Characters;
import vladimir.yandex.entity.Result;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{

    private List<Result> mCharacters;

    public GalleryAdapter(List<Result> characters) {
        mCharacters = characters;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(holder.mImage.getContext())
                .load(mCharacters.get(position).getImage())
                .into(holder.mImage);
        holder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PhotoActivity.class);
                intent.putExtra("URL", "http://i.imgur.com/zuG2bGQ.jpg");
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCharacters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView mImage;
        public ViewHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.image);
        }
    }
}
