package guiApp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

import lejos.pc.comm.NXTConnector;
import controller.RoboShopViewerApp;

public class MainPanel extends JFrame {

	public void launcher() {
		login login = new login();
		login.launchFrame();
	}

	public static void main(String[] args) {
		MainPanel mp = new MainPanel();
		mp.launcher();
	}

	public Connection getConnection(Connection con) {

		String url = "jdbc:oracle:thin:@localhost:1521:XE";
		String driver = "oracle.jdbc.driver.OracleDriver";
		String user = "fyp";
		String pwd = "mUSh5909";
		String url_live = "jdbc:oracle:thin:@172.16.60.13:1521:orcl";
		String user_live = "A164120";
		String pwd_live = "164120";
		// Connection con = null;
		// Connection con_live = null;
		try {
			int i = 0;

			Class.forName(driver);

			switch (i) {
			default:
				con = DriverManager.getConnection(url, user, pwd);
				System.out.println("Localhost connected.");
				return con;
			case 1:
				con = DriverManager
						.getConnection(url_live, user_live, pwd_live);
				System.out.println("Oracle FSKTM connected.");
				return con;
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	public static NXTConnector getNXTConnect(NXTConnector NXTCon) {
		NXTCon = new NXTConnector();

		int j = 0;
		switch (j) {
		case 1:
			NXTCon.connectTo("usb://");
			return NXTCon;
		default:
			NXTCon.connectTo("btspp://");
			return NXTCon;
		}

	}// end of getNXTConnect

	private ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	class login extends JFrame implements KeyListener, ActionListener {
		private JFrame loginFrame;
		private JLabel id, id_pwd, header;
		private JTextField id_tf;
		private JPasswordField pwd_tf;
		private JButton login_btn, resetP;
		private Connection con;
		private JPanel headerP, bodyP;

		public login() {
			loginFrame = new JFrame("ROBOSHOP - MAIN");
			loginFrame.getContentPane().setLayout(new BorderLayout(2, 1));
			headerP = new JPanel();
			headerP.setLayout(new FlowLayout());

			bodyP = new JPanel();
			bodyP.setLayout(new FlowLayout());

			header = new JLabel();
			header.setAlignmentX(Component.CENTER_ALIGNMENT);

			ImageIcon headerico = createImageIcon("img/icon-robot-shop.png", "");
			header.setIcon(headerico);

			id = new JLabel("Email : ");
			id_tf = new JTextField(10);
			id_pwd = new JLabel("Password : ");
			pwd_tf = new JPasswordField(10);
			login_btn = new JButton("Login");
			resetP = new JButton("Reset Password");
			resetP.setEnabled(false);

			headerP.add(header);
			bodyP.add(id);
			bodyP.add(id_tf);
			bodyP.add(id_pwd);
			bodyP.add(pwd_tf);
			bodyP.add(login_btn);
			bodyP.add(resetP);
		}

		public void launchFrame() {
			loginFrame.setPreferredSize(new Dimension(600, 200));
			;
			loginFrame.getContentPane().add(headerP);
			loginFrame.getContentPane().add(bodyP, BorderLayout.SOUTH);
			loginFrame.pack();
			loginFrame.setResizable(false);

			Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension framesize = loginFrame.getSize();
			loginFrame.setLocation(((screensize.width - framesize.width) / 2),
					((screensize.height - framesize.height) / 2));

			loginFrame.setLocationRelativeTo(null);
			loginFrame.setVisible(true);
			loginFrame.getRootPane().setDefaultButton(login_btn);
			login_btn.addKeyListener(this);
			login_btn.addActionListener(this);
			resetP.addActionListener(this);
			loginFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			loginFrame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					int confirmed = JOptionPane.showConfirmDialog(null,
							"Are you sure want to exit the program?",
							"Exit Program", JOptionPane.YES_NO_OPTION);
					if (confirmed == JOptionPane.YES_OPTION) {
						System.exit(0);
					}
				}
			});
		}

		public void keyTyped(KeyEvent e) {

		}

		public void keyReleased(KeyEvent e) {

		}

		public void keyPressed(KeyEvent e) {

			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				String id_str = id_tf.getText();
				char[] pwd_char = pwd_tf.getPassword();
				String pwd_str = new String(pwd_char);

				try {
					con = getConnection(con);
					String sql = "select user_name,role_id from roboshop_user where user_email=? and user_password=?";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setString(1, id_str);
					ps.setString(2, pwd_str);
					ResultSet rs = ps.executeQuery();

					if (rs.next()) {

						int role_id = rs.getInt("role_id");
						if (role_id == 1) {
							JOptionPane.showMessageDialog(null, "Welcome" + " "
									+ rs.getString("user_name") + "!");
							loginFrame.setVisible(false);

							Admin_Panel ap = new Admin_Panel();
							ap.launchFrame();
						} else {
							JOptionPane.showMessageDialog(null, "Welcome" + " "
									+ rs.getString("user_name") + "!");
							loginFrame.setVisible(false);
							CashierPanel cp = new CashierPanel();
							cp.launchFrame();
						}

					} else {
						JOptionPane.showMessageDialog(null,
								"Incorrect ID or password");
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		public void actionPerformed(ActionEvent ae) {

			if (ae.getSource().equals(login_btn)) {
				String id_str = id_tf.getText();
				char[] pwd_char = pwd_tf.getPassword();
				String pwd_str = new String(pwd_char);

				try {
					con = getConnection(con);
					String sql = "select user_name,role_id from roboshop_user where user_email=? and user_password=?";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setString(1, id_str);
					ps.setString(2, pwd_str);
					ResultSet rs = ps.executeQuery();

					if (rs.next()) {

						int role_id = rs.getInt("role_id");
						if (role_id == 1) {
							JOptionPane.showMessageDialog(null, "Welcome" + " "
									+ rs.getString("user_name") + "!");
							loginFrame.setVisible(false);

							Admin_Panel ap = new Admin_Panel();
							ap.launchFrame();
						} else {
							JOptionPane.showMessageDialog(null, "Welcome" + " "
									+ rs.getString("user_name") + "!");
							loginFrame.setVisible(false);
							CashierPanel cp = new CashierPanel();
							cp.launchFrame();
						}

					} else {
						JOptionPane.showMessageDialog(null,
								"Incorrect ID or password");
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (ae.getSource() == resetP) {
				loginFrame.setVisible(false);
				reset_password rs = new reset_password();
				rs.launchFrame();
			}
		}// end of actionPerformed

	}// end of class login

	class reset_password extends JFrame implements ActionListener {
		private JFrame resetFrame;
		private JLabel id, id_pwd;
		private JTextField id_tf;
		private JPasswordField pwd_tf;
		private JButton resetP;
		private Connection con;

		public reset_password() {
			resetFrame = new JFrame("Roboshop - Reset Password");
			id = new JLabel("Email : ");
			id_tf = new JTextField(10);
			id_pwd = new JLabel("Password : ");
			pwd_tf = new JPasswordField(10);
			resetP = new JButton("Reset Password");

		}// end of reset_password

		public void launchFrame() {
			resetFrame.setSize(1366, 720);
			resetFrame.getContentPane().setLayout(
					new FlowLayout(FlowLayout.CENTER, 20, 20));
			resetFrame.getContentPane().add(id);
			resetFrame.getContentPane().add(id_tf);
			resetFrame.getContentPane().add(id_pwd);
			resetFrame.getContentPane().add(pwd_tf);
			resetFrame.getContentPane().add(resetP);
			resetFrame.pack();

			Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension framesize = resetFrame.getSize();
			resetFrame.setLocation(((screensize.width - framesize.width) / 2),
					((screensize.height - framesize.height) / 2));

			resetFrame.setLocationRelativeTo(null);
			resetFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			resetFrame.setVisible(true);
			resetFrame.getRootPane().setDefaultButton(resetP);
			resetP.addActionListener(this);
		}// end of launchframe

		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() == resetP) {
				String id_str = id_tf.getText();
				char[] pwd_char = pwd_tf.getPassword();
				String pwd_str = new String(pwd_char);

				try {
					con = getConnection(con);
					String sql = "select user_name from roboshop_user where user_name=?";
					PreparedStatement ps = con.prepareStatement(sql);
					ResultSet rs = ps.executeQuery();

					if (rs.next()) {
						String name = rs.getString("user_name");
						if (id_str == name) {
							try {
								String sql2 = "update roboshop_user set user_password=?";
								PreparedStatement ps2 = con
										.prepareStatement(sql2);
								ps2.setString(1, pwd_str);
								int update = ps2.executeUpdate();

								if (update > 0) {
									JOptionPane.showMessageDialog(null,
											"Password has been update");
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}// end if
		}// end of actionPerformed

	}// end of class reset_password

	class Admin_Panel extends JFrame implements ActionListener {

		private JFrame adminFrame;
		private JButton add_staff, view_staff, modify_staff, add_stock,
				view_stock, remove_stock, logout;
		private Font font1 = new Font("Arial", Font.BOLD, 20);

		public Admin_Panel() {
			adminFrame = new JFrame("ROBOSHOP - Admin Panel");
			add_staff = new JButton("Add New Staff");
			view_staff = new JButton("View Staff");
			modify_staff = new JButton("Modify Staff");
			add_stock = new JButton("Add New Stock");
			view_stock = new JButton("View Stock");
			remove_stock = new JButton("Modify Stock");
			logout = new JButton("Logout");
			add_staff.setFont(font1);
			view_staff.setFont(font1);
			modify_staff.setFont(font1);
			add_stock.setFont(font1);
			view_stock.setFont(font1);
			remove_stock.setFont(font1);
			logout.setFont(font1);

			add_staff.setBounds(250, 100, 300, 100);
			view_staff.setBounds(250, 220, 300, 100);
			modify_staff.setBounds(250, 340, 300, 100);
			add_stock.setBounds(750, 100, 300, 100);
			view_stock.setBounds(750, 220, 300, 100);
			remove_stock.setBounds(750, 340, 300, 100);
			logout.setBounds(500, 500, 300, 100);

		}

		public void launchFrame() {
			adminFrame.setSize(1366, 720);
			adminFrame.setLayout(null);
			adminFrame.getContentPane().add(add_staff);
			adminFrame.getContentPane().add(view_staff);
			adminFrame.getContentPane().add(modify_staff);
			adminFrame.getContentPane().add(add_stock);
			adminFrame.getContentPane().add(view_stock);
			adminFrame.getContentPane().add(remove_stock);
			adminFrame.getContentPane().add(logout);

			// adminFrame.pack();

			Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension framesize = adminFrame.getSize();
			adminFrame.setLocation(((screensize.width - framesize.width) / 2),
					((screensize.height - framesize.height) / 2));

			adminFrame.setLocationRelativeTo(null);
			adminFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			adminFrame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					int confirmed = JOptionPane.showConfirmDialog(null,
							"Are you sure want to exit the program?",
							"Exit Program", JOptionPane.YES_NO_OPTION);
					if (confirmed == JOptionPane.YES_OPTION) {
						System.exit(0);
					}
				}
			});
			adminFrame.setVisible(true);

			add_staff.addActionListener(this);
			view_staff.addActionListener(this);
			modify_staff.addActionListener(this);

			add_stock.addActionListener(this);
			view_stock.addActionListener(this);
			remove_stock.addActionListener(this);
			logout.addActionListener(this);
		}

		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource().equals(add_staff)) {
				adminFrame.setVisible(false);
				add_staff_panel asp = new add_staff_panel();
				asp.launchFrame();
			} else if (ae.getSource().equals(view_staff)) {
				adminFrame.setVisible(false);
				view_staff_panel vsp = new view_staff_panel();
				vsp.launchFrame();
			} else if (ae.getSource().equals(modify_staff)) {
				adminFrame.setVisible(false);
				modify_staff_panel msp = new modify_staff_panel();
				msp.launchFrame();
			} else if (ae.getSource().equals(add_stock)) {
				adminFrame.setVisible(false);
				add_stock_panel asp2 = new add_stock_panel();
				asp2.launchFrame();
			} else if (ae.getSource().equals(view_stock)) {
				adminFrame.setVisible(false);
				view_stock_panel vsp2 = new view_stock_panel();
				vsp2.launchFrame();
			} else if (ae.getSource().equals(remove_stock)) {
				adminFrame.setVisible(false);
				modify_stock_panel msp2 = new modify_stock_panel();
				msp2.launchFrame();
			} else if (ae.getSource().equals(logout)) {
				adminFrame.setVisible(false);
				MainPanel mp = new MainPanel();
				mp.launcher();
			}
		}// end of actionPerformed

	}// end of class admin_panel

	class add_staff_panel extends JFrame implements ActionListener {

		private JFrame addStaffFrame;
		private JLabel staff_name, staff_email, staff_phone, staff_ic,
				staff_password, staff_compare_password, staff_role;
		private JTextField tf1, tf2, tf3, tf4;
		private JPasswordField pf1, pf2;
		private JComboBox role;
		private JButton register, back;
		private Font font1 = new Font("Arial", Font.BOLD, 25);
		private Connection con;

		public add_staff_panel() {
			addStaffFrame = new JFrame("ROBOSHOP - Add New Staff");

			staff_name = new JLabel("Full Name : ");
			staff_name.setBounds(20, 50, 100, 25);

			tf1 = new JTextField(20);
			tf1.setBounds(130, 50, 160, 25);

			staff_email = new JLabel("Email : ");
			staff_email.setBounds(30, 100, 100, 25);

			tf2 = new JTextField(20);
			tf2.setBounds(130, 100, 160, 25);

			staff_phone = new JLabel("Phone No : ");
			staff_phone.setBounds(30, 150, 100, 25);

			tf3 = new JTextField(20);
			tf3.setBounds(130, 150, 160, 25);

			staff_ic = new JLabel("IC NO : ");
			staff_ic.setBounds(30, 200, 100, 25);

			tf4 = new JTextField(20);
			tf4.setBounds(130, 200, 160, 25);

			staff_password = new JLabel("Password : ");
			staff_password.setBounds(30, 250, 160, 25);

			pf1 = new JPasswordField(20);
			pf1.setBounds(200, 250, 160, 25);

			staff_compare_password = new JLabel("Confirm Password : ");
			staff_compare_password.setBounds(30, 300, 160, 25);

			pf2 = new JPasswordField(20);
			pf2.setBounds(200, 300, 160, 25);

			staff_role = new JLabel("Role : ");
			staff_role.setBounds(30, 350, 100, 25);

			String[] role_list = { "", "Admin", "Staff" };
			role = new JComboBox(role_list);
			role.setSelectedIndex(0);
			role.setBounds(130, 350, 160, 25);

			register = new JButton("Register");
			register.setBounds(100, 400, 200, 60);
			register.setFont(font1);

			back = new JButton("Back");
			back.setBounds(310, 400, 200, 60);
			back.setFont(font1);

		}

		public void launchFrame() {

			addStaffFrame.setSize(1366, 720);
			addStaffFrame.getContentPane().add(staff_name);
			addStaffFrame.getContentPane().add(tf1);
			addStaffFrame.getContentPane().add(staff_email);
			addStaffFrame.getContentPane().add(tf2);
			addStaffFrame.getContentPane().add(staff_phone);
			addStaffFrame.getContentPane().add(tf3);
			addStaffFrame.getContentPane().add(staff_ic);
			addStaffFrame.getContentPane().add(tf4);
			addStaffFrame.getContentPane().add(staff_password);
			addStaffFrame.getContentPane().add(pf1);
			addStaffFrame.getContentPane().add(staff_compare_password);
			addStaffFrame.getContentPane().add(pf2);
			addStaffFrame.getContentPane().add(staff_role);
			addStaffFrame.getContentPane().add(role);
			addStaffFrame.getContentPane().add(register);
			addStaffFrame.getContentPane().add(back);

			addStaffFrame.setLocationRelativeTo(null);
			addStaffFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			addStaffFrame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					int confirmed = JOptionPane.showConfirmDialog(null,
							"Are you sure want to exit the program?",
							"Exit Program", JOptionPane.YES_NO_OPTION);
					if (confirmed == JOptionPane.YES_OPTION) {
						System.exit(0);
					}
				}
			});
			addStaffFrame.setLayout(null);
			addStaffFrame.setVisible(true);
			addStaffFrame.getRootPane().setDefaultButton(register);

			register.addActionListener(this);
			back.addActionListener(this);

		}

		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource().equals(register)) {
				String name_str = tf1.getText();
				String email_str = tf2.getText();
				String phone_str = tf3.getText();
				String ic_str = tf4.getText();
				char[] pwds_char = pf1.getPassword();
				String pwds_str = new String(pwds_char);
				char[] pwds_char2 = pf2.getPassword();
				String pwds_str2 = new String(pwds_char2);
				int role_int = role.getSelectedIndex();

				if (pwds_str.equals(pwds_str2)) {
					try {
						int x = 0;
						con = getConnection(con);

						String sql = "insert into roboshop_user values('',?,?,?,?,?,?)";
						PreparedStatement ps = con.prepareStatement(sql);
						ps.setString(1, name_str);
						ps.setString(2, email_str);
						ps.setString(3, phone_str);
						ps.setString(4, ic_str);
						ps.setString(5, pwds_str);
						ps.setInt(6, role_int);
						ResultSet rs = ps.executeQuery();
						x++;
						if (x > 0) {
							JOptionPane.showMessageDialog(register, name_str
									+ " " + "successfully registered");
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					JOptionPane.showMessageDialog(null,
							"Password does not match");
				}// end if compare password

			} else if (ae.getSource().equals(back)) {
				addStaffFrame.setVisible(false);
				Admin_Panel ap = new Admin_Panel();
				ap.launchFrame();
			}// end if e.getSource() == register

		}// end of void actionPerformed()
	}// end of add_stock_panel class

	class view_staff_panel extends JFrame implements ActionListener {
		private JTable table;
		private JFrame viewStaffFrame;
		private ArrayList columnName, viewData, row, subArray;
		private Vector columnNameVector, dataVector, subVector;
		private JButton back;
		private JScrollPane jsp;
		private DefaultTableCellRenderer center;
		private Connection con;

		public view_staff_panel() {
			viewStaffFrame = new JFrame("ROBOSHOP - View Staff");

			back = new JButton("Back");

			columnName = new ArrayList();
			viewData = new ArrayList();

			try {
				con = getConnection(con);
				String sql = "select u.user_id, u.user_name, u.user_email, u.user_phone_no, u.user_ic_no, r.user_role_name from roboshop_user u join roboshop_user_role r on u.role_id=r.user_role_id";
				Statement ps = con.createStatement();
				ResultSet rs = ps.executeQuery(sql);
				ResultSetMetaData rsmd = rs.getMetaData();

				int count = rsmd.getColumnCount();
				for (int i = 1; i <= count; i++) {
					columnName.add(rsmd.getColumnName(i));
				}
				while (rs.next()) {
					row = new ArrayList(count);
					for (int i = 1; i <= count; i++) {
						row.add(rs.getObject(i));
					}
					viewData.add(row);
				}

				columnNameVector = new Vector();
				dataVector = new Vector();

				for (int i = 0; i < viewData.size(); i++) {
					subArray = (ArrayList) viewData.get(i);
					subVector = new Vector();
					for (int j = 0; j < subArray.size(); j++) {
						subVector.add(subArray.get(j));
					}
					dataVector.add(subVector);
				}

				columnNameVector.addElement("ID");
				columnNameVector.addElement("Name");
				columnNameVector.addElement("Email");
				columnNameVector.addElement("Phone No");
				columnNameVector.addElement("IC No");
				columnNameVector.addElement("Role");

				table = new JTable(dataVector, columnNameVector) {
					public Class getColumnClass(int column) {
						for (int row = 0; row < getRowCount(); row++) {
							Object o = getValueAt(row, column);
							if (o != null) {
								return o.getClass();
							}
						}
						return Object.class;
					}
				};

			} catch (Exception e) {
				e.printStackTrace();
			}// end of try catch

			center = new DefaultTableCellRenderer();
			center.setHorizontalAlignment(JLabel.CENTER);
			for (int i = 0; i < columnName.size(); i++) {
				table.getColumnModel().getColumn(i).setCellRenderer(center);
			}
			jsp = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
					JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			jsp.setBounds(1, 1, 1350, 600);
			back.setBounds(610, 630, 160, 25);

		}

		public void launchFrame() {
			viewStaffFrame.setSize(1366, 720);
			viewStaffFrame.setLayout(null);
			viewStaffFrame.getContentPane().add(jsp);
			viewStaffFrame.getContentPane().add(back);

			Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension framesize = viewStaffFrame.getSize();
			viewStaffFrame.setLocation(
					((screensize.width - framesize.width) / 2),
					((screensize.height - framesize.height) / 2));

			viewStaffFrame.setLocationRelativeTo(null);
			viewStaffFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			viewStaffFrame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					int confirmed = JOptionPane.showConfirmDialog(null,
							"Are you sure want to exit the program?",
							"Exit Program", JOptionPane.YES_NO_OPTION);
					if (confirmed == JOptionPane.YES_OPTION) {
						System.exit(0);
					}
				}
			});
			viewStaffFrame.setVisible(true);

