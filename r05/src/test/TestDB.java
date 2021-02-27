package test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;
import java.util.stream.Stream;

public class TestDB
{
    public static void main(String[] args) throws IOException, ClassNotFoundException
    {
        try
        {
            runTest();
        }
        catch (SQLException e)
        {
            for (Throwable t : e)
                t.printStackTrace();
        }
    }

    public static void runTest() throws SQLException, IOException, ClassNotFoundException
    {
        Class.forName("org.apache.derby.client.ClientAutoloadedDriver");

        try (Connection conn = getConnection();
             Statement stat = conn.createStatement())
        {
            stat.executeUpdate("CREATE TABLE Greetings (Message CHAR(20))");
            stat.executeUpdate("INSERT INTO Greetings VALUES ('Witaj, świecie!')");

            try (ResultSet result = stat.executeQuery("SELECT * FROM Greetings"))
            {
                if (result.next())
                    System.out.println(result.getString(1));
            }
            stat.executeUpdate("DROP TABLE Greetings");
        }
    }

    public static Connection getConnection() throws SQLException, IOException
    {
        Properties props = new Properties();
        try(InputStream in =
                    Files.newInputStream(Paths.get("database.properties")))
        {
            props.load(in);
        }

        String url = props.getProperty("jdbc.url");
        String username = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");

        Stream<Driver> driverStream = DriverManager.drivers();
        return DriverManager.getConnection(url, username, password);
    }
}
