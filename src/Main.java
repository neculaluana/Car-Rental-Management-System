import javax.swing.*;

public class Main {
    public static void main(String[] args) {

            DbUtils.getConnection();
        JFrame parentFrame = new JFrame();
        LoginPage loginPage = new LoginPage(parentFrame);

    }
}