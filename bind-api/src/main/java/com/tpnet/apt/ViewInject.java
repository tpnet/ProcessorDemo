package com.tpnet.apt;

/**
 * Created by Litp on 2017/9/19.
 */

public abstract interface ViewInject<T> {
    void inject(T t, Object object);
}
