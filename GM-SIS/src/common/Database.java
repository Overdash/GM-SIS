package common;

import java.sql.*;

import misc.AlertBox;
import org.controlsfx.dialog.Dialogs;

/**
 * Created by Prodige on 15/01/2017.
 * This class uses a Singleton pattern which ensures that one static instance of the database
 * is running at all times. Avoids creating new connections repeatedly to stop data from going
 * out of sync with the database.
 *
 * @author SE11
 */

public class Database { // Class will use Singleton Pattern to ensure one INSTANCE of the DB at all times

    private static final Database INSTANCE = new Database();
    private Connection con = null;

    private final String DRIVER = "org.sqlite.JDBC";
    private final String URL = "jdbc:sqlite:resources/gmsisDB.sqlite";

    private Database(){
        connect();
    }

    public static Database getInstance(){
        return INSTANCE;
    }

    public Connection getCon(){
        return this.con;
    }

    /**
     * Establishes a valid database connection
     *
     * @return connection to use while system is running.
     */
    private Connection connect(){
        if(con != null)
            return con;
        //System.out.println("Here");
        try{
            Class.forName(DRIVER);
            con = DriverManager.getConnection(URL);
           // System.out.println("Caught driver");
        } catch (ClassNotFoundException ce){
            popError("Driver not found!", ce);
            ce.getException();
        } catch (SQLException se){
            popError("Connection Failed. Verify URL.", se);
            se.printStackTrace();
        }

        return con;
    }

    /**
     * Closes DB connection. Shouldn't need to be called.
     */
    public void close(){
        try{
            if(con != null)
                con.close();
        } catch (SQLException se){
            System.out.println("No current connection.");
            se.printStackTrace();
        }
    }

    public void update(String sql){
        try{
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException se){
            System.out.println("SQL Failure");
            se.printStackTrace();
        } catch (NullPointerException ne){
            System.out.println("Uninitialised Connection or Updating a non-existing item.");
        }
    }

    public ResultSet query(String sql){
        ResultSet rs = null;
        try{
            Statement stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
        } catch (SQLException se){
            System.out.println("SQL Failure");
            se.printStackTrace();
        }
        return rs;
    }

    /*public void testDB(){ //In-case Database starts acting up and we need to test it

        Statement statement;
        try {
            statement = con.createStatement();
            statement.setQueryTimeout(10);
            statement.executeUpdate("drop table if exists `test`");
            statement.executeUpdate("create table `test` (`id` integer, `name` string)");
            statement.executeUpdate("insert into `test` values('1', 'test 1')");
            statement.executeUpdate("insert into `test` values('2', 'test 2')");

            ResultSet rs = statement.executeQuery("select * from `test`");

            System.out.println("id	name");
            while(rs.next()){
                System.out.println(rs.getInt("id")+"	"+rs.getString("name"));
            }
        }
        catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        finally {
            if (con != null){
                try{
                    con.close();
                }
                catch(SQLException ex){
                    System.err.println(ex.getMessage());
                }
            }
        }
    }*/

    private void popError(String msg, Exception e){// Will show a popup message
        AlertBox.error("Error connecting to database!",msg, e);
    }

}
