package com.youzi.autoAnn;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.youzi.autoAnn.util.DoclectAnnUtil;
import com.youzi.autoAnn.util.DocletUtil;

@SpringBootApplication
@EnableWebMvc
@MapperScan("com.youzi.autoAnn.dao")
public class MainApplication 
{
    public static void main( String[] args )
    {
        SpringApplication.run(MainApplication.class, args);
        try {
			DoclectAnnUtil.initDoclect();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(DoclectAnnUtil.values);
    }
}
