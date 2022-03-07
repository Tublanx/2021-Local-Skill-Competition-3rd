import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.util.Arrays;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class EmployeeSelection extends Baseframe {

	JCheckBox chk[] = new JCheckBox[8];
	JTextField txt;

	String cate = "";

	public EmployeeSelection(EmployeeInfo employeeInfo) {
		super("직종선택", 300, 370);

		this.add(n = new JPanel(new GridLayout(4, 2)), "North");
		this.add(c = new JPanel());
		this.add(s = new JPanel(), "South");

		for (int i = 0; i < chk.length; i++) {
			chk[i] = new JCheckBox(category[i + 1]);
			n.add(chk[i]);

			chk[i].addItemListener(e -> {
				txt.setEnabled(true);

				for (int j = 0; j < chk.length; j++) {
					if (chk[j].isSelected()) {
						txt.setEnabled(false);
					}
				}

				cate = String.join(",",
						Arrays.stream(chk).filter(x -> x.isSelected()).map(x -> x.getText()).toArray(String[]::new));
				txt.setText(cate);
			});
		}

		c.add(lbl1("선택직종명", 0, 15));
		c.add(txt = new JTextField(15));

		s.add(btn("선택", e -> {
			if (txt.getText().isEmpty()) {
				eMsg("직종을 선택하세요.");
				return;
			}

			employeeInfo.txt2.setText(cate);
			dispose();
		}));

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				employeeInfo.txt2.setText("");
			};
		});

		n.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "직종선택", TitledBorder.LEFT, TitledBorder.TOP,
				new Font("", Font.BOLD, 25)));
		sz(n, 280, 250);

		this.setVisible(true);
	}
}
