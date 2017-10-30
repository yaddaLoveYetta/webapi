package com.yadda.api.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ApiServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private ApplicationContext context;

	private ApiHandler apiHandler;

	@Override
	public void init() throws ServletException {
		super.init();
		// 获取spring 容器
		context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		apiHandler = context.getBean(ApiHandler.class);

		if (apiHandler == null) {
			throw new ExceptionInInitializerError("com.yadda.api.core.ApiHandler配置异常！");
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		apiHandler.handle(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		apiHandler.handle(req, resp);
		getServletContext();
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		apiHandler.handle(req, resp);
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		apiHandler.handle(req, resp);
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		apiHandler.handle(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		apiHandler.handle(req, resp);
	}

	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		apiHandler.handle(req, resp);
	}
}
