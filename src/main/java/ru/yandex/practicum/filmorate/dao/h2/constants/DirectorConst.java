package ru.yandex.practicum.filmorate.dao.h2.constants;

public class DirectorConst {

  public static final String SELECT_DIRECTORS_ALL = "SELECT * FROM directors";
  public static final String SELECT_DIRECTORS_FIND_BY_ID = "SELECT * FROM directors WHERE id = ?";
  public static final String UPDATE_DIRECTORS = "UPDATE directors SET name = ? WHERE id = ?";
  public static final String DELETE_DIRECTORS = "DELETE FROM directors WHERE id = ?";

}
