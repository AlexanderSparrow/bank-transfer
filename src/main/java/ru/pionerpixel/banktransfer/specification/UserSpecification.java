package ru.pionerpixel.banktransfer.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.pionerpixel.banktransfer.dto.UserSearchRequest;
import ru.pionerpixel.banktransfer.model.EmailData;
import ru.pionerpixel.banktransfer.model.PhoneData;
import ru.pionerpixel.banktransfer.model.User;

import java.util.ArrayList;
import java.util.List;


public class UserSpecification {
    public static Specification<User> build(UserSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getName() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder
                        .lower(root.get("name")), "%" + request.getName()
                        .toLowerCase() + "%"));
            }

            if (request.getDateOfBirthAfter() != null) {
                predicates.add(criteriaBuilder.greaterThan(root.get("dateOfBirth"), request.getDateOfBirthAfter()));
            }

            if (request.getEmail() != null) {
                Join<User, EmailData> emailJoin = root.join("emails", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(emailJoin.get("email"), request.getEmail()));
            }

            if (request.getPhone() != null) {
                Join<User, PhoneData> phoneJoin = root.join("phones", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(phoneJoin.get("phone"), request.getPhone()));
            }

            if (predicates.isEmpty()) return criteriaBuilder.conjunction();

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
}
