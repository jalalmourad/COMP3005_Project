import java.sql.*;
import java.util.Scanner;

public class Main {


    //
    // Member Functions
    //

    // registration for new members
    public void addMember(String first_name, String last_name, Date date_joined, int member_weight, int member_height, Time member_time, String track_exercise_routine) {

        String url = "jdbc:postgresql://localhost:5432/Project";
        String user = "postgres";
        String password = "postgres";

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            String insert = "INSERT INTO members(first_name, last_name, date_joined, member_weight, member_height, member_time, track_exercise_routine) VALUES(?,?,?,?,?,?,?)";
            try (PreparedStatement prepare = connection.prepareStatement(insert)) {
                prepare.setString(1, first_name);
                prepare.setString(2, last_name);
                prepare.setDate(3, date_joined);
                prepare.setInt(4, member_weight);
                prepare.setInt(5, member_height);
                prepare.setTime(6, member_time);
                prepare.setString(7, track_exercise_routine);
                prepare.executeUpdate();
                System.out.println("Data is inserted\n");
            }


        } catch (Exception e) {
        }
    }


    //Checks the availability of the trainer
    public void checkAvailability(Time appointment_time, Date appointment_date) {

        String url = "jdbc:postgresql://localhost:5432/Project";
        String user = "postgres";
        String password = "postgres";

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            String insert = "SELECT t.trainer_id " + "FROM Trainers t " + "LEFT JOIN Schedule schedule ON t.trainer_id = schedule.trainer_id " +
                    "WHERE NOT ( schedule.appointment_time = ? AND schedule.appointment_date = ?)";
            try (PreparedStatement prepare = connection.prepareStatement(insert)) {
                prepare.setTime(1, appointment_time);
                prepare.setDate(2, appointment_date);
                prepare.executeUpdate();
                System.out.println("Data is inserted/n");
            }

        } catch (Exception e) {
        }
    }

    //Schedules a training session after checking the Trainer's availability
    public void scheduleManagement(int member_id) {

        String url = "jdbc:postgresql://localhost:5432/Project";
        String user = "postgres";
        String password = "postgres";

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            // first were gonna display the entire schedule by availability (only classes that are not full)
            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT * FROM schedule WHERE (SELECT COUNT(*) FROM member_booking WHERE schedule.schedule_id = member_booking.schedule_id) < max_members");
            ResultSet resultSet = statement.getResultSet();

            System.out.println("\nAvailable Classes:\n\n");

            while(resultSet.next())
            {
                System.out.print("id: " + resultSet.getInt("schedule_id") + ", Trainer: ");
                System.out.print(resultSet.getInt("trainer_id") + ", Time: ");
                System.out.print(resultSet.getTime("appointment_time") + ", Date: ");
                System.out.print(resultSet.getDate("appointment_date") + ", Room #: ");
                System.out.print(resultSet.getInt("appointment_room") + ", Max Members: ");
                System.out.print(resultSet.getInt("max_members") + ", Fee: ");
                System.out.print(resultSet.getInt("joining_fee") + "\n");
            }

            Scanner in = new Scanner(System.in);
            System.out.println("\nplease input the id of the class you'd like to join\n");
            String c = in.nextLine();
            int userChoice = Integer.parseInt(c);

            statement = connection.createStatement();
            statement.executeQuery("SELECT COUNT(*) FROM schedule WHERE (SELECT COUNT(*) FROM member_booking WHERE schedule.schedule_id = member_booking.schedule_id) < max_members and schedule_id = '" + userChoice + "'");
            resultSet = statement.getResultSet();

            resultSet.next();

            if (resultSet.getInt("count") == 0)
            {
                System.out.println("\nCould not find appointment matching specified\n");
                return;
            }

            statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO member_booking (member_id, schedule_id) VALUES(" + member_id+ "', '" + userChoice + "')");

            statement = connection.createStatement();
            statement.executeQuery("SELECT fee FROM schedule WHERE (SELECT COUNT(*) FROM member_booking WHERE schedule.schedule_id = member_booking.schedule_id) < max_members and schedule_id = '" + userChoice + "'");
            resultSet = statement.getResultSet();
            resultSet.next();

            statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO billing (member_id, amount, schedule_id) VALUES(" + member_id+ "', '" + resultSet.getInt("joining_fee") + "','" + userChoice + "')");

            connection.close();

        } catch (Exception e) {
        }
    }

    // update personal information -> fitness goals, health metrics etc.
    public void manageProfile(int member_id) {
        Scanner in = new Scanner(System.in);

        System.out.println("What would you like to update?\n1. Weight\n2. Time for exercise completions (running)\n3. Exercise routine\n4. Quit");

        Boolean select = false;
        while (select == false) {
            String c = in.nextLine();
            int userChoice = Integer.parseInt(c);

            if (userChoice == 1) {
                System.out.println("What is your new weight?");
                String w = in.nextLine();

                int weight = Integer.parseInt(w);
                updateWeight(member_id, weight);
            } else if (userChoice == 2) {
                System.out.println("What is your new time?");
                String t = in.nextLine();

                int time = Integer.parseInt(t);
                updateTime(member_id, time);
            } else if (userChoice == 3) {
                System.out.println("What is your new exercise routine?");
                String routine = in.nextLine();

                updateExcerciseRoutine(member_id, routine);
            } else if (userChoice == 4) {
                break;
            } else {
                System.out.println("Invalid option. Please select either 1, 2, 3 or 4");
            }
        }
    }

    /*
     * Helper functions for manage profile
     */

    public void updateWeight(int member_id, int member_weight) {
        String url = "jdbc:postgresql://localhost:5432/Project";
        String user = "postgres";
        String password = "postgres";

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            String query = "UPDATE members SET member_weight = ? WHERE member_id = ?";
            try (PreparedStatement prepare = connection.prepareStatement(query)) {
                prepare.setInt(1, member_id);
                prepare.setInt(2, member_weight);

                prepare.executeUpdate();
            }
        } catch (Exception e) {
        }
    }

    public void updateTime(int member_id, int member_time) {
        String url = "jdbc:postgresql://localhost:5432/Project";
        String user = "user";
        String password = "password";

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            String query = "UPDATE members SET member_time = ? WHERE member_id = ?";
            try (PreparedStatement prepare = connection.prepareStatement(query)) {
                prepare.setInt(1, member_id);
                prepare.setInt(2, member_time);

                prepare.executeUpdate();
            }
        } catch (Exception e) {
        }
    }

    public void updateExcerciseRoutine(int member_id, String routine) {
        String url = "jdbc:postgresql://localhost:5432/Project";
        String user = "postgres";
        String password = "postgres";

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            String query = "UPDATE members SET track_exercise_routine = ? WHERE member_id = ?";
            try (PreparedStatement prepare = connection.prepareStatement(query)) {
                prepare.setInt(1, member_id);
                prepare.setString(2, routine);

                prepare.executeUpdate();
            }
        } catch (Exception e) {
        }
    }

    // a function that displays exercise routines, fitness achievements, and health statistics based on a given user id
    public void displayDashboard(int member_id) {
        String url = "jdbc:postgresql://localhost:5432/Project";
        String user = "postgres";
        String password = "postgres";

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT * FROM members WHERE id = '" + member_id + "';");
            ResultSet resultSet = statement.getResultSet();

            // display user by id and name
            System.out.print("Dashboard for user " + member_id + " : ");
            System.out.print(resultSet.getString("first_name") + " ");
            System.out.print(resultSet.getString("last_name") + "\n\n");

            // display user routine
            System.out.print("Exercise Routine:\n");
            System.out.print(resultSet.getString("track_exercise_routine") + "\n\n");

            // display the users health statistics
            System.out.print("Fitness Statistics:\n");
            System.out.print(resultSet.getInt("member_height") + "\t");
            System.out.print(resultSet.getInt("member_weight") + "\t");
            System.out.print(resultSet.getInt("member_time") + "\t");

            // get the goals completed by this user
            statement.executeQuery("SELECT * FROM goals WHERE id='" + member_id + "' and is_achieved = 'true';");
            resultSet = statement.getResultSet();

            System.out.print("Achieved Goals:\n");
            while (resultSet.next()) {
                System.out.print(resultSet.getString("goal_description") + "\t");
                System.out.print(resultSet.getString("achieve_by_date") + "\t");
                System.out.print(resultSet.getInt("start_weight") + "\t");
                System.out.print(resultSet.getInt("goal_weight") + "\t");
                System.out.print(resultSet.getInt("start_time") + "\t");
                System.out.print(resultSet.getInt("goal_time") + "\t");
            }
            connection.close();
        } catch (Exception e) {
        }
    }

    //
    // Trainer Functions
    //

    public void scheduleManagement(int trainer_id, Time appointment_time, Date appointment_date, Date appointment_room) {
        String url = "jdbc:postgresql://localhost:5432/Project";
        String user = "postgres";
        String password = "postgres";

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            // First we check over the all the current schedule entries, if the trainer
            // already has a booking at that time and date, or the room has a booking at
            // that time or date, we exit since we cant overlap our bookings.
            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT COUNT(*) FROM Schedule WHERE (trainer_id = " + trainer_id + " and appointment_time = '" + appointment_time + "' and appointment_date = '" + appointment_date + "') or (appointment_date = '" + appointment_date + "' and appointment_time = '" + appointment_time + "' and appointment_room = '" + appointment_room + "');");
            ResultSet resultSet = statement.getResultSet();

            resultSet.next();
            if (resultSet.getInt("count") > 0) {
                System.out.print("Unable to book at specified time due to conflicts in the schedule:\n");
            }

            // If available we then book for the trainer that time and space.
            statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO Schedule ( trainer_id, appointment_time, appointment_date, appointment_room) VALUES ("  +trainer_id + ", '" + appointment_time + "', '" + appointment_date + "', '" + appointment_room + "');");

            connection.close();

        } catch (Exception e) {
        }
    }

    public void memberProfileViewing(String first_name, String last_name) {

        String url = "jdbc:postgresql://localhost:5432/Project";
        String user = "postgres";
        String password = "postgres";

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);


            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT * FROM members WHERE first_name= '" + first_name + "' AND last_name = ' " + last_name + "' ;");
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                System.out.print(resultSet.getInt("member_id") + "\t");
                System.out.print(resultSet.getString("last_name") + "\t");
                System.out.print(resultSet.getString("last_name") + "\t");
                System.out.print(resultSet.getString("date_joined") + "\t");
                System.out.print(resultSet.getString("fee") + "\t");
                System.out.print(resultSet.getString("member_weight") + "\t");
                System.out.print(resultSet.getString("member_height") + "\t");
                System.out.print(resultSet.getString("member_time") + "\t");
                System.out.println(resultSet.getString("track_exercise_routine"));
            }
        } catch (Exception e) {
        }
    }

    //Returns the most recent date the equipment had maintenance
    public void equipmentMaintenanceMonitoring() {

        String url = "jdbc:postgresql://localhost:5432/Project";
        String user = "postgres";
        String password = "postgres";

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT * FROM Equipment");
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                System.out.print(resultSet.getInt("equipment_id") + "\t");
                System.out.print(resultSet.getString("equipment_name") + "\t");
                System.out.print(resultSet.getDate("equipment_maintenance_date"));
            }
        } catch (Exception e) {
        }
    }

    //
    // Administrator Functions
    //

    /**
     * Schedule id - used to identify unique schedules
     * member id - member that is using the room
     * trainer id - trainer that is using the room
     * appointment time - time of room booking --> to consider: will this be a time range? i.e 8:00 - 9:00
     * appointment date - date of room booking
     * appointment room - int to identify room num
     * <p>
     * Administrator should have option to cancel a room booking -> delete a row of schedule_id, member_id, trainer_id, appointment_time, appointment_date and appointment_room
     * Administrator should have option to reschedule room booking -> update time and/or date of room booking and/or appointment_room
     */
    public void manageRoomBooking(int schedule_id) {
        Scanner in = new Scanner(System.in);

        System.out.println("1. Cancel booking\n2. Reschedule Booking");

        Boolean select = false;
        while (select == false) {
            String c = in.nextLine();
            int userChoice = Integer.parseInt(c);
            if (userChoice == 1) {
                cancelRoomBooking(schedule_id);
                select = true;
            } else if (userChoice == 2) {
                System.out.println("New booking time (HH:mm:ss): ");
                String time = in.nextLine();

                System.out.println("New booking date (YYYY-MM-DD): ");
                String date = in.nextLine();

                System.out.println("New booking room: ");
                String r = in.nextLine();
                int room = Integer.parseInt(r);

                rescheduleBookingTime(schedule_id, time);
                rescheduleBookingDate(schedule_id, date);
                rescheduleBookingRoom(schedule_id, room, date);
                select = true;
            } else {
                System.out.println("Invalid option. Please select either 1 or 2");
            }
        }
    }

    /*
    Helper functions for room booking management
    */

    // delete room booking
    public void cancelRoomBooking(int schedule_id){
        String url = "jdbc:postgresql://localhost:5432/Project";
        String user = "postgres";
        String password = "postgres";

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            String query = "DELETE FROM Schedule WHERE schedule_id = ?";
            try (PreparedStatement prepare = connection.prepareStatement(query)) {
                prepare.setInt(1, schedule_id);

                prepare.executeUpdate();
            }

            statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM member_booking WHERE schedule_id = '" + schedule_id + "'");

            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // time must be of hrs:mins:secs format
    public void rescheduleBookingTime (int schedule_id, String time){
        String url = "jdbc:postgresql://localhost:5432/Project";
        String user = "postgres";
        String password = "postgres";

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            String query = "UPDATE Schedule SET booking_time = ? WHERE schedule_id = ?";
            try (PreparedStatement prepare = connection.prepareStatement(query)) {
                prepare.setInt(1, schedule_id);
                prepare.setTime(2, java.sql.Time.valueOf(time));

                prepare.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rescheduleBookingDate (int schedule_id, String date){
        String url = "jdbc:postgresql://localhost:5432/Project";
        String user = "postgres";
        String password = "postgres";

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            String query = "UPDATE Schedule SET booking_date = ? WHERE schedule_id = ?";
            try (PreparedStatement prepare = connection.prepareStatement(query)) {
                prepare.setInt(1, schedule_id);
                prepare.setDate(2, java.sql.Date.valueOf(date));

                prepare.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isRoomBooked(String date, int room_num){
        String url = "jdbc:postgresql://localhost:5432/Project";
        String user = "postgres";
        String password = "postgres";

        try{
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);

            String availabilityQuery = "SELECT COUNT(*) FROM Schedule WHERE booking_date = ? AND room_number = ?";
            try(PreparedStatement p = conn.prepareStatement(availabilityQuery)){
                p.setDate(1, java.sql.Date.valueOf(date));
                p.setInt(2, room_num);

                try(ResultSet results = p.executeQuery()){
                    results.next();
                    int bookingCount = results.getInt(1);
                    return bookingCount > 0;
                }
            }

        }
        catch(Exception e){
            e.printStackTrace();
            // assume room booked in the case of any errors
            return true;
        }
    }

    public void rescheduleBookingRoom (int schedule_id, int room_num, String date){
        String url = "jdbc:postgresql://localhost:5432/Project";
        String user = "poster";
        String password = "postgres";

        if(isRoomBooked(date, room_num)){
            System.out.println("Room is booked for this time slot!");
        }
        else{
            try {
                Class.forName("org.postgresql.Driver");
                Connection connection = DriverManager.getConnection(url, user, password);

                String query = "UPDATE Schedule SET room_number = ? WHERE schedule_id = ?";
                try (PreparedStatement prepare = connection.prepareStatement(query)) {
                    prepare.setInt(1, schedule_id);
                    prepare.setInt(2, room_num);

                    prepare.executeUpdate();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // Updates the class room number, Date and Time.
    public void classScheduleUpdate ( int schedule_id, int appointment_room, Date appointment_date, Time appointment_time ){
        String url = "jdbc:postgresql://localhost:5432/Project";
        String user = "postgres";
        String password = "postgres";

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            String query = "UPDATE Schedule SET appointment_room = ? , appointment_date = ? , appointment_time = ? where schedule_id = ?";
            try (PreparedStatement prepare = connection.prepareStatement(query)) {
                prepare.setInt(1, appointment_room);
                prepare.setDate(2, appointment_date);
                prepare.setTime(3, appointment_time);
                prepare.setInt(4, schedule_id);

                prepare.executeUpdate();
            }
        } catch (Exception e) {
        }
    }

    // Here we will process billing once daily and if any members have join dates that occurred
    // on the same day of the month as today's date they will be billed
    public void BillingAndPayment(Date current_date)
    {
        String url = "jdbc:postgresql://localhost:5432/Project";
        String user = "postgres";
        String password = "postgres";

        try
        {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT * FROM billing");
            ResultSet resultSet = statement.getResultSet();

            while (resultSet.next())
            {
                System.out.print("Billing member: ");
                System.out.print(resultSet.getInt("member_id") + "\n");
                System.out.print(resultSet.getInt("amount") + "\n");
                // here we would do integrated billing by passing a users payment information into the related service
            }

            connection.close();
        }
        catch (Exception e) {}
    }

    public static void main (String[]args)
    {
        // store the url, users name and users password to connect to our db
        String url = "jdbc:postgresql://localhost:5432/Project";

        Scanner in = new Scanner(System.in);

        System.out.println("Enter username");
        String user = in.nextLine();

        System.out.println("Enter password");
        String password = in.nextLine();


        while (true) {
            break;
        }


        in.close();
    }
}


