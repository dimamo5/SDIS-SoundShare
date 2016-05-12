import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;

/* A database implementation using JBDC(Java DataBase Connectivity API)
   > No left open connections: each query requires establishing a db connection which is closed at the end;
   > ^ TODO: Discuss if this is the best approach
*/

/* User's database
   |            User            |
   |____________________________|
   |username: char(15)          |
   |password: char(32) -> sha256|
 */

public class Database {

    private String db_name = null;
    private Connection connection = null;

    private enum Query_types {INSERT, SELECT, DELETE, UPDATE}

    ;

    public Database(String db_name) {
        this.db_name = db_name;
        init_db(db_name);
    }

    private void init_db(String db_name) {
        connect_to_db(db_name);
        create_table();
        close_connection();
    }

    /*
    Creates connection to the database
    */
    private void connect_to_db(String db_name) {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + db_name);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Connected to database successfully");
    }

    /*
    Creates User's table
     */
    private void create_table() {
        Statement stmt = null;

        try {
            stmt = this.connection.createStatement();

            String sql = "CREATE TABLE USERS " +
                    "(ID             INTEGER        PRIMARY KEY    AUTOINCREMENT," +
                    " USERNAME       CHAR(15)                   NOT NULL, " +
                    " PASSWORD       CHAR(32)                   NOT NULL)";
            stmt.executeUpdate(sql);
            stmt.close();
            this.connection.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    /*
    Insert's an user in the database
    */
    //TODO : Verificar se username já existe na base de dados
    private boolean insert_user(String username, String password) {
        byte[] hashed = null;
        String sql = null;

        if ((hashed = get_sha256_hash(password)) == null) {
            System.err.println("Error hashing password");
            return false;
        }

        sql = "INSERT INTO USERS (USERNAME,PASSWORD) VALUES (?,?);";

        String values[] = {username, new String(hashed)};

        return (Boolean) execute_sql(Query_types.INSERT.name(), sql, values);
    }

    private void close_connection() {
        try {
            this.connection.close();
            System.out.println("Closed connection");
        } catch (Exception e) {
            System.err.println("Error closing connection");
            e.printStackTrace();
        }
    }


    private ArrayList select_user_by_username(String username) {
        String sql;
        String values[] = {username};
        sql = "SELECT * FROM USERS WHERE USERNAME = ?";

        return (ArrayList) execute_sql(Query_types.SELECT.name(), sql, values);
    }

    /* For log in purpose prolly */
    private ArrayList select_user_by_credentials(String username, String password) {
        String sql;
        String values[] = {username, password};
        sql = "SELECT * FROM USERS WHERE USERNAME = ? AND PASSWORD = ?";

        return (ArrayList) execute_sql(Query_types.SELECT.name(), sql, values);
    }


    /*
    Execute a query using prepared statement
    */
    private Object execute_sql(String query_type, String sql_stmt, String[] values) {

        PreparedStatement pstmt = null;
        boolean ret = false;
        ArrayList<ArrayList<String>> users = new ArrayList<>();


        try {
            connect_to_db(this.db_name);

            this.connection.setAutoCommit(false);
            pstmt = this.connection.prepareStatement(sql_stmt); //SQL-Injection prevention

            for (int i = 0; i < values.length; i++) {
                pstmt.setString(i + 1, values[i]);
            }

            //TODO Add 2 missing cases (DELETE + UPDATE)
            if (query_type.equals(Query_types.INSERT.name())) {
                ret = pstmt.executeUpdate() != 0;
            } else if (query_type.equals(Query_types.SELECT.name())) {
                ResultSet rs = pstmt.executeQuery();

                if (!rs.isBeforeFirst()) {
                    System.out.println("No data returned from query");
                }

                while (rs.next()) {
                    ArrayList<String> user = new ArrayList<>();

                    user.add(rs.getString("USERNAME"));
                    user.add(rs.getString("PASSWORD"));
                    users.add(user);
                }
            }


            pstmt.close();
            this.connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        close_connection();

        //Return section
        if (query_type.equals(Query_types.INSERT.name())) {
            System.out.println("Ret value from executing query: " + ret);
            return ret;
        } else if (query_type.equals(Query_types.SELECT.name())) {
            return users;
        } else return false;
    }


    private byte[] get_sha256_hash(String to_hash) {
        byte[] digest = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(to_hash.getBytes("UTF-8"));  // Change this to "UTF-16" if needed
            digest = md.digest();
        } catch (Exception e) {
            System.err.println("get_sha256_hash Method Exception: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }

        return digest;
    }


    public static void main(String args[]) {

        Database db = new Database("Users.db");

        db.insert_user("Manuel123456789", "lol");

        ArrayList<ArrayList<String>> users = db.select_user_by_credentials("Manuel123456789", new String(db.get_sha256_hash("lol")));
        try {
            for (ArrayList<String> user : users)
                System.out.println(user.get(0) + " " + user.get(1));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}