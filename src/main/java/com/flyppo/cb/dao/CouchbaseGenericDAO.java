package com.flyppo.cb.dao;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.inject.Singleton;

import com.flyppo.cb.config.CouchbaseConfiguration;
import com.flyppo.cb.constants.LoggerConstants;
import com.flyppo.cb.exceptions.CouchbaseServiceException;
import com.flyppo.cb.exceptions.DAOInvalidRequestException;
import com.flyppo.cb.exceptions.DBException;
import com.flyppo.cb.service.CouchbaseService;
import com.google.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * DAO layer for campaign summary documents
 * 	- all generics taken care of here
 * 
 * @author mmt6461
 *
 */
@Slf4j
@Getter
@Setter
public abstract class CouchbaseGenericDAO<T> {

	private final CouchbaseService couchbaseService;
	private final CouchbaseConfiguration configuration;
	private final Class<T> clazz;
	
	public CouchbaseGenericDAO (CouchbaseService couchService, CouchbaseConfiguration configuration,
			Class<T> clazz) {
		this.couchbaseService = couchService;
		this.configuration = configuration;
		this.clazz = clazz;
	}

	/**
	 * retrieves the document basis the given document ID
	 * 
	 * @param documentId
	 * @return
	 * @throws DBException
	 */
	public T getOrNull(String documentId) throws DBException {

		try {
			
			String key = getCouchbaseKey(documentId);
			return getValue(key);
		} catch (CouchbaseServiceException e) {
			log.error(LoggerConstants.COUCH_EXCEPTION,e);
			throw new DBException("couchbase exception", e);
		}
	}

	public void save(T t) throws DBException {

		try {
			
			String key = getCouchbaseKey(getDocumentID(t));
			couchbaseService.putValue(key, t);
		} catch (CouchbaseServiceException e) {
			log.error(LoggerConstants.COUCH_EXCEPTION,e);
			throw new DBException(LoggerConstants.COUCH_EXCEPTION, e);
		}
	}
	
	/**
	 * composes the coucbhase document key for the given document ID:
	 * 	- prefixes version
	 * 
	 * @param campaignId
	 * @return
	 */
	private String getCouchbaseKey (String campaignId) {
		
		return formatKey(Arrays.asList(campaignId));
	}

	/**
	 * formats the list of keys as per application convention
	 * 
	 * @param attrs
	 * @return
	 */
	private String formatKey (List<String> attrs) {
		
		if (attrs == null || attrs.isEmpty())
			return null;
		
		StringBuilder builder = new StringBuilder();
		builder.append(configuration.getKeyPrefix());
		
		for (String attr : attrs)
		{
			builder.append(configuration.getKeySeparator());
			builder.append(attr);
		}
		
		return builder.toString();
	}
	
	/**
	 * returns the unique document ID for the POJO
	 * 
	 * @param t
	 * @return
	 */
	public abstract String getDocumentID (T t);
	
	/**
	 * retrieves the object (generics) supported
	 * 
	 * @param key
	 * @param klass
	 * @return
	 */
	public T getValue(String key) throws CouchbaseServiceException {

		// get the JSON string from Couch
		String json = couchbaseService.getJSONValue(key);
		if (!Objects.nonNull(json))
			return null;
		
		// map JSON to Object
		try {
			
			return couchbaseService.getObjectMapper().readValue(json, clazz);
		} catch (IOException e) {
			
			log.error(LoggerConstants.COUCH_MAPPING_ERROR, couchbaseService.getBucketName(), key, e);
            log.error(LoggerConstants.COUCH_JSON_OBJECT_MAPPING_EXCEPTION, key, e);
            throw new DAOInvalidRequestException(LoggerConstants.COUCH_JSON_OBJECT_MAPPING_EXCEPTION, e);
		}
	}
}
