package com.yadda.demo;

import org.springframework.stereotype.Service;

import com.yadda.api.core.ApiMapping;
import com.yadda.api.core.ApiRequest;
import com.yadda.api.core.ApiVersion;

@Service
@ApiMapping(value = "test")
public class ServiceDemo {

	@ApiVersion(version = 1.0f)
	@ApiMapping(value = "setAge", useLogin = true)
	public void setAge(Student student, int age, ApiRequest apiRequest) {

		student.setAge(age);
	}

	@ApiMapping(value = "getInfo")
	@ApiVersion(version = 2.0f)
	public Student getInfo(String name) {
		Student s = new Student();
		s.setName(name);
		return s;
	}

	@ApiMapping(value = "getName")
	public String getName() {
		return "fsdfdsafds";
	}

	@ApiMapping(value = "test")
	public void test() {

	}

}
