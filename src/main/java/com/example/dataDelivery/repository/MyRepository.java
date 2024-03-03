package com.example.dataDelivery.repository;

import java.util.ArrayList;
import java.util.Optional;

public interface MyRepository<E,K> {
    public Optional<E> findById(K key);
    public ArrayList<E> findAll();
    public K save(E entity);
    public Optional<E> update(E entity);
    public boolean delete(E entity);
    public boolean deleteById(K key);

}
