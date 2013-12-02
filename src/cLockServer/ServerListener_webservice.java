package cLockServer;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;



// Will map the resource to the URL todos
@Path("/message")
public class ServerListener {

  // Allows to insert contextual objects into the class, 
  // e.g. ServletContext, Request, Response, UriInfo
  @Context
  UriInfo uriInfo;
  @Context
  Request request;


  // //Return the list of todos to the user in the browser
  @GET
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)
	  public Message getMessage(JAXBElement<Message> msg) {
		  Message m = msg.getValue();
		  ServerManager.doOperation(m);
	    if(msg==null)
	      throw new RuntimeException("Get: Todo with " + m.getMId() +  " not found");
	    return m; 
  }
  @POST
  @Produces(MediaType.APPLICATION_XML)
  @Consumes(MediaType.APPLICATION_XML)
  public Message newTodo(@FormParam("mId") String mId,
      @FormParam("nHops") String nHops,
      @FormParam("rId") String rId,
      @FormParam("rName") String rName,
      @FormParam("rState") String rState,
      @FormParam("op") String op,
      @FormParam("lType") String lType,
      @FormParam("desc") String desc,
      @FormParam("user") String user,
      @Context HttpServletResponse servletResponse) throws IOException {
	  System.out.println("Received message");
	  File file = new File("Hello1.txt");
      // creates the file
      file.createNewFile();
      // creates a FileWriter Object
      FileWriter writer = new FileWriter(file); 
      // Writes the content to the file
      writer.write("This\n is\n an\n example\n"); 
      writer.flush();
      writer.close();

    Message msg = new Message(mId, nHops, rId, rName, rState, op, lType, desc, user);
    ServerManager.doOperation(msg);
    return msg;
  }
   
  
  // Defines that the next path parameter after todos is
  // treated as a parameter and passed to the TodoResources
  // Allows to type http://localhost:8080/de.vogella.jersey.todo/rest/todos/1
  // 1 will be treaded as parameter todo and passed to TodoResource
  @Path("{message}")
  public Parser getTodo(@PathParam("message") String id) {
    return new Parser(uriInfo, request, id);
  }
  
} 
