package com.epam.esm.gift.model.repo;

import com.epam.esm.gift.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TagDaoImpl implements TagDao {
    private static final String SQL_GET_ALL_TAG = "SELECT *FROM tag";
    private static final String SQL_GET_TAG_BY_ID = "SELECT *FROM tag WHERE id = ?";
    private static final String SQL_GET_TAG_BY_NAME = "SELECT *FROM tag WHERE name = ?";
    private static final String SQL_ADD_TAG = "INSERT INTO tag(name) VALUES(?)";
    private static final String SQL_UPDATE_TAG = "UPDATE tag SET name = ? WHERE id = ?";
    private static final String SQL_DELETE_TAG_BY_ID = "DELETE FROM tag WHERE id = ?";
    private static final String SQL_DELETE_CERTIFICATE_TAG_BY_TAG_ID = "DELETE FROM gift_certificate_tag " +
            "WHERE tag_id = ?";
    private final DataSource dataSource;

    @Autowired
    public TagDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Tag> getAll() {
        List<Tag> tags = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pst = connection.prepareStatement(SQL_GET_ALL_TAG);
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

    @Override
    public Optional<Tag> getById(int id) {
        Tag tag = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pst = connection.prepareStatement(SQL_GET_TAG_BY_ID);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                tag = new Tag();
                mapToTag(rs, tag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(tag);
    }

    @Override
    public int create(Tag tag) {
        int generatedId = -1;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pst = connection.prepareStatement(SQL_ADD_TAG, Statement.RETURN_GENERATED_KEYS);) {

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

    @Override
    public void update(Tag tag) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pst = connection.prepareStatement(SQL_UPDATE_TAG);
            pst.setString(1, tag.getName());
            pst.setInt(2, tag.getId());
            pst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                deleteTagFromCertificateTag(id, connection);
                PreparedStatement pst = connection.prepareStatement(SQL_DELETE_TAG_BY_ID);
                pst.setInt(1, id);
                pst.execute();
                connection.commit();
            }catch (SQLException e) {
                e.printStackTrace();
                connection.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }


    @Override
    public Tag getByName(String name) {
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

    private void deleteTagFromCertificateTag(int id, Connection connection) {
        try (PreparedStatement pst = connection.prepareStatement(SQL_DELETE_CERTIFICATE_TAG_BY_TAG_ID)) {
            pst.setInt(1, id);
            pst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void mapToTag(ResultSet rs, Tag tag) throws SQLException {
        tag.setId(rs.getInt("id"));
        tag.setName(rs.getString("name"));
    }
}
