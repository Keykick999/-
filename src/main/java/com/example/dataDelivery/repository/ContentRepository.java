package com.example.dataDelivery.repository;

import com.example.dataDelivery.entity.Content;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

@Repository
public class ContentRepository implements MyRepository<Content,Long>{

    private Connection conn;
    public Connection getConnection() {
        return conn;
    }



    //생성자 -> DB연동
    public ContentRepository() {
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
    public Optional<Content> findById(Long id) {
        String sql = "SELECT id, title, substance FROM content WHERE id = ?";
        Content content = null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    content = new Content();
                    content.setId(rs.getLong("id"));
                    content.setTitle(rs.getString("title"));
                    content.setSubstance(rs.getString("substance"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(content);
    }


    //전체 조회
    public ArrayList<Content> findAll() {

        ArrayList<Content> contentList = new ArrayList<>();

        //DB에서 조회 코드
        try {
            String sql = "" +
                    "SELECT id, title, substance " +
                    "FROM content";

            PreparedStatement pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Content content = new Content();
                content.setId((rs.getLong("id")));
                content.setTitle(rs.getString("title"));
                content.setSubstance(rs.getString("substance"));
                contentList.add(content);
            }
                rs.close();
                pstmt.close();

            return contentList;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    // 저장
    @Override
    public Long save(Content content) {
        int savedRows = 0;  //저장 성공 시 1, 실패 시 0

        //DB저장 코드
        try {
            String sql = "" +
                    " INSERT INTO content(id, title, substance) " +
                    " VALUES(?, ?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, content.getId());
            pstmt.setString(2, content.getTitle());
            pstmt.setString(3, content.getSubstance());

            savedRows = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (savedRows == 1) {
            return content.getId();
        } else {
            return null;
        }
    }



    // 삭제
    @Override
    public boolean delete(Content content) {
        int deletedRows = 0;  //삭제 성공 시 1, 실패 시 0

        try{
            String sql = "DELETE FROM content WHERE id=?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1,content.getId());

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
        Optional<Content> content = findById(id);

        int deletedRows = 0;  //삭제 성공 시 1, 실패 시 0

        try{
            String sql = "DELETE FROM content WHERE id=?";

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
    public Optional<Content> update(Content content) {
        Optional<Content> findContent = findById(content.getId());
        if (findContent.isEmpty() != true) {
            String sql = "UPDATE content SET title=?, substance=? WHERE id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, content.getTitle());
                pstmt.setString(2, content.getSubstance());
                pstmt.setLong(3, content.getId());

                pstmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Optional.of(content);
    }


    //key값인 지 확인
    public Optional<Content> checkId(Content content) {
        ArrayList<Content> contentList = findAll();
        return contentList.stream()
                .filter(saved -> saved.getId().equals(content.getId()))
                .findFirst()
                .map(savedContent -> Optional.<Content>empty())
                .orElse(Optional.ofNullable(content));
    }

}
