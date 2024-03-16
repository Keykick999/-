package com.example.dataDelivery.repository;

import com.example.dataDelivery.entity.CommentReaction;
import com.example.dataDelivery.entity.Favor;
import com.example.dataDelivery.entity.Member;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

@Repository
public class CommentReactionRepository implements MyRepository<CommentReaction, Long> {
    private Connection conn;

    public Connection getConnection() {
        return conn;
    }

    public CommentReactionRepository() {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/orcl", "c##java", "oracle");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<CommentReaction> findById(Long id) {
        String sql = "SELECT id, memberId, commentId, contentId, favor FROM commentReaction WHERE id = ?";
        CommentReaction commentReaction = null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    commentReaction = new CommentReaction(
                            rs.getLong("id"),
                            rs.getString("memberId"),
                            rs.getLong("commentId"),
                            Favor.valueOf(rs.getString("favor")),
                            rs.getLong("contentId"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(commentReaction);
    }

    @Override
    public ArrayList<CommentReaction> findAll() {
        // Implement findAll logic
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Long save(CommentReaction commentReaction) {
        int savedRows = 0;

        try {
            String sql = "INSERT INTO commentReaction(id, memberId, commentId, contentId, favor) VALUES(?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, commentReaction.getId());
                pstmt.setString(2, commentReaction.getMemberId());
                pstmt.setLong(3, commentReaction.getCommentId());
                pstmt.setLong(4, commentReaction.getContentId());
                pstmt.setString(5, commentReaction.getFavor().toString());

                savedRows = pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return savedRows == 1 ? commentReaction.getId() : null;
    }

    @Override
    public Optional<CommentReaction> update(CommentReaction commentReaction) {
        Optional<CommentReaction> findCommentReaction = findById(commentReaction.getId());
        if (findCommentReaction.isPresent()) {
            String sql = "UPDATE commentReaction SET memberId=?, commentId=?, contentId=?, favor=? WHERE id=?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, commentReaction.getMemberId());
                pstmt.setLong(2, commentReaction.getCommentId());
                pstmt.setLong(3, commentReaction.getContentId());
                pstmt.setString(4, commentReaction.getFavor().toString());
                pstmt.setLong(5, commentReaction.getId());

                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return Optional.of(commentReaction);
    }

    @Override
    public boolean delete(CommentReaction entity) {
        // Implement delete logic
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean deleteById(Long key) {
        // Implement deleteById logic
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Optional<ArrayList<CommentReaction>> findByContentId(Long contentId) {
        ArrayList<CommentReaction> commentReactionList = new ArrayList<>();

        String sql = "SELECT id, memberId, commentId, contentId, favor FROM commentReaction WHERE contentId = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, contentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CommentReaction commentReaction = new CommentReaction(
                            rs.getLong("id"),
                            rs.getString("memberId"),
                            rs.getLong("commentId"),
                            Favor.valueOf(rs.getString("favor")),
                            rs.getLong("contentId"));
                    commentReactionList.add(commentReaction);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(commentReactionList);
    }



    @Override
    public Optional<ArrayList<CommentReaction>> findByCommentId(Long commentId) {
        ArrayList<CommentReaction> commentReactionList = new ArrayList<>();

        String sql = "SELECT id, memberId, commentId, contentId, favor FROM commentReaction WHERE commentId = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, commentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CommentReaction commentReaction = new CommentReaction(
                            rs.getLong("id"),
                            rs.getString("memberId"),
                            rs.getLong("commentId"),
                            Favor.valueOf(rs.getString("favor")),
                            rs.getLong("contentId"));
                    commentReactionList.add(commentReaction);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(commentReactionList.size() != 0) {
            return Optional.ofNullable(commentReactionList);
        } else{
            return Optional.empty();
        }
    }



    @Override
    public Optional<Member> findByEmail(String email) {
        // Implement findByEmail logic
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
