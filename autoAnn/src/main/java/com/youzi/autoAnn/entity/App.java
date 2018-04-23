package com.youzi.autoAnn.entity;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.youzi.Swagger.Annotation.Schedu;
import com.youzi.Swagger.entity.ScheduModel;
import com.youzi.Swagger.entity.TestDoclet;

/**
 * Hello world!
 *
 */
@EnableScheduling
public class App {
	private final static String DOCLET = "C:\\\\\\\\Users\\\\\\\\Administrator\\\\\\\\eclipse-workspace\\\\\\\\Swagger\\\\\\\\src\\\\\\\\main\\\\\\\\java\\\\\\\\com\\\\\\\\youzi\\\\\\\\Swagger\\\\\\\\entity\\\\\\\\TestDoclet.java";
	private static Map<String, Map<String, Object>> voMap = new HashMap<String, Map<String, Object>>();
	private static Map<String, Map<String, Object>> conMap = new HashMap<String, Map<String, Object>>();
	public static int scanSize=0;
	public static void mains(String[] args) {
		ThreadPoolTaskExecutor th = new ThreadPoolTaskExecutor();
		ScheduledTaskRegistrar registrar = new ScheduledTaskRegistrar();
		String packPath = "com.youzi.Swagger.ScheduPackage";
		System.out.println(App.class.getPackage().toString()
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

	}

	public static void main(String[] args)
			throws NoSuchMethodException, SecurityException,
			IllegalArgumentException, InvocationTargetException, IOException {
		List<ScheduModel> list = new ArrayList<ScheduModel>();
		File tempFile = new File("App.Java");
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
			Map<String, Map<String, Map<String, Object>>> values = new HashMap<String, Map<String, Map<String, Object>>>();
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
						new String[]{"-doclet", TestDoclet.class.getName(),
								"-docletpath", DOCLET, file.getAbsolutePath()});
				TestDoclet.getVoProperties(voMap);
				values.put("vo", voMap);
				// Scan Column 下一层为vo的类名 再一层位vo的属性
			} else if (file.getAbsoluteFile().toString().contains("controller")
					&& file.getAbsoluteFile().toString().endsWith("java")) {
				com.sun.tools.javadoc.Main.execute(
						new String[]{"-doclet", TestDoclet.class.getName(),
								"-docletpath", DOCLET, file.getAbsolutePath()});
				try {
					TestDoclet.getControllerPro(conMap);
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

	// fixedRate 间隔在两次任务执行内
	// fixedDelay 在上一次任务执行完
	@Scheduled()
	public static void scanScheduByPackAge(String packAge)
			throws URISyntaxException, FileNotFoundException {
		// Package pack=new
		// File file=new File("com/youzi/Swagger/ScheduPackage");
		// System.out.println(file.listFiles());
		Class clazz = App.class;
		Package pack = clazz.getPackage().getPackage("ScheduPackage");
		System.out.println(pack);
		/*
		 * URL url=ResourceUtils.getURL(packAge); System.out.println(url); URI
		 * uri = url.toURI();
		 * 
		 * String urls=uri.toString().replace(".", "/"); File file=new
		 * File(urls); System.out.println(urls);
		 * System.out.println(file.list());
		 * System.out.println(file.listFiles()); File[]files=file.listFiles();
		 * if(null!=files) { for (File fil : files) {
		 * System.out.println(fil.getName()); } }
		 */
	}

	public static void findAndAddClassesInPackageByFile(String packageName,
			String packagePath, final boolean recursive,
			Set<Class<?>> classes) {
		// 获取此包的目录 建立一个File
		File dir = new File(packagePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			// log.warn("用户定义包名 " + packageName + " 下没有任何文件");
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles();
		// 循环所有文件
		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(
						packageName + "." + file.getName(),
						file.getAbsolutePath(), recursive, classes);
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0,
						file.getName().length() - 6);
				try {
					// 添加到集合中去
					// classes.add(Class.forName(packageName + '.' +
					// className));
					// 经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
					classes.add(Thread.currentThread().getContextClassLoader()
							.loadClass(packageName + '.' + className));
				} catch (ClassNotFoundException e) {
					// log.error("添加用户自定义视图类错误 找不到此类的.class文件");
					e.printStackTrace();
				}
			}
		}
	}
}
