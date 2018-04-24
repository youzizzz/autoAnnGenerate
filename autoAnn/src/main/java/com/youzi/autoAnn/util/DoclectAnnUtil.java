package com.youzi.autoAnn.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Hello world!
 *
 */
@org.springframework.stereotype.Component
public class DoclectAnnUtil {
	//private final static String DOCLET = "C:\\\\\\\\Users\\\\\\\\Administrator\\\\\\\\eclipse-workspace\\\\\\\\Swagger\\\\\\\\src\\\\\\\\main\\\\\\\\java\\\\\\\\com\\\\\\\\youzi\\\\\\\\Swagger\\\\\\\\entity\\\\\\\\TestDoclet.java";
	private static Map<String, Map<String, Object>> voMap = new ConcurrentHashMap<String, Map<String, Object>>();
	private static Map<String, Map<String, Object>> conMap = new ConcurrentHashMap<String, Map<String, Object>>();
	public static String DocletClass="";
	public static Map<String, Map<String, Map<String, Object>>> values=new ConcurrentHashMap<>();
	public static int scanSize=0;
	/*public static void mains(String[] args) {
		ThreadPoolTaskExecutor th = new ThreadPoolTaskExecutor();
		ScheduledTaskRegistrar registrar = new ScheduledTaskRegistrar();
		String packPath = "com.youzi.Swagger.ScheduPackage";
		System.out.println(DoclectAnnUtil.class.getPackage().toString()
				.replace("package", "").trim());
		List<ScheduModel> list = new ArrayList<ScheduModel>();
		try { /// scanScheduByPackAge("com.youzi.Swagger.ScheduPackage");
			Enumeration<URL> dirs = Thread.currentThread()
					.getContextClassLoader()
					.getResources(packPath.replace(".", "/"));
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				System.out.println(url.getProtocol());
				String filePath = URLDecoder.decode(url.getFile(), "utf-8");
				File file = new File(filePath);
				if (null != file) {
					if (file.isDirectory()) {
						File[] files = file.listFiles();
						if (null != files && files.length != 0) {
							for (File fi : files) {
								Class clazz = Class.forName(packPath + "."
										+ fi.getName().replaceAll(".class", "")
												.trim());
								if (null != clazz) {
									Method[] method = clazz
											.getDeclaredMethods();
									if (null != method && method.length != 0) {
										for (Method me : method) {
											Schedu scheud = me.getAnnotation(
													Schedu.class);
											ScheduModel scheduModel = new ScheduModel();
											scheduModel.setId(UUID.randomUUID()
													.toString());
											scheduModel.setMethod_Name(
													me.getName());
											scheduModel.setMethod_Corn(
													null != scheud
															? scheud.corn()
															: null);
											scheduModel.setIsEnable(1);
											scheduModel.setClass_Name(
													clazz.getName());
											list.add(scheduModel);
										}
									}
								}
							}
						}
					}
				}
			}
			// 最后更改时间 是否启用
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(list);

	}*/

	public static void initDoclect()
			throws NoSuchMethodException, SecurityException,
			IllegalArgumentException, InvocationTargetException, IOException {
	/*	List<ScheduModel> list = new ArrayList<ScheduModel>();*/
		File tempFile = new File("DoclectAnnUtil.Java");
		String filesPath = tempFile.getAbsolutePath().substring(0,
				tempFile.getAbsolutePath().lastIndexOf("\\"));
		// System.out.println(filesPath);
		File file = new File(filesPath);
		file.listFiles(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return name.endsWith("java");
			}
		});
		if (null != file) {
			Long beginDate=System.currentTimeMillis();
			splitCloumnAndPath(values, file);
			Long endDate=System.currentTimeMillis();
			System.out.println("扫描所用时间："+(endDate-beginDate)/1000%60+"s"+"\t"+"共扫描"+scanSize+"个文件");
			Map<String, Map<String, Object>> methodMap = values
					.get("controller");
			for (String constr : methodMap.keySet()) {
				Map<String, Object> map = methodMap.get(constr);
				if (map.keySet().size() > 0) {
					for (String mestr : map.keySet()) {
						if(null!=map.get(mestr)) {
							try {
								if(null!=((Map<String,Object>)map.get(mestr)).get("parms")) {
									Map<String, Object> inMap = (Map<String, Object>) ((Map<String, Object>) ((Map<String,Object>)(Map<String,Object>)map.get(mestr)).get("parms")).get("inParm");
									if (null != inMap) {
										for (String instr : inMap.keySet()) {
											if (values.get("vo").keySet().contains(instr)) {
												System.out.println("找到了"+constr+"类里面"+mestr+"方法里面传入参数"+instr+"对应实体类上面的注解：\r\n"+((Map<String,Object>)values.get("vo").get(instr).get(instr)).get("classAnn")+"\r\n"+"其对应实体类位置在"+
														((Map<String,Object>)values.get("vo").get(instr).get(instr)).get("className"));
											}
										}
									}
								}
							}catch (ClassCastException e) {
								continue;
							}
						}
					}
				}
			}
			/*
			 * for (String str : values.keySet()) { System.out.println(str); }
			 */
			/*
			 * File[] files = file.listFiles();
			 * System.out.println(file.isDirectory()); if (null != files &&
			 * files.length != 0) {
			 * 
			 * for (File file2 : files) { System.out.println(file2.getName());
			 * 
			 * } }
			 */
		}
		// System.out.println(list);

	}

	/**
	 * 第一层是Controller的路径 vo controller controller->paths[]-> 第二层是具体的值 方法级别
	 * 第三层是可能是实体类 参数级别
	 * 
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public static void splitCloumnAndPath(
			Map<String, Map<String, Map<String, Object>>> values, File file)
			throws NoSuchMethodException, SecurityException,
			IllegalArgumentException, InvocationTargetException, IOException {
		scanSize++;
		if (file.isDirectory()) {
			if (null != file.listFiles() && file.listFiles().length > 0) {
				for (File fie : file.listFiles()) {
					splitCloumnAndPath(values, fie);
				}
			}

		} else {
			if (file.getAbsoluteFile().toString().contains("entity")
					&& file.getAbsoluteFile().toString().endsWith("java")
					&& !file.getAbsoluteFile().toString().contains("test")) {
				// System.out.println(file.getAbsolutePath());

				com.sun.tools.javadoc.Main.execute(
						new String[]{"-doclet", DocletUtil.class.getName(),
								"-docletpath", DocletClass, file.getAbsolutePath()});
				DocletUtil.getVoProperties(voMap);
				values.put("vo", voMap);
				// Scan Column 下一层为vo的类名 再一层位vo的属性
			} else if (file.getAbsoluteFile().toString().contains("controller")
					&& file.getAbsoluteFile().toString().endsWith("java")) {
				com.sun.tools.javadoc.Main.execute(
						new String[]{"-doclet", DocletUtil.class.getName(),
								"-docletpath", DocletClass, file.getAbsolutePath()});
				try {
					DocletUtil.getControllerPro(conMap);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				values.put("controller", conMap);
			}
		}
	}
	public static void initDocletClass() throws IOException {
		File tempFile=new File(DoclectAnnUtil.class.getName()+".java");
		String filesPath = tempFile.getAbsolutePath().substring(0,
				tempFile.getAbsolutePath().lastIndexOf("\\"));
		File file = new File(filesPath);
		file.listFiles(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return name.endsWith("java");
			}
		});
		DocletClass=file+"\\"+DocletUtil.class.getName().replaceAll("package", "").trim().replace(".", "\\")+".java";
	}
}
