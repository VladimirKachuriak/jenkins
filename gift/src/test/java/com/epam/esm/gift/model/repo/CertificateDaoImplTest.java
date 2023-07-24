package com.epam.esm.gift.model.repo;

import com.epam.esm.gift.model.Certificate;
import com.epam.esm.gift.model.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;



class CertificateDaoImplTest {
    private DataSource dataSource;
    private CertificateDao certificateDAO;


    @BeforeEach
    public void setUp() {
        dataSource = TestDatabaseConfig.createDataSource();
        certificateDAO = new CertificateDaoImpl(dataSource);
        createTestTable();
        insertTestData();
    }
    @AfterEach
    public void tearDown() {
        dropTestTable();
    }

    @Test
    void getAll() {
        List<Certificate> certificates = certificateDAO.getAll("new","chain","ASC", "ASC");
        assertEquals(1, certificates.size());
        assertEquals(2, certificates.get(0).getId());
    }

    @Test
    void testGetAll() {
        List<Certificate> certificates = certificateDAO.getAll();
        assertEquals(3, certificates.size());
    }


    @Test
    void getById() {
        Certificate certificate = certificateDAO.getById(2).orElse(null);
        assert certificate != null;
        assertEquals("METRO", certificate.getName());
        assertEquals("chain of stores", certificate.getDescription());
    }

    @Test
    void create() {
        Certificate certificate = new Certificate();
        certificate.setName("ATB");
        certificate.setDescription("product supermarket");
        certificate.setPrice(23);

        List<Tag> tags = new ArrayList<>();
        Tag tag = new Tag();
        tag.setName("new tag");
        Tag tag1 = new Tag();
        tag1.setName("discount");
        tags.add(tag);
        tags.add(tag1);

        certificate.setTags(tags);

        assertEquals(4, certificateDAO.create(certificate));
        assertEquals(4, certificateDAO.getAll(null,null,null,null).size());
    }

    @Test
    void update() {
        Certificate certificate = new Certificate();
        certificate.setId(1);
        certificate.setName("ATB");
        certificate.setDescription("product supermarket");
        certificate.setPrice(23);

        List<Tag> tags = new ArrayList<>();
        Tag tag = new Tag();
        tag.setName("new");
        tags.add(tag);
        certificate.setTags(tags);
        certificateDAO.update(certificate);
        assertEquals("ATB", certificateDAO.getById(1).get().getName());
        assertEquals(1, certificateDAO.getById(1).get().getTags().size());
    }

    @Test
    void delete() {
        assertEquals(3, certificateDAO.getAll(null,null,null,null).size());
        certificateDAO.delete(3);
        assertEquals(2, certificateDAO.getAll(null,null,null,null).size());
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
            String insertSQL = """
                    INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date)
                    VALUES ('gift', 'abc', 2, 3, '2024-06-05 21:52:13', '2024-06-05 21:52:16');
                    INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date)
                    VALUES ('METRO', 'chain of stores', 2, 3, '2025-06-05 21:52:13', '2025-06-05 21:52:16');
                    INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date)
                    VALUES ('Google', 'global internet company', 2, 3, '2026-06-05 21:52:13', '2026-06-05 21:52:16');

                    INSERT INTO tag(name) VALUES ('bonus');
                    INSERT INTO tag(name) VALUES ('point');
                    INSERT INTO tag(name) VALUES ('new');
                    INSERT INTO tag(name) VALUES ('old');
                    INSERT INTO tag(name) VALUES ('discount');

                    INSERT INTO  gift_certificate_tag(certificate_id, tag_id) VALUES (1, 1);
                    INSERT INTO  gift_certificate_tag(certificate_id, tag_id) VALUES (1, 2);
                    INSERT INTO  gift_certificate_tag(certificate_id, tag_id) VALUES (2, 3);
                    INSERT INTO  gift_certificate_tag(certificate_id, tag_id) VALUES (2, 4);
                    INSERT INTO  gift_certificate_tag(certificate_id, tag_id) VALUES (3, 1);
                    INSERT INTO  gift_certificate_tag(certificate_id, tag_id) VALUES (3, 5);
                    """;
            PreparedStatement statement = connection.prepareStatement(insertSQL);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}