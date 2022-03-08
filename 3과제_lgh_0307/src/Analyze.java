import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.sql.SQLException;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public class Analyze extends Baseframe {

	JPanel chart;
	JComboBox<String> com;

	String str[] = "10,20,30,40,50".split(",");
	Color col[] = { Color.black, Color.blue, Color.red, Color.green, Color.yellow };

	public Analyze() {
		super("지원자 분석", 600, 450);

		this.add(chart = new JPanel(new BorderLayout()) {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setFont(new Font("HY헤드라인M", 0, 25));
				g2d.drawString("회사별 지원자 (연령별)", 100, 30);

				g2d.setFont(new Font("", 0, 13));
				try {
					var rs = stmt.executeQuery("SELECT \r\n" + "    SUM(IF(YEAR(NOW()) - YEAR(u_birth) + 1 >= 10\r\n"
							+ "            AND YEAR(NOW()) - YEAR(u_birth) + 1 < 20,\r\n" + "        1,\r\n"
							+ "        0)) one,\r\n" + "    SUM(IF(YEAR(NOW()) - YEAR(u_birth) + 1 >= 20\r\n"
							+ "            AND YEAR(NOW()) - YEAR(u_birth) + 1 < 30,\r\n" + "        1,\r\n"
							+ "        0)) two,\r\n" + "    SUM(IF(YEAR(NOW()) - YEAR(u_birth) + 1 >= 30\r\n"
							+ "            AND YEAR(NOW()) - YEAR(u_birth) + 1 < 40,\r\n" + "        1,\r\n"
							+ "        0)) three,\r\n" + "    SUM(IF(YEAR(NOW()) - YEAR(u_birth) + 1 >= 40\r\n"
							+ "            AND YEAR(NOW()) - YEAR(u_birth) + 1 < 50,\r\n" + "        1,\r\n"
							+ "        0)) four,\r\n" + "    SUM(IF(YEAR(NOW()) - YEAR(u_birth) + 1 >= 50\r\n"
							+ "            AND YEAR(NOW()) - YEAR(u_birth) + 1 < 60,\r\n" + "        1,\r\n"
							+ "        0)) five\r\n" + "FROM\r\n" + "    applicant a,\r\n" + "    user u,\r\n"
							+ "    employment e,\r\n" + "    company c\r\n" + "WHERE\r\n"
							+ "    a.e_no = e.e_no AND a.u_no = u.u_no\r\n"
							+ "        AND c.c_no = e.c_no and c.c_name = '" + com.getSelectedItem()
							+ "' group by e.e_no");
					int max = 0, i = 0, width = 50, height = 250, base = 300;
					if (rs.next()) {
						for (int j = 1; j <= rs.getMetaData().getColumnCount(); j++) {
							if (max < rs.getInt(j)) {
								max = rs.getInt(j);
							}
						}

						for (int k = 0; k < str.length; k++) {
							var pr = (double) rs.getInt(k + 1) / max;

							g2d.setColor(Color.BLACK);
							g2d.drawString(str[k] + "대", 50 + k * 100, 320);
							g2d.setColor(col[k]);
							g2d.fillRect(40 + 100 * k, (int) (base - (height * pr)), width, (int) (height * pr));
							g2d.fillRect(500, 150 + k * 20, 15, 15);
							g2d.setColor(Color.BLACK);
							g2d.drawRect(40 + 100 * k, (int) (base - (height * pr)), width, (int) (height * pr));
							g2d.drawString(str[i] + "대:" + rs.getString(k + 1) + "명", 520, 160 + k * 20);
						}

					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		chart.add(n = new JPanel(new FlowLayout(2)), "North");
		n.add(com = new JComboBox<String>());

		try {
			var rs = stmt.executeQuery("select c_name from company c, employment e where e.c_no = c.c_no");
			while (rs.next()) {
				com.addItem(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		com.addActionListener(e -> {
			chart.repaint();
			chart.revalidate();
		});

		n.setOpaque(false);

		repaint();
		revalidate();

		this.setVisible(true);
	}

	public static void main(String[] args) {
		new Analyze();
	}
}
