package ch.smartapes.linguaadhoc.dictionary;

import java.io.BufferedReader;
import java.io.FileInputStream;
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
			//Set up connection
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:en_de.sqlite");
			con.setAutoCommit(false);

			//Get user input
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));

			System.out.println("Enter Classification:");
			String category = br.readLine();
			System.out.println("Enter word:");
			String word = br.readLine();
			System.out.println("Enter depth of closure:");
			int depth = Integer.parseInt(br.readLine());

			// CREATE table if not exists
			Statement st = con.createStatement();
			st.executeUpdate("CREATE TABLE IF NOT EXISTS belongsto(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "idWord INTEGER, idClassification INTEGER);");
			
			//prepare insert statement
			st_insert_classification = con
					.prepareStatement("INSERT INTO belongsto(idWord, idClassification) VALUES(?,?)");
			
			//Decide whether we run over all classification or just a particular classification
			if (category.equals("0")){
				//We look for all matches of words with the terms given by the rules
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						new FileInputStream("res/classifier_rules.txt"), "utf8"));
				ResultSet rs = st.executeQuery("SELECT _id, name FROM classifications");
				while(rs.next()){
					int class_id = rs.getInt(1);
					String line = reader.readLine();
					String[] terms = line.split(",");
					String class_name = rs.getString(2);
					System.out.println("\n"+"NOW:  "+class_name);
					for(int i = 1; i < terms.length; i++){
						System.out.print(terms[i]);
						Statement st2 = con.createStatement();
						ResultSet rs2 = st2.executeQuery("SELECT words._id FROM words WHERE language1 LIKE '%"
								+ terms[i]
								+ "%' OR comment1 LIKE '%"
								+ terms[i]
								+ "%' "
								+ "AND words._id NOT IN (SELECT idWord FROM belongsto WHERE belongsto.idClassification = "
								+ class_id + ")");
						while(rs2.next()){
							st_insert_classification.setInt(1, rs2.getInt(1));
							st_insert_classification.setInt(2, class_id);
							st_insert_classification.addBatch();
						}
						st_insert_classification.executeBatch();
						con.commit();
					}
				}
			}else{
				//We look for ONE particular classification
				ResultSet rs = st
						.executeQuery("SELECT _id FROM classifications WHERE tag = '"
								+ category + "'");
				if (rs.next()) {
					// We have such a classification (with the following id)
					int class_id = rs.getInt(1);
	
					//Get all word ids which have a match
					ResultSet rs2 = st
							.executeQuery("SELECT words._id FROM words WHERE language1 LIKE '%"
									+ word
									+ "%' OR comment1 LIKE '%"
									+ word
									+ "%' "
									+ "AND words._id NOT IN (SELECT idWords FROM belongsto WHERE belongsto.idClassification = "
									+ class_id + ")");
	
					while (rs2.next()) {
						st_insert_classification.setInt(1, rs2.getInt(1));
						st_insert_classification.setInt(2, class_id);
						st_insert_classification.addBatch();
					}
					st_insert_classification.executeBatch();
					con.commit();
	
					for (int k = 0; k < depth; k++) {
						// TODO:Closure
					}
				} else {
					System.out.println("invalid Classification");
				}
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
