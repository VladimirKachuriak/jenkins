package com.epam.esm.gift.model.repo;

import com.epam.esm.gift.model.Certificate;
import com.epam.esm.gift.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CertificateDaoImpl implements CertificateDao {
    private static final String SQL_GET_ALL_CERTIFICATE = "SELECT gc.* FROM gift_certificate gc";
    private static final String SQL_GET_ALL_CERTIFICATE_SORT = """
            SELECT gc.*
            FROM gift_certificate gc
                     JOIN gift_certificate_tag gct ON gc.id = gct.certificate_id
                     JOIN tag t ON gct.tag_id = t.id
            WHERE t.name = ?
            """;
    private static final String SQL_GET_CERTIFICATE_BY_ID = "SELECT *FROM gift_certificate WHERE id = (?)";

    private static final String SQL_ADD_CERTIFICATE = "INSERT INTO gift_certificate(name, description, price, duration, create_date, last_update_date)" +
            "VALUES(?,?,?,?,?,?)";
    private static final String SQL_UPDATE_CERTIFICATE_BY_ID = "UPDATE gift_certificate SET name = ?, description = ?, " +
            "price = ?, duration = ?, last_update_date = ? WHERE id = ?";
    private static final String SQL_DELETE_CERTIFICATE_BY_ID = "DELETE FROM gift_certificate WHERE id = ?";
    private static final String SQL_GET_TAG_BY_CERTIFICATE_ID = "SELECT t.*" +
            "FROM tag t " +
            "JOIN gift_certificate_tag gct ON t.id = gct.tag_id " +
            "WHERE gct.certificate_id = ?";
    private static final String SQL_ADD_TAG = "INSERT INTO tag(name) VALUES(?)";
    private static final String SQL_GET_TAG_BY_NAME = "SELECT *FROM tag WHERE name = ?";
    private static final String SQL_ADD_CERTIFICATE_TAG = "INSERT INTO gift_certificate_tag (certificate_id, tag_id)" +
            "VALUES(?,?)";
    private static final String SQL_DELETE_CERTIFICATE_TAG_BY_TAG_AND_CERTIFICATE_ID = "DELETE FROM gift_certificate_tag " +
            "WHERE certificate_id = ? AND tag_id = ?";
    private static final String SQL_GET_TAG_BY_ID = "SELECT *FROM gift_certificate_tag WHERE tag_id = ?";
    private static final String SQL_DELETE_TAG_BY_ID = "DELETE FROM tag WHERE id = ?";
    private static final String SQL_DELETE_ASSOCIATION_CERTIFICATE_BY_ID = "DELETE FROM gift_certificate_tag " +
            "WHERE certificate_id = ?";
    private final DataSource dataSource;

    @Autowired
    public CertificateDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String paramBuilder(String name, String description, String sortByDate, String SortByName) {
        StringBuilder query = new StringBuilder();
        query.append(SQL_GET_ALL_CERTIFICATE);
        if (name != null && !name.isBlank()) {
            query.append("""
                        JOIN gift_certificate_tag gct ON gc.id = gct.certificate_id
                        JOIN tag t ON gct.tag_id = t.id
                    WHERE t.name = ?
                            """);
        }
        if (description != null && !description.isBlank()) {
            if (query.isEmpty()) {
                query.append(" WHERE ");
            } else {
                query.append(" AND ");
            }
            query.append("CONCAT(gc.name,' ',gc.description) LIKE ?");
        }
        StringBuilder orderBy = new StringBuilder();
        if (sortByDate != null && !sortByDate.isBlank()) {
            if (sortByDate.equals("DESC")) {
                orderBy.append(" ORDER BY gc.last_update_date DESC");
            } else {
                orderBy.append(" ORDER BY gc.last_update_date ASC");
            }
        }
        if (SortByName != null && !SortByName.isBlank()) {
            if (!orderBy.isEmpty()) {
                orderBy.append(" , gc.name");
            } else {
                orderBy.append(" ORDER BY gc.name");
            }
            if (SortByName.equals("DESC")) {
                orderBy.append(" DESC");
            } else {
                orderBy.append(" ASC");
            }
        }
        query.append(orderBy);
        return query.toString();
    }


    @Override
    public List<Certificate> getAll() {
        List<Certificate> certificates = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pst = connection.prepareStatement(SQL_GET_ALL_CERTIFICATE);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Certificate certificate = new Certificate();
                mapToCertificate(rs, certificate);
                certificates.add(certificate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return certificates;
    }

    @Override
    public List<Certificate> getAll(String tagName, String description, String sortByDate, String sortByName) {
        List<Certificate> certificates = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pst = connection.prepareStatement(paramBuilder(tagName, description, sortByDate, sortByName));
            int counter = 1;
            if (tagName != null && !tagName.isBlank()) {
                pst.setString(counter, tagName);
                counter++;
            }
            if (description != null && !description.isBlank()) {
                pst.setString(counter, "%" + description + "%");
            }
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Certificate certificate = new Certificate();
                mapToCertificate(rs, certificate);
                certificates.add(certificate);
            }
            for (Certificate certificate : certificates) {
                if (certificate != null) certificate.setTags(getTagsForCertificate(certificate.getId()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return certificates;
    }

    @Override
    public Optional<Certificate> getById(int id) {
        Certificate certificate = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pst = connection.prepareStatement(SQL_GET_CERTIFICATE_BY_ID);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                certificate = new Certificate();
                mapToCertificate(rs, certificate);
            }
            if (certificate != null) certificate.setTags(getTagsForCertificate(certificate.getId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(certificate);
    }


    @Override
    public int create(Certificate certificate) {
        int generatedId = 1;
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                PreparedStatement pst = connection.prepareStatement(SQL_ADD_CERTIFICATE, Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, certificate.getName());
                pst.setString(2, certificate.getDescription());
                pst.setInt(3, certificate.getPrice());
                pst.setInt(4, certificate.getDuration());
                pst.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                pst.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                pst.execute();
                ResultSet generatedKeys = pst.getGeneratedKeys();
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getInt(1);
                }
                for (Tag tag : certificate.getTags()) {
                    int tagId;
                    Tag tagDb = getTagByName(tag.getName());
                    if (tagDb == null) {
                        tagId = create(tag, connection);
                    } else {
                        tagId = tagDb.getId();
                    }
                    addTagToCertificate(generatedId, tagId, connection);
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    @Override
    public void update(Certificate certificate) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                PreparedStatement pst = connection.prepareStatement(SQL_UPDATE_CERTIFICATE_BY_ID);
                pst.setString(1, certificate.getName());
                pst.setString(2, certificate.getDescription());
                pst.setInt(3, certificate.getPrice());
                pst.setInt(4, certificate.getDuration());
                pst.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                pst.setInt(6, certificate.getId());
                pst.execute();
                List<Tag> tagsDB = getTagsForCertificate(certificate.getId());
                for (Tag tag : tagsDB) {
                    if (!certificate.getTags().contains(tag)) {
                        deleteByCertificateAndTagId(certificate.getId(), tag.getId(), connection);
                    }
                }
                for (Tag tag : certificate.getTags()) {
                    if (!tagsDB.contains(tag)) {
                        int tagId;
                        Tag tagDb = getTagByName(tag.getName());
                        if (tagDb == null) {
                            tagId = create(tag, connection);
                        } else {
                            tagId = tagDb.getId();
                        }
                        addTagToCertificate(certificate.getId(), tagId, connection);
                    }
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                deleteCertificateAssociation(id, connection);
                PreparedStatement pst = connection.prepareStatement(SQL_DELETE_CERTIFICATE_BY_ID);
                pst.setInt(1, id);
                pst.execute();
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                connection.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private List<Tag> getTagsForCertificate(int id) {
        List<Tag> tags = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pst = connection.prepareStatement(SQL_GET_TAG_BY_CERTIFICATE_ID);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Tag tag = new Tag();
                mapToTag(rs, tag);
                tags.add(tag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tags;
    }

    private int create(Tag tag, Connection connection) {
        int generatedId = -1;
        try (PreparedStatement pst = connection.prepareStatement(SQL_ADD_TAG, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, tag.getName());
            pst.execute();
            ResultSet generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                generatedId = generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    private Tag getTagByName(String name) {
        Tag tag = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pst = connection.prepareStatement(SQL_GET_TAG_BY_NAME);
            pst.setString(1, name);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                tag = new Tag();
                mapToTag(rs, tag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tag;
    }

    private void addTagToCertificate(int certificateId, int tagId, Connection connection) {
        try (PreparedStatement pst = connection.prepareStatement(SQL_ADD_CERTIFICATE_TAG);) {
            pst.setInt(1, certificateId);
            pst.setInt(2, tagId);
            pst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteByCertificateAndTagId(int certificateId, int tagId, Connection connection) {
        try (PreparedStatement pst = connection.prepareStatement(SQL_DELETE_CERTIFICATE_TAG_BY_TAG_AND_CERTIFICATE_ID);
             PreparedStatement pst1 = connection.prepareStatement(SQL_GET_TAG_BY_ID);
             PreparedStatement pst2 = connection.prepareStatement(SQL_DELETE_TAG_BY_ID);) {
            pst.setInt(1, certificateId);
            pst.setInt(2, tagId);
            pst.execute();
            pst1.setInt(1, tagId);
            ResultSet rs = pst1.executeQuery();
            if (!rs.next()) {
                pst2.setInt(1, tagId);
                pst2.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteCertificateAssociation(int certificate_id, Connection connection) throws SQLException {
        try (PreparedStatement pst = connection.prepareStatement(SQL_DELETE_ASSOCIATION_CERTIFICATE_BY_ID)) {
            pst.setInt(1, certificate_id);
            pst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        }
    }

    private void mapToCertificate(ResultSet rs, Certificate certificate) throws SQLException {
        certificate.setId(rs.getInt("id"));
        certificate.setName(rs.getString("name"));
        certificate.setDescription(rs.getString("description"));
        certificate.setPrice(rs.getInt("price"));
        certificate.setDuration(rs.getInt("duration"));
        certificate.setCreateDate(rs.getTimestamp("create_date").toLocalDateTime());
        certificate.setLastUpdateDate(rs.getTimestamp("last_update_date").toLocalDateTime());
    }

    private void mapToTag(ResultSet rs, Tag tag) throws SQLException {
        tag.setId(rs.getInt("id"));
        tag.setName(rs.getString("name"));
    }
}
