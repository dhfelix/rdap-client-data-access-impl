package mx.nic.rdap.client.dao.desktop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.IDN;
import java.sql.Connection;
import java.sql.SQLException;

import org.h2.tools.RunScript;

import mx.nic.rdap.client.sql.DatabaseSession;
import mx.nic.rdap.client.sql.QueryGroup;

/**
 *
 */
public class DatabaseCreator {

	public static void createDatabaseTables() throws SQLException, IOException {
		try (Connection connection = DatabaseSession.getRdapConnection();
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						QueryGroup.class.getClassLoader().getResourceAsStream("META-INF/sql/create.sql")))) {
			IDN.toASCII("");
			RunScript.execute(connection, reader);
		}
	}
}
