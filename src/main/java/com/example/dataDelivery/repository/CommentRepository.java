package com.example.dataDelivery.repository;

import com.example.dataDelivery.entity.Comment;
import com.example.dataDelivery.entity.CommentReaction;
import com.example.dataDelivery.entity.Member;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

@Repository
public class CommentRepository implements MyRepository<Comment, Long> {

    private Connection conn;

    // 생성자 -> DB 연동
    public CommentRepository() {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521/orcl",
                    "c##java",
                    "oracle"
            );
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // 아이디로 조회
    @Override
    public Optional<Comment> findById(Long id) {
        String sql = "SELECT id, contentId, writer, text, dates, likesCount, dislikesCount FROM comments WHERE id = ?";
        Comment comment = null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    comment = new Comment(
                            rs.getLong("id"),
                            rs.getLong("contentId"),
                            rs.getString("writer"),
                            rs.getString("text"),
                            rs.getDate("dates"),
                            rs.getInt("likesCount"),
                            rs.getInt("dislikesCount")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(comment);
    }

    // 게시글 ID로 댓글들 조회
    public Optional<ArrayList<Comment>> findByContentId(Long contentId) {
        String sql = "SELECT id, contentId, writer, text, dates, likesCount, dislikesCount FROM comments WHERE contentId = ?";
        ArrayList<Comment> comments = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, contentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(new Comment(
                            rs.getLong("id"),
                            rs.getLong("contentId"),
                            rs.getString("writer"),
                            rs.getString("text"),
                            rs.getDate("dates"),
                            rs.getInt("likesCount"),
                            rs.getInt("dislikesCount")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.of(comments);
    }

    @Override
    public Optional<ArrayList<CommentReaction>> findByCommentId(Long commentId) {
        return Optional.empty();
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return Optional.empty();
    }

    // 전체 조회
    public ArrayList<Comment> findAll() {
        ArrayList<Comment> comments = new ArrayList<>();
        String sql = "SELECT id, contentId, writer, text, dates, likesCount, dislikesCount FROM comments";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                comments.add(new Comment(
                        rs.getLong("id"),
                        rs.getLong("contentId"),
                        rs.getString("writer"),
                        rs.getString("text"),
                        rs.getDate("dates"),
                        rs.getInt("likesCount"),
                        rs.getInt("dislikesCount")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comments;
    }

    // 저장
    @Override
    public Long save(Comment comment) {
        String sql = "INSERT INTO comments(id, contentId, writer, text, dates, likesCount, dislikesCount) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, comment.getId());
            pstmt.setLong(2, comment.getContentId());
            pstmt.setString(3, comment.getWriter());
            pstmt.setString(4, comment.getText());
            pstmt.setDate(5, new java.sql.Date(comment.getDates().getTime()));
            pstmt.setInt(6, comment.getLikesCount());
            pstmt.setInt(7, comment.getDislikesCount());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return comment.getId();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 삭제
    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM comments WHERE id = ?";
        int deletedRows = 0;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            deletedRows = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return deletedRows == 1;
    }

    // 삭제 (객체 기반)
    @Override
    public boolean delete(Comment comment) {
        return deleteById(comment.getId());
    }

    // 변경
    @Override
    public Optional<Comment> update(Comment comment) {
        String sql = "UPDATE comments SET contentId=?, writer=?, text=?, dates=?, likesCount=?, dislikesCount=? WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, comment.getContentId());
            pstmt.setString(2, comment.getWriter());
            pstmt.setString(3, comment.getText());
            pstmt.setDate(4, new java.sql.Date(comment.getDates().getTime()));
            pstmt.setInt(5, comment.getLikesCount());
            pstmt.setInt(6, comment.getDislikesCount());
            pstmt.setLong(7, comment.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return Optional.of(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


}
