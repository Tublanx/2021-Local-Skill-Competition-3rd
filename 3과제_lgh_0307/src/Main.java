import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Main extends Baseframe {

	JPanel cn, cc, sn, sc;
	JPanel cmain, csub1, csub2;
	JLabel img, name;
	JTextField txt;
	JButton btn[] = { new JButton(), new JButton(), new JButton(), new JButton() };
	JComboBox<String> com;
	Timer timer;

	ArrayList<JPanel> imglist = new ArrayList<>();
	ArrayList<Integer> noList = new ArrayList<Integer>();

	public Main() {
		super("Main", 400, 500);
		this.setDefaultCloseOperation(3);

		this.add(n = new JPanel(), "North");
		this.add(c = new JPanel(new BorderLayout()));
		this.add(s = new JPanel(new BorderLayout()), "South");

		n.add(lbl2("아르바이트", 0, 20));
		n.add(img = new JLabel());
		n.add(name = new JLabel());

		c.add(cn = new JPanel(), "North");
		c.add(cc = new JPanel(new BorderLayout()));

		s.add(sn = new JPanel(new FlowLayout(0)), "North");
		s.add(sc = sz(new JPanel(null), 0, 100));

		cn.add(lbl1("기업검색", 2, 20));
		cn.add(txt = new JTextField(15));
		cn.add(btn("검색", e -> search()));

		cc.add(cmain = new JPanel(new GridLayout(1, 0, 20, 5)));
		cc.add(lbl1("인기기업", 2, 20), "North");

		cmain.add(csub1 = new JPanel(new GridLayout(0, 1, 5, 5)));
		cmain.add(csub2 = new JPanel(new GridLayout(0, 1, 5, 5)));

		logout();
		famous();

		for (int i = 0; i < 4; i++) {
			csub2.add(btn[i]);

			btn[i].addActionListener(e -> {
				if (e.getActionCommand().equals("로그인")) {
					new Login(this).addWindowListener(new Before(Main.this));
				} else if (e.getActionCommand().equals("로그아웃")) {
					iMsg("로그아웃 되었습니다.");
					logout();
				} else if (e.getActionCommand().equals("회원가입")) {
					new Sign().addWindowListener(new Before(Main.this));
				} else if (e.getActionCommand().equals("채용정보")) {
					new EmployeeInfo().addWindowListener(new Before(Main.this));
				} else if (e.getActionCommand().equals("마이페이지")) {
					new Mypage().addWindowListener(new Before(Main.this));
				} else if (e.getActionCommand().equals("닫기")) {
					System.exit(0);
				}
			});
		}

		sn.add(lbl1("지역", 0, 20));
		sn.add(com = new JComboBox<String>(
				new DefaultComboBoxModel<String>("전체,서울,부산,대구,인천,광주,대전,울산,세종,경기,강원,충북,충남,전북,전남,경북,경남,제주".split(","))));

		com.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				animation();
			}
		});

		animation();

		sz(com, 120, 30);
		cc.setBorder(new EmptyBorder(0, 20, 0, 20));
		this.setVisible(true);
	}

	void search() {
		if (txt.getText().isEmpty()) {
			eMsg("검색할 기업명을 입력하세요.");
			return;
		}

		try {
			var rs = stmt
					.executeQuery("select * from company where c_name like '%" + txt.getText() + "%' order by c_no");
			while (rs.next()) {
				noList.add(rs.getInt(1));
			}

			rs.last();
			if (rs.getRow() == 0) {
				eMsg("검색한 기업이 없습니다");
				txt.setText("");
				txt.requestFocus();
				return;
			}

			execute("update company set c_search = c_search + 1 where c_no = " + noList.get(0));
			famous();
			new CompanyDetail(noList.get(0) + "").addWindowListener(new Before(Main.this));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void famous() {
		csub1.removeAll();
		csub1.setLayout(new GridLayout(0, 1));

		try {
			var rs = stmt.executeQuery("select * from company group by c_no order by c_search desc, c_no limit 5");
			int i = 1;
			while (rs.next()) {
				var tmp = new JPanel(new BorderLayout());
				tmp.add(lbl1(i + "", 0, 15), "West");
				tmp.add(lbl1(rs.getString("c_name"), 0, 15));
				tmp.add(lbl1(rs.getInt("c_search") + "", 0, 15), "East");
				i++;

				csub1.add(tmp);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void animation() {
		sc.removeAll();
		imglist.clear();

		var loc = com.getSelectedItem().toString().equals("전체") ? "" : com.getSelectedItem().toString();

		if (timer != null)
			timer.stop();

		try {
			var rs = stmt
					.executeQuery("select c_name, c_img from company where left(c_address, 2) like '%" + loc + "%'");
			int i = 0;
			while (rs.next()) {
				if (rs.getRow() == 0) {
					eMsg("선택한 기업정보가 없습니다.");
					com.setSelectedIndex(0);
					animation();
					return;
				}

				var tmp = sz(new JPanel(new BorderLayout()), 100, 100);
				tmp.add(new JLabel(new ImageIcon(Toolkit.getDefaultToolkit()
						.createImage(rs.getBlob(2).getBinaryStream().readAllBytes()).getScaledInstance(100, 80, 4))));
				tmp.add(lbl1(rs.getString(1), 0, 13), "South");

				sc.add(tmp);
				imglist.add(tmp);

				tmp.setBounds(i * 100, 0, 100, 100);
				tmp.setBorder(new LineBorder(Color.BLACK));
				i++;
			}

			int len = imglist.stream().mapToInt(x -> x.getWidth()).sum() - 100;

			timer = new Timer(1, e -> {
				for (int j = 0; j < imglist.size(); j++) {
					imglist.get(j).setBounds(imglist.get(j).getX() - 1, 0, 100, 100);
					if (imglist.get(j).getX() <= -100) {
						imglist.get(j).setBounds(len, 0, 100, 100);
					}

					repaint();
					revalidate();
				}
			});

			timer.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void logout() {
		String tmp[] = "로그인,회원가입,닫기".split(",");

		img.setBorder(null);
		img.setIcon(null);
		name.setText("");
		btn[3].setVisible(false);

		for (int i = 0; i < tmp.length; i++) {
			btn[i].setText(tmp[i]);
		}
	}

	void login(Object pic) {
		String tmp[] = "로그아웃,채용정보,마이페이지,닫기".split(",");

		img.setBorder(new LineBorder(Color.BLACK));
		img.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage((byte[]) pic).getScaledInstance(30, 30, 4)));
		name.setText(getone("select u_name from user where u_no=" + uno) + "님 환영합니다.");
		btn[3].setVisible(true);
		for (int i = 0; i < tmp.length; i++) {
			btn[i].setText(tmp[i]);
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}
