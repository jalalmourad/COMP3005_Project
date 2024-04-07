import java.sql.*;
import java.util.Scanner;

public class Main {


    //
    // Member Functions
    //

    // registration for new members
    public static void addMember(String url, String user, String password, String first_name, String last_name, Date date_joined, int member_weight, int member_height, int member_time, String track_exercise_routine) {

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
                prepare.setInt(6, member_time);
                prepare.setString(7, track_exercise_routine);
                prepare.executeUpdate();
                System.out.println("Data is inserted\n");
            }


        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }


    //Checks the availability of the trainer
    public static void checkAvailability(String url, String user, String password, Time appointment_time, Date appointment_date) {


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
            System.out.println("Error: " + e);
        }
    }

    //Schedules a training session after checking the Trainer's availability
    public static void scheduleManagement(String url, String user, String password, int member_id) {

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
            statement.executeUpdate("INSERT INTO member_booking (member_id, schedule_id) VALUES(" + member_id+ ", " + userChoice + ")");

            statement = connection.createStatement();
            statement.executeQuery("SELECT joining_fee FROM schedule WHERE schedule_id = " + userChoice + "");
            resultSet = statement.getResultSet();
            resultSet.next();

            statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO billing (member_id, amount, schedule_id) VALUES(" + member_id+ ", " + resultSet.getInt("joining_fee") + "," + userChoice + ")");

            connection.close();

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // update personal information -> fitness goals, health metrics etc.
    public static void manageProfile(String url, String user, String password, int member_id) {
        
        try
        {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT COUNT(*) FROM members WHERE member_id = '" + member_id + "';");
            ResultSet resultSet = statement.getResultSet();

            resultSet.next();

            if (resultSet.getInt("count") == 0)
            {
                System.out.println("No member with user id: " + member_id + " exists\n");
                return;
            }

            connection.close();
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e);
            return;
        }
        
        
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("What would you like to update?\n1. Weight\n2. Time for exercise completions (running)\n3. Exercise routine\n4. Quit");

            String c = in.nextLine();
            int userChoice = Integer.parseInt(c);

            if (userChoice == 1) {
                System.out.println("What is your new weight?");
                String w = in.nextLine();

                int weight = Integer.parseInt(w);
                updateWeight( url, user, password, member_id, weight);
            } else if (userChoice == 2) {
                System.out.println("What is your new time?");
                String t = in.nextLine();

                int time = Integer.parseInt(t);
                updateTime(url,  user,  password, member_id, time);
            } else if (userChoice == 3) {
                System.out.println("What is your new exercise routine?");
                String routine = in.nextLine();

                updateExcerciseRoutine(url,  user,  password, member_id, routine);
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

    public static void updateWeight(String url, String user, String password, int member_id, int member_weight) {

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            String query = "UPDATE members SET member_weight = ? WHERE member_id = ?";
            try (PreparedStatement prepare = connection.prepareStatement(query)) {
                prepare.setInt(1, member_weight);
                prepare.setInt(2, member_id);


                prepare.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public static void updateTime(String url, String user, String password, int member_id, int member_time) {
        

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            String query = "UPDATE members SET member_time = ? WHERE member_id = ?";
            try (PreparedStatement prepare = connection.prepareStatement(query)) {
                prepare.setInt(1, member_time);
                prepare.setInt(2, member_id);


                prepare.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public static void updateExcerciseRoutine(String url, String user, String password, int member_id, String routine) {
        

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            String query = "UPDATE members SET track_exercise_routine = ? WHERE member_id = ?";
            try (PreparedStatement prepare = connection.prepareStatement(query)) {
                prepare.setString(1, routine);
                prepare.setInt(2, member_id);


                prepare.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // a function that displays exercise routines, fitness achievements, and health statistics based on a given user id
    public static void displayDashboard(String url, String user, String password, int member_id) {

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT * FROM members WHERE member_id = '" + member_id + "';");
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
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
            }
            // get the goals completed by this user
            statement.executeQuery("SELECT * FROM goals WHERE member_id='" + member_id + "' and is_achieved = 'true';");
            resultSet = statement.getResultSet();

            System.out.print("\nAchieved Goals:\n");
            while (resultSet.next()) {
                System.out.print(resultSet.getString("goal_description") + "\t");
                System.out.print(resultSet.getString("achieve_by_date") + "\t");
                System.out.print(resultSet.getInt("start_weight") + "\t");
                System.out.print(resultSet.getInt("goal_weight") + "\t");
                System.out.print(resultSet.getInt("start_time") + "\t");
                System.out.print(resultSet.getInt("goal_time") + "\t");
                System.out.println("\n");
            }
            connection.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    //
    // Trainer Functions
    //

    
    public static void scheduleManagement(String url, String user, String password, int trainer_id, Time appointment_time, Date appointment_date, int appointment_room) {

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
            else{
                // If available we then book for the trainer that time and space.
                statement = connection.createStatement();
                statement.executeUpdate("INSERT INTO Schedule ( trainer_id, appointment_time, appointment_date, appointment_room) VALUES ("  +trainer_id + ", '" + appointment_time + "', '" + appointment_date + "', '" + appointment_room + "');");

                connection.close();
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public static void memberProfileViewing(String url, String user, String password, String first_name, String last_name) {

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);


            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT * FROM members WHERE first_name= '" + first_name + "' AND last_name = '" + last_name + "' ;");
            ResultSet resultSet = statement.getResultSet();

            //System.out.print("here");

            while (resultSet.next()) {
                System.out.print(resultSet.getInt("member_id") + "\t");
                System.out.print(resultSet.getString("first_name") + "\t");
                System.out.print(resultSet.getString("last_name") + "\t");
                System.out.print(resultSet.getString("date_joined") + "\t");
                System.out.print(resultSet.getString("fee") + "\t");
                System.out.print(resultSet.getString("member_weight") + "\t");
                System.out.print(resultSet.getString("member_height") + "\t");
                System.out.print(resultSet.getString("member_time") + "\t");
                System.out.println(resultSet.getString("track_exercise_routine"));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
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
     public static void manageRoomBooking(String url, String user, String password, int schedule_id) {
        Scanner in = new Scanner(System.in);

        System.out.println("cancelling room booking with id: " + schedule_id);

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM member_booking WHERE schedule_id = " + schedule_id );

            String query = "DELETE FROM Schedule WHERE schedule_id = ?";
            try (PreparedStatement prepare = connection.prepareStatement(query)) {
                prepare.setInt(1, schedule_id);

                prepare.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


     //Returns the most recent date the equipment had maintenance
    public static void equipmentMaintenanceMonitoring(String url, String user, String password) {

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT * FROM Equipment");
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                System.out.println("|");
                System.out.print(resultSet.getInt("equipment_id") + "\t");
                System.out.print(resultSet.getString("equipment_name") +"\t");
                System.out.print(resultSet.getDate("equipment_maintenance_date"));
                System.out.println("\t");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
    
    // Updates the class room number, Date and Time.
    public static void classScheduleUpdate (String url, String user, String password, int schedule_id, int appointment_room, Date appointment_date, Time appointment_time ){

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
            e.printStackTrace();
        }
    }

    // Here we will process billing once daily and if any members have join dates that occurred
    // on the same day of the month as today's date they will be billed
    public static void BillingAndPayment(String url, String user, String password)
    {
        
        try
        {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);

            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT * FROM billing");
            ResultSet resultSet = statement.getResultSet();

            while (resultSet.next())
            {
                System.out.print("Billing member id: ");
                System.out.print(resultSet.getInt("member_id") + "\n");
                System.out.print("amount owed ($): " + resultSet.getInt("amount") + ".00\n");
                // here we would do integrated billing by passing a users payment information into the related service
            }

            connection.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int memberLoop(String url, String user, String password)
    {
         Scanner in = new Scanner(System.in);

        while (true)
        {
            System.out.println("Enter '1' to register as a new member,  '2' to manage a memebers profile, '3' to display your dashboard, or '4' to manage your schedule (5 to exit)");
            String choice = in.nextLine();
            int userChoice = Integer.parseInt(choice);

            if (userChoice == 1)
            {
                System.out.println("Enter first name: ");
                String first_name = in.nextLine();
                System.out.println("Enter last name: ");
                String last_name = in.nextLine();
                System.out.println("Enter Date joined:");
                Date date_joined = Date.valueOf(in.nextLine());
                System.out.println("Enter weight:");
                int member_weight = Integer.parseInt(in.nextLine());
                System.out.println("Enter height:");
                int member_height = Integer.parseInt(in.nextLine());
                System.out.println("Enter time:");
                int member_time = Integer.parseInt(in.nextLine());
                System.out.println("Enter routine:");
                String track_exercise_routine = in.nextLine();

                addMember(url, user, password, first_name, last_name, date_joined, member_weight, member_height, member_time, track_exercise_routine);

            }
            else if (userChoice == 2)
            {
                System.out.println("Enter member id: ");
                int member_id = Integer.parseInt(in.nextLine());
                manageProfile(url, user, password, member_id);

            }
            else if (userChoice == 3)
            {
                System.out.println("Enter member id: ");
                int member_id = Integer.parseInt(in.nextLine());
                displayDashboard(url, user, password, member_id);
 
            }
            else if (userChoice == 4)
            {
                System.out.println("Enter member id: ");
                int member_id = Integer.parseInt(in.nextLine());
                scheduleManagement(url, user, password, member_id);

            }
            else if (userChoice == 5)
            {
                return -1;
            }
            else 
            {
                System.out.println("invalid choice");
            }
        }
    }

    public static int trainerLoop(String url, String user, String password)
    {
        Scanner in = new Scanner(System.in);

        while (true)
        {
            System.out.println("Enter '1' for schedule management, or '2' for profile viewing (3 to exit)");
            String choice = in.nextLine();
            int userChoice = Integer.parseInt(choice);

            if (userChoice == 1)
            {
                System.out.println("Enter trainer id:");
                int trainer_id = Integer.parseInt(in.nextLine());
                System.out.println("Enter appointment time (HH:MM:SS):");
                Time appointment_time = Time.valueOf(in.nextLine());
                System.out.println("Enter appointment Date: (YYYY-MM-DD):");
                Date appointment_date = Date.valueOf(in.nextLine());
                System.out.println("Enter appointment Room:");
                int appointment_room = Integer.parseInt(in.nextLine());

                scheduleManagement(url, user, password, trainer_id, appointment_time, appointment_date, appointment_room);
            }
            else if (userChoice == 2)
            {
                System.out.println("Enter member first_name:");
                String first_name = in.nextLine();
                System.out.println("Enter member last_name:");
                String last_name = in.nextLine();

                memberProfileViewing(url, user, password, first_name, last_name);
            }
            else if (userChoice == 3)
            {
                return -1;
            }
            else 
            {
                System.out.println("invalid choice");
            }
        }
    }

    public static int adminLoop(String url, String user, String password)
    {
        Scanner in = new Scanner(System.in);

        while (true)
        {
            System.out.println("Enter '1' to manage booked rooms, '2' for equipment maintenance, '3' for class schedule updating, or '4' to view all current billing information (5 to exit)");
            String choice = in.nextLine();
            int userChoice = Integer.parseInt(choice);

            if (userChoice == 1)
            {
                System.out.println("Enter schedule id:");
                int schedule_id = Integer.parseInt(in.nextLine());
                manageRoomBooking(url, user, password, schedule_id);
            }
            else if (userChoice == 2)
            {
                equipmentMaintenanceMonitoring(url, user, password);
            }
            else if (userChoice == 3)
            {
                System.out.println("Enter schedule id:");
                int schedule_id = Integer.parseInt(in.nextLine());
                System.out.println("Enter appointment time (HH:MM:SS):");
                Time appointment_time = Time.valueOf(in.nextLine());
                System.out.println("Enter appointment Date: (YYYY-MM-DD):");
                Date appointment_date = Date.valueOf(in.nextLine());
                System.out.println("Enter appointment Room:");
                int appointment_room = Integer.parseInt(in.nextLine());
                classScheduleUpdate(url, user, password, schedule_id, appointment_room, appointment_date, appointment_time);
            }
            else if (userChoice == 4)
            {
                BillingAndPayment(url, user, password);
            }
            else if (userChoice == 5)
            {
                return -1;
            }
            else 
            {
                System.out.println("invalid choice");
            }
        }
        
    }
    
    public static void run_program(String url, String user, String password){
        Scanner in = new Scanner(System.in);
        while (true)
        {
            System.out.println("Enter '1' for user functionality, '2' for trainer functionality, and '3' for admin functionality, and 4 to exit the program");
            String type = in.nextLine();
            int user_type = Integer.parseInt(type);

            if (user_type == 1)
            {
                memberLoop(url, user, password);
            }
            else if (user_type == 2)
            {
                trainerLoop(url, user, password);
            }
            else if (user_type == 3)
            {
                adminLoop(url, user, password);
            }
            else if (user_type == 4)
            {
                break;
            }
            else 
            {
                System.out.println("invalid choice");
            }
        }
    }
    
    public static void main (String[]args)
    {
        // store the url, users name and users password to connect to our db
        String url = "jdbc:postgresql://localhost:5432/Project";

        Scanner in = new Scanner(System.in);

        System.out.println("Enter username:");
        String user = in.nextLine();

        System.out.println("Enter password:");
        String password = in.nextLine();

        run_program(url, user, password);
        
        in.close();
    }
}

