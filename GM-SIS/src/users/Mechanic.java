package users;

import common.Database;
import misc.AlertBox;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Created by Prodige on 21/01/2017.
 */
public class Mechanic extends Employee{ //TODO Re-model this class.

    private double rate;

    public Mechanic(ResultSet rs){
        super(rs);
        try {
            rate = rs.getDouble("Rate");
        } catch (SQLException se){
            se.printStackTrace();
        }
    }

    public double getRate() {
        return rate;
    }

    public double calCost(int hours){
        return hours * rate;
    }

    public boolean checkMax(LocalDate date, LocalDate end, int hours){
        ChronoUnit unit = ChronoUnit.DAYS;
        long duration = date.until(end,unit);
        int sundays = (int)Math.ceil(duration/7);
        int saturdays = Math.round(duration/6);
        //System.out.println(duration);
        //System.out.println(sundays + " sat: " + saturdays);
        if(sundays >= 1)
            duration -= sundays;
        if(saturdays >= 1)
            duration -= saturdays;

        int totalHours = ((int)duration)*9 + saturdays*3;
        //System.out.println(totalHours);
        return totalHours >= hours;
    }

    //public static double calCost(int hours, double rate)

    public static boolean isAvailable(LocalDate date, LocalTime time, int mechID){
        ChronoUnit unit = ChronoUnit.MINUTES;
        String query = "SELECT BookingTime FROM Bookings WHERE EmployeeID = "+ mechID +
                " AND BookingDate = '" + date + "'";
        ResultSet rs = Database.getInstance().query(query);
        try {
            while(rs.next()) {
                LocalTime dateFound = LocalTime.parse(rs.getString(1));
                //System.out.println(time.until(dateFound, unit));
                if (time.until(dateFound, unit) > -30 && time.until(dateFound,unit) < 30)
                    return false;
            }
        } catch (SQLException se) {
            se.printStackTrace();
            AlertBox.error("SQL Error",
                    "Cannot check mechanic availability: Mechanic's Employee ID: " + mechID, se);
        }
        return true;
    }

    //Implement max hours methods to calculate the max hours for a booking
}
