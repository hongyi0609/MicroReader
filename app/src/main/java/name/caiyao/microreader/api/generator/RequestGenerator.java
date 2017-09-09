package name.caiyao.microreader.api.generator;

import java.io.File;
import java.io.IOException;

import name.caiyao.microreader.MicroApplication;
import name.caiyao.microreader.utils.NetWorkUtil;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by hongy_000 on 2017/9/9.
 */

public class RequestGenerator {
    private RequestGenerator() {

    }

    private final static String CACHE_CONTROL = "Cache-Control"; //指定Response-Request遵循的缓存规则
    private final static Object monitor = new Object(); // 对象锁
    private final static Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            if (NetWorkUtil.isNetWorkAvailable(MicroApplication.getContext())) {
                int maxAge = 60; // 在线缓存在1分钟内可读取
                return originalResponse.newBuilder()
                        .removeHeader("Prama")  //Prama：no-cache 类似于Control-Cache缓存，主要用于向后兼容
                        .removeHeader(CACHE_CONTROL) // Cache-Control：Public，Cache-Control：Private，
                        // Cache-Control：no-cache
                        .header(CACHE_CONTROL, "public, max-age=" + maxAge) //可以被任何缓存，缓存1分钟
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28; // 离线时缓存保存4周
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader(CACHE_CONTROL)
                        .header(CACHE_CONTROL, "public, only-if-cached, max-stale=" + maxStale)//只返回不超过最大缓存周期，
                        //的缓存数据
                        .build();
            }
        }
    };

    private static File httpCacheDirectory = new File(MicroApplication.getContext().getCacheDir(), "httpCache");
    private static final int CACHE_SIZE = 1000 * 1000 * 10; // 10M
    private static Cache cache = new Cache(httpCacheDirectory, CACHE_SIZE);

    public static void setHttpCacheDirectory(String name) {
        httpCacheDirectory = new File(MicroApplication.getContext().getCacheDir(), name);
    }

    public static File getHttpCacheDirectory() {
        return httpCacheDirectory;
    }

    public static void cacheConfigration(File httpCacheDirectory, int cacheSize) {
        cache = new Cache(httpCacheDirectory, cacheSize);
    }


    private static OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
            .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
            .cache(cache)
            .build();

    private static Retrofit retrofit = null;

    public static Retrofit obtainRetrofit(String baseUrl, Converter.Factory factory) {

        if (factory instanceof GsonConverterFactory) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } else if (factory instanceof SimpleXmlConverterFactory) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    public static <T> T getServiceApi(final Class<T> service) {
//        return new Retrofit.Builder()
//                        .baseUrl(baseUrl)
//                        .client(client)
//                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .build().create(service);
        return retrofit.create(service);
    }
}
