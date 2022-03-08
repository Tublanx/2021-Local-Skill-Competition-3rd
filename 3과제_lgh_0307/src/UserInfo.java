import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class UserInfo extends Baseframe {

	LocalDate today = LocalDate.now();

	String str[] = "지원 회사,이름,생년월일,최종학력,email".split(",");

	public UserInfo() {
		super("지원자 정보", 500, 500);

		this.add(new JScrollPane(c = new JPanel(new GridLayout(0, 1))));
		data();

		this.setVisible(true);
	}

	void data() {
		c.removeAll();

		try {
			var rs = stmt.executeQuery(
					"select c_name, u_name, u_birth, u_graduate, u_email, u_img, a.a_no, c.c_no from applicant a, user u, employment e, company c where a.e_no = e.e_no and a.u_no = u.u_no and e.c_no = c.c_no and a.a_apply = 0");
			while (rs.next()) {
				var tmp = new JPanel(new BorderLayout(20, 0));
				var tmp_c = new JPanel(new GridLayout(0, 1));
				var img = new JLabel(new ImageIcon(
						Toolkit.getDefaultToolkit().createImage(rs.getBlob("u_img").getBinaryStream().readAllBytes())
								.getScaledInstance(100, 100, 4)));
				var birth = LocalDate.parse(rs.getString("u_birth"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				var age = today.getYear() - birth.getYear() + 1;
				var ano = rs.getInt("a_no");
				var cno = rs.getInt("c_no");

				for (int i = 0; i < str.length; i++) {
					if (i == 0) {
						tmp_c.add(lbl2(str[i] + " : " + rs.getString(i + 1), 2, 15));
					} else if (i == 1) {
						tmp_c.add(lbl2(str[i] + " : " + rs.getString(i + 1) + "(나이 : " + age + "세)", 2, 15));
					} else if (i == 4) {
						tmp_c.add(lbl2(str[i] + " : " + rs.getString(i + 1), 2, 15));
					} else if (i == 3) {
						tmp_c.add(lbl2(str[i] + " : " + graduate[rs.getInt(i + 1)], 2, 15));
					} else {
						tmp_c.add(lbl2(str[i] + " : " + rs.getString(i + 1), 2, 15));
					}
				}

				var pop = new JPopupMenu();

				for (var i : "합격,불합격".split(",")) {
					var item = new JMenuItem(i);
					pop.add(item);

					item.addActionListener(e -> {
						var res = e.getActionCommand().equals("합격") ? 1 : 2;

						execute("update applicant set a_apply=" + res + " where a_no=" + ano);
						if (res == 1) {
							execute("update company set c_employee = c_employee + 1 where c_no = " + cno);
						}

						iMsg("심사가 완료되었습니다.");
						data();
					});
				}

				tmp.add(img, "West");
				tmp.add(tmp_c);

				tmp.setComponentPopupMenu(pop);
				tmp.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5, 5, 5, 5)));

				c.add(sz(tmp, 200, 150));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		repaint();
		revalidate();
	}
}
