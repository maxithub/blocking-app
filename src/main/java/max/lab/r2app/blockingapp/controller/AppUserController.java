package max.lab.r2app.blockingapp.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import max.lab.r2app.blockingapp.domain.AppUser;
import max.lab.r2app.blockingapp.repository.AppUserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Collections;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.springframework.http.ResponseEntity.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AppUserController {
    private final AppUserRepository appUserRepo;

    @PostMapping("/appuser")
    public ResponseEntity create(@RequestBody AppUser appUser) {
        if (appUserRepo.findById(appUser.getId()).isPresent()) {
            return badRequest().body(Collections.singletonMap("errors", String.format("AppUser: %d already exists", appUser.getId())));
        }
        appUserRepo.insert(appUser);
        return ok().build();
    }

    @PutMapping("/appuser/{id}")
    public ResponseEntity update(@PathVariable("id") String id, @RequestBody AppUser appUser) {
        if (!appUserRepo.findById(id).isPresent()) {
            return badRequest().body(Collections.singletonMap("errors", String.format("AppUser: %s does NOT exist", appUser.getId())));
        }

        appUserRepo.update(appUser);
        return ok().build();
    }

    @GetMapping("/appuser/{id}")
    public ResponseEntity findOne(@PathVariable("id") String id) {
        Optional<AppUser> appUser = appUserRepo.findById(id);
        if (!appUser.isPresent()) {
            return notFound().build();
        }
        return ok(appUser.get());
    }

    @GetMapping("/appusers")
    public ResponseEntity findMany(RequestParams requestParams) {
        return ok(appUserRepo.find(
                ofNullable(requestParams.province),
                ofNullable(requestParams.city),
                ofNullable(requestParams.age),
                requestParams.toPageable()));
    }

    @Data
    public static class RequestParams {
        private String province;
        private String city;
        private Integer age;

        @Min(0)
        private int page = 0;

        @Min(5)
        @Max(500)
        private int size = 10;

        public Pageable toPageable() {
            return PageRequest.of(page, size);
        }
    }
}
