package com.lhzw.searchlocmap.net;

import com.lhzw.searchlocmap.bean.AllBDInfosBean;
import com.lhzw.searchlocmap.bean.BaseBean;
import com.lhzw.searchlocmap.bean.BindingWatchBean;
import com.lhzw.searchlocmap.bean.NetResponseBean;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by hecuncun on 2019/5/13
 */
public interface Api {
//    /**
//     * 测试登陆接口
//     * @param loginName
//     * @param password
//     * @return
//     */
//    @GET("security/login")
//    Observable<BaseBean<UserInfo>> loginCall(@Query("loginName") String loginName, @Query("password") String password);

    /**
     * 手持机与手表绑定接口
     *
     * @param handsetNumber 手持机北斗号
     * @param childNumber   手表的固话注册码
     * @return
     */
    @POST("handsets/binding/{handsetNumber}-{childNumber}")
    Observable<BaseBean> canBinding(@Path("handsetNumber") String handsetNumber, @Path("childNumber") String childNumber);

    /**
     * 手持机与手表解绑接口
     *
     * @param handsetNumber 手持机的北斗号
     * @param childNumber   手表的固话注册码   如果是多个之间用,隔开
     * @return
     */
    @DELETE("handsets/binding/{handsetNumber}-{childNumber}")
    Observable<BaseBean> deleteBinding(@Path("handsetNumber") String handsetNumber, @Path("childNumber") String childNumber);

    /**
     * 获取所有的北斗信息
     * @return
     */
    @GET("bds")
    Observable<AllBDInfosBean> getAllBDInfos();

    /**
     * 4g 通信接口
     */

    @POST("remoteapi/bd")
    Observable<NetResponseBean> uploadInfo(@Body RequestBody requestBody);

    /**
     * 根据手持机的北斗号  查询绑定的手表
     */
    @GET("handsets/binding/{handsetNumber}/watch")
    Observable<BindingWatchBean> getBindingWatch(@Path("handsetNumber") String handsetNumber);
}