import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class Posting extends Baseframe {

	JComboBox<String> com1, com2;
	JTextField txt[] = { new JTextField(20), new JTextField(20), new JTextField(20) };
	JRadioButton jrd[] = { new JRadioButton("��"), new JRadioButton("��"), new JRadioButton("����") };
	JButton btn1, btn2 = new JButton("����");

	String eno;
	String str[] = "ȸ���,������,�ñ�,��������,����,�����з�".split(",");
	AdminInfo info;

	public Posting() {
		super("������", 500, 500);

		this.add(c = new JPanel(new GridLayout(0, 1)));
		this.add(s = new JPanel(new FlowLayout(2)), "South");

		for (int i = 0; i < str.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));
			tmp.add(sz(lbl1(str[i], 2, 13), 60, 30));

			if (i == 0) {
				tmp.add(sz(com1 = new JComboBox<String>(), 120, 30));
			} else if (i == 5) {
				tmp.add(sz(com2 = new JComboBox<String>(), 120, 30));
			} else if (i == 4) {
				for (var k : jrd) {
					tmp.add(k);
				}
			} else {
				tmp.add(txt[i - 1]);
			}

			c.add(tmp);
		}

		for (var k : graduate) {
			com2.addItem(k);
		}

		try {
			var rs = stmt.executeQuery("select c_name from company where c_no not in (select c_no from employment)");
			while (rs.next()) {
				com1.addItem(rs.getString(1));
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		s.add(btn1 = btn("���", e -> {
			for (int i = 0; i < txt.length; i++) {
				if (txt[i].getText().isEmpty()) {
					eMsg("��ĭ�� �����մϴ�.");
					return;
				}
			}

			if (txt[1].getText().matches(".*[^0-9].*") || txt[2].getText().matches(".*[^0-9].*")) {
				eMsg("���ڷ� �Է��ϼ���.");
				return;
			}

			if (e.getActionCommand().equals("���")) {
				var cno = getone("select * from company where c_name='" + com1.getSelectedItem() + "'");
				var g = 0;

				for (int i = 0; i < jrd.length; i++) {
					if (jrd[i].isSelected()) {
						g = i;
					}
				}

				g += 1;

				execute("insert employment values(0, '" + cno + "','" + txt[0].getText() + "','" + txt[1].getText()
						+ "','" + txt[2].getText() + "','" + g + "','" + com2.getSelectedIndex() + "')");
				iMsg("����� �Ϸ�Ǿ����ϴ�.");
				dispose();
			} else {
				try {
					var rs = stmt.executeQuery(
							"select count(a.a_no) from employment e, applicant a where a.e_no = e.e_no and e.e_no="
									+ eno + " group by e.e_no");
					if (rs.next()) {
						if (rs.getInt(1) > toInt(txt[2].getText())) {
							eMsg("���������� �����ں��� �����ϴ�.");
							return;
						}
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				iMsg("������ �Ϸ�Ǿ����ϴ�.");
				execute("update employment set e_title='" + txt[0].getText() + "', e_pay = '" + txt[1].getText()
						+ "', e_people = '" + txt[2].getText() + "' where e_no = " + eno);
				info.data();
				dispose();
			}
		}));

		c.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "��������"));
		this.setVisible(true);
	}

	public Posting(String eno, AdminInfo adminInfo) {
		this();
		this.eno = eno;
		this.info = adminInfo;

		com1.removeAllItems();
		for (var k : jrd) {
			k.setEnabled(false);
		}
		com2.setEnabled(false);

		try {
			var rs = stmt.executeQuery("select * from applicant where e_no=" + eno);
			rs.last();
			if (rs.getRow() == 0) {
				s.add(btn2 = btn("����", e -> {
					execute("delete from employment where e_no=" + eno);
					iMsg("������ �Ϸ�Ǿ����ϴ�.");
					info.data();
					dispose();
				}));
			}
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		try {
			var rs = stmt.executeQuery("select c_name from company where c_no in (select c_no from employment)");
			while (rs.next()) {
				com1.addItem(rs.getString(1));
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			var rs = stmt.executeQuery("select * from employment e, company c where e.c_no = c.c_no and e_no = " + eno);
			if (rs.next()) {
				txt[0].setText(rs.getString("e_title"));
				txt[1].setText(rs.getString("e_pay"));
				txt[2].setText(rs.getString("e_people"));
				jrd[rs.getInt("e_gender") - 1].setSelected(true);
				com1.setSelectedItem(rs.getString("c_name"));
				com2.setSelectedItem(graduate[rs.getInt("e_graduate")]);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		com1.addActionListener(e -> {
			try {
				var rs = stmt
						.executeQuery("select * from employment e, company c where e.c_no = c.c_no and c.c_name = '"
								+ com1.getSelectedItem() + "'");
				if (rs.next()) {
					txt[0].setText(rs.getString("e_title"));
					txt[1].setText(rs.getString("e_pay"));
					txt[2].setText(rs.getString("e_people"));
					jrd[rs.getInt("e_gender") - 1].setSelected(true);
					com1.setSelectedItem(rs.getString("c_name"));
					com2.setSelectedItem(graduate[rs.getInt("e_graduate")]);
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		btn1.setText("����");
		s.add(btn2);
	}
}
