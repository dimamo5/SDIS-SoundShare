package database;

import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

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
    private static Database instance = null;

    private enum Query_types {INSERT, SELECT, DELETE, UPDATE}

    ;

    public Database(String db_name) {
        this.db_name = db_name;
        init_db(db_name);
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database("soundshare");
        }
        return instance;
    }

    private void init_db(String db_name) {
        connect_to_db(db_name);
        //create_table();
        close_connection();
    }

    /*
    Creates connection to the database
    */
    private void connect_to_db(String db_name) {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + System.getProperty("user.dir") + "/resources/" + db_name + ".db");
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
    public boolean insert_user(String username, String password) {
        String hashed = null;
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


    public ArrayList select_user_by_username(String username) {
        String sql;
        String values[] = {username};
        sql = "SELECT * FROM USERS WHERE USERNAME = ?";

        return (ArrayList) execute_sql(Query_types.SELECT.name(), sql, values);
    }

    /* For log in purpose prolly */
    public String select_user_by_credentials(String username, String password) {
        String sql;
        String values[] = {username, get_sha256_hash(password)};

        sql = "SELECT * FROM USERS WHERE USERNAME = ? AND PASSWORD = ?";

        ArrayList<ArrayList<String>> arr = (ArrayList) execute_sql(Query_types.SELECT.name(), sql, values);
        try {
            if (arr.get(0).get(1).equals(username)) {
                return generateToken(Integer.parseInt(arr.get(0).get(0)));
            } else return "ERROR";
        }
        catch (IndexOutOfBoundsException i) {
            return "ERROR";
        }
    }

    public String generateToken(int id) {
        String sql = "UPDATE USERS SET ACCESSTOKEN= ? WHERE id=?";
        String token = UUID.randomUUID().toString();
        String values[] = {token, String.valueOf(id)};

        this.execute_sql(Query_types.UPDATE.name(), sql, values);

        return token;
    }

    public boolean verifyToken(String token) {
        String sql = "SELECT * FROM USERS WHERE ACCESSTOKEN =?";
        String[] values = {token};
        ArrayList result= (ArrayList) this.execute_sql(Query_types.SELECT.name(), sql, values);
        return result.size()!=0;
    }

    public boolean deleteToken(String token) {
        String sql = "UPDATE USERS SET ACCESSTOKEN=NULL WHERE ACCESSTOKEN =?";
        String[] values = {token};
        return (Boolean) this.execute_sql(Query_types.DELETE.name(), sql, values);

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

            if (query_type.equals(Query_types.INSERT.name())) {
                ret = pstmt.executeUpdate() != 0;
            } else if (query_type.equals(Query_types.SELECT.name())) {
                ResultSet rs = pstmt.executeQuery();

                if (!rs.isBeforeFirst()) {
                    System.out.println("No data returned from query");
                }

                while (rs.next()) {
                    ArrayList<String> user = new ArrayList<>();

                    user.add(rs.getString("ID"));
                    user.add(rs.getString("USERNAME"));
                    user.add(rs.getString("PASSWORD"));
                    user.add(rs.getString("ACCESSTOKEN"));
                    users.add(user);
                }
            } else if (query_type.equals(Query_types.UPDATE.name())) {
                ret = pstmt.executeUpdate() != 0;
            } else if (query_type.equals(Query_types.DELETE.name())) {
                ret = pstmt.executeUpdate() != 0;
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
        } else if (query_type.equals(Query_types.DELETE.name())) {
            return ret;
        } else if (query_type.equals(Query_types.UPDATE.name())) {
            return ret;
        } else return false;
    }


    private String get_sha256_hash(String to_hash) {
        byte[] digest = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(to_hash.getBytes("UTF-8"));  // Change this to "UTF-16" if needed
            digest = md.digest();
            return String.format("%064x", new java.math.BigInteger(1, digest));
        } catch (Exception e) {
            System.err.println("get_sha256_hash Method Exception: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


    public static void main(String args[]) {

        Database db = Database.getInstance();
        db.insert_user("teste", "lol");

        /*String s=db.generateToken(2);
        System.out.println(s);
        System.out.println("Encontrou:" + db.verifyToken(s));
        System.out.println("Eliminou:" + db.deleteToken(s));
        System.out.println("Encontrou:" + db.verifyToken(s));*/


        /*ArrayList<ArrayList<String>> users = db.select_user_by_credentials("Manuel123456789", new String(db.get_sha256_hash("lol")));
        try {
            for (ArrayList<String> user : users)
                System.out.println(user.get(0) + " " + user.get(1));

        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

}