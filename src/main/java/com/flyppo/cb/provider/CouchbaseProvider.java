package com.flyppo.cb.provider;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.flyppo.cb.config.CouchbaseConfiguration;
import com.flyppo.cb.constants.LoggerConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class CouchbaseProvider implements Provider<Bucket>{

    private Bucket bucket;
    private CouchbaseConfiguration configuration;

    /**
     * constructor used for couchbase manager (cluster, bucket) creation
     * 
     * @param couchNodeList
     * @param bucketName
     * @param bucketPasswd
     * @param contentService
     *          to fetch configuration dynamically
     */
    @Inject
    public CouchbaseProvider(CouchbaseConfiguration config) {

    	this.configuration = config;
        
        // create cluster
        CouchbaseCluster cluster = createCouchbaseCluster(configuration.getCouchNodeKeyList(), configuration.getCouchQueryTimeoutMillis(), configuration.getCouchConnectTimeoutMillis());
        
        // open the bucket
        long logTime = System.currentTimeMillis();
        try {

            this.bucket = cluster.openBucket(configuration.getBucketName(),configuration.getBucketPassword());
        } catch (RuntimeException e) {
            log.error(LoggerConstants.COUCH_BUCKET_CREATE_EXCEPTION, e);
        }
        
        if (this.bucket != null) {
            log.info(LoggerConstants.COUCH_BUCKET_OPENED_SUCCESSFULLY, configuration.getBucketName(), System.currentTimeMillis()-logTime);
        } else {
            log.info(LoggerConstants.COUCH_BUCKET_OPEN_FAILED, configuration.getBucketName());
        }
    }
    
    /**
     * returns the bucket object for querying the couchbase server
     * @return
     */
	public Bucket get() {

		return bucket;
	}
    
    /**
     * creates the couchbase cluster object
     * 
     * @param couchNodeList
     * @param queryTimeout
     * @param connectTimeout
     * @return
     */
    public static CouchbaseCluster createCouchbaseCluster(String couchNodeList, int queryTimeout, int connectTimeout) {
        
        // parse the comma separated nodes
        List<String> couchNodes = parseNodeList(couchNodeList);

        log.info("Starting connection to couchbase cluster");

        // Shuffling to prevent load on a particular server
        long seed = System.currentTimeMillis();
        Collections.shuffle(couchNodes, new Random(seed));

        return CouchbaseCluster.create(buildCouchbaseEnvironment(queryTimeout, connectTimeout), couchNodes);
    }
    
    /**
     * builds the couchbase environment with the appropriate timeouts
     * 
     * @param queryTimeout
     * @param connectTimeout
     * @return
     */
    public static CouchbaseEnvironment buildCouchbaseEnvironment(int queryTimeout, int connectTimeout) {
        
        return DefaultCouchbaseEnvironment.builder()
                .queryTimeout(queryTimeout)
                .connectTimeout(connectTimeout)
//                .keepAliveInterval(Long.valueOf(CacheConfiguration.getProperty(CacheConstants.COUCHBASE_KEEP_ALIVE_INTERVAL,CacheConstants.DEFAULT_KEEP_ALIVE_INTERVAL)) * CacheConstants.SECOND_TO_MILLIS_MULIPLIER)
//                .dnsSrvEnabled(Boolean.valueOf(CacheConfiguration.getProperty(CacheConstants.COUCHBASE_DNS_SERVER_LOOKUP_ENABLED)))
                .build();
    }
    
    /**
     * parses the comma separated couch node list
     * @param couchNodeList
     * @return
     */
    public static List<String> parseNodeList (String couchNodeList) {
        
        return Arrays.asList(couchNodeList.split(","));
    }
}
