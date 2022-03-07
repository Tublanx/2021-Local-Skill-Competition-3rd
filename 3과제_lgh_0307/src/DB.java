import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class DB {
	static Connection con;
	static Statement stmt;

	static {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost?serverTimezone=UTC&allowLoadLocalInfile=true",
					"root", "1234");
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "���� ����", "���", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	void createT(String t, String c) {
		execute("create table " + t + "(" + c + ")");
		execute("load data local infile './datafiles/" + t + ".txt' into table " + t + " ignore 1 lines");
	}

	public DB() {
		execute("drop database if exists 2022����_2");
		execute("create database 2022����_2 default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant insert, select, update, delete on 2022����_2.* to user@localhost");
		execute("set global local_infile = 1");
		execute("use 2022����_2");

		createT("company",
				"c_no int primary key not null auto_increment, c_name varchar(10), c_ceo varchar(10), c_address varchar(100), c_category varchar(15), c_employee varchar(15), c_img longblob, c_search int");
		createT("employment",
				"e_no int primary key not null auto_increment, c_no int, e_title varchar(30), e_pay int, e_people int, e_gender int, e_graduate int, foreign key(c_no) references company(c_no)");
		createT("user",
				"u_no int primary key not null auto_increment, u_name varchar(10), u_id varchar(10), u_pw varchar(15), u_birth varchar(15), u_email varchar(30), u_gender int, u_graduate int, u_address varchar(100), u_img longblob");
		createT("applicant",
				"a_no int primary key not null auto_increment, e_no int, u_no int, a_apply int, foreign key(e_no) references employment(e_no), foreign key(u_no) references user(u_no)");

		try {
			for (var pic : new File("datafiles/���").listFiles()) {
				if (pic.getName().matches(".*[1].*")) {
					var ps1 = con.prepareStatement("update company set c_img=? where c_name like ?");
					ps1.setBinaryStream(1, new FileInputStream(pic));
					ps1.setObject(2, "%" + pic.getName().substring(0, pic.getName().length() - 5) + "%");
					ps1.execute();
				}
			}

			var ps2 = con.prepareStatement("update user set u_img=? where u_no=?");

			var rs2 = stmt.executeQuery("select u_no from user");
			while (rs2.next()) {
				ps2.setObject(1, new FileInputStream(new File("datafiles/ȸ������/" + rs2.getInt(1) + ".jpg")));
				ps2.setObject(2, rs2.getInt(1));
				ps2.execute();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "���� ����", "���", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		JOptionPane.showMessageDialog(null, "���� ����", "����", JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);
	}

	public static void main(String[] args) {
		new DB();
	}
}
