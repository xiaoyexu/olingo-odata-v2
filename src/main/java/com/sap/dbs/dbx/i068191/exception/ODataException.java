package com.sap.dbs.dbx.i068191.exception;

public class ODataException {
	public static class DataNotExistException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3208571724430423634L;
		
		public DataNotExistException(String msg) {
			super(msg);
		}
	}
	
	public static class DataExistException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3208571724430423634L;
		
		public DataExistException(String msg) {
			super(msg);
		}
	}
	
	public static DataNotExistException newDataNotExistException(String msg) {
		return new DataNotExistException(msg);
	}
	
	public static DataExistException newDataExistException(String msg) {
		return new DataExistException(msg);
	}
}
