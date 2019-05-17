package com.sanron.pppig.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.sanron.pppig.util.SingleLiveEvent
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Author:sanron
 * Time:2019/1/14
 * Description:
 */
open class BaseViewModel(application: Application) : AndroidViewModel(application) {

    private val mCompositeDisposable = CompositeDisposable()

    private val mCompositeMap = mutableMapOf<String, Disposable>()

    val toastMsg = SingleLiveEvent<String>()

    /**
     * 显示加载中，值为Dispose的tag值
     */
    val rxShowLoading = SingleLiveEvent<Disposable>()

    private fun autoDispose(disposable: Disposable) {
        if (!mCompositeDisposable.isDisposed) {
            mCompositeDisposable.add(disposable)
        } else {
            disposable.dispose()
        }
    }


    fun <T> withLoading(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream.doOnSubscribe { disposable ->
                rxShowLoading.value = disposable
            }.doFinally {
                rxShowLoading.value = null
            }
        }
    }

    fun <T> autoDispose(tag: String? = null): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream.doOnSubscribe { disposable ->
                tag?.let {
                    mCompositeMap.remove(tag)?.dispose()
                    mCompositeMap.put(tag, disposable)
                }
                autoDispose(disposable)
            }
        }
    }

    override fun onCleared() {
        mCompositeMap.clear()
        mCompositeDisposable.clear()
        super.onCleared()
    }
}
