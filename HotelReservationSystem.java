package com.sprk;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class HotelReservationSystem
{
	//db variable(sensitive)
	private static final String url="jdbc:mysql://localhost:3306/hotel_db";
	
	private static final String username="root";
	
	private static final String password="root";
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException
	{
		try
		{
			//load drivers need for db
			Class.forName("com.mysql.cj.jdbc.Driver");
		}
		catch(ClassNotFoundException e)
		{
			System.out.println(e.getMessage());
		}
		
		try
		{
			Connection connection=DriverManager.getConnection(url, username, password);
			while(true)
			{
				System.out.println();
				System.out.println("Hotel Managment System");
				Scanner scanner=new Scanner(System.in);
				System.out.println("1.Reserve a room");
				System.out.println("2.View Reservations");
				System.out.println("3.Get Room Number");
				System.out.println("4.Update Reservations");
				System.out.println("5.Delete Reservations");
				System.out.println("0.Exit");
				System.out.print("Choose an option:");
				int choice=scanner.nextInt();
				switch(choice)
				{
				case 1:
					reserveRoom(connection, scanner);
					break;
					
				case 2:
					viewReservations(connection);
					break;
					
				case 3:
					getRoomNumber(connection, scanner);
					break;
					
				case 4:
					updateReservation(connection, scanner);
					break;
					
				case 5:
					deleteReservation(connection, scanner);
					break;
					
				case 0:
					exit();
					scanner.close();
					return;
					
				default:
					System.out.println("Invalid choice, plz try again");
				}
				
			}
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
		catch(InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}
		

		private static void reserveRoom(Connection connection,Scanner scanner)
		{
			try
			{
				System.out.print("Enter guest name");
				String guestName=scanner.next();
				scanner.nextLine();
				System.out.print("Enter room number:");
				int roomNumber=scanner.nextInt();
				System.out.print("Enter contact number:");
				String contactNumber=scanner.next();
				 
				
				String sql="insert into reservations(guest_name, room_number, contact_number)values('"+guestName+"',"+roomNumber+",'"+contactNumber+"')";
				
				try(Statement statement=connection.createStatement())
				{
					int affectedRows=statement.executeUpdate(sql);
					
					if(affectedRows>0)
					{
						System.out.println("Reservation Successful");
					}
					else
					{
						System.out.println("Reservation failed");
					}
				}

			}catch(SQLException e)
			{
				e.printStackTrace();
			
		}
		
	}
		private static void viewReservations(Connection connection) throws SQLException {
	        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";

	        try (Statement statement = connection.createStatement();
	             ResultSet resultSet = statement.executeQuery(sql)) {

	            System.out.println("Current Reservations:");
	            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
	            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
	            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

	            while (resultSet.next()) {
	                int reservationId = resultSet.getInt("reservation_id");
	                String guestName = resultSet.getString("guest_name");
	                int roomNumber = resultSet.getInt("room_number");
	                String contactNumber = resultSet.getString("contact_number");
	                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

	                // Format and display the reservation data in a table-like format
	                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
	                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
	            }

	            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
	        }
	    }

	private static void getRoomNumber(Connection connection, Scanner scanner)
	{
		try {
			System.out.print("Enter reservation ID:");
			int reservationID=scanner.nextInt();
			System.out.print("Enter guest name:");
			String guestName=scanner.next();
			
			String sql="SELECT room_number FROM reservations "
					+"WHERE reservation_id = "+reservationID
					+"AND guest_name='"+guestName+"'";
			
			try(Statement statement=connection.createStatement();
					ResultSet resultSet=statement.executeQuery(sql))
			{
				if(resultSet.next())
				{
					int roomNumber=resultSet.getInt("room_number");
					System.out.println("Room number for Reservation ID "+reservationID+
					" and Guest "+ guestName + " is: "+roomNumber);
				}
				else
				{
					System.out.println("Reservation not found for the given ID and guest name");
				}
			}	
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private static void updateReservation(Connection connection,Scanner scanner)
	{
		try {
			System.out.println("Enter reservation ID to Update:");
			int reservationID=scanner.nextInt();
			scanner.nextLine();
			
			if(!reservationExists(connection, reservationID))
			{
				System.out.println("Reservation not found f");
				return;
			}
			System.out.print("Enter new guest name:");
			String newGuestName=scanner.nextLine();
			System.out.println("Enter new room number:");
			int newRoomNumber=scanner.nextInt();
			System.out.println("Enter new contact number:");
			String newContactNumber=scanner.next();
			
			
			String sql="UPDATE reservations SET guest_name='"+newGuestName+"',"
					+ "room_number="+newRoomNumber+","
					+"contact_number='"+newContactNumber+"'"
					+"WHERE reservation_id="+reservationID;
			
			try(Statement statement=connection.createStatement())
			{
				int affectedRows=statement.executeUpdate(sql);
				
				if(affectedRows>0)
				{
					System.out.println("Reservation updated successfully");
				}
				else
				{
					System.out.println("Reservation update failed");
				}
				
			}
		}
			catch(SQLException e)
		{
				e.printStackTrace();
		}
	}
	private static void deleteReservation(Connection connection,Scanner scanner)
	{
		try {
			System.out.println("Enter reservation ID to delete:");
			int reservationID=scanner.nextInt();
			
			if(!reservationExists(connection, reservationID))
			{
				System.out.println("Reservation not found for the given ID.");
				return;
			}
			
			String sql="DELETE FROM reservations WHERE reservation_id="+reservationID;
			
			try (Statement statement=connection.createStatement())
			{
				int affectedRows=statement.executeUpdate(sql);
				
				if(affectedRows>0)
				{
					System.out.println("Reservation deleted successfully!");
				}
				else
				{
					System.out.println("Reservation deletion failed.");
				}
				
			}
			}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private static boolean reservationExists(Connection connection, int reservationID)
	{
		try {
			String sql="SELECT reservation_id FROM reservations WHERE reservation_id="+reservationID;
			
			try(Statement statement=connection.createStatement();
					ResultSet resultSet=statement.executeQuery(sql))
			{
				return resultSet.next();
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public static void exit() throws InterruptedException
	{
		System.out.print("Existing System");
		int i=5;
		while(i!=0)
		{
			System.out.print(".");
			Thread.sleep(450);
			i--;
		}
		System.out.println();
		System.out.println("Thank You For Using Hotel reservation System!!!");
	}
}
