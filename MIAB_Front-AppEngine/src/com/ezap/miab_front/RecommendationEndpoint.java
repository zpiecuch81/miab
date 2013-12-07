package com.ezap.miab_front;

import com.ezap.miab_front.EMF;

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

@Api(name = "recommendationendpoint", namespace = @ApiNamespace(ownerDomain = "ezap.com", ownerName = "ezap.com", packagePath = "miab_front"))
public class RecommendationEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listRecommendation")
	public CollectionResponse<Recommendation> listRecommendation(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		Cursor cursor = null;
		List<Recommendation> execute = null;

		try {
			mgr = getEntityManager();
			Query query = mgr
					.createQuery("select from Recommendation as Recommendation");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null) {
				query.setFirstResult(0);
				query.setMaxResults(limit);
			}

			execute = (List<Recommendation>) query.getResultList();
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Recommendation obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Recommendation> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getRecommendation")
	public Recommendation getRecommendation(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		Recommendation recommendation = null;
		try {
			recommendation = mgr.find(Recommendation.class, id);
		} finally {
			mgr.close();
		}
		return recommendation;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param recommendation the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertRecommendation")
	public Recommendation insertRecommendation(Recommendation recommendation) {
		EntityManager mgr = getEntityManager();
		try {
			if (containsRecommendation(recommendation)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.persist(recommendation);
		} finally {
			mgr.close();
		}
		return recommendation;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param recommendation the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateRecommendation")
	public Recommendation updateRecommendation(Recommendation recommendation) {
		EntityManager mgr = getEntityManager();
		try {
			if (!containsRecommendation(recommendation)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.persist(recommendation);
		} finally {
			mgr.close();
		}
		return recommendation;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeRecommendation")
	public void removeRecommendation(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		try {
			Recommendation recommendation = mgr.find(Recommendation.class, id);
			mgr.remove(recommendation);
		} finally {
			mgr.close();
		}
	}

	private boolean containsRecommendation(Recommendation recommendation) {
		EntityManager mgr = getEntityManager();
		boolean contains = true;
		try {
			Recommendation item = mgr.find(Recommendation.class,
					recommendation.getKey());
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
