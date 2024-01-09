import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginPage extends JDialog {
    private JTextField txtUsername;
    private JPasswordField passPassword;
    private JButton buttonOk;
    private JButton buttonCancel;
    private JPanel loginPanel;

    public LoginPage(JFrame parent) {
        super(parent);
        setTitle("Car Rental Login");
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        ImageIcon icon=new ImageIcon("C:\\MyRepos\\CarRental\\images.jpg");
        setIconImage(icon.getImage());
        txtUsername = new JTextField();
        passPassword = new JPasswordField();
        buttonOk = new JButton("Login");
        buttonCancel = new JButton("Cancel");
        loginPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 5, 5, 5);


        constraints.gridx = 0;
        constraints.gridy = 0;
        loginPanel.add(new JLabel("Username:"), constraints);

        constraints.gridx = 1;
        loginPanel.add(txtUsername, constraints);


        constraints.gridx = 0;
        constraints.gridy = 1;
        loginPanel.add(new JLabel("Password:"), constraints);

        constraints.gridx = 1;
        loginPanel.add(passPassword, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        loginPanel.add(buttonOk, constraints);

        constraints.gridx = 1;
        loginPanel.add(buttonCancel, constraints);

        setContentPane(loginPanel);
        setMinimumSize(new Dimension(250, 150));


        getRootPane().setDefaultButton(buttonOk);


        buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticate();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });


        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    authenticate();
                }
            }
        };

        txtUsername.addKeyListener(enterKeyAdapter);
        passPassword.addKeyListener(enterKeyAdapter);

        pack();
        setVisible(true);
    }

    private void authenticate() {
        String username = txtUsername.getText();
        String password = new String(passPassword.getPassword());

        // Assuming DbUtils.getAuthenticatedUser is a method that authenticates the user
        if (DbUtils.getAuthenticatedUser(username, password) != null) {
            dispose();
            SwingUtilities.invokeLater(() -> new MenuPage());
        } else {
            JOptionPane.showMessageDialog(LoginPage.this, "Login Failed", "Login Result", JOptionPane.ERROR_MESSAGE);
        }
    }
}
