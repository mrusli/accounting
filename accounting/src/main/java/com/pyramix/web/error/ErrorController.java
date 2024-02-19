package com.pyramix.web.error;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;

import com.pyramix.web.common.GFCBaseController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ErrorController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3740359171162655089L;

	private static final Logger log = Logger.getLogger(ErrorController.class);
	
	public void onCreate$errorWindow(Event event) {
		log.info("errorWindow created");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		processError(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		processError(request, response);		
	}
	
	private void processError(HttpServletRequest request, HttpServletResponse response)
		throws IOException {
		Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
		log.info(throwable.getMessage());
	}
}
