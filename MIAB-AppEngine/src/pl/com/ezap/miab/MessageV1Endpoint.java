
package pl.com.ezap.miab;

import pl.com.ezap.miab.EMF;

import java.util.ArrayList;
import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;

@Api( name = "messagev1endpoint", namespace = @ApiNamespace(
    ownerDomain = "com.pl",
    ownerName = "com.pl",
    packagePath = "ezap.miab" ) )
public class MessageV1Endpoint
{
  /**
   * This method lists all the entities inserted in datastore.
   * It uses HTTP GET method and paging support.
   *
   * @return A CollectionResponse class containing the list of all entities
   * persisted and a cursor to the next page.
   */
  @ApiMethod( name = "listMessageV1" )
  public CollectionResponse<MessageV1> listMessageV1(
      @Named("geoIndex") long geoIndex,
      @Named("isHidden") boolean isHidden,
      @Nullable @Named("cursor") String cursorString,
      @Nullable @Named("limit") Integer limit)
  {
    ArrayList<MessageV1> execute = new ArrayList<MessageV1>();
    try{
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Query allEntities = new Query("MessageV1");
      allEntities.setFilter( CompositeFilterOperator.and(
          FilterOperator.EQUAL.of( "geoIndex", Long.valueOf(geoIndex) ),
          FilterOperator.EQUAL.of( "isHidden", Boolean.valueOf(isHidden) ) ) );
      PreparedQuery pq = datastore.prepare(allEntities);

      if( pq != null ) {
        for ( Entity entity : pq.asIterable() ) {
          MessageV1 message = new MessageV1();
          //message.setMessage( (String)entity.getProperty("message") );
          message.setLocation( (GeoPt)entity.getProperty("location") );
          //message.setHidden( (boolean)entity.getProperty("isHidden") );
          //message.setFlowing( (boolean)entity.getProperty("isFlowing") );
          //message.setGeoIndex( (long)entity.getProperty("geoIndex") );
          //message.setTimeStamp( (long)entity.getProperty("timeStamp") );
          //if( message.isFlowing() ) {
          //  message.setDeltaLocation( (GeoPt)entity.getProperty("deltaLocation") );
          //  message.setFlowStamp( (long)entity.getProperty("flowStamp") );
          //}
          message.setID( (long)entity.getKey().getId() );
          execute.add(message);
        }
      }
    }catch(Exception e) {
      e.printStackTrace();
    }

    return CollectionResponse.<MessageV1> builder().setItems(execute)
        .setNextPageToken(cursorString).build();
  }

  /**
   * This method gets the entity having primary key id. It uses HTTP GET method.
   *
   * @param id the primary key of the java bean.
   * @return The entity with primary key id.
   */
  @ApiMethod( name = "getMessageV1" )
  public MessageV1 getMessageV1( @Named( "id" ) Long id )
  {
    EntityManager mgr = getEntityManager();
    MessageV1 messagev1 = null;
    try {
      messagev1 = mgr.find( MessageV1.class, id );
    }
    finally {
      mgr.close();
    }
    return messagev1;
  }

  /**
   * This inserts a new entity into App Engine datastore. If the entity already
   * exists in the datastore, an exception is thrown.
   * It uses HTTP POST method.
   *
   * @param messagev1 the entity to be inserted.
   * @return The inserted entity.
   */
  @ApiMethod( name = "insertMessageV1" )
  public MessageV1 insertMessageV1( MessageV1 messagev1 )
  {
    EntityManager mgr = getEntityManager();
    try {
      if( containsMessageV1( messagev1 ) ) {
        throw new EntityExistsException( "Object already exists" );
      }
      mgr.persist( messagev1 );
    }
    finally {
      mgr.close();
    }
    return messagev1;
  }

  /**
   * This method is used for updating an existing entity. If the entity does not
   * exist in the datastore, an exception is thrown.
   * It uses HTTP PUT method.
   *
   * @param messagev1 the entity to be updated.
   * @return The updated entity.
   */
  @ApiMethod( name = "updateMessageV1" )
  public MessageV1 updateMessageV1( MessageV1 messagev1 )
  {
    EntityManager mgr = getEntityManager();
    try {
      if( !containsMessageV1( messagev1 ) ) {
        throw new EntityNotFoundException( "Object does not exist" );
      }
      mgr.persist( messagev1 );
    }
    finally {
      mgr.close();
    }
    return messagev1;
  }

  /**
   * This method removes the entity with primary key id.
   * It uses HTTP DELETE method.
   *
   * @param id the primary key of the entity to be deleted.
   */
  @ApiMethod( name = "removeMessageV1" )
  public void removeMessageV1( @Named( "id" ) Long id )
  {
    EntityManager mgr = getEntityManager();
    try {
      MessageV1 messagev1 = mgr.find( MessageV1.class, id );
      mgr.remove( messagev1 );
    }
    finally {
      mgr.close();
    }
  }

  @ApiMethod( name = "getFlowingMessage" )
  public MessageV1 getFlowingMessage()
  {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query allEntities = new Query("MessageV1");
    allEntities.setFilter( new FilterPredicate( "flowStamp", FilterOperator.NOT_EQUAL, null ) );
    allEntities.addSort( "flowStamp", SortDirection.DESCENDING );
    PreparedQuery pq = datastore.prepare(allEntities);
    long currentTimeStamp = new java.util.Date().getTime();
    for( Entity entity : pq.asIterable() ) {
      long timeStamp = (long)entity.getProperty( "timeStamp" );
      long duration = currentTimeStamp - timeStamp;
      if( duration < 1000 * 3600 * 24 ) {
        continue;
      }
      MessageV1 message = new MessageV1();;
      message.setTimeStamp( timeStamp );
      message.setID( (long)entity.getKey().getId() );
      message.setLocation( (GeoPt)entity.getProperty( "location" ) );
      message.setGeoIndex( (Long)entity.getProperty( "geoIndex") );
      message.setFlowing( true );
      message.setMessage( (String)entity.getProperty( "message" ) );
      return message;
    }
    return null;
  }

  private boolean containsMessageV1( MessageV1 messagev1 )
  {
    if( messagev1.getID() == null ) {
      return false;
    }
    EntityManager mgr = getEntityManager();
    boolean contains = true;
    try {
      MessageV1 item = mgr.find( MessageV1.class, messagev1.getID() );
      if( item == null ) {
        contains = false;
      }
    }
    finally {
      mgr.close();
    }
    return contains;
  }

  private static EntityManager getEntityManager()
  {
    return EMF.get().createEntityManager();
  }
}
