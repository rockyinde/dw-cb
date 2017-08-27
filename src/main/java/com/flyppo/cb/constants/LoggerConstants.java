package com.flyppo.cb.constants;

/**
 * utility class to hold all static logging string literals
 * @author MMT6461
 *
 */
public class LoggerConstants {

    public static final String DAO_INTERNAL_EXCEPTION = "internal DAO exception. returning failure";
    public static final String MONGO_QUERY_EXCEPTION = "Query to mongodb threw an exception";
    public static final String MONGO_QUERY_FAILURE = "Could not fulfill the request. Returning failure.";
    public static final String INPUT_JSON_REQUEST = "Input Json Request: {}";
    public static final String JSON_OBJECT_MAPPING_EXCEPTION = "Exception while mapping json data to object";

    // couchbase manager
    public static final String COUCH_KEY_IS_NULL = "couch key is null";
    public static final String COUCH_DOCUMENT_IS_NULL_FOR_KEY = "couch document is null for key: {}";
    public static final String COUCH_PUT_VALUE_IS_NULL_FOR_KEY = "couch put value is null for key: {}";
    public static final String COUCH_PUT_INVALID_INPUT_FOR_KEY = "invalid input params given for couch put for key: {}";
    public static final String COUCH_JSON_OBJECT_MAPPING_EXCEPTION = "Exception while mapping put value to json for key: {}";
    public static final String TIME_TAKEN_FOR_COUCH_PUT_OPERATION = "time taken for put operation for key {} is: {} ms";
    public static final String COUCH_TIMEOUT_VALUE = "couchbase timeout value is set to: {} ms";
    public static final String COUCH_BUCKET_OPENED_SUCCESSFULLY = "Bucket {} opened and took {} ms.";
    public static final String COUCH_BUCKET_OPEN_FAILED = "Bucket {} is null and could not be opened.";
    public static final String COUCH_BUCKET_CREATE_EXCEPTION = "Error while creating CouchBaseClient Object";
    public static final String COUCH_TIME_TAKEN_GET_VALUE = "time taken to retrieve document for key: {} is: {} ms";
    public static final String COUCH_GET_DOCUMENT_RETURNED_NULL = "null document returned for key: {}";
    public static final String COUCH_MAPPING_ERROR = "Could not map value from bucket {} for key : {}";
    public static final String COUCH_EXCEPTION_FOR_EXECUTING_QUERY = "exception when executing query: {}";
    public static final String COUCH_EXCEPTION_FOR_PUT_OPERATION = "exception for put operation for document: {}";
    public static final String COUCH_EXCEPTION_FOR_REMOVE_KEY = "exception for remove operation for key: {}";
    public static final String COUCH_EXCEPTION_FOR_BULK_GET = "exception for bulk get operation";
    
    // couchbase dao
    public static final String COUCH_EXCEPTION = "couchbase exception raised";
    public static final String COUCH_DOCUMENT_NOT_FOUND_EXCEPTION = "document does not exist on couchbase for key: {}";
    public static final String COUCH_QUERY_USED = "query being used: {}";
    public static final String COUCH_QUERY_RESULT_PARSE_ERROR = "query returned with errors {}";
    public static final String COUCH_REMOVE_DOCUMENT_QUERY = "received remove user preferences request for userid {}, and deviceid {}";
    public static final String COUCH_REMOVE_DOCUMENT_DOES_NOT_EXIST = "remove user preferences document does not exist";
    public static final String COUCH_DOCUMENT_MAPPING_EXCEPTION = "exception while mapping json document to entity object";
    public static final String COUCH_TIMEOUT_EXCEPTION_FOR_KEY = "couch timeout exception for key: {}";
    public static final String COUCH_TIMEOUT_EXCEPTION = "couch timeout exception";
    
    // DB installer
    public static final String DB_INSTALLATION_INVALID_SEED_DATA = "invalid seed data";
    public static final String DB_INSTALLATION_PREF_GROUP_OBJECT = "Saving preference group object for : Id = {}";
    public static final String DB_INSTALLATION_DONE_SAVING_PREF_GROUP = "Done preference group Object for Id = {}";
    public static final String DB_INSTALLATION_PREF_OBJECT = "Saving preference object for : Id = {}";
    public static final String DB_INSTALLATION_DONE_SAVING_PREF = "Done preference Object for Id = {}";
    public static final String DB_INSTALLATION_JSON_EXCEPTION = "Exception while getting string data of json element : {}";
    public static final String DB_INSTALLATION_EMPTY_JSON_FILE = "Json data is not present in the file : {}";
    public static final String COUCHBASE_DESIGN_DOCUMENT_ALREADY_INSERTED = "design document already inserted. ignoring";
    public static final String INVALID_OPTION_EXCEPTION = "Invalid option selected from client side";
    
    // user service
    public static final String CONNECTOR_USER_SERVICE_EXCEPTION = "exception when connecting to user service";
    
    private LoggerConstants () {
        throw new IllegalAccessError();
    }
}
