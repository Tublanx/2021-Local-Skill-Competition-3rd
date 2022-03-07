import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login extends Baseframe {

	JPanel cw, cc, ce;
	JTextField txt[] = { new JTextField(15), new JPasswordField() };

	String str[] = "���̵�,��й�ȣ".split(",");

	public Login(Main m) {
		super("�α���", 450, 200);

		this.add(lbl2("�Ƹ�����Ʈ", 0, 35), "North");
		this.add(c = new JPanel(new BorderLayout()));

		c.add(cw = new JPanel(new GridLayout(0, 1, 15, 30)), "West");
		c.add(cc = new JPanel(new GridLayout(0, 1, 15, 30)));
		c.add(ce = new JPanel(new GridLayout()), "East");

		for (int i = 0; i < str.length; i++) {
			cw.add(lbl1(str[i], 2, 15));
			cc.add(txt[i]);
		}
		ce.add(sz(btn("�α���", e -> {
			if (txt[0].getText().isEmpty() || txt[1].getText().isEmpty()) {
				eMsg("��ĭ�� �����մϴ�.");
				return;
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				iMsg("�����ڷ� �α����Ͽ����ϴ�.");
				new Admin().addWindowListener(new Before(Login .this));
				return;
			}

			try {
				var rs = stmt.executeQuery("select * from user where u_id = '" + txt[0].getText() + "' and u_pw = '"
						+ txt[1].getText() + "'");
				if (!rs.next()) {
					eMsg("ȸ�� ������ ��ġ���� �ʽ��ϴ�.");
					txt[0].setText("");
					txt[1].setText("");
					txt[0].requestFocus();
					return;
				} else {
					uno = rs.getString(1);
					iMsg(rs.getString(2) + "�� ȯ���մϴ�.");
					m.login(rs.getBlob("u_img").getBinaryStream().readAllBytes());
					this.dispose();
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}), 120, 120));

		cw.setBorder(new EmptyBorder(0, 0, 0, 15));
		cc.setBorder(new EmptyBorder(0, 0, 0, 15));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
		this.setVisible(true);
	}
}
