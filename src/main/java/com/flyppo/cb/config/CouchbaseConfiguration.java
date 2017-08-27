package com.flyppo.cb.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouchbaseConfiguration {

	private int couchOperationTimeoutMillis;
	private int couchQueryTimeoutMillis;
	private int couchConnectTimeoutMillis;
	private String couchNodeKeyList;
	private String bucketName;
	private String bucketPassword;
	private String keyPrefix;
	private String keySeparator;
}
