package ch.smartapes.linguaadhoc.dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Classify {

	private static Connection con;

	private static PreparedStatement st_insert_classification;

	public static void main(String[] args) {

		try {
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:linguaadhoc.db");
			con.setAutoCommit(false);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			System.out.println("Enter Classification:");
			String category = br.readLine();
			System.out.println("Enter word:");
			String word = br.readLine();
			System.out.println("Enter depth of closure:");
			int depth = Integer.parseInt(br.readLine());

			// Do initial seeding
			Statement st = con.createStatement();
			st.executeUpdate("CREATE TABLE IF NOT EXISTS belongsto(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "idWord INTEGER, idClassification INTEGER);");
			
//			stmt = con.createStatement();
//			ResultSet rs = stmt.executeQuery("SELECT _id FROM words, classifications")
			
			for (int k = 0; k < depth; k++) {
				// Closure

			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Classify() {
		
	}

}
