package vladimir.yandex;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{

    private int[] mDataset;

    public GalleryAdapter(int[] dataset){
        mDataset = dataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mImage.setText(String.valueOf(position));
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
        return mDataset.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mImage;
        public ViewHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.image);
        }
    }
}
