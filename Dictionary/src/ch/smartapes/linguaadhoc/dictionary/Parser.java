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

public class Parser {

	private static Connection con;

	private static PreparedStatement st_insert_word;

	public static void main(String[] args) {

		try {
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:linguaadhoc.db");
			con.setAutoCommit(false);
			Statement stat = con.createStatement();
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS words(id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "language1 VARCHAR(255), language2 VARCHAR(255),"
					+ "comment1 VARCHAR(255), comment2 VARCHAR(255));");

			st_insert_word = con
					.prepareStatement("INSERT INTO words(language1, language2, comment1, comment2) VALUES(?,?,?,?);");

			Parser parser = new Parser();
			parser.parse();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Parser() {

	}

	public void parse() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream("res/de-en.txt"), "utf8"));

			final int NOUNS = 112778;

			int count = 0;
			String line;

			System.out.println(reader.readLine());
			while (count < NOUNS & (line = reader.readLine()) != null) {
				count++;
				if (line.startsWith("#") || line.isEmpty()) {
					// Comment, skip it
					continue;
				}
				String[] lang = line.split(" :: ");
				String[] de = lang[0].split(" \\| ");
				String[] en = lang[1].split(" \\| ");

				// Rule for nouns
				if (count < NOUNS) {
					String[] de_firstword = de[0].split(" \\{");
					String[] en_firstword = en[0].split("\\;");
					System.out.println("|" + de_firstword[0] + "|==|"
							+ en_firstword[0] + "|");
					String com1 = "";
					for (int i = 1; i < de_firstword.length; i++) {
						com1 = com1.concat(de_firstword[i]);
					}
					String com2 = "";
					for (int i = 1; i < en_firstword.length; i++) {
						com2 = com2.concat(en_firstword[i]);
					}
					System.out.println(com1 + " " + com2);

					st_insert_word.setString(1, de_firstword[0]);
					st_insert_word.setString(2, en_firstword[0]);
					st_insert_word.setString(3, com1);
					st_insert_word.setString(4, com2);
					st_insert_word.addBatch();

				}

				if (count % 1000 == 0) {
					st_insert_word.executeBatch();
					con.commit();
				}
			}
			st_insert_word.executeBatch();
			con.commit();
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
