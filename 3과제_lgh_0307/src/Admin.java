import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class Admin extends Baseframe {

	String str[] = "채용 정보,지원자 목록,공고 등록,지원자 분석,닫기".split(",");

	public Admin() {
		super("관리자 메인", 700, 700);

		this.add(c = new JPanel(new GridLayout(5, 5, 5, 5)));
		this.add(s = new JPanel(), "South");

		load();

		for (int i = 0; i < str.length; i++) {
			s.add(btn(str[i], e -> {
				if (e.getActionCommand().equals(str[0])) {
					new AdminInfo().addWindowListener(new Before(Admin.this));
				} else if (e.getActionCommand().equals(str[1])) {
					new UserInfo().addWindowListener(new Before(Admin.this));
				} else if (e.getActionCommand().equals(str[2])) {
					new Posting().addWindowListener(new Before(Admin.this));
				} else if (e.getActionCommand().equals(str[3])) {
					new Analyze().addWindowListener(new Before(Admin.this));
				} else {
					dispose();
				}
			}));
		}

		this.setVisible(true);
	}

	void load() {
		c.removeAll();

		try {
			var rs = stmt.executeQuery("select c_no, c_img, c_name from company");
			while (rs.next()) {
				var lbl = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit()
						.createImage(rs.getBlob(2).getBinaryStream().readAllBytes()).getScaledInstance(130, 120, 4))) {
					float alpha = 0.1f;

					@Override
					protected void paintComponent(Graphics g) {
						Graphics2D g2d = (Graphics2D) g;
						g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
						super.paintComponent(g);
					}
				};

				String cno = rs.getString(1);

				lbl.setToolTipText(rs.getString(3));

				lbl.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						new CompanyDetail(cno, Admin.this).addWindowListener(new Before(Admin.this));
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						lbl.alpha = 1f;
						lbl.repaint();
					}

					@Override
					public void mouseExited(MouseEvent e) {
						lbl.alpha = 0.1f;
						lbl.repaint();
					}
				});

				lbl.setBorder(new LineBorder(Color.BLACK));
				c.add(lbl);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Admin();
	}
}
