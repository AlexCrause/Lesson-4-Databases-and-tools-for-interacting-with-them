package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Задание
 * =======
 * Создайте базу данных (например, SchoolDB).
 * В этой базе данных создайте таблицу Courses с полями id (ключ), title, duration.
 * Настройте Hibernate для работы с вашей базой данных.
 * Создайте Java-класс Course, соответствующий таблице Courses, с необходимыми аннотациями Hibernate.
 * Используя Hibernate, напишите код для вставки, чтения, обновления и удаления данных в таблице Course
 * убедитесь, что каждая операция выполняется в отдельной транзакции.
 */
public class HomeWork {

    private static final Random random = new Random();

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3307";
        String user = "root";
        String password = "admin";

        //Подключение к базе данных
        try (Connection connection = DriverManager.getConnection(url, user, password)) {

            //Создание базы данных
            createDatabase(connection);
            System.out.println("База данных создана");

            //Использование базы данных
            useDatabase(connection);
            System.out.println("База данных используется");

            //Создание таблицы
            createTable(connection);
            System.out.println("Таблица создана");


            //Добавление данных
            int count = random.nextInt(1, 19);
            for (int i = 0; i < count; i++)
                insertData(connection, Course.getRandomCourse());
            System.out.println("Данные добавлены");

            //Чтение данных
            Collection<Course> courses = readData(connection);
            for (var course : courses)
                System.out.println(course);
            System.out.println("Данные прочитаны");

            // Обновление данных
            for (var course : courses) {
                course.updateTitle();
                course.updateDuration();
                updateData(connection, course);
            }
            System.out.println("Данные обновлены");

            //Чтение данных
            courses = readData(connection);
            for (var course : courses)
                System.out.println(course);
            System.out.println("Данные прочитаны");

            //Удаление данных
            for (var course : courses)
                deleteData(connection, course.getId());
            System.out.println("Данные удалены");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void createDatabase(Connection connection) throws SQLException {
        String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS SchoolDB";
        try (PreparedStatement statement = connection.prepareStatement(createDatabaseSQL)) {
            statement.execute();
        }
    }

    private static void useDatabase(Connection connection) throws SQLException {
        String useDatabaseSQL = "USE SchoolDB";
        try (PreparedStatement statement = connection.prepareStatement(useDatabaseSQL)) {
            statement.execute();
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS courses " +
                "(id INT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(255), duration INT)";
        try (PreparedStatement statement = connection.prepareStatement(createTableSQL)) {
            statement.execute();
        }
    }


    /**
     * Добавление данных в таблицу courses
     *
     * @param connection Cоединение с базой данных
     * @param course     Курс
     * @throws SQLException Ошибка при выполнении SQL-запроса
     */
    private static void insertData(Connection connection, Course course) throws SQLException {
        String insertDataSQL = "INSERT INTO courses (title, duration) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertDataSQL)) {
            statement.setString(1, course.getTitle());
            statement.setInt(2, course.getDuration());
            statement.executeUpdate();
        }
    }

    /**
     * Чтение данных из таблицы courses
     *
     * @param connection Cоединение с базой данных
     * @return коллекция курсов
     * @throws SQLException Ошибка при выполнении SQL-запроса
     */
    private static Collection<Course> readData(Connection connection) throws SQLException {
        ArrayList<Course> coursesList = new ArrayList<>();
        String readDataSQL = "SELECT * FROM courses";
        try (PreparedStatement statement = connection.prepareStatement(readDataSQL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                int duration = resultSet.getInt("duration");
                coursesList.add(new Course(id, title, duration));
            }
            return coursesList;
        }
    }

    /**
     * Обновление данных в таблице courses по идентификатору
     *
     * @param connection Соединение с БД
     * @param course     Курс
     * @throws SQLException Исключение при выполнении запроса
     */
    private static void updateData(Connection connection, Course course) throws SQLException {
        String updateDataSQL = "UPDATE courses SET title=?, duration=? WHERE id=?;";
        try (PreparedStatement statement = connection.prepareStatement(updateDataSQL)) {
            statement.setString(1, course.getTitle());
            statement.setInt(2, course.getDuration());
            statement.setInt(3, course.getId());
            statement.executeUpdate();
        }
    }


    /**
     * Удаление записи из таблицы courses по идентификатору
     * @param connection Соединение с БД
     * @param id Идентификатор записи
     * @throws SQLException Исключение при выполнении запроса
     */
    private static void deleteData(Connection connection, int id) throws SQLException {
        String deleteDataSQL = "DELETE FROM courses WHERE id=?;";
        try (PreparedStatement statement = connection.prepareStatement(deleteDataSQL)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }
}
