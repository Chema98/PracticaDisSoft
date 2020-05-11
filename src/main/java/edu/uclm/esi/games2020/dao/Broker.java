package edu.uclm.esi.games2020.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Broker {
	private String user;
	private String pwd;
	private String url;
	
	private Broker() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.url="jdbc:mysql://alarcosj.esi.uclm.es:3306/ajedrez?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&useSSL=false";
			user="ideas";
			pwd="ideas123";
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	private static class BrokerHolder {
		static Broker singleton=new Broker();
	}
	
	public static Broker get() {
		return BrokerHolder.singleton;
	}

	public Connection getBd() throws SQLException {
		return DriverManager.getConnection(url, user, pwd);
	}
}
