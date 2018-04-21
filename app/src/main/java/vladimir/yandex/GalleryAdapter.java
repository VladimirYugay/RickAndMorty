package vladimir.yandex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.Duration;

import vladimir.yandex.entity.Result;
import vladimir.yandex.utils.RetryCallback;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Result> mCharacters;
    protected final int REGULAR_ITEM = 0;
    protected final int LOADING_ITEM = 1;

    protected boolean INTERNET_ERROR = false;
    protected boolean DATA_ERROR = false;

    private RetryCallback mCallback;

    public GalleryAdapter(Context context) {
        mCharacters = new ArrayList<>();
        mCallback = (RetryCallback) context;
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
            View view = inflater.inflate(R.layout.gallery_item_loading, parent, false);
            viewHolder = new LoadingViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == mCharacters.size()){
            return LOADING_ITEM;
        }else {
            return REGULAR_ITEM;
        }
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
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                if(INTERNET_ERROR){
                    loadingViewHolder.mErrorLayouyt.setVisibility(View.VISIBLE);
                    loadingViewHolder.mProgress.setVisibility(View.GONE);
                    loadingViewHolder.mErrorText.setText("Проверьте ваше интернет соединение");
                }else if(DATA_ERROR){
                    loadingViewHolder.mErrorLayouyt.setVisibility(View.VISIBLE);
                    loadingViewHolder.mProgress.setVisibility(View.GONE);
                    loadingViewHolder.mErrorText.setText("Вы загрузили все картинки");
                } else {
                    loadingViewHolder.mErrorLayouyt.setVisibility(View.GONE);
                    loadingViewHolder.mProgress.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mCharacters.size() + 1;
    }


    /*
        Вспомогательные функции
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
        Вспомогательные функции для работы с ошибками
   _________________________________________________________________________________________________
    */

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

    class LoadingViewHolder extends RecyclerView.ViewHolder{
        ProgressBar mProgress;
        LinearLayout mErrorLayouyt;
        TextView mErrorText;
        ImageButton mRetryButton;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            mProgress = itemView.findViewById(R.id.loadmore_progress);
            mErrorLayouyt = itemView.findViewById(R.id.loadmore_errorlayout);
            mErrorText = itemView.findViewById(R.id.loadmore_errortxt);
            mRetryButton = itemView.findViewById(R.id.loadmore_retry);

            mRetryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.retryLoad();
                }
            });
        }
    }
}
