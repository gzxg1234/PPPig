package com.sanron.pppig.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.sanron.datafetch_interface.Source
import com.sanron.datafetch_interface.SourceManager
import com.sanron.pppig.app.PiApp
import com.sanron.pppig.util.CLog
import com.sanron.pppig.util.MainHandler
import dalvik.system.DexClassLoader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.concurrent.thread

/**
 * Author:sanron
 * Time:2019/5/13
 * Description:
 */
@SuppressLint("StaticFieldLeak")
object FetchManager {

    val TAG = FetchManager::class.java.simpleName

    lateinit var context: Context

    lateinit var sourceManager: SourceManager

    private val souceMap = mutableMapOf<String, Source>()

    val sp: SharedPreferences by lazy {
        return@lazy context.getSharedPreferences("FETCH_CONFIG", Context.MODE_PRIVATE)
    }

    /**
     * 切换视频数据源
     */
    fun changeSource(id: String, save: Boolean = false) {
        souceMap[id]?.let {
            Repo.dataFetch = it.fetch
            if (save) {
                sp.edit()
                        .putString("currentFetchId", id)
                        .apply()
            }
        }
    }

    fun getSourceById(id: String): Source? {
        return souceMap[id]
    }

    fun currentSource(): Source? {
        return souceMap[currentSourceId()]
    }

    fun currentSourceId(): String? {
        return sp.getString("currentFetchId", "")
    }

    private fun readFetchConfig() {
        val id = currentSourceId()
        if (id.isNullOrEmpty()) {
            changeSource(sourceManager.getSourceList()[0].id, true)
        } else {
            changeSource(id, false)
        }
    }

    private fun getFetchVersion(): Int {
        return sp.getInt("fetchVersion", -1)
    }

    private fun setFetchVersion(v: Int): Boolean {
        return sp.edit()
                .putInt("fetchVersion", v)
                .commit()
    }

    /**
     * jar包路径
     */
    private fun getJarPath(context: Context): String {
        return context.filesDir.absolutePath + File.separator + "fetch.jar"
    }

    /**
     * assets内置包名称
     */
    private fun getAssetsFetchJarFileName(context: Context): String {
        return "fetch.jar"
    }

    /**
     * 加载SourceManager实现类
     */
    private fun loadSourceManager(path: String): SourceManager? {
        val classLoader = DexClassLoader(path, PiApp.sInstance.cacheDir.absolutePath, null, FetchManager::class.java.classLoader)
        try {
            val clazz = classLoader.loadClass("com.sanron.datafetch.SourceManagerImpl")
            val constructor = clazz.getConstructor()
            val obj = constructor.newInstance()
            return obj as SourceManager
        } catch (e: ClassNotFoundException) {
            CLog.e(TAG, "未找到实现类", e)
        } catch (e: NoSuchMethodException) {
            CLog.e(TAG, "未找到实现类构造方法", e)
        } catch (e: ClassCastException) {
            CLog.e(TAG, "未实现SourceManager接口", e)
        }
        return null
    }

    /**
     * 初始化加载
     */
    fun init(context: Context, success: () -> Unit, failed: (String) -> Unit) {
        this.context = context.applicationContext
        thread {
            val fetchVersion = getFetchVersion()
            val jarPath = File(getJarPath(context))
            var sm: SourceManager? = null
            if (fetchVersion == -1) {
                //应用第一次启动，加载assets内置jar包
                var assetJarInputStream: InputStream? = null
                var jarOutStream: OutputStream? = null
                try {
                    assetJarInputStream = context.assets.open(getAssetsFetchJarFileName(context))
                    jarPath.delete()
                    if (!jarPath.createNewFile()) {
                        MainHandler.post {
                            failed("创建文件失败")
                        }
                    }
                    jarOutStream = jarPath.outputStream()
                    assetJarInputStream.copyTo(jarOutStream)
                    sm = loadSourceManager(jarPath.absolutePath)
                    if (sm != null && !sm.getSourceList().isNullOrEmpty()) {
                        setFetchVersion(sm.getVersion())
                    }
                } catch (e: IOException) {
                    MainHandler.post {
                        failed("加载失败")
                    }
                    jarPath.delete()
                } finally {
                    try {
                        jarOutStream?.close()
                        assetJarInputStream?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } else {
                sm = loadSourceManager(jarPath.absolutePath)
            }
            if (sm != null && !sm.getSourceList().isNullOrEmpty()) {
                sourceManager = sm
                sourceManager.getSourceList().forEach {
                    souceMap[it.id] = it
                }
                sourceManager.initContext(context = PiApp.sInstance)
                sourceManager.setHttpClient(Injector.provideOkHttpClient())
                readFetchConfig()
                MainHandler.post {
                    success()
                }
            } else {
                MainHandler.post {
                    failed("创建数据源失败")
                }
            }
        }
    }
}
