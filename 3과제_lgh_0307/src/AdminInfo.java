import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class AdminInfo extends Baseframe {

	DefaultTableModel m = model("�̹���,������,�����ο�,�ñ�,����,�ּ�,�����з�,����,eno".split(","));
	JTable t = table(m);
	JScrollPane jsc = new JScrollPane(t);

	public AdminInfo() {
		super("������ ä������", 850, 600);

		this.add(n = new JPanel(new BorderLayout()), "North");

		n.add(lbl2("������ ä������", 0, 25));
		n.add(btn("�������", e -> {
			if (t.getSelectedRow() == -1) {
				eMsg("������ ���� �����ϼ���.");
				return;
			}

			new Posting(t.getValueAt(t.getSelectedRow(), 8).toString(), this)
					.addWindowListener(new Before(AdminInfo.this));
		}), "East");
		this.add(jsc);

		data();

		t.setRowHeight(80);
		t.getColumn("�̹���").setPreferredWidth(80);
		t.getColumn("������").setPreferredWidth(150);
		t.getColumn("�����ο�").setPreferredWidth(80);
		t.getColumn("�ñ�").setPreferredWidth(40);
		t.getColumn("����").setPreferredWidth(150);
		t.getColumn("�ּ�").setPreferredWidth(150);
		t.getColumn("�����з�").setPreferredWidth(80);
		t.getColumn("����").setPreferredWidth(20);
		t.getColumn("eno").setMinWidth(0);
		t.getColumn("eno").setMaxWidth(0);

		this.setVisible(true);
	}
	
	void data() {
		m.setRowCount(0);
		
		try {
			var rs = stmt.executeQuery(
					"select c.c_name, e.e_title, e.e_people, format(e.e_pay, '#,##0'), c.c_category, c.c_address, e.e_graduate, e.e_gender, e.e_no from employment e, company c, applicant a where e.c_no = c.c_no and (a.a_apply = 0 or a.a_apply = 1) group by e.e_no");
			while (rs.next()) {
				var row = new Object[m.getColumnCount()];

				row[0] = new JLabel(icon("���/" + rs.getString(1) + "2", 70, 70));
				row[1] = rs.getString(2);
				row[2] = rs.getInt(3);
				row[3] = rs.getString(4);
				row[4] = String.join(",",
						Arrays.stream(rs.getString(5).split(",")).map(x -> category[toInt(x)]).toArray(String[]::new));
				row[5] = rs.getString(6);
				row[6] = graduate[toInt(rs.getString(7))];
				row[7] = gender[toInt(rs.getString(8)) - 1];
				row[8] = rs.getInt(9);

				m.addRow(row);
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
