package com.example.dataDelivery.repository;

import com.example.dataDelivery.entity.Comment;
import com.example.dataDelivery.entity.Content;
import com.example.dataDelivery.entity.Member;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

@Repository
public class MemberRepository implements MyRepository<Member,String>{

    private Connection conn;
    public Connection getConnection() {
        return conn;
    }



    //생성자 -> DB연동
    public MemberRepository() {
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
    public Optional<Member> findById(String id) {
        String sql = "SELECT id, name, email, phone, password FROM member WHERE id = ?";
        Member member = null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    member = new Member();
                    member.setId(rs.getString("id"));
                    member.setName(rs.getString("name"));
                    member.setEmail(rs.getString("email"));
                    member.setPhone(rs.getString("phone"));
                    member.setPassword(rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(member);
    }


    // 이메일로 조회
    @Override
    public Optional<Member> findByEmail(String email) {
        String sql = "SELECT id, name, email, phone, password FROM member WHERE email = ?";
        Member member = null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    member = new Member();
                    member.setId(rs.getString("id"));
                    member.setName(rs.getString("name"));
                    member.setEmail(rs.getString("email"));
                    member.setPhone(rs.getString("phone"));
                    member.setPassword(rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(member);
    }


    //전체 조회
    public ArrayList<Member> findAll() {

        ArrayList<Member> memberList = new ArrayList<>();

        //DB에서 조회 코드
        try {
            String sql = "" +
                    "SELECT id, name, email, phone, password " +
                    "FROM member";

            PreparedStatement pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Member member = new Member();
                member.setId((rs.getString("id")));
                member.setName(rs.getString("name"));
                member.setEmail(rs.getString("email"));
                member.setPhone(rs.getString("phone"));
                member.setPhone(rs.getString("password"));
                memberList.add(member);
            }
            rs.close();
            pstmt.close();

            return memberList;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    // 저장
    @Override
    public String save(Member member) {
        int savedRows = 0;  //저장 성공 시 1, 실패 시 0

        //DB저장 코드
        try {
            String sql = "" +
                    " INSERT INTO member(id, name, email, phone, password) " +
                    " VALUES(?, ?, ?, ?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getId());
            pstmt.setString(2, member.getName());
            pstmt.setString(3, member.getEmail());
            pstmt.setString(4, member.getPhone());
            pstmt.setString(5, member.getPassword());

            savedRows = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (savedRows == 1) {
            return member.getId();
        } else {
            return null;
        }
    }



    // 삭제
    @Override
    public boolean delete(Member member) {
        int deletedRows = 0;  //삭제 성공 시 1, 실패 시 0

        try{
            String sql = "DELETE FROM member WHERE id=?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,member.getId());

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
    public boolean deleteById(String id) {
        Optional<Member> member = findById(id);

        int deletedRows = 0;  //삭제 성공 시 1, 실패 시 0

        try{
            String sql = "DELETE FROM member WHERE id=?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,id);

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
    public Optional<ArrayList<Comment>> findByContentId(Long id) {
        return Optional.empty();
    }


    // 변경
    @Override
    public Optional<Member> update(Member member) {
        Optional<Member> findContent = findById(member.getId());
        if (findContent.isEmpty() != true) {
            String sql = "UPDATE member SET name=?, email=?, phone=?, password=? WHERE id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, member.getName());
                pstmt.setString(2, member.getEmail());
                pstmt.setString(3, member.getPhone());
                pstmt.setString(4, member.getPassword());
                pstmt.setString(5, member.getId());

                pstmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Optional.of(member);
    }


    //key 값인 지 확인
    public Optional<Member> checkId(Member member) {
        ArrayList<Member> contentList = findAll();
        return contentList.stream()
                .filter(saved -> saved.getId().equals(member.getId()))
                .findFirst()
                .map(savedContent -> Optional.<Member>empty())
                .orElse(Optional.ofNullable(member));
    }


    public Optional<Member> login(Member member) {
        Optional<Member> target = findById(member.getId());
        if(target.isPresent() && target.get().equals(member)) {
            return target;
        }
        return Optional.empty();
    }

}
