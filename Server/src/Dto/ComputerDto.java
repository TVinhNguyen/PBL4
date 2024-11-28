package Dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import DatabaseConnection.DBConnection;
import Model.Computer;

public class ComputerDto {
//lấy tất cả danh sách máy tính
	public static List<Computer> getAllComputers()
	{
		String query="select * from Computer";
		List<Computer> computers=new ArrayList<Computer>();
		try(Connection connection=DBConnection.getConnection();
			PreparedStatement statement=connection.prepareStatement(query);	
			ResultSet resultSet=statement.executeQuery()) {
			while(resultSet.next())
			{
				int idComputer=resultSet.getInt("idComputer");
				String nameComputer=resultSet.getString("nameComputer");
				int statusComputer=resultSet.getInt("statusComputer");
			    computers.add(new Computer(idComputer,nameComputer,statusComputer));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return computers;
	}
	public static String checkIDComputerTakeNameComputer(int idComputer)
	{
		try {
			for(var computer: getAllComputers())
			{
				if(computer.getIdComputer()==idComputer)
				{
					return computer.getNameComputer();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	//cập nhật trạng thái máy tính 
	public static Boolean setStatus(int idComputer, int idStatus) {
		try {
			String query="update Computer set statusComputer = ? where idComputer = ?";
			try(Connection conn=DBConnection.getConnection();
				PreparedStatement statement=conn.prepareStatement(query)
					) {
				statement.setInt(1, idComputer);
				statement.setInt(2, idStatus);
				int result=statement.executeUpdate();
				if(result>0)
				{
					return true;
				}
			} catch (Exception e) {
			    e.printStackTrace();
			}
		} catch (Exception e) {
		}
		return false;
	}

}
