package com.example.dataDelivery.repository;

import com.example.dataDelivery.entity.Comment;
import com.example.dataDelivery.entity.Member;

import java.util.ArrayList;
import java.util.Optional;

public interface MyRepository<E,K> {
    public Optional<E> findById(K key);
    public ArrayList<E> findAll();
    public K save(E entity);
    public Optional<E> update(E entity);
    public boolean delete(E entity);
    public boolean deleteById(K key);

    Optional<ArrayList<E>> findByContentId(Long id);

    Optional<Member> findByEmail(String email);
}
