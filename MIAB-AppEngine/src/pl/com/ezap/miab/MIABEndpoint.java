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

@Api(name = "miabendpoint", namespace = @ApiNamespace(ownerDomain = "com.pl", ownerName = "com.pl", packagePath = "ezap.miab"))
public class MIABEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@ApiMethod(name = "listMIAB")
	public CollectionResponse<MIAB> listMIAB(
			@Named("geoIndex") long geoIndex,
			@Named("isBurried") boolean isBurried,
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query allEntities = new Query("MIAB");
		allEntities.setFilter( CompositeFilterOperator.and(
				FilterOperator.EQUAL.of( "geoIndex", Long.valueOf(geoIndex) ),
				FilterOperator.EQUAL.of( "isBurried", Boolean.valueOf(isBurried) ) ) );
		PreparedQuery pq = datastore.prepare(allEntities);

		ArrayList<MIAB> execute = new ArrayList<MIAB>();

		for ( Entity entity : pq.asIterable() ) {
			MIAB miab = new MIAB();
			miab.setMessage( (String)entity.getProperty("message") );
			miab.setLocation( (GeoPt)entity.getProperty("location") );
			miab.setBurried( (boolean)entity.getProperty("isBurried") );
			miab.setFlowing( (boolean)entity.getProperty("isFlowing") );
			miab.setDeltaLocation( (GeoPt)entity.getProperty("deltaLocation") );
			miab.setGeoIndex( (long)entity.getProperty("geoIndex") );
			miab.setFlowStamp( (long)entity.getProperty("flowStamp") );
			miab.setTimeStamp( (long)entity.getProperty("timeStamp") );
			miab.setID( (long)entity.getKey().getId() );
			execute.add(miab);
		}

		return CollectionResponse.<MIAB> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getMIAB")
	public MIAB getMIAB(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		MIAB miab = null;
		try {
			miab = mgr.find(MIAB.class, id);
		} finally {
			mgr.close();
		}
		return miab;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param miab the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertMIAB")
	public MIAB insertMIAB(MIAB miab) {
		EntityManager mgr = getEntityManager();
		try {
			if (containsMIAB(miab)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.persist(miab);
		} finally {
			mgr.close();
		}
		return miab;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param miab the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateMIAB")
	public MIAB updateMIAB(MIAB miab) {
		EntityManager mgr = getEntityManager();
		try {
			if (!containsMIAB(miab)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.persist(miab);
		} finally {
			mgr.close();
		}
		return miab;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeMIAB")
	public void removeMIAB(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		try {
			MIAB miab = mgr.find(MIAB.class, id);
			mgr.remove(miab);
		} finally {
			mgr.close();
		}
	}

	private boolean containsMIAB(MIAB miab) {
		if( miab.getID() == null ) {
			return false;
		}
		EntityManager mgr = getEntityManager();
		boolean contains = true;
		try {
			MIAB item = mgr.find(MIAB.class, miab.getID());
			if (item == null) {
				contains = false;
			}
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

}
