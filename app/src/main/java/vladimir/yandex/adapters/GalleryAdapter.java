package vladimir.yandex.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.ArrayList;
import java.util.List;
import vladimir.yandex.Constants;
import vladimir.yandex.R;
import vladimir.yandex.RetryCallback;
import vladimir.yandex.activities.GalleryActivity;
import vladimir.yandex.activities.PhotoActivity;
import vladimir.yandex.entity.Result;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Result> mCharacters;
    public final int REGULAR_ITEM = 0;
    public final int LOADING_ITEM = 1;
    public boolean ERROR = false;
    public boolean isFooterAdded = false;
    RetryCallback mCallback;

    public GalleryAdapter(Context context) {
        mCharacters = new ArrayList<>();
        mCallback = (RetryCallback) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == REGULAR_ITEM){
            View view = inflater.inflate(R.layout.gallery_item, parent, false);
            viewHolder = new RegularViewHolder(view);
        }else if(viewType == LOADING_ITEM){
            View view = inflater.inflate(R.layout.gallery_item_loading, parent, false);
            viewHolder = new LoadingViewHolder(view);
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == REGULAR_ITEM){
                final RegularViewHolder regularViewHolder = (RegularViewHolder) holder;
                Glide.with(regularViewHolder.mImage.getContext())
                        .load(mCharacters.get(position).getImage())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(regularViewHolder.mImage);
                regularViewHolder.mImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), PhotoActivity.class);
                        intent.putExtra(Constants.URL, mCharacters.get(position).getImage());
                        intent.putExtra(Constants.NAME, mCharacters.get(position).getName());
                        v.getContext().startActivity(intent);
                    }
                });
        }else{
            final LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            if(ERROR){
                loadingViewHolder.mErrorLayouyt.setVisibility(View.VISIBLE);
                loadingViewHolder.mProgress.setVisibility(View.GONE);
            }else{
                loadingViewHolder.mErrorLayouyt.setVisibility(View.GONE);
                loadingViewHolder.mProgress.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public int getItemCount() {
        return mCharacters.size();
    }


    @Override
    public int getItemViewType(int position) {
        return (position == mCharacters.size() - 1 && isFooterAdded) ? LOADING_ITEM : REGULAR_ITEM;
    }

    /*
        Вспомогательные функции для загрузки данных в адаптер
   _________________________________________________________________________________________________
    */
    public void add(Result result){
        mCharacters.add(result);
        notifyItemInserted(mCharacters.size() - 1);
    }

    public void addAll(List<Result> results){
        for(Result result : results){
            add(result);
        }
        //Говорим, что все норм, ошибок нет, говорим, что можно добавить вью типа футер, добавляе пустой объект.
        ERROR = false;
        isFooterAdded = true;
        add(new Result());
    }

    public void removeFooter(){
        //Говорим, что нельяз добавлять вью типа футер, удаляем последний элемент, перерисовываем.
        int position = mCharacters.size() - 1;
        if(position > 0){
            isFooterAdded = false;
            mCharacters.remove(mCharacters.size() - 1);
            notifyItemRemoved(mCharacters.size() - 1);
        }
    }

    public void addErrorFooter(){
        //Если ошибки до этого не было, говорим что есть, убираем футер, говорим, что ошибка и что можно добавить вью типа футер
        //добавляем элемент
        if(!ERROR){
            removeFooter();
            ERROR = true;
            isFooterAdded = true;
            add(new Result());
        }
    }


    /*
        Вспомогательные функции для сохранения данных после поворота экрана
   _________________________________________________________________________________________________
    */
    public List<Result> getGalleryItems(){
        return mCharacters;
    }

    /*
        Вьюхолдеры
        В пдане Java, нестатичные классы внутри класса ведут к утечке памяти
        Однако, в данном случае, они будут использоваться только с данным
        адаптером, поэтому что они статик, что нет, разницы нет.
   _________________________________________________________________________________________________
    */
    class RegularViewHolder extends RecyclerView.ViewHolder{
        ImageView mImage;
        RegularViewHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.photoImage);
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ProgressBar mProgress;
        LinearLayout mErrorLayouyt;
        ImageButton mRetryButton;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            mProgress = itemView.findViewById(R.id.loadmore_progress);
            mErrorLayouyt = itemView.findViewById(R.id.loadmore_error_layout);
            mRetryButton = itemView.findViewById(R.id.button_retry);
            mRetryButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCallback.retryPageLoad();
        }
    }
}
