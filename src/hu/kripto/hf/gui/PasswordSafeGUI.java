package hu.kripto.hf.gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class PasswordSafeGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JTextField addURLTextField;
	private JTextField addUsernameTextField;
	private JPasswordField addPasswordTextField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PasswordSafeGUI frame = new PasswordSafeGUI();
					frame.setTitle("Password Safe Application");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public PasswordSafeGUI() {
		this.setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel MainPage = new JPanel();
		MainPage.setBounds(0, 0, 450, 278);
		contentPane.add(MainPage);
		MainPage.setLayout(null);
		
		JButton btnExit_1 = new JButton("Exit");
		btnExit_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnExit_1.setBounds(327, 243, 117, 29);
		MainPage.add(btnExit_1);
		
		// JList list = new JList();
		// list.setBounds(54, 38, 187, 140);
		// MainPage.add(list);
		
		// Button to add a new record for a user
		JButton btnAdd = new JButton("Add");
		
		btnAdd.setBounds(97, 186, 75, 29);
		MainPage.add(btnAdd);
		
		JList list = new JList();
		list.setBounds(54, 33, 166, 141);
		MainPage.add(list);
		MainPage.setVisible(false);
		
		JPanel AddPage = new JPanel();
		AddPage.setBounds(0, 0, 450, 278);
		contentPane.add(AddPage);
		AddPage.setLayout(null);
		
		JButton btnCancel = new JButton("Cancel");

		btnCancel.setBounds(327, 243, 117, 29);
		AddPage.add(btnCancel);
		
		JButton btnAddNewRecord = new JButton("Add new record");
		btnAddNewRecord.setBounds(6, 243, 142, 29);
		AddPage.add(btnAddNewRecord);
		
		addURLTextField = new JTextField();
		addURLTextField.setBounds(201, 30, 134, 28);
		AddPage.add(addURLTextField);
		addURLTextField.setColumns(10);
		
		addUsernameTextField = new JTextField();
		addUsernameTextField.setBounds(201, 70, 134, 28);
		AddPage.add(addUsernameTextField);
		addUsernameTextField.setColumns(10);
		
		addPasswordTextField = new JPasswordField();
		addPasswordTextField.setBounds(201, 110, 134, 28);
		AddPage.add(addPasswordTextField);
		addPasswordTextField.setColumns(10);
		
		JLabel lblURL = new JLabel("URL:");
		lblURL.setBounds(116, 36, 73, 16);
		AddPage.add(lblURL);
		
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(116, 76, 73, 16);
		AddPage.add(lblUsername);
		
		JLabel lblPasswordAdd = new JLabel("Password:");
		lblPasswordAdd.setBounds(116, 116, 73, 16);
		AddPage.add(lblPasswordAdd);
		
		AddPage.setVisible(false);
		
		JPanel SignInPage = new JPanel();
		SignInPage.setBounds(0, 0, 450, 278);
		contentPane.add(SignInPage);
		SignInPage.setLayout(null);
		
		usernameField = new JTextField();
		usernameField.setBounds(185, 49, 134, 28);
		SignInPage.add(usernameField);
		usernameField.setColumns(10);
		
		JButton btnSignIn = new JButton("Sign In");
		
		btnSignIn.setBounds(97, 226, 117, 29);
		SignInPage.add(btnSignIn);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnExit.setBounds(226, 226, 117, 29);
		SignInPage.add(btnExit);
		
		JLabel lblUsernameSign = new JLabel("Username:");
		lblUsernameSign.setBounds(104, 55, 69, 16);
		SignInPage.add(lblUsernameSign);
		
		JLabel lblPasswordSign = new JLabel("Password:");
		lblPasswordSign.setBounds(104, 119, 69, 16);
		SignInPage.add(lblPasswordSign);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(185, 113, 134, 28);
		SignInPage.add(passwordField);
		
		SignInPage.setVisible(true);
		
		final JPanel sip = SignInPage;
		final JPanel ap = AddPage;
		final JPanel mp = MainPage;
		
		btnSignIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				sip.setVisible(false);
				ap.setVisible(false);
				mp.setVisible(true);
			}
		});
		
		// When the user changed his mind and doesn't want to add new record.
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// SignInPage.setVisible(false);
				ap.setVisible(false);
				mp.setVisible(true);
			}
		});
		
		// When the user wants to add a new record on MainPage
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// SignInPage.setVisible(false);
				ap.setVisible(true);
				mp.setVisible(false);
			}
		});
	}
}
