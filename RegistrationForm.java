import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegistrationForm extends JFrame {

    // Declare the components
    private JLabel nameLabel, emailLabel, phoneLabel, genderLabel, cityLabel, stateLabel, descriptionLabel;
    private JTextField nameField, emailField, phoneField, cityField, stateField;
    private JTextArea descriptionArea;
    private JRadioButton maleRadioButton, femaleRadioButton, otherRadioButton;
    private ButtonGroup genderGroup;
    private JButton submitButton, cancelButton, adminButton;

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/RegistrationDB";
    private static final String USER = "root"; // replace with your MySQL username
    private static final String PASS = "Gautam@2004"; // replace with your MySQL password

    public RegistrationForm() {
        // Set the frame properties
        setTitle("Registration Form");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize the components
        nameLabel = new JLabel("Name:");
        emailLabel = new JLabel("Email:");
        phoneLabel = new JLabel("Phone Number:");
        genderLabel = new JLabel("Gender:");
        cityLabel = new JLabel("City:");
        stateLabel = new JLabel("State:");
        descriptionLabel = new JLabel("Description:");

        nameField = new JTextField(30);
        emailField = new JTextField(30);
        phoneField = new JTextField(30);
        cityField = new JTextField(30);
        stateField = new JTextField(30);

        descriptionArea = new JTextArea();
        descriptionArea.setRows(5);
        descriptionArea.setColumns(20);

        maleRadioButton = new JRadioButton("Male");
        femaleRadioButton = new JRadioButton("Female");
        otherRadioButton = new JRadioButton("Other");

        genderGroup = new ButtonGroup();
        genderGroup.add(maleRadioButton);
        genderGroup.add(femaleRadioButton);
        genderGroup.add(otherRadioButton);

        submitButton = new JButton("Submit");
        cancelButton = new JButton("Cancel");
        adminButton = new JButton("Admin");

        // Set the layout manager
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Add components to the frame
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(nameLabel, gbc);

        gbc.gridx = 1;
        add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(emailLabel, gbc);

        gbc.gridx = 1;
        add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(phoneLabel, gbc);

        gbc.gridx = 1;
        add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(genderLabel, gbc);

        gbc.gridx = 1;
        add(maleRadioButton, gbc);

        gbc.gridy = 4;
        add(femaleRadioButton, gbc);

        gbc.gridy = 5;
        add(otherRadioButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        add(cityLabel, gbc);

        gbc.gridx = 1;
        add(cityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        add(stateLabel, gbc);

        gbc.gridx = 1;
        add(stateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        add(descriptionLabel, gbc);

        gbc.gridx = 1;
        add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        add(submitButton, gbc);

        gbc.gridx = 1;
        add(cancelButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 10;
        add(adminButton, gbc);

        // Add action listeners for the buttons
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToDatabase();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });

        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginDialog loginDialog = new LoginDialog();
                loginDialog.setVisible(true);
            }
        });
    }

    private void saveToDatabase() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String gender = genderGroup.getSelection() == null ? "" : genderGroup.getSelection().getActionCommand();
        String city = cityField.getText();
        String state = stateField.getText();
        String description = descriptionArea.getText();

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO RegistrationDetails (name, email, phone, gender, city, state, description) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, gender);
            stmt.setString(5, city);
            stmt.setString(6, state);
            stmt.setString(7, description);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Form submitted successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving data.");
        }
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        cityField.setText("");
        stateField.setText("");
        descriptionArea.setText("");
        genderGroup.clearSelection();
    }

    private class AdminInterface extends JFrame {

        private JTable table;

        public AdminInterface() {
            setTitle("Admin Interface");
            setSize(600, 400);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            // Fetch data from database and populate table
            String[] columnNames = {"ID", "Name", "Email", "Phone", "Gender", "City", "State", "Description"};
            String[][] data = fetchDataFromDatabase();

            table = new JTable(data, columnNames);
            add(new JScrollPane(table));

            setVisible(true);
        }

        private String[][] fetchDataFromDatabase() {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                 ResultSet rs = stmt.executeQuery("SELECT * FROM RegistrationDetails")) {

                rs.last();
                int rowCount = rs.getRow();
                rs.beforeFirst();

                String[][] data = new String[rowCount][8];
                int row = 0;
                while (rs.next()) {
                    data[row][0] = String.valueOf(rs.getInt("id"));
                    data[row][1] = rs.getString("name");
                    data[row][2] = rs.getString("email");
                    data[row][3] = rs.getString("phone");
                    data[row][4] = rs.getString("gender");
                    data[row][5] = rs.getString("city");
                    data[row][6] = rs.getString("state");
                    data[row][7] = rs.getString("description");
                    row++;
                }
                System.out.println("Fetched " + rowCount + " records from the database.");
                return data;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return new String[0][0];
            }
        }
    }

    private class LoginDialog extends JDialog {

        private JTextField usernameField;
        private JPasswordField passwordField;
        private JButton loginButton, cancelButton;

        public LoginDialog() {
            setTitle("Admin Login");
            setSize(300, 150);
            setLayout(new GridBagLayout());
            setLocationRelativeTo(null);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;

            // Add username label and field
            gbc.gridx = 0;
            gbc.gridy = 0;
            add(new JLabel("Username:"), gbc);

            usernameField = new JTextField(15);
            gbc.gridx = 1;
            add(usernameField, gbc);

            // Add password label and field
            gbc.gridx = 0;
            gbc.gridy = 1;
            add(new JLabel("Password:"), gbc);

            passwordField = new JPasswordField(15);
            gbc.gridx = 1;
            add(passwordField, gbc);

            // Add login and cancel buttons
            loginButton = new JButton("Login");
            cancelButton = new JButton("Cancel");

            gbc.gridx = 0;
            gbc.gridy = 2;
            add(loginButton, gbc);

            gbc.gridx = 1;
            add(cancelButton, gbc);

            // Add action listeners for the buttons
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (authenticate()) {
                        new AdminInterface();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(LoginDialog.this, "Invalid username or password.");
                    }
                }
            });

            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
        }

        private boolean authenticate() {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Replace with your admin credentials
            String adminUsername = "Gautam1015";
            String adminPassword = "sweety1015";

            return username.equals(adminUsername) && password.equals(adminPassword);
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/RegistrationDB", "root", "Gautam@2004"
            );
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from RegistrationDetails");
            while (resultSet.next()) {
                System.out.println(resultSet.getInt(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3));
                ;

            }
            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RegistrationForm().setVisible(true);
            }
        });
    }
}
