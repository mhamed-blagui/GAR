package com.gar.system.constants;

public class GarConstants {

	private GarConstants(){
	}

	//Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";
    
	/** PACKAGES NAME **/
	public final static String BASE_PACKAGE_NAME = "com.gar.system";
	public final static String MODEL_PACKAGE_NAME = "com.gar.system..model";
	public final static String DAO_PACKAGE_NAME = "com.gar.system.repository";
	
	/** TABLES NAME **/
	public final static String GAR_USER_TABLE_NAME = "GAR_USER";
	public final static String GAR_AUTHORITY_TABLE_NAME = "GAR_AUTHORITY";
	public final static String GAR_AUDIT_EVENT_TABLE_NAME = "GAR_AUDIT_EVENT";
	public final static String GAR_TASK_HISTORY_TABLE_NAME = "GAR_TASK_HISTORY";
	public final static String GAR_OPERATION_HISTORY_TABLE_NAME = "GAR_OPERATION_HISTORY";
	
	public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";
}
