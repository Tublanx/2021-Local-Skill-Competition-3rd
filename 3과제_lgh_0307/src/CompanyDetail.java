import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class CompanyDetail extends Baseframe {

	JPanel cc;
	JLabel img;
	JButton btn;
	JTextField txt[] = { new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField() };

	File file;
	Admin a;

	String str[] = "기업명,대표자,주소,직종,직원수".split(",");

	public CompanyDetail(String cno) {
		super("기업상세정보", 300, 500);

		this.add(c = new JPanel(new BorderLayout()));

		c.add(img = sz(new JLabel(), 200, 200), "North");
		c.add(cc = new JPanel(new GridLayout(0, 1, 5, 5)));

		try {
			var rs = stmt.executeQuery("select * from company where c_no=" + cno);
			while (rs.next()) {
				txt[0].setText(rs.getString(2));
				txt[1].setText(rs.getString(3));
				txt[2].setText(rs.getString(4));
				txt[3].setText(String.join(",",
						Arrays.stream(rs.getString(5).split(",")).map(x -> category[toInt(x)]).toArray(String[]::new)));
				txt[4].setText(rs.getString(6));
				img.setIcon(new ImageIcon(
						Toolkit.getDefaultToolkit().createImage(rs.getBlob("c_img").getBinaryStream().readAllBytes())
								.getScaledInstance(270, 200, 4)));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < str.length; i++) {
			var tmp = new JPanel(new BorderLayout());

			tmp.add(sz(lbl1(str[i], 2, 13), 60, 0), "West");
			tmp.add(txt[i]);

			txt[i].setEnabled(false);
			cc.add(tmp);
		}

		this.add(btn = btn("닫기", e -> dispose()), "South");

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
		img.setBorder(new LineBorder(Color.BLACK));
		this.setVisible(true);
	}

	public CompanyDetail(String cno, Admin admin) {
		this(cno);
		this.a = admin;
		this.setTitle("기업정보수정");

		txt[1].setEnabled(true);
		txt[2].setEnabled(true);
		btn.setText("수정");

		img.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					var jfc = new JFileChooser("datafiles/회원사진/");
					jfc.setMultiSelectionEnabled(false);

					if (jfc.showOpenDialog(CompanyDetail.this) == JFileChooser.APPROVE_OPTION) {
						file = jfc.getSelectedFile();
						img.setIcon(new ImageIcon(
								Toolkit.getDefaultToolkit().getImage(file.getPath()).getScaledInstance(270, 200, 4)));
					}
				}
			}
		});

		btn.addActionListener(e -> {
			if (txt[1].getText().isEmpty() || txt[2].getText().isEmpty()) {
				eMsg("빈칸이 존재합니다.");
				return;
			}

			execute("update company set c_ceo='" + txt[1].getText() + "', c_address = '" + txt[2].getText()
					+ "' where c_no = " + cno);

			if (file != null) {
				try {
					try {
						var ps = con.prepareStatement("update company set c_img=? where c_no = ?");
						ps.setObject(1, new FileInputStream(file));
						ps.setObject(2, cno);
						ps.execute();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Files.copy(file.toPath(), new File("datafiles/기업/" + txt[0].getText() + "1.jpg").toPath(),
							StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

			iMsg("수정이 완료되었습니다.");
			this.a.load();
			dispose();

		});
	}
}
