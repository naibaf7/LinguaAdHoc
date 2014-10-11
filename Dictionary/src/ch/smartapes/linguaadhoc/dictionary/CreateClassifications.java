package ch.smartapes.linguaadhoc.dictionary;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateClassifications {

	private static Connection con;

	private static PreparedStatement st_insert_classification;

	public static void main(String[] args) {
		try {
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:linguaadhoc.db");
			con.setAutoCommit(false);
			Statement stat = con.createStatement();
			stat.executeQuery("DROP TABLE IF EXISTS classifications;");
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS classifications(id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "tag VARCHAR(128), name VARCHAR(128));");

			st_insert_classification = con
					.prepareStatement("INSERT INTO classifications(tag, name) VALUES(?,?)");

			CreateClassifications creator = new CreateClassifications();
			creator.fillTable();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public CreateClassifications() {

	}

	public void fillTable() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream("res/classifications.txt"), "utf8"));

			int count = 0;
			String line;

			System.out.println(reader.readLine());
			while ((line = reader.readLine()) != null) {
				count++;
				if (line.startsWith("#") || line.isEmpty()) {
					// Comment, skip it
					continue;
				}
				
				if (line.contains(",")){
					//Special Case
					String[] input = line.split(",");

					st_insert_classification.setString(1, input[0]);
					st_insert_classification.setString(2, input[1]);
				}
				else{
					st_insert_classification.setString(1, line);
					String name = line.replace("_", " ");
					//TODO: First letters to uppercase
					st_insert_classification.setString(2, name);					
				}
				st_insert_classification.addBatch();
			}
			st_insert_classification.executeBatch();
			con.commit();
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
