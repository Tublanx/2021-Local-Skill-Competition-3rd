import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class Avaliable extends Baseframe {

	JTextField txt[] = new JTextField[5];

	String str[] = "����̸�,��ǥ��,�ּ�,��������,���������з�".split(",");

	public Avaliable(String eno, EmployeeInfo employeeInfo) {
		super("�������ɿ���", 320, 400);
		this.setLayout(new GridLayout(0, 1));

		for (int i = 0; i < txt.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));

			tmp.add(sz(lbl1(str[i], 2, 15), 110, 30));
			tmp.add(txt[i] = new JTextField(15));

			txt[i].setEnabled(false);
			this.add(tmp);
		}
		var tmp = new JPanel();
		tmp.add(btn("�������ɿ��κ���", e -> {
			System.out.println(ugender);

			if (!txt[3].getText().equals(ugender) && !txt[3].getText().equals("����")) {
				eMsg("������ �Ұ��մϴ�.");
				return;
			}

			if (!txt[4].getText().equals("����")
					&& (Arrays.asList(graduate).indexOf(txt[4].getText()) < Arrays.asList(graduate).indexOf(ugender))) {
				eMsg("������ �Ұ��մϴ�.");
				return;
			}

			try {
				var rs = stmt.executeQuery("select * from applicant where e_no=" + eno + " and u_no=" + uno
						+ " and (a_apply = 0 or a_apply = 1)");
				rs.last();
				if (rs.getRow() > 0) {
					eMsg("�հ��� �Ǵ� �ɻ����Դϴ�.");
					return;
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			iMsg("���� ������ �����Դϴ�.");
			employeeInfo.btn[1].setEnabled(true);
			dispose();
		}));
		this.add(tmp);

		try {
			var rs = stmt.executeQuery(
					"select c.c_name, c.c_ceo, c.c_address, e.e_gender, e.e_graduate from employment e, company c where e.c_no = c.c_no and e_no="
							+ eno);
			if (rs.next()) {
				txt[0].setText(rs.getString(1));
				txt[1].setText(rs.getString(2));
				txt[2].setText(rs.getString(3));
				txt[3].setText(gender[rs.getInt(4) - 1]);
				txt[4].setText(graduate[rs.getInt(5)]);
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		this.setVisible(true);
	}
}
