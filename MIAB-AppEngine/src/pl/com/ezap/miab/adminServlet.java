package pl.com.ezap.miab;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class adminServlet extends HttpServlet {

	private static final long serialVersionUID = 8928074712232673690L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("<h1 align=\"center\">Admin Servlet Test</h1>");

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query allEntities = new Query("MIAB");
		allEntities.setFilter( new FilterPredicate( "geoIndex", FilterOperator.EQUAL, Long.valueOf(140) ) );
		PreparedQuery pq = datastore.prepare(allEntities);
		String str = new String();
		str = " Number of messages: " + Integer.toString( pq.countEntities() );
		resp.getWriter().println( str );
	}

}
