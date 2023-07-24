package com.epam.esm.gift.model.repo;

import com.epam.esm.gift.model.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TagDaoImplTest {
    private DataSource dataSource;
    private TagDao tagDAO;


    @BeforeEach
    public void setUp() {
        dataSource = TestDatabaseConfig.createDataSource();
        tagDAO = new TagDaoImpl(dataSource);
        createTestTable();
        insertTestData();
    }

    @AfterEach
    public void tearDown() {
        dropTestTable();
    }

    @Test
    void getAll() {
        List<Tag> tags = tagDAO.getAll();
        assertEquals(2, tags.size());

        Tag tag1 = tags.get(0);
        assertEquals(1, tag1.getId());
        assertEquals("Tag1", tag1.getName());

        Tag tag2 = tags.get(1);
        assertEquals(2, tag2.getId());
        assertEquals("Tag2", tag2.getName());
    }

    @Test
    void getById() {
        Tag tag = new Tag();
        tag.setId(1);
        tag.setName("Tag1");

        Tag tagDb = tagDAO.getById(1).orElse(null);

        assertEquals(tag, tagDb);
    }

    @Test
    void create() {
        Tag tag = new Tag();
        tag.setName("Tag3");

        assertEquals(3, tagDAO.create(tag));

        Tag tagDb = tagDAO.getById(3).orElse(null);
        assertEquals(tag, tagDb);
    }

    @Test
    void update() {
        Tag tag = new Tag();
        tag.setId(1);
        tag.setName("Updated");

        tagDAO.update(tag);

        Tag tagDb = tagDAO.getById(1).orElse(null);
        assertEquals(tag, tagDb);
        assertEquals("Updated", tagDb.getName());
    }

    @Test
    void delete() {
        Tag tag = new Tag();
        tag.setName("Tag2");
        assertEquals(tag, tagDAO.getById(2).orElse(null));

        tagDAO.delete(2);

        Tag tagDb = tagDAO.getById(2).orElse(null);
        assertEquals(null, tagDb);
    }

    @Test
    void getByName() {
        Tag tag = new Tag();
        tag.setName("Tag2");
        assertEquals(tag, tagDAO.getByName("Tag2"));
    }

    private void createTestTable() {
        try (Connection connection = dataSource.getConnection()) {
            String createTableSQL = """
                    CREATE TABLE gift_certificate(
                    	id INT NOT NULL AUTO_INCREMENT,
                    	name varchar(20) NOT NULL,
                    	description VARCHAR(30) NOT NULL,
                      	price INT DEFAULT 0,
                            duration INT NOT NULL,
                            create_date DATETIME NOT NULL,
                    	last_update_date DATETIME NOT NULL,
                    	PRIMARY KEY (id)    \s
                    );
                    CREATE TABLE tag(
                    	id INT NOT NULL AUTO_INCREMENT,
                    	name VARCHAR(30) NOT NULL,
                    	PRIMARY KEY (id)	
                    );
                    CREATE TABLE gift_certificate_tag (
                    	certificate_id INT NOT NULL,
                    	tag_id INT NOT NULL,
                    	FOREIGN KEY (certificate_id) REFERENCES gift_certificate(id),
                            FOREIGN KEY (tag_id) REFERENCES tag(id),
                    	UNIQUE (certificate_id, tag_id)
                    );
                    """;
            PreparedStatement statement = connection.prepareStatement(createTableSQL);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void dropTestTable() {
        try (Connection connection = dataSource.getConnection()) {
            String dropTableSQL = "DROP TABLE IF EXISTS gift_certificate_tag; DROP TABLE IF EXISTS tag; DROP TABLE IF EXISTS gift_certificate;";
            PreparedStatement statement = connection.prepareStatement(dropTableSQL);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertTestData() {
        try (Connection connection = dataSource.getConnection()) {
            String insertSQL = "INSERT INTO tag (id, name) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(insertSQL);
            statement.setInt(1, 1);
            statement.setString(2, "Tag1");
            statement.execute();

            statement.setInt(1, 2);
            statement.setString(2, "Tag2");
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}