			back.addActionListener(this);
		}

		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() == back) {
				viewStaffFrame.setVisible(false);
				Admin_Panel ap = new Admin_Panel();
				ap.launchFrame();
			}
		}

	}// end of view_staff_panel

	class modify_staff_panel extends JFrame implements ActionListener {
		private JLabel staff_name, staff_email, staff_phone, staff_ic,
				staff_id;
		private JTextField tf1, tf2, tf3, tf4;
		private JButton updateStaff, deleteStaff, back;
		private JFrame modifyStaffFrame;
		private Connection con;
		private JComboBox idCB;

		public modify_staff_panel() {
			modifyStaffFrame = new JFrame("ROBOSHOP - Modify Staff");

			staff_name = new JLabel("Name : ");
			staff_name.setBounds(30, 50, 100, 25);
			tf1 = new JTextField();
			tf1.setBounds(130, 50, 160, 25);

			staff_email = new JLabel("Email : ");
			staff_email.setBounds(30, 100, 100, 25);
			tf2 = new JTextField();
			tf2.setBounds(130, 100, 160, 25);

			staff_phone = new JLabel("Phone No : ");
			staff_phone.setBounds(30, 150, 100, 25);
			tf3 = new JTextField();
			tf3.setBounds(130, 150, 160, 25);

			staff_ic = new JLabel("IC No : ");
			staff_ic.setBounds(30, 200, 100, 25);
			tf4 = new JTextField();
			tf4.setBounds(130, 200, 160, 25);

			updateStaff = new JButton("Update");
			updateStaff.setBounds(200, 300, 160, 25);
			deleteStaff = new JButton("Delete");
			deleteStaff.setBounds(370, 300, 160, 25);
			back = new JButton("Back");
			back.setBounds(540, 300, 160, 25);

			staff_id = new JLabel("Staff ID : ");
			staff_id.setBounds(400, 80, 160, 25);
			idCB = new JComboBox();
			idCB.addItem(" ");
			idCB.setSelectedIndex(0);
			idCB.setBounds(400, 100, 160, 25);

			try {

				con = getConnection(con);

				String sql = "select user_id from roboshop_user";
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					int id = rs.getInt("user_id");
					idCB.addItem(id);

				}

			} catch (Exception e) {
				e.printStackTrace();
				;
			}// end of try catch

		}// end of remove_staff_panel

		public void launchFrame() {
			modifyStaffFrame.setSize(1366, 720);
			modifyStaffFrame.getContentPane().add(staff_id);
			modifyStaffFrame.getContentPane().add(idCB);
			modifyStaffFrame.getContentPane().add(staff_name);
			modifyStaffFrame.getContentPane().add(tf1);
			modifyStaffFrame.getContentPane().add(staff_email);
			modifyStaffFrame.getContentPane().add(tf2);
			modifyStaffFrame.getContentPane().add(staff_phone);
			modifyStaffFrame.getContentPane().add(tf3);
			modifyStaffFrame.getContentPane().add(staff_ic);
			modifyStaffFrame.getContentPane().add(tf4);
			modifyStaffFrame.getContentPane().add(updateStaff);
			modifyStaffFrame.getContentPane().add(deleteStaff);
			modifyStaffFrame.getContentPane().add(back);

			modifyStaffFrame.setLocationRelativeTo(null);
			modifyStaffFrame
					.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			modifyStaffFrame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					int confirmed = JOptionPane.showConfirmDialog(null,
							"Are you sure want to exit the program?",
							"Exit Program", JOptionPane.YES_NO_OPTION);
					if (confirmed == JOptionPane.YES_OPTION) {
						System.exit(0);
					}
				}
			});
			modifyStaffFrame.setLayout(null);
			modifyStaffFrame.setVisible(true);

			updateStaff.addActionListener(this);
			deleteStaff.addActionListener(this);
			back.addActionListener(this);
			idCB.addActionListener(this);

		}// end of launchFrame

		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource().equals(idCB)) {

				try {
					int ids = (int) idCB.getSelectedItem();

					con = getConnection(con);
					String sql = "Select * from roboshop_user where user_id=?";
					PreparedStatement st = con.prepareStatement(sql);
					st.setInt(1, ids);
					ResultSet rs = st.executeQuery();

					while (rs.next()) {

						String name = rs.getString("user_name");
						String email = rs.getString("user_email");
						String phone = rs.getString("user_phone_no");
						String ic = rs.getString("user_ic_no");

						tf1.setText(name);

						tf2.setText(email);

						tf3.setText(phone);

						tf4.setText(ic);

					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (ae.getSource().equals(updateStaff)) {
				try {

					int ids = (int) idCB.getSelectedItem();
					String name_str = tf1.getText();
					String email_str = tf2.getText();
					String phone_str = tf3.getText();
					String ic_str = tf4.getText();
					con = getConnection(con);
					String sql = "update roboshop_user set user_name=?, user_email=?, user_phone_no=?, user_ic_no=? where user_id="
							+ ids + " ";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setString(1, name_str);
					ps.setString(2, email_str);
					ps.setString(3, phone_str);
					ps.setString(4, ic_str);
					int update = ps.executeUpdate();

					if (update > 0) {
						JOptionPane.showMessageDialog(null, name_str
								+ "has been updated!");
						idCB.repaint();
						idCB.revalidate();
					} else {
						JOptionPane.showMessageDialog(null, "Error on update");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (ae.getSource().equals(deleteStaff)) {
				try {

					int ids = (int) idCB.getSelectedItem();
					String name_str = tf1.getText();
					con = getConnection(con);
					String sql = "delete from roboshop_user where user_id="
							+ ids + " ";
					PreparedStatement ps = con.prepareStatement(sql);
					int delete = ps.executeUpdate();

					if (delete > 0) {

						if (JOptionPane.showConfirmDialog(null, "User: "
								+ name_str + " " + "with ID: " + ids + " "
								+ "will be removed!", "Warning!",
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
							idCB.repaint();
							idCB.revalidate();

						}// end if
					} else {
						JOptionPane
								.showMessageDialog(null, "Unable to delete!");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (ae.getSource().equals(back)) {
				modifyStaffFrame.setVisible(false);
				Admin_Panel ap = new Admin_Panel();
				ap.launchFrame();
			}// end if

		}// end of actionPerformed

	}// end of remove_staff_panel

	class add_stock_panel extends JFrame implements ActionListener {
		private JFrame addStockFrame;
		private JLabel stock_name, stock_price, stock_x_coordinate,
				stock_y_coordinate, stock_quantity;
		private JTextField tf1, tf2, tf3, tf4, tf5;
		private JButton addStock, back;
		private Font font1 = new Font("Arial", Font.BOLD, 25);
		private Connection con;

		public add_stock_panel() {
			addStockFrame = new JFrame("ROBOSHOP - Add New Stock");

			stock_name = new JLabel("Product Name : ");
			stock_price = new JLabel("Product Unit Price : ");
			stock_quantity = new JLabel("Quantity : ");
			stock_x_coordinate = new JLabel("X Coordinate : ");
			stock_y_coordinate = new JLabel("Y Coordinate : ");

			tf1 = new JTextField();
			tf2 = new JTextField();
			tf3 = new JTextField();
			tf4 = new JTextField();
			tf5 = new JTextField();

			addStock = new JButton("Add Stock");
			back = new JButton("Back");

			stock_name.setFont(font1);
			stock_price.setFont(font1);
			stock_quantity.setFont(font1);
			stock_x_coordinate.setFont(font1);
			stock_y_coordinate.setFont(font1);
			addStock.setFont(font1);
			back.setFont(font1);

			stock_name.setBounds(300, 50, 300, 30);
			tf1.setBounds(550, 50, 300, 30);
			stock_price.setBounds(300, 100, 300, 30);
			tf2.setBounds(550, 100, 300, 30);
			stock_quantity.setBounds(300, 150, 300, 30);
			tf3.setBounds(550, 150, 300, 30);
			stock_x_coordinate.setBounds(300, 200, 300, 30);
			tf4.setBounds(550, 200, 300, 30);
			stock_y_coordinate.setBounds(300, 250, 300, 30);
			tf5.setBounds(550, 250, 300, 30);
			addStock.setBounds(480, 300, 200, 50);
			back.setBounds(700, 300, 150, 50);

		}// end of public add_stock_panel

		public void launchFrame() {
			addStockFrame.setSize(1366, 720);
			addStockFrame.setLayout(null);
			addStockFrame.getContentPane().add(stock_name);
			addStockFrame.getContentPane().add(tf1);
			addStockFrame.getContentPane().add(stock_price);
			addStockFrame.getContentPane().add(tf2);
			addStockFrame.getContentPane().add(stock_quantity);
			addStockFrame.getContentPane().add(tf3);
			addStockFrame.getContentPane().add(stock_x_coordinate);
			addStockFrame.getContentPane().add(tf4);
			addStockFrame.getContentPane().add(stock_y_coordinate);
			addStockFrame.getContentPane().add(tf5);
			addStockFrame.getContentPane().add(addStock);
			addStockFrame.getContentPane().add(back);

			Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension framesize = addStockFrame.getSize();
			addStockFrame.setLocation(
					((screensize.width - framesize.width) / 2),
					((screensize.height - framesize.height) / 2));

			addStockFrame.setLocationRelativeTo(null);
			addStockFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			addStockFrame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					int confirmed = JOptionPane.showConfirmDialog(null,
							"Are you sure want to exit the program?",
							"Exit Program", JOptionPane.YES_NO_OPTION);
					if (confirmed == JOptionPane.YES_OPTION) {
						System.exit(0);
					}
				}
			});
			addStockFrame.setVisible(true);
			addStockFrame.getRootPane().setDefaultButton(addStock);

			addStock.addActionListener(this);
			back.addActionListener(this);
		}// end of launchFrame

		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource().equals(addStock)) {
				try {
					String p_name = tf1.getText();
					String priceSTR = tf2.getText();
					int p_price = Integer.parseInt(priceSTR);
					String quantitySTR = tf3.getText();
					int p_quantity = Integer.parseInt(quantitySTR);
					String x_val_str = tf4.getText();
					int x_val = Integer.parseInt(x_val_str);
					String y_val_str = tf5.getText();
					int y_val = Integer.parseInt(y_val_str);

					int x = 0;
					con = getConnection(con);

					String sql = "insert into roboshop_product values('',?,?,?,?,?)";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setString(1, p_name);
					ps.setInt(2, p_price);
					ps.setInt(3, p_quantity);
					ps.setInt(4, x_val);
					ps.setInt(5, y_val);
					ResultSet rs = ps.executeQuery();
					x++;
					if (x > 0) {
						JOptionPane.showMessageDialog(addStock, p_name + " "
								+ "has been added!");
					} else {
						JOptionPane.showMessageDialog(null, "Already exist");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}// end of try catch
			} else if (ae.getSource().equals(back)) {
				addStockFrame.setVisible(false);
				Admin_Panel ap = new Admin_Panel();
				ap.launchFrame();
			}// end if
		}// end of actionPerformed

	}// end of class add_stock_panel

	class view_stock_panel extends JFrame implements ActionListener {
		private JLabel stock_name, stock_price, stock_quantity,
				stock_coordinate;
		private JTable table;
		private JFrame viewStockFrame;
		private ArrayList columnName, viewData, row, subArray;
		private Vector columnNameVector, dataVector, subVector;
		private JButton back;
		private JScrollPane jsp;
		private DefaultTableCellRenderer center;
		private Connection con;

		public view_stock_panel() {
			viewStockFrame = new JFrame("ROBOSHOP - View Products");

			stock_name = new JLabel("Name");
			stock_price = new JLabel("Unit Price");
			stock_quantity = new JLabel("Quantity");
			stock_coordinate = new JLabel("Coordinate");

			back = new JButton("Back");

			columnName = new ArrayList();
			viewData = new ArrayList();

			try {
				con = getConnection(con);
				String sql = "select product_id, product_name, product_unit_price, product_quantity, concat(concat(product_x_val,','),product_y_val) from roboshop_product";
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();

				int count = rsmd.getColumnCount();
				for (int i = 1; i <= count; i++) {
					columnName.add(rsmd.getColumnName(i));
				}
				while (rs.next()) {
					row = new ArrayList(count);
					for (int i = 1; i <= count; i++) {
						row.add(rs.getObject(i));
					}
					viewData.add(row);
				}

				columnNameVector = new Vector();
				dataVector = new Vector();

				for (int i = 0; i < viewData.size(); i++) {
					subArray = (ArrayList) viewData.get(i);
					subVector = new Vector();
					for (int j = 0; j < subArray.size(); j++) {
						subVector.add(subArray.get(j));
					}
					dataVector.add(subVector);
				}

				columnNameVector.addElement("ID");
				columnNameVector.addElement("Name");
				columnNameVector.addElement("Unit Price (RM)");
				columnNameVector.addElement("Quantity");
				columnNameVector.addElement("Coordinate");

				table = new JTable(dataVector, columnNameVector) {
					public Class getColumnClass(int column) {
						for (int row = 0; row < getRowCount(); row++) {
							Object o = getValueAt(row, column);
							if (o != null) {
								return o.getClass();
							}
						}
						return Object.class;
					}
				};

			} catch (Exception e) {
				e.printStackTrace();
			}// end of try catch

			center = new DefaultTableCellRenderer();
			center.setHorizontalAlignment(JLabel.CENTER);
			for (int i = 0; i < columnName.size(); i++) {
				table.getColumnModel().getColumn(i).setCellRenderer(center);
			}
			jsp = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
					JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			jsp.setBounds(1, 1, 1350, 600);
			back.setBounds(610, 630, 160, 25);
		}// end of public view_stock_panel

		public void launchFrame() {
			viewStockFrame.setSize(1366, 720);
			viewStockFrame.setLayout(null);
			viewStockFrame.getContentPane().add(jsp);
			viewStockFrame.getContentPane().add(back);

			Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension framesize = viewStockFrame.getSize();
			viewStockFrame.setLocation(
					((screensize.width - framesize.width) / 2),
					((screensize.height - framesize.height) / 2));

			viewStockFrame.setLocationRelativeTo(null);
			viewStockFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			viewStockFrame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					int confirmed = JOptionPane.showConfirmDialog(null,
							"Are you sure want to exit the program?",
							"Exit Program", JOptionPane.YES_NO_OPTION);
					if (confirmed == JOptionPane.YES_OPTION) {
						System.exit(0);
					}
				}
			});
			viewStockFrame.setVisible(true);

			back.addActionListener(this);
		}// endd of public void launchFrame()

		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource().equals(back)) {
				viewStockFrame.setVisible(false);
				Admin_Panel ap = new Admin_Panel();
				ap.launchFrame();
			}
		}// end of public void actionPerformed()
	}// end of class view_stock_panel

	class modify_stock_panel extends JFrame implements ActionListener {
		private JLabel stock_id, stock_name, stock_price, stock_quantity,
				x_coordinate, y_coordinate;
		private JTextField tf1, tf2, tf3, tf4, tf5;
		private JButton updateStock, deleteStock, back;
		private JFrame modifyStockFrame;
		private Connection con;
		private JComboBox idCB;

		public modify_stock_panel() {
			modifyStockFrame = new JFrame("ROBOSHOP - Modify Stock");

			stock_name = new JLabel("Product Name : ");
			stock_name.setBounds(30, 50, 160, 25);
			tf1 = new JTextField();
			tf1.setBounds(200, 50, 160, 25);

			stock_price = new JLabel("Product Unit Price(RM) : ");
			stock_price.setBounds(30, 100, 160, 25);
			tf2 = new JTextField();
			tf2.setBounds(200, 100, 160, 25);

			stock_quantity = new JLabel("Product Quantity : ");
			stock_quantity.setBounds(30, 150, 160, 25);
			tf3 = new JTextField();
			tf3.setBounds(200, 150, 160, 25);

			x_coordinate = new JLabel("X Coordinate : ");
			x_coordinate.setBounds(30, 200, 160, 25);
			tf4 = new JTextField();
			tf4.setBounds(200, 200, 160, 25);

			y_coordinate = new JLabel("Y Cooordinate : ");
			y_coordinate.setBounds(30, 250, 160, 25);
			tf5 = new JTextField();
			tf5.setBounds(200, 250, 160, 25);

			updateStock = new JButton("Update");
			updateStock.setBounds(200, 350, 160, 25);
			deleteStock = new JButton("Delete");
			deleteStock.setBounds(370, 350, 160, 25);
			back = new JButton("Back");
			back.setBounds(540, 350, 160, 25);

			stock_id = new JLabel("Product ID :");
			stock_id.setBounds(400, 80, 160, 25);
			idCB = new JComboBox();
			idCB.addItem(" ");
			idCB.setSelectedIndex(0);
			idCB.setBounds(400, 100, 160, 25);

			try {

				con = getConnection(con);

				String sql = "select product_id from roboshop_product";
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					int id = rs.getInt("product_id");
					idCB.addItem(id);

				}

			} catch (Exception e) {
				e.printStackTrace();
				;
			}// end of try catch

		}// end of remove_stock_panel

		public void launchFrame() {
			modifyStockFrame.setSize(1366, 720);
			modifyStockFrame.getContentPane().add(stock_id);
			modifyStockFrame.getContentPane().add(idCB);
			modifyStockFrame.getContentPane().add(stock_name);
			modifyStockFrame.getContentPane().add(tf1);
			modifyStockFrame.getContentPane().add(stock_price);
			modifyStockFrame.getContentPane().add(tf2);
			modifyStockFrame.getContentPane().add(stock_quantity);
			modifyStockFrame.getContentPane().add(tf3);
			modifyStockFrame.getContentPane().add(x_coordinate);
			modifyStockFrame.getContentPane().add(tf4);
			modifyStockFrame.getContentPane().add(y_coordinate);
			modifyStockFrame.getContentPane().add(tf5);
			modifyStockFrame.getContentPane().add(updateStock);
			modifyStockFrame.getContentPane().add(deleteStock);
			modifyStockFrame.getContentPane().add(back);

			modifyStockFrame.setLocationRelativeTo(null);
			modifyStockFrame
					.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			modifyStockFrame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					int confirmed = JOptionPane.showConfirmDialog(null,
							"Are you sure want to exit the program?",
							"Exit Program", JOptionPane.YES_NO_OPTION);
					if (confirmed == JOptionPane.YES_OPTION) {
						System.exit(0);
					}
				}
			});
			modifyStockFrame.setLayout(null);
			modifyStockFrame.setVisible(true);

			updateStock.addActionListener(this);
			deleteStock.addActionListener(this);
			back.addActionListener(this);
			idCB.addActionListener(this);

		}// end off launchFrame

		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource().equals(idCB)) {

				try {
					int ids = (int) idCB.getSelectedItem();

					con = getConnection(con);
					String sql = "Select * from roboshop_product where product_id=?";
					PreparedStatement st = con.prepareStatement(sql);
					st.setInt(1, ids);
					ResultSet rs = st.executeQuery();

					while (rs.next()) {

						String name = rs.getString("product_name");
						int price = rs.getInt("product_unit_price");
						int quantity = rs.getInt("product_quantity");
						int x_val = rs.getInt("product_x_val");
						int y_val = rs.getInt("product_y_val");

						tf1.setText(name);
						String priceSTR = Integer.toString(price);
						tf2.setText(priceSTR);
						String quantitySTR = Integer.toString(quantity);
						tf3.setText(quantitySTR);
						String x_val_str = Integer.toString(x_val);
						tf4.setText(x_val_str);
						String y_val_str = Integer.toString(y_val);
						tf5.setText(y_val_str);

					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (ae.getSource().equals(updateStock)) {
				try {

					int ids = (int) idCB.getSelectedItem();
					String name_str = tf1.getText();
					String price_str = tf2.getText();
					int prices = Integer.parseInt(price_str);
					String quantity_str = tf3.getText();
					int quantitys = Integer.parseInt(quantity_str);
					String x_val_str = tf4.getText();
					int x_vals = Integer.parseInt(x_val_str);
					String y_val_str = tf5.getText();
					int y_vals = Integer.parseInt(y_val_str);

					con = getConnection(con);
					String sql = "update roboshop_product set product_name=?, product_unit_price=?, product_quantity=?, product_x_val=?, product_y_val=? where product_id="
							+ ids + " ";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setString(1, name_str);
					ps.setInt(2, prices);
					ps.setInt(3, quantitys);
					ps.setInt(4, x_vals);
					ps.setInt(5, y_vals);
					int update = ps.executeUpdate();

					if (update > 0) {
						JOptionPane.showMessageDialog(null, name_str
								+ "has been updated!");
						idCB.repaint();
						idCB.revalidate();
					} else {
						JOptionPane.showMessageDialog(null, "Error on update");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (ae.getSource().equals(deleteStock)) {

				try {

					int ids = (int) idCB.getSelectedItem();
					String name_str = tf1.getText();
					con = getConnection(con);
					String sql = "delete from roboshop_product where product_id="
							+ ids + " ";
					PreparedStatement ps = con.prepareStatement(sql);
					int delete = ps.executeUpdate();

					if (delete > 0) {

						if (JOptionPane.showConfirmDialog(deleteStock, "User: "
								+ name_str + " " + "with ID: " + ids + " "
								+ "will be removed!", "Warning!",
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
							idCB.repaint();

						}// end if
					} else {
						JOptionPane
								.showMessageDialog(null, "Unable to delete!");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (ae.getSource().equals(back)) {
				modifyStockFrame.setVisible(false);
				Admin_Panel ap = new Admin_Panel();
				ap.launchFrame();
			}// end if

		}// end of actionPerformed
	}// end of class remove_stock_panel

	class CashierPanel extends JFrame implements ActionListener {
		private JFrame cashierFrame;
		private JPanel cfLeft, cfCenter, cfRight, cfL, leftPanelT, leftPanelC,
				leftPanelB, cfC, centerPanelT, centerPanelC,innerC, centerPanelB,
				topR, cfR, cartAP, rbtnP;
		private JButton logoutB, paymentB, p1, stopB, atc;
		private JLabel paymentL, itemInfoL, itemL, pname, pPrice, pQuantity;
		private JLabel pnameL, pPriceL, pQuantityL;
		private JTextArea cartA;
		private JScrollPane jsp1;
		private Font font1 = new Font("Arial", Font.BOLD, 20);
		private Font font2 = new Font("Arial", Font.BOLD, 25);
		private Font font3 = new Font("Arial", Font.BOLD, 15);
		private Connection con;
		private String receipt;
		private NXTConnector NXTCon;
		private DataOutputStream sendData;
		private ArrayList<String> dataArr;
		private JButton[] pList;

		public CashierPanel() {
			cashierFrame = new JFrame("ROBOSHOP - Cashier");
			cashierFrame.getContentPane().setLayout(new GridLayout(1, 3));

			cfLeft = new JPanel();
			cfLeft.setLayout(new BorderLayout());

			cfCenter = new JPanel();
			cfCenter.setLayout(new BorderLayout());

			cfRight = new JPanel();
			cfRight.setLayout(new BorderLayout());

			/* right panel */
			cfR = new JPanel();
			cfR.setLayout(new BorderLayout(3, 1));
			cfR.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));

			topR = new JPanel();
			topR.setLayout(new FlowLayout());

			cartAP = new JPanel();
			cartAP.setLayout(new FlowLayout());

			rbtnP = new JPanel();
			rbtnP.setLayout(new FlowLayout(FlowLayout.CENTER));
			/* end right panel */

			/* cfR start */
			paymentL = new JLabel("Carts");
			paymentL.setFont(font2);
			cartA = new JTextArea();
			// cartA = new JTextPane();
			cartA.setFont(font3);
			receipt = "\n Roboshop Official Receipt. \n Thanks for shopping with us!";
			cartA.setText(receipt);
			// cartA.setPreferredSize(new Dimension(300,600));
			cartA.setLineWrap(true);
			cartA.setWrapStyleWord(true);
			cartA.setEditable(false);
			// StyledDocument sdoc = cartA.getStyledDocument();
			// SimpleAttributeSet center = new SimpleAttributeSet();
			// StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
			// sdoc.setParagraphAttributes(1, sdoc.getLength(), center, false);
			jsp1 = new JScrollPane(cartA);
			jsp1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

			jsp1.setPreferredSize(new Dimension(300, 590));
			jsp1.setMinimumSize(new Dimension(300, 590));

			stopB = new JButton("Stop");
			stopB.setFont(font1);
			stopB.setPreferredSize(new Dimension(150, 30));
			stopB.addActionListener(this);

			paymentB = new JButton("Payment");
			paymentB.setFont(font1);
			paymentB.setPreferredSize(new Dimension(150, 30));
			paymentB.setEnabled(false);

			logoutB = new JButton("Logout");
			logoutB.setFont(font1);
			logoutB.setPreferredSize(new Dimension(150, 30));
			/* cfR end */
			topR.add(paymentL);
			cartAP.add(jsp1, BorderLayout.CENTER);
			// rbtnP.add(paymentB);
			rbtnP.add(logoutB);
			rbtnP.add(stopB);

			/* center panel start */
			cfC = new JPanel();
			cfC.setLayout(new BorderLayout(2, 1));
			cfC.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));

			centerPanelT = new JPanel();
			centerPanelT.setLayout(new FlowLayout());

			centerPanelC = new JPanel();
			centerPanelC.setLayout(new FlowLayout(FlowLayout.CENTER));
			
			innerC = new JPanel();
			innerC.setLayout(new GridLayout(4,4));

			centerPanelB = new JPanel();
			centerPanelB.setLayout(new FlowLayout(FlowLayout.CENTER));

			/* center panel end */

			/* cfC start */
			itemInfoL = new JLabel("Products Info");
			itemInfoL.setFont(font2);

			pname = new JLabel();
			pnameL = new JLabel();
			pPrice = new JLabel();
			pPriceL = new JLabel();
			pQuantity = new JLabel();
			pQuantityL = new JLabel();
			atc = new JButton();
			atc.setVisible(false);

			pname.setFont(font1);
			pPrice.setFont(font1);
			pQuantity.setFont(font1);
			
			/* cfC end */

			/* left panel start */
			cfL = new JPanel();
			cfL.setLayout(new BorderLayout(2, 1));
			cfL.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));

			leftPanelT = new JPanel();
			leftPanelT.setLayout(new FlowLayout());

			leftPanelC = new JPanel();
			leftPanelC.setLayout(new FlowLayout(FlowLayout.CENTER));
			/* left panel end */

			/* cfL start */

			itemL = new JLabel("Available Products");
			itemL.setFont(font2);

			try {

				con = getConnection(con);
				String sql = "select product_name from roboshop_product";
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				dataArr = new ArrayList<String>();
				while (rs.next()) {
					String names = rs.getString("product_name");
					dataArr.add(names);

				}
				pList = new JButton[dataArr.size()];
				for (int i = 0; i < pList.length; i++) {
					pList[i] = new JButton((String) dataArr.get(i));
					pList[i].setFont(font1);
					pList[i].setPreferredSize(new Dimension(200, 50));
					pList[i].addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							for (int j = 0; j < pList.length; j++) {
								if (ae.getSource() == pList[j]) {
									 //cartA.append("\n " + pList[j].getText()+" " + "is added to cart");
									try {
										con = getConnection(con);
										String sql = "Select * from roboshop_product where product_name='"
												+ pList[j].getText() + "'";
										PreparedStatement ps = con
												.prepareStatement(sql);
										ResultSet rs = ps.executeQuery();
										if (rs.next()) {
											String productname = rs
													.getString("product_name");
											int productprice = rs
													.getInt("product_unit_price");
											int productqty = rs
													.getInt("product_quantity");

											pname.setText("Product Name : ");
											pname.setFont(font1);
											pnameL.setText(productname);
											pnameL.setFont(font1);
											pPrice.setText("Unit Price : ");
											pPrice.setFont(font1);
											pPriceL.setText(Integer
													.toString(productprice));
											pPriceL.setFont(font1);
											pQuantity.setText("Available Quantity : ");
											pQuantity.setFont(font1);
											pQuantityL.setText(Integer
													.toString(productqty));
											pQuantityL.setFont(font1);
											atc.setText("Add to Cart");
											atc.setFont(font1);
											atc.setPreferredSize(new Dimension(200,50));
											atc.addActionListener(new ActionListener(){
												public void actionPerformed(ActionEvent ae){
														if(ae.getSource()==atc){
															cartA.append("\n "+""+ " " + "is added to cart");
														}													
												}
											});//end of atc action listener
											atc.setVisible(true);
											
										}
										

									} catch (Exception e) {
										e.printStackTrace();
									}// end of try catch
									
									

								}
							}
						}// end of actionPerformed
					});// end of actionListener

					leftPanelC.add(pList[i]);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}// end try catch

			innerC.add(pname);
			innerC.add(pnameL);
			innerC.add(pPrice);
			innerC.add(pPriceL);
			innerC.add(pQuantity);
			innerC.add(pQuantityL);
			innerC.add(atc);
			
			centerPanelC.add(innerC);

			p1 = new JButton("p1");
			p1.setPreferredSize(new Dimension(150, 30));
			p1.addActionListener(this);
			/* cfL end */
			centerPanelB.add(p1);

			leftPanelT.add(itemL, BorderLayout.CENTER);
			centerPanelT.add(itemInfoL, BorderLayout.CENTER);
			cfL.add(leftPanelT, BorderLayout.NORTH);
			cfL.add(leftPanelC, BorderLayout.CENTER);
			cfC.add(centerPanelT, BorderLayout.NORTH);
			cfC.add(centerPanelC, BorderLayout.CENTER);
			cfC.add(centerPanelB, BorderLayout.SOUTH);
			cfR.add(topR, BorderLayout.NORTH);
			cfR.add(cartAP, BorderLayout.CENTER);
			cfR.add(rbtnP, BorderLayout.SOUTH);

		}// end of public cashierpanel

		public void launchFrame() {

			cfLeft.add(cfL);
			cfCenter.add(cfC);
			cfRight.add(cfR);
			cashierFrame.getContentPane().add(cfLeft);
			cashierFrame.getContentPane().add(cfCenter);
			cashierFrame.getContentPane().add(cfRight);
			cashierFrame.pack();
			cashierFrame.setSize(1366, 720);
			cashierFrame.setLocationRelativeTo(null);
			cashierFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			cashierFrame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					int confirmed = JOptionPane.showConfirmDialog(null,
							"Are you sure want to exit the program?",
							"Exit Program", JOptionPane.YES_NO_OPTION);
					if (confirmed == JOptionPane.YES_OPTION) {
						System.exit(0);
					}
				}
			});
			cashierFrame.setVisible(true);
			logoutB.addActionListener(this);

		}// end of launchframe

		public void actionPerformed(ActionEvent ae) {

			if (ae.getSource() == logoutB) {
				cashierFrame.setVisible(false);
				MainPanel mp = new MainPanel();
				mp.launcher();
			} else if (ae.getSource() == stopB) {
				try {
					sendData = new DataOutputStream(NXTCon.getOutputStream());

					sendData.writeInt(-1);
					sendData.flush();
				} catch (Exception e) {

				}
			} else if (ae.getSource() == p1) {

				cartA.append("\n" + p1.getText() + " " + "is added to cart\n");
				try {
					int p_id = 21;
					con = getConnection(con);
					// NXTCon = new NXTConnector();
					NXTCon = getNXTConnect(NXTCon);

					// NXTCon.connectTo("usb://");
					String sql = "select * from roboshop_product where product_id=?";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setInt(1, p_id);
					ResultSet rs = ps.executeQuery();

					while (rs.next()) {
						if (NXTCon.getNXTComm() == null) {
							System.out.println("Failed to connect any NXT");
							sendData.close();
						} else if (NXTCon.getNXTComm() != null) {
							System.out.println("Connected to "
									+ NXTCon.getNXTInfo() + "\n");
							sendData = new DataOutputStream(
									NXTCon.getOutputStream());
							RoboShopViewerApp.startKinect(NXTCon);

						}// end of nxt con

						int x_val = rs.getInt("product_x_val");
						int y_val = rs.getInt("product_y_val");
						sendData.writeInt(x_val);
						sendData.writeInt(y_val);
						sendData.flush();

					}
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println(e);
				}// end of try catch

			}

		}// end of actionPerformed
	}// end of CashierPanel

}
