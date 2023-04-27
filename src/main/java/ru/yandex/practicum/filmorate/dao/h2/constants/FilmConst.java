package ru.yandex.practicum.filmorate.dao.h2.constants;

public class FilmConst {

  public static final String FILMS_FILTERING_BY_RELEASE_DATE =
          "SELECT * FROM films AS f "
                  + "INNER JOIN mpa m ON m.mpa_id = f.mpa_id "
                  + "LEFT OUTER JOIN films_directors d ON f.film_id = d.film_id "
                  + "LEFT OUTER JOIN likes l ON f.film_id = l.film_id "
                  + "WHERE director_id = ? "
                  + "GROUP BY f.film_id "
                  + "ORDER BY f.releasedate";

  public static final String FILMS_FILTERING_BY_LIKES =
          "SELECT * FROM films AS f "
                  + "INNER JOIN mpa m ON m.mpa_id = f.mpa_id "
                  + "LEFT OUTER JOIN films_directors d ON f.film_id = d.film_id "
                  + "LEFT OUTER JOIN likes l ON f.film_id = l.film_id "
                  + "WHERE director_id = ? "
                  + "GROUP BY f.film_id "
                  + "ORDER BY count(DISTINCT l.user_id) DESC";
}
