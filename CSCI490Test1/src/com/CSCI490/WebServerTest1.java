package com.CSCI490;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("ws1")
public class WebServerTest1 {

	@Path("/test1")
	@GET
	@Produces("text/plain")
	public String getMessage1(){
		return "My first web service in CSCI490 is working!";
		
	}
	
	@Path("/test2")
	@GET
	@Produces("text/plain")
	public String getMessage2(){
		return "Welcome to software engineering!";
	}
}
