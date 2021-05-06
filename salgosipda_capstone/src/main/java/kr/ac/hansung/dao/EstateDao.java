package kr.ac.hansung.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import kr.ac.hansung.model.Estate;

@Repository
public class EstateDao {
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	//부동산 크롤링 정보 삽입
	public boolean insert(Estate estate) {
		String esId = estate.getId();
		String esName = estate.getName();
		String esType = estate.getType();
		String esPrice=estate.getPrice();
		String esAddress=estate.getAddress();
		String esx=estate.getX_coord();
		String esy=estate.getY_coord();


		String sqlStatement = "insert into estate(Id,Name,Type,Price,Address,x_coord,y_coord)"
				+ "values(?,?,?,?,?,?,?)";

		return (jdbcTemplate.update(sqlStatement, new Object[] { esId, esName, esType, esPrice, esAddress, esx,esy }) == 1);

	}
	
	//매물 정보 출력
	public List<Estate> getEstate() {
		String sqlStatement = "select Id,Name,Type,Price,Address,x_coord,y_coord from estate ";
		return jdbcTemplate.query(sqlStatement, new RowMapper<Estate>() {

			@Override
			public Estate mapRow(ResultSet rs, int rowNum) throws SQLException {
				Estate estate = new Estate();
				estate.setId(rs.getString("Id"));
				estate.setName(rs.getString("Name"));
				estate.setType(rs.getString("Type"));
				estate.setPrice(rs.getString("Price"));
				estate.setAddress(rs.getString("Address"));
				estate.setX_coord(rs.getString("x_coord"));
				estate.setY_coord(rs.getString("y_coord"));

				

				return estate;
			}

		});

	}

	

	

}
