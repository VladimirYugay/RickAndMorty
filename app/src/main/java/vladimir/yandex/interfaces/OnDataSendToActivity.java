package vladimir.yandex.interfaces;

import java.util.List;

import vladimir.yandex.entity.Result;

public interface OnDataSendToActivity {
    public void sendData(List<Result> results);
}
