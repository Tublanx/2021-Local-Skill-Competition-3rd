import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

public class Sign extends Baseframe {

	JPanel cc;
	JPanel main1, main2, sub1, sub2;
	JLabel img;
	JTextField txt[] = { new JTextField(15), new JTextField(15), new JPasswordField(15), new JTextField(15) };
	JTextField email, detail;
	JComboBox<String> domain, education, addr;
	JRadioButton male, female;
	ButtonGroup bg = new ButtonGroup();

	File file;

	String str1[] = "이름,아이디,비밀번호,생년월일".split(",");
	String str2[] = "이메일,성별,최종학력,주소".split(",");

	public Sign() {
		super("회원가입", 550, 500);

		this.add(c = new JPanel(new BorderLayout()));
		this.add(s = new JPanel(new FlowLayout(2)), "South");

		c.add(cc = new JPanel(new GridLayout(0, 1, 10, 10)));

		cc.add(main1 = new JPanel(new BorderLayout()));
		cc.add(main2 = new JPanel(new GridLayout(0, 1, 10, 10)));

		main1.add(sub1 = new JPanel(new GridLayout(0, 1, 10, 10)));
		main1.add(sub2 = sz(new JPanel(new BorderLayout()), 200, 0), "East");

		for (int i = 0; i < str1.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));
			tmp.add(sz(lbl1(str1[i], 2, 13), 80, 30));
			tmp.add(txt[i]);
			sub1.add(tmp);
		}
		sub2.add(img = new JLabel());

		for (int i = 0; i < str2.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));
			tmp.add(sz(lbl1(str2[i], 2, 13), 80, 30));

			if (i == 0) {
				tmp.add(email = new JTextField(10));
				tmp.add(lbl1("@", 0, 13));
				tmp.add(domain = new JComboBox<String>(new DefaultComboBoxModel<>(
						"naver.com,outlook.com,daum.com,gmail.com,nate.com,kebi.com,yahoo.com,korea.com,empal.com,hanmail.net"
								.split(","))));
			} else if (i == 1) {
				tmp.add(male = new JRadioButton("남"));
				tmp.add(female = new JRadioButton("여"));
				bg.add(male);
				bg.add(female);
				male.setSelected(true);
			} else if (i == 2) {
				tmp.add(education = new JComboBox<String>("대학교 졸업,고등학교 졸업,중학교 졸업".split(",")));
			} else if (i == 3) {
				tmp.add(addr = new JComboBox<String>(new DefaultComboBoxModel<String>(
						",서울,부산,대구,인천,광주,대전,울산,세종,경기,강원,충북,충남,전북,전남,경북,경남,제주".split(","))));
				tmp.add(detail = new JTextField(20));
				sz(addr, 100, 25);
			}

			main2.add(tmp);
		}

		s.add(sz(btn("가입", e -> {
			for (int i = 0; i < txt.length; i++) {
				if (txt[i].getText().isEmpty() || img.getIcon() == null || email.getText().isEmpty()
						|| detail.getText().isEmpty() || education.getSelectedIndex() == -1
						|| addr.getSelectedIndex() == -1 || domain.getSelectedIndex() == -1) {
					eMsg("빈칸이 있습니다.");
					return;
				}
			}

			String pw = txt[2].getText(), date = txt[3].getText(), id = txt[1].getText();
			if (!(pw.matches(".*[0-9].*") && pw.matches(".*[a-zA-Z].*") && pw.matches(".*[!@#$].*"))
					|| pw.length() < 4) {
				eMsg("비밀번호 형식이 일치하지 않습니다.");
				return;
			}

			if (id.equals(getone("select u_id from user where u_id = '" + id + "'"))) {
				eMsg("이미 존재하는 아이디입니다.");
				txt[1].setText("");
				txt[1].requestFocus();
				return;
			}

			try {
				if (LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).isAfter(LocalDate.now())) {
					eMsg("생년월일 형식이 맞지 않습니다.");
					txt[3].setText("");
					txt[3].requestFocus();
					return;
				}
			} catch (Exception e1) {
				eMsg("생년월일 형식이 맞지 않습니다.");
				txt[3].setText("");
				txt[3].requestFocus();
				return;
			}

			try {
				String str[] = { txt[0].getText(), id, pw, txt[3].getText(),
						email.getText() + "@" + domain.getSelectedItem(), (male.isSelected() ? "1" : "2"),
						String.valueOf(education.getSelectedIndex()), addr.getSelectedItem() + detail.getText() };
				var ps = con.prepareStatement("insert user values(0,?,?,?,?,?,?,?,?,?)");
				for (int i = 1; i < 9; i++) {
					ps.setObject(i, str[i - 1]);
				}
				ps.setObject(9, new FileInputStream(file));
				ps.execute();

				var no = getone("select u_no from user where u_id = '" + txt[0].getText() + "'");
				Files.copy(file.toPath(), new File("datafiles/회원사진/" + no + ".jpg").toPath());
				iMsg("회원가입이 완료되었습니다.");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			dispose();
		}), 100, 30));

		addr.addItemListener(e -> {
			detail.setText("");
			detail.requestFocus();

			if (addr.getSelectedIndex() != 0) {
				detail.setEnabled(true);
			} else {
				detail.setEnabled(false);
			}
		});

		img.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					var jfc = new JFileChooser("datafiles/회원사진/");
					jfc.setMultiSelectionEnabled(false);
					jfc.setFileFilter(new FileFilter() {

						@Override
						public String getDescription() {
							return "JPG Images";
						}

						@Override
						public boolean accept(File f) {
							return f.getName().endsWith("jpg");
						}
					});

					if (jfc.showOpenDialog(Sign.this) == JFileChooser.APPROVE_OPTION) {
						file = jfc.getSelectedFile();
						img.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(file.getPath())
								.getScaledInstance(img.getWidth(), img.getHeight(), 4)));
					}
				}
			}
		});

		detail.setEnabled(false);
		education.setSelectedIndex(-1);
		img.setBorder(new LineBorder(Color.BLACK));
		c.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "회원가입"));
		this.setVisible(true);
	}

	public static void main(String[] args) {
		new Sign();
	}
}
