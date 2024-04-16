package ar.edu.ubp.das.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ar.edu.ubp.das.beans.UserBean;
import ar.edu.ubp.das.db.Dao;


public class MSUsersDao extends Dao<UserBean, UserBean>{
	
	@Override
	public UserBean insert(UserBean user) throws SQLException {
		try {
			this.connect();
			if (user.getCompany() != null) {
				this.setProcedure("dbo.new_user(?,?,?,?,?,?)");
				this.setParameter(1, user.getUsername());
				this.setParameter(2, user.getName());
				this.setParameter(3, user.getLastName());
				this.setParameter(4, user.getPassword());
				this.setParameter(5, user.getRole());
				this.setParameter(6, user.getCompany());
			} else {
				this.setProcedure("dbo.new_user(?,?,?,?,?)");
				this.setParameter(1, user.getUsername());
				this.setParameter(2, user.getName());
				this.setParameter(3, user.getLastName());
				this.setParameter(4, user.getPassword());
				this.setParameter(5, user.getRole());
			}
			this.executeUpdate();
		} catch(SQLException e) {
			System.out.print(e);
			throw e;
		} finally {
			this.close();
		}
		return user;	
	}

	@Override
	public UserBean make(ResultSet result) throws SQLException {
		UserBean user = new UserBean();
		user.setIdUser(result.getInt("idUser"));
		user.setUsername(result.getString("username"));
		user.setName(result.getString("name"));
		user.setLastName(result.getString("lastName"));
		user.setRole(result.getString("role"));
		user.setPassword(result.getString("password"));
		user.setCompany(result.getInt("company"));
		user.setToken(result.getString("token"));
		return user;
	}

	@Override
	public List<UserBean> select(UserBean user) throws SQLException {
		try {
			this.connect();
			System.out.println(user);
			if (user != null) {
				if (user.getToken() != null) {
					this.setProcedure("dbo.get_by_token(?)");
					this.setParameter(1, user.getToken());
					List<UserBean> usersFind = this.executeQuery();
			        return usersFind;
				}
				else if (user.getUsername() != null) {
					this.setProcedure("dbo.get_username(?)");
					System.out.println("get_username");
					this.setParameter(1, user.getUsername());
					List<UserBean> usersFind = this.executeQuery();
			        return usersFind;
				} else if (user.getCompany() != null) {
					System.out.println(user.getCompany());
					System.out.println("get_user_by_company");
					this.setProcedure("dbo.get_user_by_company(?)");
					this.setParameter(1, user.getCompany());
					List<UserBean> usersFind = this.executeQuery();
					System.out.println(usersFind);
			        return usersFind;
				} else {
					this.setProcedure("dbo.get_user(?)");
					System.out.println("get_user" + user.getIdUser());
					this.setParameter(1, user.getIdUser());
					List<UserBean> usersFind = this.executeQuery();
			        return usersFind;
				}
			} else {
				this.setProcedure("dbo.get_users_all");
				System.out.println("dbo.get_users_all");
				List<UserBean> usersFind = this.executeQuery();
		        return usersFind;
			}
		} finally {
			this.close();
		}
	}

	@Override
	public UserBean update(UserBean user) throws SQLException {
		try {
			this.connect();
			if (user.getIdUser() != null) {
				System.out.println("update_user");
				this.setProcedure("dbo.update_user(?,?,?,?)");
				this.setParameter(1, user.getIdUser());
				this.setParameter(2, user.getUsername());
				this.setParameter(3, user.getName());
				this.setParameter(4, user.getLastName());
				this.executeQuery();
			} else {
				this.setProcedure("dbo.update_pass(?,?)");
				this.setParameter(1, user.getUsername());
				this.setParameter(2, user.getPassword());	
				this.executeQuery();
			}
		} finally {
			this.close();
		}
		return user;
	}

	@Override
	public boolean valid(UserBean user) throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.login(?,?)");
			this.setParameter(1, user.getUsername());
			this.setParameter(2, user.getPassword());

			List<UserBean> userFind = this.executeQuery();

            if (userFind.size() > 0) {
            	return true;
            }
	        return false;
		} finally {
			this.close();
		}
	}
	
	@Override
	public UserBean delete(UserBean user) throws SQLException {
		try {
			this.connect();
			this.setProcedure("delete_user(?)");
			this.setParameter(1, user.getIdUser());
			this.executeQuery();
			return user;
		} finally {
			this.close();
		}
	}

}
