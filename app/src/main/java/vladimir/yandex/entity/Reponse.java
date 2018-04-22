package vladimir.yandex.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reponse implements Parcelable {

    @SerializedName("info")
    @Expose
    private Info info;
    @SerializedName("results")
    @Expose
    private List<Result> results = null;

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.info, flags);
        dest.writeTypedList(this.results);
    }

    public Reponse() {
    }

    protected Reponse(Parcel in) {
        this.info = in.readParcelable(Info.class.getClassLoader());
        this.results = in.createTypedArrayList(Result.CREATOR);
    }

    public static final Parcelable.Creator<Reponse> CREATOR = new Parcelable.Creator<Reponse>() {
        @Override
        public Reponse createFromParcel(Parcel source) {
            return new Reponse(source);
        }

        @Override
        public Reponse[] newArray(int size) {
            return new Reponse[size];
        }
    };
}