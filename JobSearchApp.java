package assa;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
public class JobSearchApp {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private String username;
    private JPanel jobListingsPanel;
    public JobSearchApp() {
        initializeUI();
        setupStartScreen();
        setupCompanyLoginForm();
        setupLoginForm();
        setupRegistrationForm();
        setupJobAdsForm();
    }


    private void initializeUI() {
        frame = new JFrame("Job Search Application");
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.add(cardPanel);
        frame.setVisible(true);
    }

    private void setupStartScreen() {
        JPanel startPanel = new JPanel(new GridBagLayout());
        JButton userLoginButton = new JButton("Kullanıcı Girişi");
        JButton companyLoginButton = new JButton("Şirket Girişi");

        // Set button size
        Dimension buttonSize = new Dimension(200, 40);
        userLoginButton.setPreferredSize(buttonSize);
        companyLoginButton.setPreferredSize(buttonSize);

        // Add buttons to the panel with GridBagConstraints for top alignment
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(40, 0, 0, 0); // Set top margin
        gbc.anchor = GridBagConstraints.NORTH; // Align to the top
        startPanel.add(userLoginButton, gbc);

        gbc.gridy = 1;
        startPanel.add(companyLoginButton, gbc);

        cardPanel.add(startPanel, "StartScreen");

        userLoginButton.addActionListener(e -> cardLayout.show(cardPanel, "Login"));
        companyLoginButton.addActionListener(e -> cardLayout.show(cardPanel, "Company"));
    }
    private void setupCompanyLoginForm() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        JTextField usernameOrEmailField = new JTextField();
        JButton backButton = new JButton("Geri Dön");
        JButton loginButton = new JButton("Login");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 20, 0); // Top margin

        JLabel companyLabel = new JLabel("Company Name:");
        companyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        loginPanel.add(companyLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(usernameOrEmailField, gbc);
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginButton, gbc);

        gbc.gridx = 1;
        loginPanel.add(backButton, gbc);

        cardPanel.add(loginPanel, "Company");

        backButton.addActionListener(e -> cardLayout.show(cardPanel, "StartScreen"));

        loginButton.addActionListener(e -> {
            String companyName = usernameOrEmailField.getText();
            boolean loginSuccessful = false;

            try (Connection conn = connectToDatabase()) {
                if (conn != null) {
                    String sql = "SELECT Name FROM Companies WHERE Name = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, companyName);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) {
                            loginSuccessful = true;
                            this.username = rs.getString("Name");
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            if (loginSuccessful) {
                JOptionPane.showMessageDialog(frame, "Login successful");
                setupCompanyDashboard(username);
                cardLayout.show(cardPanel, "Company Advertisements");
            } else {
                JOptionPane.showMessageDialog(frame, "This company name doesn't exist!");
            }
        });
    }

    private void setupCompanyDashboard(String companyName) {
        JPanel companyDashboardPanel = new JPanel();
        companyDashboardPanel.setLayout(new BoxLayout(companyDashboardPanel, BoxLayout.Y_AXIS));



        JButton addJobListingButton = createStyledButton("Yeni İş İlanı Ekle");
        addJobListingButton.addActionListener(e -> showAddJobListingForm(companyName));

        JButton viewApplicationsButton = createStyledButton("Başvuruları Görüntüle");
        viewApplicationsButton.addActionListener(e -> setupViewApplicationsScreen(companyName));

        JButton backButton = createStyledButton("Geri Dön");
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "StartScreen"));

        JButton viewCertificatesButton = createStyledButton("Sertifikaları Görüntüle");
        viewCertificatesButton.addActionListener(e -> viewCompanyCertificates(companyName));

        JButton addCertificateButton = createStyledButton("Yeni Sertifika Ekle");
        addCertificateButton.addActionListener(e -> showAddCertificateForm(companyName));

        JButton ShowAppButton = createStyledButton("Sertifika Başvurularını Görüntüle");
        ShowAppButton.addActionListener(e -> setupViewApplicationsScreen(companyName));

        // Butonları panele ekle, her birini aynı boyutta olacak şekilde düzenle
        companyDashboardPanel.add(createButtonPanel(addJobListingButton));
        companyDashboardPanel.add(createButtonPanel(viewApplicationsButton));
        companyDashboardPanel.add(createButtonPanel(backButton));
        companyDashboardPanel.add(createButtonPanel(viewCertificatesButton));
        companyDashboardPanel.add(createButtonPanel(addCertificateButton));
        companyDashboardPanel.add(createButtonPanel(ShowAppButton));

        try (Connection conn = connectToDatabase()) {
            if (conn != null) {
                String sql = "SELECT * FROM getJobsByCompany(?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, companyName);
                    ResultSet rs = pstmt.executeQuery();

                    while (rs.next()) {
                        // İlan bilgilerini alın
                        String title = rs.getString("Title");
                        String description = rs.getString("Description");
                        String location = rs.getString("Location");
                        String type = rs.getString("Type");
                        String category = rs.getString("Category");
                        Date postedDate = rs.getDate("PostedDate");

                        // Her ilan için ayrı bir panel oluşturun
                        JPanel jobPanel = new JPanel();
                        jobPanel.setLayout(new BoxLayout(jobPanel, BoxLayout.Y_AXIS));
                        jobPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                        jobPanel.add(new JLabel("Başlık: " + title));
                        jobPanel.add(new JLabel("Açıklama: " + description));
                        jobPanel.add(new JLabel("Lokasyon: " + location));
                        jobPanel.add(new JLabel("Tür: " + type));
                        jobPanel.add(new JLabel("Kategori: " + category));
                        jobPanel.add(new JLabel("Yayınlanma Tarihi: " + postedDate.toString()));

                        companyDashboardPanel.add(jobPanel);
                        companyDashboardPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Aralık ekleyin
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                JScrollPane scrollPane = new JScrollPane(companyDashboardPanel);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                cardPanel.add(scrollPane, "CompanyDashboard");
                cardLayout.show(cardPanel, "CompanyDashboard");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    // ...

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 40));
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setBackground(new Color(135, 206, 250)); // Özel bir renk
        button.setForeground(Color.WHITE);
        return button;
    }

    private JPanel createButtonPanel(JButton button) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.add(button);
        return buttonPanel;
    }
    private void viewCompanyCertificates(String companyName) {
        JPanel certificatesPanel = new JPanel();
        certificatesPanel.setLayout(new BoxLayout(certificatesPanel, BoxLayout.Y_AXIS));

        JButton backButton = new JButton("Geri Dön");
        backButton.addActionListener(e -> setupCompanyDashboard(companyName));

        certificatesPanel.add(backButton);
        try (Connection conn = connectToDatabase()) {
            if (conn != null) {
                String sql = "SELECT * FROM Certificates WHERE CompanyID IN (SELECT CompanyID FROM Companies WHERE Name = ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, companyName);
                    ResultSet rs = pstmt.executeQuery();

                    while (rs.next()) {
                        String name = rs.getString("Name");
                        String description = rs.getString("Description");

                        JPanel certificatePanel = new JPanel();
                        certificatePanel.setLayout(new BoxLayout(certificatePanel, BoxLayout.Y_AXIS));
                        certificatePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                        certificatePanel.add(new JLabel("Adı: " + name));
                        certificatePanel.add(new JLabel("Açıklama: " + description));

                        certificatesPanel.add(certificatePanel);
                        certificatesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(certificatesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        cardPanel.add(scrollPane, "ViewCertificates");
        cardLayout.show(cardPanel, "ViewCertificates");
    }
    private void showAddCertificateForm(String companyName) {
        JPanel addCertificatePanel = new JPanel(new GridLayout(0, 2));


        JTextField nameField = new JTextField();
        JTextArea descriptionArea = new JTextArea(4, 20);
        JButton submitButton = new JButton("Sertifika Ekle");

        addCertificatePanel.add(new JLabel("Sertifika Adı:"));
        addCertificatePanel.add(nameField);
        addCertificatePanel.add(new JLabel("Açıklama:"));
        addCertificatePanel.add(new JScrollPane(descriptionArea));
        addCertificatePanel.add(submitButton);

        JButton backButton = new JButton("Geri Dön");
        backButton.addActionListener(e -> setupCompanyDashboard(companyName));

        addCertificatePanel.add(backButton);

        submitButton.addActionListener(e -> {
            String name = nameField.getText();
            String description = descriptionArea.getText();

            try (Connection conn = connectToDatabase()) {
                if (conn != null) {
                    String sql = "INSERT INTO Certificates (CompanyID, Name, Description) VALUES ((SELECT CompanyID FROM Companies WHERE Name = ?), ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, companyName);
                        pstmt.setString(2, name);
                        pstmt.setString(3, description);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "Sertifika başarıyla eklendi");
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Sertifika eklenirken bir hata oluştu");
            }
        });

        cardPanel.add(addCertificatePanel, "AddCertificate");
        cardLayout.show(cardPanel, "AddCertificate");
    }
    private void setupCompanyApplications(String companyName) {
        JPanel companyApplicationsPanel = new JPanel();
        companyApplicationsPanel.setLayout(new BoxLayout(companyApplicationsPanel, BoxLayout.Y_AXIS));


        JButton backButton = new JButton("Geri Dön");
        backButton.addActionListener(e -> setupCompanyDashboard(companyName));

        companyApplicationsPanel.add(backButton);


        try (Connection conn = connectToDatabase()) {
            if (conn != null) {
                String sql = "SELECT a.ApplicationID, a.JobID, a.UserID, a.Status, u.FullName, j.Title FROM Applications a " +
                        "JOIN Jobs j ON a.JobID = j.JobID " +
                        "JOIN Users u ON a.UserID = u.UserID " +
                        "WHERE j.EmployerID IN (SELECT CompanyID FROM Companies WHERE Name = ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, companyName);
                    ResultSet rs = pstmt.executeQuery();

                    while (rs.next()) {
                        int applicationId = rs.getInt("ApplicationID");
                        String jobTitle = rs.getString("Title");
                        String applicantName = rs.getString("FullName");
                        String currentStatus = rs.getString("Status");

                        // Her başvuru için bir panel oluşturun
                        JPanel applicationPanel = new JPanel();
                        applicationPanel.setLayout(new BoxLayout(applicationPanel, BoxLayout.Y_AXIS));
                        applicationPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                        applicationPanel.add(new JLabel("İş Başlığı: " + jobTitle));
                        applicationPanel.add(new JLabel("Başvuran: " + applicantName));
                        applicationPanel.add(new JLabel("Durum: " + currentStatus));

                        JButton acceptButton = new JButton("Kabul Et");
                        JButton declineButton = new JButton("Reddet");

                        acceptButton.addActionListener(e -> updateApplicationStatus(applicationId, "Accepted"));
                        declineButton.addActionListener(e -> updateApplicationStatus(applicationId, "Declined"));

                        applicationPanel.add(acceptButton);
                        applicationPanel.add(declineButton);

                        companyApplicationsPanel.add(applicationPanel);
                        companyApplicationsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Aralık ekleyin
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(companyApplicationsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        cardPanel.add(scrollPane, "CompanyApplications");
        cardLayout.show(cardPanel, "CompanyApplications");
    }
    private void setupViewApplicationsScreen(String companyName) {
        JPanel applicationsPanel = new JPanel();
        applicationsPanel.setLayout(new BoxLayout(applicationsPanel, BoxLayout.Y_AXIS));
        JButton backButton = new JButton("Geri Dön");
        backButton.addActionListener(e -> setupCompanyDashboard(companyName));
        applicationsPanel.add(backButton);
        // Toplam başvuru sayısını almak için SQL sorgusu
        String countSql = "SELECT COUNT(*) AS TotalApplications " +
                "FROM UserCertificates uc " +
                "JOIN Certificates c ON uc.CertificateID = c.CertificateID " +
                "JOIN Users u ON uc.UserID = u.UserID " +
                "WHERE c.CompanyID = (SELECT CompanyID FROM Companies WHERE Name = ?) HAVING COUNT(*) > 0";
        try (Connection conn = connectToDatabase()) {
            // Toplam başvuru sayısını bul
            try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
                countStmt.setString(1, companyName);
                ResultSet countResult = countStmt.executeQuery();

                if (countResult.next()) {
                    int totalApplications = countResult.getInt("TotalApplications");
                    JLabel totalApplicationsLabel = new JLabel("Toplam Başvuru Sayısı: " + totalApplications);
                    applicationsPanel.add(totalApplicationsLabel);
                    applicationsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Aralık ekleyin
                }
            }

            // Şirketin sertifika başvurularını listele
            String applicationsSql = "SELECT uc.UserCertificateID, u.FullName, c.Name, uc.Status " +
                    "FROM UserCertificates uc " +
                    "JOIN Certificates c ON uc.CertificateID = c.CertificateID " +
                    "JOIN Users u ON uc.UserID = u.UserID " +
                    "WHERE c.CompanyID = (SELECT CompanyID FROM Companies WHERE Name = ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(applicationsSql)) {
                pstmt.setString(1, companyName);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    int userCertificateId = rs.getInt("UserCertificateID");
                    String applicantName = rs.getString("FullName");
                    String certificateName = rs.getString("Name");
                    String currentStatus = rs.getString("Status");

                    JPanel applicationPanel = new JPanel();
                    applicationPanel.setLayout(new BoxLayout(applicationPanel, BoxLayout.Y_AXIS));
                    applicationPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    applicationPanel.add(new JLabel("Applicant: " + applicantName));
                    applicationPanel.add(new JLabel("Certificate: " + certificateName));
                    applicationPanel.add(new JLabel("Current Status: " + currentStatus));

                    JButton acceptButton = new JButton("Accept");
                    JButton declineButton = new JButton("Decline");

                    acceptButton.addActionListener(e -> updateCertificateApplicationStatus(userCertificateId, "Accepted"));
                    declineButton.addActionListener(e -> updateCertificateApplicationStatus(userCertificateId, "Declined"));

                    applicationPanel.add(acceptButton);
                    applicationPanel.add(declineButton);

                    applicationsPanel.add(applicationPanel);
                    applicationsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(applicationsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        cardPanel.add(scrollPane, "ViewCertificateApplications");
        cardLayout.show(cardPanel, "ViewCertificateApplications");
    }


    private void updateApplicationStatus(int applicationId, String newStatus) {
        try (Connection conn = connectToDatabase()) {
            String sql = "UPDATE Applications SET Status = ? WHERE ApplicationID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newStatus);
                pstmt.setInt(2, applicationId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Application status updated to: " + newStatus);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating application status.");
        }
    }

    private void updateCertificateApplicationStatus(int userCertificateId, String newStatus) {
        try (Connection conn = connectToDatabase()) {
            String sql = "UPDATE UserCertificates SET Status = ? WHERE UserCertificateID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newStatus);
                pstmt.setInt(2, userCertificateId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Certificate application status updated to: " + newStatus);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating certificate application status.");
        }
    }
    private void showAddJobListingForm(String companyName) {
        JPanel addJobPanel = new JPanel(new GridLayout(0, 2)); // Sıfır satır, iki sütun
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Ortalama işlemi için wrapper panel

        JTextField titleField = new JTextField();
        JTextArea descriptionArea = new JTextArea(4, 20);
        JTextField locationField = new JTextField();
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"Tam Zamanlı", "Yarı Zamanlı", "Staj"});
        JTextField categoryField = new JTextField();
        JButton submitButton = new JButton("İlanı Ekle");
        JButton backButton = new JButton("Geri Dön");

        addJobPanel.add(new JLabel("Başlık:"));
        addJobPanel.add(titleField);
        addJobPanel.add(new JLabel("Açıklama:"));
        addJobPanel.add(new JScrollPane(descriptionArea)); // Açıklama için kaydırma çubuğu
        addJobPanel.add(new JLabel("Lokasyon:"));
        addJobPanel.add(locationField);
        addJobPanel.add(new JLabel("Tür:"));
        addJobPanel.add(typeComboBox);
        addJobPanel.add(new JLabel("Kategori:"));
        addJobPanel.add(categoryField);

        addJobPanel.add(submitButton);
        addJobPanel.add(backButton);

        backButton.addActionListener(e -> setupCompanyDashboard(companyName));

        // İlan ekleme işlemi
        submitButton.addActionListener(e -> {
            String title = titleField.getText();
            String description = descriptionArea.getText();
            String location = locationField.getText();
            String type = (String) typeComboBox.getSelectedItem();
            String category = categoryField.getText();
            Date postedDate = new Date(System.currentTimeMillis()); // Mevcut tarih

            try (Connection conn = connectToDatabase()) {
                if (conn != null) {
                    String sql = "INSERT INTO Jobs (EmployerID, Title, Description, Location, Type, Category, PostedDate) VALUES ((SELECT CompanyID FROM Companies WHERE Name = ?), ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, companyName);
                        pstmt.setString(2, title);
                        pstmt.setString(3, description);
                        pstmt.setString(4, location);
                        pstmt.setString(5, type);
                        pstmt.setString(6, category);
                        pstmt.setDate(7, postedDate); // Tarih ekleniyor
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "İlan başarıyla eklendi");
                        setupCompanyDashboard(companyName); // Dashboard'u yeniden yükle
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "İlan eklenirken bir hata oluştu");
            }
        });

        wrapperPanel.add(addJobPanel);

        cardPanel.add(wrapperPanel, "AddJob");

        // cardPanel'in boyutunu al
        Dimension cardPanelSize = cardPanel.getSize();

        // addJobPanel'in genişliğini ve yüksekliğini ayarla
        int panelWidth = cardPanelSize.width / 2; // cardPanel'in %50'si
        int panelHeight = (int) (cardPanelSize.height * 0.6); // cardPanel'in %60'ı

        addJobPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));

        cardLayout.show(cardPanel, "AddJob");
    }
    private void setupLoginForm() {
        JPanel loginPanel = new JPanel(new GridLayout(4, 4));
        JTextField usernameOrEmailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton switchToRegisterButton = new JButton("Register");

        loginPanel.add(new JLabel("Username/Email:"));
        loginPanel.add(usernameOrEmailField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(switchToRegisterButton);
        JButton backButton = new JButton("Geri Dön");
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "StartScreen"));
        loginPanel.add(backButton);

        cardPanel.add(loginPanel, "Login");

        loginButton.addActionListener(e -> {
            String usernameOrEmail = usernameOrEmailField.getText();
            String password = new String(passwordField.getPassword());
            boolean loginSuccessful = false;

            try (Connection conn = connectToDatabase()) {
                if (conn != null) {
                    String sql = "SELECT Username FROM Users WHERE (Username = ? OR Email = ?) AND PasswordHash = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, usernameOrEmail);
                        pstmt.setString(2, usernameOrEmail);
                        pstmt.setString(3, password); // Verify hashed password
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) {
                            loginSuccessful = true;
                            this.username = rs.getString("Username");
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            if (loginSuccessful) {
                JOptionPane.showMessageDialog(frame, "Login successful");
                setupUserApplicationsForm(this.username); // Setup and show user's applications
                cardLayout.show(cardPanel, "UserApplications");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid login credentials");
            }
        });



        switchToRegisterButton.addActionListener(e -> cardLayout.show(cardPanel, "Register"));
    }
    private void setupRegistrationForm() {
        JPanel registerPanel = new JPanel(new GridLayout(11, 2)); // Adjust grid layout for additional fields
        JTextField newUsernameField = new JTextField();
        JTextField newEmailField = new JTextField();
        JTextField newFullNameField = new JTextField();
        JTextField newLocationField = new JTextField();
        JTextArea newProfileSummaryField = new JTextArea(3, 20);
        String[] experienceLevels = {"Entry-level", "Mid-level", "Senior", "Manager"};
        JComboBox<String> newExperienceLevelField = new JComboBox<>(experienceLevels);
        JTextArea newEducationField = new JTextArea(3, 20);
        JTextArea newSkillsField = new JTextArea(3, 20);
        JPasswordField newPasswordField = new JPasswordField();
        JButton registerButton = new JButton("Register");
        JButton switchToLoginButton = new JButton("Login");

        // Add components to the panel
        registerPanel.add(new JLabel("*Username:"));
        registerPanel.add(newUsernameField);
        registerPanel.add(new JLabel("*Email:"));
        registerPanel.add(newEmailField);
        registerPanel.add(new JLabel("*Full Name:"));
        registerPanel.add(newFullNameField);
        registerPanel.add(new JLabel("Location:"));
        registerPanel.add(newLocationField);
        registerPanel.add(new JLabel("Profile Summary:"));
        registerPanel.add(new JScrollPane(newProfileSummaryField)); // Use JScrollPane for text areas
        registerPanel.add(new JLabel("Experience Level:"));
        registerPanel.add(newExperienceLevelField);
        registerPanel.add(new JLabel("Education:"));
        registerPanel.add(new JScrollPane(newEducationField));
        registerPanel.add(new JLabel("Skills:"));
        registerPanel.add(new JScrollPane(newSkillsField));
        registerPanel.add(new JLabel("*Password:"));
        registerPanel.add(newPasswordField);
        registerPanel.add(registerButton);
        registerPanel.add(switchToLoginButton);

        cardPanel.add(registerPanel, "Register");

        registerButton.addActionListener(e -> {
            String username = newUsernameField.getText();
            String email = newEmailField.getText();
            String fullName = newFullNameField.getText();
            String location = newLocationField.getText();
            String profileSummary = newProfileSummaryField.getText();
            String experienceLevel = (String) newExperienceLevelField.getSelectedItem();
            String education = newEducationField.getText();
            String skills = newSkillsField.getText();
            String password = new String(newPasswordField.getPassword());

            // Hash the password before storing (recommended for security)
            if (username.isEmpty() || email.isEmpty() || fullName.isEmpty() ||  password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Lütfen zorunlu alanları doldurun.");
                return;
            }

            try (Connection conn = connectToDatabase()) {
                if (conn != null) {
                    String sql = "INSERT INTO Users (Username, Email, FullName, Location, ProfileSummary, ExperienceLevel, Education, Skills, PasswordHash) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, username);
                        pstmt.setString(2, email);
                        pstmt.setString(3, fullName);
                        pstmt.setString(4, location);
                        pstmt.setString(5, profileSummary);
                        pstmt.setString(6, experienceLevel);
                        pstmt.setString(7, education);
                        pstmt.setString(8, skills);
                        pstmt.setString(9, password); // Store hashed password
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "Registration successful");
                        cardLayout.show(cardPanel, "Login");
                    }
                }
            } catch (SQLException ex) {

                String errorMessage = ex.getMessage();// PostgreSQL'den gelen hatayı kontrol et


                String specialErrorMessage = "Password en az 6 karakter olmalidir";// Trigger'dan gelen özel hata mesajını kontrol et
                if (errorMessage.contains(specialErrorMessage)) {// Ozel durum, ozel mesaji goster

                    JOptionPane.showMessageDialog(frame, "Kayıt sırasında hata oluştu: " + specialErrorMessage);
                } else {// Diger durumlar icin genel hata mesaji goster

                    JOptionPane.showMessageDialog(frame, "Kayıt sırasında hata oluştu: " + errorMessage);
                }
            }
        });

        switchToLoginButton.addActionListener(e -> cardLayout.show(cardPanel, "Login"));
    }
    private void setupJobAdsForm() {
        JPanel jobAdsPanel = new JPanel();
        jobAdsPanel.setLayout(new BoxLayout(jobAdsPanel, BoxLayout.Y_AXIS));

        // Filter controls
        JComboBox<String> locationComboBox = new JComboBox<>();
        JComboBox<String> categoryComboBox = new JComboBox<>();
        JButton filterButton = new JButton("Filtrele");

        // Load filter options
        loadFilterOptions(locationComboBox, categoryComboBox);

        // Filter panel setup
        JPanel filterPanel = new JPanel(new GridLayout(2, 2));
        filterPanel.add(new JLabel("Lokasyon:"));
        filterPanel.add(locationComboBox);
        filterPanel.add(new JLabel("Kategori:"));
        filterPanel.add(categoryComboBox);

        // Back to Applications button
        JButton backButton = new JButton("Back to Applications");
        backButton.addActionListener(e -> {
            setupUserApplicationsForm(this.username);
            cardLayout.show(cardPanel, "UserApplications");
        });
        filterButton.addActionListener(e -> {
            String selectedLocation = (String) locationComboBox.getSelectedItem();
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            showFilteredJobAdvertisements(selectedLocation, selectedCategory);
        });

        // Aligning components
        JPanel alignmentPanel = new JPanel(new BorderLayout());
        alignmentPanel.add(filterButton, BorderLayout.NORTH);
        alignmentPanel.add(filterPanel, BorderLayout.CENTER);
        alignmentPanel.add(backButton, BorderLayout.EAST);

        // Job listings panel setup
        jobListingsPanel = new JPanel();
        jobListingsPanel.setLayout(new BoxLayout(jobListingsPanel, BoxLayout.Y_AXIS));

        // Add alignment panel and job listings panel to jobAdsPanel
        jobAdsPanel.add(alignmentPanel);

        // Wrap the jobListingsPanel in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(jobListingsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Center the jobListingsPanel within the JScrollPane
        scrollPane.getViewport().setViewPosition(new Point(0, 0));

        jobAdsPanel.add(scrollPane);

        // Initially show all job advertisements
        showJobAdvertisements();

        // Filter button action listener


        // Add jobAdsPanel to cardPanel
        cardPanel.add(jobAdsPanel, "JobAds");
    }
    private void loadFilterOptions(JComboBox<String> locationComboBox, JComboBox<String> categoryComboBox) {
        // Add "All" option
        locationComboBox.addItem("All");
        categoryComboBox.addItem("All");

        // Load locations and categories from database
        try (Connection conn = connectToDatabase()) {
            // Load locations
            String locationSql = "SELECT DISTINCT Location FROM Jobs";
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(locationSql);
                while (rs.next()) {
                    locationComboBox.addItem(rs.getString("Location"));
                }
            }

            // Load categories
            String categorySql = "SELECT DISTINCT Category FROM Jobs";
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(categorySql);
                while (rs.next()) {
                    categoryComboBox.addItem(rs.getString("Category"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    private void showFilteredJobAdvertisements(String location, String category) {
        // Clear existing job advertisements
        jobListingsPanel.removeAll();

        String sql;
        boolean useFunction = !"All".equals(location) && !"All".equals(category);

        if (useFunction) {
            // Use the SQL function
            sql = "SELECT * FROM get_jobs_by_location_and_industry(?, ?)";
        } else {
            // Use the original SQL query with dynamic filters
            sql = "SELECT JobID, Title, Description, Location, Type, Category, PostedDate FROM Jobs";
            if (!"All".equals(location) || !"All".equals(category)) {
                sql += " WHERE ";
                if (!"All".equals(location)) {
                    sql += "Location = ? ";
                    if (!"All".equals(category)) {
                        sql += "AND ";
                    }
                }
                if (!"All".equals(category)) {
                    sql += "Category = ?";
                }
            }
        }

        try (Connection conn = connectToDatabase();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            if (useFunction) {
                // Set parameters for the function
                pstmt.setString(paramIndex++, location);
                pstmt.setString(paramIndex, category);
            } else {
                // Set parameters for the original query
                if (!"All".equals(location)) {
                    pstmt.setString(paramIndex++, location);
                }
                if (!"All".equals(category)) {
                    pstmt.setString(paramIndex, category);
                }
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                JPanel jobPanel = new JPanel();
                jobPanel.setLayout(new BoxLayout(jobPanel, BoxLayout.Y_AXIS));

                int jobId = rs.getInt("JobID");
                jobPanel.add(new JLabel("Title: " + rs.getString("Title")));
                jobPanel.add(new JLabel("Description: " + rs.getString("Description")));
                jobPanel.add(new JLabel("Location: " + rs.getString("Location")));
                jobPanel.add(new JLabel("Type: " + rs.getString("Type")));
                jobPanel.add(new JLabel("Category: " + rs.getString("Category")));
                jobPanel.add(new JLabel("Posted Date: " + rs.getDate("PostedDate").toString()));

                JButton applyButton = new JButton("Başvur");
                applyButton.addActionListener(e -> applyToJob(this.username, jobId));
                jobPanel.add(applyButton);

                jobPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                jobListingsPanel.add(jobPanel);
            }
            jobListingsPanel.revalidate();
            jobListingsPanel.repaint();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void applyToJob(String username, int jobId) {
        try (Connection conn = connectToDatabase()) {
            if (conn != null) {
                String sql = "INSERT INTO Applications (JobID, UserID, ApplicationDate, Status) VALUES (?, (SELECT UserID FROM Users WHERE Username = ?), CURRENT_DATE, 'Pending')";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, jobId);
                    pstmt.setString(2, username);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(frame, "Başvuru başarılı");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            String errorMessage = ex.getMessage();

            // Özel durumu kontrol et
            String specialErrorMessage = "Bu ise daha once basvurdunuz";
            if (errorMessage.contains(specialErrorMessage)) {
                // Özel durum, özel mesaj
                JOptionPane.showMessageDialog(frame, "Başvuru sırasında hata oluştu: " + specialErrorMessage);
            } else {
                // Diğer durumlar için genel hata mesajı
                JOptionPane.showMessageDialog(frame, "Başvuru sırasında hata oluştu: " + errorMessage);
            }
        }
    }
    private void showJobAdvertisements() {
        // Clear existing job advertisements
        jobListingsPanel.removeAll();

        try (Connection conn = connectToDatabase()) {
            String sql = "SELECT * FROM allJobAds";
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    JPanel jobPanel = new JPanel();
                    jobPanel.setLayout(new BoxLayout(jobPanel, BoxLayout.Y_AXIS));

                    int jobId = rs.getInt("JobID");
                    jobPanel.add(new JLabel("Title: " + rs.getString("Title")));
                    jobPanel.add(new JLabel("Description: " + rs.getString("Description")));
                    jobPanel.add(new JLabel("Location: " + rs.getString("Location")));
                    jobPanel.add(new JLabel("Type: " + rs.getString("Type")));
                    jobPanel.add(new JLabel("Category: " + rs.getString("Category")));
                    jobPanel.add(new JLabel("Posted Date: " + rs.getDate("PostedDate").toString()));

                    JButton applyButton = new JButton("Başvur");
                    applyButton.addActionListener(e -> applyToJob(this.username, jobId));
                    jobPanel.add(applyButton);

                    jobPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    jobListingsPanel.add(jobPanel);
                }

                // Add vertical glue to center the job listings
                jobListingsPanel.add(Box.createVerticalGlue());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Repaint the panel
        jobListingsPanel.revalidate();
        jobListingsPanel.repaint();
    }
    private void setupUserProfileForm(String username) {
        JPanel userProfilePanel = new JPanel(new GridLayout(0, 2));
        JTextField fullNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField locationField = new JTextField();
        JComboBox<String> experienceLevelField = new JComboBox<>(new String[]{"Junior", "Mid-level", "Senior", "Manager"});
        JTextArea educationField = new JTextArea(3, 20);
        JTextArea skillsField = new JTextArea(3, 20);
        JButton saveButton = new JButton("Save Changes");
        JButton backButton = new JButton("Back to Applications"); // Back button
        // Populate fields with user data
        populateUserProfile(username, fullNameField, emailField, locationField, experienceLevelField, educationField, skillsField);
        userProfilePanel.add(saveButton);
        userProfilePanel.add(backButton); // Add the back button to the panel
        userProfilePanel.add(new JLabel("Full Name:"));
        userProfilePanel.add(fullNameField);
        userProfilePanel.add(new JLabel("Email:"));
        userProfilePanel.add(emailField);
        userProfilePanel.add(new JLabel("Location:"));
        userProfilePanel.add(locationField);
        userProfilePanel.add(new JLabel("Experience Level:"));
        userProfilePanel.add(experienceLevelField);
        userProfilePanel.add(new JLabel("Education:"));
        userProfilePanel.add(new JScrollPane(educationField));
        userProfilePanel.add(new JLabel("Skills:"));
        userProfilePanel.add(new JScrollPane(skillsField));


        cardPanel.add(userProfilePanel, "UserProfile");

        saveButton.addActionListener(e -> updateUserProfile(username, fullNameField.getText(), emailField.getText(), locationField.getText(), (String)experienceLevelField.getSelectedItem(), educationField.getText(), skillsField.getText()));
        backButton.addActionListener(e -> {
            setupUserApplicationsForm(username); // Navigate back to user's applications
            cardLayout.show(cardPanel, "UserApplications");
        });
        populateUserProfile(username, fullNameField, emailField, locationField, experienceLevelField, educationField, skillsField);

        cardPanel.add(userProfilePanel, "UserProfile");
    }
    private void populateUserProfile(String username, JTextField fullNameField, JTextField emailField, JTextField locationField, JComboBox<String> experienceLevelField, JTextArea educationField, JTextArea skillsField) {
        try (Connection conn = connectToDatabase()) {
            if (conn != null) {
                String sql = "SELECT FullName, Email, Location, ExperienceLevel, Education, Skills FROM Users WHERE Username = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, username);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        fullNameField.setText(rs.getString("FullName"));
                        emailField.setText(rs.getString("Email"));
                        locationField.setText(rs.getString("Location"));
                        experienceLevelField.setSelectedItem(rs.getString("ExperienceLevel"));
                        educationField.setText(rs.getString("Education"));
                        skillsField.setText(rs.getString("Skills"));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    private void updateUserProfile(String username, String fullName, String email, String location, String experienceLevel, String education, String skills) {
        try (Connection conn = connectToDatabase()) {
            if (conn != null) {
                String sql = "UPDATE Users SET FullName = ?, Email = ?, Location = ?, ExperienceLevel = ?, Education = ?, Skills = ? WHERE Username = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, fullName);
                    pstmt.setString(2, email);
                    pstmt.setString(3, location);
                    pstmt.setString(4, experienceLevel);
                    pstmt.setString(5, education);
                    pstmt.setString(6, skills);
                    pstmt.setString(7, username);
                    int updatedRows = pstmt.executeUpdate();
                    if (updatedRows > 0) {
                        JOptionPane.showMessageDialog(frame, "Profile updated successfully");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Profile update failed");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating profile");
        }
    }
    private void setupUserApplicationsForm(String username) {
        JPanel userApplicationsPanel = new JPanel();
        userApplicationsPanel.setLayout(new BoxLayout(userApplicationsPanel, BoxLayout.Y_AXIS));

        displayUserApplications(username, userApplicationsPanel);

        // Butonları ekleyin
        JButton viewAllJobsButton = new JButton("View All Job Listings");
        JButton editProfileButton = new JButton("Edit Profile");
        JButton logOutButton = new JButton("Log Out");
        JButton backButton = new JButton("Geri Dön");

        backButton.addActionListener(e -> cardLayout.show(cardPanel, "StartScreen"));
        viewAllJobsButton.addActionListener(e -> cardLayout.show(cardPanel, "JobAds"));
        editProfileButton.addActionListener(e -> {
            setupUserProfileForm(username);
            cardLayout.show(cardPanel, "UserProfile");
        });
        logOutButton.addActionListener(e -> cardLayout.show(cardPanel, "Login"));

        // Butonları başvuru paneline ekleyin
        userApplicationsPanel.add(backButton);
        userApplicationsPanel.add(viewAllJobsButton);
        userApplicationsPanel.add(editProfileButton);
        userApplicationsPanel.add(logOutButton);
        JButton listCertificatesButton = new JButton("Sertifikaları Listele");
        JButton listCertificateApplicationsButton = new JButton("Sertifika Başvurularını Listele");

        listCertificatesButton.addActionListener(e -> listCertificates(username));
        listCertificateApplicationsButton.addActionListener(e -> listCertificateApplications(username));

        userApplicationsPanel.add(listCertificatesButton);
        userApplicationsPanel.add(listCertificateApplicationsButton);

        // userApplicationsPanel'i JScrollPane içine alın
        JScrollPane scrollPane = new JScrollPane(userApplicationsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // JScrollPane'i kart paneline ekleyin
        cardPanel.add(scrollPane, "UserApplications");

        // Kartı göster
        cardLayout.show(cardPanel, "UserApplications");
    }
    private void listCertificates(String username) {
        JPanel certificatesPanel = new JPanel();
        certificatesPanel.setLayout(new BoxLayout(certificatesPanel, BoxLayout.Y_AXIS));

        JButton backButton = new JButton("Geri Dön");
        backButton.addActionListener(e -> setupUserApplicationsForm(username));
        certificatesPanel.add(backButton);

        try (Connection conn = connectToDatabase()) {
            String sql = "SELECT CertificateID, Name, Description FROM Certificates WHERE CertificateID NOT IN (SELECT CertificateID FROM UserCertificates WHERE UserID = (SELECT UserID FROM Users WHERE Username = ?))";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    int certificateId = rs.getInt("CertificateID");
                    String name = rs.getString("Name");
                    String description = rs.getString("Description");

                    JPanel certificatePanel = new JPanel();
                    certificatePanel.setLayout(new BoxLayout(certificatePanel, BoxLayout.Y_AXIS));
                    certificatePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    certificatePanel.add(new JLabel("Sertifika Adı: " + name));
                    certificatePanel.add(new JLabel("Açıklama: " + description));

                    JButton enrollButton = new JButton("Kayıt Ol");
                    enrollButton.addActionListener(e -> enrollCertificate(username, certificateId));

                    certificatePanel.add(enrollButton);
                    certificatesPanel.add(certificatePanel);
                    certificatesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(certificatesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        cardPanel.add(scrollPane, "ListCertificates");
        cardLayout.show(cardPanel, "ListCertificates");
    }

    private void enrollCertificate(String username, int certificateId) {
        try (Connection conn = connectToDatabase()) {
            String sql = "INSERT INTO UserCertificates (UserID, CertificateID, EnrollmentDate, Status) VALUES ((SELECT UserID FROM Users WHERE Username = ?), ?, CURRENT_DATE, 'Enrolled')";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setInt(2, certificateId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Sertifikaya başarıyla kayıt oldunuz.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Sertifikaya kayıt olunurken bir hata oluştu.");
        }
    }
    private void listCertificateApplications(String username) {
        JPanel certificateApplicationsPanel = new JPanel();
        certificateApplicationsPanel.setLayout(new BoxLayout(certificateApplicationsPanel, BoxLayout.Y_AXIS));

        JButton backButton = new JButton("Geri Dön");
        backButton.addActionListener(e -> setupUserApplicationsForm(username));
        certificateApplicationsPanel.add(backButton);

        try (Connection conn = connectToDatabase()) {
            String sql = "SELECT c.Name, uc.EnrollmentDate, uc.Status FROM UserCertificates uc JOIN Certificates c ON uc.CertificateID = c.CertificateID WHERE uc.UserID = (SELECT UserID FROM Users WHERE Username = ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    String name = rs.getString("Name");
                    Date enrollmentDate = rs.getDate("EnrollmentDate");
                    String status = rs.getString("Status");

                    JPanel certificatePanel = new JPanel();
                    certificatePanel.setLayout(new BoxLayout(certificatePanel, BoxLayout.Y_AXIS));
                    certificatePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    certificatePanel.add(new JLabel("Sertifika Adı: " + name));
                    certificatePanel.add(new JLabel("Kayıt Tarihi: " + enrollmentDate.toString()));
                    certificatePanel.add(new JLabel("Durum: " + status));

                    certificateApplicationsPanel.add(certificatePanel);
                    certificateApplicationsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(certificateApplicationsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        cardPanel.add(scrollPane, "ListCertificateApplications");
        cardLayout.show(cardPanel, "ListCertificateApplications");
    }
    private void displayUserApplications(String username, JPanel panel) {
        try (Connection conn = connectToDatabase()) {
            if (conn != null) {
                String sql = "SELECT * FROM get_user_applications(?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, username);
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        JPanel jobPanel = new JPanel();
                        jobPanel.setLayout(new BoxLayout(jobPanel, BoxLayout.Y_AXIS));

                        int applicationId = rs.getInt("ApplicationID");

                        jobPanel.add(new JLabel("Title: " + rs.getString("Title")));
                        jobPanel.add(new JLabel("Description: " + rs.getString("Description")));
                        jobPanel.add(new JLabel("Location: " + rs.getString("Location")));
                        jobPanel.add(new JLabel("Type: " + rs.getString("Type")));
                        jobPanel.add(new JLabel("Category: " + rs.getString("Category")));
                        jobPanel.add(new JLabel("Posted Date: " + rs.getDate("PostedDate").toString()));
                        jobPanel.add(new JLabel("Application Status: " + rs.getString("Status")));
                        JButton cancelButton = new JButton("İptal Et");
                        cancelButton.addActionListener(e -> cancelApplication(applicationId, jobPanel, panel));
                        jobPanel.add(cancelButton);

                        jobPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                        panel.add(jobPanel);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void cancelApplication(int applicationId, JPanel jobPanel, JPanel panel) {
        try (Connection conn = connectToDatabase()) {
            if (conn != null) {
                String sql = "DELETE FROM Applications WHERE ApplicationID = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, applicationId);
                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows > 0) {
                        panel.remove(jobPanel);
                        panel.revalidate();
                        panel.repaint();
                        JOptionPane.showMessageDialog(frame, "Başvuru başarıyla iptal edildi");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Başvuru iptali sırasında bir hata oluştu");
        }
    }
    private Connection connectToDatabase() {
        String url = "jdbc:postgresql://localhost:5432/JobSearchApp";
        String user = "postgres";
        String password = "";

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JobSearchApp());
    }
}