package com.youzi.autoAnn.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.standard.Standard;

public class DocletUtil extends Doclet {
	public static RootDoc roots;
	private static String fName;
	
	public static boolean start(RootDoc root) {
		roots = root;
		// show(roots);
		return true;
		// return HtmlDoclet.start(root);
	}

	public static boolean validOptions(String[][] paramArrayOfString,
			DocErrorReporter paramDocErrorReporter) {
		System.out.println("Print arguments:");
		for (String[] strings : paramArrayOfString) {
			System.out.println("  " + strings[0] + "->" + strings[1]);
		}
		System.out.println();
		return Standard.validOptions(paramArrayOfString, paramDocErrorReporter);
	}

	// 显示DocRoot中的基本信息
	public static void show(RootDoc root) {
		// System.out.println(">>>>>>>open");

		ClassDoc[] classes = root.classes();
		// System.out.println(root);
		for (int i = 0; i < classes.length; ++i) {
			// System.out.println(classes[i]);
			for (FieldDoc field : classes[i].fields(false)) {
				/*
				 * System.out.println(method.name()); AnnotationDesc[]
				 * ann=method.annotations(); if(ann.length>0) { for (int j = 0;
				 * j < ann.length; j++) {
				 * System.out.println(ann[i].annotationType()); } }
				 */
				// System.out.println(method.commentText());
				System.out.println(field.name());
				AnnotationDesc[] ann = field.annotations();
				if (null != ann) {
					for (int j = 0; j < ann.length; j++) {
						System.out.println(ann[i].annotationType());
					}
				}
				System.out.println(field.getRawCommentText());
			}
		}
	}

	public static void getVoProperties(Map<String, Map<String, Object>> map) {
		ClassDoc[] classes = roots.classes();
		Map<String, Object> value = new HashMap<String, Object>();
		Map<String,Object> voMap=new HashMap<String, Object>();
		String className = null;
		for (int i = 0; i < classes.length; ++i) {
			className = classes[i].name();
			className = className.replace(".java", "").trim();
			// System.out.println(className);

			for (FieldDoc field : classes[i].fields(false)) {
				AnnotationDesc[] ann = field.annotations();
				voMap.put(field.name(), field.commentText());
			}
			voMap.put("classAnn", classes[i].getRawCommentText().trim());
			voMap.put("className", classes[i]);
			value.put(className, voMap);
			map.put(className, value);
		}
	}

	public static void getControllerPro(Map<String, Map<String, Object>> map)
			throws InstantiationException, IllegalAccessException,
			NoSuchMethodException, SecurityException, IllegalArgumentException,
			InvocationTargetException, IOException {
		ClassDoc[] classes = roots.classes();
		Map<String, Object> value = new HashMap<String, Object>();
		String className = null;
		for (int i = 0; i < classes.length; ++i) {
			System.out.println("classname-->" + classes[i].name());
			className = classes[i].name();
			className = className.replace(".java", "").trim();
			// System.out.println(className);
			for (MethodDoc method : classes[i].methods()) {
				AnnotationDesc[] ann = method.annotations();
				boolean flag = false;
				Map<String, Object> parms = new HashMap<String, Object>();
				// 获取具体CLASS 下面该方法上的注解
				for (AnnotationDesc annotationDesc : ann) {
					if (annotationDesc.annotationType().toString().contains(
							"org.springframework.web.bind.annotation.GetMapping")) {
						flag = true;
						
						break;
					}
				}
				parms.put("outType", method.returnType().toString());
				if (flag) {
					Map<String,Object> methodParm=new HashMap<String, Object>();
					Parameter[] params = method.parameters();
					for (Parameter parameter : params) {
						Map<String, Object> inParm = new HashMap<String, Object>();
						inParm.put(parameter.typeName(), parameter.name());
						parms.put("inParm", inParm);
						getMethodAnn(className, methodParm, method.name());
						methodParm.put("parms", parms);
						methodParm.put("Ann", method.getRawCommentText().replace("\t", "").trim());
					}
					value.put(method.name(),methodParm );
				}
			}
			map.put(className, value);
		}
	}
	
	/**
	 * 根据方法名获取上面的注解
	 * @param name Controller名字
	 * @param map 值 用来存放getMapping里面的参数
	 * @param methodName 单个方法名
	 * @throws IOException
	 */
	public static void getMethodAnn(String name, Map<String, Object> map,
			String methodName) throws IOException {
		Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader()
				.getResources("");
		while (dirs.hasMoreElements()) {
			URL url = dirs.nextElement();
			try {
				String filePath = URLDecoder.decode(url.getFile(), "utf-8");
				// System.out.println(filePath);
				getFileByPathName(filePath, name);
				fName = fName
						.substring(fName.indexOf("classes") + 8, fName.length())
						.replace("\\", ".");
				Class clazz = Class.forName(fName.replace(".class", "").trim());
				Method[] method = clazz.getMethods();
				if (null != method && method.length > 0) {
					for (Method me : method) {
						if (me.getName().equals(methodName)) {
							GetMapping get = me.getAnnotation(GetMapping.class);
							Map<String, Object> AnnMap = new HashMap<String, Object>();
							AnnMap.put("value", Arrays.toString(get.value()));
							AnnMap.put("name", get.name());
							AnnMap.put("path", Arrays.toString(get.path()));
							AnnMap.put("params", Arrays.toString(get.params()));
							AnnMap.put("headers", Arrays.toString(get.headers()));
							AnnMap.put("consumes", Arrays.toString(get.consumes()));
							AnnMap.put("produces", Arrays.toString(get.produces()));
							map.put("requestParm", AnnMap);
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 根据文件名拿到一个Class用来获取注解值
	 * @param fileName 文件名字
	 * @param controllerName 控制器名字
	 */
	public static void getFileByPathName(String fileName,
			String controllerName) {
		File file = new File(fileName);
		if (file.isDirectory()) {
			File[] fi = file.listFiles();
			if (null != fi && fi.length > 0) {
				for (File file2 : fi) {
					getFileByPathName(file2.getAbsolutePath(), controllerName);
				}
			}
		} else {

		}
		if (file.getAbsolutePath().contains(controllerName)) {
			fName = fileName;
			return;
		}
	}
}
