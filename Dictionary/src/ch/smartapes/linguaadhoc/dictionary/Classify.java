package ch.smartapes.linguaadhoc.dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Classify {

	private static Connection con;

	private static PreparedStatement st_insert_classification, st_get_matches;

	public static void main(String[] args) {

		try {
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:en_de.sqlite");
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
			
			st_insert_classification = con.prepareStatement("INSERT INTO belongsto(idWord, idClassification) VALUES(?,?)");
//			st_get_matches = con.prepareStatement("SELECT )
			
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT _id FROM classifications WHERE tag = '" + category+"'");
			if (rs.next()){
				int class_id = rs.getInt(1);
				rs = stmt.executeQuery("SELECT words._id FROM words WHERE language1 LIKE '%"+word+"%' OR comment1 LIKE '%"+word+"%'");
				
				while(rs.next()){
					st_insert_classification.setInt(1, rs.getInt(1));
					st_insert_classification.setInt(2, class_id);
					st_insert_classification.addBatch();
				}
				st_insert_classification.executeBatch();
				con.commit();
				
				for (int k = 0; k < depth; k++) {
					// Closure
				//rs = 
				}
			}
			else{
				System.out.println("invalid Classification");
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
