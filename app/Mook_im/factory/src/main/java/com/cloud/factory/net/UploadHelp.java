package com.cloud.factory.net;

import android.text.format.DateFormat;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.cloud.common.utils.HashUtil;
import com.cloud.factory.Factory;

import java.io.File;
import java.util.Date;

/**
 * Created by fmm on 2017/8/24.
 */

public class UploadHelp {
    private static String Tag = UploadHelp.class.getSimpleName();
    private static String ENDPOINT = "http://oss-cn-shanghai.aliyuncs.com";
    private static String BUCKETNAME = "fdouble";




    /**
     * 上传图片
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadImage(String path){
        String url = upload(getImageObjKey(path),path);
        return url;
    }

    /**
     * 上传头像
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadPortrait(String path){
        String url = upload(getPortraitObjKey(path),path);
        return url;
    }

    /**
     * 上传音频
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadAudio(String path){
        String url = upload(getAudioObjeKey(path),path);
        return url;
    }

    private static OSSClient getOssClient() {
        // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考访问控制章节
        OSSCredentialProvider credentialProvider = new
                OSSPlainTextAKSKCredentialProvider("LTAIBZr2n3nbVeK7"
                , "R0ZanvhqiQWZTffQ1lKJeVZ75jjyVe");

        return new OSSClient(Factory.app(), ENDPOINT, credentialProvider);
    }


    private static String upload(String objKey, String path) {
        PutObjectRequest request = new PutObjectRequest(BUCKETNAME, objKey, path);

        try {
            OSSClient ossClient = getOssClient();
            PutObjectResult result = ossClient.putObject(request);
            String url = ossClient.presignPublicObjectURL(BUCKETNAME, objKey);
            Log.d(Tag, String.format("presignPublicObjectURL:%s", url));
            return url;
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return "";
    }


    // image/201703/dawewqfas243rfawr234.jpg
    private static String getImageObjKey(String path){
        String fileMd5 = HashUtil.getMD5String(path);
        String dateString = getDateString();

        return String.format("image/%s/%s.jpg",dateString,fileMd5);
    }


    // portrait/201703/dawewqfas243rfawr234.jpg
    private static String getPortraitObjKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("portrait/%s/%s.jpg", dateString, fileMd5);
    }
    // audio/201703/dawewqfas243rfawr234.jpg
    private static String getAudioObjeKey(String path){
        String fileMD5 = HashUtil.getMD5String(path);
        String dateString = getDateString();
        return String.format("Audio/%s/%s.mp3",dateString,fileMD5);
    }
    /**
     * 分月存储，避免一个文件夹太多
     *
     * @return yyyyMM
     */
    public static String getDateString() {
        return DateFormat.format("yyyyMM", new Date()).toString();
    }

}
