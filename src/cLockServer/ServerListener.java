package cLockServer;

import java.io.IOException;
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
import javax.ws.rs.core.UriInfo;

/**
 * This class maps the resource to URL The message sent on URL
 * "http:\\<SERVER_URL>\message" will be read with this class. We use post
 * method to perform operation.
 * 
 * @author Nachiket
 * 
 */
@Path("/message")
public class ServerListener {

	// Allows to insert contextual objects into the class,
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	// Return the list of todos for applications
	@GET
	// @Consumes(MediaType.APPLICATION_XML)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Message> getTodosBrowser() {
		List<Message> todos = new ArrayList<Message>();
		todos.addAll(ServerManager.instance.getModel().values());
		return todos;

	}

	// Return the list of todos to the user in the browser
	@GET
	// @Consumes(MediaType.TEXT_XML)
	@Produces(MediaType.TEXT_XML)
	public List<Message> getTodos() {
		List<Message> todos = new ArrayList<Message>();
		todos.addAll(ServerManager.instance.getModel().values());
		return todos;
	}

	/**
	 * We use post to send message from user to server and back from server to
	 * user.
	 * 
	 * @param mId
	 *            : Message Id
	 * @param nHops
	 *            : Number of Hops
	 * @param rId
	 *            : Resource Id
	 * @param rName
	 *            : Resource Name
	 * @param rState
	 *            : State in which to lock the resource
	 * @param op
	 *            : Operation to perform on resource/ lock.
	 * @param lType
	 *            : Lock Type.
	 * @param desc
	 *            : Descrition.
	 * @param user
	 *            : User Id.
	 * @param servletResponse
	 *            : Context to read the messae from.
	 * @return
	 * @throws IOException
	 */
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Message newTodo(@FormParam("mId") String mId,
			@FormParam("nHops") String nHops, @FormParam("rId") String rId,
			@FormParam("rName") String rName,
			@FormParam("rState") String rState, @FormParam("op") String op,
			@FormParam("lType") String lType, @FormParam("desc") String desc,
			@FormParam("user") String user,
			@Context HttpServletResponse servletResponse) throws IOException {

		Message msg = new Message(mId, nHops, rId, rName, rState, op, lType,
				desc, user);
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
