package com.tpnet.apt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;


public class ProxyInfo {


    public String packageName;   //包名 --com.tpnet.processordemo

    public String typeFullName;   //Activity全路径 --com.tpnet.processordemo.IndexActivity

    public String proxyClassName;  // 生成的类的名称 --IndexMativity$$ViewInject

    public TypeElement typeElement;  // 类

    //存放view的id,元素
    public Map<Integer, VariableElement> injectVariables = new HashMap<>();

    public static final String PROXY = "ViewInject";   //这个名称，需要对应api的Module里面的ViewInject接口的名称

    public ProxyInfo(Elements elementUtils, TypeElement classElement) {
        this.typeElement = classElement;

        //获取包名
        PackageElement packageElement = elementUtils.getPackageOf(classElement);
        this.packageName = packageElement.getQualifiedName().toString();

        this.typeFullName = classElement.getQualifiedName().toString();

        //生成 生成类的名称 -- IndexMativity$$ViewInject
        this.proxyClassName = classElement.getSimpleName() + "$$" + PROXY;
    }



    public String getProxyClassFullName() {
        return packageName + "." + proxyClassName;
    }



    public TypeElement getTypeElement() {
        return typeElement;
    }


    /**
     * 生成java文件
     */
    public void generateJavaFile() throws IOException {




    }

    public static void main(String[] args){

    }
}