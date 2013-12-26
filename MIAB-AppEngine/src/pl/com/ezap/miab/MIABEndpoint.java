package pl.com.ezap.miab;

import pl.com.ezap.miab.EMF;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Api(name = "miabendpoint", namespace = @ApiNamespace(ownerDomain = "com.pl", ownerName = "com.pl", packagePath = "ezap.miab"))
public class MIABEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listMIAB")
	public CollectionResponse<MIAB> listMIAB(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		Cursor cursor = null;
		List<MIAB> execute = null;

		try {
			mgr = getEntityManager();
			Query query = mgr.createQuery("select from MIAB as MIAB");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null) {
				query.setFirstResult(0);
				query.setMaxResults(limit);
			}

			execute = (List<MIAB>) query.getResultList();
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (MIAB obj : execute)
				;
		} finally {
			mgr.close();
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
