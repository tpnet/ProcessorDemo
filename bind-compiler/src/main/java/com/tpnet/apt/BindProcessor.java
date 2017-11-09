package com.tpnet.apt;


import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;



@SupportedAnnotationTypes("com.tpnet.apt.BindView")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class BindProcessor extends AbstractProcessor {


    //自定义一些属性
    public static final String CUSTOM_ANNOTATION = "yuweiguoCustomAnnotation";


    //日志类
    private Messager mMessager;

    //文件处理,生成java源文件
    private Filer mFileUtils;

    //可以获取一些元素相关的信息
    private Elements mElementUtils;

    //存放需要处理的元素
    private Map<String, ProxyInfo> mProxyMap = new HashMap<String, ProxyInfo>();


    /**
     * 初始化变量
     * 当我们编译程序时注解处理器工具会调用此方法并且提供实现ProcessingEnvironment接口的对象作为参数。
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        //获取信息
        super.init(processingEnv);

        //返回实现Messager接口的对象，用于报告错误信息、警告提醒。
        mMessager = processingEnv.getMessager();

        //返回实现Filer接口的对象，用于创建文件、类和辅助文件。
        mFileUtils = processingEnv.getFiler();

        //用于元素处理
        mElementUtils = processingEnv.getElementUtils();

    }

    /**
     * 添加要处理的注解
     * @return
     */
/*    @Override
    public Set<String> getSupportedAnnotationTypes() {

        Set<String> annotation = new LinkedHashSet<>();
        annotation.add(BindView.class.getCanonicalName());
        return annotation;
    }*/

/*
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
*/

    /**
     * 自定义的一些参数
     * @return
     */
    @Override
    public Set<String> getSupportedOptions() {
        Set<String> options = new LinkedHashSet<>();
        options.add(CUSTOM_ANNOTATION);
        return options;
    }

    /**
     * 作用就是根据注解，拿到对应的类、方法、变量，然后生成代理类
     * @param annotations
     * @param roundEnv
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        mMessager.printMessage(Diagnostic.Kind.NOTE,"开始处理进程Processor");

        mProxyMap.clear();

        //获取注解的 元素(变量、类、方法)
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(BindView.class);
        mMessager.printMessage(Diagnostic.Kind.NOTE,"elements数量："+elements.size());


        //第一步：循环处理每个注解的元素 放到Map里面
        for(Element element:elements){

            //检查element类型是否是变量VariableElement
            if (!(element instanceof VariableElement)){
                //不是变量直接返回
                return false;
            }

            //转换为变量类型,这里为修饰的变量
            VariableElement variableElement = (VariableElement)element;

            //这里为修饰的变量的所在类 - IndexActivity
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();

            //获取类的全路径，作为key --com.tpnet.processordemo.IndexActivity
            String qualifiedName = typeElement.getQualifiedName().toString();

            //生成对象
            ProxyInfo proxyInfo = mProxyMap.get(qualifiedName);

            if(proxyInfo == null){
                proxyInfo = new ProxyInfo(mElementUtils,typeElement);
                mProxyMap.put(qualifiedName,proxyInfo);
            }

            //获取注解，
            BindView annotation = variableElement.getAnnotation(BindView.class);
            int id = annotation.value();  //获取注解里面的value,这里是(R.id.xx)
            //保存需要处理的每个被注解的变量
            proxyInfo.injectVariables.put(id,variableElement);

        }


        //第二步骤： 遍历Map生成代理类
        for(String key: mProxyMap.keySet()){
            ProxyInfo proxyInfo = mProxyMap.get(key);

            try {
                //创建文件对象
                JavaFileObject soureFile = mFileUtils.createSourceFile(
                        proxyInfo.getProxyClassFullName(),   //文件名,全路径
                        proxyInfo.getTypeElement());
                //创建写入对象
                Writer writer = soureFile.openWriter();
                //写入内容
                writer.write(proxyInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return true;
    }


}
