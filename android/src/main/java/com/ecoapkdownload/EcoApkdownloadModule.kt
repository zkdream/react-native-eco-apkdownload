package com.ecoapkdownload


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import com.hjq.http.EasyConfig
import com.hjq.http.EasyHttp
import com.hjq.http.EasyUtils
import com.hjq.http.config.IRequestInterceptor
import com.hjq.http.lifecycle.ApplicationLifecycle
import com.hjq.http.listener.OnDownloadListener
import com.hjq.http.model.HttpMethod
import com.hjq.http.request.HttpRequest
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.PermissionLists
import com.hjq.permissions.permission.base.IPermission
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.File

@ReactModule(name = EcoApkdownloadModule.NAME)
class EcoApkdownloadModule(reactContext: ReactApplicationContext) :
    NativeEcoApkdownloadSpec(reactContext) {

    override fun getName(): String {
        return NAME
    }

    // Example method
    // See https://reactnative.dev/docs/native-modules-android
    override fun downloadApk(path: String, name: String) {
        Log.d("EcoApkdownload", "downloadApk: $path")
        var totalSize: Long=0;

        val notificationManager =
            reactApplicationContext.getSystemService(NotificationManager::class.java)
        val notificationId = reactApplicationContext.applicationInfo.uid
        var channelId = ""

        // 适配 Android 8.0 通知渠道新特性
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                reactApplicationContext.getString(R.string.update_notification_channel_id),
                reactApplicationContext.getString(R.string.update_notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            channel.enableLights(false)
            channel.enableVibration(false)
            channel.vibrationPattern = longArrayOf(0)
            channel.setSound(null, null)
            notificationManager.createNotificationChannel(channel)
            channelId = channel.id
        }

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(reactApplicationContext, channelId)
                // 设置通知时间
                .setWhen(System.currentTimeMillis())
                // 设置通知标题
                .setContentTitle("ECOSteam")
                // 设置通知小图标
                .setSmallIcon(R.mipmap.ic_launcher_new1)
                // 设置通知大图标
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        reactApplicationContext.resources,
                        R.mipmap.ic_launcher_new1
                    )
                )
                // 设置通知静音
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                // 设置震动频率
                .setVibrate(longArrayOf(0))
                // 设置声音文件
                .setSound(null)
                // 设置通知的优先级
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // 创建要下载的文件对象
        val apkFile = File(
            reactApplicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "ECOSteam_v_" + name + ".apk"
        )

        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .build()

        EasyConfig.with(okHttpClient)
            // 是否打印日志
            .setLogEnabled(true)
            // 设置服务器配置
            .setServer(RequestServer())
            // 设置请求处理策略
            .setHandler(RequestHandler())
            // 设置请求重试次数
            .setRetryCount(1)
            .setInterceptor(object : IRequestInterceptor{

                override fun interceptResponse(
                    httpRequest: HttpRequest<*>?,
                    response: Response?
                ): Response {

                    val body = response?.body

                    if (body?.contentLength() == -1L) {
                        val length = response.headers.get("x-oss-meta-content-length")
                        totalSize = length!!.toLong()
                    } else {
                        totalSize = body?.contentLength()!!
                    }
                    return super.interceptResponse(httpRequest, response)

                }
            })

            .into()

        EasyHttp.download(ApplicationLifecycle.getInstance())
            .method(HttpMethod.GET)
            .file(apkFile)
            .url(path)


            .listener(object : OnDownloadListener {
                override fun onStart(file: File?) {
                    HProgressDialogUtils.showHorizontalProgressDialog(
                        getCurrentActivity(),
                        "下载进度",
                        false
                    )

                }

                override fun onByte(file: File?, totalByte: Long, downloadByte: Long) {
                    super.onByte(file, totalByte, downloadByte)
                    if(totalSize>downloadByte){
                        val progress = EasyUtils.getProgressProgress(totalSize, downloadByte)
                        Log.e("sss", "onByte: "+progress )
                        HProgressDialogUtils.setProgress(progress);
                        // 更新下载通知
                        notificationManager.notify(
                            notificationId, notificationBuilder
                                // 设置通知的文本

                                .setContentText(String.format(reactApplicationContext.getString(R.string.update_status_running)!!, progress))
                                // 设置下载的进度
                                .setProgress(100, progress, false)
                                // 设置点击通知后是否自动消失
                                .setAutoCancel(false)
                                // 是否正在交互中
                                .setOngoing(true)
                                // 重新创建新的通知对象
                                .build()
                        )
                    }

                }

                override fun onProgress(file: File, progress: Int) {
                    Log.e("sss", "progress: "+progress )

                }

                override fun onComplete(file: File) {
                    // 显示下载成功通知
                    notificationManager.notify(
                        notificationId, notificationBuilder
                            // 设置通知的文本
                            .setContentText(String.format(reactApplicationContext.getString(R.string.update_status_successful)!!, 100))
                            // 设置下载的进度
                            .setProgress(100, 100, false)
                            // 设置通知点击之后的意图
                            .setContentIntent(PendingIntent.getActivity(reactApplicationContext, 1, getInstallIntent(apkFile),
                                Intent.FILL_IN_ACTION or PendingIntent.FLAG_IMMUTABLE))
                            // 设置点击通知后是否自动消失
                            .setAutoCancel(true)
                            // 是否正在交互中
                            .setOngoing(false)
                            .build()
                    )
//                    updateView?.setText(R.string.update_status_successful)
//                    // 标记成下载完成
//                    downloadComplete = true

                    Log.e("sss", "onComplete: "+file.path )
                    HProgressDialogUtils.cancel()
                    // 安装 Apk
                    installApk(apkFile)
                }

                override fun onError(file: File, e: Exception) {
                    Log.e("sss", "Exception: "+file.path )
                    HProgressDialogUtils.cancel()
                    // 清除通知
                    notificationManager.cancel(notificationId)
//                    updateView?.setText(R.string.update_status_failed)
                    // 删除下载的文件
                    file.delete()
                }

                override fun onEnd(file: File) {
                    HProgressDialogUtils.cancel()
                    // 更新进度条
                }
            }).start()

    }

    private fun installApk(mApkFile: File) {
        XXPermissions.with(reactApplicationContext) // 申请多个权限
            .permission(PermissionLists.getRequestInstallPackagesPermission())
            .request(object : OnPermissionCallback {
                override fun onGranted(
                    permissions: MutableList<IPermission?>,
                    allGranted: Boolean
                ) {
                    if (allGranted) {
                        reactApplicationContext.startActivity(getInstallIntent(mApkFile))
                    } else {
                        Toast.makeText(reactApplicationContext,"没有安装权限",Toast.LENGTH_LONG).show()
                        val intent: Intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                        intent.data = Uri.parse("package:" + reactApplicationContext.packageName)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        reactApplicationContext.startActivity(intent)
                        return
                    }
                }
            })
    }

    /**
     * 获取安装意图
     */
    private fun getInstallIntent(mApkFile: File): Intent {
        val intent = Intent()
        intent.setAction(Intent.ACTION_VIEW)
        val uri: Uri?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(
                reactApplicationContext,
                "${reactApplicationContext.packageName}.fileprovider",
                mApkFile
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } else {
            uri = Uri.fromFile(mApkFile)
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }
    

    companion object {
        const val NAME = "EcoApkdownload"
    }
}
