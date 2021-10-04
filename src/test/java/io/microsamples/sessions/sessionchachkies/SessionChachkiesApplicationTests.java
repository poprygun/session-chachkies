package io.microsamples.sessions.sessionchachkies;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SessionChachkiesApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testRestTemplate;

	private List<String> getSessionIdsFromDatabase()
			throws SQLException {

		List<String> result = new ArrayList<>();
		ResultSet rs = getResultSet(
				"SELECT * FROM SPRING_SESSION");

		while (rs.next()) {
			result.add(rs.getString("SESSION_ID"));
		}
		return result;
	}

	private List<byte[]> getSessionAttributeBytesFromDatabase()
			throws SQLException {

		List<byte[]> result = new ArrayList<>();
		ResultSet rs = getResultSet(
				"SELECT * FROM SPRING_SESSION_ATTRIBUTES");

		while (rs.next()) {
			result.add(rs.getBytes("ATTRIBUTE_BYTES"));
		}
		return result;
	}

	private ResultSet getResultSet(String sql)
			throws SQLException {

		Connection conn = DriverManager
				.getConnection("jdbc:h2:mem:testdb", "sa", "");
		Statement stat = conn.createStatement();
		return stat.executeQuery(sql);
	}

	@Test
	void whenH2DbIsQueried_thenOneSessionIsCreated()
			throws SQLException {

		assertThat(this.testRestTemplate.getForObject(
				"http://localhost:" + port + "/", String.class))
				.isNotEmpty();
		assertEquals(1, getSessionIdsFromDatabase().size());
	}

	@Test
	void whenH2DbIsQueried_thenSessionAttributeIsRetrieved()
			throws Exception {

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("color", "red");
		this.testRestTemplate.postForObject(
				"http://localhost:" + port + "/saveColor", map, String.class);
		List<byte[]> queryResponse = getSessionAttributeBytesFromDatabase();

		assertEquals(1, queryResponse.size());
		ObjectInput in = new ObjectInputStream(
				new ByteArrayInputStream(queryResponse.get(0)));
		List<String> obj = (List<String>) in.readObject();
		assertEquals("red", obj.get(0));
	}

}
