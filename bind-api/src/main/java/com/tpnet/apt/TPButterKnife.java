package com.tpnet.apt;

import android.app.Activity;

/**
 * Created by Litp on 2017/9/19.
 */

public class TPButterKnife  {


    private static final String SUFFIX = "$$" + ViewInject.class.getSimpleName();



    public static void inject(Activity activity){

        inject(activity,activity);

    }

    public static void inject(Object host, Object root){

        Class<?> clazz = host.getClass();

        //拼接类的全路径，
        String proxyClassFullName = clazz.getName()+SUFFIX;
        //省略try,catch相关代码
        Class<?> proxyClazz = null;

        try {
            proxyClazz = Class.forName(proxyClassFullName);

            //通过newInstance生成实例，强转，调用代理类的inject方法
            ViewInject viewInject = (ViewInject) proxyClazz.newInstance();

            //调用生成类里面的inject方法，进行findView
            viewInject.inject(host,root);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

}
