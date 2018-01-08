package vioson.lee.mgtv.network;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import vioson.lee.mgtv.pojo.Step1Data;
import vioson.lee.mgtv.pojo.Step2Response;


/**
 * Created by viosonlee
 * on 2018/1/6.
 * for
 */

public interface Api {
    @GET("player/video")
    Observable<BaseResponse<Step1Data>> stepOne(@Query("video_id") String videoID);

    @GET("vod.do")
    Observable<Step2Response> stepTwo(@QueryMap Map<String, String> map);

}
