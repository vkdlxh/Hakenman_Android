package archiveasia.jp.co.hakenman.extension

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val isPending = AtomicBoolean(false)

    /**
     * 1. 値が変更されるとisPendingをtrueに変更しObserverを呼び出す
     */
    @MainThread
    override fun setValue(value: T?) {
        isPending.set(true)
        super.setValue(value)
    }

    /**
     * 2. isPendingがtrueか確認し、내부에 등록된 Observer는 isPending이 true인지 확인하고,
     *    trueの場合またfalseに変えてから変更されたというイベントをアナウンスする
     */
    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer {
            if (isPending.compareAndSet(true, false)) {
                observer.onChanged(it)
            }
        })
    }

    /**
     * TがVoidの場合楽に呼び出せるメソッド
     */
    @MainThread
    fun call() {
        value = null
    }
}