import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class EmployeeInfo extends Baseframe {

	JTextField txt1, txt2;
	JComboBox com[] = { new JComboBox("��ü,����,�λ�,�뱸,��õ,����,����,���,����,���,����,���,�泲,����,����,���,�泲,����".split(",")),
			new JComboBox("��ü,���б� ����,����б� ����,���б� ����,����".split(",")), new JComboBox("��ü,����,����,����".split(",")) };
	JButton btn[] = { new JButton("�˻��ϱ�"), new JButton("�����ϱ�") };

	DefaultTableModel m = model("�̹���,�����,��������,�ñ�,����,����,�з�,����,eno".split(","));
	JTable t = table(m);
	JScrollPane jsc = new JScrollPane(t);

	String str[] = "�����,����".split(",");

	public EmployeeInfo() {
		super("ä������", 850, 600);

		this.add(lbl2("ä������", 0, 25), "North");
		this.add(c = new JPanel(new GridLayout(0, 1)));
		this.add(s = new JPanel(new BorderLayout()), "South");

		for (int i = 0; i < 3; i++) {
			var tmp = new JPanel(new FlowLayout(0));

			if (i == 2) {
				tmp.setLayout(new BorderLayout());
				var left = new JPanel(new FlowLayout(0));
				var right = new JPanel(new FlowLayout(2));
				left.add(sz(lbl1("����", 0, 15), 60, 30));
				left.add(com[0]);
				left.add(sz(lbl1("�з�", 0, 15), 60, 30));
				left.add(com[1]);
				left.add(sz(lbl1("����", 0, 15), 60, 30));
				left.add(com[2]);
				right.add(btn[0] = btn("�˻��ϱ�", e -> search()));
				right.add(btn[1] = btn("�����ϱ�", e -> {
					iMsg("��û�� �Ϸ�Ǿ����ϴ�.");
					execute("insert applicant values(0, '" + t.getValueAt(t.getSelectedRow(), 8).toString() + "','"
							+ uno + "','" + 0 + "')");
					search();
				}));
				tmp.add(left, "West");
				tmp.add(right, "East");
			} else if (i == 0) {
				tmp.add(sz(lbl1("�����", 0, 15), 60, 30));
				tmp.add(txt1 = new JTextField(20));
			} else if (i == 1) {
				tmp.add(sz(lbl1("����", 0, 15), 60, 30));
				tmp.add(txt2 = new JTextField(20));
			}

			c.add(tmp);
		}

		s.add(jsc);

		search();

		for (int i = 0; i < com.length; i++) {
			com[i].addItemListener(e -> {
				search();
			});
		}

		txt2.addMouseListener(new MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent e) {
				new EmployeeSelection(EmployeeInfo.this).addWindowListener(new Before(EmployeeInfo.this));
			};
		});

		t.addMouseListener(new MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent e) {
				if (t.getSelectedRow() == -1) {
					return;
				}

				btn[1].setEnabled(false);

				new Avaliable(t.getValueAt(t.getSelectedRow(), 8).toString(), EmployeeInfo.this)
						.addWindowListener(new Before(EmployeeInfo.this));
			};
		});

		t.setRowHeight(80);
		t.getColumn("�̹���").setPreferredWidth(80);
		t.getColumn("�����").setPreferredWidth(150);
		t.getColumn("����").setPreferredWidth(150);
		t.getColumn("��������").setPreferredWidth(80);
		t.getColumn("�ñ�").setPreferredWidth(30);
		t.getColumn("����").setPreferredWidth(150);
		t.getColumn("�з�").setPreferredWidth(80);
		t.getColumn("����").setPreferredWidth(20);
		t.getColumn("eno").setMinWidth(0);
		t.getColumn("eno").setMaxWidth(0);

		txt2.setEnabled(false);
		btn[1].setEnabled(false);
		this.setVisible(true);
	}

	void search() {
		m.setRowCount(0);

		String joken1 = "", joken2 = "", joken3 = "";

		if (!txt2.getText().isEmpty()) {
			var cate = Arrays.stream(txt2.getText().split(",")).mapToInt(x -> Arrays.asList(category).indexOf(x))
					.mapToObj(String::valueOf).toArray(String[]::new);
			joken1 = " and c_category in (" + String.join(",", cate) + ") ";
		}

		if (com[1].getSelectedIndex() != 0) {
			joken2 = "and e_graduate =" + (com[1].getSelectedIndex() - 1);
		}

		if (com[2].getSelectedIndex() != 0) {
			joken3 = "and e.e_gender=" + (com[2].getSelectedIndex());
		}

		try {
			var rs = stmt.executeQuery("SELECT \r\n" + "    c.c_name,\r\n" + "    e.e_title,\r\n"
					+ "    CONCAT((SELECT COUNT(*) FROM applicant a WHERE a.e_no = e.e_no AND (a.a_apply = 0 OR a.a_apply = 1)), '/', e.e_people) chk,\r\n"
					+ "    FORMAT(e.e_pay, '#,##0'),\r\n" + " c.c_category,\r\n" + "    c.c_address,\r\n"
					+ "    e.e_graduate,\r\n" + "    e.e_gender,\r\n" + " e.e_no\r\n" + "FROM\r\n"
					+ "    employment e,\r\n" + "    applicant a,\r\n" + "    company c,\r\n" + "    user u\r\n"
					+ "WHERE\r\n" + "     e.c_no = c.c_no\r\n" + joken1 + joken2 + joken3 + " and e.e_title like '%"
					+ txt1.getText() + "%' and left(c.c_address, 2) like '%"
					+ (com[0].getSelectedItem().equals("��ü") ? "" : com[0].getSelectedItem()) + "%' GROUP BY e.e_no;");
			while (rs.next()) {
				var row = new Object[m.getColumnCount()];

				row[0] = new JLabel(icon("���/" + rs.getString(1) + "2", 70, 70));
				row[1] = rs.getString(2);
				row[2] = rs.getString(3);
				row[3] = rs.getString(4);
				row[4] = String.join(",",
						Arrays.stream(rs.getString(5).split(",")).map(x -> category[toInt(x)]).toArray(String[]::new));
				row[5] = rs.getString(6);
				row[6] = graduate[toInt(rs.getString(7))];
				row[7] = gender[toInt(rs.getString(8)) - 1];
				row[8] = rs.getInt(9);

				m.addRow(row);
			}

			rs.last();
			if (rs.getRow() == 0) {
				eMsg("�˻� ����� �����ϴ�.");
				txt1.setText("");
				txt2.setText("");
				com[0].setSelectedIndex(0);
				com[1].setSelectedIndex(0);
				com[2].setSelectedIndex(0);
				search();
				return;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		uno = "1";
		ugender = "����";
		new EmployeeInfo();
	}
}
