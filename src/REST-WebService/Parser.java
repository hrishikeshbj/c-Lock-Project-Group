package cLockServer;


import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import cLockServer.ServerManager;
import cLockServer.Message;

public class Parser {
  @Context
  UriInfo uriInfo;
  @Context
  Request request;
  String id;
  public Parser(UriInfo uriInfo, Request request, String id) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.id = id;
  }
  
  //Application integration     
  @GET
  @Consumes(MediaType.APPLICATION_XML)
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  public Message getMessage(JAXBElement<Message> msg) {
	  Message m = msg.getValue();
	  ServerManager.doOperation(m);
    if(msg==null)
      throw new RuntimeException("Get: Todo with " + id +  " not found");
    return m;
  }
  
} 

