package com.flyppo.cb.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.Document;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.error.DocumentDoesNotExistException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flyppo.cb.config.CouchbaseConfiguration;
import com.flyppo.cb.constants.LoggerConstants;
import com.flyppo.cb.exceptions.CouchbaseServiceException;
import com.flyppo.cb.exceptions.DAOInvalidRequestException;

import rx.Observable;

/**
 * the single interface for couchbase operations and queries
 * to be autowired
 * 
 * the manager interacts directly with the underlying couchbase bucket sdk api
 * 
 * @author mmt6461
 *
 */
@Singleton
public class CouchbaseService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CouchbaseService.class);

    private static final String KEY_SEPARATOR = "::";

    private final ObjectMapper mapper;
	private final Bucket bucket;
	private final CouchbaseConfiguration configuration;
    
    /**
     * constructor used for couchbase manager (cluster, bucket) creation
     * 
     * @param couchNodeList
     * @param bucketName
     * @param bucketPasswd
     * @param opTimeoutMillis
     */
	@Inject
	public CouchbaseService(CouchbaseConfiguration configuration, Bucket bucket) {

        mapper = new ObjectMapper();
        this.configuration = configuration;
        this.bucket = bucket;
	}
	
	/**
	 * returns the operation timeout in millis
	 * @return
	 */
	private int getOperationTimeoutInMillis() {
	    
	    return configuration.getCouchOperationTimeoutMillis();
	}
	
	/**
	 * Returns the json value for the given key as a string
	 * 
	 * @param key
	 * @return
	 */
	public String getJSONValue(String key) {
		
		if (key == null) {
			
			LOGGER.error(LoggerConstants.COUCH_KEY_IS_NULL);
			return null;
		}
		
        // retrieve the document for the key
		long start = System.currentTimeMillis();
		JsonDocument document = bucket.get(key, getOperationTimeoutInMillis(), TimeUnit.MILLISECONDS);
		long stop = System.currentTimeMillis();
		
		LOGGER.info(LoggerConstants.TIME_TAKEN_FOR_COUCH_PUT_OPERATION, key, stop-start);
		
		if (document == null || document.content() == null) {
			
			LOGGER.error(LoggerConstants.COUCH_GET_DOCUMENT_RETURNED_NULL, key);
			return null;
		}
		
		return document.content().toString();
	}

	/**
	 * Returns formatted string to be used as a key. Param are appended together
	 * with || as seperator. Eg. if params are Key,For,Document then generated
	 * key is Key||For||Document Empty string is returned if no params are
	 * passed in the function.
	 * 
	 * @param params
	 *            Parameters which are used to construct the key.
	 * @return Formatted key
	 */
	public String getFormattedKey(String... params) {
		
		if (params == null)
			return null;
		
		StringBuilder key = new StringBuilder();
		for (int i = 0; i < params.length; i++) {
			key.append(params[i]);
			if (i != (params.length - 1)) {
				key.append(KEY_SEPARATOR);
			}
		}
		return appendVersion(key.toString());
	}


	public void putValue(String key, Object value) throws CouchbaseServiceException {
		putValue(key, 0, value);
	}

	/**
	 * puts the object as a document for the given ttl
	 * 
	 * @param key
	 * @param ttl
	 * @param value
	 * @throws CouchbaseServiceException
	 */
	public void putValue(String key, int ttl, Object value) throws CouchbaseServiceException {
		
		// input validation
		if (key == null || value == null || ttl < 0) {
			LOGGER.error(LoggerConstants.COUCH_PUT_INVALID_INPUT_FOR_KEY, key);
			return;
		}
		
		String jsonStr = null;
		
		// map the object to json string
		try {
			jsonStr = mapper.writeValueAsString(value);
		} catch (IOException e) {
			LOGGER.error(LoggerConstants.COUCH_JSON_OBJECT_MAPPING_EXCEPTION, key, e);
			throw new DAOInvalidRequestException(LoggerConstants.COUCH_JSON_OBJECT_MAPPING_EXCEPTION, e);
		}
		
		// create couch document
		JsonObject jsonObject = JsonObject.fromJson(jsonStr);
		Document<JsonObject> document = JsonDocument.create(key, ttl, jsonObject);
		
		// upsert the doucment
        long start = System.currentTimeMillis();
		try {
		    bucket.upsert(document);
		} catch (RuntimeException e) {

		    LOGGER.error(LoggerConstants.COUCH_EXCEPTION_FOR_PUT_OPERATION, jsonObject.toString());
		    LOGGER.error(LoggerConstants.COUCH_EXCEPTION, e);
		    throw new CouchbaseServiceException(LoggerConstants.COUCH_EXCEPTION, e);
		}
		long stop = System.currentTimeMillis();
		
		LOGGER.info(LoggerConstants.TIME_TAKEN_FOR_COUCH_PUT_OPERATION, key, stop-start);
	}
	
    /**
	 * returns the bucket name for the current bucket
	 * 
	 * @return
	 */
	public String getBucketName() {
	    
	    return bucket.name();
	}
	
	/**
	 * removes the document associated with the given key
	 * 
	 * @param key
	 */
	public void remove (String key) throws CouchbaseServiceException {
	    
	    try {
	        bucket.remove(key);
        } catch (DocumentDoesNotExistException e) {
            
            LOGGER.error(LoggerConstants.COUCH_EXCEPTION_FOR_REMOVE_KEY, key);
            LOGGER.error(LoggerConstants.COUCH_DOCUMENT_NOT_FOUND_EXCEPTION, e);
            throw new DAOInvalidRequestException(LoggerConstants.COUCH_DOCUMENT_NOT_FOUND_EXCEPTION, e);
	    } catch (RuntimeException e) {
	        
            LOGGER.error(LoggerConstants.COUCH_EXCEPTION_FOR_REMOVE_KEY, key);
	        LOGGER.error(LoggerConstants.COUCH_EXCEPTION, e);
	        throw new CouchbaseServiceException(LoggerConstants.COUCH_EXCEPTION, e);
	    }
	}
	
    /**
     * @param cacheKeys
     * @return bulk JsonDocument for input keys
     * @throws CacheException 
     */
    public List<JsonDocument> bulkGet(final Collection<String> cacheKeys) throws CouchbaseServiceException {
        
        try{
            return Observable.from(cacheKeys).
                    flatMap(cacheKey -> bucket.async().get(cacheKey).timeout(getOperationTimeoutInMillis(), TimeUnit.MILLISECONDS)).
                    toList().toBlocking().single();
        } catch(RuntimeException e){
            
            // for timeout exception
            LOGGER.error(LoggerConstants.COUCH_EXCEPTION_FOR_BULK_GET);
            LOGGER.error(LoggerConstants.COUCH_EXCEPTION, e);
            throw new CouchbaseServiceException(LoggerConstants.COUCH_EXCEPTION, e);
        }
    }
    
    /**
     * returns whether the couchbase cluster is up
     * 
     * @return
     */
    public boolean healthCheck() {
        
        if (bucket == null || StringUtils.isEmpty(bucket.name()))
            return false;

        try {
            bucket.get("test");
        } catch (RuntimeException e) {
            
            LOGGER.error("exception during health check", e);
            return false;
        }
        
        return true;
    }

    /**
     * @param key
     * @return appended version with cache separator for key
     */
    public String appendVersion(String key) {
        
        return new StringBuilder().append(configuration.getKeyPrefix()).append(KEY_SEPARATOR).append(key).toString();
    }
    
    /**
     * retrieves the singleton object mapper
     * 
     * @return
     */
    public ObjectMapper getObjectMapper() {
    	
    	return mapper;
    }
}
