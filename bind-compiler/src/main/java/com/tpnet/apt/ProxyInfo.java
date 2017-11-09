package com.tpnet.apt;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;


public class ProxyInfo {


    private String packageName;   //包名 --com.tpnet.processordemo

    private String proxyClassName;  // 生成的类的名称 --IndexMativity$$ViewInject

    private TypeElement typeElement;  // 类

    //存放view的id,元素
    public Map<Integer, VariableElement> injectVariables = new HashMap<>();

    public static final String PROXY = "ViewInject";   //这个名称，需要对应api的Module里面的ViewInject接口的名称

    public ProxyInfo(Elements elementUtils, TypeElement classElement) {
        this.typeElement = classElement;

        //获取包名
        PackageElement packageElement = elementUtils.getPackageOf(classElement);
        this.packageName = packageElement.getQualifiedName().toString();

        //生成 生成类的名称 -- IndexMativity$$ViewInject
        this.proxyClassName = classElement.getSimpleName() + "$$" + PROXY;
    }


    /**
     * 生成java文件代码
     * @return
     */
    public String generateJavaCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("// Generated code. Do not modify!\n");
        builder.append("package ").append(packageName).append(";\n\n");

        //注意，这个ImPort的包路径，是api的包路径
        builder.append("import com.tpnet.apt.*;\n");
        builder.append('\n');

        builder.append("public class ").append(proxyClassName).append(" implements " + ProxyInfo.PROXY + "<" + typeElement.getQualifiedName() + ">");
        builder.append(" {\n");

        generateMethods(builder);
        builder.append('\n');

        builder.append("}\n");
        return builder.toString();

    }


    /**
     * 生成方法
     * @param builder
     */
    private void generateMethods(StringBuilder builder) {

        builder.append("@Override\n ");
        builder.append("public void inject(" + typeElement.getQualifiedName() + " host, Object source ) {\n");

        for (int id : injectVariables.keySet()) {
            VariableElement element = injectVariables.get(id);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            builder.append(" if(source instanceof android.app.Activity){\n");
            builder.append("host." + name).append(" = ");
            builder.append("(" + type + ")(((android.app.Activity)source).findViewById( " + id + "));\n");
            builder.append("\n}else{\n");
            builder.append("host." + name).append(" = ");
            builder.append("(" + type + ")(((android.view.View)source).findViewById( " + id + "));\n");
            builder.append("\n}");
        }
        builder.append("  }\n");


    }

    public String getProxyClassFullName() {
        return packageName + "." + proxyClassName;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }


}