package vladimir.yandex;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import vladimir.yandex.entity.Result;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Result> mCharacters;
    private Context mContext;

    private static final int REGULAR_ITEM = 0;
    private static final int LOADING_ITEM = 1;


    public GalleryAdapter(Context context){
        this.mContext = context;
        mCharacters = new ArrayList<>();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == REGULAR_ITEM){
            View view = inflater.inflate(R.layout.gallery_item, parent, false);
            viewHolder = new RegularViewHolder(view);
        }else if(viewType == LOADING_ITEM){
            View view = inflater.inflate(R.layout.gallery_item, parent, false);
            viewHolder = new LoadingViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mCharacters.size() - 1) ? LOADING_ITEM : REGULAR_ITEM;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)){
            case REGULAR_ITEM:
                final RegularViewHolder regularViewHolder = (RegularViewHolder) holder;
                Glide.with(regularViewHolder.mImage.getContext())
                        .load(mCharacters.get(position).getImage())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(regularViewHolder.mImage);
                regularViewHolder.mImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), PhotoActivity.class);
                        intent.putExtra("URL", mCharacters.get(position).getImage());
                        v.getContext().startActivity(intent);
                    }
                });
                break;
            case LOADING_ITEM:
                LoadingViewHolder loadingVH = (LoadingViewHolder) holder;
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mCharacters.size();
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
    static class RegularViewHolder extends RecyclerView.ViewHolder{

        ImageView mImage;
        RegularViewHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.image);
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View itemView) {
            super(itemView);

        }
    }
}
