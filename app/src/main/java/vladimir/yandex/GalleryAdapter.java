package vladimir.yandex;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import vladimir.yandex.entity.Characters;
import vladimir.yandex.entity.Result;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{

    private List<Result> mCharacters;
    private Context mContext;

    public GalleryAdapter(Context context){
        this.mContext = context;
        mCharacters = new ArrayList<>();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide.with(holder.mImage.getContext())
                .load(mCharacters.get(position).getImage())
                .into(holder.mImage);
        holder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PhotoActivity.class);
                intent.putExtra("URL", mCharacters.get(position).getImage());
                    v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCharacters == null ? 0: mCharacters.size();
    }


    /*
        Функции для  бесконечной подгрузки
   _________________________________________________________________________________________________
    */

    public void add(Result result){
        mCharacters.add(result);
        notifyItemInserted(mCharacters.size() - 1);
    }

    public void addAll(List<Result> results){
        for(Result result: results){
            add(result);
        }
    }

    /*
        Вьюхолдеры
   _________________________________________________________________________________________________
    */
    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView mImage;
        ViewHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.image);
        }
    }
}
