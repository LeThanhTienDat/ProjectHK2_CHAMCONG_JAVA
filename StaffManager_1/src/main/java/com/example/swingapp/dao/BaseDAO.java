package com.example.swingapp.dao;

import java.util.List;

/**
 * BaseDAO là interface tổng quát cho mọi DAO trong hệ thống.
 * Mỗi DAO cụ thể (EmployeeDAO, RestaurantDAO, ...) sẽ kế thừa interface này.
 * @param <T> kiểu đối tượng Model tương ứng (ví dụ Employee, Restaurant, ...)
 */
public interface BaseDAO<T> {
    boolean insert(T obj);
    boolean update(T obj);
    boolean delete(int id);
    List<T> getAll();
}
