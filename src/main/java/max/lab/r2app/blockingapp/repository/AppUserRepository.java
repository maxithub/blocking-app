package max.lab.r2app.blockingapp.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import max.lab.r2app.blockingapp.domain.AppUser;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.StringUtils.isEmpty;

@RequiredArgsConstructor
@Repository
@Slf4j
public class AppUserRepository {
    private final DataSource dataSource;

    public void insert(AppUser appUser) {
        new JdbcTemplate(dataSource).update(
                "insert into app_user(id, first_name, last_name, middle_name, gender, age, province, city) values (?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[] {appUser.getId(), appUser.getFirstName(), appUser.getLastName(), appUser.getMiddleName(),
                appUser.getGender(), appUser.getAge(), appUser.getProvince(), appUser.getCity()});
    }

    public void update(AppUser appUser) {
        new JdbcTemplate(dataSource).update(
                "update app_user set first_name=?, last_name=?, middle_name=?, gender=?, age=?, province=?, city=? where id=?",
                new Object[] {appUser.getFirstName(), appUser.getLastName(), appUser.getMiddleName(),
                        appUser.getGender(), appUser.getAge(), appUser.getProvince(), appUser.getCity(), appUser.getId()});
    }

    public Optional<AppUser> findById(String id) {
        List<AppUser> appUser = new JdbcTemplate(dataSource).query(
                "select id, first_name, last_name, middle_name, gender, age, province, city from app_user where id=?",
                new Object[] { id }, rowMapper);
        return Optional.ofNullable(appUser.isEmpty() ? null : appUser.get(0));
    }

    public List<AppUser> find(Optional<String> province, Optional<String> city, Optional<Integer> age,
                              Pageable page) {
        StringBuilder sql = new StringBuilder("select id, first_name, last_name, middle_name, gender, age, province, city from app_user where 1=1 ");
        List<Object> params = new ArrayList<>();
        if (province.isPresent() && !isEmpty(province.get())) {
            sql.append(" and province = ? ");
            params.add(province.get());
        }
        if (city.isPresent() && !isEmpty(city.get())) {
            sql.append(" and city = ? ");
            params.add(city.get());
        }
        if (age.isPresent()) {
            sql.append(" and age = ? ");
            params.add(age.get());
        }
        sql.append(" limit ? offset ?");
        params.add(page.getPageSize());
        params.add(page.getOffset());
        return new JdbcTemplate(dataSource).query(sql.toString(), params.toArray(new Object[params.size()]), rowMapper);
    }

    private final static RowMapper<AppUser> rowMapper = (rs, i) -> AppUser.builder()
            .id(rs.getString("id"))
            .firstName(rs.getString("first_name"))
            .lastName(rs.getString("last_name"))
            .middleName(rs.getString("middle_name"))
            .gender(rs.getString("gender"))
            .age(rs.getInt("age"))
            .province(rs.getString("province"))
            .city(rs.getString("city"))
            .build();
}
