package name.caiyao.microreader.api.weixin;

import java.io.File;
import java.io.IOException;

import name.caiyao.microreader.MicroApplication;
import name.caiyao.microreader.api.generator.RequestGenerator;
import name.caiyao.microreader.utils.NetWorkUtil;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by 蔡小木 on 2016/3/4 0004.
 */
public class TxRequest {

    private TxRequest() {}

    private static final String CACHE_CONTROL = "Cache-Control";
    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            if (NetWorkUtil.isNetWorkAvailable(MicroApplication.getContext())) {
                int maxAge = 60; // 在线缓存在1分钟内可读取
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader(CACHE_CONTROL)
                        .header(CACHE_CONTROL, "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28; // 离线时缓存保存4周
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader(CACHE_CONTROL)
                        .header(CACHE_CONTROL, "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    };
    private static File httpCacheDirectory = new File(MicroApplication.getContext().getCacheDir(), "txCache");

    private static int cacheSize = 10 * 1024 * 1024; // 10 MiB
    private static Cache cache = new Cache(httpCacheDirectory, cacheSize);
    private static OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
            .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
            .cache(cache)
            .build();

    private static TxApi txApi = null;
    private static final Object monitor = new Object();
    public static TxApi getTxApi() {
        synchronized (monitor){
            if (txApi == null) {
//                txApi = new Retrofit.Builder()
//                        .baseUrl("http://api.huceo.com")
//                        .client(client)
//                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .build().create(TxApi.class);
                RequestGenerator.setHttpCacheDirectory("txCache");
                RequestGenerator.cacheConfigration(RequestGenerator.getHttpCacheDirectory(), 10*1000*1000);
                RequestGenerator.obtainRetrofit("http://api.huceo.com", GsonConverterFactory.create());
                txApi = RequestGenerator.getServiceApi(TxApi.class);
            }
            return txApi;
        }
    }
}
