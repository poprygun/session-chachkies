package io.microsamples.sessions.sessionchachkies;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SessionChachkiesApplicationTests {

	private EasyRandom easyRandom;

	@LocalServerPort
	private int port;

//	@Autowired
	private RestTemplate testRestTemplate;

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

	@BeforeEach
	void setUp(){
		easyRandom = new EasyRandom();
		testRestTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
	}

	@Test
	@Order(0)
	void whenH2DbIsQueried_thenOneSessionIsCreated() throws SQLException {
		assertThat(this.testRestTemplate.getForObject(
				"http://localhost:" + port + "/", String.class))
				.isNotEmpty();
		assertEquals(1, getSessionIdsFromDatabase().size());
	}

	@Test
	void whenH2DbIsQueried_thenSessionAttributeIsRetrieved() throws Exception {

		final Chachkie chachkieToSave = easyRandom.nextObject(Chachkie.class);
		this.testRestTemplate.postForObject(
				"http://localhost:" + port + "/save", chachkieToSave, String.class);

		List<byte[]> queryResponse = getSessionAttributeBytesFromDatabase();

		assertThat(queryResponse.size()).isEqualTo(1);
		ObjectInput in = new ObjectInputStream(
				new ByteArrayInputStream(queryResponse.get(0)));
		List<Chachkie> obj = (List<Chachkie>) in.readObject();
		assertThat(obj.get(0)).isEqualTo(chachkieToSave);
	}

}
