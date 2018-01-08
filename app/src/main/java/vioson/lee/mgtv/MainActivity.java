package vioson.lee.mgtv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import vioson.lee.mgtv.network.ApiException;
import vioson.lee.mgtv.network.BaseObserver;
import vioson.lee.mgtv.network.Requester;
import vioson.lee.mgtv.pojo.Step1Data;
import vioson.lee.mgtv.pojo.Step2Response;

public class MainActivity extends AppCompatActivity {
    private EditText number;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        number = findViewById(R.id.video_number);
        String lastVideoID = AppDataHandler.getLastVideoId(this);
        if (!TextUtils.isEmpty(lastVideoID)) {
            number.setText(lastVideoID);
        }
        message = findViewById(R.id.message);
    }

    public void play(View view) {
        String videoNumber = number.getText().toString().trim();
        if (TextUtils.isEmpty(videoNumber))
            Toast.makeText(this, "请填写视频编号", Toast.LENGTH_SHORT).show();
        AppDataHandler.saveLastVideoId(this, videoNumber);
        jiexi(videoNumber);
    }

    private void jiexi(String videoNumber) {
        message.setText("正在获取视频地址");
        Requester.stepOne(videoNumber, new BaseObserver<Step1Data>() {
            @Override
            protected void onHandleSuccess(Step1Data data) {
                if (data != null && data.getStream() != null) {
                    List<Step1Data.StreamBean> stream = data.getStream();
                    for (int i = stream.size() - 1; i >= 0; i--) {
                        Step1Data.StreamBean streamBean = stream.get(i);
                        if (!TextUtils.isEmpty(streamBean.getUrl())) {
                            jiexi2(streamBean.getUrl());
                            break;
                        }
                    }
                }
            }

            @Override
            protected void onHandleError(ApiException e) {
                super.onHandleError(e);
                message.setText(e.getMessage());
            }
        });
    }

    private void jiexi2(String url) {
        message.setText("正在获取视频播放地址");
        Requester.stepTwo(url, new Observer<Step2Response>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Step2Response data) {
                if (data != null) {
                    jiexi3(data.getInfo());
                }
            }

            @Override
            public void onError(Throwable e) {
                message.setText(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

        });
    }

    private void jiexi3(String info) {
        if (TextUtils.isEmpty(info)) {
            message.setText("播放地址为空");
            return;
        }
        try {
            info = info.replace("http://", "");
            info = info.replace("https://", "");
            String file = info.substring(info.indexOf("/"), info.indexOf("?"));
            String fid = file;
            for (int i = 0; i < 5; i++) {
                fid = fid.substring(fid.indexOf("/") + 1);
            }
            fid = fid.substring(0, fid.indexOf("_"));
            String fmt = "4";
            String pno = "3001";
            String videoUrl = UrlTool.getVideoUrl(fmt, pno, fid, file);
            message.setText(String.format("播放地址为：%s", videoUrl));
            PlayActivity.launch(this, videoUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
