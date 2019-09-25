package com.tm.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

import java.io.File;
import java.net.URL;

/**
 * @author hasee
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WebView webView = findViewById(R.id.web);
        initWeb(webView);
        findViewById(R.id.btn_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("https://activity.tuia.cn/activity/index?id=15564&slotId=305733&login=normal&appKey=3zHqTHGuvNp13ckCto2LQiAfyGsi&deviceId=3ddb13cc-42cd-4720-8f9a-17de18de38bd&tenter=SOW&subActivityWay=1&tck_rid_6c8=0ad0276bjzwmdbm0-7348142&tck_loc_c5d=tactivity-15564&dcm=401.305733.0.0&&tenter=SOW&specialType=0&userType=2&isTestActivityType=0&visType=0");
            }
        });
    }

    private void initWeb(final WebView webView) {
        webView.addJavascriptInterface(new TAHandler(), "TAHandler");
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(false);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSetting.setMediaPlaybackRequiresUserGesture(false);
        }
        //发奖必须加
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    if (url == null) {
                        return false;
                    }
                    //处理普通请求
                    if (url.startsWith("http") || url.startsWith("https")) {
                        /**
                         * 如果是8.0以上的,则不调用loadUrl,并返回false
                         */
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            return false;
                        }
                        if (webView != null) {
                            webView.loadUrl(url);
                        }
                        return true;
                    } else {
                        /**
                         * 防止  找不到网页net:err_unknown_url_scheme
                         * 支持scheme协议
                         */
                        startActivity(MainActivity.this, Uri.parse(url));
                        return true;
                    }
                } catch (Exception e) {
                    //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    e.printStackTrace();
                    //返回true 代表让webview自己执行
                    return true;
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                startDown(url);
            }
        });
    }


    /**
     * 根据Uri 调起应用
     *
     * @param context
     * @param uri
     * @return
     */
    public boolean startActivity(Context context, Uri uri) {
        if (context == null) {
            return false;
        }
        if (uri == null) {
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PackageManager packageManager = context.getPackageManager();
        ResolveInfo resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo == null) {
            return false;
        }
        context.startActivity(intent);
        return true;
    }

    /**
     * 开始下载任务
     * V1.0.00
     */
    private void startDown(final String url) {
        final String apkFilePath = CommonUtils.getDownLoadPath() + EncryptUtils.encryptMD5ToString(url) + ".apk";
        if (FileDownloader.getImpl().getStatus(url, apkFilePath) != FileDownloadStatus.progress) {
            FileDownloader.getImpl().create(url).setPath(apkFilePath, false)
                    .setListener(new FileDownloadListener() {

                        @Override
                        protected void started(BaseDownloadTask task) {
                            super.started(task);
                        }

                        @Override
                        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                            super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                        }

                        @Override
                        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                        }

                        @Override
                        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                        }

                        @Override
                        protected void completed(BaseDownloadTask task) {
                            File file = new File(apkFilePath);
                            if (file != null && file.exists()) {
                                final boolean rename = FileUtils.rename(file, EncryptUtils.encryptMD5ToString(url) + "a.apk");
                                if (rename) {
                                    final File renameFile = CommonUtils.checkFileExit(EncryptUtils.encryptMD5ToString(url ) + "a.apk");
                                    if (renameFile != null && renameFile.exists()) {
                                        FileUtils.delete(apkFilePath);
                                        AppUtil.openFile(MainActivity.this, renameFile);
                                    }
                                } else {
                                    AppUtil.openFile(MainActivity.this, file);
                                }
                            }
                        }

                        @Override
                        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        }

                        @Override
                        protected void error(BaseDownloadTask task, Throwable e) {
                        }

                        @Override
                        protected void warn(BaseDownloadTask task) {
                        }

                        @Override
                        protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
                            super.retry(task, ex, retryingTimes, soFarBytes);
                        }
                    })
                    .setTag(url)
                    .start();
        }else {
            Toast.makeText(MainActivity.this,"下载中...",Toast.LENGTH_LONG);
        }
    }

    /**
     * js交互类
     */
    public class TAHandler {

        /**
         * 发奖接口
         * @param data
         */
        @JavascriptInterface
        public void reward(String data) {
            Log.d("TAHandler","data= "+data);
        }

        /**
         * 关闭事件
         */
        @JavascriptInterface
        public void close() {
            Log.d("TAHandler","close ");
           runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    finish();
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
