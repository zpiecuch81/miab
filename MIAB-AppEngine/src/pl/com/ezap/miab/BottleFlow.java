package pl.com.ezap.miab;

import java.util.ArrayList;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;

public class BottleFlow extends HttpServlet
{
  private static final long serialVersionUID = -6868704222792323683L;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
  {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query allEntities = new Query("MessageV1");
    allEntities.setFilter( new FilterPredicate( "flowStamp", FilterOperator.NOT_EQUAL, null ) );
    allEntities.addSort( "flowStamp", SortDirection.ASCENDING );
    PreparedQuery pq = datastore.prepare(allEntities);
//    String str = new String();
//    str = " Number of messages: " + Integer.toString( pq.countEntities() );
    ArrayList<Entity> toCommit = new ArrayList<Entity>();
    for( Entity entity : pq.asIterable() ) {
//      str += "\n" + (String)entity.getProperty( "message" );

      GeoPt location = (GeoPt)entity.getProperty( "location" );
      GeoPt deltaLocation = (GeoPt)entity.getProperty( "deltaLocation" );

      float deltaLatitude = deltaLocation.getLatitude();
      float latitude = location.getLatitude() + deltaLatitude;
      float longitude = location.getLongitude() + deltaLocation.getLongitude();

      if( latitude > 90.0 ){
        latitude = 180 - latitude;
        deltaLatitude = deltaLatitude * (-1);
      } else if ( latitude < -90.0 ) {
        latitude = -180 - latitude;
        deltaLatitude = deltaLatitude * (-1);
      }
      if( longitude > 180.0 ) {
        longitude -= 360.0;
      } else if( longitude < -180.0 ) {
        longitude += 360.0;
      }

      entity.setProperty( "location", new GeoPt( latitude, longitude ) );
      entity.setProperty( "deltaLocation", new GeoPt( deltaLatitude, deltaLocation.getLongitude() ) );
      entity.setProperty( "flowStamp", new java.util.Date().getTime() );
      GeoIndex geoIndex = new GeoIndex();
      entity.setProperty( "geoIndex", geoIndex.getIndex( latitude, longitude ) );

      toCommit.add( entity );
    }

    datastore.put( toCommit );
  }
}
