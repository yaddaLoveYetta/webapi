package com.yadda.api.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.yadda.demo.Student;

public class UtilJsonTest {

	@Test
	public void toMap() {
		String json = "{\"name\":\"yadda\",\"age\":31,\"fav\":[\"song\",\"pain\",\"music\"]}";

		Map<String, Object> result = new HashMap<>(3);

		result = JsonUtil.toMap(json);

		System.out.println(result);
	}

	@Test
	public void toBean() {
		 String json = "{'name':'yaddass','age':32,'fav':['song','pain','music','dsfsf']}";
		// Student student = JsonUtil.toBean(json, Student.class);
		//
		// System.out.println(student);
		// System.out.println(student.getFav().toString());

		Student s1 = new Student();

		s1.setName("yadda");
		s1.setAge(31);
		List<String> fav = new ArrayList<>();
		fav.add("song");
		fav.add("pain");
		fav.add("music");
		s1.setFav(fav);

		String str = JSON.toJSONString(s1);
		System.err.println(str);

		// Student s2 = JSON.parseObject(str, new TypeReference<Student>() {});
		//Student s2 = JSON.parseObject(str, Student.class);
		Student s2 = JsonUtil.toBean(s1, Student.class);
		Student s3 = JsonUtil.toBean(json, Student.class);
		// com.alibaba.fastjson.TypeReference.TypeReference<T>()
		// com.alibaba.fastjson.TypeReference.TypeReference<Student>()
		System.out.println(s3);
		System.out.println(s3.getName());
		System.out.println(s3.getAge());
		System.out.println(s3.getFav().size());
	}
}
