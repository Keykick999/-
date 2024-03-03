package com.example.dataDelivery.repository;

import com.example.dataDelivery.entity.Comment;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

@Repository
public class CommentRepository implements MyRepository<Comment,Long>{

    private Connection conn;
    public Connection getConnection() {
        return conn;
    }



    //생성자 -> DB연동
    public CommentRepository() {
        try {
            Class.forName("oracle.jdbc.OracleDriver");

            conn = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521/orcl",
                    "c##java",
                    "oracle"
            );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // 아이디로 조회
    @Override
    public Optional<Comment> findById(Long id) {
        String sql = "SELECT id, contentId, writer, text, dates FROM comments WHERE id = ?";
        Comment comments = null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    comments = new Comment();
                    comments.setId(rs.getLong("id"));
                    comments.setContentId(rs.getLong("contentId"));
                    comments.setWriter(rs.getString("writer"));
                    comments.setText(rs.getString("text"));
                    comments.setDates(rs.getDate("dates"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(comments);
    }



    public Optional<ArrayList<Comment>> findByContentId(Long contentId) {
        ArrayList<Comment> commentList = new ArrayList<Comment>();

        String sql = "SELECT id, contentId, writer, text, dates FROM comments WHERE contentId = ?";
        Comment comment = null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, contentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    comment = new Comment();
                    comment.setId(rs.getLong("id"));
                    comment.setContentId(rs.getLong("contentId"));
                    comment.setWriter(rs.getString("writer"));
                    comment.setText(rs.getString("text"));
                    comment.setDates(rs.getDate("dates"));
                    commentList.add(comment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(commentList);
    }



    //전체 조회
    public ArrayList<Comment> findAll() {

        ArrayList<Comment> commentList = new ArrayList<>();

        //DB에서 조회 코드
        try {
            String sql = "" +
                    "SELECT id, contentId, writer, text, dates " +
                    "FROM comments";

            PreparedStatement pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Comment comments = new Comment();
                comments.setId((rs.getLong("id")));
                comments.setContentId(rs.getLong("contentId"));
                comments.setWriter(rs.getString("writer"));
                comments.setText(rs.getString("text"));
                comments.setDates(rs.getDate("dates"));
                commentList.add(comments);
            }
            rs.close();
            pstmt.close();

            return commentList;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    // 저장
    @Override
    public Long save(Comment comments) {
        int savedRows = 0;  //저장 성공 시 1, 실패 시 0

        //DB저장 코드
        try {
            String sql = "" +
                    " INSERT INTO comments(id, contentId, writer, text, dates) " +
                    " VALUES(?, ?, ?, ?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, comments.getId());
            pstmt.setLong(2, comments.getContentId());
            pstmt.setString(3, comments.getWriter());
            pstmt.setString(4, comments.getText());
            pstmt.setDate(5, comments.getDates());

            savedRows = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (savedRows == 1) {
            return comments.getId();
        } else {
            return null;
        }
    }



    // 삭제
    @Override
    public boolean delete(Comment comments) {
        int deletedRows = 0;  //삭제 성공 시 1, 실패 시 0

        try{
            String sql = "DELETE FROM comments WHERE id=?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1,comments.getId());

            deletedRows = pstmt.executeUpdate();

            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        if(deletedRows == 1){ //삭제 성공 시 1 반환
            return true;
        } else{
            return false;
        }
    }


    @Override
    public boolean deleteById(Long id) {
        Optional<Comment> comments = findById(id);

        int deletedRows = 0;  //삭제 성공 시 1, 실패 시 0

        try{
            String sql = "DELETE FROM comments WHERE id=?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1,id);

            deletedRows = pstmt.executeUpdate();

            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        if(deletedRows == 1){ //삭제 성공 시 1 반환
            return true;
        } else{
            return false;
        }
    }


    // 변경
    @Override
    public Optional<Comment> update(Comment comments) {
        Optional<Comment> findComments = findById(comments.getId());
        if (findComments.isEmpty() != true) {
            String sql = "UPDATE comments SET contentId=?, writer=?, text=?, dates=? WHERE id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, comments.getContentId());
                pstmt.setString(2, comments.getWriter());
                pstmt.setString(3, comments.getText());
                pstmt.setDate(4, comments.getDates());
                pstmt.setLong(5, comments.getId());

                pstmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Optional.of(comments);
    }
}
