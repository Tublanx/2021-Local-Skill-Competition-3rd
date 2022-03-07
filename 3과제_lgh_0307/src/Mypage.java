import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.sql.SQLException;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Mypage extends Baseframe {

	JPopupMenu pop = new JPopupMenu();
	JMenuItem item;
	JPanel cn, cc;

	DefaultTableModel m = model("번호,기업명,모집정보,시급,모집정원,최종학력,성별,합격여부,ano".split(","));
	JTable t = table(m);
	JScrollPane jsc = new JScrollPane(t);

	public Mypage() {
		super("Mypage", 1000, 400);

		this.add(lbl2("Mypage", 0, 25), "North");
		this.add(c = new JPanel(new BorderLayout()));
		this.add(s = new JPanel(new FlowLayout(2)), "South");

		c.add(cn = new JPanel(new GridLayout(0, 1)), "North");
		c.add(cc = new JPanel(new BorderLayout()));

		cn.add(lbl2("성명 : " + getone("select u_name from user where u_no=" + uno), 2, 20));
		cn.add(lbl2("성별 : " + ugender, 2, 20));
		cn.add(lbl2("최종학력 : " + graduate[toInt(getone("select u_graduate from user where u_no=" + uno))], 2, 20));

		cc.add(jsc);

		try {
			var rs = stmt.executeQuery(
					"select c.c_name, e.e_title, format(e.e_pay, '#,##0'), e.e_people, e.e_graduate, e.e_gender, a.a_apply, a.a_no from applicant a, user u, company c, employment e where a.e_no = e.e_no and a.u_no = u.u_no and e.c_no = c.c_no and a.u_no="
							+ uno);
			while (rs.next()) {
				var row = new Object[m.getColumnCount()];

				row[0] = rs.getRow();
				for (int i = 1; i < 5; i++) {
					row[i] = rs.getString(i);
				}
				row[5] = graduate[rs.getInt(5)];
				row[6] = gender[rs.getInt(6) - 1];
				row[7] = (rs.getInt(7) == 0 ? "심사중" : (rs.getInt(7) == 1 ? "합격" : "불합격"));
				row[8] = rs.getInt(8);

				m.addRow(row);
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		s.add(btn("PDF 인쇄", e -> {
			try {
				t.print();
			} catch (PrinterException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}));

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

			}
		});

		pop.add(item = new JMenuItem("삭제"));

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 3) {
					pop.show(t, e.getX(), e.getY());
				}
			}
		});

		item.addActionListener(e -> {
			iMsg("삭제가 완료되었습니다.");
			execute("delete from applicant where a_no = " + t.getValueAt(t.getSelectedRow(), 8));
			m.removeRow(t.getSelectedRow());
		});

		t.getColumn("모집정보").setPreferredWidth(200);
		t.getColumn("ano").setMinWidth(0);
		t.getColumn("ano").setMaxWidth(0);

		this.setVisible(true);
	}

	public static void main(String[] args) {
		uno = "1";
		ugender = "여자";
		new Mypage();
	}
}
